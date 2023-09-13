package com.example.serviteca.ui.historial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.serviteca.R
import com.example.serviteca.ui.consultar.ServicioPrestado

class HistorialAdapter(
    private val serviciosList: MutableList<ServicioPrestado>,
    private val onItemClick: (ServicioPrestado) -> Unit
) : RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    inner class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fechaTextView: TextView = itemView.findViewById(R.id.fechaTextView)
        val estadoTextView: TextView = itemView.findViewById(R.id.estadoTextView)
        val totalTextView: TextView = itemView.findViewById(R.id.totalTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return HistorialViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val currentItem = serviciosList[position]

        holder.fechaTextView.text = currentItem.serpFechaServicio
        holder.estadoTextView.text = currentItem.serpEstado
        holder.totalTextView.text = currentItem.serpObservaciones.toString()

        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
    }

    override fun getItemCount() = serviciosList.size
}
