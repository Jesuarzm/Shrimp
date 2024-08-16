package com.grupo4.shrimp.fragments.cuenta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grupo4.shrimp.R

class RegistroServiciosAdapter(
    private val registros: List<RegistroServicio>,
    private val onClick: (RegistroServicio) -> Unit
) : RecyclerView.Adapter<RegistroServiciosAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIDRegistro: TextView = itemView.findViewById(R.id.tvIDRegistro)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)

        fun bind(registro: RegistroServicio) {
            tvIDRegistro.text = "IDRegistro: ${registro.id}"
            tvFecha.text = "Fecha: ${registro.fecha}"
            itemView.setOnClickListener { onClick(registro) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_registro_servicio, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(registros[position])
    }

    override fun getItemCount() = registros.size
}
