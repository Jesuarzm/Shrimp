package com.grupo4.shrimp

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.grupo4.shrimp.data.dao.MySqlConexion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.DriverManager

class SecondActivity : AppCompatActivity() {

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


        button_registrarse.setOnClickListener {
            val nombre = editNombre.text.toString().trim()
            val apellido = editApellido.text.toString().trim()
            val telefonoStr = editTel.text.toString().trim()
            val correo = editCorreo.text.toString().trim()
            val cedulaStr = editCedula.text.toString().trim()
            val password = editPass.text.toString().trim()

            // Validar que los campos que se esperan como enteros no estén vacíos
            if (telefonoStr.isNotEmpty() && cedulaStr.isNotEmpty() && nombre.isNotEmpty() && apellido.isNotEmpty() && correo.isNotEmpty() && password.isNotEmpty()) {
                val telefono = telefonoStr.toInt()
                val cedula = cedulaStr.toInt()

                lifecycleScope.launch {
                    val registroUsuario = registrarUsuario(cedula, nombre, apellido, telefono, correo, password)
                    withContext(Dispatchers.Main) {
                        if (registroUsuario) {
                            Toast.makeText(this@SecondActivity, "Usuario Registrado con éxito!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this@SecondActivity, "Error de registro", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_LONG).show()
            }
        }

        val regresarButton: Button = findViewById(R.id.button)
        regresarButton.setOnClickListener {
            // Redirigir a MainActivity (login layout) cuando se haga clic en el botón
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // Opcional: finalizar la actividad actual si no quieres que el usuario pueda volver con el botón de atrás
            finish()
        }


    }

    private suspend fun registrarUsuario(id: Int, nombre: String, apellido: String, tel: Int, correo: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            val connection = MySqlConexion.getConexion()
            if (connection != null) {
                try {
                    val statement = connection.prepareStatement("INSERT INTO Usuarios (IDUsuario, " +
                            "Nombre, Apellido, Telefono, Correo, TipoUsuario, " +
                            "Password, IDDireccion) VALUES (?, ?, ?, ?, ?, 'Cliente', ?, 1)")
                    statement.setInt(1, id)
                    statement.setString(2, nombre)
                    statement.setString(3, apellido)
                    statement.setInt(4, tel)
                    statement.setString(5, correo)
                    statement.setString(6, password)

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
}
