package com.betojc.app.inventory.controller;

import com.betojc.app.inventory.dto.LotDTO;
import com.betojc.app.inventory.model.Lot;
import com.betojc.app.inventory.service.LotService;
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
@CrossOrigin
@RequestMapping("/api/lotes")
@Validated
public class LotController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LotController.class);

    @Autowired
    private LotService lotService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/{id}")
    public ResponseEntity<LotDTO> getLotById(@PathVariable String id) {
        try {
            LOGGER.info("Recibida solicitud para obtener el lote con ID: {}", id);
            LotDTO lot = lotService.getLotById(id);
            if (lot != null) {
                LOGGER.info("Lote encontrado: {}", lot.toString());
                return ResponseEntity.ok(lot);
            } else {
                LOGGER.warn("Lote con ID {} no encontrado", id);
                return ResponseEntity.notFound().build();
            }
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error al obtener el lote con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<LotDTO>> getAllLots(
            @RequestParam(value = "fecha", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha)
            throws ExecutionException, InterruptedException {
        if (fecha == null) {
            fecha = LocalDate.now();
        }
        List<LotDTO> lots = lotService.getAllLots(fecha);
        return ResponseEntity.ok(lots);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addLot(@Valid @RequestBody Lot lot) {
        try {
            LOGGER.info("Recibida solicitud para agregar un nuevo lote: {}", lot.toString());
            lotService.addLot(lot);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lote agregado exitosamente");
            response.put("lot", lot);

            messagingTemplate.convertAndSend("/topic/lotes", "Se añadio el Lote: " + lot);

            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error al agregar el lote: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al agregar el lote: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/eliminar")
    public ResponseEntity<Map<String, Object>> deleteLot(@RequestBody Map<String, String> requestBody) {
        String lotId = requestBody.get("id");
        try {
            LOGGER.info("Recibida solicitud para eliminar el lote con ID: {}", lotId);
            lotService.deleteLot(lotId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Lote con ID " + lotId + " ha sido eliminado exitosamente.");

            messagingTemplate.convertAndSend("/topic/lotes", "Se elimino el Lote: " + lotId);

            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error al eliminar el lote con ID {}: {}", lotId, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al eliminar el lote.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateLot(@Valid @RequestBody Lot lot, @PathVariable String id) {
        try {
            LOGGER.info("Recibida solicitud para actualizar el lote con ID: {} con los siguientes cambios: {}", id,
                    lot);

            // Actualizar lote
            lotService.updateLot(id, lot);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lote con ID " + id + " ha sido actualizado exitosamente.");

            messagingTemplate.convertAndSend("/topic/lotes", "Se atualizo el Lote: " + lot);

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);

        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error al actualizar el lote con ID {}: {}", id, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al actualizar el lote.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }
    }
}
