-- =====================================================================
--  Pi.Na MKT API  —  Script de criação do schema (MySQL 8+)
--  Gerado a partir das entidades JPA em src/main/java/.../entities
--
--  Observações:
--   * IDs são BIGINT AUTO_INCREMENT (entidades usam Long + IDENTITY).
--   * Tabelas `USER` e `COLUMN` são palavras reservadas -> usam crase (`).
--   * Booleans -> TINYINT(1) (1 = true, 0 = false).
--   * LocalDateTime -> DATETIME.  byte[] @Lob -> LONGBLOB.
--   * Ordem de criação respeita as dependências de chave estrangeira.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS apipina
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE apipina;

-- ---------------------------------------------------------------------
-- 1. ROLE
-- ---------------------------------------------------------------------
CREATE TABLE `ROLE` (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(50),
    access_key  VARCHAR(50),
    description VARCHAR(255),
    created_at  DATETIME,
    updated_at  DATETIME,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 2. COMPANY
-- ---------------------------------------------------------------------
CREATE TABLE `COMPANY` (
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    name               VARCHAR(100),
    slug               VARCHAR(100),
    is_active          TINYINT(1)   DEFAULT 1,
    google_calendar_id VARCHAR(255),
    created_at         DATETIME,
    updated_at         DATETIME,
    PRIMARY KEY (id),
    UNIQUE KEY uk_company_slug (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 3. USER
-- ---------------------------------------------------------------------
CREATE TABLE `USER` (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    name           VARCHAR(100),
    email          VARCHAR(150),
    password       VARCHAR(255),
    avatar_url     VARCHAR(500),
    is_active      TINYINT(1)   DEFAULT 1,
    created_at     DATETIME,
    updated_at     DATETIME,
    phone          VARCHAR(20),
    job_title      VARCHAR(255),          -- entidade não define length (DER sugeria 45)
    seniority      VARCHAR(255),          -- entidade não define length (DER sugeria 45)
    responsibility TEXT,
    bio            VARCHAR(500),
    linkedin       VARCHAR(255),
    -- department  VARCHAR(45),           -- existia no DER, mas NÃO existe na entidade User
    Role_id        BIGINT,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_email (email),
    CONSTRAINT fk_user_role FOREIGN KEY (Role_id) REFERENCES `ROLE` (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 4. BOARD
-- ---------------------------------------------------------------------
CREATE TABLE `BOARD` (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    name             VARCHAR(100),
    description      VARCHAR(500),
    background_color VARCHAR(7),
    is_active        TINYINT(1)   DEFAULT 1,
    created_at       DATETIME,
    updated_at       DATETIME,
    Company_id       BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_board_company FOREIGN KEY (Company_id) REFERENCES `COMPANY` (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 5. COLUMN
-- ---------------------------------------------------------------------
CREATE TABLE `COLUMN` (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    name       VARCHAR(50),
    `position` INT,
    created_at DATETIME,
    updated_at DATETIME,
    Board_id   BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_column_board FOREIGN KEY (Board_id) REFERENCES `BOARD` (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 6. CARD
-- ---------------------------------------------------------------------
CREATE TABLE `CARD` (
    id                       BIGINT       NOT NULL AUTO_INCREMENT,
    title                    VARCHAR(255),
    description              TEXT,
    priority                 ENUM('LOW','MEDIUM','HIGH','CRITICAL'),
    `position`               INT,
    due_date                 DATETIME,
    is_active                TINYINT(1)   DEFAULT 1,
    completed                TINYINT(1)   DEFAULT 0,
    google_calendar_event_id VARCHAR(255),
    created_at               DATETIME,
    updated_at               DATETIME,
    Column_id                BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_card_column FOREIGN KEY (Column_id) REFERENCES `COLUMN` (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 7. ATTACHMENT
-- ---------------------------------------------------------------------
CREATE TABLE `ATTACHMENT` (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    file_name      VARCHAR(255) NOT NULL,
    file_size      BIGINT,
    content_type   VARCHAR(100),
    file_data      LONGBLOB     NOT NULL,
    company_id     BIGINT       NOT NULL,
    uploaded_by_id BIGINT,
    created_at     DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT fk_attachment_company FOREIGN KEY (company_id)     REFERENCES `COMPANY` (id),
    CONSTRAINT fk_attachment_user    FOREIGN KEY (uploaded_by_id) REFERENCES `USER` (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 8. BOARD_USER  (N:N entre BOARD e USER)
-- ---------------------------------------------------------------------
CREATE TABLE `BOARD_USER` (
    board_id BIGINT NOT NULL,
    user_id  BIGINT NOT NULL,
    PRIMARY KEY (board_id, user_id),
    CONSTRAINT fk_board_user_board FOREIGN KEY (board_id) REFERENCES `BOARD` (id),
    CONSTRAINT fk_board_user_user  FOREIGN KEY (user_id)  REFERENCES `USER` (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 9. USER_CARD  (N:N entre CARD e USER — usuários atribuídos ao card)
-- ---------------------------------------------------------------------
CREATE TABLE `USER_CARD` (
    card_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (card_id, user_id),
    CONSTRAINT fk_user_card_card FOREIGN KEY (card_id) REFERENCES `CARD` (id),
    CONSTRAINT fk_user_card_user FOREIGN KEY (user_id) REFERENCES `USER` (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- (OPCIONAL) Dados iniciais.
-- O DataInitializer do backend já cria estes papéis e o admin no boot,
-- então só rode este bloco se quiser popular o banco manualmente.
-- A senha do admin abaixo é apenas placeholder; o backend grava o hash BCrypt.
-- =====================================================================
-- INSERT INTO `ROLE` (name, access_key, description) VALUES
--     ('ADMIN',   'ROLE_ADMIN',   'Administrador do sistema'),
--     ('MANAGER', 'ROLE_MANAGER', 'Gestor de projetos'),
--     ('USER',    'ROLE_USER',    'Usuário padrão');
