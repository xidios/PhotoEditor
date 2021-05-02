package com.example.photoeditor.fragments

import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.example.photoeditor.*


class RVAdapter(image: Parcelable?) : RecyclerView.Adapter<RVAdapter.ViewHolder>() {
    var image_ = image

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemKode: TextView
        var itemKategori: TextView
        var itemIsi: TextView

        init {
            itemKode = itemView.findViewById(R.id.kodePertanyaan)
            itemKategori = itemView.findViewById(R.id.kategori)
            itemIsi = itemView.findViewById(R.id.isiPertanyaan)


            itemView.setOnClickListener {
                var position: Int = getAdapterPosition()
                val context = itemView.context

                if (position == 0) {
                    val intent = Intent(context, RotationActivity::class.java).apply {
                        putExtra("NUMBER", position)
                        putExtra("CODE", itemKode.text)
                        putExtra("CATEGORY", itemKategori.text)
                        putExtra("CONTENT", itemIsi.text)
                        putExtra("Image", image_)
                    }
                    context.startActivity(intent)
                } else if (position == 1) {
                    val intent = Intent(context, ScalingActivity::class.java).apply {
                        putExtra("NUMBER", position)
                        putExtra("CODE", itemKode.text)
                        putExtra("CATEGORY", itemKategori.text)
                        putExtra("CONTENT", itemIsi.text)
                        putExtra("Image", image_)
                    }
                    context.startActivity(intent)
                } else if (position == 2) {
                    val intent = Intent(context, ColorCorrectionActivity::class.java).apply {
                        putExtra("NUMBER", position)
                        putExtra("CODE", itemKode.text)
                        putExtra("CATEGORY", itemKategori.text)
                        putExtra("CONTENT", itemIsi.text)
                        putExtra("Image", image_)
                    }
                    context.startActivity(intent)
                } else if (position == 3) {
                    val intent = Intent(context, SegmentationActivity::class.java).apply {
                        putExtra("NUMBER", position)
                        putExtra("CODE", itemKode.text)
                        putExtra("CATEGORY", itemKategori.text)
                        putExtra("CONTENT", itemIsi.text)
                        putExtra("Image", image_)
                    }
                    context.startActivity(intent)
                } else if (position == 4) {
                    val intent = Intent(context, SplinesActivity::class.java).apply {
                        putExtra("NUMBER", position)
                        putExtra("CODE", itemKode.text)
                        putExtra("CATEGORY", itemKategori.text)
                        putExtra("CONTENT", itemIsi.text)
                        putExtra("Image", image_)
                    }
                    context.startActivity(intent)
                } else if (position == 5) {
                    val intent = Intent(context, CubeActivity::class.java).apply {
                        putExtra("NUMBER", position)
                        putExtra("CODE", itemKode.text)
                        putExtra("CATEGORY", itemKategori.text)
                        putExtra("CONTENT", itemIsi.text)
                        putExtra("Image", image_)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.card, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        viewHolder.itemKode.text = kode[i]
        viewHolder.itemKategori.text = kategori[i]
        viewHolder.itemIsi.text = isi[i]

    }

    override fun getItemCount(): Int {
        return kode.size
    }

    private val kode = arrayOf(
        "Поворот",
        "Масштабирование", "Цветокоррекция", "Сегментация",
        "Сплайны", "Кубик"
    )

    private val kategori = arrayOf(
        "-", "-",
        "-", "-",
        "-", "-"
    )

    private val isi = arrayOf(
        "Поворот изображения на угол, кратный 90 градусам",
        "Изменение размера изображения",
        "Применение к изображению выбранного фильтра",
        "Распознавание лиц на изображении",
        "Преобразование ломаных линий в сплайны",
        "Вращение объемного игрального кубика"
    )
}

