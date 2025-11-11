package com.example.ecolab.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ecolab.ui.screens.StoreScreen
import com.example.ecolab.ui.screens.EditProfileScreen
import com.example.ecolab.ui.screens.GameMode
import com.example.ecolab.ui.screens.HelpScreen
import com.example.ecolab.ui.screens.HomeScreenV2
import com.example.ecolab.ui.screens.LibraryScreen
import com.example.ecolab.ui.screens.LoginScreen
import com.example.ecolab.ui.screens.MapScreen
import com.example.ecolab.ui.screens.ProfileScreen
import com.example.ecolab.ui.screens.QuizScreen
import com.example.ecolab.ui.screens.QuizSetupScreenV2
import com.example.ecolab.ui.screens.RegisterScreen
import com.example.ecolab.ui.screens.SettingsScreen
import com.example.ecolab.ui.theme.Palette
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.example.ecolab.ui.screens.ForgotPasswordScreen

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

    val context = LocalContext.current

    val openUrl: (String) -> Unit = { url ->
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Map,
        BottomNavItem.Library,
        BottomNavItem.Profile
    )

    val bottomNavRoutes = bottomNavItems.map { it.route }
    val shouldShowScaffold = currentDestination?.route?.let { currentRoute ->
        bottomNavRoutes.any { it == currentRoute }
    } ?: false

    val navHost = @Composable { modifier: Modifier ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = modifier
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreenV2(
                    onQuizClick = { navController.navigate("quiz_setup") },
                    onStoreClick = { navController.navigate("store") }
                )
            }
            composable(BottomNavItem.Map.route) { MapScreen() }
            composable(BottomNavItem.Library.route) { LibraryScreen(onGuideClick = openUrl) }
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
                    },
                    onAchievementsClick = { navController.navigate("achievements") },
                    onSettingsClick = { navController.navigate("settings") },
                    onHelpClick = { navController.navigate("help") }
                )
            }
            composable("edit_profile") {
                EditProfileScreen(onNavigateUp = { navController.navigateUp() })
            }
            composable("achievements") {
                // TODO: Implement AchievementsScreen
            }
            composable("settings") {
                SettingsScreen(navController = navController)
            }
            composable("help") {
                HelpScreen(navController = navController)
            }
            composable(
                route = "quiz/{theme}/{gameMode}",
                arguments = listOf(
                    navArgument("theme") { type = NavType.StringType },
                    navArgument("gameMode") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val theme = backStackEntry.arguments?.getString("theme") ?: "Default"
                val gameModeString = backStackEntry.arguments?.getString("gameMode") ?: "NORMAL"
                val gameMode = try {
                    GameMode.valueOf(gameModeString.uppercase())
                } catch (e: IllegalArgumentException) {
                    GameMode.NORMAL
                }

                QuizScreen(
                    onClose = { navController.popBackStack() },
                    theme = theme,
                    gameMode = gameMode
                )
            }
            composable("quiz_setup") {
                QuizSetupScreenV2(
                    onStartQuiz = { theme, gameMode ->
                        navController.navigate("quiz/$theme/${gameMode.name}")
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable("store") {
                StoreScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("login") {
                LoginScreen(
                    onLogin = {
                        navController.navigate(BottomNavItem.Home.route)
                    },
                    onRegisterClick = {
                        navController.navigate("register")
                    },
                    onForgotPasswordClick = {
                        navController.navigate("forgot_password")
                    }
                )
            }
            composable("forgot_password") {
                ForgotPasswordScreen(
                    onBackClick = { navController.popBackStack() },
                    onPasswordResetSent = {
                        navController.popBackStack()
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
                Surface(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 0.dp,
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .drawBehind {
                                drawRoundRect(
                                    color = Palette.primary.copy(alpha = 0.1f),
                                    size = size,
                                    cornerRadius = CornerRadius(24.dp.toPx()),
                                    style = Stroke(width = 1.dp.toPx())
                                )
                            }
                    ) {
                        NavigationBar(
                            containerColor = Color.Transparent,
                            tonalElevation = 0.dp,
                            modifier = Modifier.clip(RoundedCornerShape(24.dp))
                        ) {
                            bottomNavItems.forEach { screen ->
                                val isSelected = currentDestination?.hierarchy?.any {
                                    it.route == screen.route
                                } == true

                                NavigationBarItem(
                                    icon = {
                                        val scale by animateFloatAsState(
                                            targetValue = if (isSelected) 1.15f else 1f,
                                            animationSpec = spring(stiffness = 300f),
                                            label = "scale"
                                        )

                                        val backgroundColor by animateColorAsState(
                                            targetValue = if (isSelected)
                                                Palette.primary.copy(alpha = 0.15f)
                                            else
                                                Color.Transparent,
                                            animationSpec = spring(stiffness = 300f),
                                            label = "background"
                                        )

                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .scale(scale)
                                                .background(
                                                    color = backgroundColor,
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                BottomNavIcons.getIconForRoute(screen.route, isSelected),
                                                contentDescription = screen.title,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    },
                                    label = {
                                        val textColor by animateColorAsState(
                                            targetValue = if (isSelected)
                                                Palette.primary
                                            else
                                                Palette.textMuted,
                                            animationSpec = spring(stiffness = 300f),
                                            label = "textColor"
                                        )

                                        Text(
                                            text = screen.title,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isSelected)
                                                FontWeight.Bold
                                            else
                                                FontWeight.Medium,
                                            fontSize = 10.sp,
                                            color = textColor
                                        )
                                    },
                                    selected = isSelected,
                                    onClick = {
                                        if (!isSelected) {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                    interactionSource = remember { MutableInteractionSource() },
                                    alwaysShowLabel = true,
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Palette.primary,
                                        unselectedIconColor = Palette.textMuted,
                                        selectedTextColor = Palette.primary,
                                        unselectedTextColor = Palette.textMuted,
                                        indicatorColor = Color.Transparent
                                    )
                                )
                            }
                        }
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