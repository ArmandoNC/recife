package pe.armando.recife.ui.products

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import pe.armando.recife.R
import pe.armando.recife.ui.cart.CartViewModel
import pe.armando.recife.ui.components.TopBarMenu

@Composable
fun ProductDetailScreen(productId: String, navController: NavHostController, cartViewModel: CartViewModel) {
    // Estado para almacenar el producto cargado desde Firestore
    var product by remember { mutableStateOf<Product?>(null) }

    // Estado para mostrar o esconder el indicador de carga
    var isLoading by remember { mutableStateOf(true) }

    // Estado para manejar la cantidad seleccionada del producto
    var quantity by remember { mutableStateOf(1) }

    // Estado para manejar la visualización de mensajes temporales (snackbars)
    val snackbarHostState = remember { SnackbarHostState() }

    // CoroutineScope para manejar tareas asincrónicas
    val coroutineScope = rememberCoroutineScope()

    // Instancia de Firebase Firestore para interactuar con la base de datos
    val db = FirebaseFirestore.getInstance()

    // Listener que se activa cuando cambia el producto específico en Firestore
    DisposableEffect(productId) {
        val productRef = db.collection("products").document(productId)

        // Listener para detectar cambios en tiempo real en el documento del producto
        val listenerRegistration = productRef.addSnapshotListener { snapshot, e ->
            if (e != null || snapshot == null || !snapshot.exists()) {
                isLoading = false
                return@addSnapshotListener
            }
            product = snapshot.toObject(Product::class.java) // Convertir el documento en un objeto Product
            isLoading = false
        }

        // Eliminar el listener cuando se salga de la pantalla
        onDispose {
            listenerRegistration.remove()
        }
    }

    // Mostrar indicador de carga mientras se obtiene el producto
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator() // Indicador de carga circular
        }
    } else {
        product?.let { productDetail ->
            // Si el producto está cargado, mostrar los detalles
            Scaffold(
                topBar = {
                    TopBarMenu(navController = navController, onSignOut = { /* Manejar el cierre de sesión */ })
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
                content = { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {
                        // Cargar la imagen del producto con un efecto de transición
                        val painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current).data(data = productDetail.imageUrl)
                                .apply {
                                    crossfade(true) // Efecto de transición suave
                                    placeholder(R.drawable.placeholder) // Imagen de marcador de posición mientras se carga
                                    error(R.drawable.placeholder) // Imagen de marcador de posición si hay un error
                                }.build()
                        )

                        // Mostrar la imagen del producto
                        Image(
                            painter = painter,
                            contentDescription = productDetail.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Mostrar el nombre del producto
                        Text(text = productDetail.name, style = MaterialTheme.typography.headlineMedium)

                        // Mostrar el ID del producto
                        Text(text = productDetail.id, style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Mostrar el precio del producto
                        Text(text = "S/. ${productDetail.price}", style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Mostrar la cantidad disponible del producto
                        Text(text = "Disponible: ${productDetail.quantity}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Mostrar la descripción del producto
                        Text(text = productDetail.description, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(32.dp))

                        // Sección para aumentar o disminuir la cantidad seleccionada
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Botón para disminuir la cantidad (mínimo 1)
                            IconButton(onClick = { if (quantity > 1) quantity -= 1 }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Disminuir cantidad")
                            }
                            // Mostrar la cantidad seleccionada
                            Text(text = "$quantity", style = MaterialTheme.typography.headlineMedium)
                            // Botón para aumentar la cantidad (máximo la cantidad disponible)
                            IconButton(onClick = { if (quantity < productDetail.quantity) quantity += 1 }) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Aumentar cantidad")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón para agregar el producto al carrito
                        Button(
                            onClick = {
                                // Agregar el producto al carrito con la cantidad seleccionada
                                cartViewModel.addProductToCart(productDetail.copy(quantity = quantity))
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Producto agregado al carrito") // Mostrar mensaje de confirmación
                                }
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Agregar al Carrito")
                        }
                    }
                }
            )
        }
    }
}