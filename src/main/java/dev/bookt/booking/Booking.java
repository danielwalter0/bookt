package dev.bookt.booking;

// Booking = an actual reservation of a resource for a specific time window by a specific user.

import dev.bookt.resource.Resource;
import dev.bookt.tenant.Tenant;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "booking")
public class Booking {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "starts_at", nullable = false)
    private OffsetDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    private OffsetDateTime endsAt;

    @Column(nullable = false)
    private String status;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;


    public Booking() {}

    public Booking(Tenant tenant, Resource resource, UUID userId, OffsetDateTime startsAt, OffsetDateTime endsAt, String status, OffsetDateTime expiresAt) {
        this.id = UUID.randomUUID();
        this.tenant = tenant;
        this.resource = resource;
        this.userId = userId;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.status = status;
        this.expiresAt = expiresAt;
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public OffsetDateTime getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(OffsetDateTime startsAt) {
        this.startsAt = startsAt;
    }

    public OffsetDateTime getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(OffsetDateTime endsAt) {
        this.endsAt = endsAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

}
