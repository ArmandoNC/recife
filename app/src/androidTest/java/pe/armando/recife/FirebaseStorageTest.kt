package pe.armando.recife

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FirebaseStorageTest {

    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference.child("product_images/pc.jpg")

    @Test
    fun testFirebaseStorageDownloadUrl() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // Ensure the app context is not null
        assertNotNull(appContext)

        // Attempt to download the URL of a file
        val urlTask = storageRef.downloadUrl
        urlTask.addOnSuccessListener { uri ->
            assertNotNull(uri)
            println("Download URL: $uri")
        }.addOnFailureListener { exception ->
            assert(false) { "Failed to get download URL: ${exception.message}" }
        }

        // Await the task to ensure it completes for the test
        runBlocking {
            urlTask.await()
        }
    }
}