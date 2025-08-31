CREATE TABLE IF NOT EXISTS entry
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    position   INTEGER      NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version    BIGINT    DEFAULT 0,
    CONSTRAINT uk_entry_position UNIQUE (position)
);

CREATE INDEX IF NOT EXISTS idx_entry_position_brin ON entry USING BRIN (position);

CREATE INDEX IF NOT EXISTS idx_entry_position_btree ON entry USING BTREE (position);

CREATE INDEX IF NOT EXISTS idx_entry_created_at ON entry USING BTREE (created_at);