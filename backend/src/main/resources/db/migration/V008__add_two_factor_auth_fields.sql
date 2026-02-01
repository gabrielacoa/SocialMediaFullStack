-- V008: Agregar campos para Two-Factor Authentication (2FA)
-- Fecha: 2026-01-26

ALTER TABLE users
ADD COLUMN two_factor_enabled BOOLEAN DEFAULT FALSE,
ADD COLUMN two_factor_secret VARCHAR(64) DEFAULT NULL;

-- Indice para consultas de usuarios con 2FA habilitado
CREATE INDEX idx_users_two_factor_enabled ON users(two_factor_enabled);
