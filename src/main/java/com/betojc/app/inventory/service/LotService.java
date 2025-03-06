package com.betojc.app.inventory.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betojc.app.inventory.dto.LotDTO;
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
public class LotService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LotService.class);

    public LotDTO getLotById(String lotId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot doc = db.collection("Lotes").document(lotId).get().get();

        if (doc.exists()) {
            Lot lot = doc.toObject(Lot.class);
            return new LotDTO(lotId, lot);
        } else {
            LOGGER.warn("Lote con ID {} no encontrado", lotId);
            return null; // Lote no encontrado
        }
    }

    public List<LotDTO> getAllLots(LocalDate date) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        List<LotDTO> lotDTOs = new ArrayList<>();

        // Obtener la fecha de inicio y fin del día especificado
        Date startOfDay = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfDay = Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Crear la consulta para obtener los lotes del día especificado
        ApiFuture<QuerySnapshot> querySnapshot = db.collection("Lotes")
                .whereGreaterThanOrEqualTo("inicio", startOfDay)
                .whereLessThan("inicio", endOfDay)
                .get();

        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            Lot lot = document.toObject(Lot.class);
            String lotId = document.getId();
            lotDTOs.add(new LotDTO(lotId, lot));
        }

        return lotDTOs;
    }

    public void addLot(Lot lot) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference lotsRef = db.collection("Lotes");

        // Agregar un nuevo documento con un ID automático
        ApiFuture<DocumentReference> future = lotsRef.add(lot);

        // Esperar la operación asincrónica para obtener el ID del nuevo documento
        DocumentReference documentRef = future.get();

        LOGGER.info("Lote agregado con ID: {}", documentRef.getId());
    }

    public void deleteLot(String lotId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        // Eliminar el documento con el ID proporcionado de la colección "Lotes"
        ApiFuture<WriteResult> deleteFuture = db.collection("Lotes").document(lotId).delete();
        deleteFuture.get(); // Esperar a que se complete la operación de eliminación
        LOGGER.info("Lote con ID {} eliminado exitosamente", lotId);
    }

    public void updateLot(String id, Lot lot) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        // Convertir el objeto Lot a un mapa
        Map<String, Object> updates = new HashMap<>();
        if (lot.getProducto() != null) {
            updates.put("producto", lot.getProducto());
        }
        if (lot.getCantidad() != 0) {
            updates.put("cantidad", lot.getCantidad());
        }
        if (lot.getInicio() != null) {
            updates.put("inicio", lot.getInicio());
        }
        if (lot.getFin() != null) {
            updates.put("fin", lot.getFin());
        }

        // Obtener la referencia al documento del lote
        DocumentReference lotRef = db.collection("Lotes").document(id);

        // Actualizar el documento del lote con los campos proporcionados
        ApiFuture<WriteResult> updateFuture = lotRef.set(updates, SetOptions.merge());
        updateFuture.get(); // Esperar a que se complete la actualización

        LOGGER.info("Lote con ID {} actualizado exitosamente", id);
    }
}
