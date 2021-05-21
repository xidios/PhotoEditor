package com.example.photoeditor.fragments

import android.content.Intent
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.photoeditor.*
import com.example.photoeditor.effect_activities.RotationActivity
import com.example.photoeditor.effect_activities.UnsharpMaskActivity

class RVAdapter(val context: FragmentActivity?, val image: Parcelable) :
    RecyclerView.Adapter<RVAdapter.ViewHolder>() {
    val REQUEST_ID = 1
    val KEY = "Image"

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var taskName: TextView = itemView.findViewById(R.id.task_name)
        var taskIcon: ImageView = itemView.findViewById(R.id.task_icon)
        var taskDescription: TextView = itemView.findViewById(R.id.task_description)

        init {
            itemView.setOnClickListener {
                val position: Int = adapterPosition
                var intent = Intent()

                when (position) {
                    0 -> intent = Intent(context, RotationActivity::class.java)
                    1 -> intent = Intent(context, ScalingActivity::class.java)
                    2 -> intent = Intent(context, ColorCorrectionActivity::class.java)
                    3 -> intent = Intent(context, SegmentationActivity::class.java)
                    4 -> intent = Intent(context, SplinesActivity::class.java)
                    5 -> intent = Intent(context, CubeActivity::class.java)
                    6 -> intent = Intent(context, UnsharpMaskActivity::class.java)
                    7 -> intent = Intent(context, RetouchingActivity::class.java)
                }
                intent = intent.apply {
                    putExtra(KEY, image)
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
    }

    override fun getItemCount() = taskName.size

    private val taskName = arrayOf(
        R.string.rotation_task_name,
        R.string.scaling_task_name,
        R.string.color_correction_task_name,
        R.string.segmentation_task_name,
        R.string.splines_task_name,
        R.string.cube_task_name,
        R.string.unsharp_mask_task_name,
        R.string.retouch_task_name
    )

    private val taskIcon = arrayOf(
        R.drawable.download,
        R.drawable.image,
        R.drawable.download,
        R.drawable.download,
        R.drawable.download,
        R.drawable.download,
        R.drawable.download,
        R.drawable.download
    )

    private val taskDescription = arrayOf(
        R.string.rotation_task_desc,
        R.string.scaling_task_desc,
        R.string.color_correction_task_desc,
        R.string.segmentation_task_desc,
        R.string.splines_task_desc,
        R.string.cube_task_desc,
        R.string.unsharp_mask_task_desc,
        R.string.retouch_task_desc
    )
}
