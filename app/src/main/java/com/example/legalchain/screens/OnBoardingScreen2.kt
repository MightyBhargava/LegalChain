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
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthPx = with(density) { screenWidth.toPx() }

    val illustrationSize = screenWidth * 0.65f
    val outerCircle = illustrationSize
    val middleCircle = illustrationSize * 0.82f
    val innerCircle = illustrationSize * 0.65f

    val offsetX = remember { Animatable(screenWidthPx) }
    val scope = rememberCoroutineScope()
    val brainRotation = remember { Animatable(0f) }

    /* ---------- ENTRY SLIDE ---------- */
    LaunchedEffect(Unit) {
        offsetX.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 300,
                easing = LinearEasing
            )
        )
    }

    /* ---------- ROTATION ---------- */
    LaunchedEffect(Unit) {
        while (true) {
            brainRotation.animateTo(
                targetValue = 360f,
                animationSpec = tween(
                    durationMillis = 2000,
                    easing = LinearEasing
                )
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

            /* ---------- TOP ILLUSTRATION ---------- */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                AccentGreen.copy(alpha = 0.08f),
                                Color.White
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier.size(illustrationSize),
                    contentAlignment = Alignment.Center
                ) {

                    Box(
                        modifier = Modifier
                            .size(outerCircle)
                            .background(AccentGreen.copy(0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(middleCircle)
                                .background(AccentGreen.copy(0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🧠",
                                fontSize = (illustrationSize.value * 0.22f).sp,
                                modifier = Modifier.graphicsLayer(
                                    rotationZ = brainRotation.value
                                )
                            )
                        }
                    }

                    FloatingIcon(
                        icon = Icons.Filled.AutoAwesome,
                        background = DarkGreen,
                        offsetX = -illustrationSize * 0.4f,
                        offsetY = -illustrationSize * 0.3f
                    )

                    FloatingIcon(
                        icon = Icons.Filled.Notifications,
                        background = AccentAmber,
                        offsetX = illustrationSize * 0.4f,
                        offsetY = 0.dp
                    )

                    FloatingIcon(
                        icon = Icons.Filled.Description,
                        background = AccentGreen,
                        offsetX = -illustrationSize * 0.3f,
                        offsetY = illustrationSize * 0.3f
                    )
                }
            }

            /* ---------- BOTTOM CONTENT ---------- */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                /* Dots */
                Row(horizontalArrangement = Arrangement.Center) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .then(
                                    if (index == 1)
                                        Modifier.width(28.dp).height(8.dp)
                                    else Modifier.size(8.dp)
                                )
                                .background(
                                    if (index == 1) DarkGreen else Color(0xFFCFCFCF),
                                    RoundedCornerShape(4.dp)
                                )
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                }

                Spacer(Modifier.height(18.dp))

                Text(
                    text = "AI-Powered Insights",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Get smart reminders, AI-generated summaries, document scanning with OCR, and predictive case analytics.",
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = Color(0xFF4B5563),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(22.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                offsetX.animateTo(
                                    targetValue = screenWidthPx,
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = LinearEasing
                                    )
                                )
                                navController?.popBackStack()
                            }
                        },
                        modifier = Modifier.weight(1f).height(52.dp)
                    ) {
                        Text("Back", fontSize = 16.sp)
                    }

                    Button(
                        onClick = {
                            navController?.navigate("role_selection")
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Get Started", fontSize = 16.sp)
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
            .background(background, RoundedCornerShape(16.dp))
            .padding(8.dp)
    )
}


