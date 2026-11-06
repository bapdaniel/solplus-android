package com.solplus.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Hoje", Icons.Default.Home)
    object Calendar : Screen("calendar", "Calendário", Icons.Default.CalendarMonth)
    object History : Screen("history", "Histórico", Icons.Default.History)
    object Settings : Screen("settings", "Ajustes", Icons.Default.Settings)
    object Diagnostics : Screen("diagnostics", "Diagnóstico", Icons.Default.HealthAndSafety)
    object Lab : Screen("lab", "Laboratório SOL+", Icons.Default.Build)
}
