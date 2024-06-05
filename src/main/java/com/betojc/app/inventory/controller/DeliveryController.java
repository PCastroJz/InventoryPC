package com.betojc.app.inventory.controller;

import com.betojc.app.inventory.service.DeliveryService;
import com.betojc.app.inventory.dto.DeliveryDTO;
import com.betojc.app.inventory.model.Delivery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@CrossOrigin
@RequestMapping("/api/delivery")
public class DeliveryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryController.class);

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryDTO> getDeliveryById(@PathVariable String id) {
        try {
            LOGGER.info("Recibida solicitud para obtener el repartidor con ID: {}", id);
            DeliveryDTO delivery = deliveryService.getDeliveryById(id);
            if (delivery != null) {
                LOGGER.info("Repartidor encontrado: {}", delivery.toString());
                return ResponseEntity.ok(delivery);
            } else {
                LOGGER.warn("Repartidor con ID {} no encontrado", id);
                return ResponseEntity.notFound().build(); // Repartidor no encontrado
            }
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error al obtener el repartidor con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<DeliveryDTO>> getAllDelivery() {
        try {
            List<DeliveryDTO> delivery = deliveryService.getAllDeliveries();
            return ResponseEntity.ok(delivery);
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error al obtener todos los delivery: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> addDelivery(@Valid @RequestBody Delivery delivery) {
        try {
            LOGGER.info("Recibida solicitud para agregar un nuevo repartidor: {}", delivery.toString());
            deliveryService.addDelivery(delivery);

            // Construir la respuesta JSON con los detalles del repartidor agregado
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Repartidor agregado exitosamente");
            response.put("delivery", delivery); // Agregar los detalles del repartidor

            messagingTemplate.convertAndSend("/topic/delivery", "Se añadio al repartidor: " + delivery);

            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error al agregar el repartidor: {}", e.getMessage());

            // En caso de error, construir la respuesta JSON con el mensaje de error
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al agregar el repartidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/eliminar")
    public ResponseEntity<String> deleteDelivery(@RequestBody Map<String, String> requestBody) {
        String deliveryId = requestBody.get("id");
        try {
            LOGGER.info("Recibida solicitud para eliminar el repartidor con ID: {}", deliveryId);
            deliveryService.deleteDelivery(deliveryId);

            messagingTemplate.convertAndSend("/topic/delivery", "Se elimino al repartidor: " + deliveryId);

            return ResponseEntity.ok("Repartidor con ID " + deliveryId + " ha sido eliminado exitosamente.");
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error al eliminar el repartidor con ID {}: {}", deliveryId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el repartidor.");
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateDelivery(@Valid @RequestBody Delivery delivery, @PathVariable String id) {
        try {
            LOGGER.info("Recibida solicitud para actualizar el repartidor con ID: {} con los siguientes cambios: {}", id,
                    delivery);
            deliveryService.updateDelivery(id, delivery);

            // Construir la respuesta JSON
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Repartidor con ID " + id + " ha sido actualizado exitosamente.");

            messagingTemplate.convertAndSend("/topic/delivery", "Se actualizo al repartidor: " + delivery);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error al actualizar el repartidor con ID {}: {}", id, e.getMessage());

            // En caso de error, construir la respuesta JSON con el mensaje de error
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al actualizar el repartidor.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }
    }
}
