package com.example.legalchain.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
private val LightBorder = Color(0xFFE5E7EB)
private val TitleText = Color(0xFF111827)
private val BodyText = Color(0xFF111111)

@Composable
fun RoleSelectionScreen(navController: NavHostController? = null) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ⚖️ JUSTICE ICON BOX
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .background(DarkGreen, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚖️",
                    fontSize = 40.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Welcome to LegalChain",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TitleText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Please select your role to continue to your personalized dashboard.",
                fontSize = 14.sp,
                color = BodyText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // OPTIONS
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // LAWYER OPTION
                RoleOptionCard(
                    title = "Login as Lawyer",
                    subtitle = "Manage cases, hearings & clients",
                    onClick = {
                        navController?.navigate("login_lawyer")
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Work,
                            contentDescription = "Lawyer",
                            tint = Color.Black,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                )

                // CLIENT OPTION
                RoleOptionCard(
                    title = "Login as Client",
                    subtitle = "Track cases & view documents",
                    onClick = {
                        navController?.navigate("login_client")
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Client",
                            tint = Color.Black,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "By continuing, you agree to our Terms of Service",
                fontSize = 11.sp,
                color = Color(0xFF4B5563),
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
    icon: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(
            width = 2.dp,
            color = LightBorder
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(Color(0xFFE5E7EB), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TitleText
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = BodyText
                )
            }
        }
    }
}
