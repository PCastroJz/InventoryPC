package com.betojc.app.inventory.controller;

import com.betojc.app.inventory.dto.ShipDTO;
import com.betojc.app.inventory.model.Ship;
import com.betojc.app.inventory.service.ShipService;
import com.betojc.app.inventory.service.ShipService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/envios")
@CrossOrigin
@Validated
public class ShipController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShipController.class);

    @Autowired
    private ShipService shipService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/{id}")
    public ResponseEntity<ShipDTO> getShipById(@PathVariable String id) {
        try {
            LOGGER.info("Recibida solicitud para obtener el envio con ID: {}", id);
            ShipDTO ship = shipService.getShipById(id);
            if (ship != null) {
                LOGGER.info("Envio encontrado: {}", ship.toString());
                return ResponseEntity.ok(ship);
            } else {
                LOGGER.warn("Envio con ID {} no encontrado", id);
                return ResponseEntity.notFound().build();
            }
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error al obtener el envio con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ShipDTO>> getAllShips(
            @RequestParam(value = "fecha", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha)
            throws ExecutionException, InterruptedException {
        if (fecha == null) {
            fecha = LocalDate.now();
        }
        List<ShipDTO> ships = shipService.getAllShips(fecha);
        return ResponseEntity.ok(ships);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addShip(@Valid @RequestBody Ship ship) {
        try {
            LOGGER.info("Recibida solicitud para agregar un nuevo envio: {}", ship.toString());
            shipService.addShip(ship);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Envio agregado exitosamente");
            response.put("ship", ship);

            messagingTemplate.convertAndSend("/topic/lotes", "Se añadio el envio: " + ship);

            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error al agregar el envio: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al agregar el envio: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/eliminar")
    public ResponseEntity<Map<String, Object>> deleteShip(@RequestBody Map<String, String> requestBody) {
        String shipId = requestBody.get("id");
        try {
            LOGGER.info("Recibida solicitud para eliminar el envio con ID: {}", shipId);
            shipService.deleteShip(shipId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Envio con ID " + shipId + " ha sido eliminado exitosamente.");

            messagingTemplate.convertAndSend("/topic/lotes", "Se elimino el envio: " + shipId);

            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error al eliminar el envio con ID {}: {}", shipId, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al eliminar el envio.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateShip(@Valid @RequestBody Ship ship, @PathVariable String id) {
        try {
            LOGGER.info("Recibida solicitud para actualizar el envio con ID: {} con los siguientes cambios: {}", id,
                    ship);

            // Actualizar envio
            shipService.updateShip(id, ship);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Envio con ID " + id + " ha sido actualizado exitosamente.");

            messagingTemplate.convertAndSend("/topic/lotes", "Se actualizo el envio: " + ship);

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);

        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error al actualizar el envio con ID {}: {}", id, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al actualizar el envio.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }
    }
}
