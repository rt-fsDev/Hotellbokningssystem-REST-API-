package com.hotel.booking.controller;

import com.hotel.booking.dto.BookingRequest;
import com.hotel.booking.model.Booking;
import com.hotel.booking.model.RoomType;
import com.hotel.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * BOOKING-CONTROLLER - Hanterar alla bokningsrelaterade endpoints

 * Detta är REST-API:ts huvudsakliga controller för hotellbokningar
 * Alla metoder är skyddade med @PreAuthorize som kollar användarens roll

 * Roller:
 * - USER: Kan se rum, skapa bokningar
 * - ADMIN: Kan se alla bokningar, ta bort bokningar
 */
@RestController
@RequestMapping("/api")
public class BookingController {
    private final BookingService bookingService;  // Service-lager för affärslogik

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * VISAR TILLGÄNGLIGA RUM
     * GET /api/rooms

     * Tillgänglig för: USER och ADMIN (sätts i SecurityConfig)
     *
     * @return Lista med rumstyper och hur många som finns kvar
     */
    @GetMapping("/rooms")
    public ResponseEntity<List<Map<String, Object>>> getAvailableRooms() {
        // Hämta alla rumstyper med antal tillgängliga från service
        Map<RoomType, Integer> available = bookingService.getAllAvailableRooms();

        // Bygg ett snyggare svar med svenska namn och priser
        List<Map<String, Object>> response = new ArrayList<>();
        for (Map.Entry<RoomType, Integer> entry : available.entrySet()) {
            Map<String, Object> roomInfo = new HashMap<>();
            roomInfo.put("roomType", entry.getKey().svName);      // "Enkelrum", "Dubbelrum", "Svit"
            roomInfo.put("capacity", entry.getKey().capacity);    // Max antal gäster: 1,2,3
            roomInfo.put("available", entry.getValue());          // Antal lediga rum
            roomInfo.put("pricePerNight", entry.getKey().pricePerNight); // Pris per natt
            response.add(roomInfo);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * HÄMTAR ALLA BOKNINGAR (ADMIN ENDAST)
     * GET /api/bookings

     * @ PreAuthorize("hasRole('ADMIN')") - Endast ADMIN kan anropa denna
     *
     * @return Lista med alla bokningar i systemet
     */
    @GetMapping("/bookings")
    @PreAuthorize("hasRole('ADMIN')")  // Spring Security kollar rollen AUTOMATISKT!
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    /**
     * SKAPAR NY BOKNING (USER ENDAST)
     * POST /api/bookings

     * @ PreAuthorize("hasRole('USER')") - Endast inloggade användare (USER-rollen)
     *
     * @param request - Validerad BookingRequest (kollar namn, rumstyp, antal gäster)
     * @return Den skapade bokningen med ID och totalpris

     * Exempel request body:
     * {
     *   "guestName": "Kalle Svensson",
     *   "roomType": "Dubbelrum",
     *   "numberOfGuests": 2
     * }
     */
    @PostMapping("/bookings")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Booking> createBooking(@Valid @RequestBody BookingRequest request) {
        // Konvertera svenska rumstypen (sträng) till enum
        RoomType roomType = RoomType.fromSvName(request.getRoomType());

        // Anropa service för att skapa bokningen
        Booking booking = bookingService.createBooking(
                request.getGuestName(),
                roomType,
                request.getNumberOfGuests()
        );

        // Returnera 201 CREATED istället för 200 OK (standard för POST när resurs skapas)
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    /**
     * RADERAR BOKNING (ADMIN ENDAST)
     * DELETE /api/bookings/{id}

     * @ PreAuthorize("hasRole('ADMIN')") - Endast ADMIN kan ta bort bokningar
     *
     * @param id - Bokningens ID (från URL: /api/bookings/1)
     * @return 204 No Content om lyckad borttagning
     */
    @DeleteMapping("/bookings/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBooking(@PathVariable int id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();  // 204 = lyckad borttagning utan body
    }
}