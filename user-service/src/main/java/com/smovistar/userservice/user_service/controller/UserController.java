package com.smovistar.userservice.user_service.controller;

import com.smovistar.userservice.user_service.domain.usuario.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/usuario")
public class UserController {
	@Autowired
	private UsuarioService logicaUsuario;

	@PostMapping
	public ResponseEntity<DatosRespuestaUsuario> registrarUsuario(@RequestBody @Valid DatosRegistroUsuario datos,
	                                                              UriComponentsBuilder uriComponentsBuilder) {
		var respuesta = logicaUsuario.registrar(datos);
		URI uri = uriComponentsBuilder.path("/usuarios/{email}").buildAndExpand(respuesta.email()).toUri();
		return ResponseEntity.created(uri).body(respuesta);
	}

	@GetMapping("/{email}")
	public ResponseEntity<DatosRespuestaUsuario> obtenerUsuario(@PathVariable String email) {
		var respuesta = logicaUsuario.obtener(email);
		return ResponseEntity.ok(respuesta);
	}

	@PutMapping("/{email}")
	public ResponseEntity<DatosRespuestaUsuario> actualizarUsuario(@PathVariable String email,
	                                                               @RequestBody @Valid DatosActualizarUsuario datos) {
		var respuesta = logicaUsuario.actualizar(email, datos);
		return ResponseEntity.ok(respuesta);
	}

	@DeleteMapping("/{email}")
	public ResponseEntity<Void> eliminarUsuario(@PathVariable String email) {
		logicaUsuario.eliminar(email);
		return ResponseEntity.noContent().build();
	}
}
