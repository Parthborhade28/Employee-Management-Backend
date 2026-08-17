package com.manage.security;

import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private static final String SECRET = "mysecretkeymysecretkeymysecretkeymysecretkey";

	private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

	public String generateToken(String email) {

		return Jwts.builder().subject(email).issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24)).signWith(key).compact();
	}

	public String extractUsername(String token) {

		return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
	}

	public boolean validateToken(String token, String email) {

		String username = extractUsername(token);

		return username.equals(email);
	}
}