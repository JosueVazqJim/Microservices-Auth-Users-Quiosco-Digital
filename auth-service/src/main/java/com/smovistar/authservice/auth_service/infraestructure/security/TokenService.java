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

	public DecodedJWT decodeToken(String token) {
		if (token == null) throw new RuntimeException("Token inválido");

		try {
			Algorithm algorithm = Algorithm.HMAC256(apiSecret);
			return JWT.require(algorithm)
					.withIssuer("smovistar")
					.build()
					.verify(token);
		} catch (JWTVerificationException exception) {
			throw new RuntimeException("Token inválido o expirado");
		}
	}

	private Instant generarFechaExpiracion() {
	    return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.systemDefault().getRules().getOffset(Instant.now()));
	}
}
