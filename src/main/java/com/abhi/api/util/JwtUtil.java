package com.abhi.api.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtUtil {

	private String secret = "abhishek";

	public String extractUserName(String token) {
		return extractclaims(token, Claims::getSubject);

	}

	private <T> T extractclaims(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);

		return claimsResolver.apply(claims);
	}

	public Date extractExpiration(String token) {
		return extractclaims(token, Claims::getExpiration);
	}

	private Claims extractAllClaims(String token) {
		Claims body = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

		return body;
	}

	private Boolean isTokenExpired(String token) {

		return extractExpiration(token).before(new Date());
	}

	public String generateToken(String userName) {
		Map<String, Object> claims = new HashMap<>();

		return createToken(claims, userName);
	}

	private String createToken(Map<String, Object> claims, String userName) {

		return Jwts.builder().setClaims(claims).setSubject(userName).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60*60))
				.signWith(SignatureAlgorithm.HS256, secret).compact();
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		String userName = extractUserName(token);

		return userName.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

}
