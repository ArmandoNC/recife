package pe.armando.recife
import com.google.firebase.auth.FirebaseAuth
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

class UserLoginTest  {
    @Test
    fun testUserLogin() {
        val auth = FirebaseAuth.getInstance()
        val email = "testuser@example.com"
        val password = "password123"

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Test passes if login is successful
                assertTrue(task.isSuccessful)
            } else {
                // Test fails if login is unsuccessful
                fail("User registration failed")
            }
        }
    }
}



@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("pe.armando.recife", appContext.packageName)
    }
}