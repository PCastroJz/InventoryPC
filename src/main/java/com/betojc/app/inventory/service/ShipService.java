package com.betojc.app.inventory.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betojc.app.inventory.dto.ShipDTO;
import com.betojc.app.inventory.model.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class ShipService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShipService.class);

    public ShipDTO getShipById(String shipId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot doc = db.collection("Envios").document(shipId).get().get();

        if (doc.exists()) {
            Ship ship = doc.toObject(Ship.class);
            return new ShipDTO(shipId, ship);
        } else {
            LOGGER.warn("Envio con ID {} no encontrado", shipId);
            return null; // Envio no encontrado
        }
    }

    public List<ShipDTO> getAllShips(LocalDate date) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        List<ShipDTO> ShipDTOs = new ArrayList<>();

        // Obtener la fecha del día especificado
        Date startOfDay = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfDay = Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Crear la consulta para obtener los lotes del día especificado
        ApiFuture<QuerySnapshot> querySnapshot = db.collection("Envios")
                .whereGreaterThanOrEqualTo("fecha", startOfDay)
                .whereLessThan("fecha", endOfDay)
                .get();

        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            Ship ship = document.toObject(Ship.class);
            String shipId = document.getId();
            ShipDTOs.add(new ShipDTO(shipId, ship));
        }

        return ShipDTOs;
    }

    public void addShip(Ship ship) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference shipRef = db.collection("Envios");

        // Agregar un nuevo documento con un ID automático
        ApiFuture<DocumentReference> future = shipRef.add(ship);

        // Esperar la operación asincrónica para obtener el ID del nuevo documento
        DocumentReference documentRef = future.get();

        LOGGER.info("Envio agregado con ID: {}", documentRef.getId());
    }

    public void deleteShip(String shipId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        // Eliminar el documento con el ID proporcionado de la colección "Envios"
        ApiFuture<WriteResult> deleteFuture = db.collection("Envios").document(shipId).delete();
        deleteFuture.get(); // Esperar a que se complete la operación de eliminación
        LOGGER.info("Envio con ID {} eliminado exitosamente", shipId);
    }

    public void updateShip(String id, Ship ship) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        // Convertir el objeto Ship a un mapa
        Map<String, Object> updates = new HashMap<>();
        if (ship.getProducto() != null) {
            updates.put("producto", ship.getProducto());
        }
        if (ship.getCantidad() != 0) {
            updates.put("cantidad", ship.getCantidad());
        }
        if (ship.getFecha() != null) {
            updates.put("fecha", ship.getFecha());
        }
        if (ship.getRepartidor() != null) {
            updates.put("repartidor", ship.getRepartidor());
        }
        if (ship.getJabas() != 0) {
            updates.put("jabas", ship.getJabas());
        }

        // Obtener la referencia al documento del lote
        DocumentReference shipRef = db.collection("Envios").document(id);

        // Actualizar el documento del lote con los campos proporcionados
        ApiFuture<WriteResult> updateFuture = shipRef.set(updates, SetOptions.merge());
        updateFuture.get(); // Esperar a que se complete la actualización

        LOGGER.info("Envio con ID {} actualizado exitosamente", id);
    }
}
