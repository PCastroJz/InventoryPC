package com.inventory.betojc.services;

import com.inventory.betojc.exceptions.ResourceNotFoundException;
import com.inventory.betojc.models.Token;
import com.inventory.betojc.models.User;
import com.inventory.betojc.repositories.UserRepository;
import com.inventory.betojc.security.JwtUtils;

import jakarta.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    public ResponseEntity<Map<String, Object>> acceder(String email, String password) {
        Map<String, Object> response = new HashMap<>();

        try {
            User usuario = userRepository.findByEmail(email);
            if (usuario == null) {
                response.put("mensaje", "Usuario no encontrado.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            if (!passwordEncoder.matches(password, usuario.getPassword())) {
                response.put("mensaje", "Contraseña incorrecta.");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            String token = jwtUtils.generateJwtToken(usuario); 

            response.put("mensaje", "Acceso exitoso.");
            response.put("usuario", usuario);
            response.put("token", token);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("mensaje", "Error al acceder: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> registrarUsuario(User usuario) {
        Map<String, Object> response = new HashMap<>();
        try {
            usuario.setValidEmail(false);

            String encodedPassword = passwordEncoder.encode(usuario.getPassword());
            usuario.setPassword(encodedPassword);

            User nuevoUsuario = userRepository.save(usuario);
            Token token = tokenService.generarTokenConfirmacion(nuevoUsuario);

            String enlaceConfirmacion = generarEnlaceConfirmacion(token);
            emailService.enviarCorreoHtml(usuario.getEmail(), "Confirma tu correo electrónico",
                    generarCuerpoCorreoHtml(enlaceConfirmacion));

            response.put("mensaje", "Usuario registrado exitosamente. Se ha enviado un correo electrónico de confirmación.");
            response.put("usuario", nuevoUsuario);
            logger.info("Usuario registrado: {}", nuevoUsuario.getEmail());
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (MessagingException e) {
            response.put("mensaje", "Error al enviar el correo electrónico: " + e.getMessage());
            logger.error("Error al registrar usuario: {}", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<User>> obtenerTodosLosUsuarios() {
        List<User> usuarios = userRepository.findAll();
        logger.info("Se obtuvieron todos los usuarios.");
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    public ResponseEntity<User> obtenerUsuarioPorId(Long id) {
        User usuario = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        logger.info("Se obtuvo el usuario con ID: {}", id);
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Object>> actualizarUsuario(Long id, User usuario) {
        Map<String, Object> response = new HashMap<>();
        try {
            User usuarioExistente = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    
            usuarioExistente.setFirstname(usuario.getFirstname());
            usuarioExistente.setLastname(usuario.getLastname());
    
            userRepository.save(usuarioExistente);
    
            response.put("mensaje", "Usuario actualizado exitosamente.");
            logger.info("Usuario actualizado con ID: {}", id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("mensaje", "Error al actualizar el usuario: " + e.getMessage());
            logger.error("Error al actualizar el usuario con ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, Object>> eliminarUsuario(Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            userRepository.deleteById(id);
            response.put("mensaje", "Usuario eliminado exitosamente.");
            logger.info("Usuario eliminado con ID: {}", id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("mensaje", "Error al eliminar el usuario: " + e.getMessage());
            logger.error("Error al eliminar usuario con ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String generarEnlaceConfirmacion(Token token) {
        return "http://localhost:8080/confirmar?token=" + token.getToken();
    }

    private String generarCuerpoCorreoHtml(String enlaceConfirmacion) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"es\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Confirma tu correo electrónico</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: sans-serif;\n" +
                "            line-height: 1.5;\n" +
                "        }\n" +
                "        .container {\n" +
                "            max-width: 600px;\n" +
                "            margin: 0 auto;\n" +
                "            padding: 20px;\n" +
                "            border: 1px solid #ddd;\n" +
                "        }\n" +
                "        .logo {\n" +
                "            text-align: center;\n" +
                "            margin-bottom: 20px;\n" +
                "            font-weight: 600;\n" +
                "            font-size: 40px;\n" +
                "        }\n" +
                "        .logo img {\n" +
                "            max-width: 200px;\n" +
                "        }\n" +
                "        .button {\n" +
                "            display: inline-block;\n" +
                "            padding: 10px 20px;\n" +
                "            background-color: #007bff;\n" +
                "            color: #fff;\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 5px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"logo\">\n" +
                "            Logo\n" + // Reemplaza con la etiqueta <img> si tienes un logo
                "        </div>\n" +
                "        <h1>Confirma tu correo electrónico</h1>\n" +
                "        <p>Se registro tu cuenta en el Sistema de Productos Caseros BetoJC, para poder acceder porfavor confirma tu correo:</p>\n" +
                "        <a href=\"" + enlaceConfirmacion + "\" class=\"button\">Confirmar correo electrónico</a>\n" +
                "        <p>Si no realizaste el registro, favor de ignorar este mensaje.</p>\n" +
                "    </div>\n" +
                " </body>\n" +
                "</html>";
    }
}