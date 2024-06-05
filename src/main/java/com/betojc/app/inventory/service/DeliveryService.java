package com.betojc.app.inventory.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betojc.app.inventory.dto.DeliveryDTO;
import com.betojc.app.inventory.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class DeliveryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryService.class);

    public DeliveryDTO getDeliveryById(String deliveryId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot doc = db.collection("Repartidores").document(deliveryId).get().get();

        if (doc.exists()) {
            Delivery delivery = doc.toObject(Delivery.class);
            return new DeliveryDTO(deliveryId, delivery);
        } else {
            LOGGER.warn("Repartidor con ID {} no encontrado", deliveryId);
            return null; // Repartidor no encontrado
        }
    }

    public List<DeliveryDTO> getAllDeliveries() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        List<DeliveryDTO> deliveryDTOs = new ArrayList<>();

        ApiFuture<QuerySnapshot> querySnapshot = db.collection("Repartidores").get();
        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            Delivery delivery = document.toObject(Delivery.class);
            String deliveryId = document.getId();
            deliveryDTOs.add(new DeliveryDTO(deliveryId, delivery));
        }

        return deliveryDTOs;
    }

    public void addDelivery(Delivery delivery) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference deliveryRef = db.collection("Repartidores");

        // Comprobar si ya existe un repartidor con el mismo nombre
        ApiFuture<QuerySnapshot> future = deliveryRef.whereEqualTo("nombre", delivery.getNombre()).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        if (!documents.isEmpty()) {
            // Repartidor ya existe
            LOGGER.warn("Repartidor con nombre {} ya existe en la base de datos", delivery.getNombre());
            throw new IllegalArgumentException("Repartidor con nombre " + delivery.getNombre() + " ya existe.");
        } else {
            // Agregar un nuevo documento con un ID automático
            ApiFuture<DocumentReference> addFuture = deliveryRef.add(delivery);
            DocumentReference documentRef = addFuture.get();
            LOGGER.info("Repartidor agregado con ID: {}", documentRef.getId());
        }
    }

    public void deleteDelivery(String deliveryId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        // Eliminar el documento con el ID proporcionado de la colección "Repartidores"
        ApiFuture<WriteResult> deleteFuture = db.collection("Repartidores").document(deliveryId).delete();
        deleteFuture.get(); // Esperar a que se complete la operación de eliminación
        LOGGER.info("Repartidor con ID {} eliminado exitosamente", deliveryId);
    }

    public void updateDelivery(String deliveryId, Delivery delivery) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference deliveryRef = db.collection("Repartidores").document(deliveryId);

        // Obtener el nombre del repartidor antes de la actualización
        DocumentSnapshot documentSnapshot = deliveryRef.get().get();
        String oldName = documentSnapshot.getString("nombre");

        ApiFuture<WriteResult> updateFuture = deliveryRef.set(delivery, SetOptions.merge());
        updateFuture.get(); // Esperar a que se complete la actualización

        LOGGER.info("Repartidor con ID {} actualizado exitosamente", deliveryId);

        // Actualizar el nombre del repartidor en los envíos relacionados
        updateRepartidorNameInShips(oldName, delivery.getNombre());
    }

    private void updateRepartidorNameInShips(String oldRepartidorName, String newRepartidorName)
            throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference shipsRef = db.collection("Envios");

        ApiFuture<QuerySnapshot> querySnapshot = shipsRef.whereEqualTo("repartidor", oldRepartidorName).get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        for (QueryDocumentSnapshot document : documents) {
            DocumentReference shipRef = document.getReference();
            ApiFuture<WriteResult> future = shipRef.update("repartidor", newRepartidorName);
            future.get(); // Esperar a que se complete la actualización
            LOGGER.info("Envío con ID {} actualizado con nuevo nombre de repartidor {}", shipRef.getId(),
                    newRepartidorName);
        }
    }
}
