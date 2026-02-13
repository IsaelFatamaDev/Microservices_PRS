CREATE TABLE IF NOT EXISTS water_box_assignments (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    organization_id VARCHAR(36) NOT NULL,
    water_box_id VARCHAR(36) NOT NULL REFERENCES water_boxes(id),
    user_id VARCHAR(36) NOT NULL,
    assignment_date TIMESTAMP NOT NULL DEFAULT NOW(),
    assignment_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    end_date TIMESTAMP,
    record_status VARCHAR(10) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(36),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(36)
);

CREATE INDEX idx_assignments_water_box ON water_box_assignments(water_box_id);
CREATE INDEX idx_assignments_user ON water_box_assignments(user_id);
CREATE INDEX idx_assignments_status ON water_box_assignments(assignment_status);
