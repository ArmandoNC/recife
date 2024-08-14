package pe.armando.recife.ui.payment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import pe.armando.recife.navigation.Destinations
import pe.armando.recife.ui.cart.CartViewModel
import pe.armando.recife.ui.components.TopBarMenu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavHostController, // Controlador de navegación para manejar las rutas entre pantallas
    onSignOut: () -> Unit, // Función que se ejecuta cuando el usuario cierra sesión
    cartViewModel: CartViewModel // ViewModel que contiene la lógica y el estado del carrito de compras
){
    // SnackbarHostState se usa para mostrar mensajes temporales en la pantalla (snackbars)
    val snackbarHostState = remember { SnackbarHostState() }

    // CoroutineScope para manejar operaciones asincrónicas, como la interacción con la base de datos
    val coroutineScope = rememberCoroutineScope()

    // Instancia de Firebase Firestore para interactuar con la base de datos en la nube
    val db = FirebaseFirestore.getInstance()

    // Estado que contiene los elementos del carrito de compras
    val cartItems = cartViewModel.cartItems.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Proceso de Pago") }, // Título de la barra superior
                actions = {
                    TopBarMenu(navController = navController, onSignOut = onSignOut) // Menú desplegable en la barra superior
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Lugar donde se muestran los snackbars
        content = { paddingValues ->
            // Column para organizar los elementos de la pantalla de forma vertical
            Column(
                modifier = Modifier
                    .fillMaxSize() // Llenar todo el espacio disponible en la pantalla
                    .padding(paddingValues) // Agregar padding de los valores de scaffold
                    .padding(16.dp), // Padding adicional
                verticalArrangement = Arrangement.Center, // Centrar los elementos verticalmente
                horizontalAlignment = Alignment.CenterHorizontally // Centrar los elementos horizontalmente
            ) {
                Text(text = "Welcom to Pay Page") // Texto de bienvenida a la página de pago
                Spacer(modifier = Modifier.height(16.dp)) // Espacio entre los elementos

                // Botón para realizar el pago
                Button(onClick = {
                    coroutineScope.launch {
                        // Mostrar un snackbar indicando que el pedido se está preparando
                        snackbarHostState.showSnackbar("Tu pedido se está preparando")
                        // Navegar a la pantalla de historial de órdenes
                        navController.navigate(Destinations.OrderHistory.route)

                        // Preparar los datos de la orden para guardarlos en Firestore
                        val orderData = hashMapOf(
                            "userId" to FirebaseAuth.getInstance().currentUser?.uid.orEmpty(), // ID del usuario
                            "totalAmount" to cartViewModel.totalAmount.value, // Monto total del carrito
                            "orderStatus" to "En preparación", // Estado inicial de la orden
                            "orderDate" to com.google.firebase.Timestamp.now(), // Fecha y hora de la orden
                            "items" to cartItems.map { // Lista de productos en la orden
                                hashMapOf(
                                    "productId" to it.id, // ID del producto
                                    "quantity" to it.quantity // Cantidad del producto
                                )
                            }
                        )

                        // Guardar la orden en Firestore
                        db.collection("orders")
                            .add(orderData)
                            .addOnSuccessListener {
                                // Limpiar el carrito después de guardar la orden
                                cartViewModel.clearCart()

                                // Navegar a la pantalla de historial de órdenes después de guardar la orden
                                navController.navigate(Destinations.OrderHistory.route)
                            }
                            .addOnFailureListener {
                                coroutineScope.launch {
                                    // Mostrar un snackbar si ocurre un error al guardar la orden
                                    snackbarHostState.showSnackbar("Error al guardar la orden")
                                }
                            }
                    }
                }) {
                    Text("Pagar Ahora") // Texto del botón de pago
                }
            }
        }
    )
}
