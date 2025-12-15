package com.example.legalchain

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.legalchain.cases.*
import com.example.legalchain.data.DataStoreManager
import com.example.legalchain.documents.*
import com.example.legalchain.home.HomeScreen
import com.example.legalchain.home.NotificationScreen
import com.example.legalchain.home.SettingsScreen
import com.example.legalchain.screens.*
import com.example.legalchain.ui.theme.LegalChainTheme
import kotlinx.coroutines.launch
import java.io.File

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
class MainActivity : ComponentActivity() {

    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataStoreManager = DataStoreManager(applicationContext)

        setContent {
            AppContent(dataStoreManager)
        }
    }
}

@Composable
private fun AppContent(
    dataStoreManager: DataStoreManager
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val isDarkMode by dataStoreManager.isDarkModeFlow.collectAsState(false)

    val ctx = LocalContext.current
    val prefs = remember {
        ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    LegalChainTheme(darkTheme = isDarkMode) {
        Surface(color = MaterialTheme.colorScheme.background) {

            NavHost(
                navController = navController,
                startDestination = "splash"
            ) {

                /* ---------------- SPLASH / AUTH ---------------- */

                composable("splash") { SplashScreen(navController) }
                composable("onboarding1") { OnboardingScreen1(navController) }
                composable("onboarding2") { OnboardingScreen2(navController) }
                composable("role_selection") { RoleSelectionScreen(navController) }

                composable("login") { LoginScreen(navController, false) }
                composable("login_lawyer") { LoginScreen(navController, true) }
                composable("login_client") { LoginScreen(navController, false) }
                composable("register") { RegisterScreen(navController) }
                composable("forgot_password") { ForgotPasswordScreen(navController) }

                /* ---------------- HOME ---------------- */

                composable("home") {
                    HomeScreen(
                        onNavigateRaw = { route ->
                            navController.navigate(route.removePrefix("/"))
                        },
                        unreadCountProvider = { 0 }
                    )
                }

                /* ---------------- NOTIFICATIONS ---------------- */

                composable("notifications") {
                    NotificationScreen(
                        onBack = { navController.popBackStack() },
                        onOpenRoute = { route ->
                            navController.navigate(route.removePrefix("/"))
                        }
                    )
                }

                /* ---------------- SETTINGS ---------------- */

                composable("settings") {
                    SettingsScreen(
                        onEmailNotificationsToggle = {},
                        onAppNotificationsToggle = {},
                        onLogoutConfirmed = {
                            scope.launch { }
                            navController.navigate("role_selection") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        onNavigate = { route ->
                            navController.navigate(route.removePrefix("/"))
                        },
                        onBack = { navController.popBackStack() }
                    )
                }

                /* ---------------- CASES ---------------- */

                composable("cases") {
                    val role = prefs.getString("userRole", "client") ?: "client"

                    CaseListScreen(
                        isLawyer = role == "lawyer",
                        onAddCase = { navController.navigate("cases/add") },
                        onOpenCase = { caseId ->
                            navController.navigate("cases/$caseId/activity")
                        },
                        onBack = {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    )
                }

                composable("cases/add") {
                    AddCaseScreen(
                        onBack = { navController.popBackStack() },
                        onCreate = { navController.navigate("cases") },
                        onCancel = { navController.popBackStack() }
                    )
                }

                composable(
                    route = "cases/{caseId}/activity",
                    arguments = listOf(navArgument("caseId") {
                        type = NavType.StringType
                    })
                ) {
                    CaseActivityScreen(onBack = { navController.popBackStack() })
                }

                /* ---------------- DOCUMENT LIST ---------------- */

                composable("docs") {
                    DocumentListScreen(
                        onBack = { navController.popBackStack() },
                        onUpload = { navController.navigate("documents/upload") },
                        onOpenCategory = {
                            navController.navigate("documents/categories")
                        },
                        onOpenDocument = { docId ->
                            navController.navigate("documents/preview/$docId")
                        }
                    )
                }

                /* ---------------- DOCUMENT PREVIEW ---------------- */

                composable(
                    route = "documents/preview/{docId}",
                    arguments = listOf(navArgument("docId") {
                        type = NavType.StringType
                    })
                ) { entry ->
                    val docId = entry.arguments?.getString("docId") ?: ""

                    DocumentPreviewScreen(
                        docId = docId,
                        documentName = "$docId.pdf",
                        pages = 3,
                        size = "245 KB",
                        category = "FIR",
                        relatedCase = "Singh vs State",
                        uploadedAt = "Dec 10, 2024",
                        tags = listOf("important"),
                        onBack = { navController.popBackStack() },
                        onViewOCR = {
                            navController.navigate("documents/ocr/$docId")
                        },
                        onDelete = {
                            navController.popBackStack("docs", false)
                        }
                    )
                }

                /* ---------------- DOCUMENT OCR ---------------- */

                composable(
                    route = "documents/ocr/{docId}",
                    arguments = listOf(navArgument("docId") {
                        type = NavType.StringType
                    })
                ) { entry ->
                    val docId = entry.arguments?.getString("docId") ?: ""
                    val file = File(ctx.filesDir, "$docId.pdf")

                    fun openPdf(path: String) {
                        val uri = FileProvider.getUriForFile(
                            ctx,
                            "${ctx.packageName}.fileprovider",
                            File(path)
                        )
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "application/pdf")
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        }
                        ctx.startActivity(intent)
                    }

                    DocumentOCRScreen(
                        filePath = file.absolutePath,
                        onBack = { navController.popBackStack() },
                        onOpenFile = { openPdf(it) }
                    )
                }

                /* ---------------- DOCUMENT UPLOAD ---------------- */

                composable("documents/upload") {
                    DocumentUploadScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToDocuments = {
                            navController.navigate("docs") {
                                popUpTo("docs") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}
