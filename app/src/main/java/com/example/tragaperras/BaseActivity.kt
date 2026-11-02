package com.example.tragaperras

import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.StringRes
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tragaperras.datos.PartidaRepository
import com.google.android.material.appbar.MaterialToolbar
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * Pantalla base: configura la barra de acción y el menú de navegación una sola vez.
 * Todas las pantallas de la app (menos la de bienvenida) heredan de esta clase.
 */
abstract class BaseActivity : AppCompatActivity() {

    protected val repositorio: PartidaRepository by lazy { PartidaRepository.obtener(this) }

    /** Agrupa las suscripciones de RxJava para liberarlas al cerrar la pantalla y evitar fugas de memoria. */
    protected val disposables = CompositeDisposable()

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    protected fun mostrarError(error: Throwable) {
        Log.e(this::class.java.simpleName, "Error de base de datos", error)
        Toast.makeText(this, R.string.error_bd, Toast.LENGTH_SHORT).show()
    }

    protected fun configurarToolbar(@StringRes titulo: Int) {
        setSupportActionBar(findViewById<MaterialToolbar>(R.id.toolbar))
        supportActionBar?.title = getString(titulo)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val destino = when (item.itemId) {
            R.id.menu_inicio -> MainActivity::class.java
            R.id.menu_jugar -> GameActivity::class.java
            R.id.menu_historial -> HistoryActivity::class.java
            R.id.menu_reglas -> RulesActivity::class.java
            else -> return super.onOptionsItemSelected(item)
        }
        if (destino != this::class.java) {
            // Reutiliza la pantalla si ya está abierta, en lugar de apilar copias.
            startActivity(Intent(this, destino).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }
        return true
    }
}
