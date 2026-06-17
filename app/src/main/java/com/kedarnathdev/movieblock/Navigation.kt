package com.kedarnathdev.movieblock

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kedarnathdev.movieblock.ui.screens.CreateTaskScreen
import com.kedarnathdev.movieblock.ui.screens.TasksScreen

sealed class Screen(val route: String) {
    object CreateTask : Screen("create_task")
    object Tasks : Screen("tasks")
}

@Composable
fun MovieBlockNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.CreateTask.route
    ) {
        composable(Screen.CreateTask.route) {
            CreateTaskScreen(
                onNavigateToTasks = {
                    navController.navigate(Screen.Tasks.route)
                }
            )
        }
        
        composable(Screen.Tasks.route) {
            TasksScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
