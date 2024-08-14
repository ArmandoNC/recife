package pe.armando.recife.ui.profile

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfileRepository(
    private val db: FirebaseFirestore, // Instancia de Firebase Firestore para realizar operaciones en la base de datos.
    private val auth: FirebaseAuth // Instancia de FirebaseAuth para obtener información de autenticación del usuario.
) {

    // Función suspendida que obtiene el perfil del usuario autenticado.
    suspend fun getUserProfile(): User? {
        // Obtiene el ID del usuario actual autenticado. Si no hay un usuario autenticado, devuelve `null`.
        val userId = auth.currentUser?.uid ?: return null

        // Realiza una consulta a la colección "users" en Firestore para obtener el documento asociado con el ID del usuario.
        val document = db.collection("users").document(userId).get().await()

        // Convierte el documento obtenido en un objeto `User` y lo devuelve. Si no se puede convertir, devuelve `null`.
        return document.toObject(User::class.java)
    }

    // Función suspendida que guarda (o actualiza) el perfil del usuario en Firestore.
    suspend fun saveUserProfile(user: User) {
        // Obtiene el ID del usuario actual autenticado. Si no hay un usuario autenticado, la función retorna sin hacer nada.
        val userId = auth.currentUser?.uid ?: return

        // Guarda (o actualiza) el objeto `User` en la colección "users" en Firestore, asociándolo con el ID del usuario.
        db.collection("users").document(userId).set(user).await()
    }
}