package com.smovistar.userservice.user_service.domain;

public class UsuarioNoEncontradoException extends RuntimeException {
	public UsuarioNoEncontradoException(String message) {
		super(message);
	}
}
