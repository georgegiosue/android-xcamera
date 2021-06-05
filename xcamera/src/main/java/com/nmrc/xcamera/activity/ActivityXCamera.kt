package com.nmrc.xcamera.activity

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.nmrc.xcamera.util.XCamera
import com.nmrc.xcamera.util.newToast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ActivityXCamera(private val activity: ComponentActivity) {

    private var imageCapture: ImageCapture? = null
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private var outputDir: File
    private lateinit var path: Uri
    private val mainExe by lazy { ContextCompat.getMainExecutor(this.activity) }

    init { outputDir = outputDirectory() }

    fun start(
        selector: CameraSelector,
        @XCamera.Mode mode: Int = XCamera.MAX_QUALITY,
        surface: Preview.SurfaceProvider
    ) {

        val cameraProviderFuture = ProcessCameraProvider
            .getInstance(this.activity)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also { preview ->
                    preview.setSurfaceProvider(surface)
                }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(mode)
                .build()

            try {
                cameraProvider.apply {
                    unbindAll()
                    bindToLifecycle(
                        activity,
                        selector,
                        preview,
                        imageCapture
                    )
                }

            } catch (e: Exception) {
                activity.newToast("Use case binding failed $e")
            }
        }, mainExe)
    }

    private fun photoFile(extF: String) = File(
        outputDir,
        SimpleDateFormat(XCamera.FILENAME_FORMAT, Locale.getDefault())
            .format(System.currentTimeMillis()) + extF
    )

    fun takePhoto(
        @XCamera.Extension extF: String = XCamera.JPG,
        withError: ((Exception) -> Unit)? = null,
        withSuccessful: ((Uri) -> Unit)? = null
    ) {
        val imageCapture = imageCapture ?: return

        val photoFile = photoFile(extF)

        val outputOptions = ImageCapture
            .OutputFileOptions
            .Builder(photoFile)
            .build()

        imageCapture.takePicture(
            outputOptions,
            mainExe,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    path = output.savedUri ?: Uri.fromFile(photoFile)
                    withSuccessful?.let { run -> run(path) }
                }

                override fun onError(e: ImageCaptureException) {
                    withError?.let { run -> run(e) }
                }
            }
        )
    }

    private fun outputDirectory(): File {
        val mediaDir = activity.externalMediaDirs?.firstOrNull()?.let { file ->
            File(
                file,
                "pictures"
            ).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir
        else activity.filesDir!!
    }
}