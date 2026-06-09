package com.hotel.booking.model;

import java.time.LocalDateTime;

/**
 * ERROR RESPONSE MODELL - Standardiserat felmeddelande-format

 * Används av GlobalExceptionHandler för att skicka konsekventa felmeddelanden
 * till klienten

 * Exempel på svar vid fel:
 * {
 *   "timestamp": "2024-01-15T14:30:00",
 *   "status": 404,
 *   "message": "Bokning med ID 99 hittades inte.",
 *   "path": "/api/bookings/99"
 * }
 */
public class ErrorResponse {
    private LocalDateTime timestamp;  // När felet inträffade (ISO-8601 format)
    private int status;               // HTTP-statuskod (400, 404, 409, etc.)
    private String message;           // Mänskligt läsbart felmeddelande
    private String path;              // Vilken URL som anropades

    public ErrorResponse(LocalDateTime timestamp, int status, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.path = path;
    }

    // Getters och Setters
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
}