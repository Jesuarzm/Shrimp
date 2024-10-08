package com.grupo4.shrimp.fragments.cuenta

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.grupo4.shrimp.R
import com.grupo4.shrimp.data.dao.MySqlConexion
import com.grupo4.shrimp.utils.UsuarioSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Types

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [nav_cuenta.newInstance] factory method to
 * create an instance of this fragment.
 */
class nav_cuenta : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textViewM = view.findViewById<TextView>(R.id.textView_user_name)
        val correo = UsuarioSingleton.usuario // Usa el valor de UsuarioSingleton

        // Llamar a la función obtenerNombreUsuario de manera asíncrona
        CoroutineScope(Dispatchers.Main).launch {
            val nombreUsuario = correo?.let { obtenerNombreUsuario(it) }
            textViewM.text = nombreUsuario ?: "Nombre no disponible"
        }

        val btnConfiguracion = view.findViewById<Button>(R.id.button_settings)
        btnConfiguracion.setOnClickListener {
            val intent = Intent(activity, config_cuenta::class.java)
            startActivity(intent)
        }

        val btnCuenta = view.findViewById<Button>(R.id.button_account_management)
        btnCuenta.setOnClickListener {
            val intent = Intent(activity, admin_cuenta::class.java)
            startActivity(intent)
        }
        val btnRegistro = view.findViewById<Button>(R.id.bottom_actividad)
        btnRegistro.setOnClickListener {
            val intent = Intent(activity, RegistroServiciosActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nav_cuenta, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment nav_cuenta.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            nav_cuenta().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private suspend fun obtenerNombreUsuario(correo: String): String? {
        return withContext(Dispatchers.IO) {
            val connection = MySqlConexion.getConexion()
            if (connection != null) {
                try {
                    // Preparar la llamada al procedimiento almacenado
                    val statement = connection.prepareCall("{CALL obtenerNombreCompletoUsuarioPorCorreo(?, ?)}")
                    statement.setString(1, correo)
                    statement.registerOutParameter(2, Types.VARCHAR)
                    statement.execute()
                    val nombreUsuario = statement.getString(2)

                    nombreUsuario
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

}