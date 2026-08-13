package dev.bookt.tenant;

// the tenant (business/customer) that owns this resource, e.g. the gym that owns this court

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "tenant")
public class Tenant {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "api_key_hash", nullable = false, unique = true)
    private String apiKeyHash;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "allowed_origins", nullable = false)
    private String[] allowedOrigins;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public Tenant() {}

    public Tenant(String name, String apiKeyHash, String[] allowedOrigins) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.apiKeyHash = apiKeyHash;
        this.allowedOrigins = (allowedOrigins != null) ? allowedOrigins : new String[]{};
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApiKeyHash() {
        return apiKeyHash;
    }

    public void setApiKeyHash(String apiKeyHash) {
        this.apiKeyHash = apiKeyHash;
    }

    public String[] getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(String[] allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

}
