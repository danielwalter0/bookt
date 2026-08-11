package dev.bookt.booking;

import dev.bookt.resource.Resource;
import dev.bookt.resource.ResourceNotFoundException;
import dev.bookt.resource.ResourceRepository;
import dev.bookt.tenant.Tenant;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ResourceRepository resourceRepository;

    // Constructor injection — Spring automatically supplies real instances
    // of both repositories when it creates this service bean.
    public BookingService(BookingRepository bookingRepository, ResourceRepository resourceRepository) {
        this.bookingRepository = bookingRepository;
        this.resourceRepository = resourceRepository;
    }

    public Booking createBooking(CreateBookingRequest request) {
        Resource resource = resourceRepository.findById(request.resourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        Tenant tenant = resource.getTenant();
        Booking booking = new Booking(tenant, resource, request.userId(), request.startsAt(), request.endsAt(), "CONFIRMED", null);
        try{
            bookingRepository.save(booking);
        } catch (DataIntegrityViolationException e){
            throw new BookingConflictException("Resource was already booked for the requested time", e);
        }

        return booking;
    }

    public Booking createHold(CreateBookingRequest request) {
        Resource resource = resourceRepository.findById(request.resourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        Tenant tenant = resource.getTenant();
        Booking booking = new Booking(tenant, resource, request.userId(), request.startsAt(), request.endsAt(), "HELD", OffsetDateTime.now().plusMinutes(10));
        try{
            bookingRepository.save(booking);
        } catch (DataIntegrityViolationException e){
            throw new BookingConflictException("Resource was already booked for the requested time", e);
        }
        return booking;
    }

    public Booking confirmBooking(ConfirmBookingRequest request, UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));
        if(!booking.getStatus().equals("HELD")) {
            throw new InvalidHoldStateException("Invalid hold state");
        }
        if(booking.getExpiresAt().isBefore(OffsetDateTime.now())){
            throw new InvalidHoldStateException("Booking has already expired");
        }
        if(!booking.getUserId().equals(request.userId())){
            throw new InvalidHoldStateException("Invalid user id");
        }
        booking.setStatus("CONFIRMED");
        booking.setExpiresAt(null);

        bookingRepository.save(booking);

        return booking;

    }

}
