package pe.armando.recife.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import pe.armando.recife.navigation.Destinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarMenu(
    navController: NavHostController,  // Controlador de navegación que maneja las rutas
    onSignOut: () -> Unit  // Acción que se ejecuta cuando el usuario cierra sesión
) {
    // Variable que controla si el menú desplegable (dropdown) está expandido o no.
    var expanded by remember { mutableStateOf(false) }

    // Barra superior (TopAppBar) que aparece en la parte superior de la pantalla
    TopAppBar(
        // Título que aparece en la barra superior.
        title = { Text("Recife") },
        // Acciones que aparecerán a la derecha del título en la barra superior.
        actions = {
            // Botón con el icono de tienda (Home). Al hacer clic, navega a la pantalla principal (Home).
            IconButton(onClick = { navController.navigate(Destinations.Home.route) }) {
                Icon(imageVector = Icons.Default.Home, contentDescription = "Home")  // Icono de tienda.
            }
            // Botón con el icono de carrito. Al hacer clic, navega a la pantalla del carrito (Cart).
            IconButton(onClick = { navController.navigate(Destinations.Cart.route) }) {
                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Cart")  // Icono de carrito.
            }
            // Contenedor que envuelve el icono de perfil y el menú desplegable.
            Box {
                // Botón con el icono de perfil. Al hacer clic, se expande o colapsa el menú desplegable.
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.Person,  // Icono de persona que representa el perfil.
                        contentDescription = "Profile"       // Descripción para accesibilidad.
                    )
                }
                // Menú desplegable que aparece debajo del icono de perfil.
                DropdownMenu(
                    expanded = expanded,                    // Controla si el menú está visible o no.
                    onDismissRequest = { expanded = false }, // Cierra el menú cuando se hace clic fuera de él.
                    modifier = Modifier
                        .wrapContentWidth()                  // Ajusta el ancho del menú para que coincida con su contenido.
                        .align(Alignment.TopStart)           // Alinea el menú debajo del icono de perfil.
                ) {
                    // Opción del menú para editar el perfil.
                    DropdownMenuItem(
                        text = { Text("Edit Profile") },    // Texto de la opción.
                        onClick = {
                            expanded = false                // Colapsa el menú cuando se selecciona esta opción.
                            navController.navigate(Destinations.Profile.route)  // Navega a la pantalla de edición de perfil.
                        }
                    )
                    // Opción del menú para ver el historial de pedidos.
                    DropdownMenuItem(
                        text = { Text("Order History") },    // Texto de la opción.
                        onClick = {
                            expanded = false                // Colapsa el menú cuando se selecciona esta opción.
                            navController.navigate(Destinations.OrderHistory.route)  // Navega a la pantalla del historial de pedidos.
                        }
                    )
                }
            }
            // Botón con el icono de salir. Al hacer clic, cierra la sesión y navega a la pantalla de inicio de sesión.
            IconButton(onClick = {
                onSignOut()                                 // Llama a la función de cerrar sesión.
                navController.navigate(Destinations.SignIn.route) { // Navega a la pantalla de inicio de sesión.
                    popUpTo(Destinations.Home.route) {      // Elimina las pantallas previas del backstack para evitar volver atrás.
                        inclusive = true
                    }
                }
            }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sign Out")  // Icono de salir.
            }
        }
    )
}