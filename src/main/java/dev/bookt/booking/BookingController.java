package dev.bookt.booking;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking createBooking(@RequestBody CreateBookingRequest request) {
        return bookingService.createBooking(request);
    }

    @PostMapping("/hold")
    @ResponseStatus(HttpStatus.CREATED)
    public Booking createHold(@RequestBody CreateBookingRequest request) {
        return bookingService.createHold(request);
    }

    @PostMapping("/{id}/confirm")
    @ResponseStatus(HttpStatus.OK)
    public Booking confirmBooking(@PathVariable UUID id, @RequestBody ConfirmBookingRequest request) {
        return bookingService.confirmBooking(request, id);
    }


}
