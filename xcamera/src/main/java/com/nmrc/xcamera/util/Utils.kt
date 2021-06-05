package com.nmrc.xcamera.util

import android.app.Activity
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.IntDef
import androidx.annotation.StringDef
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.fragment.app.Fragment
import com.nmrc.xcamera.activity.ActivityXCamera
import com.nmrc.xcamera.fragment.FragmentXCamera

fun Fragment.xCamera() = FragmentXCamera(this)
fun ComponentActivity.xCamera() = ActivityXCamera(this)

fun Fragment.newToast(text: String) = Toast.makeText(this.requireContext(), text, Toast.LENGTH_SHORT).show()
fun Activity.newToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

object XCamera {

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(
        MAX_QUALITY,
        MAX_LATENCY
    )
    annotation class Mode

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(
        PNG,
        JPEG,
        JPG
    )
    annotation class Extension

    const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    const val PNG = ".png"
    const val JPEG = ".jpeg"
    const val JPG = ".jpg"
    const val MAX_QUALITY = ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
    const val MAX_LATENCY = ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
    val BACK = CameraSelector.DEFAULT_BACK_CAMERA
    val FRONT = CameraSelector.DEFAULT_FRONT_CAMERA
}