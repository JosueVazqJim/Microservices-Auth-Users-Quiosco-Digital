package com.smovistar.userservice.user_service.domain.usuario;

import jakarta.validation.constraints.*;

import java.util.Date;
import java.util.List;

public record DatosActualizarUsuario(
		String nombre,

		@Email
		String email,

		@Pattern(regexp = "\\+?[0-9]{10,15}", message = "El número de teléfono debe ser válido y contener entre 10 y 15 dígitos")
		String telefono,

		@Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
		String contrasena,

		String direccion,

		String estado,

		String pais,

		@Past
		Date fechaNacimiento,

		String rol,

		String numeroTarjeta,

		Boolean eliminado,

		Boolean estadoSuscripcion,

		List<String> editorialesSeguidas

) {
}
