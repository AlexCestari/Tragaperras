package com.example.tragaperras.modelo

/**
 * Una tirada guardada en la base de datos.
 * Al ser data class, Kotlin genera equals, hashCode, toString y copy automáticamente.
 */
data class Partida(
    val id: Long = 0,
    val fecha: Long,
    val apuesta: Int,
    val simbolos: List<Simbolo>,
    val premio: Int,
    val saldoFinal: Int
)
