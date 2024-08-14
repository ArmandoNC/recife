package pe.armando.recife.ui.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Timestamp
import pe.armando.recife.ui.components.TopBarMenu
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(orderId: String, navController: NavHostController, onSignOut: () -> Unit) {
    // Simulación de una orden con detalles de productos
    val order = remember {
        Order(
            id = orderId,  // ID de la orden, se pasa como parámetro
            date = Timestamp.now(),  // Fecha actual como fecha de la orden
            totalAmount = 50.00,  // Monto total de la orden
            status = "Delivered",  // Estado de la orden
            items = listOf(
                OrderItem("1", "Product 1", "S/. 10.00", 2, "https://via.placeholder.com/150"),
                OrderItem("2", "Product 2", "S/. 15.00", 1, "https://via.placeholder.com/150"),
                OrderItem("3", "Product 3", "S/. 20.00", 1, "https://via.placeholder.com/150")
            )  // Lista de productos en la orden
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Detail") },  // Título de la barra superior
                navigationIcon = {
                    // Icono de retroceso, que navega a la pantalla anterior
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Menú superior reutilizable para opciones adicionales
                    TopBarMenu(navController = navController, onSignOut = onSignOut)
                }
            )
        },
        content = { paddingValues ->
            // Columna que organiza los detalles de la orden
            Column(
                modifier = Modifier
                    .fillMaxSize()  // Ocupa todo el espacio disponible
                    .padding(paddingValues)
                    .padding(16.dp)  // Espaciado interno
            ) {
                // Formatear la fecha de la orden
                val formattedDate = remember {
                    val sdf = SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                    sdf.format(order.date.toDate())
                }
                // Mostrar el ID de la orden
                Text(text = "Order ID: ${order.id}", style = MaterialTheme.typography.headlineSmall)
                // Mostrar la fecha de la orden
                Text(text = "Date: $formattedDate", style = MaterialTheme.typography.bodyMedium)
                // Mostrar el monto total de la orden
                Text(text = "Total: S/. ${order.totalAmount}", style = MaterialTheme.typography.bodyMedium)
                // Mostrar el estado de la orden
                Text(text = "Status: ${order.status}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))  // Espaciado entre el encabezado y la lista de productos
                // Lista de productos en la orden
                LazyColumn {
                    items(order.items) { item ->
                        // Aquí podrías añadir un componente visual para cada item de la orden
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    )
}

// Función de previsualización para ver cómo se vería la pantalla en Android Studio
@Preview(showBackground = true)
@Composable
fun OrderDetailScreenPreview() {
    OrderDetailScreen(orderId = "1", navController = rememberNavController(), onSignOut = {})
}