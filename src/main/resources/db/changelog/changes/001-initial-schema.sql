--liquibase formatted sql

--changeset equipo:001-initial-schema
--comment: Estructura inicial del chat con soporte E2E
-- 1) users: usuarios del sistema
CREATE TABLE users (
                       id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       email VARCHAR(150) DEFAULT NULL UNIQUE,
                       password_hash VARCHAR(255) DEFAULT NULL, -- si usas login local (bcrypt)
                       is_active TINYINT(1) DEFAULT 1,
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       last_seen DATETIME DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2) user_profiles: información pública/editable (opcional)
CREATE TABLE user_profiles (
                               user_id INT UNSIGNED NOT NULL PRIMARY KEY,
                               display_name VARCHAR(100) DEFAULT NULL,
                               avatar_url VARCHAR(255) DEFAULT NULL,
                               bio VARCHAR(200) DEFAULT NULL,
                               updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               CONSTRAINT fk_user_profiles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3) devices: dispositivos del usuario y clave pública para E2E (simplificado)
CREATE TABLE devices (
                         id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                         user_id INT UNSIGNED NOT NULL,
                         device_name VARCHAR(100) DEFAULT NULL,
                         public_key VARCHAR(300) DEFAULT NULL, -- llave pública del dispositivo (E2E)
                         created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                         INDEX idx_devices_user (user_id),
                         CONSTRAINT fk_devices_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4.0.1 contact_statuses
CREATE TABLE contact_statuses (
                                  id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                  code VARCHAR(20) NOT NULL UNIQUE,     -- ej. 'PENDING',ACCEPTED,BLOCKED y REMOVED
                                  label VARCHAR(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4) contacts: contactos (amistades / libreta de direcciones)
CREATE TABLE contacts (
                          id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          owner_id INT UNSIGNED NOT NULL,       -- usuario que tiene el contacto
                          contact_user_id INT UNSIGNED NOT NULL,-- id del usuario que es el contacto
                          contact_status_id INT UNSIGNED NOT NULL DEFAULT 1, -- ENUM('pending','accepted','blocked','removed') referencia a contact_statuses (1 = pending)
                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                          updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          UNIQUE KEY ux_owner_contact (owner_id, contact_user_id),
                          INDEX idx_contacts_owner (owner_id),
                          INDEX idx_contacts_status (contact_status_id),
                          CONSTRAINT fk_contacts_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
                          CONSTRAINT fk_contacts_contact_user FOREIGN KEY (contact_user_id) REFERENCES users(id) ON DELETE CASCADE,
                          CONSTRAINT fk_contacts_status FOREIGN KEY (contact_status_id) REFERENCES contact_statuses(id) ON DELETE RESTRICT,
                          CHECK (owner_id <> contact_user_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5.0.1
CREATE TABLE IF NOT EXISTS conversation_types (
                                                  id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                                  code VARCHAR(50) NOT NULL UNIQUE,    --  (ej. 'DIRECT' GROUP)
    label VARCHAR(100) DEFAULT NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- 5) conversations: conversaciones (directas o grupales)   CHECAR................................
CREATE TABLE conversations (
                               id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                               conversation_type_id INT UNSIGNED NOT NULL DEFAULT 1,	-- ENUM('direct','group')
                               title VARCHAR(200) DEFAULT NULL,    -- nombre del grupo (si aplica)
                               created_by INT UNSIGNED DEFAULT NULL,
                               created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                               last_message_at DATETIME DEFAULT NULL, --  Para ordenar chats por actividad reciente sin subconsultas pesadas
                               INDEX idx_conversations_creator (created_by),
                               INDEX idx_conversations_last_msg (last_message_at), -- Índice para el ordenamiento
                               CONSTRAINT fk_conversations_creator FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
                               CONSTRAINT fk_conversation_type FOREIGN KEY (conversation_type_id ) REFERENCES conversation_types(id) ON DELETE RESTRICT

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6.0.1 role_statuses
CREATE TABLE role_statuses (
                               id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                               code VARCHAR(10) NOT NULL UNIQUE,     -- ej. 'MEMBER',ADMIN,OWNER
                               label VARCHAR(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6) conversation_members: miembros de cada conversación
CREATE TABLE conversation_members (
                                      id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                      conversation_id INT UNSIGNED NOT NULL,
                                      user_id INT UNSIGNED NOT NULL,
                                      role_status_id INT UNSIGNED NOT NULL DEFAULT 1,	-- ENUM('member','admin','owner')
                                      joined_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                      UNIQUE KEY ux_conv_user (conversation_id, user_id),
                                      INDEX idx_conv_members_user (user_id),
                                      CONSTRAINT fk_conv_members_conv FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
                                      CONSTRAINT fk_conv_members_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                      CONSTRAINT fk_conv_members_role FOREIGN KEY (role_status_id) REFERENCES role_statuses(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7.0.1 message_types
CREATE TABLE message_types (
                               id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                               code VARCHAR(10) NOT NULL UNIQUE,     -- valor técnico (ej. 'TEXT','FILE' y 'SYSTEM')
                               label VARCHAR(30) DEFAULT NULL      -- etiqueta para UI
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7) messages: mensajes por conversación
CREATE TABLE messages (
                          id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          conversation_id INT UNSIGNED NOT NULL,
                          sender_id INT UNSIGNED DEFAULT NULL,
                          message_type_id INT UNSIGNED NOT NULL DEFAULT 1, -- referencia message_types (1 = TEXT)
                          content TEXT NOT NULL COMMENT 'Ciphertext en Base64', -- TEXT para soportar la longitud del Base64 del ciphertext + auth tag
                          iv VARCHAR(50) DEFAULT NULL COMMENT 'Vector de inicialización (IV) para AES/GCM en Base64', --  El Vector de Inicialización (12 bytes) es crítico para GCM.
    -- [NUEVO] Checksum/Hash opcional para verificar integridad post-descifrado si fuera necesario,
    -- aunque GCM ya tiene Auth Tag integrado. (Opcional, no lo agrego para no complicar).
                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                          edited_at DATETIME DEFAULT NULL,
                          deleted_at DATETIME DEFAULT NULL, --  Para borrado lógico (Req implícito de gestión de historial)
                          CONSTRAINT fk_messages_conv FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
                          CONSTRAINT fk_messages_sender FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE SET NULL,
                          CONSTRAINT fk_messages_type FOREIGN KEY (message_type_id) REFERENCES message_types(id) ON DELETE RESTRICT,
                          INDEX idx_messages_sender (sender_id),
                          INDEX idx_conv_created (conversation_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 8) message_status: estado del mensaje por destinatario (entregado/ leído)
CREATE TABLE message_status (
                                id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                message_id INT UNSIGNED NOT NULL,
                                recipient_id INT UNSIGNED NOT NULL,
                                delivered TINYINT(1) DEFAULT 0,
                                delivered_at DATETIME DEFAULT NULL,
                                reading TINYINT(1) DEFAULT 0,
                                read_at DATETIME DEFAULT NULL,
                                UNIQUE KEY ux_msg_recipient (message_id, recipient_id),
                                FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,
                                FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9) attachments: archivos adjuntos (metadatos)
CREATE TABLE attachments (
                             id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                             message_id INT UNSIGNED NOT NULL,
                             filename VARCHAR(255) DEFAULT NULL,
                             mime_type VARCHAR(100) DEFAULT NULL,
                             size BIGINT DEFAULT 0,
                             storage_url VARCHAR(500) DEFAULT NULL, -- URL a S3/MinIO o ruta
                             checksum VARCHAR(100) DEFAULT NULL,     -- SHA256 o similar
                             created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                             INDEX idx_attachments_message (message_id),
                             CONSTRAINT fk_attachments_message FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10) sessions: sesiones/refresh tokens (simplificado)
CREATE TABLE sessions (
                          id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          user_id INT UNSIGNED NOT NULL,
                          refresh_token_hash VARCHAR(255) DEFAULT NULL,
                          device_info VARCHAR(255) DEFAULT NULL,
                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                          expires_at DATETIME DEFAULT NULL,
                          INDEX idx_sessions_user (user_id),
                          CONSTRAINT fk_sessions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11) audit_events: logs básicos (conexiones, desconexiones, mensajes)
CREATE TABLE audit_events (
                              id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                              user_id INT UNSIGNED DEFAULT NULL,
                              event_type VARCHAR(50) NOT NULL,
                              event_data VARCHAR(200) DEFAULT NULL,
                              created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                              INDEX idx_audit_user (user_id),
                              CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;