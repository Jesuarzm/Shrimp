package com.grupo4.shrimp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.grupo4.shrimp.data.dao.MySqlConexion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SecondActivity : AppCompatActivity() {

    var selectedBarrioId: Int = 0
    val barrioMap = mutableMapOf<String, Int>() // Declara el mapa globalmente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro)

        val button_registrarse = findViewById<Button>(R.id.button_registrarse)

        val editNombre = findViewById<EditText>(R.id.edit_nombre)
        val editApellido = findViewById<EditText>(R.id.edit_apellido)
        val editTel = findViewById<EditText>(R.id.edit_telefono)
        val editCorreo = findViewById<EditText>(R.id.edit_correo)
        val editCedula = findViewById<EditText>(R.id.edit_cedula)
        val editPass = findViewById<EditText>(R.id.edit_password)
        val editDirection = findViewById<EditText>(R.id.edit_direccion)

        // Spinners
        val spinnerPais = findViewById<Spinner>(R.id.spinner_pais)
        val spinnerProvincia = findViewById<Spinner>(R.id.spinner_provincia)
        val spinnerCanton = findViewById<Spinner>(R.id.spinner_canton)
        val spinnerBarrio = findViewById<Spinner>(R.id.spinner_barrio)


        // Listener para cargar barrios cuando se seleccione un cantón
        spinnerCanton.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val cantonSeleccionado = spinnerCanton.selectedItem.toString()
                lifecycleScope.launch {
                    llenarSpinnerBarrio(spinnerBarrio, cantonSeleccionado)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Listener para barrio seleccionado
        spinnerBarrio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedBarrioName = parent.getItemAtPosition(position).toString()
                selectedBarrioId = barrioMap[selectedBarrioName] ?: 0 // Obtén el ID del barrio a partir del nombre
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        button_registrarse.setOnClickListener {
            val nombre = editNombre.text.toString().trim()
            val apellido = editApellido.text.toString().trim()
            val telefonoStr = editTel.text.toString().trim()
            val correo = editCorreo.text.toString().trim()
            val cedulaStr = editCedula.text.toString().trim()
            val password = editPass.text.toString().trim()
            val direction = editDirection.text.toString().trim()


            if (telefonoStr.isNotEmpty() && cedulaStr.isNotEmpty() && nombre.isNotEmpty() && apellido.isNotEmpty() && correo.isNotEmpty() && password.isNotEmpty()) {
                val telefono = telefonoStr.toInt()
                val cedula = cedulaStr.toInt()

                lifecycleScope.launch {
                    val usuarioExiste = verificarUsuarioExiste(correo)
                    withContext(Dispatchers.Main) {
                        if (usuarioExiste) {
                            Toast.makeText(this@SecondActivity, "El usuario ya existe", Toast.LENGTH_LONG).show()
                        } else {
                            val idDireccion = insertarDireccion(selectedBarrioId, direction)
                            if (idDireccion != null) {
                                val registroUsuario = registrarUsuario(cedula, nombre, apellido, telefono, correo, "Cliente", password, idDireccion)
                                if (registroUsuario) {
                                    Toast.makeText(this@SecondActivity, "Usuario Registrado con éxito!", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(this@SecondActivity, "Error de registro", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(this@SecondActivity, "Error al registrar la dirección", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_LONG).show()
            }
        }

        val regresarButton: Button = findViewById(R.id.button_regresar)
        regresarButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }



        // Llenar el spinner de Países al iniciar la actividad
        lifecycleScope.launch {
            llenarSpinnerPais(spinnerPais)
        }

        // Listener para cargar provincias cuando se seleccione un país
        spinnerPais.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val paisSeleccionado = spinnerPais.selectedItem.toString()
                lifecycleScope.launch {
                    llenarSpinnerProvincia(spinnerProvincia, paisSeleccionado)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Listener para cargar cantones cuando se seleccione una provincia
        spinnerProvincia.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val provinciaSeleccionada = spinnerProvincia.selectedItem.toString()
                lifecycleScope.launch {
                    llenarSpinnerCanton(spinnerCanton, provinciaSeleccionada)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Listener para cargar barrios cuando se seleccione un cantón
        spinnerCanton.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val cantonSeleccionado = spinnerCanton.selectedItem.toString()
                lifecycleScope.launch {
                    llenarSpinnerBarrio(spinnerBarrio, cantonSeleccionado)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Lógica para el botón de "Registrarse" permanece igual
    }

    // Función para llenar Spinner de País
    private suspend fun llenarSpinnerPais(spinnerPais: Spinner) {
        val paises = withContext(Dispatchers.IO) {
            val connection = MySqlConexion.getConexion()
            val listaPaises = mutableListOf<String>()
            if (connection != null) {
                try {
                    val query = "SELECT NombrePais FROM Pais"
                    val statement = connection.createStatement()
                    val resultSet = statement.executeQuery(query)
                    while (resultSet.next()) {
                        listaPaises.add(resultSet.getString("NombrePais"))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    connection.close()
                }
            }
            listaPaises
        }

        withContext(Dispatchers.Main) {
            val adapter = ArrayAdapter(this@SecondActivity, android.R.layout.simple_spinner_item, paises)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPais.adapter = adapter
        }
    }

    // Función para llenar Spinner de Provincia basado en el país seleccionado
    private suspend fun llenarSpinnerProvincia(spinnerProvincia: Spinner, pais: String) {
        val provincias = withContext(Dispatchers.IO) {
            val connection = MySqlConexion.getConexion()
            val listaProvincias = mutableListOf<String>()
            if (connection != null) {
                try {
                    val query = "SELECT NombreProvincia FROM Provincia p INNER JOIN Pais pa ON p.IDPais = pa.IDPais WHERE pa.NombrePais = ?"
                    val statement = connection.prepareStatement(query)
                    statement.setString(1, pais)
                    val resultSet = statement.executeQuery()
                    while (resultSet.next()) {
                        listaProvincias.add(resultSet.getString("NombreProvincia"))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    connection.close()
                }
            }
            listaProvincias
        }

        withContext(Dispatchers.Main) {
            val adapter = ArrayAdapter(this@SecondActivity, android.R.layout.simple_spinner_item, provincias)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerProvincia.adapter = adapter
        }
    }

    // Función para llenar Spinner de Cantón basado en la provincia seleccionada
    private suspend fun llenarSpinnerCanton(spinnerCanton: Spinner, provincia: String) {
        val cantones = withContext(Dispatchers.IO) {
            val connection = MySqlConexion.getConexion()
            val listaCantones = mutableListOf<String>()
            if (connection != null) {
                try {
                    val query = "SELECT NombreCanton FROM Canton c INNER JOIN Provincia p ON c.IDProvincia = p.IDProvincia WHERE p.NombreProvincia = ?"
                    val statement = connection.prepareStatement(query)
                    statement.setString(1, provincia)
                    val resultSet = statement.executeQuery()
                    while (resultSet.next()) {
                        listaCantones.add(resultSet.getString("NombreCanton"))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    connection.close()
                }
            }
            listaCantones
        }

        withContext(Dispatchers.Main) {
            val adapter = ArrayAdapter(this@SecondActivity, android.R.layout.simple_spinner_item, cantones)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCanton.adapter = adapter
        }
    }

    // Función para llenar Spinner de Barrio basado en el cantón seleccionado
    private suspend fun llenarSpinnerBarrio(spinnerBarrio: Spinner, canton: String) {
        val barrios = withContext(Dispatchers.IO) {
            val connection = MySqlConexion.getConexion()
            val listaBarrios = mutableListOf<String>()
            if (connection != null) {
                try {
                    val query = "SELECT IDBarrio, NombreBarrio FROM Barrio b INNER JOIN Canton c ON b.IDCanton = c.IDCanton WHERE c.NombreCanton = ?"
                    val statement = connection.prepareStatement(query)
                    statement.setString(1, canton)
                    val resultSet = statement.executeQuery()

                    // Limpia el mapa de barrios antes de volver a llenarlo
                    barrioMap.clear()

                    while (resultSet.next()) {
                        val nombreBarrio = resultSet.getString("NombreBarrio")
                        val idBarrio = resultSet.getInt("IDBarrio") // Asegúrate de que la consulta también recupere el ID del barrio
                        listaBarrios.add(nombreBarrio)
                        barrioMap[nombreBarrio] = idBarrio // Asigna el nombre del barrio como clave y el ID como valor
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    connection.close()
                }
            }
            listaBarrios
        }

        withContext(Dispatchers.Main) {
            val adapter = ArrayAdapter(this@SecondActivity, android.R.layout.simple_spinner_item, barrios)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerBarrio.adapter = adapter
        }
    }
    private suspend fun registrarUsuario(
        id: Int,
        nombre: String,
        apellido: String,
        tel: Int,
        correo: String,
        tipoUsuario: String,
        password: String,
        direccion: Int
    ): Boolean {
        return withContext(Dispatchers.IO) {
            val connection = MySqlConexion.getConexion()
            if (connection != null) {
                try {
                    val statement = connection.prepareCall("{CALL pr_insertarUsuarios(?, ?, ?, ?, ?, ?, ?, ?)}")
                    statement.setInt(1, id)
                    statement.setString(2, nombre)
                    statement.setString(3, apellido)
                    statement.setInt(4, tel)
                    statement.setString(5, correo)
                    statement.setString(6, tipoUsuario)
                    statement.setString(7, password)
                    statement.setInt(8, direccion)
                    val rowsInserted = statement.executeUpdate()
                    rowsInserted > 0
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                } finally {
                    connection.close()
                }
            } else {
                false
            }
        }
    }
    private suspend fun insertarDireccion(idBarrio: Int, detalleDireccion: String): Int? {
        return withContext(Dispatchers.IO) {
            val connection = MySqlConexion.getConexion()
            if (connection != null) {
                try {
                    val query = "INSERT INTO Direccion (IDBarrio, DetalleDireccion) VALUES (?, ?)"
                    val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)

                    // Asigna valores a la consulta
                    statement.setInt(1, idBarrio)
                    statement.setString(2, detalleDireccion)

                    val rowsInserted = statement.executeUpdate()

                    if (rowsInserted > 0) {
                        // Obtén el último ID insertado
                        val generatedKeys = statement.generatedKeys
                        if (generatedKeys.next()) {
                            generatedKeys.getInt(1) // Retorna el ID generado
                        } else {
                            null // No se obtuvo ID
                        }
                    } else {
                        null // La inserción falló
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null // Error en la inserción
                } finally {
                    connection.close()
                }
            } else {
                null // La conexión a la base de datos es nula
            }
        }
    }

    private suspend fun verificarUsuarioExiste(correo: String): Boolean {
        return withContext(Dispatchers.IO) {
            val connection = MySqlConexion.getConexion()
            if (connection != null) {
                try {
                    val statement = connection.prepareCall("{CALL pr_VerificarUsuarioExiste(?)}")
                    statement.setString(1, correo)
                    val resultSet = statement.executeQuery()
                    resultSet.next()
                    resultSet.getBoolean(1)
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                } finally {
                    connection.close()
                }
            } else {
                false
            }
        }
    }
}