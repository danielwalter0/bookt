-- Enables GiST indexing on non-range types (needed for the exclusion constraint below)
CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE tenant (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    api_key_hash TEXT NOT NULL UNIQUE,
    allowed_origins TEXT[] NOT NULL DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE resource (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    name TEXT NOT NULL,
    slot_minutes INT NOT NULL,
    opens_at TIME NOT NULL,
    closes_at TIME NOT NULL
);

CREATE TABLE booking (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    resource_id UUID NOT NULL REFERENCES resource(id),
    user_id UUID NOT NULL,
    starts_at TIMESTAMPTZ NOT NULL,
    ends_at TIMESTAMPTZ NOT NULL,
    status TEXT NOT NULL,
    expires_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    time_range TSTZRANGE GENERATED ALWAYS AS
        (tstzrange(starts_at, ends_at, '[)')) STORED,
    CONSTRAINT valid_interval CHECK (ends_at > starts_at)
);

ALTER TABLE booking ADD CONSTRAINT no_overlapping_bookings
    EXCLUDE USING gist (
    resource_id WITH =,
    time_range  WITH &&
) WHERE (status IN ('HELD', 'CONFIRMED'));