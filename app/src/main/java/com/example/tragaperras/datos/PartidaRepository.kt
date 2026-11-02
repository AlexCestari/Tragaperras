package com.example.tragaperras.datos

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.tragaperras.datos.DBHelper.Companion.COL_APUESTA
import com.example.tragaperras.datos.DBHelper.Companion.COL_FECHA
import com.example.tragaperras.datos.DBHelper.Companion.COL_ID
import com.example.tragaperras.datos.DBHelper.Companion.COL_MONEDAS
import com.example.tragaperras.datos.DBHelper.Companion.COL_PREMIO
import com.example.tragaperras.datos.DBHelper.Companion.COL_SALDO_FINAL
import com.example.tragaperras.datos.DBHelper.Companion.COL_SIMBOLOS
import com.example.tragaperras.datos.DBHelper.Companion.TABLA_JUGADOR
import com.example.tragaperras.datos.DBHelper.Companion.TABLA_PARTIDA
import com.example.tragaperras.modelo.Partida
import com.example.tragaperras.modelo.Simbolo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * Único punto de acceso a SQLite. Todas las operaciones devuelven tipos de RxJava
 * y se ejecutan en el hilo de entrada/salida (Schedulers.io), nunca en el hilo principal.
 */
class PartidaRepository private constructor(private val dbHelper: DBHelper) {

    fun obtenerSaldo(): Single<Int> = Single.fromCallable {
        dbHelper.readableDatabase.query(
            TABLA_JUGADOR, arrayOf(COL_MONEDAS), "$COL_ID = 1", null, null, null, null
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
    }.subscribeOn(Schedulers.io())

    fun guardarSaldo(monedas: Int): Completable = Completable.fromAction {
        dbHelper.writableDatabase.update(
            TABLA_JUGADOR, ContentValues().apply { put(COL_MONEDAS, monedas) }, "$COL_ID = 1", null
        )
    }.subscribeOn(Schedulers.io())

    /** Guarda la partida y el nuevo saldo en una misma transacción: o se guardan los dos o ninguno. */
    fun guardarPartida(partida: Partida): Completable = Completable.fromAction {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            db.insert(TABLA_PARTIDA, null, ContentValues().apply {
                put(COL_FECHA, partida.fecha)
                put(COL_APUESTA, partida.apuesta)
                put(COL_SIMBOLOS, partida.simbolos.joinToString(SEPARADOR) { it.name })
                put(COL_PREMIO, partida.premio)
                put(COL_SALDO_FINAL, partida.saldoFinal)
            })
            db.update(
                TABLA_JUGADOR, ContentValues().apply { put(COL_MONEDAS, partida.saldoFinal) }, "$COL_ID = 1", null
            )
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }.subscribeOn(Schedulers.io())

    /** Las partidas con mayor premio (solo las que han ganado algo). */
    fun obtenerMejores(limite: Int): Single<List<Partida>> = Single.fromCallable {
        dbHelper.readableDatabase.query(
            TABLA_PARTIDA, null, "$COL_PREMIO > 0", null, null, null,
            "$COL_PREMIO DESC, $COL_FECHA DESC", limite.toString()
        ).use { leerPartidas(it) }
    }.subscribeOn(Schedulers.io())

    /** Todas las partidas, de la más reciente a la más antigua. */
    fun obtenerHistorial(): Single<List<Partida>> = Single.fromCallable {
        dbHelper.readableDatabase.query(
            TABLA_PARTIDA, null, null, null, null, null, "$COL_FECHA DESC"
        ).use { leerPartidas(it) }
    }.subscribeOn(Schedulers.io())

    private fun leerPartidas(cursor: Cursor): List<Partida> {
        val iId = cursor.getColumnIndexOrThrow(COL_ID)
        val iFecha = cursor.getColumnIndexOrThrow(COL_FECHA)
        val iApuesta = cursor.getColumnIndexOrThrow(COL_APUESTA)
        val iSimbolos = cursor.getColumnIndexOrThrow(COL_SIMBOLOS)
        val iPremio = cursor.getColumnIndexOrThrow(COL_PREMIO)
        val iSaldo = cursor.getColumnIndexOrThrow(COL_SALDO_FINAL)

        val partidas = ArrayList<Partida>(cursor.count)
        while (cursor.moveToNext()) {
            partidas += Partida(
                id = cursor.getLong(iId),
                fecha = cursor.getLong(iFecha),
                apuesta = cursor.getInt(iApuesta),
                simbolos = cursor.getString(iSimbolos).split(SEPARADOR).map { Simbolo.valueOf(it) },
                premio = cursor.getInt(iPremio),
                saldoFinal = cursor.getInt(iSaldo)
            )
        }
        return partidas
    }

    companion object {
        private const val SEPARADOR = ","

        @Volatile
        private var instancia: PartidaRepository? = null

        /** Una sola instancia para toda la app, con el contexto de la aplicación para no retener pantallas. */
        fun obtener(context: Context): PartidaRepository =
            instancia ?: synchronized(this) {
                instancia ?: PartidaRepository(DBHelper(context.applicationContext)).also { instancia = it }
            }
    }
}
