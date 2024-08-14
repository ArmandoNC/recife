package pe.armando.recife.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    // Instancia de ProfileRepository, que maneja la interacción con Firebase para obtener y guardar datos del perfil de usuario.
    private val repository = ProfileRepository(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance())

    // Estado interno que contiene la información del usuario. Es mutable y se actualiza a medida que se obtienen o guardan datos.
    private val _user = MutableStateFlow<User?>(null)

    // Estado expuesto que permite observar la información del usuario. Es de solo lectura para las vistas.
    val user: StateFlow<User?> = _user

    // Bloque de inicialización que se ejecuta cuando el ViewModel se crea, invocando la función para cargar el perfil del usuario.
    init {
        loadProfile()
    }

    // Función privada que carga el perfil del usuario desde Firebase usando el repositorio.
    private fun loadProfile() {
        viewModelScope.launch {
            // Se llama a la función suspendida getUserProfile del repositorio para obtener la información del usuario actual.
            val currentUser = repository.getUserProfile()
            // Actualiza el estado interno con la información obtenida del usuario.
            _user.value = currentUser
        }
    }

    // Función pública que guarda el perfil del usuario con la información proporcionada. Incluye callbacks de éxito y error.
    fun saveProfile(
        name: String,
        email: String,
        address: String,
        department: String,
        province: String,
        district: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Ejecuta la lógica de guardado en un hilo de corrutina.
        viewModelScope.launch {
            try {
                // Obtiene el ID del usuario actual. Si no está disponible, simplemente retorna.
                val userId = _user.value?.id ?: FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

                // Crea un objeto de tipo User con la información proporcionada.
                val updatedUser = User(
                    id = userId,
                    name = name,
                    email = email,
                    address = address,
                    department = department,
                    province = province,
                    district = district
                )

                // Llama al repositorio para guardar el perfil actualizado del usuario en Firebase.
                repository.saveUserProfile(updatedUser)

                // Actualiza el estado interno con la nueva información del usuario.
                _user.value = updatedUser

                // Llama al callback de éxito.
                onSuccess()
            } catch (e: Exception) {
                // En caso de error, llama al callback de error con el mensaje de error.
                onError(e.message ?: "Error saving profile")
            }
        }
    }
}