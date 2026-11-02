# Tragaperras

Juego de azar para Android con estética de máquina recreativa de los 90, desarrollado en Kotlin para la asignatura FP.061 Desarrollo de aplicaciones móviles nativas (UOC).

## Funcionalidades (Producto 1)

- Pantalla de bienvenida con botón INSERT COIN.
- Barra de acción con menú: Inicio, Jugar, Historial y Reglas.
- Tragaperras de tres rodillos animados con Canvas y símbolos con distinta probabilidad.
- Apuestas de 1, 5 o 10 monedas; el saldo se mantiene entre sesiones.
- Historial de partidas y las 5 mejores en la pantalla de inicio.
- Persistencia en SQLite con acceso asíncrono mediante RxJava.

## Estructura

| Paquete | Contenido |
| --- | --- |
| `(raíz)` | Activities: Splash, Main, Game, History, Rules y BaseActivity |
| `vista` | RodilloView (animación con Canvas) y PartidaAdapter |
| `logica` | Tragaperras: tiradas aleatorias ponderadas y cálculo de premios |
| `modelo` | Simbolo, Jugador y Partida |
| `datos` | DBHelper (SQLite) y PartidaRepository (RxJava) |

## Requisitos

Android Studio, minSdk 24.
