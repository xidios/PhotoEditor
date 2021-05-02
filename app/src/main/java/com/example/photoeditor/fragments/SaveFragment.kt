package com.example.photoeditor.fragments

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.example.photoeditor.R
import kotlinx.android.synthetic.main.fragment_image.*
import kotlinx.android.synthetic.main.fragment_image.currentImage
import kotlinx.android.synthetic.main.fragment_save.*

class SaveFragment : Fragment(R.layout.fragment_save) {
    companion object {
        val TAG = "SaveFragment"

        private const val KEY = "Image"

        fun newInstance(image: Parcelable?) = SaveFragment().apply {
            arguments = bundleOf(Pair(KEY, image))
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val image = arguments?.getParcelable<Parcelable>("Image")

    }
}