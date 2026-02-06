--liquibase formatted sql

--changeset equipo:002-initial-data context:dev
--comment: Datos de prueba iniciales (ajustados para E2E y optimización de listas)

-- ------------------------
-- 1) Usuarios
-- ------------------------
INSERT INTO users (id, username, email, password_hash, is_active, created_at, last_seen)
VALUES
    (1, 'andres', 'andres@example.com', '$2a$10$examplehashandres', 1, '2025-11-01 10:00:00', '2025-11-10 18:00:00'),
    (2, 'maria',  'maria@example.com',  '$2a$10$examplehashmaria',  1, '2025-11-02 09:30:00', '2025-11-10 17:55:00'),
    (3, 'juan',   'juan@example.com',   NULL,                     1, '2025-11-05 08:20:00', NULL),
    (4, 'soporte','soporte@example.com','$2a$10$examplehashsoporte',1, '2025-11-06 12:00:00', NULL);

-- ------------------------
-- 2) Perfiles de usuario
-- ------------------------
INSERT INTO user_profiles (user_id, display_name, avatar_url, bio, updated_at)
VALUES
    (1, 'Andrés G.', 'https://ui-avatars.com/api/?name=Andres+G', 'Estudiante de Ingeniería - Oaxaca', '2025-11-01 10:00:00'),
    (2, 'María L.',  'https://ui-avatars.com/api/?name=Maria+L',  'Desarrolladora frontend', '2025-11-02 09:30:00'),
    (3, 'Juan P.',   NULL, NULL, '2025-11-05 08:20:00');

-- ------------------------
-- 3) Devices
-- ------------------------
INSERT INTO devices (id, user_id, device_name, public_key, created_at)
VALUES
    (1, 1, 'Andres-PC', 'pubkey-andres-pc-ABC123', '2025-11-10 17:50:00'),
    (2, 1, 'Andres-Phone', 'pubkey-andres-phone-XYZ456', '2025-11-10 17:55:00'),
    (3, 2, 'Maria-Laptop', 'pubkey-maria-lap-AAA111', '2025-11-10 17:40:00');

-- ------------------------
-- 4) Catálogo de Estados de Contacto
-- ------------------------
INSERT INTO contact_statuses (id, code, label) VALUES
                                                   (1, 'PENDING',  'Pendiente'),
                                                   (2, 'ACCEPTED', 'Aceptado'),
                                                   (3, 'BLOCKED',  'Bloqueado'),
                                                   (4, 'REMOVED',  'Eliminado')
    ON DUPLICATE KEY UPDATE code = code;

-- ------------------------
-- Contactos
-- ------------------------
INSERT INTO contacts (id, owner_id, contact_user_id, contact_status_id, created_at, updated_at)
VALUES
    (1, 1, 2, 1, '2025-11-03 11:00:00', '2025-11-03 11:00:00'),
    (2, 1, 3, 1,  '2025-11-09 15:00:00', '2025-11-09 15:00:00'),
    (3, 2, 1, 2, '2025-11-03 11:00:00', '2025-11-03 11:00:00');

-- ------------------------
-- 5) Tipos de Conversación
-- ------------------------
INSERT INTO conversation_types (id, code, label) VALUES
                                                     (1, 'DIRECT',  'Directo'),
                                                     (2, 'GROUP', 'Grupo')
    ON DUPLICATE KEY UPDATE code = code;

-- ------------------------
-- Conversaciones
-- AJUSTE IMPORTANTE: Se agrega valor a 'last_message_at' basado en los mensajes de prueba
-- ------------------------
INSERT INTO conversations (id, conversation_type_id, title, created_by, created_at, last_message_at)
VALUES
    -- Conv 1: Directo Andres-Maria. Último mensaje fue a las 10:01:30
    (1, 1, NULL, 1, '2025-11-03 11:05:00', '2025-11-07 10:01:30'),
    -- Conv 2: Grupo Proyecto. Último mensaje fue a las 14:30:00
    (2, 2, 'Proyecto Chat', 1, '2025-11-07 09:00:00', '2025-11-08 14:30:00');

-- ------------------------
-- 6) Roles
-- ------------------------
INSERT INTO role_statuses (id, code, label) VALUES
                                                (1, 'MEMBER', 'Miembro'),
                                                (2, 'ADMIN',  'Administrador'),
                                                (3, 'OWNER',  'Propietario')
    ON DUPLICATE KEY UPDATE code = code;

