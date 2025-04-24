package com.smovistar.authservice.auth_service.domain.usuarios;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DatosAutenticacionUsuario(
		@NotBlank(message = "El email no puede estar vacío")
		String email,

		@NotBlank
		@Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
		String contrasena
) {
}
