package com.simats.legalchain.screens

import android.net.Uri
import android.widget.Toast
import com.simats.legalchain.network.ApiClient
import com.simats.legalchain.network.RegisterResponse
import com.simats.legalchain.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import java.text.DecimalFormat

private val DarkGreen = Color(0xFF004D40)
private val ThickBlack = Color(0xFF000000) // pure black for bold labels

// simple country → states data
private val countries = listOf("India", "United States", "United Kingdom")

private val statesByCountry = mapOf(
    "India" to listOf(
        "Andhra Pradesh", "Telangana", "Tamil Nadu",
        "Karnataka", "Maharashtra", "Kerala", "Delhi"
    ),
    "United States" to listOf(
        "California", "New York", "Texas", "Florida"
    ),
    "United Kingdom" to listOf(
        "England", "Scotland", "Wales", "Northern Ireland"
    )
)

private enum class RegisterRole { LAWYER, CLIENT }

@Composable
fun RegisterScreen(navController: NavHostController? = null) {
    val context = LocalContext.current

    // -------- STATE ----------
    var role by remember { mutableStateOf<RegisterRole?>(null) }
    var showRoleDropdown by remember { mutableStateOf(false) }

    var showPassword by remember { mutableStateOf(false) }
    var agreedTerms by remember { mutableStateOf(false) }

    // base fields
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // location fields
    var country by remember { mutableStateOf("") }
    var showCountryDropdown by remember { mutableStateOf(false) }

    var lawyerState by remember { mutableStateOf("") }
    var showStateDropdown by remember { mutableStateOf(false) }

    var district by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var barId by remember { mutableStateOf("") }

    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var selectedFileSize by remember { mutableStateOf<Long?>(null) } // bytes
    var selectedFileTooLarge by remember { mutableStateOf(false) }

    var isSubmitted by remember { mutableStateOf(false) }

    var showSuccessDialog by remember { mutableStateOf(false) }


    // file size limit
    val maxBytes = 5L * 1024L * 1024L // 5 MB

    // launcher for picking a document
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult

        // read file name and size safely
        try {
            val resolver = context.contentResolver
            val afd = resolver.openAssetFileDescriptor(uri, "r")
            val size = afd?.length ?: -1L
            afd?.close()

            // Fallback: try openInputStream to estimate
            val finalSize = if (size <= 0L) {
                resolver.openInputStream(uri)?.use { it.available().toLong() } ?: -1L
            } else size

            selectedFileUri = uri
            selectedFileSize = if (finalSize > 0) finalSize else null
            // Try to obtain displayName
            val name = queryFileName(resolver = context.contentResolver, uri = uri) ?: uri.lastPathSegment
            selectedFileName = name

            if (selectedFileSize != null && selectedFileSize!! > maxBytes) {
                selectedFileTooLarge = true
                Toast.makeText(context, "File too large. Max 5 MB allowed.", Toast.LENGTH_LONG).show()
            } else {
                selectedFileTooLarge = false
            }
        } catch (t: Throwable) {
            selectedFileUri = uri
            selectedFileName = uri.lastPathSegment
            selectedFileSize = null
            selectedFileTooLarge = false
            Toast.makeText(context, "Could not read selected file details.", Toast.LENGTH_SHORT).show()
        }
    }

    // after lawyer submits -> verification pending screen
    if (isSubmitted && role == RegisterRole.LAWYER) {
        VerificationPendingScreen(onBackToLogin = {
            navController?.navigate("login") {
                popUpTo("register") { inclusive = true }
                launchSingleTop = true
            }
        })
        return
    }

    // -------- MAIN UI ----------
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // tightened for neater spacing
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            DarkGreen,
                            DarkGreen.copy(alpha = 0.95f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 28.dp, start = 20.dp, end = 20.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Back
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { navController?.popBackStack() }
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White.copy(alpha = 0.95f),
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Back",
                        color = Color.White.copy(alpha = 0.95f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {

                    // Logo
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .background(Color.White, RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚖️", fontSize = 30.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        // Header text WHITE as requested
                        Text(
                            text = "LegalChain",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Create Account",
                            color = Color.White.copy(alpha = 0.95f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Join us to manage your legal matters",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // CONTENT CARD (scrollable)
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 170.dp),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ---------- ROLE DROPDOWN ----------
                Box(modifier = Modifier.fillMaxWidth()) {

                    Text(
                        text = "I am a",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold, // thicker text
                        color = ThickBlack
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { showRoleDropdown = !showRoleDropdown },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        contentPadding = PaddingValues(horizontal = 18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val icon = when (role) {
                                    RegisterRole.LAWYER -> Icons.Outlined.Work
                                    RegisterRole.CLIENT -> Icons.Outlined.Person
                                    null -> Icons.Outlined.Person
                                }
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = DarkGreen,
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = when (role) {
                                        RegisterRole.LAWYER -> "Lawyer / Advocate"
                                        RegisterRole.CLIENT -> "Client"
                                        null -> "Select your role"
                                    },
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold, // thicker selection text
                                    color = ThickBlack
                                )
                            }

                            Icon(
                                imageVector = Icons.Outlined.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (showRoleDropdown) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 90.dp),
                            shape = RoundedCornerShape(18.dp),
                            tonalElevation = 8.dp,
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column {
                                RoleDropdownItem(
                                    title = "Lawyer / Advocate",
                                    subtitle = "Manage cases and clients",
                                    icon = Icons.Outlined.Work
                                ) {
                                    role = RegisterRole.LAWYER
                                    showRoleDropdown = false
                                }
                                // thicker black divider
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    thickness = 2.dp,
                                    color = ThickBlack
                                )
                                RoleDropdownItem(
                                    title = "Client",
                                    subtitle = "Track your legal matters",
                                    icon = Icons.Outlined.Person
                                ) {
                                    role = RegisterRole.CLIENT
                                    showRoleDropdown = false
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // common field colors (use theme)
                val fieldColors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                    focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    focusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    cursorColor = MaterialTheme.colorScheme.primary
                )

                // ---------- BASE FIELDS ----------
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name", fontSize = 15.sp, color = ThickBlack, fontWeight = FontWeight.Bold) },
                    placeholder = { Text("Enter your full name", fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = fieldColors,
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address", fontSize = 15.sp, color = ThickBlack, fontWeight = FontWeight.Bold) },
                    placeholder = { Text("Enter your email", fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Email,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = fieldColors,
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = {
                        Text(
                            "Phone Number",
                            fontSize = 15.sp,
                            color = ThickBlack,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    placeholder = { Text("Enter mobile number", fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = fieldColors,
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", fontSize = 15.sp, color = ThickBlack, fontWeight = FontWeight.Bold) },
                    placeholder = { Text("Create a password", fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = "Toggle password",
                            modifier = Modifier
                                .size(22.dp)
                                .clickable { showPassword = !showPassword }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation =
                        if (showPassword)
                            androidx.compose.ui.text.input.VisualTransformation.None
                        else
                            androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    colors = fieldColors,
                    shape = RoundedCornerShape(16.dp)
                )

                Text(
                    text = "At least 8 characters with numbers and symbols",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                )

                // ---------- LAWYER EXTRA FIELDS ----------
                if (role == RegisterRole.LAWYER) {
                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Professional Details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold, // thicker heading
                        color = ThickBlack,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = barId,
                        onValueChange = { barId = it },
                        label = { Text("Bar Council ID", fontSize = 15.sp, color = ThickBlack, fontWeight = FontWeight.Bold) },
                        placeholder = { Text("e.g. MAH/1234/2020", fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Description,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = fieldColors,
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // COUNTRY DROPDOWN
                    Text(
                        text = "Country",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ThickBlack,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedButton(
                        onClick = { showCountryDropdown = !showCountryDropdown },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Public,
                                    contentDescription = null,
                                    tint = DarkGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (country.isBlank()) "Select country" else country,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ThickBlack
                                )
                            }
                            Icon(
                                imageVector = Icons.Outlined.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (showCountryDropdown) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            tonalElevation = 8.dp,
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column {
                                countries.forEach { c ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                country = c
                                                lawyerState = ""
                                                showCountryDropdown = false
                                            }
                                            .padding(horizontal = 16.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Public,
                                            contentDescription = null,
                                            tint = DarkGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = c,
                                            fontSize = 15.sp,
                                            color = ThickBlack
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // STATE DROPDOWN (enabled only when country chosen)
                    Text(
                        text = "State",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ThickBlack,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    val availableStates = statesByCountry[country] ?: emptyList()

                    OutlinedButton(
                        onClick = { if (country.isNotBlank()) showStateDropdown = !showStateDropdown },
                        enabled = country.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.LocationOn,
                                    contentDescription = null,
                                    tint = if (country.isNotBlank()) DarkGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when {
                                        country.isBlank() -> "Select country first"
                                        lawyerState.isBlank() -> "Select state"
                                        else -> lawyerState
                                    },
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (country.isNotBlank()) ThickBlack else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            Icon(
                                imageVector = Icons.Outlined.KeyboardArrowDown,
                                contentDescription = null,
                                tint = if (country.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    if (showStateDropdown && availableStates.isNotEmpty()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            tonalElevation = 8.dp,
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column {
                                availableStates.forEach { s ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                lawyerState = s
                                                showStateDropdown = false
                                            }
                                            .padding(horizontal = 16.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.LocationOn,
                                            contentDescription = null,
                                            tint = DarkGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = s,
                                            fontSize = 15.sp,
                                            color = ThickBlack
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = district,
                        onValueChange = { district = it },
                        label = { Text("District", fontSize = 15.sp, color = ThickBlack, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = fieldColors,
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Office Address", fontSize = 15.sp, color = ThickBlack, fontWeight = FontWeight.Bold) },
                        placeholder = { Text("Full office address", fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = fieldColors,
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Proof Documents",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ThickBlack,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // open file picker – allow any type, user can choose PDF/JPG
                                filePickerLauncher.launch("*/*")
                            },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.outline
                        ),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(vertical = 16.dp, horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CloudUpload,
                                contentDescription = null,
                                tint = DarkGreen,
                                modifier = Modifier.size(30.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Upload ID & Bar Certificate",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ThickBlack
                            )
                            Text(
                                text = "PDF, JPG up to 5 MB",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )

                            selectedFileName?.let { name ->
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Selected: $name",
                                    fontSize = 13.sp,
                                    color = if (selectedFileTooLarge) Color.Red else ThickBlack,
                                    textAlign = TextAlign.Center
                                )
                                selectedFileSize?.let { size ->
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = formatBytes(size),
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                                if (selectedFileTooLarge) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "File exceeds 5 MB limit — please choose a smaller file.",
                                        fontSize = 12.sp,
                                        color = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ---------- TERMS ----------
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { agreedTerms = !agreedTerms },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = agreedTerms,
                        onCheckedChange = { agreedTerms = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = DarkGreen,
                            uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            checkmarkColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = buildAnnotatedString {
                            append("I agree to the ")
                            withStyle(
                                SpanStyle(
                                    color = ThickBlack,
                                    fontWeight = FontWeight.SemiBold
                                )
                            ) {
                                append("Terms of Service")
                            }
                            append(" and ")
                            withStyle(
                                SpanStyle(
                                    color = ThickBlack,
                                    fontWeight = FontWeight.SemiBold
                                )
                            ) {
                                append("Privacy Policy")
                            }
                        },
                        fontSize = 14.sp,
                        color = ThickBlack.copy(alpha = 0.95f),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start
                    )
                }

                // ---------- MAIN BUTTON ----------
                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {

                        if (fullName.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
                            Toast.makeText(context, "Fill all required fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (phone.length < 10) {
                            Toast.makeText(context, "Enter valid phone number", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (role == RegisterRole.LAWYER && selectedFileUri == null) {
                            Toast.makeText(context, "Upload document", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val api = ApiClient.apiService

                        val documentPart =
                            if (role == RegisterRole.LAWYER) {
                                val file = uriToFile(selectedFileUri!!, context)
                                val req = file.asRequestBody("application/octet-stream".toMediaType())
                                MultipartBody.Part.createFormData("document", file.name, req)
                            } else null

                        api.register(
                            body(role!!.name.lowercase()),
                            body(fullName),
                            body(email),
                            body(phone),          // ✅ ADD THIS
                            body(password),

                            if (role == RegisterRole.LAWYER) body(barId) else null,
                            if (role == RegisterRole.LAWYER) body(country) else null,
                            if (role == RegisterRole.LAWYER) body(lawyerState) else null,
                            if (role == RegisterRole.LAWYER) body(district) else null,
                            if (role == RegisterRole.LAWYER) body(address) else null,

                            documentPart
                        ).enqueue(object : Callback<RegisterResponse> {

                            override fun onResponse(
                                call: Call<RegisterResponse>,
                                response: Response<RegisterResponse>
                            ) {
                                if (response.isSuccessful && response.body()?.status == "success") {
                                    showSuccessDialog = true
                                } else {
                                    Toast.makeText(
                                        context,
                                        response.body()?.message ?: "Registration failed",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }


                            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                ) {
                    Text(
                        text = if (role == RegisterRole.LAWYER) "Submit for Verification" else "Create Account",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = buildAnnotatedString {
                        append("Already have an account? ")
                        withStyle(
                            style = SpanStyle(
                                color = ThickBlack,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append("Sign In")
                        }
                    },
                    fontSize = 14.sp,
                    color = ThickBlack.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController?.navigate("login") {
                                popUpTo("register") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
    // ================= SUCCESS DIALOG =================
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { /* prevent dismiss */ },
            title = {
                Text(
                    text = "Registration Successful",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Your account has been created successfully.\n\nPlease login to continue."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false

                        navController?.navigate("role_selection") {
                            popUpTo("register") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                ) {
                    Text("Go to Login")
                }
            }
        )
    }
}

/**
 * Safely query display name for Uri using contentResolver (may return null)
 */
private fun queryFileName(resolver: android.content.ContentResolver, uri: Uri): String? {
    val projection = arrayOf(android.provider.OpenableColumns.DISPLAY_NAME)
    var result: String? = null
    resolver.query(uri, projection, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val idx = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (idx >= 0) result = cursor.getString(idx)
        }
    }
    return result
}

/** Simple human-readable bytes */
private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB")
    var value = bytes.toDouble()
    var i = 0
    while (value >= 1024 && i < units.lastIndex) {
        value /= 1024
        i++
    }
    val df = DecimalFormat("#.##")
    return "${df.format(value)} ${units[i]}"
}

@Composable
private fun RoleDropdownItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = DarkGreen,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold, // thicker title
                color = ThickBlack
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = ThickBlack.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun VerificationPendingScreen(onBackToLogin: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(110.dp)
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f), RoundedCornerShape(48.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Verification Pending",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ThickBlack
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Thank you for registering. Your profile and documents are currently under review by our team. This process typically takes 24–48 hours.",
                fontSize = 15.sp,
                color = ThickBlack.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StepRow("Document verification")
                    StepRow("Bar Council ID check")
                    StepRow("Profile activation")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onBackToLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = DarkGreen
                ),
                border = BorderStroke(1.dp, DarkGreen),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = "Back to Login",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun StepRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = DarkGreen,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = ThickBlack
        )
    }
}
private fun body(value: String): okhttp3.RequestBody {
    return value.toRequestBody("text/plain".toMediaType())
}

