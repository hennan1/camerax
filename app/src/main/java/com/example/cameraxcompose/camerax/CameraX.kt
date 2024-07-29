package com.example.cameraxcompose.camerax

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.cameraxcompose.utils.Commons.showLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import android.os.Build.MODEL
import android.os.Build.BRAND
import android.os.Build.MANUFACTURER
import android.util.Log
import android.view.Surface
import android.view.Surface.ROTATION_90

class CameraX(
    private var context: Context,
    private var owner: LifecycleOwner,
) {
    private lateinit var cameraControl: CameraControl // Declare cameraControl

    private var imageCapture: ImageCapture? = null
//
//        enum class FlashMode {
//            ON,
//            OFF
//        }
//
//        private var flashMode: FlashMode = FlashMode.OFF
//
//    fun setFlashMode(mode: FlashMode) {
//        flashMode = mode
//        startCameraPreviewView()
//        // Check if imageCapture is initialized
//        val imageCapture = imageCapture ?: return
//
//        // Convert FlashMode to ImageCapture.FlashMode
//        val imageCaptureFlashMode = when (flashMode) {
//            FlashMode.ON -> ImageCapture.FLASH_MODE_ON
//            FlashMode.OFF -> ImageCapture.FLASH_MODE_OFF
//        }
//
//        // Apply flash mode setting to ImageCapture
//        imageCapture.flashMode = imageCaptureFlashMode
//
//        Log.d("flash", "Flash mode set to: $mode")
//    }

//    fun toggleFlash(){
//        imageCapture.
//    }

    // Other CameraX functionalities...

    @SuppressLint("WrongConstant", "RestrictedApi")
    fun startCameraPreviewView(): PreviewView {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val previewView = PreviewView(context)
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val aspectRatio = 1
        imageCapture = ImageCapture.Builder()
            .setFlashMode(ImageCapture.FLASH_MODE_ON)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setTargetAspectRatio(aspectRatio)
            .build()
//        val cameraProvider = ProcessCameraProvider.getInstance(context)
//        val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
//        val imageAnalyzer = imageAnalyzer.setAnalyzer(executor, analyzer)
//        val camera = cameraProvider.bindToLifecycle(
//            this, cameraSelector, preview, imageCapture, imageAnalyzer)
//
//        if (camera.cameraInfo.hasFlashUnit()) {
//            camera.cameraControl.enableTorch(true)
//        }
//        imageCapture = ImageCapture.Builder().setFlashMode(ImageCapture.FLASH_MODE_ON).build()


        val camSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        try {
           val cam =  cameraProviderFuture.get().bindToLifecycle(
                owner,
                camSelector,
                preview,
                imageCapture




           )
            if (cam.cameraInfo.hasFlashUnit()) {
                cam.cameraControl.enableTorch(false)
            } else {

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return previewView
    }
//    fun setFocusArea(centerX: Float, centerY: Float) {
//        cameraControl.startFocusAndMetering(
//            FocusMeteringAction.Builder(
//                MeteringPointFactory().convertPoint(centerX, centerY),
//                FocusMeteringAction.FLAG_AF
//            ).build()
//        )
//    }
    fun turnFlashOff(){

        imageCapture?.flashMode = ImageCapture.FLASH_MODE_OFF
        Log.d("flash", "turnFlashOff: called")
    }
    fun turnFlashOn(){
        imageCapture?.flashMode = ImageCapture.FLASH_MODE_ON
        Log.d("flash", "turnOnOff: called")

    }

     fun capturePhoto() =owner.lifecycleScope.launch{
        val imageCapture = imageCapture ?: return@launch


        imageCapture.takePicture(ContextCompat.getMainExecutor(context), object :
            ImageCapture.OnImageCapturedCallback(), ImageCapture.OnImageSavedCallback {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                owner.lifecycleScope.launch {
                    saveMediaToStorage(
                        imageProxyToBitmap(image),
                        System.currentTimeMillis().toString()
                    )
                }
                image.close()
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                showLog("onCaptureSuccess: Uri  ${outputFileResults.savedUri}")
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                showLog("onCaptureSuccess: onError")
            }
        })


    }

    private suspend fun imageProxyToBitmap(image: ImageProxy): Bitmap =
        withContext(owner.lifecycleScope.coroutineContext) {
            val planeProxy = image.planes[0]
            val buffer: ByteBuffer = planeProxy.buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }



    fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val brand = Build.BRAND
        return if (model.lowercase().startsWith(manufacturer.lowercase())) {
            capitalize(model)
        } else {
            "${capitalize(manufacturer)} $model $brand"
        }
    }

    private fun capitalize(s: String?): String {
        if (s.isNullOrEmpty()) {
            return ""
        }
        val first = s[0]
        return if (first.isUpperCase()) {
            s
        } else {
            first.uppercaseChar() + s.substring(1)
        }
    }

    private suspend fun saveMediaToStorage(bitmap: Bitmap, name: String) {
        withContext(IO) {
            val filename = "${name+getDeviceName()}.jpg"
            var fos: OutputStream? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.contentResolver?.also { resolver ->
                    val contentValues = ContentValues().apply {

                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        put(
                            MediaStore.MediaColumns.RELATIVE_PATH,
                            Environment.DIRECTORY_DCIM

                        )}
                    val imageUri: Uri? =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                    fos = imageUri?.let { with(resolver) { openOutputStream(it) } }
                }
            } else {
                val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                val image = File(imagesDir, filename).also { fos = FileOutputStream(it) }
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                    mediaScanIntent.data = Uri.fromFile(image)
                    context.sendBroadcast(mediaScanIntent)
                }
            }

            fos?.use {
                val success = async(IO) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }
                if (success.await()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Saved Successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }
        }
    }

}