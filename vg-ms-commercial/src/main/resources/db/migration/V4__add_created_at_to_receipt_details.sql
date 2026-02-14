-- Add created_at column to receipt_details table
ALTER TABLE receipt_details
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT NOW();

-- Add index for created_at if needed for queries
CREATE INDEX IF NOT EXISTS idx_receipt_details_created_at ON receipt_details(created_at);
