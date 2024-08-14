package pe.armando.recife.ui.profile

// Esta clase de datos representa el modelo de un usuario en la aplicación.

data class User(
    val id: String = "",          // ID único del usuario. Se asigna un valor por defecto vacío.
    val name: String = "",        // Nombre completo del usuario. Por defecto está vacío.
    val email: String = "",       // Dirección de correo electrónico del usuario. Por defecto está vacío.
    val address: String = "",     // Dirección del usuario. Por defecto está vacío.
    val department: String = "",  // Departamento en el que reside el usuario. Por defecto está vacío.
    val province: String = "",    // Provincia en la que reside el usuario. Por defecto está vacío.
    val district: String = ""     // Distrito en el que reside el usuario. Por defecto está vacío.
)