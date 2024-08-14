package pe.armando.recife.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.firestore.FirebaseFirestore
import pe.armando.recife.auth.SignInScreen
import pe.armando.recife.ui.cart.CartScreen
import pe.armando.recife.ui.cart.CartViewModel
import pe.armando.recife.ui.home.HomeScreen
import pe.armando.recife.ui.orders.OrderDetailScreen
import pe.armando.recife.ui.orders.OrderHistoryScreen
import pe.armando.recife.ui.payment.PaymentScreen
import pe.armando.recife.ui.products.ProductDetailScreen
import pe.armando.recife.ui.products.ProductListScreen
import pe.armando.recife.ui.profile.ProfileScreen

@Composable
fun NavGraph(
    navController: NavHostController, // Controlador de navegación para moverse entre diferentes pantallas
    startDestination: String = Destinations.SignIn.route, // Pantalla de inicio por defecto, en este caso la pantalla de inicio de sesión
    isUserSignedIn: Boolean, // Estado que indica si el usuario está autenticado
    onGoogleSignIn: () -> Unit, // Callback que se ejecuta cuando el usuario intenta iniciar sesión con Google
    onSignOut: () -> Unit, // Callback que se ejecuta cuando el usuario cierra sesión
    db: FirebaseFirestore // Referencia a la base de datos Firestore para acceder a los datos
) {
    // ViewModel para manejar los datos del carrito de compras
    val cartViewModel = remember { CartViewModel() }

    // NavHost contiene las definiciones de todas las rutas de navegación
    NavHost(
        navController = navController, // Controlador de navegación pasado como parámetro
        startDestination = startDestination // Se establece la pantalla de inicio (por defecto es SignInScreen)
    ) {
        // Definición de la ruta para la pantalla de inicio de sesión
        composable(Destinations.SignIn.route) {
            SignInScreen(
                onGoogleSignIn = onGoogleSignIn, // Se pasa la función de inicio de sesión con Google
                onSignOut = onSignOut, // Se pasa la función para cerrar sesión
                isUserSignedIn = isUserSignedIn, // Estado de autenticación del usuario
                navController = navController // Controlador de navegación
            )
        }

        // Definición de la ruta para la pantalla de inicio
        composable(Destinations.Home.route) {
            HomeScreen(navController = navController, onSignOut = onSignOut, db = db) // HomeScreen recibe el controlador de navegación, la función de cierre de sesión y la referencia a Firestore
        }

        // Definición de la ruta para la pantalla de lista de productos
        composable(Destinations.ProductList.route) {
            ProductListScreen(navController = navController, db = db) // ProductListScreen recibe el controlador de navegación y la referencia a Firestore
        }

        // Definición de la ruta para la pantalla de detalles del producto
        composable(
            route = Destinations.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType }) // Se espera un argumento "productId" de tipo String
        ) { backStackEntry ->
            // Se obtiene el "productId" de los argumentos de la navegación
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            // Se navega a la pantalla de detalles del producto
            ProductDetailScreen(productId = productId, navController = navController, cartViewModel = cartViewModel)
        }

        // Definición de la ruta para la pantalla del carrito de compras
        composable(Destinations.Cart.route) {
            CartScreen(
                cartItems = cartViewModel.cartItems.collectAsState().value, // Estado de los items en el carrito
                onCheckout = { /* Aquí se maneja el proceso de pago */ },
                onRemoveItem = { cartViewModel.removeProductFromCart(it) }, // Callback para eliminar un producto del carrito
                cartViewModel = cartViewModel, // Se pasa el ViewModel del carrito
                navController = navController, // Controlador de navegación
                onSignOut = onSignOut // Callback para cerrar sesión
            )
        }

        // Definición de la ruta para la pantalla de historial de pedidos
        composable(Destinations.OrderHistory.route) {
            OrderHistoryScreen(
                navController = navController, // Controlador de navegación
                onSignOut = onSignOut // Callback para cerrar sesión
            )
        }

        // Definición de la ruta para la pantalla de detalles del pedido
        composable(
            route = Destinations.OrderDetail.route,
            arguments = listOf(navArgument("orderId") { defaultValue = "1" }) // Se espera un argumento "orderId" de tipo String, con un valor por defecto de "1"
        ) { backStackEntry ->
            // Se obtiene el "orderId" de los argumentos de la navegación
            val orderId = backStackEntry.arguments?.getString("orderId") ?: return@composable
            // Se navega a la pantalla de detalles del pedido
            OrderDetailScreen(orderId = orderId, navController = navController, onSignOut = onSignOut)
        }

        // Definición de la ruta para la pantalla de perfil de usuario
        composable(Destinations.Profile.route) {
            ProfileScreen(navController = navController) // ProfileScreen recibe el controlador de navegación
        }

        // Definición de la ruta para la pantalla de pago
        composable("payment_screen") {
            PaymentScreen(
                navController = navController, // Controlador de navegación
                onSignOut = onSignOut, // Callback para cerrar sesión
                cartViewModel = cartViewModel // Se pasa el ViewModel del carrito
            )
        }

    }
}