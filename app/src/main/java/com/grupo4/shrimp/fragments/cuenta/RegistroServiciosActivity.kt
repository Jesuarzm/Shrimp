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
                    val statement = connection.prepareStatement("SELECT IDRegistro, Fecha FROM RegistroServicios, Usuarios WHERE IDCliente = IDUsuario and Correo = ?")
                    statement.setString(1, UsuarioSingleton.usuario)  // Usar el ID del usuario actual
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

