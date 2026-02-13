-- =====================================================
-- V1: Create suppliers table
-- Microservice: vg-ms-inventory-purchases
-- =====================================================

CREATE TABLE IF NOT EXISTS suppliers (
    id              VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    organization_id VARCHAR(36)  NOT NULL,
    record_status   VARCHAR(10)  NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by      VARCHAR(100),
    supplier_name   VARCHAR(200) NOT NULL,
    ruc             VARCHAR(20)  UNIQUE,
    address         VARCHAR(300),
    phone           VARCHAR(20),
    email           VARCHAR(100),
    contact_person  VARCHAR(200)
);

CREATE INDEX IF NOT EXISTS idx_suppliers_org ON suppliers(organization_id);
CREATE INDEX IF NOT EXISTS idx_suppliers_status ON suppliers(record_status);
