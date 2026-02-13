-- Tabla de deudas
CREATE TABLE IF NOT EXISTS debts (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    organization_id VARCHAR(36) NOT NULL,
    record_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(36),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(36),
    user_id VARCHAR(36) NOT NULL,
    period_month INTEGER NOT NULL,
    period_year INTEGER NOT NULL,
    original_amount NUMERIC(10,2) NOT NULL,
    pending_amount NUMERIC(10,2) NOT NULL,
    late_fee NUMERIC(10,2) DEFAULT 0,
    debt_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    due_date TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_debts_organization_id ON debts(organization_id);
CREATE INDEX IF NOT EXISTS idx_debts_user_id ON debts(user_id);
CREATE INDEX IF NOT EXISTS idx_debts_status ON debts(debt_status);
CREATE INDEX IF NOT EXISTS idx_debts_period ON debts(period_year, period_month);

-- Tabla de pagos
CREATE TABLE IF NOT EXISTS payments (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    organization_id VARCHAR(36) NOT NULL,
    record_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(36),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(36),
    user_id VARCHAR(36) NOT NULL,
    payment_date TIMESTAMP NOT NULL DEFAULT NOW(),
    total_amount NUMERIC(10,2) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    receipt_number VARCHAR(50),
    notes TEXT
);

CREATE INDEX IF NOT EXISTS idx_payments_organization_id ON payments(organization_id);
CREATE INDEX IF NOT EXISTS idx_payments_user_id ON payments(user_id);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(payment_status);

-- Tabla de detalles de pago
CREATE TABLE IF NOT EXISTS payment_details (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    payment_id VARCHAR(36) NOT NULL REFERENCES payments(id),
    debt_id VARCHAR(36) NOT NULL REFERENCES debts(id),
    amount NUMERIC(10,2) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_payment_details_payment ON payment_details(payment_id);

-- Tabla de recibos
CREATE TABLE IF NOT EXISTS receipts (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    organization_id VARCHAR(36) NOT NULL,
    record_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(36),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(36),
    receipt_number VARCHAR(50) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    period_month INTEGER NOT NULL,
    period_year INTEGER NOT NULL,
    issue_date TIMESTAMP NOT NULL DEFAULT NOW(),
    due_date TIMESTAMP,
    total_amount NUMERIC(10,2) NOT NULL,
    paid_amount NUMERIC(10,2) DEFAULT 0,
    pending_amount NUMERIC(10,2) NOT NULL,
    receipt_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    notes TEXT
);

CREATE INDEX IF NOT EXISTS idx_receipts_organization_id ON receipts(organization_id);
CREATE INDEX IF NOT EXISTS idx_receipts_user_id ON receipts(user_id);
CREATE INDEX IF NOT EXISTS idx_receipts_status ON receipts(receipt_status);
CREATE INDEX IF NOT EXISTS idx_receipts_period ON receipts(period_year, period_month);
CREATE INDEX IF NOT EXISTS idx_receipts_number ON receipts(receipt_number);

-- Tabla de detalles de recibo
CREATE TABLE IF NOT EXISTS receipt_details (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    receipt_id VARCHAR(36) NOT NULL REFERENCES receipts(id),
    concept_type VARCHAR(30) NOT NULL,
    description VARCHAR(255),
    amount NUMERIC(10,2) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_receipt_details_receipt ON receipt_details(receipt_id);

-- Tabla de caja chica
CREATE TABLE IF NOT EXISTS petty_cash (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    organization_id VARCHAR(36) NOT NULL,
    record_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(36),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(36),
    responsible_user_id VARCHAR(36) NOT NULL,
    current_balance NUMERIC(10,2) NOT NULL DEFAULT 0,
    max_amount_limit NUMERIC(10,2) NOT NULL,
    petty_cash_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);

CREATE INDEX IF NOT EXISTS idx_petty_cash_organization ON petty_cash(organization_id);
CREATE INDEX IF NOT EXISTS idx_petty_cash_status ON petty_cash(petty_cash_status);

-- Tabla de movimientos de caja chica
CREATE TABLE IF NOT EXISTS petty_cash_movements (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    organization_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(36),
    petty_cash_id VARCHAR(36) NOT NULL REFERENCES petty_cash(id),
    movement_date TIMESTAMP NOT NULL DEFAULT NOW(),
    movement_type VARCHAR(20) NOT NULL,
    amount NUMERIC(10,2) NOT NULL,
    category VARCHAR(30),
    description TEXT,
    voucher_number VARCHAR(50),
    previous_balance NUMERIC(10,2),
    new_balance NUMERIC(10,2)
);

CREATE INDEX IF NOT EXISTS idx_petty_movements_cash ON petty_cash_movements(petty_cash_id);

-- Tabla de cortes de servicio
CREATE TABLE IF NOT EXISTS service_cuts (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    organization_id VARCHAR(36) NOT NULL,
    record_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(36),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(36),
    user_id VARCHAR(36) NOT NULL,
    water_box_id VARCHAR(36),
    scheduled_date TIMESTAMP,
    executed_date TIMESTAMP,
    cut_reason VARCHAR(30) NOT NULL,
    debt_amount NUMERIC(10,2),
    reconnection_date TIMESTAMP,
    reconnection_fee_paid BOOLEAN DEFAULT FALSE,
    cut_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    notes TEXT
);

CREATE INDEX IF NOT EXISTS idx_service_cuts_organization ON service_cuts(organization_id);
CREATE INDEX IF NOT EXISTS idx_service_cuts_user ON service_cuts(user_id);
CREATE INDEX IF NOT EXISTS idx_service_cuts_status ON service_cuts(cut_status);
