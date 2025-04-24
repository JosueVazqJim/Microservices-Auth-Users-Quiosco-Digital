package com.smovistar.userservice.user_service.domain.usuario;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
	private String id;
	private String nombre;
	private String email;
	private String telefono;
	private String contrasena;
	private String direccion;
	private String estado;
	private String pais;
	private Date fechaNacimiento;
	private String rol;
	private String numeroTarjeta;
	private Boolean eliminado;
	private Boolean estadoSuscripcion;
	private List<String> editorialesSeguidas;

	public Usuario(DatosRegistroUsuario datos) {
		this.nombre = datos.nombre();
		this.email = datos.email();
		this.telefono = datos.telefono();
		this.contrasena = datos.contrasena();
		this.direccion = datos.direccion();
		this.estado = datos.estado();
		this.pais = datos.pais();
		this.fechaNacimiento = datos.fechaNacimiento();
		this.numeroTarjeta = datos.numeroTarjeta();
		this.rol = datos.rol();
		this.eliminado = false;
		this.estadoSuscripcion = true;
		this.editorialesSeguidas = List.of();
	}
}
