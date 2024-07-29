package com.example.cameraxcompose.composable

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.cameraxcompose.camerax.CameraX
import com.example.cameraxcompose.utils.Commons.REQUIRED_PERMISSIONS
import com.truid.android.ui.carddetection.components.Overlay


@Composable
fun CameraCompose(
    context: Context,
    cameraX: CameraX,
    onCaptureClick: () -> Unit,
) {



    var hasCamPermission by remember {
        mutableStateOf(
            REQUIRED_PERMISSIONS.all {
                ContextCompat.checkSelfPermission(context, it) ==
                        PackageManager.PERMISSION_GRANTED
            })
    }

    var isFlashOn by remember {
        mutableStateOf(false)
    }
//    val lifecycleOwner = LocalLifecycleOwner.current
//    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
//
//    val imageAnalysis = setupImageAnalysis(
//        executor = executorSingleThread,
//        format = imageAnalysisFormat,
//        resolution = Size(
//            (resolution.width * imageAnalysisResolutionFactor).toInt(),
//            (resolution.height * imageAnalysisResolutionFactor).toInt()
//        ),
//        imageAnalysisCallback = imageAnalysisCallback
//    )
//    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
//    var camera: Camera? = null
//    val cameraProvider = cameraProviderFuture.get()
//
//
//
//    camera = cameraProvider?.bindToLifecycle(
//        lifecycleOwner, cameraSelector, imageAnalysis, preview
//    )
//    fun toggleFlash(flashStatus: Boolean) {
//        isFlashOn = true
//        val cameraInfo = camera?.cameraInfo
//        if (cameraInfo != null) {
//            if (cameraInfo.hasFlashUnit() && cameraInfo.torchState.value != null) camera?.cameraControl?.enableTorch(
//                flashStatus
//            )
//        }
//    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { granted ->
            hasCamPermission =granted.size == 2
        }
    )
    LaunchedEffect(key1 = true) {
        launcher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )

    }
//    fun toggleFlashMode() {
//        isFlashOn = !isFlashOn
//        cameraX.setFlashMode(if (isFlashOn) CameraX.FlashMode.ON else CameraX.FlashMode.OFF)
//    }
    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCamPermission) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { cameraX.startCameraPreviewView() }
            )
        }

        Overlay(
            verticalOffset = 0,
            indicatorColor = Color.Green,
            onGloballyPositioned = { topLeft, bottomRight ->
                Log.d("Check", "CameraCompose: working")
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Flash button
                Button(
                    onClick = {
                              cameraX.turnFlashOn()
                    },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Flash on")
                }
                Button(
                    onClick = {
                        cameraX.turnFlashOff()
                    },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Flash off")
                }

                // Capture button
                Button(
                    onClick = onCaptureClick
                ) {
                    Text(text = "Capture")
                }
            }
        }
    }

}

//    Box(modifier = Modifier.fillMaxSize()) {
//        if (hasCamPermission) {
//            AndroidView(
//                modifier = Modifier.fillMaxSize(),
//                factory = { cameraX.startCameraPreviewView() }
//            )
//        }
//
//        Overlay(
//            verticalOffset = 0,
//            indicatorColor = Color.Green,
//            onGloballyPositioned = { topLeft, bottomRight ->
//                Log.d("Check", "CameraCompose: working")
//            }
//        )
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(bottom = 16.dp),
//            verticalArrangement = Arrangement.Bottom,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//          Row {
//              Button(
//                  onClick = onCaptureClick,
////                  modifier = Modifier.align(Alignment.CenterHorizontally)
//              ) {
//                  Text(text = "Cap")
//              }
//
//          }
//        }
//    }
//}

//    Box(modifier = Modifier.fillMaxSize()) {
//        if (hasCamPermission) {
//            AndroidView(
//                modifier = Modifier.fillMaxSize(),
//                factory = { cameraX.startCameraPreviewView() }
//            )
//        }
//
//    }
//    Column(
//        modifier = Modifier.fillMaxSize(), Arrangement.Bottom, Alignment.CenterHorizontally
//    ) {
//        Overlay(
//
//            verticalOffset = 0,
//            indicatorColor = Color.Green,
//            onGloballyPositioned = { topLeft, bottomRight ->
////                Log.d(TruID.LOG_TAG, "---- density: ${screenPixelDensity}")
////                Log.d(
////                    TruID.LOG_TAG,
////                    "---- on globally position of overlay called ${topLeft.x / screenPixelDensity}, ${topLeft.y / screenPixelDensity}"
////                )
//
//
//                Log.d("Check", "CameraCompose: working")
//
//
//
//            }
//        )
//        Button(
//            onClick = onCaptureClick
////            colors = ButtonDefaults.buttonColors(contentColor = )
//
//        ) {
////            Icon(ImageVector = Icons.Default.AddCircle,"capture")
//            Text(text = "  ")
////            Image(painter = Painter, contentDescription = R. )
//        }
//    }
//}
//
