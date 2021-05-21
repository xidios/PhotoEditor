package com.example.photoeditor.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoeditor.R
import com.example.photoeditor.fragments.model.RVAdapter
import kotlinx.android.synthetic.main.fragment_effects.*

class EffectsFragment : Fragment(R.layout.fragment_effects) {
    companion object {
        private var layoutManager: RecyclerView.LayoutManager? = null
        private var adapter: RecyclerView.Adapter<RVAdapter.ViewHolder>? = null

        const val TAG = "EffectsFragment"
        private const val KEY = "Image"

        fun newInstance(image: Parcelable) = EffectsFragment().apply {
            arguments = bundleOf(Pair(KEY, image))
        }
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)

        val image = arguments?.getParcelable<Parcelable>("Image")
        if (image != null) {
            recycler_view.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = RVAdapter(activity, image)
            }
        }
    }

    fun rewriteImage(receivedImage: Parcelable?) {
        if (receivedImage != null) {
            recycler_view.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = RVAdapter(activity, receivedImage)
            }
        }
    }
}