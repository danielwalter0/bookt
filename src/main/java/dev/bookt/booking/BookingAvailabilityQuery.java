package dev.bookt.booking;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

import static dev.bookt.jooq.tables.Booking.BOOKING;

@Component
public class BookingAvailabilityQuery {
    private final DSLContext dsl;

    public BookingAvailabilityQuery(DSLContext dsl) {
        this.dsl = dsl;
    }

    boolean hasConflict(UUID resourceId, OffsetDateTime startsAt, OffsetDateTime endsAt) {
        return dsl.fetchExists(
                dsl.selectFrom(BOOKING)
                        .where(BOOKING.RESOURCE_ID.eq(resourceId))
                        .and(DSL.condition("time_range && tstzrange({0}, {1})", startsAt, endsAt))
                        .and(BOOKING.STATUS.in("HELD", "CONFIRMED"))
        );
    }

}
