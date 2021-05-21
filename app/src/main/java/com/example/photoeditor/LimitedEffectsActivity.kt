package com.example.photoeditor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_limited_effects.*

class LimitedEffectsActivity : AppCompatActivity() {
    companion object {
        private var layoutManager: RecyclerView.LayoutManager? = null
        private var adapter: RecyclerView.Adapter<LimitedRVAdapter.ViewHolder>? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_limited_effects)

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = LimitedRVAdapter(this)

        effectSectionHeader.setNavigationOnClickListener {
            this.finish()
        }
    }
}