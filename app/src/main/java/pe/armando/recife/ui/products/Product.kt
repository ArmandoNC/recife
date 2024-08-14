package pe.armando.recife.ui.products

data class Product(
    val id: String = "",             // ID único del producto, utilizado para identificarlo en la base de datos.
    val name: String = "",           // Nombre del producto. Este es el nombre que se muestra a los usuarios.
    val price: Double = 0.0,         // Precio del producto. Representa el costo del producto en la tienda.
    val imageUrl: String = "",       // URL de la imagen del producto. Esta es la imagen que se muestra en la interfaz de usuario.
    val quantity: Int = 0,           // Cantidad disponible del producto. Representa el inventario disponible.
    val description: String = ""     // Descripción del producto. Proporciona detalles adicionales sobre el producto.
)
