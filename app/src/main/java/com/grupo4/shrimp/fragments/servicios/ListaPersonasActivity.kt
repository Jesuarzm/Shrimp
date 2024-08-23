package com.grupo4.shrimp.fragments.servicios

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.grupo4.shrimp.R

class ListaPersonasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layot_busqueda_servicios)

        // Obtener el tipo de servicio seleccionado desde el intent
        val tipoServicio = intent.getStringExtra("tipo_servicio")

        // Inicializar la lista de personas (esto es solo un ejemplo)
        val listaPersonas = obtenerListaPersonas(tipoServicio)

        // Configurar el adaptador para el ListView
        val listView = findViewById<ListView>(R.id.lista_personas)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaPersonas)
        listView.adapter = adapter

        // Configurar la lógica de filtros según sea necesario
    }

    private fun obtenerListaPersonas(tipoServicio: String?): List<String> {
        // Aquí deberías realizar la lógica para obtener la lista filtrada de personas desde tu base de datos
        return listOf("Persona 1", "Persona 2", "Persona 3") // Ejemplo de lista
    }
}
