package com.simats.legalchain.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    initialName: String,
    initialEmail: String = "",
    initialPhone: String = "",
    initialDesignation: String = "",
    initialLocation: String = "",
    initialBio: String = "",
    onBack: () -> Unit = {},
    onSave: (
        name: String,
        email: String,
        phone: String,
        designation: String,
        location: String,
        bio: String
    ) -> Unit = { _, _, _, _, _, _ -> }
) {
    var name by remember { mutableStateOf(initialName) }
    var email by remember { mutableStateOf(initialEmail) }
    var phone by remember { mutableStateOf(initialPhone) }
    var designation by remember { mutableStateOf(initialDesignation) }
    var location by remember { mutableStateOf(initialLocation) }
    var bio by remember { mutableStateOf(initialBio) }
    var showPhotoDialog by remember { mutableStateOf(false) }

    val initials = remember(name) {
        name.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercaseChar().toString() }
            .ifBlank { "LC" }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Profile",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ---------------- Avatar ----------------
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE5E7EB)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B5563)
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 6.dp, y = 6.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2563EB))
                            .clickable { showPhotoDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = "Change photo",
                            tint = Color.White
                        )
                    }
                }
            }

            fun boldLabel(label: String): @Composable () -> Unit = {
                Text(text = label, fontWeight = FontWeight.Bold)
            }

            // ---------------- Fields ----------------

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = boldLabel("Full Name"),
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = boldLabel("Email"),
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = boldLabel("Phone"),
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "Phone") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )

            OutlinedTextField(
                value = designation,
                onValueChange = { designation = it },
                label = boldLabel("Designation"),
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "Designation") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = boldLabel("Location"),
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "Location") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = boldLabel("Bio"),
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "Bio") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                maxLines = 6,
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )

            // ---------------- Save Button ----------------

            Button(
                onClick = { onSave(name, email, phone, designation, location, bio) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        }
    }

    // ---------------- Photo Dialog ----------------

    if (showPhotoDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoDialog = false },
            title = { Text("Change photo") },
            text = { Text("Photo change not implemented yet.") },
            confirmButton = {
                TextButton(onClick = { showPhotoDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}
