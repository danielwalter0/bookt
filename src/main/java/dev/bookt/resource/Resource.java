package dev.bookt.resource;

// Resource = the bookable "thing" itself — e.g. a specific court, room, or piece of equipment.

import dev.bookt.tenant.Tenant;
import jakarta.persistence.*;


import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "resource")
public class Resource {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false)
    private String name;

    @Column(name = "slot_minutes", nullable = false)
    private int slotMinutes;

    @Column(name = "opens_at", nullable = false)
    private LocalTime opensAt;

    @Column(name = "closes_at", nullable = false)
    private LocalTime closesAt;

    public Resource() {}

    public Resource(String name, Tenant tenant, int slotMinutes, LocalTime opensAt, LocalTime closesAt) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.tenant = tenant;
        this.slotMinutes = slotMinutes;
        this.opensAt = opensAt;
        this.closesAt = closesAt;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSlotMinutes() {
        return slotMinutes;
    }

    public void setSlotMinutes(int slotMinutes) {
        this.slotMinutes = slotMinutes;
    }

    public LocalTime getOpensAt() {
        return opensAt;
    }

    public void setOpensAt(LocalTime opensAt) {
        this.opensAt = opensAt;
    }

    public LocalTime getClosesAt() {
        return closesAt;
    }

    public void setClosesAt(LocalTime closesAt) {
        this.closesAt = closesAt;
    }

}
