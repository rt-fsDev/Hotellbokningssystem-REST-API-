package com.hotel.booking.controller;

import com.hotel.booking.config.JwtUtil;
import com.hotel.booking.dto.LoginRequest;
import com.hotel.booking.dto.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * AUTH-CONTROLLER - Hanterar inloggning och JWT-generering

 * Detta är den ENDA endpointen som är helt öppen (permitAll)
 * Alla andra endpoints kräver giltig JWT-token

 * REST-endpoint: POST /api/login
 */
@RestController  // Kombinerar @Controller och @ResponseBody - returnerar JSON direkt
@RequestMapping("/api")  // Bas-URL för alla endpoints i denna controller
public class AuthController {
    private final AuthenticationManager authenticationManager;  // Spring Securitys inloggningshanterare
    private final JwtUtil jwtUtil;  // Vårt JWT-verktyg för att skapa tokens

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /*
     * INLOGGNINGSENDPOINT

     * POST /api/login
     * Body: { "username": "user", "password": "user123" }

     * Flöde:
     * 1. Ta emot username och password från request-body
     * 2. Autentisera med AuthenticationManager
     * 3. Om OK - skapa JWT-token
     * 4. Returnera token till klienten

     * @param request - innehåller username och password
     * @return LoginResponse med JWT-token, användarnamn och roll
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        /*
         * AuthenticationManager.authenticate() gör följande:
         * 1. Letar upp användaren i UserDetailsService (InMemoryUserDetailsManager)
         * 2. Jämför lösenordet
         * 3. KASTAR exception om felaktiga uppgifter
         * 4. Returnerar ett Authentication-objekt om OK
         */
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Hämta UserDetails från det autentiserade objektet
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        // Extrahera rollen (ta bort "ROLE_" prefixet som Spring lägger till)
        String role = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        // Generera JWT-token med användarnamn och roll
        String token = jwtUtil.generateToken(userDetails.getUsername(), role);

        // Returnera token och användarinformation
        return ResponseEntity.ok(new LoginResponse(token, userDetails.getUsername(), role));
    }
}