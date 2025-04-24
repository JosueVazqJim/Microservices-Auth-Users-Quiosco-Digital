package com.smovistar.userservice.user_service.domain.usuario;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DatosPreferencias(
		@NotNull
		List<String> editorialesSeguidas
) {
}
