package com.smovistar.userservice.user_service.controller;

import com.smovistar.userservice.user_service.domain.usuario.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/preferencias")
public class PreferencesController {
	@Autowired
	private UsuarioService logicaUsuario;


	@PutMapping("/{email}")
	public  ResponseEntity<DatosRespuestaUsuario> agregarPreferencias(@PathVariable String email,
	                                                               @RequestBody @Valid DatosPreferencias datos) {
		var respuesta = logicaUsuario.agregarEditorialPreferida(email, datos);
		return ResponseEntity.ok(respuesta);
	}

	@DeleteMapping("/{email}")
	public  ResponseEntity<DatosRespuestaUsuario> eliminarPreferencias(@PathVariable String email,
	                                                                 @RequestBody @Valid DatosPreferencias datos) {
		var respuesta = logicaUsuario.eliminarEditorialPreferida(email, datos);
		return ResponseEntity.ok(respuesta);
	}

	@GetMapping("/{email}")
	public ResponseEntity<List<String>> obtenerPreferencias(@PathVariable String email) {
		var respuesta = logicaUsuario.obtenerEditorialesPreferentes(email);
		return ResponseEntity.ok(respuesta);
	}
}
