package com.betojc.app.inventory.controller;

import com.betojc.app.inventory.model.User;
import com.betojc.app.inventory.dto.UserDTO;
import com.betojc.app.inventory.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService usuarioService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> addUser(@Valid @RequestBody User user) {
        try {
            LOGGER.info("Recibida solicitud para agregar un nuevo usuario: {}", user);
            usuarioService.addUser(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario agregado exitosamente");
            response.put("Usuario", user);
            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error al agregar el usuario: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al agregar el usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> obtenerUsuario(@PathVariable String id) {
        try {
            LOGGER.info("Recibida solicitud para obtener el usuario con ID: {}", id);
            UserDTO user = usuarioService.getUserId(id);
            if (user != null) {
                LOGGER.info("Usuario encontrado: {}", user);
                return ResponseEntity.ok(user);
            } else {
                LOGGER.warn("Usuario con ID {} no encontrado", id);
                return ResponseEntity.notFound().build();
            }
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error al obtener el usuario con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> obtenerTodosLosUsuarios() {
        try {
            List<UserDTO> users = usuarioService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error al obtener todos los usuarios: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> editarUsuario(@PathVariable String id, @Valid @RequestBody User usuario) {
        try {
            LOGGER.info("Recibida solicitud para actualizar el usuario con ID: {} con los siguientes cambios: {}", id, usuario);
            usuarioService.updateUser(id, usuario);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario con ID " + id + " ha sido actualizado exitosamente.");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error al actualizar el usuario con ID {}: {}", id, e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al actualizar el usuario.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarUsuario(@PathVariable String id) {
        try {
            LOGGER.info("Recibida solicitud para eliminar el usuario con ID: {}", id);
            usuarioService.deleteUser(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario con ID " + id + " ha sido eliminado exitosamente.");
            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error al eliminar el usuario con ID {}: {}", id, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al eliminar el usuario.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        try {
            LOGGER.info("Recibida solicitud de inicio de sesión para el usuario: {}", username);
            User user = usuarioService.validateUser(username, password);
            if (user != null) {
                LOGGER.info("Usuario autenticado exitosamente: {}", user.getUsername());

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Inicio de sesión exitoso");
                response.put("user", user); // No incluir la contraseña en la respuesta

                return ResponseEntity.ok(response);
            } else {
                LOGGER.warn("Credenciales inválidas para el usuario: {}", username);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Credenciales inválidas");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error al iniciar sesión para el usuario {}: {}", username, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al iniciar sesión: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
