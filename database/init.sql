-- Inicialização do banco de dados RPG
USE rpg_database;

-- Tabela de usuários/jogadores
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabela de personagens
CREATE TABLE IF NOT EXISTS characters (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    class VARCHAR(50) NOT NULL,
    level INT DEFAULT 1,
    experience INT DEFAULT 0,
    health_points INT NOT NULL,
    max_health_points INT NOT NULL,
    mana_points INT DEFAULT 0,
    max_mana_points INT DEFAULT 0,
    strength INT DEFAULT 10,
    dexterity INT DEFAULT 10,
    constitution INT DEFAULT 10,
    intelligence INT DEFAULT 10,
    wisdom INT DEFAULT 10,
    charisma INT DEFAULT 10,
    armor_class INT DEFAULT 10,
    gold INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabela de campanhas/sessões
CREATE TABLE IF NOT EXISTS campaigns (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    master_id INT NOT NULL,
    max_players INT DEFAULT 6,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (master_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabela de participação em campanhas
CREATE TABLE IF NOT EXISTS campaign_players (
    id INT AUTO_INCREMENT PRIMARY KEY,
    campaign_id INT NOT NULL,
    character_id INT NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id) ON DELETE CASCADE,
    FOREIGN KEY (character_id) REFERENCES characters(id) ON DELETE CASCADE,
    UNIQUE KEY unique_campaign_character (campaign_id, character_id)
);

-- Tabela de itens
CREATE TABLE IF NOT EXISTS items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    item_type ENUM('weapon', 'armor', 'consumable', 'misc') NOT NULL,
    rarity ENUM('common', 'uncommon', 'rare', 'epic', 'legendary') DEFAULT 'common',
    value INT DEFAULT 0,
    weight DECIMAL(5,2) DEFAULT 0.0,
    properties JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de inventário dos personagens
CREATE TABLE IF NOT EXISTS character_inventory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    character_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT DEFAULT 1,
    is_equipped BOOLEAN DEFAULT FALSE,
    acquired_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (character_id) REFERENCES characters(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
);

-- Tabela de magias/habilidades
CREATE TABLE IF NOT EXISTS spells (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    level INT NOT NULL,
    school VARCHAR(50),
    casting_time VARCHAR(50),
    range_distance VARCHAR(50),
    components VARCHAR(100),
    duration VARCHAR(50),
    damage_dice VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de magias conhecidas pelos personagens
CREATE TABLE IF NOT EXISTS character_spells (
    id INT AUTO_INCREMENT PRIMARY KEY,
    character_id INT NOT NULL,
    spell_id INT NOT NULL,
    learned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (character_id) REFERENCES characters(id) ON DELETE CASCADE,
    FOREIGN KEY (spell_id) REFERENCES spells(id) ON DELETE CASCADE,
    UNIQUE KEY unique_character_spell (character_id, spell_id)
);

-- Tabela de logs de combate/eventos
CREATE TABLE IF NOT EXISTS game_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    campaign_id INT,
    character_id INT,
    log_type ENUM('combat', 'skill_check', 'spell_cast', 'item_use', 'level_up', 'other') NOT NULL,
    description TEXT NOT NULL,
    dice_roll VARCHAR(50),
    result_value INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id) ON DELETE SET NULL,
    FOREIGN KEY (character_id) REFERENCES characters(id) ON DELETE SET NULL
);

-- Tabela de configurações do sistema
CREATE TABLE IF NOT EXISTS system_config (
    id INT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value TEXT,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Inserir dados iniciais
INSERT INTO system_config (config_key, config_value, description) VALUES
('max_level', '20', 'Nível máximo dos personagens'),
('starting_gold', '100', 'Ouro inicial para novos personagens'),
('max_campaigns_per_user', '5', 'Máximo de campanhas que um usuário pode mestrar'),
('system_version', '1.0.0', 'Versão atual do sistema RPG')
ON DUPLICATE KEY UPDATE config_value = VALUES(config_value);

-- Inserir usuário admin padrão (senha: admin123)
INSERT INTO users (username, email, password_hash) VALUES
('admin', 'admin@rpg.com', '$2b$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj6hsxq5S/kS')
ON DUPLICATE KEY UPDATE username = VALUES(username);

-- Inserir alguns itens básicos
INSERT INTO items (name, description, item_type, rarity, value, weight) VALUES
('Espada Longa', 'Uma espada de lâmina longa e afiada', 'weapon', 'common', 15, 3.0),
('Armadura de Couro', 'Armadura leve feita de couro resistente', 'armor', 'common', 10, 10.0),
('Poção de Cura', 'Restaura pontos de vida quando consumida', 'consumable', 'common', 50, 0.5),
('Escudo de Madeira', 'Escudo básico feito de madeira reforçada', 'armor', 'common', 5, 6.0)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Inserir algumas magias básicas
INSERT INTO spells (name, description, level, school, casting_time, range_distance, components, duration, damage_dice) VALUES
('Míssil Mágico', 'Projéteis de energia mágica que sempre acertam o alvo', 1, 'Evocação', '1 ação', '36 metros', 'V, S', 'Instantâneo', '1d4+1'),
('Cura Ferimentos Leves', 'Restaura pontos de vida do alvo tocado', 1, 'Evocação', '1 ação', 'Toque', 'V, S', 'Instantâneo', '1d8+mod'),
('Detectar Magia', 'Revela a presença de magia num raio de 9 metros', 1, 'Adivinhação', '1 ação', '9 metros', 'V, S', '10 minutos', ''),
('Luz', 'Cria uma fonte de luz brilhante', 0, 'Evocação', '1 ação', 'Toque', 'V, M', '1 hora', '')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Índices para performance
CREATE INDEX idx_characters_user_id ON characters(user_id);
CREATE INDEX idx_characters_active ON characters(is_active);
CREATE INDEX idx_campaigns_master_id ON campaigns(master_id);
CREATE INDEX idx_campaigns_active ON campaigns(is_active);
CREATE INDEX idx_campaign_players_campaign_id ON campaign_players(campaign_id);
CREATE INDEX idx_campaign_players_character_id ON campaign_players(character_id);
CREATE INDEX idx_character_inventory_character_id ON character_inventory(character_id);
CREATE INDEX idx_character_spells_character_id ON character_spells(character_id);
CREATE INDEX idx_game_logs_campaign_id ON game_logs(campaign_id);
CREATE INDEX idx_game_logs_character_id ON game_logs(character_id);
CREATE INDEX idx_game_logs_created_at ON game_logs(created_at);

-- Triggers para atualizar updated_at automaticamente
DELIMITER $$
CREATE TRIGGER update_users_timestamp 
    BEFORE UPDATE ON users 
    FOR EACH ROW 
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$

CREATE TRIGGER update_characters_timestamp 
    BEFORE UPDATE ON characters 
    FOR EACH ROW 
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$

CREATE TRIGGER update_campaigns_timestamp 
    BEFORE UPDATE ON campaigns 
    FOR EACH ROW 
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$

CREATE TRIGGER update_system_config_timestamp 
    BEFORE UPDATE ON system_config 
    FOR EACH ROW 
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$
DELIMITER ;
