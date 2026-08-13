
CREATE TABLE idempotency_key (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    key TEXT NOT NULL,
    request_body_hash TEXT NOT NULL,
    response_status SMALLINT NOT NULL,
    response_body TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, key)
);
