-- Agregar columnas faltantes a payment_details para que coincida con PaymentDetailEntity
ALTER TABLE payment_details ADD COLUMN IF NOT EXISTS payment_type VARCHAR(30);
ALTER TABLE payment_details ADD COLUMN IF NOT EXISTS description VARCHAR(255);
ALTER TABLE payment_details ADD COLUMN IF NOT EXISTS period_month INTEGER;
ALTER TABLE payment_details ADD COLUMN IF NOT EXISTS period_year INTEGER;
ALTER TABLE payment_details ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW();

-- Hacer debt_id opcional (ya no es requerido en el nuevo flujo)
ALTER TABLE payment_details ALTER COLUMN debt_id DROP NOT NULL;
