package com.grupo4.shrimp.fragments.servicios

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.grupo4.shrimp.R
import com.grupo4.shrimp.data.dao.MySqlConexion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaPersonasActivity : AppCompatActivity() {

    private lateinit var spinnerCalificacion: Spinner
    private lateinit var spinnerDistancia: Spinner
    private lateinit var spinnerServicios: Spinner
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layot_busqueda_servicios)

        // Inicializar los Spinners y ListView
        spinnerCalificacion = findViewById(R.id.spinner_calificacion)
        spinnerDistancia = findViewById(R.id.spinner_distancia)
        spinnerServicios = findViewById(R.id.spinner_servicios)
        listView = findViewById(R.id.lista_personas)

        // Llenar el spinner con los servicios desde la base de datos
        llenarSpinnerServicios()

        CoroutineScope(Dispatchers.Main).launch {
            val listaPersonas = obtenerListaPersonas()

            // Actualizar el ListView con los resultados
            val adapter = ArrayAdapter(
                this@ListaPersonasActivity,
                android.R.layout.simple_list_item_1,
                listaPersonas
            )
            listView.adapter = adapter
        }
    }


    private suspend fun obtenerListaPersonas(): List<String> {
        return withContext(Dispatchers.IO) {
            val connection = MySqlConexion.getConexion()
            val listaPersonas = mutableListOf<String>()
            if (connection != null) {
                try {
                    val query = """
                    SELECT Nombre, Apellido 
                    FROM Usuarios 
                    WHERE TipoUsuario = 'Proveedor'
                """
                    val statement = connection.prepareStatement(query)
                    val resultSet = statement.executeQuery()

                    while (resultSet.next()) {
                        val nombre = resultSet.getString("Nombre")
                        val apellido = resultSet.getString("Apellido")
                        listaPersonas.add("$nombre $apellido")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    connection.close()
                }
            }
            return@withContext listaPersonas
        }
    }
    private fun llenarSpinnerServicios() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val listaServicios = obtenerListaServicios()

                // Crear un adaptador para el spinner
                val adapter = ArrayAdapter(
                    this@ListaPersonasActivity,
                    android.R.layout.simple_spinner_item,
                    listaServicios
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerServicios.adapter = adapter
            } catch (e: Exception) {
                e.printStackTrace()
                // Agregar un mensaje de error o alerta
            }
        }
    }

    private suspend fun obtenerListaServicios(): List<String> {
        return withContext(Dispatchers.IO) {
            val connection = MySqlConexion.getConexion()
            val listaServicios = mutableListOf<String>()
            if (connection != null) {
                try {
                    val query = """
                    SELECT NombreServicio
                    FROM Servicios
                """
                    val statement = connection.prepareStatement(query)
                    val resultSet = statement.executeQuery()

                    while (resultSet.next()) {
                        val servicio = resultSet.getString("NombreServicio")
                        listaServicios.add(servicio)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Agregar un mensaje de error o alerta
                } finally {
                    connection.close()
                }
            } else {
                // Manejar el caso en que la conexión es null
                println("Error: La conexión a la base de datos es null.")
            }
            return@withContext listaServicios
        }
    }
}
