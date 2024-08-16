package com.grupo4.shrimp.fragments.cuenta

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
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

        val btnActualizar = findViewById<Button>(R.id.btnActualizar)

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido = findViewById<EditText>(R.id.etApellido)
        val etTelefono = findViewById<EditText>(R.id.etTelefono)
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etTipoUsuario = findViewById<EditText>(R.id.etTipoUsuario)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etIDDireccion = findViewById<EditText>(R.id.etIDDireccion)
        val correoUsuario = UsuarioSingleton.usuario

        lifecycleScope.launch {
            val usuario = correoUsuario?.let { obtenerUsuario(it) }
            if (usuario != null) {
                withContext(Dispatchers.Main) {
                    etNombre.setText(usuario.nombre)
                    etApellido.setText(usuario.apellido)
                    etTelefono.setText(usuario.telefono.toString())
                    etCorreo.setText(usuario.correo)
                    etTipoUsuario.setText(usuario.tipoUsuario)
                    etPassword.setText(usuario.password)
                    etIDDireccion.setText(usuario.idDireccion.toString())
                }
            }
        }

        btnActualizar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val telefonoStr = etTelefono.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val tipoUsuario = etTipoUsuario.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val idDireccionStr = etIDDireccion.text.toString().trim()

            if (nombre.isNotEmpty() && apellido.isNotEmpty() && telefonoStr.isNotEmpty() && correo.isNotEmpty() && tipoUsuario.isNotEmpty() && password.isNotEmpty() && idDireccionStr.isNotEmpty()) {
                val telefono = telefonoStr.toInt()
                val idDireccion = idDireccionStr.toInt()

                val usuario = Usuario(
                    idUsuario = 1, // Cambia esto según corresponda
                    nombre = nombre,
                    apellido = apellido,
                    telefono = telefono,
                    correo = correo,
                    tipoUsuario = tipoUsuario,
                    password = password,
                    idDireccion = idDireccion
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
                    val statement = connection.prepareStatement("SELECT IDUsuario, Nombre, Apellido, Telefono, Correo, TipoUsuario, Password, IDDireccion FROM Usuarios WHERE Correo = ?")
                    statement.setString(1, correo)
                    val resultSet = statement.executeQuery()
                    if (resultSet.next()) {
                        Usuario(
                            idUsuario = resultSet.getInt("IDUsuario"),
                            nombre = resultSet.getString("Nombre"),
                            apellido = resultSet.getString("Apellido"),
                            telefono = resultSet.getInt("Telefono"),
                            correo = resultSet.getString("Correo"),
                            tipoUsuario = resultSet.getString("TipoUsuario"),
                            password = resultSet.getString("Password"),
                            idDireccion = resultSet.getInt("IDDireccion")
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
        val tipoUsuario: String,
        val password: String,
        val idDireccion: Int
    )

    private suspend fun actualizarUsuario(usuario: Usuario): Boolean {
        return withContext(Dispatchers.IO) {
            val connection = MySqlConexion.getConexion()
            if (connection != null) {
                try {
                    val statement = connection.prepareCall("{CALL pr_actualizarUsuarios(?, ?, ?, ?, ?, ?)}")
                    statement.setInt(1, usuario.idUsuario)
                    statement.setString(2, usuario.nombre)
                    statement.setString(3, usuario.apellido)
                    statement.setInt(4, usuario.telefono)
                    statement.setString(5, usuario.correo)
                    statement.setString(6, usuario.tipoUsuario)
                    statement.setString(7, usuario.password)
                    statement.setInt(8, usuario.idDireccion)
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