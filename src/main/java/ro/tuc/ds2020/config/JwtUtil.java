package ro.tuc.ds2020.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import ro.tuc.ds2020.dtos.PersonDTO;

import java.security.Key;

public class JwtUtil {

    private static final Key SECRET_KEY = Keys.hmacShaKeyFor("your-256-bit-secret-your-256-bit-secret".getBytes()); // Use a 256-bit secret

    public static String generateToken(PersonDTO person) {
        return Jwts.builder()
                .setSubject(person.getUsername())
                .claim("id", person.getId().toString())
                .claim("role", person.getRole())
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims validateToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
