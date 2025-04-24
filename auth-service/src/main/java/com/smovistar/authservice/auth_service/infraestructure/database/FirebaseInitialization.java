package com.smovistar.authservice.auth_service.infraestructure.database;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

//Archivo de conexion a Firebase, se usa el @Configuration para que lance el metodo al iniciar la aplicacion
@Configuration
public class FirebaseInitialization {

	@PostConstruct
	public void initializeFirebase() {
		try {
			// Load the service account file from resources
			InputStream serviceAccount = getClass().getClassLoader()
					.getResourceAsStream("serviceAccountKey.json");

			if (serviceAccount == null) {
				throw new IllegalStateException("serviceAccountKey.json not found in resources");
			}

			// Initialize Firebase
			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.build();

			if (FirebaseApp.getApps().isEmpty()) {
				FirebaseApp.initializeApp(options);
				System.out.println("Firebase initialized successfully");
			} else {
				System.out.println("Firebase already initialized");
			}
		} catch (IOException e) {
			throw new IllegalStateException("Failed to initialize Firebase", e);
		}
	}

	@Bean
	public Firestore getDb() {
		return FirestoreClient.getFirestore();
	}

}