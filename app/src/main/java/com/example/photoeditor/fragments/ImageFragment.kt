package com.example.photoeditor.fragments

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.photoeditor.R
import android.os.Parcelable
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.os.bundleOf
import kotlinx.android.synthetic.main.fragment_image.*
import kotlinx.android.synthetic.main.fragment_save.*
import java.util.*

class ImageFragment : Fragment(R.layout.fragment_image) {
    companion object {
        val TAG = "ImageFragment"
        private const val KEY = "Image"
        fun newInstance(image: ByteArray) = ImageFragment().apply {
            arguments = bundleOf(Pair("Image", image))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val receivedImage = arguments?.getByteArray("Image")
        if (receivedImage != null) {
            val image = BitmapFactory.decodeByteArray(receivedImage, 0, receivedImage.size)
            currentImage.setImageBitmap(image)
        }
    }
}