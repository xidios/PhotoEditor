package com.example.photoeditor.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.photoeditor.R

class SaveFragment : Fragment(R.layout.fragment_save) {
    companion object {
        val TAG = "SaveFragment"

        fun newInstance() = SaveFragment()
    }
}