CREATE TABLE IF NOT EXISTS water_boxes (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    organization_id VARCHAR(36) NOT NULL,
    box_code VARCHAR(50) NOT NULL UNIQUE,
    box_type VARCHAR(30) NOT NULL,
    installation_date TIMESTAMP,
    zone_id VARCHAR(36),
    street_id VARCHAR(36),
    address VARCHAR(255),
    current_assignment_id VARCHAR(36),
    is_active BOOLEAN NOT NULL DEFAULT true,
    record_status VARCHAR(10) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(36),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(36)
);

CREATE INDEX IF NOT EXISTS idx_water_boxes_organization ON water_boxes(organization_id);
CREATE INDEX IF NOT EXISTS idx_water_boxes_zone ON water_boxes(zone_id);
CREATE INDEX IF NOT EXISTS idx_water_boxes_status ON water_boxes(record_status);
