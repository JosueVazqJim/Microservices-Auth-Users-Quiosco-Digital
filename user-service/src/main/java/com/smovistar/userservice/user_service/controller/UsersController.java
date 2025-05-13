package com.smovistar.userservice.user_service.controller;

import com.smovistar.userservice.user_service.domain.usuario.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsersController {
	@Autowired
	private UsuarioService logicaUsuario;

	@GetMapping
	public ResponseEntity<List<DatosRespuestaUsuario>> listarUsuarios(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		var respuesta = logicaUsuario.obtenerUsuariosNoEliminadosSiSuscripcion(page, size);
		return ResponseEntity.ok(respuesta);
	}
}
