package dev.bookt.idempotency;


import jakarta.persistence.*;


import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "idempotency_key")
public class IdempotencyKey {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String key;

    @Column(name = "request_body_hash", nullable = false)
    private String requestBodyHash;

    @Column(name = "response_status", nullable = false)
    private short responseStatus;

    @Column(name = "response_body", nullable = false)
    private String responseBody;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public IdempotencyKey() {}

    public IdempotencyKey(String key, String requestBodyHash, short responseStatus, String responseBody) {
        this.id = UUID.randomUUID();
        this.key = key;
        this.requestBodyHash = requestBodyHash;
        this.responseStatus = responseStatus;
        this.responseBody = responseBody;
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRequestBodyHash() {
        return requestBodyHash;
    }

    public void setRequestBodyHash(String requestBodyHash) {
        this.requestBodyHash = requestBodyHash;
    }

    public short getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(short responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

}
