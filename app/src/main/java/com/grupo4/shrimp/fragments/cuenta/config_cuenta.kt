package com.grupo4.shrimp.fragments.cuenta

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.grupo4.shrimp.R
import com.grupo4.shrimp.data.dao.MySqlConexion
import com.grupo4.shrimp.utils.UsuarioSingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.sql.Types

class config_cuenta : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cuenta_config)

        val etNombreUsuario = findViewById<EditText>(R.id.etNombreUsuario)
        val spinnerTipoUsuario = findViewById<Spinner>(R.id.spinnerTipoUsuario)
        val spinnerServicio = findViewById<Spinner>(R.id.spinnerServicio)

        // Cargar el nombre del usuario
        CoroutineScope(Dispatchers.Main).launch {
            val nombreUsuario = UsuarioSingleton.usuario?.let { obtenerNombreUsuario(it) }
            etNombreUsuario.setText(nombreUsuario)
        }

        // Cargar los tipos de usuario en el spinner
        val tiposUsuario = listOf("Cliente", "Proveedor")  // Puedes cargar desde la base de datos si es necesario
        val adapterTipos = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposUsuario)
        adapterTipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoUsuario.adapter = adapterTipos

        // Cargar los servicios en el spinner
        CoroutineScope(Dispatchers.Main).launch {
            val listaServicios = obtenerListaServicios()
            val adapterServicios = ArrayAdapter(this@config_cuenta, android.R.layout.simple_spinner_item, listaServicios)
            adapterServicios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerServicio.adapter = adapterServicios
        }

        // Aquí puedes implementar la lógica del botón de Guardar
    }

    // Simulación de carga del nombre del usuario
    private suspend fun obtenerNombreUsuario(correo: String): String? {
        return withContext(Dispatchers.IO) {
            val connection = MySqlConexion.getConexion()
            if (connection != null) {
                try {
                    // Preparar la llamada al procedimiento almacenado
                    val statement = connection.prepareCall("{CALL obtenerNombreCompletoUsuarioPorCorreo(?, ?)}")
                    statement.setString(1, correo)
                    statement.registerOutParameter(2, Types.VARCHAR)
                    statement.execute()
                    val nombreUsuario = statement.getString(2)

                    nombreUsuario
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                } finally {
                    connection.close()
                }
            } else {
                null
            }
        }
    }

    // Simulación de carga de los servicios
    private suspend fun obtenerListaServicios(): List<String> {
        return withContext(Dispatchers.IO) {
            // Aquí iría tu lógica para obtener la lista de servicios desde la base de datos
            listOf("Servicio 1", "Servicio 2", "Servicio 3")  // Ejemplo
        }
    }
}
