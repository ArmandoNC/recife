package pe.armando.recife.ui.orders
import com.google.firebase.Timestamp

// Data class que representa una orden.
data class Order(
    val id: String = "",                      // ID único de la orden. Por defecto, es una cadena vacía.
    val date: Timestamp = Timestamp.now(),    // Fecha y hora de la orden. Se inicializa con la fecha y hora actuales.
    val totalAmount: Double = 0.0,            // Monto total de la orden. Por defecto, es 0.0.
    val status: String = "",                  // Estado de la orden (por ejemplo: "Pendiente", "Entregado"). Por defecto, es una cadena vacía.
    val items: List<OrderItem> = emptyList()  // Lista de productos (OrderItem) que forman parte de la orden. Por defecto, es una lista vacía.
)

// Data class que representa un ítem dentro de una orden.
data class OrderItem(
    val id: String = "",                      // ID único del producto. Por defecto, es una cadena vacía.
    val name: String = "",                    // Nombre del producto. Por defecto, es una cadena vacía.
    val price: String = "",                   // Precio del producto en formato de cadena. Por defecto, es una cadena vacía.
    val quantity: Int = 0,                    // Cantidad del producto en la orden. Por defecto, es 0.
    val imageUrl: String = ""                 // URL de la imagen del producto. Por defecto, es una cadena vacía.
)
