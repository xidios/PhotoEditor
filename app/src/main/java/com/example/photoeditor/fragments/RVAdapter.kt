package com.example.photoeditor.fragments

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.photoeditor.HomeActivity
import com.example.photoeditor.R


class RVAdapter : RecyclerView.Adapter<RVAdapter.ViewHolder>() {
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
                val intent = Intent(context, HomeActivity::class.java).apply {
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

    override fun getItemCount(): Int {
        return kode.size
    }

    private val kode = arrayOf("Поворот",
        "Масштабирование", "Цветокоррекция", "Сегментация",
        "Сплайны", "Кубик")

    private val kategori = arrayOf("-", "-",
        "-", "-",
        "-", "-")

    private val isi = arrayOf("Поворот изображения на угол, кратный 90 градусам",
        "Изменение размера изображения", "Применение к изображению выбранного фильтра", "Распознавание лиц на изображении",
        "Преобразование ломаных линий в сплайны", "Вращение объемного игрального кубика")
}

