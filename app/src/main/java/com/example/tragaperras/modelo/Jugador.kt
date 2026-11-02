package com.example.tragaperras.modelo

/** Saldo de monedas del jugador y las operaciones que lo modifican. */
class Jugador(monedas: Int = MONEDAS_INICIALES) {

    var monedas: Int = monedas
        private set

    fun puedeApostar(apuesta: Int): Boolean = apuesta in 1..monedas

    /** Resta la apuesta y suma el premio (0 si no hay premio). */
    fun aplicarResultado(apuesta: Int, premio: Int) {
        monedas = monedas - apuesta + premio
    }

    fun reiniciar() {
        monedas = MONEDAS_INICIALES
    }

    companion object {
        const val MONEDAS_INICIALES = 100
    }
}
