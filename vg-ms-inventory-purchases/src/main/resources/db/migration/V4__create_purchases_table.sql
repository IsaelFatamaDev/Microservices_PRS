-- =====================================================
-- V4: Create purchases table
-- Microservice: vg-ms-inventory-purchases
-- =====================================================

CREATE TABLE IF NOT EXISTS purchases (
    id              VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    organization_id VARCHAR(36)  NOT NULL,
    record_status   VARCHAR(10)  NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by      VARCHAR(100),
    purchase_code   VARCHAR(50)  NOT NULL UNIQUE,
    supplier_id     VARCHAR(36)  NOT NULL,
    purchase_date   TIMESTAMP    NOT NULL DEFAULT NOW(),
    total_amount    DOUBLE PRECISION NOT NULL DEFAULT 0,
    purchase_status VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    invoice_number  VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_purchases_org ON purchases(organization_id);
CREATE INDEX IF NOT EXISTS idx_purchases_status ON purchases(record_status);
CREATE INDEX IF NOT EXISTS idx_purchases_supplier ON purchases(supplier_id);
CREATE INDEX IF NOT EXISTS idx_purchases_purchase_status ON purchases(purchase_status);
