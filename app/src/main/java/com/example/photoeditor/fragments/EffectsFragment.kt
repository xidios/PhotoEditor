package com.example.photoeditor.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.photoeditor.R

class EffectsFragment : Fragment(R.layout.fragment_effects) {
    companion object {
        val TAG = "EffectsFragment"

        fun newInstance() = EffectsFragment()
    }
}