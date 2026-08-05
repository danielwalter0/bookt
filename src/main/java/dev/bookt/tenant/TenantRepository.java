package dev.bookt.tenant;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

// JpaRepository<Tenant, UUID> gives access CRUD operations (save, findById, findAll,
// delete, etc.) for free, without writing any implementation ourselves.
// Tenant = the entity type this repository manages.
// UUID   = the type of Tenant's primary key (the @Id field).
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    // Custom finder — Spring Data JPA generates the implementation automatically
    // just from this method name. It parses "findByApiKeyHash" and translates it
    // into: SELECT * FROM tenant WHERE api_key_hash = ?
    // Useful later for authenticating incoming requests by their API key.
    Optional<Tenant> findByApiKeyHash(String apiKeyHash);
}
