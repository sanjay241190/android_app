package com.riffstealer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.riffstealer.app.ui.screens.*
import com.riffstealer.app.viewmodel.LibraryViewModel
import com.riffstealer.app.viewmodel.RecordingViewModel
import com.riffstealer.app.viewmodel.VariationsViewModel

object Routes {
    const val HOME = "home"
    const val RECORDING = "recording"
    const val ANALYSIS = "analysis"
    const val VARIATIONS = "variations"
    const val VARIATION_DETAIL = "variation_detail/{index}"
    const val LIBRARY = "library"
    const val SETTINGS = "settings"
    const val LIBRARY_VARIATIONS = "library_variations/{melodyId}"

    fun variationDetail(index: Int) = "variation_detail/$index"
    fun libraryVariations(melodyId: Long) = "library_variations/$melodyId"
}

@Composable
fun RiffNavHost(
    navController: NavHostController = rememberNavController(),
    recordingViewModel: RecordingViewModel = viewModel(),
    variationsViewModel: VariationsViewModel = viewModel(),
    libraryViewModel: LibraryViewModel = viewModel()
) {
    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                onStartRecording = {
                    recordingViewModel.startRecording()
                    navController.navigate(Routes.RECORDING)
                },
                onNavigateToLibrary = {
                    navController.navigate(Routes.LIBRARY) {
                        popUpTo(Routes.HOME)
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }

        composable(Routes.RECORDING) {
            val amplitudes by recordingViewModel.amplitudes.collectAsState()
            val detectedNotes by recordingViewModel.detectedNoteNames.collectAsState()
            val elapsedMs by recordingViewModel.elapsedMs.collectAsState()

            RecordingScreen(
                amplitudes = amplitudes,
                detectedNotes = detectedNotes,
                elapsedMs = elapsedMs,
                onStopRecording = {
                    recordingViewModel.stopRecording()
                    navController.navigate(Routes.ANALYSIS) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }

        composable(Routes.ANALYSIS) {
            val isAnalyzing by recordingViewModel.isAnalyzing.collectAsState()
            val isGenerating by recordingViewModel.isGenerating.collectAsState()
            val noteNames by recordingViewModel.detectedNoteNames.collectAsState()
            val bpm by recordingViewModel.detectedBpm.collectAsState()
            val durationMs by recordingViewModel.melodyDurationMs.collectAsState()
            val abcNotation by recordingViewModel.abcNotation.collectAsState()

            AnalysisScreen(
                isAnalyzing = isAnalyzing,
                isGenerating = isGenerating,
                noteSequence = noteNames,
                bpm = bpm,
                durationMs = durationMs,
                abcNotation = abcNotation,
                onViewVariations = {
                    variationsViewModel.loadFromRecording(recordingViewModel)
                    navController.navigate(Routes.VARIATIONS)
                },
                onBack = {
                    navController.popBackStack(Routes.HOME, false)
                }
            )
        }

        composable(Routes.VARIATIONS) {
            val variations by variationsViewModel.variations.collectAsState()
            val playingIndex by variationsViewModel.currentlyPlayingIndex.collectAsState()

            VariationsScreen(
                variations = variations,
                currentlyPlayingIndex = playingIndex,
                onPlayVariation = { variationsViewModel.playVariation(it) },
                onFavoriteVariation = { variationsViewModel.toggleFavorite(it) },
                onVariationClick = { index ->
                    navController.navigate(Routes.variationDetail(index))
                },
                onSaveAll = { variationsViewModel.saveAll() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Routes.VARIATION_DETAIL,
            arguments = listOf(navArgument("index") { type = NavType.IntType })
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt("index") ?: 0
            val variation by variationsViewModel.getVariation(index).collectAsState(null)
            val isPlaying by variationsViewModel.isPlaying.collectAsState()
            val progress by variationsViewModel.playbackProgress.collectAsState()

            variation?.let { v ->
                VariationDetailScreen(
                    genre = v.genre,
                    mood = v.mood,
                    tempo = v.tempo,
                    description = v.description,
                    abcNotation = v.abcNotation,
                    isPlaying = isPlaying,
                    progress = progress,
                    durationMs = v.melody.durationMs,
                    onPlayPause = { variationsViewModel.playVariation(index) },
                    onSave = { variationsViewModel.saveVariation(index) },
                    onExport = { variationsViewModel.exportVariation(index) },
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Routes.LIBRARY) {
            val melodies by libraryViewModel.melodies.collectAsState()
            val favorites by libraryViewModel.favorites.collectAsState()

            LibraryScreen(
                melodies = melodies,
                favorites = favorites,
                onMelodyClick = { id ->
                    navController.navigate(Routes.libraryVariations(id))
                },
                onVariationClick = { /* navigate to detail */ },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }

        composable(
            Routes.LIBRARY_VARIATIONS,
            arguments = listOf(navArgument("melodyId") { type = NavType.LongType })
        ) { backStackEntry ->
            val melodyId = backStackEntry.arguments?.getLong("melodyId") ?: 0L
            libraryViewModel.loadVariationsForMelody(melodyId)
            val variations by libraryViewModel.melodyVariations.collectAsState()
            val playingIndex by libraryViewModel.playingIndex.collectAsState()

            VariationsScreen(
                variations = variations,
                currentlyPlayingIndex = playingIndex,
                onPlayVariation = { libraryViewModel.playVariation(it) },
                onFavoriteVariation = { libraryViewModel.toggleFavorite(it) },
                onVariationClick = { },
                onSaveAll = { },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            val apiKey by recordingViewModel.apiKey.collectAsState()

            SettingsScreen(
                apiKey = apiKey,
                onApiKeyChange = { recordingViewModel.updateApiKey(it) },
                onSaveApiKey = { recordingViewModel.saveApiKey() },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onNavigateToLibrary = {
                    navController.navigate(Routes.LIBRARY) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }
    }
}
