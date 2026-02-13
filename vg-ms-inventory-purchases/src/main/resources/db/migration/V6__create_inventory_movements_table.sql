-- =====================================================
-- V6: Create inventory_movements table (Kardex)
-- Microservice: vg-ms-inventory-purchases
-- =====================================================

CREATE TABLE IF NOT EXISTS inventory_movements (
    id              VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    organization_id VARCHAR(36)  NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    material_id     VARCHAR(36)  NOT NULL,
    movement_type   VARCHAR(20)  NOT NULL,
    quantity        DOUBLE PRECISION NOT NULL DEFAULT 0,
    unit_price      DOUBLE PRECISION NOT NULL DEFAULT 0,
    previous_stock  DOUBLE PRECISION NOT NULL DEFAULT 0,
    new_stock       DOUBLE PRECISION NOT NULL DEFAULT 0,
    reference_id    VARCHAR(36),
    reference_type  VARCHAR(30),
    notes           VARCHAR(500)
);

CREATE INDEX IF NOT EXISTS idx_inventory_movements_org ON inventory_movements(organization_id);
CREATE INDEX IF NOT EXISTS idx_inventory_movements_material ON inventory_movements(material_id);
CREATE INDEX IF NOT EXISTS idx_inventory_movements_type ON inventory_movements(movement_type);
