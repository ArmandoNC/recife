package pe.armando.recife.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import pe.armando.recife.ui.components.TopBarMenu

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = viewModel()
) {
    // Estado para manejar mensajes de error que se puedan generar al guardar el perfil.
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Observa el objeto `user` desde el ViewModel, que contiene la información del perfil del usuario.
    val user by viewModel.user.collectAsState()

    // Estados locales para almacenar los valores de los campos del formulario (nombre, email, dirección, etc.).
    var name by remember { mutableStateOf(user?.name ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var address by remember { mutableStateOf(user?.address ?: "") }
    var department by remember { mutableStateOf(user?.department ?: "") }
    var province by remember { mutableStateOf(user?.province ?: "") }
    var district by remember { mutableStateOf(user?.district ?: "") }

    // Actualiza los campos del formulario cuando los datos del usuario cambian.
    LaunchedEffect(user) {
        user?.let {
            name = it.name
            email = it.email
            address = it.address
            department = it.department
            province = it.province
            district = it.district
        }
    }

    Scaffold(
        topBar = {
            // Se usa el menú superior reutilizable `TopBarMenu` que incluye botones de navegación y cierre de sesión.
            TopBarMenu(navController = navController, onSignOut = { /* Acción de logout */ })
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize() // Ocupa todo el espacio disponible.
                    .padding(paddingValues) // Respeta el padding proporcionado por el Scaffold.
                    .padding(16.dp), // Agrega padding adicional.
                horizontalAlignment = Alignment.CenterHorizontally // Centra los elementos horizontalmente.
            ) {
                // Si hay un mensaje de error, lo muestra en la pantalla.
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error, // Estilo de texto en color de error.
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Campos de texto del formulario que permiten al usuario editar su perfil.
                TextField(
                    value = name,
                    onValueChange = { name = it }, // Actualiza el estado `name` cuando el usuario cambia el texto.
                    label = { Text("Name") }, // Etiqueta para el campo.
                    modifier = Modifier.fillMaxWidth() // El campo ocupa todo el ancho disponible.
                )
                Spacer(modifier = Modifier.height(8.dp)) // Espacio entre los campos.
                TextField(
                    value = email,
                    onValueChange = { email = it }, // Actualiza el estado `email` cuando el usuario cambia el texto.
                    label = { Text("Email") }, // Etiqueta para el campo.
                    modifier = Modifier.fillMaxWidth(), // El campo ocupa todo el ancho disponible.
                    enabled = false // El campo de email está deshabilitado, no puede ser modificado.
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("Department") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = province,
                    onValueChange = { province = it },
                    label = { Text("Province") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = district,
                    onValueChange = { district = it },
                    label = { Text("District") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Botón para guardar los cambios realizados en el formulario.
                Button(
                    onClick = {
                        // Llama a la función `saveProfile` del ViewModel para guardar los datos actualizados.
                        viewModel.saveProfile(
                            name, email, address, department, province, district,
                            onSuccess = { navController.popBackStack() }, // Si se guarda correctamente, vuelve atrás en la navegación.
                            onError = { errorMessage = it } // Si ocurre un error, muestra el mensaje de error.
                        )
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally) // Alinea el botón en el centro horizontalmente.
                ) {
                    Text("Save") // Texto del botón.
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(navController = rememberNavController())
}