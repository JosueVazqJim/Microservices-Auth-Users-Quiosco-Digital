package com.smovistar.authservice.auth_service.controller;

import com.smovistar.authservice.auth_service.domain.usuarios.DatosAutenticacionUsuario;
import com.smovistar.authservice.auth_service.domain.usuarios.Usuario;
import com.smovistar.authservice.auth_service.infraestructure.security.DatosJWTtoken;
import com.smovistar.authservice.auth_service.infraestructure.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticacionController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TokenService tokenService;

	@PostMapping
	public ResponseEntity<DatosJWTtoken> autenticarUsuario(@RequestBody @Valid DatosAutenticacionUsuario usuario) {
		Authentication authToken = new UsernamePasswordAuthenticationToken(usuario.email(), usuario.contrasena());
		var usuarioAtuh = authenticationManager.authenticate(authToken);
		System.out.printf(usuarioAtuh.toString());

		var JWTtoken = tokenService.generarToken((Usuario) usuarioAtuh.getPrincipal());
		return ResponseEntity.ok(new DatosJWTtoken(JWTtoken));
	}
}
