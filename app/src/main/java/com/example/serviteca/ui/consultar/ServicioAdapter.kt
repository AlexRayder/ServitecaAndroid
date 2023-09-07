package com.example.serviteca.ui.consultar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.serviteca.R

class ServicioAdapter(private val servicioList: List<ServicioPrestado>) : RecyclerView.Adapter<ServicioAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val serpCliTextView: TextView = itemView.findViewById(R.id.serpCliTextView)
        val serpVehiTextView: TextView = itemView.findViewById(R.id.serpVehiTextView)
        val serpEstadoTextView: TextView = itemView.findViewById(R.id.serpEstadoTextView)
        val serpObservacionesTextView: TextView = itemView.findViewById(R.id.serpObservacionesTextView)
        val serpFechaServicioTextView: TextView = itemView.findViewById(R.id.serpFechaServicioTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_servicio, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val servicio = servicioList[position]

        holder.serpCliTextView.text = "SerpCli: ${servicio.serpCli}"
        holder.serpVehiTextView.text = "SerpVehi: ${servicio.serpVehi}"
        holder.serpEstadoTextView.text = "SerpEstado: ${servicio.serpEstado}"
        holder.serpObservacionesTextView.text = "SerpObservaciones: ${servicio.serpObservaciones}"
        holder.serpFechaServicioTextView.text = "SerpFechaServicio: ${servicio.serpFechaServicio}"
    }

    override fun getItemCount(): Int {
        return servicioList.size
    }
}
