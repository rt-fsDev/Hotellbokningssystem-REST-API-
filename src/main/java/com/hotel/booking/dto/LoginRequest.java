// LoginRequest.java
package com.hotel.booking.dto;

/**
 * LOGIN REQUEST DTO - Tar emot inloggningsuppgifter från klienten
 * Används i AuthController.login()

 * Exempel request body:
 * {
 *   "username": "user",
 *   "password": "user123"
 * }
 */
public class LoginRequest {
    private String username;
    private String password;

    // Getters och Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}