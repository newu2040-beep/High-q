package com.example

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.ProjectRepository
import com.example.firebase.FirebaseManager
import com.example.model.EnhancementPreset
import com.example.model.EnhancementTool
import com.example.model.VideoProject
import com.example.ui.components.NavigationTab
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.ExportScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.HIGHQTheme
import kotlinx.coroutines.launch

sealed class Screen {
    object Welcome : Screen()
    object Auth : Screen()
    object Home : Screen()
    object Library : Screen()
    object Settings : Screen()
    data class Editor(
        val videoUri: Uri? = null,
        val project: VideoProject? = null,
        val tool: EnhancementTool = EnhancementTool.UPSCALE
    ) : Screen()
    data class Export(
        val videoUri: Uri? = null,
        val project: VideoProject? = null,
        val targetResolution: String = "4K"
    ) : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = ProjectRepository(applicationContext)
        val firebaseManager = FirebaseManager.getInstance(applicationContext)

        setContent {
            var currentTheme by remember { mutableStateOf(AppThemeMode.OBSIDIAN_DARK) }
            var currentScreen by remember {
                mutableStateOf<Screen>(
                    if (firebaseManager.isUserSignedIn()) Screen.Home else Screen.Welcome
                )
            }

            val projects by repository.projectsFlow.collectAsState(initial = emptyList())
            val coroutineScope = rememberCoroutineScope()

            // If Auth screen is active, use Pastel theme for that clean pastel aesthetic
            val effectiveTheme = if (currentScreen is Screen.Auth) {
                AppThemeMode.MINT_PASTEL
            } else {
                currentTheme
            }

            HIGHQTheme(themeMode = effectiveTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                        when (screen) {
                            is Screen.Welcome -> {
                                WelcomeScreen(
                                    onContinueWithGoogle = {
                                        coroutineScope.launch {
                                            firebaseManager.signInWithGoogle()
                                            currentScreen = Screen.Home
                                        }
                                    },
                                    onContinueWithEmail = {
                                        currentScreen = Screen.Auth
                                    },
                                    onExploreAsGuest = {
                                        currentScreen = Screen.Home
                                    }
                                )
                            }

                            is Screen.Auth -> {
                                BackHandler {
                                    currentScreen = Screen.Welcome
                                }
                                AuthScreen(
                                    firebaseManager = firebaseManager,
                                    onBack = { currentScreen = Screen.Welcome },
                                    onAuthSuccess = { currentScreen = Screen.Home }
                                )
                            }

                            is Screen.Home -> {
                                HomeScreen(
                                    firebaseManager = firebaseManager,
                                    projects = projects,
                                    onSelectVideoUri = { uri, tool ->
                                        currentScreen = Screen.Editor(videoUri = uri, tool = tool)
                                    },
                                    onOpenProject = { proj ->
                                        currentScreen = Screen.Editor(
                                            videoUri = proj.sourceUriString?.let { Uri.parse(it) },
                                            project = proj
                                        )
                                    },
                                    onNavigateTab = { tab ->
                                        currentScreen = when (tab) {
                                            NavigationTab.HOME -> Screen.Home
                                            NavigationTab.LIBRARY -> Screen.Library
                                            NavigationTab.SETTINGS -> Screen.Settings
                                        }
                                    },
                                    onOpenProfile = {
                                        currentScreen = Screen.Settings
                                    }
                                )
                            }

                            is Screen.Library -> {
                                BackHandler {
                                    currentScreen = Screen.Home
                                }
                                LibraryScreen(
                                    projects = projects,
                                    onOpenProject = { proj ->
                                        currentScreen = Screen.Editor(
                                            videoUri = proj.sourceUriString?.let { Uri.parse(it) },
                                            project = proj
                                        )
                                    },
                                    onDeleteProject = { id ->
                                        coroutineScope.launch {
                                            repository.deleteProject(id)
                                        }
                                    },
                                    onImportNewVideo = { uri ->
                                        currentScreen = Screen.Editor(videoUri = uri)
                                    },
                                    onNavigateTab = { tab ->
                                        currentScreen = when (tab) {
                                            NavigationTab.HOME -> Screen.Home
                                            NavigationTab.LIBRARY -> Screen.Library
                                            NavigationTab.SETTINGS -> Screen.Settings
                                        }
                                    }
                                )
                            }

                            is Screen.Settings -> {
                                BackHandler {
                                    currentScreen = Screen.Home
                                }
                                SettingsScreen(
                                    firebaseManager = firebaseManager,
                                    currentTheme = currentTheme,
                                    onSelectTheme = { currentTheme = it },
                                    onNavigateTab = { tab ->
                                        currentScreen = when (tab) {
                                            NavigationTab.HOME -> Screen.Home
                                            NavigationTab.LIBRARY -> Screen.Library
                                            NavigationTab.SETTINGS -> Screen.Settings
                                        }
                                    },
                                    onOpenAuth = {
                                        currentScreen = Screen.Auth
                                    }
                                )
                            }

                            is Screen.Editor -> {
                                BackHandler {
                                    currentScreen = Screen.Home
                                }
                                EditorScreen(
                                    videoUri = screen.videoUri,
                                    project = screen.project,
                                    initialTool = screen.tool,
                                    onBack = { currentScreen = Screen.Home },
                                    onStartEnhancing = { tool, preset, strength, targetRes ->
                                        currentScreen = Screen.Export(
                                            videoUri = screen.videoUri,
                                            project = screen.project,
                                            targetResolution = targetRes
                                        )
                                    }
                                )
                            }

                            is Screen.Export -> {
                                BackHandler {
                                    currentScreen = Screen.Editor(
                                        videoUri = screen.videoUri,
                                        project = screen.project
                                    )
                                }
                                ExportScreen(
                                    videoUri = screen.videoUri,
                                    project = screen.project,
                                    targetResolution = screen.targetResolution,
                                    onBack = {
                                        currentScreen = Screen.Editor(
                                            videoUri = screen.videoUri,
                                            project = screen.project
                                        )
                                    },
                                    onExportComplete = { newProject ->
                                        coroutineScope.launch {
                                            repository.saveProject(newProject)
                                            currentScreen = Screen.Library
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
