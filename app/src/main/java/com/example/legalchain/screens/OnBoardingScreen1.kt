package com.example.legalchain.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val DarkGreen = Color(0xFF004D40)
private val AccentGreen = Color(0xFF26A69A)
private val AccentOrange = Color(0xFFFFA000)

@Composable
fun OnboardingScreen1(navController: NavHostController? = null) {

    var activeDot by remember { mutableStateOf(0) }

    // suitcase bounce
    val bounceAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            bounceAnim.animateTo(
                targetValue = 10f,
                animationSpec = tween(400, easing = LinearEasing)
            )
            bounceAnim.animateTo(
                targetValue = -10f,
                animationSpec = tween(400, easing = LinearEasing)
            )
            delay(50)
        }
    }

    // ====== SLIDE OUT ANIMATION (to next screen) ======
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }

    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var isNavigating by remember { mutableStateOf(false) }

    fun goToNext() {
        if (isNavigating) return
        isNavigating = true
        scope.launch {
            activeDot = 1
            offsetX.animateTo(
                targetValue = -screenWidthPx,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearEasing
                )
            )
            navController?.navigate("onboarding2")
        }
    }

    var dragAmount by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            // detect swipe anywhere on screen
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, dragX ->
                        dragAmount += dragX
                    },
                    onDragEnd = {
                        // swipe left → next screen
                        if (dragAmount < -120f) {
                            goToNext()
                        }
                        dragAmount = 0f
                    },
                    onDragCancel = {
                        dragAmount = 0f
                    }
                )
            }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(offsetX.value.toInt(), 0) },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ========= MIDDLE ILLUSTRATION =========
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(660.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                DarkGreen.copy(alpha = 0.08f),
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

                    // Outer circle
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .background(DarkGreen.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Inner circle
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .background(DarkGreen.copy(alpha = 0.20f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Filled.Work,
                                contentDescription = null,
                                tint = DarkGreen,
                                modifier = Modifier
                                    .size(104.dp)
                                    .offset(y = bounceAnim.value.dp)
                            )
                        }
                    }

                    // Triangle icons
                    TriangleIcon(
                        icon = Icons.Filled.CalendarToday,
                        background = DarkGreen,
                        offsetX = 0.dp,
                        offsetY = (-95).dp
                    )

                    TriangleIcon(
                        icon = Icons.Filled.FolderOpen,
                        background = AccentGreen,
                        offsetX = (-90).dp,
                        offsetY = 45.dp
                    )

                    TriangleIcon(
                        icon = Icons.Filled.People,
                        background = AccentOrange,
                        offsetX = 90.dp,
                        offsetY = 45.dp
                    )
                }
            }

            // ========= PUSH CONTENT TO BOTTOM =========
            Spacer(modifier = Modifier.weight(1f))

            // ========= TEXT AND BUTTONS AT BOTTOM =========
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // progress dots
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(3) { index ->
                        if (index == activeDot) {
                            Box(
                                modifier = Modifier
                                    .width(28.dp)
                                    .height(8.dp)
                                    .background(DarkGreen, RoundedCornerShape(4.dp))
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFFCFCFCF), CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }

                // bottom text
                Text(
                    text = "Manage Your Cases",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Track all your legal cases in one place. Organize by category, monitor status, and never miss important deadlines.",
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    color = Color(0xFF4B5563),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(22.dp))

                // bottom buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            // ✅ direct go to role selection
                            navController?.navigate("role_selection") {
                                popUpTo("onboarding1") { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = DarkGreen
                        )
                    ) {
                        Text("Skip", fontSize = 18.sp)
                    }

                    Button(
                        onClick = { goToNext() },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Next", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxScope.TriangleIcon(
    icon: ImageVector,
    background: Color,
    offsetX: Dp,
    offsetY: Dp
) {
    androidx.compose.material3.Icon(
        imageVector = icon,
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier
            .size(52.dp)
            .align(Alignment.Center)
            .offset(x = offsetX, y = offsetY)
            .background(background, RoundedCornerShape(18.dp))
            .padding(8.dp)
    )
}
