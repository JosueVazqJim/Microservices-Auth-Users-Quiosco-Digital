package com.smovistar.userservice.user_service.infraestructure.errores;

import com.smovistar.userservice.user_service.domain.UsuarioNoEncontradoException;
import com.smovistar.userservice.user_service.domain.ValidacionException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class TratadorErrores {

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity error404() {
		return ResponseEntity.notFound().build();
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity error400(MethodArgumentNotValidException e) {
		var errores = e.getFieldErrors().stream().map(DatosErrorValidacion::new).toList();
		return ResponseEntity.badRequest().body(errores);
	}

	@ExceptionHandler(UsuarioNoEncontradoException.class)
	public ResponseEntity usuarioNoEncontrado( UsuarioNoEncontradoException e) {
		return ResponseEntity.status(404).body(new DatosError(e.getMessage()));
	}

	private record DatosErrorValidacion(String campo, String error) {
		public DatosErrorValidacion(FieldError error){
			this(error.getField(), error.getDefaultMessage());
		}
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity usuarioNoEncontrado( MissingServletRequestParameterException e) {
		return ResponseEntity.status(404).body(new DatosError(e.getMessage()));
	}

	@ExceptionHandler(ValidacionException.class)
	public ResponseEntity usuarioNoEncontrado( ValidacionException e) {
		return ResponseEntity.status(400).body(new DatosError(e.getMessage()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity ilegalArgs( IllegalArgumentException e) {
		return ResponseEntity.status(400).body(new DatosError(e.getMessage()));
	}


	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity ilegalArgs( HttpMessageNotReadableException e) {
		return ResponseEntity.status(400).body(new DatosError(e.getMessage()));
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity ilegalArgs( HttpRequestMethodNotSupportedException e) {
		return ResponseEntity.status(400).body(new DatosError(e.getMessage()));
	}
	private record DatosError(String error) {
		public DatosError(FieldError error){
			this(error.getDefaultMessage());
		}
	}
}
