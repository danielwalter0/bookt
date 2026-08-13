ALTER TABLE idempotency_key DROP CONSTRAINT idempotency_key_tenant_id_fkey;
ALTER TABLE idempotency_key DROP COLUMN tenant_id;