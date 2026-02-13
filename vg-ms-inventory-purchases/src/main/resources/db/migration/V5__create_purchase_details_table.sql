-- =====================================================
-- V5: Create purchase_details table
-- Microservice: vg-ms-inventory-purchases
-- =====================================================

CREATE TABLE IF NOT EXISTS purchase_details (
    id              VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    purchase_id     VARCHAR(36)      NOT NULL,
    material_id     VARCHAR(36)      NOT NULL,
    quantity        DOUBLE PRECISION NOT NULL DEFAULT 0,
    unit_price      DOUBLE PRECISION NOT NULL DEFAULT 0,
    subtotal        DOUBLE PRECISION NOT NULL DEFAULT 0,
    created_at      TIMESTAMP        NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_purchase_details_purchase ON purchase_details(purchase_id);
CREATE INDEX IF NOT EXISTS idx_purchase_details_material ON purchase_details(material_id);
