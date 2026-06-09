package com.hotel.booking.model;

/**
 * RUMSTYP - ENUM (enumererad typ) för att representera olika rumstyper

 * Enum är perfekt när man har ett FIXXT antal möjliga värden (som här: SINGLE, DOUBLE, SUITE)

 * Fördelar med enum jämfört med String:
 * 1. Typsäkert - kan inte skicka "felaktigt rum" som parameter
 * 2. Ingen stavningsrisk - kompilatorn kollar
 * 3. Kan ha inbyggda egenskaper (svName, capacity, pricePerNight)
 * 4. switch-satser fungerar bra med enum
 */
public enum RoomType {
    SINGLE("Enkelrum", 1, 500),   // Svenska namnet, kapacitet, pris per natt
    DOUBLE("Dubbelrum", 2, 1000),
    SUITE("Svit", 3, 2000);

    // Fält som varje enum-värde har
    public final String svName;      // Svenska namnet (för API-svar)
    public final int capacity;       // Max antal gäster
    public final int pricePerNight;  // Pris per natt i SEK

    /**
     * Privat konstruktor för enum (kallas när SINGLE, DOUBLE, SUITE skapas)
     */
    RoomType(String svName, int capacity, int pricePerNight) {
        this.svName = svName;
        this.capacity = capacity;
        this.pricePerNight = pricePerNight;
    }

    /**
     * KONVERTERAR SVENSKT NAMN TILL ENUM     *
     * Används i controllern när vi får "Enkelrum" som String från klienten
     * och vill konvertera till RoomType.SINGLE
     *
     * @param svName - Svenska namnet (t.ex "Enkelrum")
     * @return Motsvarande RoomType-värde
     * @throws IllegalArgumentException om namnet inte finns
     */
    public static RoomType fromSvName(String svName) {
        for (RoomType type : values()) {  // values() returnerar alla enum-värden
            if (type.svName.equals(svName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Okänd rumstyp: " + svName);
    }
}