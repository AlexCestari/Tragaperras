package com.example.tragaperras

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.tragaperras.modelo.Simbolo

/** Reglas y tabla de premios. La tabla se genera desde Simbolo, así siempre coincide con la lógica. */
class RulesActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)
        configurarToolbar(R.string.titulo_reglas)

        val tabla = findViewById<LinearLayout>(R.id.tabla_premios)
        val inflater = LayoutInflater.from(this)
        Simbolo.entries.forEach { simbolo ->
            val fila = inflater.inflate(R.layout.item_premio, tabla, false)
            fila.findViewById<ImageView>(R.id.imagen_simbolo).setImageResource(simbolo.imagenRes)
            fila.findViewById<TextView>(R.id.texto_premio).text =
                getString(R.string.premio_tres_iguales, simbolo.multiplicador)
            tabla.addView(fila)
        }
    }
}
