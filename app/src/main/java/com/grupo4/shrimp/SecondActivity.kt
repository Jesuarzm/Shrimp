package com.grupo4.shrimp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro)

        val regresarButton: Button = findViewById(R.id.button)
        regresarButton.setOnClickListener {
            // Redirigir a MainActivity (login layout) cuando se haga clic en el botón
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // Opcional: finalizar la actividad actual si no quieres que el usuario pueda volver con el botón de atrás
            finish()
        }
    }
}
