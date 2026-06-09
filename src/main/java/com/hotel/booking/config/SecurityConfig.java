package com.hotel.booking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SECURITY-KONFIGURATION - Hjärtat av säkerheten i applikationen
 * Denna klass bestämmer:
 * - Vilka endpoints som är skyddade
 * - Vilka roller som har åtkomst till vad
 * - Att sessioner inte används (stateless - vi använder JWT istället)
 * - Att vårt JwtFilter används
 */
@Configuration  // Markerar att denna klass innehåller Spring-konfiguration (beans)
@EnableMethodSecurity  // Gör att @PreAuthorize annoteringar fungerar i controllers
public class SecurityConfig {
    private final JwtFilter jwtFilter;  // Vårt JWT-filter injiceras

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * HUVUDKONFIGURATIONEN - Här definieras säkerhetsreglerna
     * SecurityFilterChain är ett objekt som Spring Security använder för att veta
     * vilka regler som gäller för olika endpoints
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. STÄNG AV CSRF (Cross-Site Request Forgery) skydd
                // Vi använder JWT som är stateless, så CSRF-skydd behövs inte
                .csrf(AbstractHttpConfigurer::disable)

                // 2. SESSION POLICY - Stateless (ingen session sparas på servern)
                // Varje request måste innehålla JWT-token för autentisering
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. AUTHORIZATION REGLER - Vem får gå vart?
                .authorizeHttpRequests(auth -> auth
                        // /api/login är helt öppen (ingen autentisering krävs)
                        .requestMatchers("/api/login").permitAll()

                        // GET /api/rooms - både USER och ADMIN kan se tillgängliga rum
                        .requestMatchers(HttpMethod.GET, "/api/rooms").hasAnyRole("USER", "ADMIN")

                        // POST /api/bookings - ENDAST USER kan skapa bokningar
                        .requestMatchers(HttpMethod.POST, "/api/bookings").hasRole("USER")

                        // GET /api/bookings - ENDAST ADMIN kan se alla bokningar
                        .requestMatchers(HttpMethod.GET, "/api/bookings").hasRole("ADMIN")

                        // DELETE /api/bookings/** - ENDAST ADMIN kan ta bort bokningar
                        .requestMatchers(HttpMethod.DELETE, "/api/bookings/**").hasRole("ADMIN")

                        // Alla andra requests kräver autentisering
                        .anyRequest().authenticated()
                )
                // 4. LÄGG TILL VÅRT JWT-FILTER före standard UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * USERDETAILSSERVICE - Här lagras användarna i minnet (för demo)

     * I en riktig applikation skulle detta vara en databas, men för denna
     * redovisning använder vi InMemoryUserDetailsManager

     * {noop} betyder "no operation password encoder" - lösenordet jämförs i klartext
     * (endast för demo - använd BCrypt i produktion!)
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // Skapa en vanlig USER
        var user = User.withUsername("user")
                .password("{noop}user123")   // {noop} = ingen kryptering
                .roles("USER")                // Roll = USER (automatiskt ROLE_USER i Spring)
                .build();

        // Skapa en ADMIN
        var admin = User.withUsername("admin")
                .password("{noop}admin123")
                .roles("ADMIN")               // Roll = ADMIN
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    /**
     * AUTHENTICATION MANAGER - Hanterar inloggningsförsök

     * Används i AuthController för att autentisera användaren med username/password
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}