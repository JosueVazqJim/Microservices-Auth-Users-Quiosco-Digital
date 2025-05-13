package com.smovistar.userservice.user_service.domain.usuario;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.smovistar.userservice.user_service.domain.UsuarioNoEncontradoException;
import com.smovistar.userservice.user_service.domain.ValidacionException;
import com.smovistar.userservice.user_service.domain.usuario.validaciones.registro.IValidadoresRegistroUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

	private final Firestore dbFirestore;
	private final List<IValidadoresRegistroUsuarios> validadores;

	@Autowired
	public UsuarioService(Firestore dbFirestore, List<IValidadoresRegistroUsuarios> validadores) {
		this.dbFirestore = dbFirestore;
		this.validadores = validadores;
	}

	public DatosRespuestaUsuario registrar(DatosRegistroUsuario datos) {
		validadores.forEach(v -> v.validar(datos));

		Usuario usuario = new Usuario(datos);

		try {
			// Obtener el último ID
			DocumentReference idRef = dbFirestore.collection("config").document("lastUserId");
			ApiFuture<DocumentSnapshot> future = idRef.get();
			DocumentSnapshot document = future.get();

			long lastId = 0;
			if (document.exists() && document.contains("lastId")) {
				lastId = document.getLong("lastId");
			}

			// Incrementar el ID
			long newId = lastId + 1;

			// Actualizar el último ID en Firestore
			idRef.update("lastId", newId);

			// Asignar el nuevo ID al usuario
			usuario.setId(String.valueOf(newId));
			dbFirestore.collection("usuarios").document(usuario.getId()).set(usuario);

			return new DatosRespuestaUsuario(usuario);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error al registrar el usuario", e);
		}
	}

	public DatosRespuestaUsuario obtener(String email) {
		Usuario usuario = buscarUsuario(email);
		return new DatosRespuestaUsuario(usuario);
	}

	public DatosRespuestaUsuario actualizar(String email, DatosActualizarUsuario datos) {
	    Usuario usuario = buscarUsuario(email);

		usuarioEliminado(usuario);

	    // Actualizar los campos del objeto Usuario
	    if (datos.nombre() != null && !datos.nombre().isEmpty()) usuario.setNombre(datos.nombre());
	    if (datos.email() != null && !datos.email().isEmpty()) usuario.setEmail(datos.email());
	    if (datos.telefono() != null && !datos.telefono().isEmpty()) usuario.setTelefono(datos.telefono());
	    if (datos.contrasena() != null && !datos.contrasena().isEmpty()) usuario.setContrasena(datos.contrasena());
	    if (datos.direccion() != null && !datos.direccion().isEmpty()) usuario.setDireccion(datos.direccion());
	    if (datos.estado() != null && !datos.estado().isEmpty()) usuario.setEstado(datos.estado());
	    if (datos.pais() != null && !datos.pais().isEmpty()) usuario.setPais(datos.pais());
	    if (datos.fechaNacimiento() != null) usuario.setFechaNacimiento(datos.fechaNacimiento());
	    if (datos.rol() != null && !datos.rol().isEmpty()) usuario.setRol(datos.rol());
	    if (datos.numeroTarjeta() != null && !datos.numeroTarjeta().isEmpty()) usuario.setNumeroTarjeta(datos.numeroTarjeta());
	    if (datos.eliminado() != null) usuario.setEliminado(datos.eliminado());
	    if (datos.estadoSuscripcion() != null) usuario.setEstadoSuscripcion(datos.estadoSuscripcion());
	    if (datos.editorialesSeguidas() != null && !datos.editorialesSeguidas().isEmpty()) {
	        List<String> editorialesFiltradas = datos.editorialesSeguidas().stream()
	                .filter(e -> e != null && !e.trim().isEmpty())
	                .collect(Collectors.toList());
	        usuario.setEditorialesSeguidas(editorialesFiltradas);
	    }

	    // Enviar el objeto actualizado a Firebase
	    dbFirestore.collection("usuarios").document(usuario.getId()).set(usuario);

	    return new DatosRespuestaUsuario(usuario);
	}

	public void eliminar(String email) {
		Usuario usuario = buscarUsuario(email);

		usuarioEliminado(usuario);

		usuario.setEliminado(true);
		usuario.setEstadoSuscripcion(false);
		dbFirestore.collection("usuarios").document(usuario.getId()).set(usuario);
	}

	public DatosRespuestaUsuario agregarEditorialPreferida(String email, DatosPreferencias datos) {
	    Usuario usuario = buscarUsuario(email);

		//verificar si la lista de editorialesSeguidas no contiene elementos con ""
		List<String> editoriales = datos.editorialesSeguidas().stream()
						.filter(e -> !e.isEmpty())
						.toList();


	    //usuarioEliminado(usuario);

	    dbFirestore.collection("usuarios").document(usuario.getId())
	            .update("editorialesSeguidas", FieldValue.arrayUnion(editoriales.toArray()));

	    // Actualizar la instancia local
	    if (usuario.getEditorialesSeguidas() == null) {
	        usuario.setEditorialesSeguidas(datos.editorialesSeguidas());
	    } else {
	        usuario.getEditorialesSeguidas().addAll(
			        editoriales.stream()
	                        .filter(e -> !usuario.getEditorialesSeguidas().contains(e))
	                        .toList()
	        );
	    }

	    return new DatosRespuestaUsuario(usuario);
	}

	public DatosRespuestaUsuario eliminarEditorialPreferida(String email, DatosPreferencias datos) {
		Usuario usuario = buscarUsuario(email);

		List<String> editoriales = datos.editorialesSeguidas().stream()
				.filter(e -> !e.isEmpty())
				.toList();

		//usuarioEliminado(usuario);

		dbFirestore.collection("usuarios").document(usuario.getId())
				.update("editorialesSeguidas", FieldValue.arrayRemove(editoriales.toArray()));

		// Actualizar la instancia local
		usuario.getEditorialesSeguidas().removeAll(editoriales);

		return new DatosRespuestaUsuario(usuario);
	}

	public List<String> obtenerEditorialesPreferentes(String email) {

		Usuario usuario = buscarUsuario(email);

		return usuario.getEditorialesSeguidas();
	}

	public List<DatosRespuestaUsuario> obtenerUsuarios(int page, int size) {
		int offset = page * size;

		// Consulta a Firestore con paginación
		ApiFuture<QuerySnapshot> future = dbFirestore.collection("usuarios")
				.orderBy("email") // Ordenar por un campo, por ejemplo, "email"
				.offset(offset)   // Saltar los primeros `offset` documentos
				.limit(size)      // Limitar el número de resultados
				.get();

		try {
			List<QueryDocumentSnapshot> documents = future.get().getDocuments();
			return documents.stream()
					.map(doc -> {
						Usuario usuario = doc.toObject(Usuario.class);
						return new DatosRespuestaUsuario(usuario);
					})
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new RuntimeException("Error al listar usuarios", e);
		}
	}

	public List<DatosRespuestaUsuario> obtenerUsuariosNoEliminadosSiSuscripcion(int page, int size) {
		int offset = page * size;

		// Consulta a Firestore con paginación
		ApiFuture<QuerySnapshot> future = dbFirestore.collection("usuarios")
				.whereEqualTo("eliminado", false)
				.whereEqualTo("estadoSuscripcion", true)
				.get();

		try {
			List<QueryDocumentSnapshot> documents = future.get().getDocuments();
			return documents.stream()
					.map(doc -> {
						Usuario usuario = doc.toObject(Usuario.class);
						return new DatosRespuestaUsuario(usuario);
					})
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new RuntimeException("Error al listar usuarios", e);
		}
	}

	private Usuario buscarUsuario(String email) {

		ApiFuture<QuerySnapshot> future = dbFirestore.collection("usuarios").whereEqualTo("email", email).get();
		List<QueryDocumentSnapshot> documents = null;
		try {
			documents = future.get().getDocuments();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
		if (documents.isEmpty()) {
			throw new UsuarioNoEncontradoException("No existe un Usuario con el email indicado");
		}

		DocumentSnapshot document = documents.getFirst(); // Tomamos el primer resultado
		Usuario usuario = document.toObject(Usuario.class);
		usuario.setId(document.getId());
		return usuario;
	}

	private void usuarioEliminado(Usuario usuario) {
		var eliminado = usuario.getEliminado();
		var estadoSuscripcion = usuario.getEstadoSuscripcion();

		if (eliminado && !estadoSuscripcion) {
			throw new ValidacionException("El usuario ya ha sido eliminado y no tiene una suscripción activa");
		}
	}
}
