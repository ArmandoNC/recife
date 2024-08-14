package pe.armando.recife.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import pe.armando.recife.ui.products.Product

// Clase que actúa como un repositorio para gestionar las operaciones relacionadas con los productos en la base de datos de Firebase Firestore.
class ProductRepository(private val db: FirebaseFirestore) {

    // Función suspendida que obtiene todos los productos de la colección "products" en Firestore.
    // Devuelve una lista de objetos de tipo Product.
    suspend fun getAllProducts(): List<Product> {
        val products = mutableListOf<Product>()  // Lista mutable donde se almacenarán los productos.
        val result = db.collection("products").get().await()  // Se obtiene la colección "products" y se espera el resultado.
        for (document in result.documents) {  // Se itera sobre cada documento en el resultado.
            val product = document.toObject(Product::class.java)  // Se convierte el documento a un objeto de tipo Product.
            if (product != null) {  // Si la conversión es exitosa (es decir, no es null),
                products.add(product)  // se agrega el producto a la lista.
            }
        }
        return products  // Se devuelve la lista de productos.
    }

    // Función suspendida que agrega un nuevo producto a la colección "products" en Firestore.
    // Recibe como parámetro un objeto de tipo Product.
    suspend fun addProduct(product: Product) {
        db.collection("products").add(product).await()  // Se agrega el producto a la colección y se espera la finalización de la operación.
    }

    // Función suspendida que actualiza un producto existente en Firestore.
    // Recibe como parámetro un objeto de tipo Product que contiene el ID y los datos actualizados.
    suspend fun updateProduct(product: Product) {
        db.collection("products").document(product.id).set(product).await()  // Se actualiza el documento con el ID del producto y se espera la finalización.
    }

    // Función suspendida que elimina un producto de la colección "products" en Firestore.
    // Recibe como parámetro el ID del producto a eliminar.
    suspend fun deleteProduct(productId: String) {
        db.collection("products").document(productId).delete().await()  // Se elimina el documento con el ID especificado y se espera la finalización.
    }

    // Función suspendida que permite ejecutar una transacción en Firestore.
    // Recibe como parámetro un bloque de código que define las operaciones de la transacción.
    suspend fun runTransaction(transactionBlock: suspend (FirebaseFirestore) -> Unit) {
        db.runTransaction { transaction ->  // Se inicia una transacción.
            runBlocking {  // Se utiliza runBlocking para ejecutar el bloque de código dentro de la transacción de manera síncrona.
                transactionBlock(db)  // Se ejecuta el bloque de transacción pasando la referencia a la base de datos.
            }
        }.await()  // Se espera a que la transacción finalice.
    }
}
