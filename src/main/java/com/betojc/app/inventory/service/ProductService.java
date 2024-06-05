package com.betojc.app.inventory.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betojc.app.inventory.dto.ProductDTO;
import com.betojc.app.inventory.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    public ProductDTO getProductById(String productId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot doc = db.collection("Productos").document(productId).get().get();

        if (doc.exists()) {
            Product product = doc.toObject(Product.class);
            return new ProductDTO(productId, product);
        } else {
            LOGGER.warn("Producto con ID {} no encontrado", productId);
            return null; // Producto no encontrado
        }
    }

    public List<ProductDTO> getAllProducts() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        List<ProductDTO> productDTOs = new ArrayList<>();

        ApiFuture<QuerySnapshot> querySnapshot = db.collection("Productos").get();
        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            Product product = document.toObject(Product.class);
            String productId = document.getId();
            productDTOs.add(new ProductDTO(productId, product));
        }

        return productDTOs;
    }

    public void addProduct(Product product) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference productsRef = db.collection("Productos");

        // Comprobar si ya existe un producto con el mismo nombre
        ApiFuture<QuerySnapshot> future = productsRef.whereEqualTo("nombre", product.getNombre()).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        if (!documents.isEmpty()) {
            // Producto ya existe
            LOGGER.warn("Producto con nombre {} ya existe en la base de datos", product.getNombre());
            throw new IllegalArgumentException("Producto con nombre " + product.getNombre() + " ya existe.");
        } else {
            // Agregar un nuevo documento con un ID automático
            ApiFuture<DocumentReference> addFuture = productsRef.add(product);
            DocumentReference documentRef = addFuture.get();
            LOGGER.info("Producto agregado con ID: {}", documentRef.getId());
        }
    }

    public void deleteProduct(String productId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        // Eliminar el documento con el ID proporcionado de la colección "Productos"
        ApiFuture<WriteResult> deleteFuture = db.collection("Productos").document(productId).delete();
        deleteFuture.get(); // Esperar a que se complete la operación de eliminación
        LOGGER.info("Producto con ID {} eliminado exitosamente", productId);
    }

    public void updateProduct(String productId, Product product) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference productRef = db.collection("Productos").document(productId);

        // Obtener el nombre del producto antes de la actualización
        DocumentSnapshot documentSnapshot = productRef.get().get();
        String oldName = documentSnapshot.getString("nombre");

        ApiFuture<WriteResult> updateFuture = productRef.set(product, SetOptions.merge());
        updateFuture.get(); // Esperar a que se complete la actualización

        LOGGER.info("Producto con ID {} actualizado exitosamente", productId);

        // Actualizar el nombre del producto en los lotes relacionados
        updateProductNameInLots(oldName, product.getNombre());

        // Actualizar el nombre del producto en los envíos relacionados
        updateProductNameInShips(oldName, product.getNombre());
    }

    private void updateProductNameInLots(String oldProductName, String newProductName)
            throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference lotsRef = db.collection("Lotes");

        ApiFuture<QuerySnapshot> querySnapshot = lotsRef.whereEqualTo("producto", oldProductName).get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        for (QueryDocumentSnapshot document : documents) {
            DocumentReference lotRef = document.getReference();
            ApiFuture<WriteResult> future = lotRef.update("producto", newProductName);
            future.get(); // Esperar a que se complete la actualización
            LOGGER.info("Lote con ID {} actualizado con nuevo nombre de producto {}", lotRef.getId(), newProductName);
        }
    }

    private void updateProductNameInShips(String oldProductName, String newProductName)
            throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference shipsRef = db.collection("Envios");

        ApiFuture<QuerySnapshot> querySnapshot = shipsRef.whereEqualTo("producto", oldProductName).get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        for (QueryDocumentSnapshot document : documents) {
            DocumentReference shipRef = document.getReference();
            ApiFuture<WriteResult> future = shipRef.update("producto", newProductName);
            future.get(); // Esperar a que se complete la actualización
            LOGGER.info("Envío con ID {} actualizado con nuevo nombre de producto {}", shipRef.getId(), newProductName);
        }
    }
}
