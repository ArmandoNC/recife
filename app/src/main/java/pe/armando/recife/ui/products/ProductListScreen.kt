package pe.armando.recife.ui.products

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import pe.armando.recife.R
import pe.armando.recife.navigation.Destinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(navController: NavHostController, db: FirebaseFirestore) {
    // Declaración de variables para almacenar la lista de productos, el estado de carga y la escucha de cambios en Firestore
    var products by remember { mutableStateOf<List<Product>>(emptyList()) } // Lista de productos
    var isLoading by remember { mutableStateOf(true) } // Indicador de carga
    var listenerRegistration by remember { mutableStateOf<ListenerRegistration?>(null) } // Registro del listener

    // Efecto lanzado al inicio para escuchar cambios en la colección "products" en Firestore
    LaunchedEffect(Unit) {
        listenerRegistration = db.collection("products").addSnapshotListener { snapshots, e ->
            if (e != null) {
                // Si ocurre un error, desactivamos el indicador de carga
                isLoading = false
                return@addSnapshotListener
            }

            // Si no hay error, actualizamos la lista de productos
            val productList = mutableListOf<Product>()
            for (document in snapshots?.documents ?: emptyList()) {
                val product = document.toObject(Product::class.java)?.copy(id = document.id)
                if (product != null) {
                    productList.add(product)
                }
            }
            products = productList
            isLoading = false // Desactivamos el indicador de carga cuando los productos están listos
        }
    }

    // Efecto desechable para eliminar el listener cuando el composable se destruye
    DisposableEffect(Unit) {
        onDispose {
            listenerRegistration?.remove()
        }
    }

    // Estructura visual de la pantalla
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product List") } // Barra superior con título
            )
        },
        content = { paddingValues ->
            if (isLoading) {
                // Si está cargando, muestra un indicador de carga
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // Si no está cargando, muestra la lista de productos
                LazyColumn(
                    contentPadding = paddingValues,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(products) { product ->
                        ProductListItem(product = product, onClick = {
                            // Navega a la pantalla de detalles del producto cuando se hace clic en un producto
                            navController.navigate(Destinations.ProductDetail.createRoute(product.id))
                        })
                    }
                }
            }
        }
    )
}

@Composable
fun ProductListItem(product: Product, onClick: () -> Unit) {
    // Componente que representa un item de producto en la lista
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }, // Al hacer clic, se ejecuta la acción pasada por onClick
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Elevación de la tarjeta
    ) {
        Row(
            modifier = Modifier.padding(16.dp), // Margen interno de la tarjeta
            verticalAlignment = Alignment.CenterVertically // Alineación vertical de los elementos dentro de la fila
        ) {
            // Carga y visualización de la imagen del producto
            val painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(data = product.imageUrl)
                    .apply<ImageRequest.Builder>(block = fun ImageRequest.Builder.() {
                        crossfade(true) // Efecto de transición
                        placeholder(R.drawable.placeholder) // Imagen de marcador de posición
                        transformations(CircleCropTransformation()) // Transformación de la imagen en un círculo
                    }).build()
            )
            Image(
                painter = painter,
                contentDescription = product.name, // Descripción de la imagen para accesibilidad
                modifier = Modifier.size(64.dp), // Tamaño de la imagen
                contentScale = ContentScale.Crop // Escalado de la imagen para recortarla y llenar el espacio
            )
            Spacer(modifier = Modifier.width(16.dp)) // Espaciado horizontal entre la imagen y el texto
            Column {
                Text(text = product.name, style = MaterialTheme.typography.bodyLarge) // Nombre del producto
                Text(text = "S/. ${product.price}", style = MaterialTheme.typography.bodyMedium) // Precio del producto
                Text(text = "Disponible: ${product.quantity}", style = MaterialTheme.typography.bodySmall) // Cantidad disponible
                Text(text = product.description, style = MaterialTheme.typography.bodySmall) // Descripción del producto
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductListScreenPreview() {
    ProductListScreen(navController = rememberNavController(), db = FirebaseFirestore.getInstance())
}