// LoginResponse.java
package com.hotel.booking.dto;

/**
 * LOGIN RESPONSE DTO - Skickar JWT-token tillbaka till klienten
 * Används i AuthController.login()

 * Exempel response body:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "username": "user",
 *   "role": "USER"
 * }
 */
public class LoginResponse {
    private String token;     // JWT-token som klienten ska spara och använda i Authorization-headern
    private String username;  // Användarnamn (för bekvämlighet)
    private String role;      // Roll (USER/ADMIN)

    public LoginResponse(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    // Getters och Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}