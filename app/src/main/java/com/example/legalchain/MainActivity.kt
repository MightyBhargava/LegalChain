package com.example.legalchain

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.legalchain.profile.HistoryScreen
import com.example.legalchain.profile.FavoritesScreen
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.navigation.NavType
import com.example.legalchain.profile.HelpScreen
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.legalchain.ai.AIInsightsScreen
import com.example.legalchain.cases.*
import com.example.legalchain.data.DataStoreManager
import com.example.legalchain.documents.*
import com.example.legalchain.home.HomeScreen
import com.example.legalchain.home.NotificationScreen
import com.example.legalchain.home.SettingsScreen
import com.example.legalchain.screens.*
import com.example.legalchain.profile.EditProfileScreen
import com.example.legalchain.profile.ProfileScreen
import com.example.legalchain.search.SearchScreen
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
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun AppContent(
    dataStoreManager: DataStoreManager
)
 {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val isDarkMode by dataStoreManager.isDarkModeFlow.collectAsState(false)

    val ctx = LocalContext.current
    LaunchedEffect(Unit) {
        DocumentRepository.init(ctx)
    }
    val prefs = remember {
        ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    LegalChainTheme(darkTheme = isDarkMode) {
        Surface(color = MaterialTheme.colorScheme.background) {

            NavHost(
                navController = navController,
                startDestination = "splash"
            ) {

                /* ---------- AUTH & ONBOARDING ---------- */
                composable("splash") { SplashScreen(navController) }
                composable("onboarding1") { OnboardingScreen1(navController) }
                composable("onboarding2") { OnboardingScreen2(navController) }
                composable("role_selection") { RoleSelectionScreen(navController) }

                composable("login") { LoginScreen(navController, false) }
                composable("login_lawyer") { LoginScreen(navController, true) }
                composable("login_client") { LoginScreen(navController, false) }
                composable("register") { RegisterScreen(navController) }
                composable("forgot_password") {
                    ForgotPasswordScreen(
                        navController = navController
                    )
                }
                /* ---------- HOME ---------- */
                composable("home") {
                    HomeScreen(
                        onNavigateRaw = { route ->
                            if (navController.currentDestination?.route != route) {
                                navController.navigate(route)
                            }
                        },
                        unreadCountProvider = { 0 }
                    )
                }

                /* ---------- AI INSIGHTS ---------- */
                composable("ai/insights") {
                    AIInsightsScreen(
                        onBack = { navController.popBackStack() },
                        onNavigate = { route ->
                            val cleanRoute = route.removePrefix("/")
                            if (navController.currentDestination?.route != cleanRoute) {
                                navController.navigate(cleanRoute)
                            }
                        }
                    )
                }

                composable("ai/daily") {
                    AIInsightsScreen(
                        onBack = { navController.popBackStack() },
                        onNavigate = { route ->
                            val cleanRoute = route.removePrefix("/")
                            if (navController.currentDestination?.route != cleanRoute) {
                                navController.navigate(cleanRoute)
                            }
                        }
                    )
                }
                /* ---------- NOTIFICATIONS ---------- */
                composable("notifications") {
                    NotificationScreen(
                        onBack = { navController.popBackStack() },
                        onOpenRoute = { route ->
                            navController.navigate(route)
                        }
                    )
                }

                /* ---------- SETTINGS ---------- */
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
                            navController.navigate(route)
                        },
                        onBack = { navController.popBackStack() }
                    )
                }

                /* ---------- CASES ---------- */
                composable("cases") {
                    val role = prefs.getString("userRole", "client") ?: "client"
                    val isLawyer = role == "lawyer"

                    CaseListScreen(
                        isLawyer = isLawyer,
                        onAddCase = { navController.navigate("cases/add") },
                        onOpenCase = { caseId ->
                            navController.navigate("cases/$caseId/details")
                        },
                        onBack = {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        onNavigate = { route ->
                            val cleanRoute = route.removePrefix("/")
                            if (navController.currentDestination?.route != cleanRoute) {
                                navController.navigate(cleanRoute)
                            }
                        },
                        onBrowseCategories = {
                            navController.navigate("cases/categories")
                        }
                    )
                }

                composable("cases/add") {
                    AddCaseScreen(
                        existingCaseId = null,
                        onBack = { navController.popBackStack() },
                        onCreate = { navController.navigate("cases") },
                        onCancel = { navController.popBackStack() }
                    )
                }

                composable(
                    "cases/edit/{caseId}",
                    arguments = listOf(navArgument("caseId") { type = NavType.StringType })
                ) { entry ->
                    val caseId = entry.arguments?.getString("caseId") ?: ""
                    AddCaseScreen(
                        existingCaseId = caseId,
                        onBack = { navController.popBackStack() },
                        onCreate = {
                            navController.navigate("cases/$caseId/details") {
                                popUpTo("cases/$caseId/details") { inclusive = true }
                            }
                        },
                        onCancel = { navController.popBackStack() }
                    )
                }

                composable(
                    "cases/{caseId}/details",
                    arguments = listOf(navArgument("caseId") { type = NavType.StringType })
                ) { entry ->
                    val caseId = entry.arguments?.getString("caseId") ?: ""
                    CaseDetailsScreen(
                        caseId = caseId,
                        onBack = { navController.popBackStack() },
                        onNavigate = { route ->
                            if (navController.currentDestination?.route != route) {
                                navController.navigate(route)
                            }
                        }
                    )
                }

                composable("cases/activity") {
                    CaseActivityScreen(onBack = { navController.popBackStack() })
                }

                /* ---------- PAYMENTS ---------- */
                composable("payments") {
                    PlaceholderScreen(title = "Payments", onBack = { navController.popBackStack() })
                }

                /* ---------- HEARINGS ---------- */
                composable("hearings/add") {
                    AddHearingScreen(
                        onBack = { navController.popBackStack() },
                        onSave = { navController.popBackStack() }
                    )
                }

                /* ---------- DOCUMENTS ---------- */
                composable("docs") {
                    DocumentListScreen(
                        onBack = {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        onUpload = { navController.navigate("documents/upload") },
                        onOpenCategory = { navController.navigate("documents/categories") },
                        onOpenDocument = { docId ->
                            navController.navigate("documents/preview/$docId")
                        },
                        onNavigate = { route ->
                            if (navController.currentDestination?.route != route) {
                                navController.navigate(route)
                            }
                        }
                    )
                }

                composable("cases/categories") {
                    CaseCategoriesScreen(
                        onBack = { navController.popBackStack() },
                        onCategoryClick = { /* TODO: filter cases by category id */ },
                        onNavigate = { route ->
                            if (navController.currentDestination?.route != route) {
                                navController.navigate(route)
                            }
                        }
                    )
                }

                composable("documents/categories") {
                    DocumentCategoriesScreen(
                        onBack = { navController.popBackStack() },
                        onOpenCategory = { navController.navigate("docs") }
                    )
                }

                composable(
                    "documents/preview/{docId}",
                    arguments = listOf(navArgument("docId") { type = NavType.StringType })
                ) { entry ->
                    val docId = entry.arguments?.getString("docId") ?: ""
                    val documents by DocumentRepository.documents.collectAsState()
                    val document = documents.find { it.id == docId }

                    if (document == null) {
                        LaunchedEffect(Unit) {
                            navController.popBackStack()
                        }
                    } else {
                        DocumentPreviewScreen(
                            docId = document.id,
                            docType = document.type,
                            documentName = document.name,
                            pages = 3,
                            size = document.size,
                            category = document.category,
                            relatedCase = document.caseName,
                            uploadedAt = document.uploadedAt,
                            tags = document.tags,
                            fileName = document.fileName,
                            onBack = { navController.popBackStack() },
                            onViewOCR = {
                                navController.navigate("documents/ocr/${document.id}")
                            },
                            onDelete = {
                                DocumentRepository.removeDocument(document.id, ctx)
                                val popped = navController.popBackStack("docs", false)
                                if (!popped) {
                                    navController.popBackStack()
                                    navController.navigate("docs") {
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onOpenShare = {
                                navController.navigate("documents/share/${document.id}")
                            },
                            onAddTag = { updatedTags ->
                                DocumentRepository.updateDocumentTags(document.id, updatedTags, ctx)
                            }
                        )
                    }
                }

                composable(
                    "documents/share/{docId}",
                    arguments = listOf(navArgument("docId") { type = NavType.StringType })
                ) { entry ->
                    val docId = entry.arguments?.getString("docId") ?: ""
                    DocumentShareScreen(
                        docId = docId,
                        documentName = "$docId.pdf",
                        size = "245 KB",
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(
                    "documents/ocr/{docId}",
                    arguments = listOf(navArgument("docId") { type = NavType.StringType })
                ) { entry ->
                    val docId = entry.arguments?.getString("docId") ?: ""
                    val documents by DocumentRepository.documents.collectAsState()
                    val document = documents.find { it.id == docId }

                    if (document == null) {
                        LaunchedEffect(Unit) {
                            navController.popBackStack()
                        }
                    } else {
                        val file = File(ctx.filesDir, document.fileName)

                        fun openFile(path: String) {
                            val fileToOpen = File(path)
                            val uri = FileProvider.getUriForFile(
                                ctx,
                                "${ctx.packageName}.fileprovider",
                                fileToOpen
                            )
                            val extension = fileToOpen.extension.lowercase()
                            val mimeType = when (extension) {
                                "pdf" -> "application/pdf"
                                "jpg", "jpeg" -> "image/jpeg"
                                "png" -> "image/png"
                                "doc" -> "application/msword"
                                "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                                else -> "application/octet-stream"
                            }

                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(uri, mimeType)
                                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            }
                            ctx.startActivity(intent)
                        }

                        DocumentOCRScreen(
                            filePath = file.absolutePath,
                            onBack = { navController.popBackStack() },
                            onOpenFile = { openFile(it) },
                            onOpenShare = {
                                navController.navigate("documents/share/${document.id}")
                            }
                        )
                    }
                }

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

                /* ---------- SEARCH ---------- */
                composable("search") {
                    SearchScreen(
                        onBack = { navController.popBackStack() },
                        onNavigate = { route ->
                            if (navController.currentDestination?.route != route) {
                                navController.navigate(route)
                            }
                        }
                    )
                }

                /* ---------- PROFILE ---------- */
                composable("profile") {
                    val role = prefs.getString("userRole", "client") ?: "client"
                    val isLawyer = role == "lawyer"
                    val defaultName = if (isLawyer) "Adv. Rajesh Kumar" else "Client User"
                    val defaultTitle = if (isLawyer) "Senior Advocate • High Court" else "Your Legal Companion"
                    val profileEntry = navController.getBackStackEntry("profile")
                    val nameState = remember(profileEntry) {
                        profileEntry.savedStateHandle.getStateFlow("profile_name", defaultName)
                    }
                    val titleState = remember(profileEntry) {
                        profileEntry.savedStateHandle.getStateFlow("profile_title", defaultTitle)
                    }
                    val profileName by nameState.collectAsState()
                    val profileTitle by titleState.collectAsState()

                    ProfileScreen(
                        isLawyer = isLawyer,
                        lawyerName = if (isLawyer) profileName else "Adv. Rajesh Kumar",
                        lawyerTitle = if (isLawyer) profileTitle else "Senior Advocate • High Court",
                        clientName = if (!isLawyer) profileName else "Client User",
                        clientTitle = if (!isLawyer) profileTitle else "Your Legal Companion",
                        onBack = {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        onNavigate = { route ->
                            val cleanRoute = route.removePrefix("/")
                            if (navController.currentDestination?.route != cleanRoute) {
                                navController.navigate(cleanRoute)
                            }
                        },
                        onLogoutConfirm = {
                            navController.navigate("role_selection") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    )
                }

                composable("profile/edit") {
                    val role = prefs.getString("userRole", "client") ?: "client"
                    val isLawyer = role == "lawyer"
                    val profileEntry = navController.getBackStackEntry("profile")
                    val currentName = profileEntry.savedStateHandle.get<String>("profile_name")
                        ?: if (isLawyer) "Adv. Rajesh Kumar" else "Client User"
                    val currentTitle = profileEntry.savedStateHandle.get<String>("profile_title")
                        ?: if (isLawyer) "Senior Advocate • High Court" else "Your Legal Companion"

                    EditProfileScreen(
                        initialName = currentName,
                        initialDesignation = currentTitle,
                        onBack = { navController.popBackStack() },
                        onSave = { updatedName, _, _, updatedDesignation, _, _ ->
                            profileEntry.savedStateHandle["profile_name"] = updatedName
                            profileEntry.savedStateHandle["profile_title"] = updatedDesignation
                            navController.popBackStack()
                        }
                    )
                }

                /* ---------- HELP & SUPPORT ---------- */
                composable("profile/help") {
                    HelpScreen(
                        onBack = {
                            navController.navigate("profile") {
                                popUpTo("profile") { inclusive = false }
                            }
                        }
                    )
                }

                /* ---------- PROFILE HISTORY ---------- */
                composable("profile/history") {
                    HistoryScreen(
                        onBack = {
                            navController.navigate("profile") {
                                popUpTo("profile") { inclusive = false }
                            }
                        }
                    )
                }

                /* ---------- PROFILE FAVORITES ---------- */
                composable("profile/favorites") {
                    FavoritesScreen(
                        onBack = {
                            navController.navigate("profile") {
                                popUpTo("profile") { inclusive = false }
                            }
                        }
                    )
                }

                /* ---------- CHAT ---------- */
                composable("chat/lawyer") {
                    PlaceholderScreen(title = "Client Messages", onBack = { navController.popBackStack() })
                }

                composable("chat/client") {
                    PlaceholderScreen(title = "Lawyer Chat", onBack = { navController.popBackStack() })
                }

                composable("ai/assistant") {
                    PlaceholderScreen(title = "AI Legal Assistant", onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(title: String, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF004D40), titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Build, null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                Spacer(Modifier.height(16.dp))
                Text("$title Feature Coming Soon", fontWeight = FontWeight.Medium, color = Color.Gray)
            }
        }
    }
}
