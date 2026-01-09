package com.simats.legalchain.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

// ✔️ Dark Green Brand Color
private val DarkGreen = Color(0xFF004D40)

@Composable
fun SplashScreen(navController: NavHostController) {

    val rotation = remember { Animatable(0f) }

    // ✔️ Force splash background = DARK GREEN always
    val bg = DarkGreen
    val onBg = Color.White
    val subtleOnBg = Color.White.copy(alpha = 0.15f)

    LaunchedEffect(Unit) {
        rotation.animateTo(
            targetValue = 720f,
            animationSpec = tween(
                durationMillis = 2000,
                easing = LinearEasing
            )
        )
        delay(300)

        navController.navigate("onboarding1") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg),
        contentAlignment = Alignment.Center
    ) {

        // Soft circular decorative rings
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .offset(x = 40.dp, y = 80.dp)
                    .size(128.dp)
                    .border(1.dp, subtleOnBg, CircleShape)
            )
            Box(
                modifier = Modifier
                    .offset(x = 260.dp, y = 160.dp)
                    .size(88.dp)
                    .border(1.dp, subtleOnBg, CircleShape)
            )
            Box(
                modifier = Modifier
                    .offset(x = 80.dp, y = 460.dp)
                    .size(96.dp)
                    .border(1.dp, subtleOnBg, CircleShape)
            )
            Box(
                modifier = Modifier
                    .offset(x = 220.dp, y = 520.dp)
                    .size(72.dp)
                    .border(1.dp, subtleOnBg, CircleShape)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 40.dp)
        ) {

            // Rotating Logo Tile
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .graphicsLayer { rotationZ = rotation.value }
                    .background(Color.White, RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚖️",
                    fontSize = 42.sp,
                    color = DarkGreen,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            color = Color(0xFF26A69A), // mint green highlight
                            shape = CircleShape
                        )
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Sparkle",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "LegalChain",
                style = MaterialTheme.typography.headlineLarge,
                color = onBg
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "AI-Powered Legal Management",
                style = MaterialTheme.typography.titleMedium,
                color = onBg.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = "Secure",
                    tint = onBg.copy(alpha = 0.85f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Secure • Smart • Simple",
                    style = MaterialTheme.typography.bodySmall,
                    color = onBg.copy(alpha = 0.85f)
                )
            }
        }

        Text(
            text = "Version 1.0.0",
            style = MaterialTheme.typography.labelSmall,
            color = onBg.copy(alpha = 0.4f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}
