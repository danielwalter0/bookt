ALTER TABLE idempotency_key DROP CONSTRAINT idempotency_key_tenant_id_key_key;
ALTER TABLE idempotency_key ADD UNIQUE (key);