package com.grupo4.shrimp

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.grupo4.shrimp.data.dao.MySqlConexion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Types
import com.grupo4.shrimp.utils.UsuarioSingleton

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginlayout)

        val buttonLogin = findViewById<Button>(R.id.button_login)
        val cajaUsuario = findViewById<EditText>(R.id.CajaUsuario)
        val cajaPassword = findViewById<EditText>(R.id.CajaPassword)

        buttonLogin.setOnClickListener {
            val usuario = cajaUsuario.text.toString()
            val password = cajaPassword.text.toString()

            if (usuario.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@MainActivity, "Por favor ingrese su correo y contraseña", Toast.LENGTH_LONG).show()
            } else {
                lifecycleScope.launch {
                    val existeUsuario = verificarUsuario(usuario, password)
                    withContext(Dispatchers.Main) {
                        if (existeUsuario) {
                            // Asigna el usuario a la variable global
                            UsuarioSingleton.usuario = usuario

                            Toast.makeText(this@MainActivity, "Inicio sesion correcto", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@MainActivity, HomeActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@MainActivity, "Usuario o contraseña incorrecta", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        val buttonRegister: Button = findViewById(R.id.button_register)
        buttonRegister.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
    }

    private suspend fun verificarUsuario(usuario: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            val connection = MySqlConexion.getConexion()
            if (connection != null) {
                try {
                    val statement = connection.prepareCall("{CALL pr_VerificarUsuario(?, ?, ?)}")
                    statement.setString(1, usuario.toString())
                    statement.setString(2, password)
                    statement.registerOutParameter(3, Types.VARCHAR)

                    statement.execute()
                    val resultado = statement.getString(3)

                    when (resultado) {
                        "Usuario y contraseña correctos" -> true
                        "Contraseña incorrecta", "Usuario no existe" -> false
                        else -> false
                    }
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
