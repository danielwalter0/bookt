package dev.bookt.booking;

import dev.bookt.resource.Resource;
import dev.bookt.resource.ResourceRepository;
import dev.bookt.tenant.Tenant;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        Tenant tenant = resource.getTenant();
        Booking booking = new Booking(tenant, resource, request.userId(), request.startsAt(), request.endsAt(), "CONFIRMED", null);
        try{
            bookingRepository.save(booking);
        } catch (DataIntegrityViolationException e){
            throw new BookingConflictException("Resource was already booked for the requested time", e);
        }

        return booking;
    }

}
