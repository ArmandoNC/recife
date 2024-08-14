package pe.armando.recife.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import pe.armando.recife.MainActivity
import pe.armando.recife.R

// Esta clase extiende FirebaseMessagingService y se encarga de recibir mensajes de Firebase Cloud Messaging (FCM).
class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Método que se invoca cuando se recibe un mensaje de FCM.
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Si el mensaje contiene una notificación (es decir, un título y un cuerpo), se envía una notificación.
        remoteMessage.notification?.let {
            sendNotification(it.title, it.body)  // Se llama a la función sendNotification con el título y el cuerpo de la notificación.
        }
    }

    // Método que se invoca cuando se genera un nuevo token para la instancia de FCM.
    // Este token se utiliza para identificar el dispositivo de forma única en los servidores de FCM.
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Aquí podrías agregar lógica para enviar el nuevo token al servidor de tu backend si fuera necesario.
    }

    // Función privada que se encarga de crear y mostrar una notificación en el dispositivo.
    private fun sendNotification(title: String?, message: String?) {
        // Se crea un Intent que abrirá MainActivity cuando se toque la notificación.
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK  // Se configuran las flags para iniciar una nueva tarea y limpiar la pila de actividades.
        }
        // Se crea un PendingIntent que encapsula el Intent anterior.
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE // Esta bandera asegura que el PendingIntent no pueda ser modificado una vez creado.
        )

        // Se construye la notificación utilizando NotificationCompat.Builder.
        val builder = NotificationCompat.Builder(this, "default")
            .setSmallIcon(R.drawable.ic_notification)  // Icono pequeño que se mostrará en la notificación.
            .setContentTitle(title)  // Título de la notificación.
            .setContentText(message)  // Texto del cuerpo de la notificación.
            .setPriority(NotificationCompat.PRIORITY_HIGH)  // Prioridad alta para que la notificación se muestre inmediatamente.
            .setContentIntent(pendingIntent)  // Se asocia el PendingIntent para que se ejecute al tocar la notificación.
            .setAutoCancel(true)  // La notificación se cancela automáticamente cuando se toca.

        // Se obtiene una instancia de NotificationManagerCompat para gestionar la notificación.
        val notificationManager = NotificationManagerCompat.from(this)

        // Si la versión de Android es Oreo o superior, se crea un canal de notificación.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "default",  // ID del canal.
                "Default Channel",  // Nombre del canal visible para el usuario.
                NotificationManager.IMPORTANCE_HIGH  // Importancia alta para notificaciones urgentes.
            ).apply {
                description = "Default Channel for App"  // Descripción del canal visible para el usuario.
            }
            notificationManager.createNotificationChannel(channel)  // Se crea el canal de notificación.
        }

        // Verificación de permisos para mostrar notificaciones.
        // A partir de Android 13 (API 33), las aplicaciones deben solicitar permiso para mostrar notificaciones.
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si no se tiene el permiso, aquí podrías solicitarlo al usuario.
            return  // Si no hay permisos, se sale de la función y no se muestra la notificación.
        }

        // Finalmente, se muestra la notificación utilizando el ID 0.
        // Cada vez que se muestra una nueva notificación con el mismo ID, esta reemplazará la anterior.
        notificationManager.notify(0, builder.build())
    }
}
