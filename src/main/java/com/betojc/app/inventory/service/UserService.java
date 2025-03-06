package com.betojc.app.inventory.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.betojc.app.inventory.dto.UserDTO;
import com.betojc.app.inventory.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UserDTO getUserId(String userId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot doc = db.collection("Usuarios").document(userId).get().get();

        if (doc.exists()) {
            User user = doc.toObject(User.class);
            return new UserDTO(userId, user);
        } else {
            LOGGER.warn("Usuario con ID {} no encontrado", userId);
            return null;
        }
    }

    public List<UserDTO> getAllUsers() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        List<UserDTO> usersDto = new ArrayList<>();

        ApiFuture<QuerySnapshot> querySnapshot = db.collection("Usuarios").get();
        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            User user = document.toObject(User.class);
            String userId = document.getId();
            usersDto.add(new UserDTO(userId, user));
        }

        return usersDto;
    }

    public void addUser(User user) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference usersRef = db.collection("Usuarios");

        // Verificar si el nombre de usuario ya existe
        Query query = usersRef.whereEqualTo("username", user.getUsername());
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        if (!querySnapshot.get().isEmpty()) {
            LOGGER.error("El nombre de usuario '{}' ya existe", user.getUsername());
            throw new RuntimeException("El nombre de usuario '" + user.getUsername() + "' ya existe");
        }

        // Encriptar la contraseña antes de guardar
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Agregar un nuevo documento con un ID automático
        ApiFuture<DocumentReference> future = usersRef.add(user);

        // Esperar la operación asincrónica para obtener el ID del nuevo documento
        DocumentReference documentRef = future.get();

        LOGGER.info("Usuario agregado con ID: {}", documentRef.getId());
    }

    public void deleteUser(String userId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> deleteFuture = db.collection("Usuarios").document(userId).delete();
        deleteFuture.get(); // Esperar a que se complete la operación de eliminación
        LOGGER.info("Usuario con ID {} eliminado exitosamente", userId);
    }

    public void updateUser(String userId, User user) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference userRef = db.collection("Usuarios").document(userId);

        // Encriptar la contraseña antes de actualizar
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        ApiFuture<WriteResult> updateFuture = userRef.set(user, SetOptions.merge());
        updateFuture.get(); // Esperar a que se complete la actualización

        LOGGER.info("Usuario con ID {} actualizado exitosamente", userId);
    }

    public User validateUser(String username, String password) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference users = db.collection("Usuarios");
        Query query = users.whereEqualTo("username", username);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
    
        if (querySnapshot.get().isEmpty()) {
            return null;
        } else {
            DocumentSnapshot document = querySnapshot.get().getDocuments().get(0);
            User user = document.toObject(User.class);
    
            // Verificar si la contraseña en texto plano coincide con la contraseña encriptada
            if (passwordEncoder.matches(password, user.getPassword())) {
                return new User(user.getUsername(), user.getNombre(), null, user.getRol()); // No incluir la contraseña en la respuesta
            } else {
                return null;
            }
        }
    }
    
}
