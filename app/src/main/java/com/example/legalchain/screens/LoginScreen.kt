package com.example.legalchain.screens

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.legalchain.auth.GoogleAuthHelper
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.Divider
import androidx.compose.foundation.layout.*
import android.app.Activity
import androidx.compose.ui.res.painterResource
import com.example.legalchain.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import com.example.legalchain.network.ApiClient
import com.example.legalchain.network.ApiService
import com.example.legalchain.network.LoginResponse
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

private val DarkGreenLogin = Color(0xFF004D40)

@Composable
fun LoginScreen(
    navController: NavHostController? = null,
    isLawyer: Boolean = false
) {
    val context = LocalContext.current
    val activity = context as Activity

    val googleAuthHelper = remember {
        GoogleAuthHelper(activity)
    }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            googleAuthHelper.handleSignInResult(
                data = result.data,
                onSuccess = { email, uid, _ ->

                    val api = ApiClient.apiService

                    val role = if (isLawyer) "lawyer" else "client"
                    val fullName = email.substringBefore("@")

                    api.googleLogin(
                        email = email,
                        googleUid = uid,
                        fullName = fullName,
                        role = role
                    ).enqueue(object : retrofit2.Callback<LoginResponse> {

                        override fun onResponse(
                            call: retrofit2.Call<LoginResponse>,
                            response: retrofit2.Response<LoginResponse>
                        ) {
                            if (response.isSuccessful && response.body()?.status == "success") {

                                val user = response.body()!!.user!!

                                val prefs = context.getSharedPreferences(
                                    "app_prefs",
                                    Context.MODE_PRIVATE
                                )

                                prefs.edit()
                                    .putString("userId", user.id)
                                    .putString("userRole", user.role)
                                    .putString("userName", user.fullName)
                                    .putString("userEmail", user.email)
                                    .apply()

                                navController?.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }

                            } else {
                                android.widget.Toast.makeText(
                                    context,
                                    response.body()?.message ?: "Google login failed",
                                    android.widget.Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onFailure(
                            call: retrofit2.Call<LoginResponse>,
                            t: Throwable
                        ) {
                            android.widget.Toast.makeText(
                                context,
                                "Network error",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                },
                onError = { error ->
                    android.widget.Toast.makeText(
                        context,
                        error,
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    }
    val headerColor = DarkGreenLogin   // always dark green

    var loginMethod by remember { mutableStateOf(LoginMethod.EMAIL) }
    var showPassword by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // HEADER AREA (bigger, dark green)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp) // slightly taller header
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            headerColor,
                            headerColor.copy(alpha = 0.95f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 44.dp, start = 20.dp, end = 20.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Back (larger touch target + text)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable {
                            navController?.popBackStack()
                        }
                        .padding(6.dp) // increased hit area
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White.copy(alpha = 0.95f),
                        modifier = Modifier.size(22.dp) // increased icon size
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Back",
                        color = Color.White.copy(alpha = 0.95f),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // logo box – bigger
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⚖️",
                            fontSize = 30.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = if (isLawyer) "Lawyer Login" else "Client Login",
                            color = Color.White,
                            fontSize = 26.sp, // larger heading
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "LegalChain",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }
        }

        // CONTENT CARD
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 200.dp),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Toggle: Email / Mobile (larger icons + text)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(16.dp))
                        .padding(6.dp)
                ) {
                    ToggleChip(
                        selected = loginMethod == LoginMethod.EMAIL,
                        onClick = { loginMethod = LoginMethod.EMAIL },
                        label = "Email",
                        icon = Icons.Default.Email
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ToggleChip(
                        selected = loginMethod == LoginMethod.MOBILE,
                        onClick = { loginMethod = LoginMethod.MOBILE },
                        label = "Mobile",
                        icon = Icons.Default.PhoneAndroid
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    // Common text field colors: larger text
                    val fieldColors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        focusedLeadingIconColor = Color.Black,
                        unfocusedLeadingIconColor = Color.Black,
                        focusedTrailingIconColor = Color.Black,
                        unfocusedTrailingIconColor = Color.Black,
                        cursorColor = Color.Black
                    )

                    if (loginMethod == LoginMethod.EMAIL) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address", fontSize = 16.sp) },
                            placeholder = { Text("Enter your email", color = Color.Black.copy(alpha = 0.6f), fontSize = 15.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = fieldColors,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
                        )
                    } else {
                        OutlinedTextField(
                            value = mobile,
                            onValueChange = { mobile = it },
                            label = { Text("Mobile Number", fontSize = 16.sp) },
                            placeholder = { Text("+91 Enter mobile number", color = Color.Black.copy(alpha = 0.6f), fontSize = 15.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.PhoneAndroid,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = fieldColors,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
                        )
                    }

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", fontSize = 16.sp) },
                        placeholder = { Text("Enter your password", color = Color.Black.copy(alpha = 0.6f), fontSize = 15.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password",
                                tint = Color.Black,
                                modifier = Modifier
                                    .size(26.dp)
                                    .clickable { showPassword = !showPassword }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation =
                            if (showPassword)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                        colors = fieldColors,
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Demo: Editable",
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280)
                            )
                        }

                        TextButton(
                            onClick = {
                                // ✅ navigate to ForgotPasswordScreen
                                navController?.navigate("forgot_password")
                            }
                        ) {
                            Text(
                                text = "Forgot Password?",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = headerColor
                            )
                        }
                    }

                    Button(
                        onClick = {

                            if (email.isBlank() || password.isBlank()) {
                                android.widget.Toast.makeText(
                                    context,
                                    "Email and Password required",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            val role = if (isLawyer) "lawyer" else "client"
                            val api = ApiClient.apiService
                            api.login(
                                type = "email",
                                value = email,
                                password = password
                            )
                                .enqueue(object : retrofit2.Callback<LoginResponse> {

                                    override fun onResponse(
                                        call: retrofit2.Call<LoginResponse>,
                                        response: retrofit2.Response<LoginResponse>
                                    ) {
                                        if (response.isSuccessful && response.body()?.status == "success") {

                                            val user = response.body()!!.user!!

                                            val prefs = context.getSharedPreferences(
                                                "app_prefs",
                                                Context.MODE_PRIVATE
                                            )

                                            prefs.edit()
                                                .putString("userId", user.id)
                                                .putString("userRole", user.role)
                                                .putString("userName", user.fullName)
                                                .putString("userEmail", user.email)
                                                .putString("document", user.document)
                                                .apply()

                                            navController?.navigate("home") {
                                                popUpTo("login") { inclusive = true }
                                            }

                                        } else {
                                            android.widget.Toast.makeText(
                                                context,
                                                response.body()?.message ?: "Invalid login",
                                                android.widget.Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }

                                    override fun onFailure(
                                        call: retrofit2.Call<LoginResponse>,
                                        t: Throwable
                                    ) {
                                        android.widget.Toast.makeText(
                                            context,
                                            "Network error",
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = headerColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Sign In",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                     Spacer(modifier = Modifier.height(14.dp))
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = buildAnnotatedString {
                        append("Don't have an account? ")
                        withStyle(
                            style = SpanStyle(
                                color = headerColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append("Sign Up")
                        }
                    },
                    fontSize = 15.sp,
                    color = Color(0xFF4B5563),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController?.navigate("register")
                        }
                )
                Spacer(modifier = Modifier.height(16.dp))

// ---- OR divider ----
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Divider(modifier = Modifier.weight(1f))
                    Text(
                        text = " OR ",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Divider(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

// ---- Sign up with Google ----
                Button(
                    onClick = {
                        googleLauncher.launch(
                            googleAuthHelper.getSignInIntent()
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {

                        Icon(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "Google",
                            modifier = Modifier.size(22.dp),
                            tint = Color.Unspecified
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Sign up with Google",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

private enum class LoginMethod { EMAIL, MOBILE }

@Composable
private fun RowScope.ToggleChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    icon: ImageVector
) {
    val bgColor = if (selected) Color.White else Color.Transparent
    val textColor = if (selected) Color(0xFF111827) else Color(0xFF6B7280)
    val elevation = if (selected) 1.dp else 0.dp

    Surface(
        modifier = Modifier
            .weight(1f)
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = bgColor,
        tonalElevation = elevation
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}