package pe.armando.recife.ui.home

import android.annotation.SuppressLint
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import pe.armando.recife.ui.components.TopBarMenu
import pe.armando.recife.ui.products.ProductListScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    onSignOut: () -> Unit,  // Función que se ejecuta al cerrar sesión
    navController: NavHostController,  // Controlador de navegación para moverse entre pantallas
    db: FirebaseFirestore  // Instancia de Firebase Firestore para manejar datos
) {
    // Scaffold es un componente que proporciona una estructura básica de pantalla
    Scaffold(
        // `topBar` define la barra superior de la pantalla.
        topBar = {
            // Aquí usamos el menú reutilizable TopBarMenu.
            // Incluye los iconos de navegación y el menú de perfil.
            TopBarMenu(navController = navController, onSignOut = onSignOut)
        },
        // `content` define el contenido principal de la pantalla.
        content = { paddingValues ->
            // Muestra la lista de productos usando el componente `ProductListScreen`.
            // Pasamos el controlador de navegación y la base de datos como parámetros.
            ProductListScreen(navController = navController, db = db)
        }
    )
}

// Esta función es para previsualizar la pantalla en el editor de diseño de Android Studio.
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    // Se llama a HomeScreen con parámetros vacíos o de ejemplo para ver cómo se vería la pantalla.
    HomeScreen(onSignOut = {}, navController = rememberNavController(), db = FirebaseFirestore.getInstance())
}