package com.auto_care_test.data.firebase.firestore

import com.auto_care_test.domain.model.Mantenimiento
import com.auto_care_test.domain.model.Vehiculo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

private const val COLECCION_USUARIOS = "usuarios"
private const val COLECCION_VEHICULOS = "vehiculos"
private const val COLECCION_MANTENIMIENTOS = "mantenimientos"

class FirestoreDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // --- Vehículos ---

    suspend fun agregarVehiculo(uid: String, vehiculo: Vehiculo) {
        val ref = firestore.collection(COLECCION_USUARIOS)
            .document(uid)
            .collection(COLECCION_VEHICULOS)
            .document(vehiculo.idVehiculo.toString())
        ref.set(vehiculo).await()
    }

    fun obtenerVehiculos(uid: String): Flow<List<Vehiculo>> = callbackFlow {
        val ref = firestore.collection(COLECCION_USUARIOS)
            .document(uid)
            .collection(COLECCION_VEHICULOS)

        val listener = ref.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val vehiculos = snapshot?.documents?.mapNotNull { it.toObject(Vehiculo::class.java) } ?: emptyList()
            trySend(vehiculos)
        }
        awaitClose { listener.remove() }
    }

    suspend fun eliminarVehiculo(uid: String, idVehiculo: Int) {
        firestore.collection(COLECCION_USUARIOS)
            .document(uid)
            .collection(COLECCION_VEHICULOS)
            .document(idVehiculo.toString())
            .delete()
            .await()
    }

    // --- Mantenimientos ---

    suspend fun agregarMantenimiento(uid: String, mantenimiento: Mantenimiento) {
        val ref = firestore.collection(COLECCION_USUARIOS)
            .document(uid)
            .collection(COLECCION_MANTENIMIENTOS)
            .document(mantenimiento.idMantenimiento.toString())
        ref.set(mantenimiento).await()
    }

    fun obtenerMantenimientos(uid: String): Flow<List<Mantenimiento>> = callbackFlow {
        val ref = firestore.collection(COLECCION_USUARIOS)
            .document(uid)
            .collection(COLECCION_MANTENIMIENTOS)

        val listener = ref.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val mantenimientos = snapshot?.documents?.mapNotNull { it.toObject(Mantenimiento::class.java) } ?: emptyList()
            trySend(mantenimientos)
        }
        awaitClose { listener.remove() }
    }

    suspend fun actualizarMantenimiento(uid: String, mantenimiento: Mantenimiento) {
        val ref = firestore.collection(COLECCION_USUARIOS)
            .document(uid)
            .collection(COLECCION_MANTENIMIENTOS)
            .document(mantenimiento.idMantenimiento.toString())
        ref.set(mantenimiento).await()
    }

    suspend fun eliminarMantenimiento(uid: String, idMantenimiento: Int) {
        firestore.collection(COLECCION_USUARIOS)
            .document(uid)
            .collection(COLECCION_MANTENIMIENTOS)
            .document(idMantenimiento.toString())
            .delete()
            .await()
    }
}
