package com.grupo4.shrimp.fragments.cuenta

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.grupo4.shrimp.R
import com.grupo4.shrimp.data.dao.MySqlConexion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetalleRegistroActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_registro)

        val idRegistro = intent.getIntExtra("IDRegistro", -1)

        val tvIDRegistroDetalle = findViewById<TextView>(R.id.tvIDRegistroDetalle)
        val tvFechaDetalle = findViewById<TextView>(R.id.tvFechaDetalle)
        val tvNombreServicioDetalle = findViewById<TextView>(R.id.tvNombreServicioDetalle)
        val tvDireccionDetalle = findViewById<TextView>(R.id.tvDireccionDetalle)
        val tvProveedorDetalle = findViewById<TextView>(R.id.tvProveedorDetalle)

        lifecycleScope.launch {
            val registro = obtenerDetalleRegistro(idRegistro)
            withContext(Dispatchers.Main) {
                tvIDRegistroDetalle.text = "IDRegistro: ${registro.id}"
                tvFechaDetalle.text = "Fecha: ${registro.fecha}"
                tvNombreServicioDetalle.text = "Nombre del Servicio: ${registro.nombreServicio}"
                tvDireccionDetalle.text = "Dirección: ${registro.direccion}"
                tvProveedorDetalle.text = "Proveedor: ${registro.proveedor}"
            }
        }
    }

    private suspend fun obtenerDetalleRegistro(idRegistro: Int): RegistroServicio {
        return withContext(Dispatchers.IO) {
            var registro = RegistroServicio(idRegistro, "", "", "", "")
            val connection = MySqlConexion.getConexion()
            if (connection != null) {
                try {
                    val statement = connection.prepareStatement(
                        """
                        SELECT 
                            rs.IDRegistro,
                            rs.Fecha,
                            s.NombreServicio,
                            CONCAT(d.Calle, ', ', d.Barrio, ', ', d.Ciudad, ', ', d.Provincia, ', ', d.Pais) AS Direccion,
                            CONCAT(p.Nombre, ' ', p.Apellido) AS Proveedor
                        FROM RegistroServicios rs
                        JOIN Servicios s ON rs.IDServicio = s.IDServicio
                        JOIN Direccion d ON rs.IDDireccion = d.IDDireccion
                        JOIN Usuarios p ON rs.IDProveedor = p.IDUsuario
                        WHERE rs.IDRegistro = ?
                        """
                    )
                    statement.setInt(1, idRegistro)
                    val resultSet = statement.executeQuery()
                    if (resultSet.next()) {
                        val id = resultSet.getInt("IDRegistro")
                        val fecha = resultSet.getString("Fecha")
                        val nombreServicio = resultSet.getString("NombreServicio")
                        val direccion = resultSet.getString("Direccion")
                        val proveedor = resultSet.getString("Proveedor")
                        registro = RegistroServicio(id, fecha, nombreServicio, direccion, proveedor)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    connection.close()
                }
            }
            registro
        }
    }
}
