package com.nmrc.xcamera.fragment

import android.annotation.SuppressLint
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.nmrc.xcamera.util.XCamera
import com.nmrc.xcamera.util.XCamera.FILENAME_FORMAT
import com.nmrc.xcamera.util.newToast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class FragmentXCamera(private val fragment: Fragment) {

    private var imageCapture: ImageCapture? = null
    private var cameraExecutor = Executors.newSingleThreadExecutor()
    private var outputDir: File
    private lateinit var path: Uri
    private val mainExe by lazy { ContextCompat.getMainExecutor(fragment.requireContext()) }

    init {
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)

                cameraExecutor.shutdown()
            }
        })

        outputDir = outputDirectory()
    }

    fun start(
        selector: CameraSelector,
        @XCamera.Mode mode: Int = XCamera.MAX_QUALITY,
        surface: Preview.SurfaceProvider
    ) {

        val cameraProviderFuture = ProcessCameraProvider
            .getInstance(fragment.requireContext())

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
                        fragment.viewLifecycleOwner,
                        selector,
                        preview,
                        imageCapture
                    )
                }

            } catch (e: Exception) {
                fragment.newToast("Use case binding failed $e")
            }
        }, mainExe)
    }

    private fun photoFile(extF: String) = File(
        outputDir,
        SimpleDateFormat(FILENAME_FORMAT, Locale.getDefault())
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

    @SuppressLint("UseRequireInsteadOfGet")
    private fun outputDirectory(): File {
        val mediaDir = fragment.activity?.externalMediaDirs?.firstOrNull()?.let { file ->
            File(
                file,
                "pictures"
            ).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir
        else fragment.activity?.filesDir!!
    }
}