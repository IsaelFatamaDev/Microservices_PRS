-- =====================================================
-- V2: Create product_categories table
-- Microservice: vg-ms-inventory-purchases
-- =====================================================

CREATE TABLE IF NOT EXISTS product_categories (
    id              VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    organization_id VARCHAR(36)  NOT NULL,
    record_status   VARCHAR(10)  NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by      VARCHAR(100),
    category_name   VARCHAR(200) NOT NULL,
    description     VARCHAR(500)
);

CREATE INDEX IF NOT EXISTS idx_product_categories_org ON product_categories(organization_id);
CREATE INDEX IF NOT EXISTS idx_product_categories_status ON product_categories(record_status);
