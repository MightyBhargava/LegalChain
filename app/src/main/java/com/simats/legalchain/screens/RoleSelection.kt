package com.simats.legalchain.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

private val DarkGreen = Color(0xFF004D40)

@Composable
fun RoleSelectionScreen(navController: NavHostController? = null) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Main Logo Box — Bigger + Dark Green
            Box(
                modifier = Modifier
                    .size(110.dp) // increased size
                    .background(DarkGreen, RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚖️",
                    fontSize = 56.sp, // larger icon
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = "Welcome to LegalChain",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Please select your role to continue to your personalized dashboard.",
                fontSize = 16.sp,
                color = colors.onBackground.copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                RoleOptionCard(
                    title = "Login as Lawyer",
                    subtitle = "Manage cases, hearings & clients",
                    onClick = { navController?.navigate("login_lawyer") },
                    icon = Icons.Filled.Work
                )

                RoleOptionCard(
                    title = "Login as Client",
                    subtitle = "Track cases & view documents",
                    onClick = { navController?.navigate("login_client") },
                    icon = Icons.Filled.Person
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = "By continuing, you agree to our Terms of Service",
                fontSize = 12.sp,
                color = colors.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RoleOptionCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp) // increased height
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp), // more rounded
        color = colors.surface,
        tonalElevation = 2.dp,
        border = BorderStroke(2.dp, colors.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Bigger icon container
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(colors.surfaceVariant, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = colors.onSurface,
                    modifier = Modifier.size(32.dp) // increased icon size
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 18.sp,   // medium-large
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = colors.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}
