package com.example.legalchain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.legalchain.screens.ForgotPasswordScreen
import com.example.legalchain.screens.LoginScreen
import com.example.legalchain.screens.OnboardingScreen1
import com.example.legalchain.screens.OnboardingScreen2
import com.example.legalchain.screens.RegisterScreen
import com.example.legalchain.screens.RoleSelectionScreen
import com.example.legalchain.screens.SplashScreen
import com.example.legalchain.ui.theme.LegalChainTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LegalChainTheme {
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "splash"
                    ) {
                        composable("splash") {
                            SplashScreen(navController)
                        }
                        composable("onboarding1") {
                            OnboardingScreen1(navController)
                        }
                        composable("onboarding2") {
                            OnboardingScreen2(navController)
                        }
                        composable("role_selection") {
                            RoleSelectionScreen(navController)
                        }
                        composable("login_lawyer") {
                            LoginScreen(navController = navController, isLawyer = true)
                        }
                        composable("login_client") {
                            LoginScreen(navController = navController, isLawyer = false)
                        }
                        composable("register") {
                            RegisterScreen(navController)
                        }
                        composable("forgot_password") {
                            ForgotPasswordScreen(navController)
                        }
                    }
                }
            }
        }
    }
}
