package com.example.photoeditor.model

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.photoeditor.CubeActivity
import com.example.photoeditor.R
import com.example.photoeditor.splines.SplinesActivity

class LimitedRVAdapter(val context: AppCompatActivity?) :
    RecyclerView.Adapter<LimitedRVAdapter.ViewHolder>() {
    val REQUEST_ID = 1

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var taskName: TextView = itemView.findViewById(R.id.task_name)
        var taskIcon: ImageView = itemView.findViewById(R.id.task_icon)
        var taskDescription: TextView = itemView.findViewById(R.id.task_description)
        var bonusLabel: View = itemView.findViewById(R.id.bonus_label)

        init {
            itemView.setOnClickListener {
                val position: Int = adapterPosition
                var intent = Intent()

                when (position) {
                    0 -> intent = Intent(context, SplinesActivity::class.java)
                    1 -> intent = Intent(context, CubeActivity::class.java)
                }
                context?.startActivityForResult(intent, REQUEST_ID)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.card, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.taskName.setText(taskName[i])
        viewHolder.taskIcon.setImageResource(taskIcon[i])
        viewHolder.taskDescription.setText(taskDescription[i])
        viewHolder.bonusLabel.setVisibility(taskBonus[i])
    }

    override fun getItemCount() = taskName.size

    private val taskName = arrayOf(
        R.string.splines_task_name,
        R.string.cube_task_name
    )

    private val taskIcon = arrayOf(
        R.drawable.ic_splines,
        R.drawable.ic_cube
    )

    private val taskDescription = arrayOf(
        R.string.splines_task_desc,
        R.string.cube_task_desc
    )

    private val taskBonus = arrayOf(
        View.VISIBLE,
        View.INVISIBLE,
    )
}
