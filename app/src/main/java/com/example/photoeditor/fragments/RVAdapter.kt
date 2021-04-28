package com.example.photoeditor.fragments

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.photoeditor.*


class RVAdapter : RecyclerView.Adapter<RVAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemKode: TextView = itemView.findViewById(R.id.kodePertanyaan)
        var itemKategori: TextView = itemView.findViewById(R.id.kategori)
        var itemIsi: TextView = itemView.findViewById(R.id.isiPertanyaan)

        init {
            itemView.setOnClickListener {
                val position: Int = getAdapterPosition()
                val context = itemView.context

                var intent = Intent()

                when (position) {
                    0 -> intent = Intent(context, RotationActivity::class.java)
                    1 -> intent = Intent(context, ScalingActivity::class.java)
                    2 -> intent = Intent(context, ColorCorrectionActivity::class.java)
                    3 -> intent = Intent(context, SegmentationActivity::class.java)
                    4 -> intent = Intent(context, SplinesActivity::class.java)
                    5 -> intent = Intent(context, CubeActivity::class.java)
                }
                intent = intent.apply {
                    putExtra("NUMBER", position)
                    putExtra("CODE", itemKode.text)
                    putExtra("CATEGORY", itemKategori.text)
                    putExtra("CONTENT", itemIsi.text)
                }
                context.startActivity(intent)
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

    override fun getItemCount() = kode.size

    private val kode = arrayOf(
        "Поворот",
        "Масштабирование",
        "Цветокоррекция",
        "Сегментация",
        "Сплайны",
        "Кубик")

    private val kategori = arrayOf(
        "Иконка",
        "Иконка",
        "Иконка",
        "Иконка",
        "Иконка",
        "Иконка")

    private val isi = arrayOf(
        "Поворот изображения на угол, кратный 90 градусам",
        "Изменение размера изображения",
        "Применение к изображению выбранного фильтра",
        "Распознавание лиц на изображении",
        "Преобразование ломаных линий в сплайны",
        "Вращение объемного игрального кубика")
}

