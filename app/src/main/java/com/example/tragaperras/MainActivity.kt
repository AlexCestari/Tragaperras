package com.example.tragaperras

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tragaperras.vista.PartidaAdapter
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy

/** Pantalla de inicio: saldo actual y las mejores partidas, leídos de SQLite con RxJava. */
class MainActivity : BaseActivity() {

    private val adaptador = PartidaAdapter()
    private lateinit var textoSaldo: TextView
    private lateinit var textoSinPartidas: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configurarToolbar(R.string.titulo_inicio)

        textoSaldo = findViewById(R.id.texto_saldo)
        textoSinPartidas = findViewById(R.id.texto_sin_partidas)
        findViewById<RecyclerView>(R.id.lista_mejores).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = adaptador
        }

        findViewById<Button>(R.id.boton_jugar).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }
    }

    /** Se recarga cada vez que se vuelve a esta pantalla, por si se ha jugado mientras tanto. */
    override fun onResume() {
        super.onResume()
        repositorio.obtenerSaldo()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { textoSaldo.text = getString(R.string.saldo, it) },
                onError = ::mostrarError
            )
            .addTo(disposables)

        repositorio.obtenerMejores(LIMITE_MEJORES)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { mejores ->
                    adaptador.submitList(mejores)
                    textoSinPartidas.visibility = if (mejores.isEmpty()) View.VISIBLE else View.GONE
                },
                onError = ::mostrarError
            )
            .addTo(disposables)
    }

    companion object {
        private const val LIMITE_MEJORES = 5
    }
}
