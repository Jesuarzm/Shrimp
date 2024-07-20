package com.grupo4.shrimp

import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
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

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            lifecycleScope.launch {
                executeUpdate("INSERT INTO Servicios (IDServicio, NombreServicio, IDTipoServicio) VALUES ('1234', 'qwer', '3')")
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