package com.example.photoeditor.fragments

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.photoeditor.R
import kotlinx.android.synthetic.main.fragment_image.*

class ImageFragment : Fragment(R.layout.fragment_image) {
    companion object {
        const val TAG = "ImageFragment"
        private const val KEY = "Image"
        fun newInstance(image: Parcelable) = ImageFragment().apply {
            arguments = bundleOf(Pair(KEY, image))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val receivedImage = arguments?.getParcelable<Parcelable>("Image")
        if (receivedImage != null) {
            currentImage.setImageURI(receivedImage as Uri)
        }
    }
}