const express = require('express');
const mysql = require('mysql2/promise');
const cors = require('cors');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const helmet = require('helmet');
const morgan = require('morgan');
const fs = require('fs');
const path = require('path');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 5000;

// Middleware de segurança
app.use(helmet());
app.use(cors());
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true }));

// Logging
const logStream = fs.createWriteStream(path.join(__dirname, 'logs', 'access.log'), { flags: 'a' });
app.use(morgan('combined', { stream: logStream }));
app.use(morgan('dev'));

// Configuração do banco de dados
const dbConfig = {
    host: process.env.DB_HOST || 'mysql',
    user: process.env.MYSQL_USER || 'rpg_user',
    password: process.env.MYSQL_PASSWORD || 'rpg_pass_2024',
    database: process.env.MYSQL_DATABASE || 'rpg_database',
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
};

let db;

// Inicializar conexão com banco
async function initDatabase() {
    try {
        db = mysql.createPool(dbConfig);
        console.log('✅ Conexão com MySQL estabelecida');
        
        // Testar conexão
        const connection = await db.getConnection();
        await connection.ping();
        connection.release();
        console.log('✅ MySQL está respondendo');
    } catch (error) {
        console.error('❌ Erro ao conectar com MySQL:', error);
        process.exit(1);
    }
}

// Middleware de autenticação
const authenticateToken = (req, res, next) => {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1];

    if (!token) {
        return res.status(401).json({ error: 'Token de acesso requerido' });
    }

    jwt.verify(token, process.env.JWT_SECRET || 'rpg-system-jwt-secret-2024', (err, user) => {
        if (err) {
            return res.status(403).json({ error: 'Token inválido' });
        }
        req.user = user;
        next();
    });
};

// Rotas de autenticação
app.post('/api/auth/register', async (req, res) => {
    try {
        const { username, email, password } = req.body;

        if (!username || !email || !password) {
            return res.status(400).json({ error: 'Todos os campos são obrigatórios' });
        }

        // Verificar se usuário já existe
        const [existing] = await db.execute(
            'SELECT id FROM users WHERE username = ? OR email = ?',
            [username, email]
        );

        if (existing.length > 0) {
            return res.status(409).json({ error: 'Usuário ou email já existe' });
        }

        // Hash da senha
        const saltRounds = parseInt(process.env.BCRYPT_ROUNDS) || 12;
        const passwordHash = await bcrypt.hash(password, saltRounds);

        // Inserir usuário
        const [result] = await db.execute(
            'INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)',
            [username, email, passwordHash]
        );

        res.status(201).json({
            success: true,
            message: 'Usuário criado com sucesso',
            userId: result.insertId
        });
    } catch (error) {
        console.error('Erro no registro:', error);
        res.status(500).json({ error: 'Erro interno do servidor' });
    }
});

app.post('/api/auth/login', async (req, res) => {
    try {
        const { username, password } = req.body;

        if (!username || !password) {
            return res.status(400).json({ error: 'Username e senha são obrigatórios' });
        }

        // Buscar usuário
        const [users] = await db.execute(
            'SELECT id, username, email, password_hash, is_active FROM users WHERE username = ?',
            [username]
        );

        if (users.length === 0) {
            return res.status(401).json({ error: 'Credenciais inválidas' });
        }

        const user = users[0];

        if (!user.is_active) {
            return res.status(401).json({ error: 'Usuário inativo' });
        }

        // Verificar senha
        const validPassword = await bcrypt.compare(password, user.password_hash);
        if (!validPassword) {
            return res.status(401).json({ error: 'Credenciais inválidas' });
        }

        // Gerar token JWT
        const token = jwt.sign(
            { userId: user.id, username: user.username },
            process.env.JWT_SECRET || 'rpg-system-jwt-secret-2024',
            { expiresIn: '24h' }
        );

        res.json({
            success: true,
            token,
            user: {
                id: user.id,
                username: user.username,
                email: user.email
            }
        });
    } catch (error) {
        console.error('Erro no login:', error);
        res.status(500).json({ error: 'Erro interno do servidor' });
    }
});

// Rotas de personagens
app.get('/api/characters', authenticateToken, async (req, res) => {
    try {
        const [characters] = await db.execute(
            'SELECT * FROM characters WHERE user_id = ? AND is_active = true ORDER BY created_at DESC',
            [req.user.userId]
        );

        res.json({ success: true, characters });
    } catch (error) {
        console.error('Erro ao buscar personagens:', error);
        res.status(500).json({ error: 'Erro interno do servidor' });
    }
});

