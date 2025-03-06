package com.betojc.app.inventory.controller;

import com.betojc.app.inventory.service.ProductService;
import com.betojc.app.inventory.dto.ProductDTO;
import com.betojc.app.inventory.model.Product;

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
@RequestMapping("/api/productos")
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable String id) {
        try {
            LOGGER.info("Recibida solicitud para obtener el producto con ID: {}", id);
            ProductDTO product = productService.getProductById(id);
            if (product != null) {
                LOGGER.info("Producto encontrado: {}", product.toString());
                return ResponseEntity.ok(product);
            } else {
                LOGGER.warn("Producto con ID {} no encontrado", id);
                return ResponseEntity.notFound().build(); // Producto no encontrado
            }
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error al obtener el producto con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProductsWithIds() {
        try {
            List<ProductDTO> products = productService.getAllProducts();
            return ResponseEntity.ok(products);
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error al obtener todos los productos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> addProduct(@Valid @RequestBody Product product) {
        try {
            LOGGER.info("Recibida solicitud para agregar un nuevo producto: {}", product.toString());
            productService.addProduct(product);

            // Construir la respuesta JSON con los detalles del producto agregado
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producto agregado exitosamente");
            response.put("product", product); // Agregar los detalles del producto

            messagingTemplate.convertAndSend("/topic/productos", "Nuevo producto agregado: " + product.toString());

            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error al agregar el producto: {}", e.getMessage());

            // En caso de error, construir la respuesta JSON con el mensaje de error
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al agregar el producto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/eliminar")
    public ResponseEntity<String> deleteProduct(@RequestBody Map<String, String> requestBody) {
        String productId = requestBody.get("id");
        try {
            LOGGER.info("Recibida solicitud para eliminar el producto con ID: {}", productId);
            productService.deleteProduct(productId);

            messagingTemplate.convertAndSend("/topic/productos", "Producto eliminado: " + productId);

            return ResponseEntity.ok("Producto con ID " + productId + " ha sido eliminado exitosamente.");
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error al eliminar el producto con ID {}: {}", productId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el producto.");
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateProduct(@Valid @RequestBody Product product, @PathVariable String id) {
        try {
            LOGGER.info("Recibida solicitud para actualizar el producto con ID: {} con los siguientes cambios: {}", id,
                    product);
            productService.updateProduct(id, product);

            // Construir la respuesta JSON
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producto con ID " + id + " ha sido actualizado exitosamente.");

            messagingTemplate.convertAndSend("/topic/productos", "Producto actualizado: " + product);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error al actualizar el producto con ID {}: {}", id, e.getMessage());

            // En caso de error, construir la respuesta JSON con el mensaje de error
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al actualizar el producto.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }
    }
}
