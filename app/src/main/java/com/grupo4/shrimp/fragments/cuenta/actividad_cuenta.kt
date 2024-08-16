package com.grupo4.shrimp.fragments.cuenta

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grupo4.shrimp.R
import com.grupo4.shrimp.data.dao.MySqlConexion
import com.grupo4.shrimp.utils.UsuarioSingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class actividad_cuenta : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_servicios)

        val rvRegistroServicios = findViewById<RecyclerView>(R.id.rvRegistroServicios)
        rvRegistroServicios.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val registros = obtenerRegistros()
            val adapter = RegistroServiciosAdapter(registros) { registro ->
                val intent = Intent(this@actividad_cuenta, DetalleRegistroActivity::class.java).apply {
                    putExtra("IDRegistro", registro.id)
                    putExtra("Fecha", registro.fecha)
                }
                startActivity(intent)
            }
            rvRegistroServicios.adapter = adapter
        }
    }

    private suspend fun obtenerRegistros(): List<RegistroServicio> {
        return withContext(Dispatchers.IO) {
            val registros = mutableListOf<RegistroServicio>()
            val connection = MySqlConexion.getConexion()
            if (connection != null) {
                try {
                    val statement = connection.prepareStatement("SELECT IDRegistro, Fecha FROM RegistroServicios WHERE IDUsuario = ?")
                    UsuarioSingleton.usuario?.let { statement.setInt(1, it) }  // Usar el ID del usuario actual
                    val resultSet = statement.executeQuery()
                    while (resultSet.next()) {
                        val id = resultSet.getInt("IDRegistro")
                        val fecha = resultSet.getString("Fecha")
                        registros.add(RegistroServicio(id, fecha))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    connection.close()
                }
            }
            registros
        }
    }
}

data class RegistroServicio(val id: Int, val fecha: String)

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

class DetalleRegistroActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_registro)

        val idRegistro = intent.getIntExtra("IDRegistro", -1)

        val tvIDRegistroDetalle = findViewById<TextView>(R.id.tvIDRegistroDetalle)
        val tvFechaDetalle = findViewById<TextView>(R.id.tvFechaDetalle)

        lifecycleScope.launch {
            val registro = obtenerDetalleRegistro(idRegistro)
            withContext(Dispatchers.Main) {
                tvIDRegistroDetalle.text = "IDRegistro: ${registro.id}"
                tvFechaDetalle.text = "Fecha: ${registro.fecha}"

                // Agrega más TextViews y lógica para mostrar otros detalles del registro
            }
        }
    }

    private suspend fun obtenerDetalleRegistro(idRegistro: Int): RegistroServicio {
        return withContext(Dispatchers.IO) {
            var registro = RegistroServicio(idRegistro, "")
            val connection = MySqlConexion.getConexion()
            if (connection != null) {
                try {
                    val statement = connection.prepareStatement("SELECT IDRegistro, Fecha FROM RegistrosServicios WHERE IDRegistro = ?")
                    statement.setInt(1, idRegistro)
                    val resultSet = statement.executeQuery()
                    if (resultSet.next()) {
                        val id = resultSet.getInt("IDRegistro")
                        val fecha = resultSet.getString("Fecha")
                        registro = RegistroServicio(id, fecha)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    connection.close()
                }
            }
            registro
        }
    }
}