package com.smovistar.authservice.auth_service.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.smovistar.authservice.auth_service.domain.usuarios.DatosAutenticacionUsuario;
import com.smovistar.authservice.auth_service.domain.usuarios.Usuario;
import com.smovistar.authservice.auth_service.infraestructure.security.DatosJWTtoken;
import com.smovistar.authservice.auth_service.infraestructure.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/seguridad")
public class AutenticacionController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TokenService tokenService;

	private final Firestore dbFirestore;

	@Autowired
	public AutenticacionController(Firestore dbFirestore) {
		this.dbFirestore = dbFirestore;
	}

	@PostMapping("/autenticar")
	public ResponseEntity<DatosJWTtoken> autenticarUsuario(@RequestBody @Valid DatosAutenticacionUsuario usuario) {
		// Creamos el token para autenticación
		Authentication authToken = new UsernamePasswordAuthenticationToken(usuario.email(), usuario.contrasena());

		Authentication usuarioAuth = authenticationManager.authenticate(authToken);

		Usuario usuarioPrincipal = (Usuario) usuarioAuth.getPrincipal();
		String jwtToken = tokenService.generarToken(usuarioPrincipal);

		return ResponseEntity.ok(new DatosJWTtoken(jwtToken));
	}

	@GetMapping("/autorizar")
	public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
		try {
			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of("valid", false, "error", "Missing or malformed Authorization header"));
			}

			String token = authHeader.replace("Bearer ", "");
			DecodedJWT jwt = tokenService.decodeToken(token);
			String email = jwt.getSubject();

			// Verificar si el usuario sigue activo y no eliminado
			ApiFuture<QuerySnapshot> future = dbFirestore.collection("usuarios").whereEqualTo("email", email).get();
			List<QueryDocumentSnapshot> documents = future.get().getDocuments();

			if (documents.isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of("valid", false, "error", "Usuario no encontrado"));
			}

			DocumentSnapshot document = documents.get(0);
			Boolean eliminado = document.getBoolean("eliminado");
			Boolean estadoSuscripcion = document.getBoolean("estadoSuscripcion");

			if (Boolean.TRUE.equals(eliminado) || Boolean.FALSE.equals(estadoSuscripcion)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of("valid", false, "error", "Usuario eliminado o sin suscripción activa"));
			}

			return ResponseEntity.ok(Map.of(
					"valid", true,
					"email", jwt.getSubject(),
					"id", jwt.getClaim("id").asString(),
					"rol", jwt.getClaim("rol").asString()
			));

		} catch (RuntimeException ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("valid", false, "error", ex.getMessage()));
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
