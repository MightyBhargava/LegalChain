package com.example.legalchain.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

private val DarkGreen = Color(0xFF004D40)
private val AccentGreen = Color(0xFF26A69A)
private val AccentAmber = Color(0xFFFFA000)

@Composable
fun OnboardingScreen2(navController: NavHostController? = null) {

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }

    val offsetX = remember { Animatable(screenWidthPx) }
    val scope = rememberCoroutineScope()

    val brainRotation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        offsetX.animateTo(
            targetValue = 0f,
            animationSpec = tween(300, easing = LinearEasing)
        )
    }

    LaunchedEffect("brain-rotation") {
        while (true) {
            brainRotation.animateTo(
                targetValue = 360f,
                animationSpec = tween(2000, easing = LinearEasing)
            )
            brainRotation.snapTo(0f)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(offsetX.value.toInt(), 0) },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // TOP / ILLUSTRATION
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(660.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                AccentGreen.copy(alpha = 0.08f),
                                Color.White
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .background(AccentGreen.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .background(AccentGreen.copy(alpha = 0.20f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🧠",
                                fontSize = 56.sp,
                                modifier = Modifier.graphicsLayer(
                                    rotationZ = brainRotation.value
                                )
                            )
                        }
                    }

                    FloatingIcon(
                        icon = Icons.Filled.AutoAwesome,
                        background = DarkGreen,
                        offsetX = (-90).dp,
                        offsetY = (-70).dp
                    )

                    FloatingIcon(
                        icon = Icons.Filled.Notifications,
                        background = AccentAmber,
                        offsetX = 95.dp,
                        offsetY = 0.dp
                    )

                    FloatingIcon(
                        icon = Icons.Filled.Description,
                        background = AccentGreen,
                        offsetX = (-70).dp,
                        offsetY = 70.dp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // BOTTOM CONTENT
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // dots
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFFCFCFCF), RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height(8.dp)
                            .background(DarkGreen, RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFFCFCFCF), RoundedCornerShape(4.dp))
                    )
                }

                Text(
                    text = "AI-Powered Insights",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Get smart reminders, AI-generated summaries, document scanning with OCR, and predictive case analytics.",
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    color = Color(0xFF4B5563),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(22.dp))

                // SIDE BY SIDE BUTTONS
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                offsetX.animateTo(
                                    targetValue = screenWidthPx,
                                    animationSpec = tween(300, easing = LinearEasing)
                                )
                                navController?.popBackStack()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = DarkGreen
                        )
                    ) {
                        Text("Back", fontSize = 18.sp)
                    }

                    Button(
                        onClick = {
                            // ✅ now go to role selection screen
                            navController?.navigate("role_selection")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Get Started", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxScope.FloatingIcon(
    icon: ImageVector,
    background: Color,
    offsetX: Dp,
    offsetY: Dp
) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier
            .size(48.dp)
            .align(Alignment.Center)
            .offset(x = offsetX, y = offsetY)
            .background(background, RoundedCornerShape(18.dp))
            .padding(8.dp)
    )
}
