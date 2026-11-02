package com.example.tragaperras.vista

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import com.example.tragaperras.modelo.Simbolo
import kotlin.math.floor
import kotlin.math.min

/**
 * Rodillo de la tragaperras dibujado con Canvas.
 * Dibuja una tira vertical de símbolos y la desplaza con una animación que frena
 * hasta dejar el símbolo de destino en el centro.
 */
class RodilloView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val simbolos = Simbolo.entries

    // Las imágenes se cargan una sola vez: en onDraw no se crea ningún objeto.
    private val imagenes: List<Drawable> =
        simbolos.map { ContextCompat.getDrawable(context, it.imagenRes)!!.mutate() }

    /** Posición de la tira, en número de símbolos (2.0 = tercer símbolo centrado). */
    private var posicion = 0f
    private var animador: ValueAnimator? = null

    fun mostrar(simbolo: Simbolo) {
        animador?.cancel()
        posicion = simbolo.ordinal.toFloat()
        contentDescription = simbolo.name
        invalidate()
    }

    /**
     * Gira el rodillo `vueltas` vueltas completas y se detiene en `destino`.
     * Llama a `alTerminar` cuando el rodillo se ha parado.
     */
    fun animarHasta(destino: Simbolo, duracionMs: Long, vueltas: Int, alTerminar: () -> Unit) {
        animador?.cancel()
        val actual = posicion.toInt() % simbolos.size
        val pasos = vueltas * simbolos.size + (destino.ordinal - actual + simbolos.size) % simbolos.size
        val inicio = actual.toFloat()

        animador = ValueAnimator.ofFloat(inicio, inicio + pasos).apply {
            duration = duracionMs
            interpolator = DecelerateInterpolator(2f)
            addUpdateListener {
                posicion = it.animatedValue as Float
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    posicion = destino.ordinal.toFloat()
                    contentDescription = destino.name
                    invalidate()
                    alTerminar()
                }
            })
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val alto = height.toFloat()
        val centroX = width / 2f
        val lado = min(width, height) * 0.8f
        val base = floor(posicion).toInt()
        val fraccion = posicion - base

        // Se dibujan el símbolo central y sus vecinos; la tarjeta recorta lo que sobra.
        for (k in -1..2) {
            val indice = Math.floorMod(base + k, simbolos.size)
            val centroY = alto / 2f + (k - fraccion) * alto
            imagenes[indice].setBounds(
                (centroX - lado / 2).toInt(), (centroY - lado / 2).toInt(),
                (centroX + lado / 2).toInt(), (centroY + lado / 2).toInt()
            )
            imagenes[indice].draw(canvas)
        }
    }

    override fun onDetachedFromWindow() {
        animador?.cancel()
        super.onDetachedFromWindow()
    }
}
