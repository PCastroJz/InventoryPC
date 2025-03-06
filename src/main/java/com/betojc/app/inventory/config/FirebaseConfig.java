package com.betojc.app.inventory.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

@Configuration
public class FirebaseConfig {

    private static final Logger LOGGER = Logger.getLogger(FirebaseConfig.class.getName());

    @Bean
    public FirebaseApp firebaseApp(@Value("${firebase.config.path}") String firebaseConfigPath) throws IOException {
        LOGGER.info("Inicializando Firebase...");

        FirebaseApp firebaseApp;
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream(firebaseConfigPath)))
                    .setDatabaseUrl("https://project1-a2c50-default-rtdb.firebaseio.com/")
                    .build();

            firebaseApp = FirebaseApp.initializeApp(options);
            LOGGER.info("Firebase inicializado correctamente.");
        } else {
            firebaseApp = FirebaseApp.getInstance();
            LOGGER.info("FirebaseApp ya estaba inicializado. Utilizando la instancia existente.");
        }

        return firebaseApp;
    }
}
