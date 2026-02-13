-- =====================================================
-- V3: Create materials table
-- Microservice: vg-ms-inventory-purchases
-- =====================================================

CREATE TABLE IF NOT EXISTS materials (
    id              VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    organization_id VARCHAR(36)  NOT NULL,
    record_status   VARCHAR(10)  NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by      VARCHAR(100),
    material_code   VARCHAR(50)  NOT NULL UNIQUE,
    material_name   VARCHAR(200) NOT NULL,
    category_id     VARCHAR(36),
    unit            VARCHAR(20)  NOT NULL DEFAULT 'UNIT',
    current_stock   DOUBLE PRECISION NOT NULL DEFAULT 0,
    min_stock       DOUBLE PRECISION NOT NULL DEFAULT 0,
    unit_price      DOUBLE PRECISION NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_materials_org ON materials(organization_id);
CREATE INDEX IF NOT EXISTS idx_materials_status ON materials(record_status);
CREATE INDEX IF NOT EXISTS idx_materials_category ON materials(category_id);
