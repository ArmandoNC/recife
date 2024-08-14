package pe.armando.recife.navigation

// Clase sellada que define los diferentes destinos de navegación en la aplicación.
// Cada destino tiene una ruta asociada que se utiliza para navegar entre pantallas.

sealed class Destinations(val route: String) {

    // Objeto que representa la pantalla de inicio de sesión (SignInScreen)
    object SignIn : Destinations("sign_in")

    // Objeto que representa la pantalla principal (HomeScreen)
    object Home : Destinations("home")

    // Objeto que representa la pantalla de lista de productos (ProductListScreen)
    object ProductList : Destinations("product_list")

    // Objeto que representa la pantalla de detalles del producto (ProductDetailScreen)
    // Esta pantalla requiere un parámetro 'productId' en su ruta.
    object ProductDetail : Destinations("product_detail/{productId}") {

        // Función auxiliar para crear la ruta completa con un 'productId' específico.
        // Se utiliza cuando se necesita navegar a esta pantalla con un producto en particular.
        fun createRoute(productId: String) = "product_detail/$productId"
    }

    // Objeto que representa la pantalla del carrito de compras (CartScreen)
    object Cart : Destinations("cart")

    // Objeto que representa la pantalla de historial de pedidos (OrderHistoryScreen)
    object OrderHistory : Destinations("order_history")

    // Objeto que representa la pantalla de detalles del pedido (OrderDetailScreen)
    // Esta pantalla requiere un parámetro 'orderId' en su ruta.
    object OrderDetail : Destinations("order_detail/{orderId}") {

        // Función auxiliar para crear la ruta completa con un 'orderId' específico.
        // Se utiliza cuando se necesita navegar a esta pantalla con un pedido en particular.
        fun createRoute(orderId: String) = "order_detail/$orderId"
    }

    // Objeto que representa la pantalla de perfil de usuario (ProfileScreen)
    object Profile : Destinations("profile")

    // Objeto que representa la pantalla de pago (PaymentScreen)
    object Payment : Destinations("payment_screen")
}