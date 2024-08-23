package com.grupo4.shrimp.fragments.cuenta

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.grupo4.shrimp.R
import com.grupo4.shrimp.data.dao.MySqlConexion
import com.grupo4.shrimp.utils.UsuarioSingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class admin_cuenta : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cuenta_layout)

        // Referencia a los campos del XML
        val btnActualizar = findViewById<Button>(R.id.btnActualizar)
        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido = findViewById<EditText>(R.id.etApellido)
        val etTelefono = findViewById<EditText>(R.id.etTelefono)
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etPais = findViewById<EditText>(R.id.etPais)
        val etProvincia = findViewById<EditText>(R.id.etProvincia)
        val etCiudad = findViewById<EditText>(R.id.etCiudad)
        val etDireccion = findViewById<EditText>(R.id.etDireccion)
        val correoUsuario = UsuarioSingleton.usuario

        // Obtener información del usuario
        lifecycleScope.launch {
            val usuario = correoUsuario?.let { obtenerUsuario(it) }
            if (usuario != null) {
                withContext(Dispatchers.Main) {
                    etNombre.setText(usuario.nombre)
                    etApellido.setText(usuario.apellido)
                    etTelefono.setText(usuario.telefono.toString())
                    etCorreo.setText(usuario.correo)
                    etPassword.setText(usuario.password)
                    etPais.setText(usuario.pais)
                    etProvincia.setText(usuario.provincia)
                    etCiudad.setText(usuario.ciudad)
                    etDireccion.setText(usuario.direccion)
                }
            }
        }

        // Acción al hacer clic en el botón de actualizar
        btnActualizar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val telefonoStr = etTelefono.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val pais = etPais.text.toString().trim()
            val provincia = etProvincia.text.toString().trim()
            val ciudad = etCiudad.text.toString().trim()
            val direccion = etDireccion.text.toString().trim()

            if (nombre.isNotEmpty() && apellido.isNotEmpty() && telefonoStr.isNotEmpty() && correo.isNotEmpty() &&
                password.isNotEmpty() && pais.isNotEmpty() && provincia.isNotEmpty() && ciudad.isNotEmpty() &&
                direccion.isNotEmpty()) {

                val telefono = telefonoStr.toInt()

                val usuario = Usuario(
                    idUsuario = 1,  // Cambia esto según corresponda
                    nombre = nombre,
                    apellido = apellido,
                    telefono = telefono,
                    correo = correo,
                    password = password,
                    pais = pais,
                    provincia = provincia,
                    ciudad = ciudad,
                    direccion = direccion
                )

                lifecycleScope.launch {
                    val actualizado = actualizarUsuario(usuario)
                    withContext(Dispatchers.Main) {
                        if (actualizado) {
                            Toast.makeText(this@admin_cuenta, "Usuario Actualizado con éxito!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this@admin_cuenta, "Error al actualizar el usuario", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun obtenerUsuario(correo: String): Usuario? {
        return withContext(Dispatchers.IO) {
            val connection = MySqlConexion.getConexion()
            if (connection != null) {
                try {
                    val statement = connection.prepareCall("{CALL pr_ObtenerUsuarioPorCorreo(?)}")
                    statement.setString(1, correo)
                    val resultSet = statement.executeQuery()
                    if (resultSet.next()) {
                        Usuario(
                            idUsuario = resultSet.getInt("IDUsuario"),
                            nombre = resultSet.getString("Nombre"),
                            apellido = resultSet.getString("Apellido"),
                            telefono = resultSet.getInt("Telefono"),
                            correo = resultSet.getString("Correo"),
                            password = resultSet.getString("Password"),
                            pais = resultSet.getString("NombrePais"),
                            provincia = resultSet.getString("NombreProvincia"),
                            ciudad = resultSet.getString("NombreCanton"),
                            direccion = resultSet.getString("DetalleDireccion")
                        )
                    } else {
                        null
                    }
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

    data class Usuario(
        val idUsuario: Int,
        val nombre: String,
        val apellido: String,
        val telefono: Int,
        val correo: String,
        val password: String,
        val pais: String,
        val provincia: String,
        val ciudad: String,
        val direccion: String
    )

    private suspend fun actualizarUsuario(usuario: Usuario): Boolean {
        return withContext(Dispatchers.IO) {
            val connection = MySqlConexion.getConexion()
            if (connection != null) {
                try {
                    // Llamamos a un procedimiento almacenado para actualizar la información del usuario y la dirección
                    val statement = connection.prepareCall("{CALL pr_actualizarUsuarioYDireccion(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")

                    // Parámetros para la tabla Usuarios
                    statement.setInt(1, usuario.idUsuario)
                    statement.setString(2, usuario.nombre)
                    statement.setString(3, usuario.apellido)
                    statement.setInt(4, usuario.telefono)
                    statement.setString(5, usuario.correo)
                    statement.setString(6, usuario.password)

                    // Parámetros para la dirección
                    statement.setString(7, usuario.pais)
                    statement.setString(8, usuario.provincia)
                    statement.setString(9, usuario.ciudad)
                    statement.setString(10, usuario.direccion)

                    // Ejecutamos la actualización y verificamos si se actualizó al menos una fila
                    statement.executeUpdate() > 0
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
