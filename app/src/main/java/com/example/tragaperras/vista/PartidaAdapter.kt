package com.example.tragaperras.vista

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tragaperras.R
import com.example.tragaperras.modelo.Partida
import java.text.DateFormat
import java.util.Date

/** Muestra una lista de partidas. Se usa en el inicio (mejores) y en el historial (todas). */
class PartidaAdapter : ListAdapter<Partida, PartidaAdapter.PartidaViewHolder>(Comparador) {

    // El formateador se crea una vez y se reutiliza en todas las filas.
    private val formatoFecha = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartidaViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_partida, parent, false)
        return PartidaViewHolder(vista)
    }

    override fun onBindViewHolder(holder: PartidaViewHolder, position: Int) {
        holder.mostrar(getItem(position), formatoFecha)
    }

    class PartidaViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        private val simbolos = listOf<ImageView>(
            vista.findViewById(R.id.simbolo_1),
            vista.findViewById(R.id.simbolo_2),
            vista.findViewById(R.id.simbolo_3)
        )
        private val textoFecha: TextView = vista.findViewById(R.id.texto_fecha)
        private val textoDetalle: TextView = vista.findViewById(R.id.texto_detalle)
        private val textoPremio: TextView = vista.findViewById(R.id.texto_premio)

        fun mostrar(partida: Partida, formatoFecha: DateFormat) {
            val contexto = itemView.context
            simbolos.forEachIndexed { i, imagen ->
                val simbolo = partida.simbolos.getOrNull(i)
                imagen.visibility = if (simbolo == null) View.GONE else View.VISIBLE
                simbolo?.let { imagen.setImageResource(it.imagenRes) }
            }
            textoFecha.text = formatoFecha.format(Date(partida.fecha))
            textoDetalle.text = contexto.getString(R.string.item_detalle, partida.apuesta, partida.saldoFinal)
            textoPremio.text = if (partida.premio > 0) {
                contexto.getString(R.string.item_premio, partida.premio)
            } else {
                contexto.getString(R.string.item_sin_premio, partida.apuesta)
            }
        }
    }

    private object Comparador : DiffUtil.ItemCallback<Partida>() {
        override fun areItemsTheSame(a: Partida, b: Partida) = a.id == b.id
        override fun areContentsTheSame(a: Partida, b: Partida) = a == b
    }
}
