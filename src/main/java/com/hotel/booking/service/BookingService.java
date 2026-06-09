package com.hotel.booking.service;

import com.hotel.booking.exception.BookingNotFoundException;
import com.hotel.booking.exception.GuestCapacityException;
import com.hotel.booking.exception.RoomFullyBookedException;
import com.hotel.booking.model.Booking;
import com.hotel.booking.model.RoomType;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BOOKING SERVICE - Affärslogik för bokningshantering

 * @ Service markerar att detta är ett service-lager (business logic)
 * Spring skapar en singleton-instans av denna klass

 * Här finns ALL logik för:
 * - Skapa bokningar (med validering av kapacitet och tillgänglighet)
 * - Ta bort bokningar (och återställa rum till inventariet)
 * - Hämta bokningar och tillgängliga rum
 */
@Service
public class BookingService {
    /**
     * LAGRING AV BOKNINGAR
     * ConcurrentHashMap = trådsäker HashMap (för flera samtidiga requests)
     * Map<Integer, Booking> = nyckel = bokningens ID, värde = Booking-objektet
     */
    private final Map<Integer, Booking> bookings = new ConcurrentHashMap<>();

    /**
     * LAGRING AV TILLGÄNGLIGA RUM
     * EnumMap = optimerad Map för enum-nycklar (RoomType)
     * Håller reda på hur många rum av varje typ som är lediga
     */
    private final Map<RoomType, Integer> availableRooms = new EnumMap<>(RoomType.class);

    /**
     * KONSTRUKTOR - Initierar inventariet när servicen skapas
     */
    public BookingService() {
        // Initialize inventory: 10 Single, 7 Double, 3 Suite
        availableRooms.put(RoomType.SINGLE, 10);
        availableRooms.put(RoomType.DOUBLE, 7);
        availableRooms.put(RoomType.SUITE, 3);
    }

    /**
     * HÄMTAR ALLA BOKNINGAR
     * @return Lista med alla bokningar (copy av Map:ens values)
     */
    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings.values());
    }

    /**
     * HÄMTAR TILLGÄNGLIGA RUM
     * @return En kopia av availableRooms (så ingen kan modifiera original-mapet)
     */
    public Map<RoomType, Integer> getAllAvailableRooms() {
        return new EnumMap<>(availableRooms);
    }

    /**
     * SKAPAR NY BOKNING - HUVUDSYFTET MED SYSTEMET

     * Steg:
     * 1. Validera att antalet gäster är <= rummets kapacitet
     * 2. Kontrollera att rum av denna typ finns lediga
     * 3. Skapa bokningen
     * 4. Minska antalet lediga rum
     * 5. Returnera bokningen
     *
     * @param guestName - Gästens namn
     * @param roomType - Rumstyp (enum)
     * @param numberOfGuests - Antal gäster
     * @return Den skapade bokningen
     * @throws GuestCapacityException om för många gäster
     * @throws RoomFullyBookedException om inga rum lediga
     */
    public Booking createBooking(String guestName, RoomType roomType, int numberOfGuests) {
        // KONTROLL 1: Kapacitetskontroll
        if (numberOfGuests > roomType.capacity) {
            throw new GuestCapacityException(roomType.svName, roomType.capacity, numberOfGuests);
        }

        // KONTROLL 2: Tillgänglighetskontroll
        int available = availableRooms.getOrDefault(roomType, 0);
        if (available <= 0) {
            throw new RoomFullyBookedException(roomType.svName);
        }

        // Skapa bokning och spara
        Booking booking = new Booking(guestName, roomType, numberOfGuests);
        bookings.put(booking.getId(), booking);

        // Minska inventariet
        availableRooms.put(roomType, available - 1);

        return booking;
    }

    /**
     * RADERAR BOKNING (ADMIN)

     * När en bokning tas bort:
     * 1. Ta bort från bookings-mapet
     * 2. Returnera rummet till inventariet (öka availableRooms)
     *
     * @param id - ID för bokningen som ska tas bort
     * @throws BookingNotFoundException om bokningen inte finns
     */
    public void deleteBooking(int id) {
        Booking removed = bookings.remove(id);
        if (removed == null) {
            throw new BookingNotFoundException(id);
        }
        // Return room to inventory
        availableRooms.put(removed.getRoomType(), availableRooms.get(removed.getRoomType()) + 1);
    }
}