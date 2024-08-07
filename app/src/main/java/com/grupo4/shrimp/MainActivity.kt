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
import java.sql.DriverManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginlayout)

        val button_login = findViewById<Button>(R.id.button_login)
        val cajaUsuario = findViewById<EditText>(R.id.CajaUsuario)
        val cajaPassword = findViewById<EditText>(R.id.CajaPassword)

        button_login.setOnClickListener {
            val usuario = cajaUsuario.text.toString()
            val password = cajaPassword.text.toString()
            lifecycleScope.launch {
                val existeUsuario = verificarUsuario(usuario, password)
                withContext(Dispatchers.Main) {
                    if (existeUsuario) {
                        Toast.makeText(this@MainActivity, "Usuario encontrado", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@MainActivity, HomeActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@MainActivity, "Usuario no encontrado", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        val button_register: Button = findViewById(R.id.button_register)
        button_register.setOnClickListener {
            // Redirigir a SecondActivity cuando se haga clic en el botón
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
    }

    private suspend fun verificarUsuario(usuario: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            val connection = MySqlConexion.getConexion()
            if (connection != null) {
                try {
                    val statement = connection.prepareStatement("SELECT * FROM Usuarios WHERE Correo = ? AND Password = ?")
                    statement.setString(1, usuario)
                    statement.setString(2, password)
                    val resultSet = statement.executeQuery()
                    resultSet.next()
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

    private class ExecuteQueryTask : AsyncTask<String, Void, Void>() {
        override fun doInBackground(vararg queries: String): Void? {
            val connection = MySqlConexion.getConexion()
            if (connection != null) {
                try {
                    val statement = connection.createStatement()
                    val resultSet = statement.executeQuery(queries[0])
                    while (resultSet.next()) {
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    connection.close()
                }
            }
            return null
        }
    }
    private suspend fun executeUpdate(query: String) {
        withContext(Dispatchers.IO) {
            val connection = MySqlConexion.getConexion()
            if (connection != null) {
                try {
                    val statement = connection.createStatement()
                    statement.executeUpdate(query)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    connection.close()
                }
            }
        }
    }
}