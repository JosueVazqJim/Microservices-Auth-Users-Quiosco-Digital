package com.smovistar.authservice.auth_service.infraestructure.security;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.smovistar.authservice.auth_service.domain.usuarios.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService implements UserDetailsService {

    private final Firestore dbFirestore;

    @Autowired
    public AuthenticationService(Firestore dbFirestore) {
        this.dbFirestore = dbFirestore;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            ApiFuture<QuerySnapshot> future = dbFirestore.collection("usuarios").whereEqualTo("email", username).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            if (documents.isEmpty()) {
                throw new UsernameNotFoundException("Usuario no encontrado en Firestore: " + username);
            }

            DocumentSnapshot document = documents.get(0); // Tomamos el primer resultado
            String id = document.getId();
            String email = document.getString("email");
            String password = document.getString("contrasena");
            Boolean eliminado = document.getBoolean("eliminado");
            Boolean estadoSuscripcion = document.getBoolean("estadoSuscripcion");
            String rol = document.getString("rol");
            System.out.println(document.getData());
            return new Usuario(id, email, password, eliminado, estadoSuscripcion, rol);

        } catch (Exception e) {
            throw new UsernameNotFoundException("Error al buscar el usuario en Firestore: " + username, e);
        }
    }
}