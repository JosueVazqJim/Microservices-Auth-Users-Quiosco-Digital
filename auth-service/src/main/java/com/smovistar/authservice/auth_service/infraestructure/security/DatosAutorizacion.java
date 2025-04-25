package com.smovistar.authservice.auth_service.infraestructure.security;

public record DatosAutorizacion(
		String valid, String email, String id, String rol
) {
}
