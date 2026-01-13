package com.simats.legalchain.screens

/* ---------- Animations ---------- */
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween

/* ---------- Foundation ---------- */
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

/* ---------- Icons ---------- */
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Work

/* ---------- Material 3 ---------- */
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text

/* ---------- Runtime ---------- */
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope

/* ---------- UI ---------- */
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
import androidx.compose.ui.unit.*

/* ---------- Navigation ---------- */
import androidx.navigation.NavHostController

/* ---------- Coroutines ---------- */
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private val DarkGreen = Color(0xFF004D40)
private val AccentGreen = Color(0xFF26A69A)
private val AccentOrange = Color(0xFFFFA000)

@Composable
fun OnboardingScreen1(navController: NavHostController? = null) {

    var activeDot by remember { mutableStateOf(0) }

    /* ---------- BOUNCE ANIMATION ---------- */
    val bounceAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            bounceAnim.animateTo(10f, tween(
                durationMillis = 400,
                easing = LinearEasing
            ))
            bounceAnim.animateTo(-10f, tween(
                durationMillis = 400,
                easing = LinearEasing
            ))
            delay(80)
        }
    }

    /* ---------- SCREEN SIZE ---------- */
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val illustrationSize = screenWidth * 0.65f   // responsive size
    val outerCircle = illustrationSize
    val middleCircle = illustrationSize * 0.82f
    val innerCircle = illustrationSize * 0.65f

    /* ---------- SLIDE ANIMATION ---------- */
    val screenWidthPx = with(density) { screenWidth.toPx() }
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var isNavigating by remember { mutableStateOf(false) }

    fun goToNext() {
        if (isNavigating) return
        isNavigating = true
        scope.launch {
            activeDot = 1
            offsetX.animateTo(-screenWidthPx, tween(
                durationMillis = 400,
                easing = LinearEasing
            ))
            navController?.navigate("onboarding2")
        }
    }

    var dragAmount by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, dragX -> dragAmount += dragX },
                    onDragEnd = {
                        if (dragAmount < -120f) goToNext()
                        dragAmount = 0f
                    },
                    onDragCancel = { dragAmount = 0f }
                )
            }
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
                            listOf(DarkGreen.copy(0.08f), Color.White)
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
                            .background(DarkGreen.copy(0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(middleCircle)
                                .background(DarkGreen.copy(0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Filled.Work,
                                contentDescription = null,
                                tint = DarkGreen,
                                modifier = Modifier
                                    .size(innerCircle * 0.6f)
                                    .offset(y = bounceAnim.value.dp)
                            )
                        }
                    }

                    TriangleIcon(Icons.Filled.CalendarToday, DarkGreen, 0.dp, -illustrationSize * 0.42f)
                    TriangleIcon(Icons.Filled.FolderOpen, AccentGreen, -illustrationSize * 0.42f, illustrationSize * 0.22f)
                    TriangleIcon(Icons.Filled.People, AccentOrange, illustrationSize * 0.42f, illustrationSize * 0.22f)
                }
            }

            /* ---------- BOTTOM CONTENT ---------- */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                /* Progress dots */
                Row(horizontalArrangement = Arrangement.Center) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .then(
                                    if (index == activeDot)
                                        Modifier.width(28.dp).height(8.dp)
                                    else Modifier.size(8.dp)
                                )
                                .background(
                                    if (index == activeDot) DarkGreen else Color(0xFFCFCFCF),
                                    if (index == activeDot) RoundedCornerShape(4.dp) else CircleShape
                                )
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                }

                Spacer(Modifier.height(18.dp))

                Text(
                    text = "Manage Your Cases",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Track all your legal cases in one place. Organize by category, monitor status, and never miss deadlines.",
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
                            navController?.navigate("role_selection") {
                                popUpTo("onboarding1") { inclusive = true }
                            }
                        },
                        modifier = Modifier.weight(1f).height(52.dp)
                    ) {
                        Text("Skip", fontSize = 16.sp)
                    }

                    Button(
                        onClick = { goToNext() },
                        modifier = Modifier.weight(1f).height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Next", fontSize = 16.sp)
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
            .size(50.dp)
            .align(Alignment.Center)
            .offset(offsetX, offsetY)
            .background(background, RoundedCornerShape(16.dp))
            .padding(10.dp)
    )
}

