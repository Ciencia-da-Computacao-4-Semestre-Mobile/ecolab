package com.example.ecolab.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ecolab.ui.theme.AccentTeal
import com.example.ecolab.ui.theme.EcoLabTheme
import com.example.ecolab.ui.screens.HomeScreen
import com.example.ecolab.ui.screens.LibraryScreen
import com.example.ecolab.ui.screens.MapScreen
import com.example.ecolab.ui.screens.ProfileScreen

// --- Placeholder Screens (will be replaced by full implementations later) ---
@Composable fun RankingScreen() { Box(Modifier.fillMaxSize()) { Text("Ranking Screen", Modifier.align(Alignment.Center)) } }
@Composable fun AchievementsScreen() { Box(Modifier.fillMaxSize()) { Text("Achievements Screen", Modifier.align(Alignment.Center)) } }
@Composable fun QuickActionScreen() { Box(Modifier.fillMaxSize()) { Text("Quick Action Screen", Modifier.align(Alignment.Center)) } }

/**
 * The main navigation graph and Scaffold structure for the app, as per the design prompt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Map,
        BottomNavItem.Library,
        BottomNavItem.Profile
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Eco Lab") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("quick_action") },
                containerColor = AccentTeal,
                elevation = FloatingActionButtonDefaults.elevation(0.dp) // No shadow
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nova Ação")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            NavigationBar(
                tonalElevation = 0.dp // No shadow
            ) {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
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
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { 
                HomeScreen(
                    onOpenRanking = { navController.navigate("ranking") },
                    onOpenAchievements = { navController.navigate("achievements") }
                ) 
            }
            composable("map") { MapScreen() }
            composable("library") { LibraryScreen() }
            composable("profile") { ProfileScreen() }
            composable("ranking") { RankingScreen() }
            composable("achievements") { AchievementsScreen() }
            composable("quick_action") { QuickActionScreen() }
        }
    }
}
