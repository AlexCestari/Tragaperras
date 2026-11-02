package com.example.tragaperras

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.tragaperras.logica.Tragaperras
import com.example.tragaperras.modelo.Jugador
import com.example.tragaperras.modelo.Partida
import com.example.tragaperras.modelo.Simbolo
import com.example.tragaperras.vista.RodilloView
import com.google.android.material.button.MaterialButtonToggleGroup
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy

/** Pantalla de juego. El saldo y cada partida se guardan en SQLite con RxJava. */
class GameActivity : BaseActivity() {

    private val tragaperras = Tragaperras()
    private var jugador = Jugador()

    private lateinit var rodillos: List<RodilloView>
    private lateinit var textoSaldo: TextView
    private lateinit var textoResultado: TextView
    private lateinit var grupoApuesta: MaterialButtonToggleGroup
    private lateinit var botonGirar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        configurarToolbar(R.string.titulo_jugar)

        rodillos = listOf(
            findViewById(R.id.rodillo_1),
            findViewById(R.id.rodillo_2),
            findViewById(R.id.rodillo_3)
        )
        textoSaldo = findViewById(R.id.texto_saldo)
        textoResultado = findViewById(R.id.texto_resultado)
        grupoApuesta = findViewById(R.id.grupo_apuesta)
        botonGirar = findViewById(R.id.boton_girar)

        rodillos[0].mostrar(Simbolo.CEREZA)
        rodillos[1].mostrar(Simbolo.SIETE)
        rodillos[2].mostrar(Simbolo.DIAMANTE)

        botonGirar.setOnClickListener { girar() }

        // Hasta que no se lee el saldo guardado, no se puede jugar.
        bloquearControles(true)
        repositorio.obtenerSaldo()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { monedas ->
                    jugador = Jugador(monedas)
                    actualizarSaldo()
                    bloquearControles(false)
                },
                onError = ::mostrarError
            )
            .addTo(disposables)
    }

    private fun apuestaSeleccionada(): Int = when (grupoApuesta.checkedButtonId) {
        R.id.apuesta_5 -> 5
        R.id.apuesta_10 -> 10
        else -> 1
    }

    private fun girar() {
        if (jugador.monedas == 0) {
            jugador.reiniciar()
            repositorio.guardarSaldo(jugador.monedas)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onError = ::mostrarError)
                .addTo(disposables)
            textoResultado.setText(R.string.saldo_reiniciado)
            actualizarSaldo()
            return
        }

        val apuesta = apuestaSeleccionada()
        if (!jugador.puedeApostar(apuesta)) {
            textoResultado.setText(R.string.saldo_insuficiente)
            return
        }

        val resultado = tragaperras.girar()
        val premio = tragaperras.calcularPremio(resultado, apuesta)
        jugador.aplicarResultado(apuesta, premio)

        // Se guarda en segundo plano mientras giran los rodillos; la interfaz no espera.
        val partida = Partida(
            fecha = System.currentTimeMillis(),
            apuesta = apuesta,
            simbolos = resultado,
            premio = premio,
            saldoFinal = jugador.monedas
        )
        repositorio.guardarPartida(partida)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = ::mostrarError)
            .addTo(disposables)

        // Mientras giran, no se puede volver a tirar ni cambiar la apuesta.
        bloquearControles(true)
        textoResultado.text = ""

        // Cada rodillo gira un poco más que el anterior, así se paran de izquierda a derecha.
        resultado.forEachIndexed { i, simbolo ->
            rodillos[i].animarHasta(
                destino = simbolo,
                duracionMs = 1200L + i * 500L,
                vueltas = 3 + i
            ) {
                if (i == rodillos.lastIndex) mostrarResultado(apuesta, premio)
            }
        }
    }

    /** Se llama cuando se para el último rodillo. */
    private fun mostrarResultado(apuesta: Int, premio: Int) {
        textoResultado.text = if (premio > 0) {
            getString(R.string.resultado_premio, premio)
        } else {
            getString(R.string.resultado_sin_premio, apuesta)
        }
        actualizarSaldo()
        bloquearControles(false)
    }

    private fun bloquearControles(bloquear: Boolean) {
        botonGirar.isEnabled = !bloquear
        for (i in 0 until grupoApuesta.childCount) {
            grupoApuesta.getChildAt(i).isEnabled = !bloquear
        }
    }

    private fun actualizarSaldo() {
        textoSaldo.text = getString(R.string.saldo, jugador.monedas)
        botonGirar.setText(if (jugador.monedas == 0) R.string.boton_reiniciar else R.string.boton_girar)
    }
}
