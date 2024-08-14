package pe.armando.recife.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun RecifeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Parámetro que determina si se debe aplicar el tema oscuro. Por defecto, toma el valor según la configuración del sistema.
    dynamicColor: Boolean = true, // Parámetro que habilita o deshabilita los colores dinámicos. Por defecto, está activado.
    content: @Composable () -> Unit // Contenido que será envuelto dentro del tema. Es una función composable.
) {
    // Selección del esquema de color según las opciones dinámicas y el tema oscuro.
    val colorScheme = when {
        // Si se permite el color dinámico y la versión del sistema operativo es S (Android 12) o superior...
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            // ...se selecciona el esquema de color dinámico oscuro o claro según el tema actual.
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme // Si no se permite el color dinámico pero el tema es oscuro, se selecciona un esquema de color oscuro predeterminado.
        else -> LightColorScheme // Si no se permite el color dinámico y el tema no es oscuro, se selecciona un esquema de color claro predeterminado.
    }

    // Aplicación del tema de Material Design 3 (Material You) utilizando el esquema de color seleccionado y la tipografía definida.
    MaterialTheme(
        colorScheme = colorScheme, // Esquema de color que se aplicará al tema.
        typography = Typography, // Tipografía definida para el tema.
        content = content // Contenido composable que se renderizará dentro del tema.
    )
}