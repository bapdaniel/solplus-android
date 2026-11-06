package com.solplus.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.content.ContextCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.solplus.app.core.notification.NotificationHelper
import com.solplus.app.ui.navigation.Screen
import com.solplus.app.ui.screens.calendar.CalendarScreen
import com.solplus.app.ui.screens.calendar.CalendarViewModel
import com.solplus.app.ui.screens.diagnostic.DiagnosticScreen
import com.solplus.app.ui.screens.diagnostic.DiagnosticViewModel
import com.solplus.app.ui.screens.history.HistoryScreen
import com.solplus.app.ui.screens.history.HistoryViewModel
import com.solplus.app.ui.screens.home.HomeScreen
import com.solplus.app.ui.screens.home.HomeViewModel
import com.solplus.app.ui.screens.settings.SettingsScreen
import com.solplus.app.ui.screens.settings.SettingsViewModel
import com.solplus.app.core.testing.TestLabManager
import com.solplus.app.ui.screens.lab.LabScreen
import com.solplus.app.ui.theme.SolCardBg
import com.solplus.app.ui.theme.SolDarkGreen
import com.solplus.app.ui.theme.SolPlusTheme

class MainActivity : ComponentActivity() {

    private var initialPromptType by mutableStateOf<String?>(null)
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SolPlus)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        initialPromptType = intent?.getStringExtra(NotificationHelper.EXTRA_PROMPT_TYPE)

        val app = application as SolPlusApp
        val repository = app.repository
        val alarmScheduler = app.alarmScheduler
        val notificationHelper = app.notificationHelper
        val preferences = app.preferences

        setContent {
            val fontScale by preferences.fontScale.collectAsState(initial = 1.15f)

            SolPlusTheme(fontScale = fontScale) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Instanciação dos ViewModels
                val homeViewModel: HomeViewModel = viewModel {
                    HomeViewModel(repository, alarmScheduler)
                }
                val calendarViewModel: CalendarViewModel = viewModel {
                    CalendarViewModel(repository)
                }
                val historyViewModel: HistoryViewModel = viewModel {
                    HistoryViewModel(repository)
                }
                val settingsViewModel: SettingsViewModel = viewModel {
                    SettingsViewModel(repository, preferences, com.solplus.app.core.util.BackupManager(app.database))
                }
                val diagnosticViewModel: DiagnosticViewModel = viewModel {
                    DiagnosticViewModel(applicationContext, notificationHelper)
                }
                val labManager = remember { TestLabManager(applicationContext, alarmScheduler) }

                // Abertura automática ao clicar na Notificação
                LaunchedEffect(initialPromptType) {
                    val promptType = initialPromptType
                    if (promptType != null) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        when (promptType) {
                            NotificationHelper.PROMPT_MORNING -> homeViewModel.showMorningPrompt()
                            NotificationHelper.PROMPT_NIGHT -> homeViewModel.showNightPrompt()
                        }
                        initialPromptType = null
                    }
                }

                // As 4 abas da barra inferior idênticas à referência visual
                val navItems = listOf(
                    Screen.Home,
                    Screen.Calendar,
                    Screen.History,
                    Screen.Settings
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier.defaultMinSize(minHeight = 72.dp),
                            containerColor = SolCardBg,
                            tonalElevation = 6.dp
                        ) {
                            navItems.forEach { screen ->
                                val isSelected = currentRoute == screen.route
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = screen.title
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = screen.title,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 12.sp,
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                    },
                                    selected = isSelected,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = SolDarkGreen,
                                        indicatorColor = SolDarkGreen,
                                        unselectedIconColor = Color(0xFF6B7280),
                                        unselectedTextColor = Color(0xFF6B7280)
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(viewModel = homeViewModel)
                        }
                        composable(Screen.Calendar.route) {
                            CalendarScreen(viewModel = calendarViewModel)
                        }
                        composable(Screen.History.route) {
                            HistoryScreen(viewModel = historyViewModel)
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(
                                viewModel = settingsViewModel,
                                onNavigateToDiagnostics = {
                                    navController.navigate(Screen.Diagnostics.route)
                                },
                                onNavigateToLab = {
                                    navController.navigate(Screen.Lab.route)
                                }
                            )
                        }
                        composable(Screen.Diagnostics.route) {
                            DiagnosticScreen(viewModel = diagnosticViewModel)
                        }
                        composable(Screen.Lab.route) {
                            LabScreen(
                                labManager = labManager,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val promptType = intent.getStringExtra(NotificationHelper.EXTRA_PROMPT_TYPE)
        if (promptType != null) {
            initialPromptType = promptType
        }
    }
}
