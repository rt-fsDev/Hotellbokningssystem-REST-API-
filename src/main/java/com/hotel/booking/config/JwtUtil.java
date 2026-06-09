package com.hotel.booking.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

/**
 * JWT-VERKTYG - Hanterar skapande och validering av JWT-tokens

 * JWT (JSON Web Token) är en standard för att skicka säker information
 * mellan klient och server. Token består av tre delar:
 * 1. HEADER - algoritm och tokentyp
 * 2. PAYLOAD - användardata (claims som username, role)
 * 3. SIGNATURE - kryptografisk signatur för att förhindra manipulation

 * Struktur: xxxxx.yyyyy.zzzzz (Base64-url encoded)
 */
@Component  // Spring bean - kan injiceras överallt
public class JwtUtil {

    /**
     * HEMLIG NYCKEL - Laddas från application.properties eller använder default-värde
     * @ Value läser in värdet från properties-filen
     * "${jwt.secret:default}" betyder: använd jwt.secret om den finns, annars default
     */
    @Value("${jwt.secret:hotellbokningssystem2024HemligNyckelForJWT}")
    private String secretString;

    /**
     * UTGÅNGSTID - 86400000 ms = 24 timmar
     * Efter denna tid måste användaren logga in igen
     */
    @Value("${jwt.expiration:86400000}")
    private long expiration;

    private Key secretKey;  // Kryptografisk nyckel i rätt format

    /**
     * @ PostConstruct - Körs automatiskt EFTER konstruktorn men INNAN bean används
     * Detta är en initieringsmetod som förbereder den hemliga nyckeln
     */
    @PostConstruct
    public void init() {
        // Konvertera strängen till Base64 och sedan till en Key-objekt för HMAC-SHA
        byte[] keyBytes = Base64.getEncoder().encode(secretString.getBytes());
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);  // HMAC-SHA algoritm för signatur
    }

    /**
     * SKAPA NY TOKEN - Anropas när användaren loggar in
     *
     * @param username - användarens namn (subject i JWT)
     * @param role     - användarens roll (USER/ADMIN) - lagras som custom claim
     * @return en komplett JWT-token som sträng
     */
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)           // Vem token tillhör
                .claim("role", role)            // Custom claim för roll
                .setIssuedAt(new Date())        // När token skapades
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // När den går ut
                .signWith(secretKey)            // Signera med hemlig nyckel
                .compact();                     // Bygg ihop till en sträng
    }

    /**
     * EXTRAHERA ANVÄNDARNAMN från token
     * Används i JwtFilter för att veta vem som gör requesten
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * EXTRAHERA ROLL från token
     * Används i JwtFilter för att sätta rätt behörigheter
     */
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    /**
     * EXTRAHERA ALLA CLAIMS (data) från token
     * Claims är ett map med all information i token
     *
     * @param token - JWT-token som sträng
     * @return Claims-objekt med all data
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)       // Använd samma nyckel för att verifiera
                .build()
                .parseClaimsJws(token)          // Parsar och verifierar signaturen
                .getBody();                     // Hämta payload-delen
    }

    /**
     * VALIDERA TOKEN - Kollar om token är giltig
     *
     * @param token - token att validera
     * @return true om token är giltig, false om den är ogiltig eller utgången
     */
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);  // Om detta kastar exception är token ogiltig
            return true;
        } catch (Exception e) {
            return false;  // Ogiltig token (felaktig signatur, utgången, etc.)
        }
    }
}