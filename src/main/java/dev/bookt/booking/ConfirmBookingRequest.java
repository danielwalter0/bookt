package dev.bookt.booking;

import java.util.UUID;

public record ConfirmBookingRequest(
        UUID userId
) {}
