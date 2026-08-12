package dev.bookt.booking;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
public class HoldExpirationJob {
    private final BookingRepository bookingRepository;

    public HoldExpirationJob(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void run(){
        List<Booking> bookings = bookingRepository.findByStatusAndExpiresAtBefore("HELD", OffsetDateTime.now());
        for(Booking booking : bookings){
            booking.setStatus("EXPIRED");
        }
        bookingRepository.saveAll(bookings);
    }
}
