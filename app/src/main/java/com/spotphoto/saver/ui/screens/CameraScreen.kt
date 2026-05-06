package com.spotphoto.saver.ui.screens

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.spotphoto.saver.util.CompassHelper
import com.spotphoto.saver.util.CompassReading
import com.spotphoto.saver.util.LocationHelper
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CameraScreen(
    onPhotoCaptured: (photoPath: String, lat: Double, lng: Double, bearing: Float, direction: String, category: String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    val locationHelper = remember { LocationHelper(context) }
    val compassHelper = remember { CompassHelper(context) }

    var compassReading by remember { mutableStateOf(CompassReading(0f, "N")) }
    var isCapturing by remember { mutableStateOf(false) }
    var captureSuccess by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(com.spotphoto.saver.data.SpotCategory.GENERAL.tag) }

    val imageCapture = remember { ImageCapture.Builder().build() }

    LaunchedEffect(Unit) {
        compassHelper.observeBearing().collect { reading ->
            compassReading = reading
        }
    }

    // Reset success indicator after showing
    LaunchedEffect(captureSuccess) {
        if (captureSuccess) {
            kotlinx.coroutines.delay(1500)
            captureSuccess = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera Preview
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageCapture
                        )
                    } catch (_: Exception) {}
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Compass overlay (top)
        CompassOverlay(
            reading = compassReading,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp)
        )

        // Success flash
        AnimatedVisibility(
            visible = captureSuccess,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF4CAF50).copy(alpha = 0.9f),
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                    Text("Spot Saved!", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Bottom controls: category selector + capture button
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Category quick-select chips
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.Black.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    com.spotphoto.saver.data.SpotCategory.entries.forEach { cat ->
                        val isSelected = selectedCategory == cat.tag
                        Surface(
                            onClick = { selectedCategory = cat.tag },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color.White.copy(alpha = 0.9f)
                            else Color.Transparent
                        ) {
                            Text(
                                text = cat.emoji,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }

            // Capture button
            Button(
                onClick = {
                    if (!isCapturing) {
                        isCapturing = true
                        scope.launch {
                            captureAndSave(
                                context = context,
                                imageCapture = imageCapture,
                                locationHelper = locationHelper,
                                compassReading = compassReading,
                                onSuccess = { path, lat, lng, bearing, dir ->
                                    onPhotoCaptured(path, lat, lng, bearing, dir, selectedCategory)
                                    captureSuccess = true
                                    isCapturing = false
                                },
                                onError = { isCapturing = false }
                            )
                        }
                    }
                },
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = "Capture",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

@Composable
private fun CompassOverlay(reading: CompassReading, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.Black.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.Explore,
                contentDescription = null,
                tint = Color(0xFF81D4FA),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "${reading.degrees.toInt()}°",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = reading.direction,
                color = Color(0xFF81D4FA),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private suspend fun captureAndSave(
    context: Context,
    imageCapture: ImageCapture,
    locationHelper: LocationHelper,
    compassReading: CompassReading,
    onSuccess: (path: String, lat: Double, lng: Double, bearing: Float, direction: String) -> Unit,
    onError: () -> Unit
) {
    val location = locationHelper.getCurrentLocation()
    if (location == null) {
        onError()
        return
    }

    val photoFile = createPhotoFile(context)
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                onSuccess(
                    photoFile.absolutePath,
                    location.latitude,
                    location.longitude,
                    compassReading.degrees,
                    compassReading.direction
                )
            }

            override fun onError(exception: ImageCaptureException) {
                onError()
            }
        }
    )
}

private fun createPhotoFile(context: Context): File {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val storageDir = File(context.filesDir, "spots")
    storageDir.mkdirs()
    return File(storageDir, "SPOT_${timestamp}.jpg")
}
