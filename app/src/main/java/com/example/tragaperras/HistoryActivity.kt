package com.example.tragaperras

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tragaperras.vista.PartidaAdapter
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy

/** Histórico completo de partidas, de la más reciente a la más antigua. */
class HistoryActivity : BaseActivity() {

    private val adaptador = PartidaAdapter()
    private lateinit var textoSinHistorial: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        configurarToolbar(R.string.titulo_historial)

        textoSinHistorial = findViewById(R.id.texto_sin_historial)
        findViewById<RecyclerView>(R.id.lista_historial).apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            addItemDecoration(DividerItemDecoration(this@HistoryActivity, DividerItemDecoration.VERTICAL))
            adapter = adaptador
        }
    }

    override fun onResume() {
        super.onResume()
        repositorio.obtenerHistorial()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { partidas ->
                    adaptador.submitList(partidas)
                    textoSinHistorial.visibility = if (partidas.isEmpty()) View.VISIBLE else View.GONE
                },
                onError = ::mostrarError
            )
            .addTo(disposables)
    }
}
