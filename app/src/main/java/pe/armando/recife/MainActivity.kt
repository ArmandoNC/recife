@file:Suppress("DEPRECATION")

package pe.armando.recife

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import pe.armando.recife.ui.theme.RecifeTheme
import pe.armando.recife.navigation.NavGraph
import pe.armando.recife.navigation.Destinations

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {

    // Variables para la autenticación de Firebase, inicio de sesión con Google y la base de datos Firestore.
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Habilita el diseño de borde a borde (oculta la barra de estado).

        // Inicializa Firebase Auth
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Configura Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Solicita el ID token del cliente web predeterminado.
            .requestEmail() // Solicita el correo electrónico del usuario.
            .build()

        // Inicializa el cliente de Google Sign In con las opciones configuradas.
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Configura la interfaz de usuario usando Jetpack Compose.
        setContent {
            RecifeApp(auth, db, ::signInWithGoogle, ::signOut)
        }

        // Obtiene el token de registro de Firebase Cloud Messaging (FCM).
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Si tiene éxito, registra el token.
            val token = task.result
            Log.d("FCM", "Token: $token")
        }
    }

    // Maneja el resultado del inicio de sesión con Google.
    @Deprecated("This method has been deprecated in favor of using the Activity Result API...")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                // Autentica con Firebase usando el ID Token de Google.
                firebaseAuthWithGoogle(account.idToken!!) { user ->
                    saveUserProfile(user) {
                        // Configura de nuevo la interfaz de usuario con el usuario autenticado.
                        setContent {
                            RecifeApp(auth, db, ::signInWithGoogle, ::signOut)
                        }
                    }
                }
            } catch (e: ApiException) {
                Log.e(TAG, "Google sign in failed", e)
            }
        }
    }

    // Inicia el proceso de inicio de sesión con Google.
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // Autentica con Firebase usando el ID Token de Google.
    private fun firebaseAuthWithGoogle(idToken: String, onSuccess: (FirebaseUser) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    task.result?.user?.let {
                        onSuccess(it) // Llama a la función de éxito si la autenticación fue exitosa.
                    }
                } else {
                    Log.e(TAG, "Google sign in failed: ${task.exception?.message}")
                }
            }
    }

    // Guarda el perfil del usuario en Firestore.
    @RequiresApi(Build.VERSION_CODES.N)
    private fun saveUserProfile(user: FirebaseUser, onComplete: () -> Unit) {
        val userProfileRef = db.collection("users").document(user.uid)
        userProfileRef.get().addOnSuccessListener { document ->
            val userProfile = document.data?.toMutableMap() ?: mutableMapOf()
            userProfile["name"] = user.displayName ?: userProfile["name"]
            userProfile["email"] = user.email ?: userProfile["email"]
            userProfile.putIfAbsent("address", "")
            userProfile.putIfAbsent("department", "")
            userProfile.putIfAbsent("province", "")
            userProfile.putIfAbsent("district", "")

            // Guarda los datos en Firestore.
            userProfileRef.set(userProfile)
                .addOnSuccessListener {
                    Log.d(TAG, "User profile saved successfully")
                    onComplete()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error saving user profile", e)
                    onComplete()
                }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Error retrieving user profile", e)
            onComplete()
        }
    }

    // Cierra la sesión tanto en Firebase como en Google.
    private fun signOut(onComplete: () -> Unit) {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener(this) {
            onComplete()
        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001 // Código de solicitud para el inicio de sesión.
        private const val TAG = "MainActivity" // Etiqueta de registro para depuración.
    }
}

// Composable principal que configura la estructura de navegación de la aplicación.
@Composable
fun RecifeApp(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    signInWithGoogle: () -> Unit,
    signOut: (onComplete: () -> Unit) -> Unit
) {
    val navController = rememberNavController() // Controlador de navegación.
    var isUserSignedIn by remember { mutableStateOf(auth.currentUser != null) } // Estado que indica si el usuario está autenticado.

    // Tema de la aplicación.
    RecifeTheme {
        NavGraph(
            navController = navController,
            startDestination = if (isUserSignedIn) Destinations.Home.route else Destinations.SignIn.route, // Destino inicial basado en la autenticación.
            isUserSignedIn = isUserSignedIn,
            onGoogleSignIn = signInWithGoogle,
            onSignOut = {
                signOut {
                    isUserSignedIn = false
                    navController.navigate(Destinations.SignIn.route) {
                        popUpTo(Destinations.Home.route) { inclusive = true } // Elimina las pantallas previas del backstack.
                    }
                }
            },
            db = db
        )
    }

    // Efecto que se ejecuta cuando cambia el usuario autenticado.
    LaunchedEffect(auth.currentUser) {
        isUserSignedIn = auth.currentUser != null
        if (isUserSignedIn) {
            navController.navigate(Destinations.Home.route) {
                popUpTo(Destinations.SignIn.route) { inclusive = true } // Elimina la pantalla de inicio de sesión del backstack.
            }
        }
    }
}