package com.example.photoeditor.fragments

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.photoeditor.R
import android.os.Parcelable
import android.content.Intent
import androidx.core.os.bundleOf
import kotlinx.android.synthetic.main.fragment_image.*
import java.util.*

class ImageFragment : Fragment(R.layout.fragment_image) {
    companion object {
        val TAG = "ImageFragment"
        private const val KEY = "Image"
        fun newInstance(image: Parcelable?) = ImageFragment().apply {
            arguments = bundleOf(Pair("Image", image))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val image = arguments?.getParcelable<Parcelable>("Image")
        currentImage.setImageURI(image as Uri)
    }
}