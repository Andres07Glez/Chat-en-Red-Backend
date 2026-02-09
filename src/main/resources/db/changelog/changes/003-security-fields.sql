--liquibase formatted sql

--changeset equipo:003-security-updates context:dev
--comment: Agrega campos para llaves publicas (E2E) y control de lectura

-- 1. Columna para guardar la llave pública ECDH (Base64)
-- Usamos TEXT (o LONGTEXT) porque las llaves RSA/ECC pueden ser largas
ALTER TABLE users ADD public_key TEXT;

-- 2. Columna para saber cuándo leyó el usuario por última vez
-- Necesario para calcular los badges de mensajes no leídos
ALTER TABLE conversation_members ADD last_read_at DATETIME;