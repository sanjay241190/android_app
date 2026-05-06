package com.spotphoto.saver

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.spotphoto.saver.ui.screens.CameraScreen
import com.spotphoto.saver.ui.screens.SpotDetailScreen
import com.spotphoto.saver.ui.screens.SpotsListScreen

sealed class Screen(val route: String) {
    data object SpotsList : Screen("spots_list")
    data object Camera : Screen("camera")
    data object Detail : Screen("detail")
}

@Composable
fun AppNavigation(viewModel: MainViewModel = viewModel()) {
    val navController = rememberNavController()
    val spots by viewModel.spots.collectAsState()
    val selectedSpot by viewModel.selectedSpot.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    NavHost(navController = navController, startDestination = Screen.SpotsList.route) {

        composable(Screen.SpotsList.route) {
            SpotsListScreen(
                spots = spots,
                selectedCategory = selectedCategory,
                onCategorySelected = { viewModel.setCategory(it) },
                onSpotClick = { spot ->
                    viewModel.selectSpot(spot)
                    navController.navigate(Screen.Detail.route)
                },
                onDeleteSpot = { spot -> viewModel.deleteSpot(spot) },
                onCameraClick = { navController.navigate(Screen.Camera.route) }
            )
        }

        composable(Screen.Camera.route) {
            CameraScreen(
                onPhotoCaptured = { path, lat, lng, bearing, direction, category ->
                    viewModel.saveSpot(path, lat, lng, bearing, direction, category)
                }
            )
        }

        composable(Screen.Detail.route) {
            selectedSpot?.let { spot ->
                SpotDetailScreen(
                    spot = spot,
                    onBack = {
                        viewModel.clearSelection()
                        navController.popBackStack()
                    },
                    onUpdateNote = { note ->
                        viewModel.updateNote(spot, note)
                    },
                    onUpdateCategory = { category ->
                        viewModel.updateCategory(spot, category)
                    }
                )
            }
        }
    }
}
