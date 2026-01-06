package com.example.legalchain.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.ui.platform.LocalContext
import android.content.Context
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
import com.example.legalchain.network.BasicResponse
import com.example.legalchain.network.ApiClient
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

private enum class ForgotStep { EMAIL, OTP }

@Composable
fun ForgotPasswordScreen(
    navController: NavHostController? = null
)
 {
     var step by remember { mutableStateOf(ForgotStep.EMAIL) }

    var showPassword by remember { mutableStateOf(false) }
    var enteredEmail by remember { mutableStateOf("") }
     val ctx = LocalContext.current
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
                        ForgotStep.OTP -> "Reset Password"
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
                    ForgotStep.EMAIL -> EmailStep(
                        onContinue = { step = ForgotStep.OTP },
                        onEmailEntered = { enteredEmail = it }
                    )

                    ForgotStep.OTP -> ResetStep(
                        email = enteredEmail,
                        showPassword = showPassword,
                        onShowPasswordChange = { showPassword = it },
                        onResetDone = {
                            val role = ctx
                                .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                .getString("userRole", "client")

                            if (role == "lawyer") {
                                navController?.navigate("login_lawyer")
                            } else {
                                navController?.navigate("login_client")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmailStep(
    onContinue: () -> Unit,
    onEmailEntered: (String) -> Unit
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
            onClick = {
                ApiClient.apiService.forgotPassword(
                    type = "email",
                    value = email
                )
                    .enqueue(object : retrofit2.Callback<BasicResponse> {

                        override fun onResponse(
                            call: retrofit2.Call<BasicResponse>,
                            response: retrofit2.Response<BasicResponse>
                        ) {
                            if (response.body()?.status == "success") {
                                onEmailEntered(email)   // ✅ SAVE REAL EMAIL
                                onContinue()
                            }
                        }

                        override fun onFailure(
                            call: retrofit2.Call<BasicResponse>,
                            t: Throwable
                        ) {
                            // ignore for now
                        }
                    })
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkGreen,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Send OTP", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
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
private fun ResetStep(
    email: String,
    showPassword: Boolean,
    onShowPasswordChange: (Boolean) -> Unit,
    onResetDone: () -> Unit
)
{
    // ✅ SUCCESS STATE (MUST BE INSIDE COMPOSABLE)
    var otp by remember { mutableStateOf("") }

    var showSuccessDialog by remember { mutableStateOf(false) }

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
                modifier = Modifier.size(40.dp)
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
// 🔢 OTP FIELD (ADD THIS)
        OutlinedTextField(
            value = otp,
            onValueChange = { otp = it },
            label = { Text("OTP") },
            placeholder = { Text("Enter 6-digit OTP") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = fieldColors
        )

        Spacer(modifier = Modifier.height(12.dp))
        // 🔐 NEW PASSWORD
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = fieldColors,
            shape = RoundedCornerShape(16.dp),
            visualTransformation =
                if (showPassword) VisualTransformation.None
                else PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 🔐 CONFIRM PASSWORD
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = fieldColors,
            shape = RoundedCornerShape(16.dp),
            visualTransformation =
                if (showPassword) VisualTransformation.None
                else PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = showPassword,
                onCheckedChange = onShowPasswordChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = DarkGreen,
                    uncheckedColor = Color.Gray
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Show passwords")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔁 RESET BUTTON
        Button(
            onClick = {
                if (newPassword == confirmPassword && otp.isNotBlank()) {
                    ApiClient.apiService.resetPassword(
                        type = "email",
                        value = email,
                        otp = otp,
                        password = newPassword
                    )
                        .enqueue(object : retrofit2.Callback<BasicResponse> {

                            override fun onResponse(
                                call: retrofit2.Call<BasicResponse>,
                                response: retrofit2.Response<BasicResponse>
                            ) {
                                val body = response.body()
                                if (body?.status == "success") {
                                    showSuccessDialog = true
                                } else {
                                    // ❌ OTP invalid / expired
                                    // You can show a toast or text error
                                }
                            }

                            override fun onFailure(
                                call: retrofit2.Call<BasicResponse>,
                                t: Throwable
                            ) {}
                        })
                }
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
        ) {
            Text("Reset Password", color = Color.White)
        }
    }

    // ✅ SUCCESS DIALOG
    if (showSuccessDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {},
            title = { Text("Password Changed", fontWeight = FontWeight.Bold) },
            text = { Text("Your password has been updated successfully.") },
            confirmButton = {
                Button(onClick = {
                    showSuccessDialog = false
                    onResetDone()
                }) {
                    Text("Go to Login")
                }
            }
        )
    }
}
