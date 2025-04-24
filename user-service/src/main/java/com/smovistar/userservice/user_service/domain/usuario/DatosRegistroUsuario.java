package com.smovistar.userservice.user_service.domain.usuario;

import jakarta.validation.constraints.*;

import java.util.Date;

public record DatosRegistroUsuario(

		@NotBlank
		String nombre,

		@NotBlank
		@Email
		String email,

		@NotBlank
		@Pattern(regexp = "\\+?[0-9]{10,15}", message = "El número de teléfono debe ser válido y contener entre 10 y 15 dígitos")
		String telefono,

		@NotBlank
		@Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
		String contrasena,

		@NotBlank
		String direccion,

		@NotBlank
		String estado,

		@NotBlank
		String pais,

		@NotNull
		@Past
		Date fechaNacimiento,

		@NotBlank
		String numeroTarjeta,

		@NotBlank
		String rol

) {}

