package pe.armando.recife.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Definición de colores personalizados usando la clase Color, con valores hexadecimales específicos.
val Red = Color(0xFFE53935)   // Rojo brillante, usado como color primario.
val Black = Color(0xFF000000) // Negro puro, usado como color secundario y de fondo en temas oscuros.
val White = Color(0xFFFFFFFF) // Blanco puro, usado como color de texto y de fondo en temas claros.

// Esquema de colores para el tema oscuro
val DarkColorScheme = darkColorScheme(
    primary = Red,            // El color primario en el tema oscuro será rojo.
    onPrimary = White,        // El texto o íconos sobre elementos primarios serán blancos.
    secondary = Black,        // El color secundario será negro.
    onSecondary = White,      // El texto o íconos sobre elementos secundarios serán blancos.
    background = Black,       // El fondo general será negro.
    onBackground = White,     // El texto sobre el fondo será blanco.
    surface = Black,          // El color de las superficies (como tarjetas y diálogos) será negro.
    onSurface = White         // El texto sobre superficies será blanco.
)

// Esquema de colores para el tema claro
val LightColorScheme = lightColorScheme(
    primary = Red,            // El color primario en el tema claro también será rojo.
    onPrimary = White,        // El texto o íconos sobre elementos primarios serán blancos.
    secondary = Black,        // El color secundario será negro.
    onSecondary = White,      // El texto o íconos sobre elementos secundarios serán blancos.
    background = White,       // El fondo general será blanco.
    onBackground = Black,     // El texto sobre el fondo será negro.
    surface = White,          // El color de las superficies será blanco.
    onSurface = Black         // El texto sobre superficies será negro.
)