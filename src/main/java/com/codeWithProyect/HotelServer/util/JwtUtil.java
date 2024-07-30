package com.codeWithProyect.HotelServer.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.KeyFactory;

import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Método para generar el token JWT
    private String generateToken(Map<String, Object> extraClaims, UserDetails details){
        return Jwts.builder().setClaims(extraClaims).setSubject(details.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*24))
                .signWith(getSigningKey(), SignatureAlgorithm.ES256).compact();
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    public String extractUserName(String token){
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers){
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    // Método para obtener la clave privada ECDSA
    private Key getSigningKey(){
        try {
            // Clave privada en Base64 copiada del archivo
            String base64Key = "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgIGQBMaT1EjxhWEa/Z9Yu7vI5u2YO0EVUMG+RhhRmyrahRANCAASNkqXltw4Ytz5mta9BoBnLFAOAnsS7bI5SHZhhM/kwymKi5pslOe/9uscUVIXn5ON4R/Rb+kcEeWTQ78JHx3oF"; // Reemplaza esto con tu clave en Base64

            // Decodificar la clave Base64
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);

            // Crear una especificación de clave PKCS8
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

            // Crear una instancia de KeyFactory para el algoritmo ECDSA
            KeyFactory keyFactory = KeyFactory.getInstance("EC");

            // Generar la clave privada
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo cargar la clave privada", e);
        }
    }
}
