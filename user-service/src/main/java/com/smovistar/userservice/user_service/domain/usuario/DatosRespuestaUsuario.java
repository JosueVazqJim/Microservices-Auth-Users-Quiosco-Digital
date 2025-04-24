package com.smovistar.userservice.user_service.domain.usuario;

import java.util.Date;
import java.util.List;

public record DatosRespuestaUsuario(
		String id,
		String email,
		String rol,
		String nombre,
		String telefono,
		String contrasena,
		String direccion,
		String estado,
		String pais,
		Date fechaNacimiento,
		String numeroTarjeta,
		boolean eliminado,
		boolean estadoSuscripcion,
		List<String> editorialesSeguidas
) {
	// Constructor adicional que recibe un id y un objeto Usuario
	public DatosRespuestaUsuario(Usuario usuario) {
		this(
				usuario.getId(),
				usuario.getEmail(),
				usuario.getRol(),
				usuario.getNombre(),
				usuario.getTelefono(),
				usuario.getContrasena(),
				usuario.getDireccion(),
				usuario.getEstado(),
				usuario.getPais(),
				usuario.getFechaNacimiento(),
				usuario.getNumeroTarjeta(),
				usuario.getEliminado(),
				usuario.getEstadoSuscripcion(),
				usuario.getEditorialesSeguidas()
		);
	}
}
