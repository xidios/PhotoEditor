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
import org.jetbrains.anko.toast

class EffectsFragment : Fragment(R.layout.fragment_effects) {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RVAdapter.ViewHolder>? = null

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = RVAdapter()
        }
    }

    companion object {
        val TAG = "EffectsFragment"
        fun newInstance() = EffectsFragment()
    }

}