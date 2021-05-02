package com.example.photoeditor.fragments

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoeditor.R
import kotlinx.android.synthetic.main.fragment_effects.*
import kotlinx.android.synthetic.main.fragment_image.*
import kotlinx.android.synthetic.main.recycler.*
import kotlinx.android.synthetic.main.recycler.recycler_view

class EffectsFragment : Fragment(R.layout.fragment_effects) {
    companion object {
        val TAG = "EffectsFragment"

        private const val KEY = "Image"

        fun newInstance(image: Parcelable?) = EffectsFragment().apply {
            arguments = bundleOf(Pair(KEY, image))
        }
    }

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RVAdapter.ViewHolder>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.recycler, container, false)
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        val image = arguments?.getParcelable<Parcelable>("Image")

        recycler_view.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            adapter = RVAdapter(image)

        }
    }



}