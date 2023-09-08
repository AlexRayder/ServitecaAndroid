package com.example.serviteca.ui.consultar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.serviteca.R

class ServicioAdapter(
    private val serviciosList: MutableList<ServicioPrestado>,
    private val onItemClick: (ServicioPrestado) -> Unit
) : RecyclerView.Adapter<ServicioAdapter.ServicioViewHolder>() {

    inner class ServicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val servicioTextView: TextView = itemView.findViewById(R.id.servicioTextView)
        val fechaTextView: TextView = itemView.findViewById(R.id.fechaTextView)
        val estadoTextView: TextView = itemView.findViewById(R.id.estadoTextView)
        val totalTextView: TextView = itemView.findViewById(R.id.totalTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicioViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_servicio, parent, false)
        return ServicioViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ServicioViewHolder, position: Int) {
        val currentItem = serviciosList[position]
        holder.servicioTextView.text = currentItem.serpCli.toString()
        holder.fechaTextView.text = currentItem.serpFechaServicio
        holder.estadoTextView.text = currentItem.serpEstado
        holder.totalTextView.text = currentItem.serpObservaciones.toString()

        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
    }

    override fun getItemCount() = serviciosList.size
}
