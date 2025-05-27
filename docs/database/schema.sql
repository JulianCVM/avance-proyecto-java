-- Script de creación de base de datos para AVANCE
-- Sistema de Creación de IA Personalizada

-- Crear la base de datos (ejecutar si no existe)
-- CREATE DATABASE avancedb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE avancedb;

-- Crear tabla de usuarios
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    active BIT NOT NULL DEFAULT 1,
    created_at DATETIME,
    last_login DATETIME
);

-- Tabla de roles de usuario
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabla de tokens para restablecer contraseña
CREATE TABLE password_reset_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date DATETIME NOT NULL,
    is_used BIT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabla de agentes de IA
CREATE TABLE agents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    purpose TEXT,
    tone VARCHAR(100),
    active BIT NOT NULL DEFAULT 1,
    model_config TEXT,
    domain_context TEXT,
    api_token VARCHAR(255) NOT NULL,
    model_provider VARCHAR(50),
    created_at DATETIME,
    updated_at DATETIME,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabla de temas permitidos para agentes
CREATE TABLE agent_allowed_topics (
    agent_id BIGINT NOT NULL,
    topic VARCHAR(255) NOT NULL,
    PRIMARY KEY (agent_id, topic),
    FOREIGN KEY (agent_id) REFERENCES agents(id)
);

-- Tabla de temas restringidos para agentes
CREATE TABLE agent_restricted_topics (
    agent_id BIGINT NOT NULL,
    topic VARCHAR(255) NOT NULL,
    PRIMARY KEY (agent_id, topic),
    FOREIGN KEY (agent_id) REFERENCES agents(id)
);

-- Tabla de agente por defecto (chatbot informativo)
CREATE TABLE default_agent (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    system_prompt TEXT,
    api_token VARCHAR(255),
    model_provider VARCHAR(50) NOT NULL,
    active BIT NOT NULL DEFAULT 1,
    created_at DATETIME,
    updated_at DATETIME
);

-- Tabla de sesiones de chat
CREATE TABLE chat_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    agent_id BIGINT,
    title VARCHAR(255),
    created_at DATETIME,
    last_activity DATETIME,
    active BIT DEFAULT 1,
    context_info TEXT,
    total_tokens_used INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (agent_id) REFERENCES agents(id)
);

-- Tabla de mensajes
CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chat_session_id BIGINT,
    content TEXT,
    role VARCHAR(50) NOT NULL,
    timestamp DATETIME,
    token_count INT DEFAULT 0,
    flagged BIT DEFAULT 0,
    flag_reason VARCHAR(255),
    model_used VARCHAR(100),
    FOREIGN KEY (chat_session_id) REFERENCES chat_sessions(id)
);

-- Tabla de sesiones de chat del chatbot por defecto
CREATE TABLE default_chat_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    session_key VARCHAR(255) UNIQUE,
    created_at DATETIME,
    last_activity DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabla de mensajes del chatbot por defecto
CREATE TABLE default_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chat_session_id BIGINT,
    content TEXT,
    role VARCHAR(50) NOT NULL,
    timestamp DATETIME,
    FOREIGN KEY (chat_session_id) REFERENCES default_chat_sessions(id)
);

-- Tabla de tokens de API
CREATE TABLE api_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_value VARCHAR(255) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    is_active BIT NOT NULL DEFAULT 1,
    name VARCHAR(100),
    created_at DATETIME,
    last_used DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabla de uso de tokens para seguimiento
CREATE TABLE token_usage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    agent_id BIGINT,
    input_tokens INT DEFAULT 0,
    output_tokens INT DEFAULT 0,
    model VARCHAR(100) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    timestamp DATETIME,
    session_id VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (agent_id) REFERENCES agents(id)
); 