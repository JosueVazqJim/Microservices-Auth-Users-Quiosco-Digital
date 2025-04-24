package com.smovistar.authservice.auth_service.infraestructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.smovistar.authservice.auth_service.domain.usuarios.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

	@Value("${api.security.secret}")
	private String apiSecret;

	public String generarToken(Usuario usuario) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(apiSecret);
			return JWT.create()
					.withIssuer("smovistar") //quien emite el token
					.withSubject(usuario.getEmail()) //a quien pertenece el token
					.withClaim("id", usuario.getId()) //informacion adicional
					.withClaim("rol", usuario.getRol())
					.withExpiresAt(generarFechaExpiracion())
					.sign(algorithm);
		} catch (JWTCreationException exception){
			throw new RuntimeException("Error al generar token");
		}
	}

	public String getSubject(String token) { //obtener el usuario del token
		if (token == null) {
			throw new RuntimeException("Token invalido");
		}
		DecodedJWT verifier = null;
		try {
			Algorithm algorithm = Algorithm.HMAC256(apiSecret);
			verifier = JWT.require(algorithm)
					.withIssuer("smovistar")
					.build()
					.verify(token);
			verifier.getSubject();
		} catch (JWTVerificationException exception) {
			System.out.println(exception.toString());
		}

		if (verifier.getSubject() == null) {
			throw new RuntimeException("Verifier invalido");
		}
		return verifier.getSubject();
	}

	private Instant generarFechaExpiracion() {
	    return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.systemDefault().getRules().getOffset(Instant.now()));
	}
}
