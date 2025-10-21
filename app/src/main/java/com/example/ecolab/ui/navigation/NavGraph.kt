package com.example.ecolab.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ecolab.ui.screens.AchievementsScreen
import com.example.ecolab.ui.screens.EditProfileScreen
import com.example.ecolab.ui.screens.HomeScreen
import com.example.ecolab.ui.screens.LibraryScreen
import com.example.ecolab.ui.screens.LoginScreen
import com.example.ecolab.ui.screens.MapScreen
import com.example.ecolab.ui.screens.ProfileScreen
import com.example.ecolab.ui.screens.QuizScreen
import com.example.ecolab.ui.screens.QuizSetupScreen
import com.example.ecolab.ui.screens.RegisterScreen
import com.example.ecolab.ui.theme.Palette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    viewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isLoggedIn by viewModel.authState.collectAsState(initial = true)

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate("login") {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
            }
        }
    }

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Map,
        BottomNavItem.Library,
        BottomNavItem.Profile
    )

    val bottomNavRoutes = bottomNavItems.map { it.route }
    val shouldShowScaffold = currentDestination?.route in bottomNavRoutes

    val navHost = @Composable { modifier: Modifier ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = modifier
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    onQuizClick = { navController.navigate("quiz_setup") },
                    onAchievementsClick = { navController.navigate("achievements") }
                )
            }
            composable(BottomNavItem.Map.route) { MapScreen() }
            composable(BottomNavItem.Library.route) { LibraryScreen() }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    onEditProfileClick = { navController.navigate("edit_profile") },
                    onSignOutClick = {
                        viewModel.signOut()
                        navController.navigate("login") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable("edit_profile") {
                EditProfileScreen(onNavigateUp = { navController.navigateUp() })
            }
            composable("quiz") { QuizScreen() }
            composable("quiz_setup") {
                QuizSetupScreen(onStartQuiz = { navController.navigate("quiz") })
            }
            composable("achievements") { AchievementsScreen() }
            composable("login") {
                LoginScreen(
                    onLogin = {
                        navController.navigate(BottomNavItem.Home.route)
                    },
                    onRegisterClick = {
                        navController.navigate("register")
                    }
                )
            }
            composable("register") {
                RegisterScreen(
                    onBackClick = { navController.popBackStack() },
                    onRegistrationSuccess = { navController.popBackStack() }
                )
            }
        }
    }

    if (shouldShowScaffold) {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = Palette.surface,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
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
                            alwaysShowLabel = false,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Palette.primary,
                                unselectedIconColor = Palette.textMuted,
                                indicatorColor = Palette.surface
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            navHost(Modifier.padding(innerPadding))
        }
    } else {
        navHost(Modifier)
    }
}
