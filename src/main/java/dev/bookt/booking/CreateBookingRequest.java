package dev.bookt.booking;

import java.time.OffsetDateTime;
import java.util.UUID;

// Represents exactly what a client sends in the POST /bookings request body.
// Deliberately does NOT include fields like id, status, or createdAt —
// those are controlled by the server, not the client.
public record CreateBookingRequest(
        UUID resourceId,
        UUID userId,
        OffsetDateTime startsAt,
        OffsetDateTime endsAt
) {}