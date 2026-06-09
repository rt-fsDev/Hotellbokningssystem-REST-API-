package com.hotel.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * HUVUDKLASS - Startpunkten för hela Spring Boot-applikationen

 * @ SpringBootApplication är en kombination av tre annoteringar:
 * 1. @Configuration - markerar klassen som en källa för bean-definitioner
 * 2. @EnableAutoConfiguration - låter Spring Boot automatiskt konfigurera applikationen
 * 3. @ComponentScan - skannar efter komponenter i paketet com.hotel.booking

 * När denna klass körs startas en inbyggd Tomcat-server (port 8080 som standard)
 * och alla Spring-beans laddas upp i IoC-containern (Inversion of Control)
 */
@SpringBootApplication
public class BookingSystemApplication {
    public static void main(String[] args) {
        // SpringApplication.run() startar hela Spring Boot-applikationen
        // Detta initierar applikationskontexten, startar servern och registrerar alla controllers/services
        SpringApplication.run(BookingSystemApplication.class, args);
    }
}