app.post('/api/characters', authenticateToken, async (req, res) => {
    try {
        const {
            name, class: characterClass, level = 1, health_points, max_health_points,
            mana_points = 0, max_mana_points = 0, strength = 10, dexterity = 10,
            constitution = 10, intelligence = 10, wisdom = 10, charisma = 10,
            armor_class = 10, gold = 100
        } = req.body;

        if (!name || !characterClass || !health_points || !max_health_points) {
            return res.status(400).json({ error: 'Campos obrigatórios: name, class, health_points, max_health_points' });
        }

        const [result] = await db.execute(
            `INSERT INTO characters (
                user_id, name, class, level, health_points, max_health_points,
                mana_points, max_mana_points, strength, dexterity, constitution,
                intelligence, wisdom, charisma, armor_class, gold
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
            [
                req.user.userId, name, characterClass, level, health_points, max_health_points,
                mana_points, max_mana_points, strength, dexterity, constitution,
                intelligence, wisdom, charisma, armor_class, gold
            ]
        );

        res.status(201).json({
            success: true,
            message: 'Personagem criado com sucesso',
            characterId: result.insertId
        });
    } catch (error) {
        console.error('Erro ao criar personagem:', error);
        res.status(500).json({ error: 'Erro interno do servidor' });
    }
});

// Rotas de campanhas
app.get('/api/campaigns', authenticateToken, async (req, res) => {
    try {
        const [campaigns] = await db.execute(
            `SELECT c.*, u.username as master_name,
                    COUNT(cp.character_id) as player_count
             FROM campaigns c
             LEFT JOIN users u ON c.master_id = u.id
             LEFT JOIN campaign_players cp ON c.id = cp.campaign_id AND cp.is_active = true
             WHERE c.is_active = true
             GROUP BY c.id
             ORDER BY c.created_at DESC`
        );

        res.json({ success: true, campaigns });
    } catch (error) {
        console.error('Erro ao buscar campanhas:', error);
        res.status(500).json({ error: 'Erro interno do servidor' });
    }
});

app.post('/api/campaigns', authenticateToken, async (req, res) => {
    try {
        const { name, description, max_players = 6 } = req.body;

        if (!name) {
            return res.status(400).json({ error: 'Nome da campanha é obrigatório' });
        }

        const [result] = await db.execute(
            'INSERT INTO campaigns (name, description, master_id, max_players) VALUES (?, ?, ?, ?)',
            [name, description, req.user.userId, max_players]
        );

        res.status(201).json({
            success: true,
            message: 'Campanha criada com sucesso',
            campaignId: result.insertId
        });
    } catch (error) {
        console.error('Erro ao criar campanha:', error);
        res.status(500).json({ error: 'Erro interno do servidor' });
    }
});

// Rotas de itens
app.get('/api/items', async (req, res) => {
    try {
        const [items] = await db.execute('SELECT * FROM items ORDER BY name');
        res.json({ success: true, items });
    } catch (error) {
        console.error('Erro ao buscar itens:', error);
        res.status(500).json({ error: 'Erro interno do servidor' });
    }
});

// Rotas de magias
app.get('/api/spells', async (req, res) => {
    try {
        const [spells] = await db.execute('SELECT * FROM spells ORDER BY level, name');
        res.json({ success: true, spells });
    } catch (error) {
        console.error('Erro ao buscar magias:', error);
        res.status(500).json({ error: 'Erro interno do servidor' });
    }
});

// Rota de health check
app.get('/api/health', async (req, res) => {
    try {
        // Testar conexão com banco
        const connection = await db.getConnection();
        await connection.ping();
        connection.release();

        res.json({
            status: 'healthy',
            timestamp: new Date().toISOString(),
            database: 'connected',
            version: '1.0.0'
        });
    } catch (error) {
        res.status(503).json({
            status: 'unhealthy',
            timestamp: new Date().toISOString(),
            database: 'disconnected',
            error: error.message
        });
    }
});

// Rota de estatísticas
app.get('/api/stats', authenticateToken, async (req, res) => {
    try {
        const [userStats] = await db.execute(
            `SELECT 
                (SELECT COUNT(*) FROM characters WHERE user_id = ? AND is_active = true) as total_characters,
                (SELECT COUNT(*) FROM campaigns WHERE master_id = ? AND is_active = true) as total_campaigns_master,
                (SELECT COUNT(*) FROM campaign_players cp 
                 JOIN characters c ON cp.character_id = c.id 
                 WHERE c.user_id = ? AND cp.is_active = true) as total_campaigns_player`,
            [req.user.userId, req.user.userId, req.user.userId]
        );

        res.json({ success: true, stats: userStats[0] });
    } catch (error) {
        console.error('Erro ao buscar estatísticas:', error);
        res.status(500).json({ error: 'Erro interno do servidor' });
    }
});

// Middleware de tratamento de erros
app.use((err, req, res, next) => {
    console.error('Erro não tratado:', err);
    res.status(500).json({ error: 'Erro interno do servidor' });
});

// Rota 404
app.use('*', (req, res) => {
    res.status(404).json({ error: 'Rota não encontrada' });
});

// Inicializar servidor
async function startServer() {
    try {
        await initDatabase();
        
        app.listen(PORT, '0.0.0.0', () => {
            console.log(`🚀 Servidor RPG rodando na porta ${PORT}`);
            console.log(`🌐 Health check: http://localhost:${PORT}/api/health`);
            console.log(`📊 Ambiente: ${process.env.NODE_ENV || 'development'}`);
        });
    } catch (error) {
        console.error('❌ Erro ao iniciar servidor:', error);
        process.exit(1);
    }
}

// Tratamento de sinais
process.on('SIGTERM', async () => {
    console.log('🛑 Recebido SIGTERM, fechando servidor...');
    if (db) {
        await db.end();
    }
    process.exit(0);
});

process.on('SIGINT', async () => {
    console.log('🛑 Recebido SIGINT, fechando servidor...');
    if (db) {
        await db.end();
    }
    process.exit(0);
});

// Iniciar servidor
startServer();
