package com.example.photoeditor.fragments

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.photoeditor.R
import kotlinx.android.synthetic.main.fragment_save.*

class SaveFragment : Fragment(R.layout.fragment_save) {
    companion object {
        val TAG = "SaveFragment"
        private const val KEY = "Image"
        fun newInstance(image: Parcelable) = SaveFragment().apply {
            arguments = bundleOf(Pair(KEY, image))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val receivedImage = arguments?.getParcelable<Parcelable>("Image")
//        val receivedImage = arguments?.getByteArray("Image")
        if (receivedImage != null) {
//            val image = BitmapFactory.decodeByteArray(receivedImage, 0, receivedImage.size)
//            resultImage.setImageBitmap(image)
            resultImage.setImageURI(receivedImage as Uri)
        }
    }

    fun rewriteImage(receivedImage: Parcelable?) {
        if (receivedImage != null) {
            resultImage.setImageURI(receivedImage as Uri)
        }
    }
}