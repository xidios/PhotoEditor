package com.example.photoeditor.fragments.model

import android.content.Intent
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.photoeditor.CubeActivity
import com.example.photoeditor.R
import com.example.photoeditor.ScalingActivity
import com.example.photoeditor.filters.ColorCorrectionActivity
import com.example.photoeditor.interpolation.Interpolation
import com.example.photoeditor.retouching.RetouchingActivity
import com.example.photoeditor.rotation.RotationActivity
//import com.example.photoeditor.scaling.ScalingActivity
import com.example.photoeditor.segmentation.SegmentationActivity
import com.example.photoeditor.splines.SplinesActivity
import com.example.photoeditor.unsharp.UnsharpMaskActivity

class RVAdapter(val context: FragmentActivity?, val image: Parcelable) :
    RecyclerView.Adapter<RVAdapter.ViewHolder>() {
    val REQUEST_ID = 1
    val KEY = "Image"

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
                    0 -> intent = Intent(context, RotationActivity::class.java)
                    1 -> intent = Intent(context, ScalingActivity::class.java)
                    2 -> intent = Intent(context, ColorCorrectionActivity::class.java)
                    3 -> intent = Intent(context, SegmentationActivity::class.java)
                    4 -> intent = Intent(context, SplinesActivity::class.java)
                    5 -> intent = Intent(context, CubeActivity::class.java)
                    6 -> intent = Intent(context, UnsharpMaskActivity::class.java)
                    7 -> intent = Intent(context, RetouchingActivity::class.java)
                    8 -> intent = Intent(context, Interpolation::class.java)
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
        viewHolder.bonusLabel.setVisibility(taskBonus[i])
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
        R.string.retouch_task_name,
        R.string.interpolation
    )

    private val taskIcon = arrayOf(
        R.drawable.ic_rotate,
        R.drawable.ic_scaling,
        R.drawable.ic_filters,
        R.drawable.ic_segmentation,
        R.drawable.ic_splines,
        R.drawable.ic_cube,
        R.drawable.ic_unsharp,
        R.drawable.ic_retouch,
        R.drawable.ic_camera
    )

    private val taskDescription = arrayOf(
        R.string.rotation_task_desc,
        R.string.scaling_task_desc,
        R.string.color_correction_task_desc,
        R.string.segmentation_task_desc,
        R.string.splines_task_desc,
        R.string.cube_task_desc,
        R.string.unsharp_mask_task_desc,
        R.string.retouch_task_desc,
        R.string.interpolaion_text
    )

    private val taskBonus = arrayOf(
        View.VISIBLE,
        View.INVISIBLE,
        View.VISIBLE,
        View.INVISIBLE,
        View.VISIBLE,
        View.INVISIBLE,
        View.INVISIBLE,
        View.VISIBLE,
        View.INVISIBLE
    )
}
