--liquibase formatted sql

--changeset equipo:004-group-keys context:dev
--comment: Tabla para distribuir la llave simétrica de los grupos

CREATE TABLE conversation_keys (
                                   id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,  -- Recomendado usar UNSIGNED aquí también
                                   conversation_id INT UNSIGNED NOT NULL,       -- <--- CORREGIDO: Agregado UNSIGNED
                                   user_id INT UNSIGNED NOT NULL,               -- <--- CORREGIDO: Agregado UNSIGNED

                                   encrypted_key TEXT NOT NULL,

                                   iv VARCHAR(50) NOT NULL,

                                   CONSTRAINT fk_ck_conversation FOREIGN KEY (conversation_id) REFERENCES conversations(id),
                                   CONSTRAINT fk_ck_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE UNIQUE INDEX idx_conv_user_key ON conversation_keys(conversation_id, user_id);