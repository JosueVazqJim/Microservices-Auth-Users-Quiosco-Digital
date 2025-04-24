package com.smovistar.userservice.user_service.controller;

import com.google.api.gax.paging.Page;
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

	@GetMapping
	public ResponseEntity<List<DatosRespuestaUsuario>> listarUsuarios(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		var respuesta = logicaUsuario.obtenerUsuariosNoEliminadosSiSuscripcion(page, size);
		return ResponseEntity.ok(respuesta);
	}

	@PutMapping("/nuevaPreferencia/{email}")
	public  ResponseEntity<DatosRespuestaUsuario> agregarPreferencia(@PathVariable String email,
	                                                               @RequestBody @Valid DatosPreferencias datos) {
		var respuesta = logicaUsuario.agregarEditorialPreferida(email, datos);
		return ResponseEntity.ok(respuesta);
	}

	@PutMapping("/eliminarPreferencia/{email}")
	public  ResponseEntity<DatosRespuestaUsuario> eliminarPreferencia(@PathVariable String email,
	                                                                 @RequestBody @Valid DatosPreferencias datos) {
		var respuesta = logicaUsuario.eliminarEditorialPreferida(email, datos);
		return ResponseEntity.ok(respuesta);
	}

	@GetMapping("/preferencias/{email}")
	public ResponseEntity<List<String>> obtenerPreferencias(@PathVariable String email) {
		var respuesta = logicaUsuario.obtenerEditorialesPreferentes(email);
		return ResponseEntity.ok(respuesta);
	}
}
