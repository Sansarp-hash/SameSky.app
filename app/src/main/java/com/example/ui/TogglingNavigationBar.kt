package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import com.example.Screen
import com.example.ui.theme.ShimmeringGold
import com.example.ui.theme.WarmObsidian
import com.example.ui.theme.MutedText

@Composable
fun TogglingNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        Pair(Screen.Discover, "Encyclopedia"),
        Pair(Screen.Profile, "Community"),
        Pair(Screen.MoreHub, "More")
    )

    BoxWithConstraints {
        if (maxWidth < 600.dp) {
            NavigationBar(
                modifier = modifier.fillMaxWidth(),
                containerColor = WarmObsidian,
                tonalElevation = 8.dp
            ) {
                items.forEach { (screen, label) ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = label) },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ShimmeringGold,
                            selectedTextColor = ShimmeringGold,
                            indicatorColor = ShimmeringGold.copy(alpha = 0.2f),
                            unselectedIconColor = MutedText,
                            unselectedTextColor = MutedText
                        )
                    )
                }
            }
        } else {
            NavigationRail(
                modifier = modifier.fillMaxHeight(),
                containerColor = WarmObsidian,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                items.forEach { (screen, label) ->
                    val isSelected = currentRoute == screen.route
                    NavigationRailItem(
                        icon = { Icon(screen.icon, contentDescription = label) },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = ShimmeringGold,
                            selectedTextColor = ShimmeringGold,
                            indicatorColor = ShimmeringGold.copy(alpha = 0.2f),
                            unselectedIconColor = MutedText,
                            unselectedTextColor = MutedText
                        )
                    )
                }
            }
        }
    }
}
