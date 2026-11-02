package com.example.tragaperras.datos

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.tragaperras.modelo.Jugador

/** Crea y actualiza la base de datos SQLite del dispositivo. */
class DBHelper(context: Context) : SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION_BD) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLA_PARTIDA (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_FECHA INTEGER NOT NULL,
                $COL_APUESTA INTEGER NOT NULL,
                $COL_SIMBOLOS TEXT NOT NULL,
                $COL_PREMIO INTEGER NOT NULL,
                $COL_SALDO_FINAL INTEGER NOT NULL
            )
            """.trimIndent()
        )
        // Índice para que la consulta de mejores partidas no recorra toda la tabla.
        db.execSQL("CREATE INDEX idx_partida_premio ON $TABLA_PARTIDA($COL_PREMIO)")

        // Tabla de una sola fila con el saldo del jugador.
        db.execSQL(
            """
            CREATE TABLE $TABLA_JUGADOR (
                $COL_ID INTEGER PRIMARY KEY CHECK ($COL_ID = 1),
                $COL_MONEDAS INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.insert(TABLA_JUGADOR, null, ContentValues().apply {
            put(COL_ID, 1)
            put(COL_MONEDAS, Jugador.MONEDAS_INICIALES)
        })
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA_PARTIDA")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_JUGADOR")
        onCreate(db)
    }

    companion object {
        const val NOMBRE_BD = "tragaperras.db"
        const val VERSION_BD = 1

        const val TABLA_PARTIDA = "partida"
        const val TABLA_JUGADOR = "jugador"

        const val COL_ID = "id"
        const val COL_FECHA = "fecha"
        const val COL_APUESTA = "apuesta"
        const val COL_SIMBOLOS = "simbolos"
        const val COL_PREMIO = "premio"
        const val COL_SALDO_FINAL = "saldo_final"
        const val COL_MONEDAS = "monedas"
    }
}
