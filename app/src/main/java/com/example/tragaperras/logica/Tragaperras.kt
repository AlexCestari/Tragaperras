package com.example.tragaperras.logica

import com.example.tragaperras.modelo.Simbolo
import kotlin.random.Random

/**
 * Lógica del juego, independiente de la interfaz y de la base de datos.
 * Usa una única instancia de Random reutilizada en todas las tiradas.
 */
class Tragaperras(
    private val numRodillos: Int = 3,
    private val random: Random = Random.Default
) {
    private val simbolos = Simbolo.entries
    private val pesoTotal = simbolos.sumOf { it.peso }

    /** Devuelve un símbolo aleatorio por rodillo. */
    fun girar(): List<Simbolo> = List(numRodillos) { simboloAleatorio() }

    /**
     * Todos iguales: apuesta x multiplicador del símbolo.
     * Al menos dos iguales: apuesta x 2.
     * Ninguno igual: 0.
     */
    fun calcularPremio(resultado: List<Simbolo>, apuesta: Int): Int {
        val (simbolo, veces) = resultado.groupingBy { it }.eachCount()
            .maxBy { it.value }
            .toPair()
        return when {
            veces == numRodillos -> apuesta * simbolo.multiplicador
            veces >= 2 -> apuesta * 2
            else -> 0
        }
    }

    /** Selección ponderada: recorre los símbolos restando pesos hasta encontrar el que toca. */
    private fun simboloAleatorio(): Simbolo {
        var tirada = random.nextInt(pesoTotal)
        for (simbolo in simbolos) {
            if (tirada < simbolo.peso) return simbolo
            tirada -= simbolo.peso
        }
        return simbolos.last()
    }
}