INSERT INTO conversation_members (id, conversation_id, user_id, role_status_id, joined_at)
VALUES
    (1, 1, 1, 3, '2025-11-03 11:05:00'),
    (2, 1, 2, 1, '2025-11-03 11:05:00'),
    (3, 2, 1, 3, '2025-11-07 09:00:00'),
    (4, 2, 2, 1, '2025-11-07 09:01:00'),
    (5, 2, 3, 1, '2025-11-07 09:02:00');

-- ------------------------
-- 7) Tipos de Mensaje
-- ------------------------
INSERT INTO message_types (id, code, label) VALUES
                                                (1, 'TEXT',  'Texto'),
                                                (2, 'FILE',  'Archivo'),
                                                (3, 'SYSTEM','Sistema')
    ON DUPLICATE KEY UPDATE code = code;

-- ------------------------
-- Mensajes
-- AJUSTE IMPORTANTE: Se agrega la columna 'iv' (dummy para pruebas)
-- ------------------------
INSERT INTO messages (id, conversation_id, sender_id, message_type_id, content, iv, created_at, edited_at)
VALUES
    -- Chat Directo
    (1, 1, 1, 1, 'Hola María, ¿cómo estás?', 'dummy_iv_001', '2025-11-07 10:00:00', NULL),
    (2, 1, 2, 1, 'Hola Andrés, bien. ¿Y tú?', 'dummy_iv_002', '2025-11-07 10:01:30', NULL),

    -- Grupo
    (3, 2, 1, 1, 'Chicos, subi el diseño al repositorio.', 'dummy_iv_003', '2025-11-08 14:20:00', NULL),
    (4, 2, 2, 1, 'Perfecto, lo reviso hoy en la tarde.', 'dummy_iv_004', '2025-11-08 14:22:00', NULL),

    -- Archivo (Referencia en content y tabla attachments)
    (5, 2, 1, 2, 'Adjunto: especificacion_v1.pdf', 'dummy_iv_005', '2025-11-08 14:30:00', NULL),

    -- Mensaje Sistema (Sin sender, IV opcional o nulo)
    (6, 2, NULL, 3, 'Usuario juan se unió al grupo.', NULL, '2025-11-07 09:02:00', NULL);

-- ------------------------
-- 8) Estados de Mensajes
-- ------------------------
INSERT INTO message_status (id, message_id, recipient_id, delivered, delivered_at, reading, read_at)
VALUES
    (1, 1, 2, 1, '2025-11-07 10:00:10', 1, '2025-11-07 10:02:00'),
    (2, 2, 1, 1, '2025-11-07 10:01:40', 1, '2025-11-07 10:02:10'),

    -- Grupo (destinatarios multiples)
    (3, 3, 2, 1, '2025-11-08 14:20:05', 0, NULL),
    (4, 3, 3, 1, '2025-11-08 14:20:06', 0, NULL),
    (5, 4, 1, 1, '2025-11-08 14:22:05', 1, '2025-11-08 16:00:00'),
    (6, 4, 3, 1, '2025-11-08 14:22:06', 0, NULL),
    (7, 5, 2, 1, '2025-11-08 14:30:05', 0, NULL),
    (8, 5, 3, 1, '2025-11-08 14:30:06', 0, NULL),

    -- Sistema
    (9, 6, 1, 1, '2025-11-07 09:02:10', 1, '2025-11-07 09:05:00'),
    (10, 6, 2, 1, '2025-11-07 09:02:10', 1, '2025-11-07 09:05:20'),
    (11, 6, 3, 1, '2025-11-07 09:02:11', 1, '2025-11-07 09:06:00');

-- ------------------------
-- 9) Adjuntos
-- ------------------------
INSERT INTO attachments (id, message_id, filename, mime_type, size, storage_url, checksum, created_at)
VALUES
    (1, 5, 'especificacion_v1.pdf', 'application/pdf', 254321, 'https://minio.example.com/bucket/especificacion_v1.pdf', 'sha256:abcd1234efgh5678', '2025-11-08 14:30:02');

-- ------------------------
-- 10) Sesiones
-- ------------------------
INSERT INTO sessions (id, user_id, refresh_token_hash, device_info, created_at, expires_at)
VALUES
    (1, 1, 'hash-refresh-token-ejemplo', 'Andres-PC', '2025-11-10 17:50:00', '2025-12-10 17:50:00');

-- ------------------------
-- 11) Audit Events
-- ------------------------
INSERT INTO audit_events (id, user_id, event_type, event_data, created_at)
VALUES
    (1, 1, 'connection', 'IP=192.168.0.10; socket=abc123', '2025-11-10 17:50:05'),
    (2, 2, 'connection', 'IP=192.168.0.11; socket=def456', '2025-11-10 17:55:10'),
    (3, 3, 'join_group', 'conversation_id=2; invited_by=1', '2025-11-07 09:02:00');