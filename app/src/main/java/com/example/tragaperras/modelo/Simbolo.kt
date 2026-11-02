package com.example.tragaperras.modelo

import androidx.annotation.DrawableRes
import com.example.tragaperras.R

/**
 * Símbolos de los rodillos. Cada uno agrupa su imagen, el multiplicador
 * que paga con tres iguales y su peso de probabilidad (más peso = sale más).
 * Para cambiar la dificultad basta con ajustar los pesos o añadir símbolos.
 */
enum class Simbolo(
    @DrawableRes val imagenRes: Int,
    val multiplicador: Int,
    val peso: Int
) {
    CEREZA(R.drawable.simbolo_cereza, 5, 30),
    LIMON(R.drawable.simbolo_limon, 8, 25),
    CAMPANA(R.drawable.simbolo_campana, 10, 18),
    ESTRELLA(R.drawable.simbolo_estrella, 15, 13),
    SIETE(R.drawable.simbolo_siete, 25, 9),
    DIAMANTE(R.drawable.simbolo_diamante, 50, 5)
}
