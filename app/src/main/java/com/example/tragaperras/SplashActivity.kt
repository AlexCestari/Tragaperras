package com.example.tragaperras

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

/** Pantalla de bienvenida: el jugador entra al juego pulsando INSERT COIN, como en una recreativa. */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        findViewById<Button>(R.id.boton_insert_coin).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            // Se cierra la bienvenida para que el botón Atrás no vuelva a ella.
            finish()
        }
    }
}
