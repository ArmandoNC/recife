package pe.armando.recife.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import pe.armando.recife.ui.products.Product

// Clase ViewModel para manejar el estado del carrito de compras del usuario
class CartViewModel(
    // Referencias a Firebase Firestore y Firebase Authentication
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    // Estado que contiene la lista de productos en el carrito, se utiliza MutableStateFlow para manejar el estado reactivo
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    // Estado que calcula el monto total del carrito basado en los productos y sus cantidades
    val totalAmount: StateFlow<Double> = _cartItems.map { items ->
        items.sumOf { it.price * it.quantity }  // Suma el total de cada producto multiplicando su precio por su cantidad
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    init {
        // Cuando se crea el ViewModel, se carga el carrito del usuario actual desde Firestore
        loadUserCart()
    }

    // Función privada para cargar el carrito del usuario desde Firestore
    private fun loadUserCart() {
        val user = auth.currentUser ?: return  // Si no hay usuario autenticado, no se realiza ninguna acción

        // Obtiene la colección 'cart' del usuario en Firestore y carga los productos en _cartItems
        db.collection("users").document(user.uid).collection("cart")
            .get()
            .addOnSuccessListener { documents ->
                val cartItems = documents.map { doc ->
                    doc.toObject(CartItem::class.java)  // Convierte cada documento en un objeto CartItem
                }
                _cartItems.value = cartItems  // Actualiza el estado del carrito con los productos obtenidos
            }
            .addOnFailureListener {
                // Manejo de errores si la operación falla
            }
    }

    // Función privada para guardar el carrito del usuario en Firestore
    private fun saveUserCart() {
        val user = auth.currentUser ?: return  // Si no hay usuario autenticado, no se realiza ninguna acción

        val cartRef = db.collection("users").document(user.uid).collection("cart")

        // Primero limpia el carrito anterior en Firestore
        cartRef.get().addOnSuccessListener { documents ->
            for (doc in documents) {
                cartRef.document(doc.id).delete()  // Elimina cada documento del carrito
            }

            // Luego guarda el nuevo carrito en Firestore
            _cartItems.value.forEach { cartItem ->
                cartRef.add(cartItem)
            }
        }
    }

    // Función para agregar un producto al carrito
    fun addProductToCart(product: Product) {
        val currentCartItems = _cartItems.value.toMutableList()  // Crea una lista mutable a partir del estado actual del carrito

        // Busca si el producto ya está en el carrito por ID y nombre
        val existingItemIndex = currentCartItems.indexOfFirst { it.id == product.id && it.name == product.name }

        if (existingItemIndex >= 0) {
            // Si el producto ya está en el carrito, incrementa la cantidad
            val existingItem = currentCartItems[existingItemIndex]
            val updatedItem = existingItem.copy(quantity = existingItem.quantity + product.quantity)
            currentCartItems[existingItemIndex] = updatedItem
        } else {
            // Si el producto no está en el carrito, se añade a la lista
            currentCartItems.add(
                CartItem(
                    id = product.id,
                    name = product.name,
                    price = product.price,
                    quantity = product.quantity,
                    imageUrl = product.imageUrl
                )
            )
        }

        // Actualiza el estado del carrito con la nueva lista de productos
        _cartItems.value = currentCartItems

        // Guarda el carrito actualizado en Firestore
        saveUserCart()
    }

    // Función para eliminar un producto del carrito
    fun removeProductFromCart(productId: String) {
        val currentCartItems = _cartItems.value.toMutableList()
        val itemIndex = currentCartItems.indexOfFirst { it.id == productId }

        if (itemIndex >= 0) {
            val existingItem = currentCartItems[itemIndex]
            if (existingItem.quantity > 1) {
                // Si la cantidad es mayor a 1, se reduce la cantidad
                currentCartItems[itemIndex] = existingItem.copy(quantity = existingItem.quantity - 1)
            } else {
                // Si la cantidad es 1, se elimina el producto del carrito
                currentCartItems.removeAt(itemIndex)
            }
            _cartItems.value = currentCartItems  // Actualiza el estado del carrito

            // Guarda el carrito actualizado en Firestore
            saveUserCart()
        }
    }

    // Función para limpiar todo el carrito
    fun clearCart() {
        val user = auth.currentUser ?: return  // Si no hay usuario autenticado, no se realiza ninguna acción

        // Limpia el carrito en Firestore
        db.collection("users").document(user.uid).collection("cart")
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    db.collection("users").document(user.uid).collection("cart").document(doc.id).delete()
                }
            }
            .addOnFailureListener {
                // Manejo de errores si es necesario
            }

        // Limpia el carrito en el estado de la aplicación
        _cartItems.value = emptyList()
    }

    // Función privada para limpiar el carrito de un usuario en Firestore
    private fun clearUserCart() {
        val user = auth.currentUser ?: return

        db.collection("users").document(user.uid).collection("cart")
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    db.collection("users").document(user.uid).collection("cart").document(doc.id).delete()
                }
            }
            .addOnFailureListener {
                // Manejo de errores si es necesario
            }
    }
}