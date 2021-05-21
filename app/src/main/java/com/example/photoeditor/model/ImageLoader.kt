package com.example.photoeditor.model

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.photoeditor.LimitedEffectsActivity
import com.example.photoeditor.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ImageLoader {
    private val DEBUG_TAG = "PhotoEditor > ImageLoader"
    lateinit var currentPhotoPath: String
    private val REQUEST_CAMERA = 10
    private val REQUEST_GALLERY = 11

    fun loadImage(activity: Activity, type: String) {
        if (!checkPermissions(activity)) {
            showDialog(activity)
        } else {
            when (type) {
                "camera" -> takePicture(activity)
                "gallery" -> pickPicture(activity)
            }
        }
    }

    private fun checkPermissions(activity: Activity): Boolean {
        val writeAccess =
            (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)
        val readAccess =
            (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)

        return (readAccess && writeAccess)
    }

    private fun showDialog(activity: Activity) {
        MaterialAlertDialogBuilder(activity)
            .setTitle(activity.resources.getString(R.string.alert_title))
            .setMessage(activity.resources.getString(R.string.alert_message))
            .setPositiveButton(R.string.alert_positive) { _, _ ->
                request(activity)
            }
            .setNegativeButton(R.string.alert_negative) { _, _ ->
                val intent = Intent(activity, LimitedEffectsActivity::class.java)
                activity.startActivity(intent)
            }
            .setCancelable(true)
            .show()
    }

    private fun request(activity: Activity) {
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        ActivityCompat.requestPermissions(activity, permissions, 0)
    }

    private fun takePicture(activity: Activity) {
        try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val file = Tools.createFile()
            currentPhotoPath = file.absolutePath
            val pathToImage = FileProvider.getUriForFile(
                activity,
                "com.example.android.fileprovider",
                file
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, pathToImage)
            activity.startActivityForResult(intent, REQUEST_CAMERA)
        } catch (e: Exception) {
            Toast.makeText(activity, R.string.image_load_error_message, Toast.LENGTH_SHORT).show()
            Log.d(DEBUG_TAG, "$e")
        }
    }

    private fun pickPicture(activity: Activity) {
        try {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            activity.startActivityForResult(intent, REQUEST_GALLERY)
        } catch (e: Exception) {
            Toast.makeText(activity, R.string.image_load_error_message, Toast.LENGTH_SHORT).show()
            Log.d(DEBUG_TAG, "$e")
        }
    }
}