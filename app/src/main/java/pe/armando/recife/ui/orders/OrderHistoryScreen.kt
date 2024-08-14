package pe.armando.recife.ui.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pe.armando.recife.ui.components.TopBarMenu
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun OrderHistoryScreen(
    navController: NavHostController,
    onSignOut: () -> Unit
) {
    // Lista mutable para almacenar las órdenes del usuario.
    val orders = remember { mutableStateListOf<Order>() }
    // Obtiene el ID del usuario autenticado.
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    // Instancia de FirebaseFirestore para realizar consultas a la base de datos.
    val db = FirebaseFirestore.getInstance()
    // Variable para manejar mensajes de error.
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Efecto que se lanza cuando se monta el composable o cambia el userId.
    LaunchedEffect(userId) {
        if (userId != null) {
            // Consulta a la colección "orders" filtrando por el ID del usuario.
            db.collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val orderId = document.id  // Obtiene el ID de la orden.
                        val order = document.toObject(Order::class.java).copy(id = orderId)  // Mapea el documento a un objeto Order y le asigna el ID.
                        orders.add(order)  // Añade la orden a la lista.
                    }
                }
                .addOnFailureListener { exception ->
                    // Si hay un error al cargar las órdenes, se muestra un mensaje de error.
                    errorMessage = "Error al cargar órdenes: ${exception.message}"
                }
        } else {
            // Si el usuario no está autenticado, se muestra un mensaje de error.
            errorMessage = "Usuario no autenticado"
        }
    }

    Scaffold(
        // Menú superior reutilizable.
        topBar = {
            TopBarMenu(navController = navController, onSignOut = onSignOut)
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()  // Ocupa todo el espacio disponible.
                    .padding(paddingValues)
                    .padding(16.dp)  // Espaciado interno.
            ) {
                // Mostrar el mensaje de error si existe.
                errorMessage?.let {
                    Text(
                        text = it,  // Texto del mensaje de error.
                        color = MaterialTheme.colorScheme.error,  // Color de error.
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Muestra la lista de órdenes en una columna.
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Itera sobre la lista de órdenes y crea un OrderListItem para cada una.
                    items(orders) { order ->
                        OrderListItem(order)
                    }
                }
            }
        }
    )
}

@Composable
fun OrderListItem(order: Order) {
    // Formateador de fechas para mostrar la fecha de la orden.
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val formattedDate = sdf.format(order.date.toDate())  // Formatea la fecha.
    val totalQuantity = order.items.sumOf { it.quantity }  // Calcula la cantidad total de productos en la orden.

    Card(
        modifier = Modifier
            .fillMaxWidth()  // La tarjeta ocupa todo el ancho disponible.
            .padding(8.dp),  // Espaciado alrededor de la tarjeta.
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)  // Elevación para dar efecto de sombra.
    ) {
        // Columna para mostrar los detalles de la orden.
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Order ID: ${order.id}", style = MaterialTheme.typography.bodyLarge)  // Muestra el ID de la orden.
            Text(text = "Fecha: $formattedDate", style = MaterialTheme.typography.bodyMedium)  // Muestra la fecha de la orden.
            Text(text = "Estado: ${order.status}", style = MaterialTheme.typography.bodyMedium)  // Muestra el estado de la orden.
            Text(text = "Total Productos: $totalQuantity", style = MaterialTheme.typography.bodyMedium)  // Muestra el total de productos.
            Text(text = "Total: S/. ${order.totalAmount}", style = MaterialTheme.typography.bodyMedium)  // Muestra el total de la orden.
            Spacer(modifier = Modifier.height(8.dp))  // Espaciado entre los elementos.
        }
    }
}