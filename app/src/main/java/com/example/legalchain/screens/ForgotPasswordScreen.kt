package com.example.legalchain.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

private val DarkGreen = Color(0xFF004D40)
private val SecondaryGreen = Color(0xFF26A69A)

private enum class ForgotStep { EMAIL, SENT, RESET }

@Composable
fun ForgotPasswordScreen(navController: NavHostController? = null) {

    var step by remember { mutableStateOf(ForgotStep.EMAIL) }
    var showPassword by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
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
                    .padding(top = 56.dp, start = 20.dp, end = 20.dp),
                horizontalAlignment = Alignment.Start
            ) {

                // Back to Login (larger / medium)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { navController?.popBackStack() }
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp) // increased size
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Back to Login",
                        color = Color.White,
                        fontSize = 18.sp, // medium text for back
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = when (step) {
                        ForgotStep.EMAIL -> "Forgot Password?"
                        ForgotStep.SENT -> "Check Your Email"
                        ForgotStep.RESET -> "Reset Password"
                    },
                    color = Color.White,
                    fontSize = 28.sp, // large heading
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // CONTENT CARD
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 160.dp),
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

                when (step) {
                    ForgotStep.EMAIL -> EmailStep(onContinue = { step = ForgotStep.SENT })
                    ForgotStep.SENT -> SentStep(
                        onTryAnother = { step = ForgotStep.EMAIL },
                        onReceived = { step = ForgotStep.RESET },
                        onBackToLogin = { navController?.popBackStack() }
                    )

                    ForgotStep.RESET -> ResetStep(
                        showPassword = showPassword,
                        onShowPasswordChange = { showPassword = it },
                        onResetDone = { navController?.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmailStep(onContinue: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(96.dp)
                .background(DarkGreen.copy(alpha = 0.08f), RoundedCornerShape(48.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = null,
                tint = DarkGreen,
                modifier = Modifier.size(40.dp) // increased icon size
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        val fieldColors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedLabelColor = Color.Black,
            unfocusedLabelColor = Color.Black,
            focusedLeadingIconColor = Color.Black,
            unfocusedLeadingIconColor = Color.Black,
            cursorColor = Color.Black
        )

        var email by remember { mutableStateOf("") }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address", fontSize = 16.sp, fontWeight = FontWeight.Medium) },
            placeholder = { Text("Enter your registered email", fontSize = 15.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true,
            colors = fieldColors,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkGreen,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Send Reset Link", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Remember your password? Sign In",
            fontSize = 15.sp,
            color = Color(0xFF4B5563),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SentStep(
    onTryAnother: () -> Unit,
    onReceived: () -> Unit,
    onBackToLogin: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(96.dp)
                .background(SecondaryGreen.copy(alpha = 0.12f), RoundedCornerShape(48.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = SecondaryGreen,
                modifier = Modifier.size(40.dp) // increased icon size
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF3F4F6)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "We sent a reset link to",
                    fontSize = 15.sp,
                    color = Color(0xFF6B7280)
                )
                Text(
                    text = "user@example.com",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Didn't receive the email? Check your spam folder or try another email.",
            fontSize = 15.sp,
            color = Color(0xFF4B5563),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onReceived,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkGreen,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("I've Received the Link", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBackToLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = DarkGreen
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Back to Login", fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Try another email",
            fontSize = 15.sp,
            color = DarkGreen,
            modifier = Modifier.clickable { onTryAnother() },
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ResetStep(
    showPassword: Boolean,
    onShowPasswordChange: (Boolean) -> Unit,
    onResetDone: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(96.dp)
                .background(DarkGreen.copy(alpha = 0.08f), RoundedCornerShape(48.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Key,
                contentDescription = null,
                tint = DarkGreen,
                modifier = Modifier.size(40.dp) // increased icon size
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        val fieldColors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedLabelColor = Color.Black,
            unfocusedLabelColor = Color.Black,
            cursorColor = Color.Black
        )

        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password", fontSize = 16.sp, fontWeight = FontWeight.Medium) },
            placeholder = { Text("Enter new password", fontSize = 15.sp) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = fieldColors,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password", fontSize = 16.sp, fontWeight = FontWeight.Medium) },
            placeholder = { Text("Confirm new password", fontSize = 15.sp) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = fieldColors,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = showPassword,
                onCheckedChange = onShowPasswordChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = DarkGreen,
                    uncheckedColor = Color(0xFF9CA3AF),
                    checkmarkColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Show passwords",
                fontSize = 15.sp,
                color = Color(0xFF4B5563)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onResetDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkGreen,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Reset Password", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
