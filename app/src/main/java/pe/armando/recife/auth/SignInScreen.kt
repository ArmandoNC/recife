package pe.armando.recife.auth

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.* // Importación de layouts básicos para la interfaz de usuario
import androidx.compose.material.icons.Icons // Importación de íconos
import androidx.compose.material.icons.filled.ExitToApp // Importación específica del ícono "ExitToApp"
import androidx.compose.material3.* // Importación de Material Design 3 para los componentes de UI
import androidx.compose.runtime.* // Importación para el manejo del estado en Compose
import androidx.compose.ui.Alignment // Importación para la alineación de elementos en Compose
import androidx.compose.ui.Modifier // Importación del modificador para alterar componentes de Compose
import androidx.compose.ui.platform.testTag // Importación para etiquetar componentes (útil en pruebas)
import androidx.compose.ui.tooling.preview.Preview // Importación para crear vistas previas de Composables
import androidx.compose.ui.unit.dp // Importación para manejar unidades de medida
import androidx.navigation.NavHostController // Importación para el controlador de navegación
import androidx.navigation.compose.rememberNavController // Importación para recordar el controlador de navegación
import kotlinx.coroutines.launch // Importación para trabajar con coroutines
import pe.armando.recife.navigation.Destinations // Importación de los destinos de navegación definidos en la app

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    onGoogleSignIn: () -> Unit, // Callback que se ejecuta cuando el usuario intenta iniciar sesión con Google
    onSignOut: () -> Unit, // Callback que se ejecuta cuando el usuario cierra sesión
    isUserSignedIn: Boolean, // Indicador que determina si el usuario ya está autenticado
    navController: NavHostController // Controlador de navegación para movernos entre pantallas
) {
    val scope = rememberCoroutineScope() // Se recuerda un CoroutineScope para manejar tareas asíncronas

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sign In") }, // Título de la barra superior
                actions = {
                    if (isUserSignedIn) { // Si el usuario ya está autenticado
                        IconButton(onClick = {
                            scope.launch { // Lanzamiento de una coroutine
                                onSignOut() // Ejecución del callback de cierre de sesión
                                navController.navigate(Destinations.SignIn.route) { // Navegación a la pantalla de inicio de sesión
                                    popUpTo(Destinations.Home.route) { inclusive = true } // Se eliminan las pantallas previas de la pila de navegación
                                }
                            }
                        }) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out") // Ícono de "Salir" en la barra superior
                        }
                    }
                }
            )
        },
        content = {
            Box(modifier = Modifier.fillMaxSize().testTag("SignInScreen")) { // Caja que llena todo el tamaño disponible
                if (isUserSignedIn) { // Si el usuario ya está autenticado
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center) // Alineación central
                            .padding(16.dp), // Espaciado interno de 16dp
                        horizontalAlignment = Alignment.CenterHorizontally // Alineación horizontal centrada
                    ) {
                        Text("Welcome!") // Texto de bienvenida
                        Spacer(modifier = Modifier.height(8.dp)) // Espaciado entre elementos
                        Button(onClick = {
                            navController.navigate(Destinations.Home.route) { // Navegación a la pantalla de inicio
                                popUpTo(Destinations.SignIn.route) { inclusive = true } // Se eliminan las pantallas previas de la pila de navegación
                            }
                        }) {
                            Text("Go to Home") // Texto del botón para ir a la pantalla de inicio
                        }
                    }
                } else { // Si el usuario no está autenticado
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center) // Alineación central
                            .padding(16.dp), // Espaciado interno de 16dp
                        horizontalAlignment = Alignment.CenterHorizontally // Alineación horizontal centrada
                    ) {
                        Button(onClick = onGoogleSignIn) { // Botón para iniciar sesión con Google
                            Text("Sign In with Google") // Texto del botón de inicio de sesión
                        }
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    SignInScreen(
        onGoogleSignIn = {}, // No-op para la vista previa
        onSignOut = {}, // No-op para la vista previa
        isUserSignedIn = false, // Estado inicial: usuario no autenticado
        navController = rememberNavController() // Controlador de navegación para la vista previa
    )
}