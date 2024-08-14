package pe.armando.recife.ui.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import pe.armando.recife.R
import pe.armando.recife.navigation.Destinations
import pe.armando.recife.ui.components.TopBarMenu
import pe.armando.recife.ui.profile.ProfileViewModel

// Representa un ítem en el carrito de compras
data class CartItem(
    val id: String = "",           // ID único del producto
    val name: String = "",         // Nombre del producto
    val price: Double = 0.0,       // Precio del producto
    val quantity: Int = 0,         // Cantidad del producto en el carrito
    val imageUrl: String = ""      // URL de la imagen del producto
)

// Pantalla del carrito de compras
@Composable
fun CartScreen(
    cartItems: List<CartItem>,           // Lista de ítems en el carrito
    onCheckout: () -> Unit,              // Acción al realizar el checkout
    onRemoveItem: (String) -> Unit,      // Acción para eliminar un ítem del carrito
    cartViewModel: CartViewModel,        // ViewModel para manejar el estado del carrito
    navController: NavHostController,    // Controlador de navegación
    onSignOut: () -> Unit,               // Acción para cerrar sesión
    profileViewModel: ProfileViewModel = viewModel()  // ViewModel para manejar el perfil del usuario
) {

    // Estado que controla si se muestra o no el diálogo de confirmación de dirección
    var showAddressDialog by remember { mutableStateOf(false) }

    // Observa el total del carrito desde el ViewModel
    val totalAmount = cartViewModel.totalAmount.collectAsState().value
    // Observa el perfil del usuario desde el ViewModel
    val userProfile = profileViewModel.user.collectAsState().value

    Scaffold(
        topBar = {
            // Barra superior con el menú y la opción de cerrar sesión
            TopBarMenu(navController = navController, onSignOut = onSignOut)
        },
        content = { paddingValues ->
            // Contenedor principal de la pantalla del carrito
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Lista de ítems en el carrito
                LazyColumn(
                    modifier = Modifier.weight(1f)  // La lista toma todo el espacio disponible
                ) {
                    // Itera sobre cada ítem en el carrito y los muestra usando CartListItem
                    items(cartItems) { cartItem ->
                        CartListItem(
                            cartItem = cartItem,
                            onRemoveItem = { onRemoveItem(cartItem.id) }  // Llama a la función para eliminar ítems
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Muestra el total del carrito
                Text(
                    text = "Total: S/. ${"%.2f".format(totalAmount)}",  // Formato del total en soles
                    style = MaterialTheme.typography.headlineSmall,     // Estilo de texto
                    modifier = Modifier.align(Alignment.End)            // Alineado a la derecha
                )

                Spacer(modifier = Modifier.height(16.dp))
                // Botón de checkout
                Button(
                    onClick = {
                        if (userProfile != null) {  // Si el perfil del usuario está disponible
                            showAddressDialog = true  // Muestra el diálogo de confirmación de dirección
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Checkout")  // Texto del botón
                }
            }

            // Diálogo de confirmación de dirección de envío
            if (showAddressDialog) {
                AlertDialog(
                    onDismissRequest = { showAddressDialog = false },  // Acción al descartar el diálogo
                    title = { Text("Confirmar Dirección de Envío") },   // Título del diálogo
                    text = {
                        Column {
                            // Muestra la dirección y otros datos del perfil del usuario
                            Text("Dirección: ${userProfile?.address}")
                            Text("Departamento: ${userProfile?.department}")
                            Text("Provincia: ${userProfile?.province}")
                            Text("Distrito: ${userProfile?.district}")
                        }
                    },
                    confirmButton = {
                        // Botón para confirmar y proceder al pago
                        Button(onClick = {
                            showAddressDialog = false
                            navController.navigate("payment_screen") // Navegación a la pantalla de pago
                        }) {
                            Text("Aceptar")
                        }
                    },
                    dismissButton = {
                        // Botón para cancelar y navegar al perfil
                        Button(onClick = {
                            showAddressDialog = false
                            navController.navigate(Destinations.Profile.route)
                        }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    )
}

// Composable que representa un ítem en la lista del carrito
@Composable
fun CartListItem(cartItem: CartItem, onRemoveItem: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),  // Espaciado alrededor del ítem
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)  // Elevación del ítem
    ) {
        Row(
            modifier = Modifier.padding(16.dp),  // Espaciado interno
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Carga y muestra la imagen del producto usando Coil
            val painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(data = cartItem.imageUrl)
                    .apply<ImageRequest.Builder>(block = fun ImageRequest.Builder.() {
                        crossfade(true)  // Efecto de transición suave
                        placeholder(R.drawable.placeholder)  // Imagen de carga
                        transformations(CircleCropTransformation())  // Transformación circular de la imagen
                    }).build()
            )
            Image(
                painter = painter,
                contentDescription = cartItem.name,  // Descripción de la imagen
                modifier = Modifier.size(64.dp),  // Tamaño de la imagen
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))  // Espaciado entre la imagen y el texto
            Column(
                modifier = Modifier.weight(1f)  // El texto toma todo el espacio disponible
            ) {
                // Muestra el nombre del producto
                Text(text = cartItem.name, style = MaterialTheme.typography.bodyLarge)
                // Muestra el precio y la cantidad del producto
                Text(text = "S/. ${"%.2f".format(cartItem.price)} x ${cartItem.quantity}", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.width(8.dp))  // Espaciado entre el texto y el botón de eliminar
            // Botón para eliminar el ítem del carrito
            IconButton(onClick = onRemoveItem) {
                Icon(Icons.Default.Delete, contentDescription = "Remove Item")  // Icono de eliminación
            }
        }
    }
}