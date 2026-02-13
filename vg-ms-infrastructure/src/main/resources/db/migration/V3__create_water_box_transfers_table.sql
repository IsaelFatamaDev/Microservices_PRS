CREATE TABLE IF NOT EXISTS water_box_transfers (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    organization_id VARCHAR(36) NOT NULL,
    water_box_id VARCHAR(36) NOT NULL REFERENCES water_boxes(id),
    from_user_id VARCHAR(36) NOT NULL,
    to_user_id VARCHAR(36) NOT NULL,
    transfer_date TIMESTAMP NOT NULL DEFAULT NOW(),
    transfer_fee NUMERIC(10,2),
    notes TEXT,
    record_status VARCHAR(10) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_transfers_water_box ON water_box_transfers(water_box_id);
CREATE INDEX IF NOT EXISTS idx_transfers_from_user ON water_box_transfers(from_user_id);
CREATE INDEX IF NOT EXISTS idx_transfers_to_user ON water_box_transfers(to_user_id);
