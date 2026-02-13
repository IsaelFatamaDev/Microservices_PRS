CREATE TABLE IF NOT EXISTS users
(
    id              VARCHAR(36) PRIMARY KEY,

    organization_id VARCHAR(36)  NOT NULL,
    zone_id         VARCHAR(36),
    street_id       VARCHAR(36),

    record_status   VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(36),
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by      VARCHAR(36),

    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(150) NOT NULL,
    document_type   VARCHAR(10)  NOT NULL,
    document_number VARCHAR(20)  NOT NULL,
    email           VARCHAR(150),
    phone           VARCHAR(20),
    address         VARCHAR(250) NOT NULL,
    role            VARCHAR(20)  NOT NULL DEFAULT 'CLIENT',

    CONSTRAINT uk_users_document_number UNIQUE (document_number),
    CONSTRAINT chk_users_document_type CHECK (document_type IN ('DNI', 'RUC', 'CE')),
    CONSTRAINT chk_users_record_status CHECK (record_status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT chk_users_role CHECK (role IN ('SUPER_ADMIN', 'ADMIN', 'CLIENT')),
    CONSTRAINT chk_users_contact CHECK (email IS NOT NULL OR phone IS NOT NULL)
);

CREATE INDEX IF NOT EXISTS idx_users_organization_id ON users (organization_id);
CREATE INDEX IF NOT EXISTS idx_users_zone_id ON users (zone_id);
CREATE INDEX IF NOT EXISTS idx_users_record_status ON users (record_status);
CREATE INDEX IF NOT EXISTS idx_users_document_number ON users (document_number);
CREATE INDEX IF NOT EXISTS idx_users_role ON users (role);

-- Comentarios
COMMENT ON TABLE users IS 'Tabla de usuarios del sistema JASS Digital';
COMMENT ON COLUMN users.record_status IS 'ACTIVE = activo, INACTIVE = eliminado lógicamente';
COMMENT ON CONSTRAINT chk_users_contact ON users IS 'Al menos email o phone debe tener valor';
