package com.smovistar.userservice.user_service.domain.usuario.validaciones.registro;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.smovistar.userservice.user_service.domain.ValidacionException;
import com.smovistar.userservice.user_service.domain.usuario.DatosRegistroUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class ValidadorDuplicados implements IValidadoresRegistroUsuarios {
	private final Firestore dbFirestore;

	@Autowired
	public ValidadorDuplicados(Firestore dbFirestore) {
		this.dbFirestore = dbFirestore;
	}

	@Override
	public void validar(DatosRegistroUsuario datos) {
		ApiFuture<QuerySnapshot> future = dbFirestore.collection("usuarios").whereEqualTo("email", datos.email()).get();
		List<QueryDocumentSnapshot> documents = null;
		try {
			documents = future.get().getDocuments();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}

		if (!documents.isEmpty()){
			// Si la lista no está vacía, significa que ya existe un usuario con ese email
			throw new ValidacionException("Ya existe un usuario con el mismo email");
		}
	}
}
