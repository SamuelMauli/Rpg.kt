const express = require('express');
const mysql = require('mysql2/promise');
const cors = require('cors');
const jwt = require('jsonwebtoken');
const helmet = require('helmet');
const morgan = require('morgan');
const fs = require('fs');
const path = require('path');
const { exec } = require('child_process');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 5000;

// Middleware de segurança
app.use(helmet({
    contentSecurityPolicy: false // Permitir inline scripts para a interface
}));
app.use(cors());
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true }));
app.use(express.static('public')); // Servir arquivos estáticos

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
let currentTunnelUrl = 'exp://detectando-tunnel.exp.direct';
let expoStatus = 'Iniciando...';

// Inicializar conexão com banco
async function initDatabase() {
    try {
        db = mysql.createPool(dbConfig);
        console.log('✅ Conexão com MySQL estabelecida');
        
        const connection = await db.getConnection();
        await connection.ping();
        connection.release();
        console.log('✅ MySQL está respondendo');
    } catch (error) {
        console.error('❌ Erro ao conectar com MySQL:', error);
        process.exit(1);
    }
}

// Função para detectar URL do tunnel do Expo
function detectExpoTunnel() {
    exec('docker-compose logs expo-rpg 2>/dev/null | grep -o "exp://[^[:space:]]*" | tail -1', (error, stdout) => {
        if (!error && stdout.trim()) {
            const newUrl = stdout.trim();
            if (newUrl !== currentTunnelUrl) {
                currentTunnelUrl = newUrl;
                console.log('🔗 Novo tunnel detectado:', currentTunnelUrl);
            }
        }
    });
    
    exec('docker-compose ps expo-rpg --format json 2>/dev/null', (error, stdout) => {
        if (!error && stdout.trim()) {
            try {
                const container = JSON.parse(stdout);
                expoStatus = container.State || 'Desconhecido';
            } catch (e) {
                expoStatus = 'Erro ao verificar';
            }
        } else {
            expoStatus = 'Container não encontrado';
        }
    });
}

// Detectar tunnel a cada 10 segundos
setInterval(detectExpoTunnel, 10000);
detectExpoTunnel(); // Primeira execução

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

// Rota principal - Interface Web com QR Code
app.get('/', (req, res) => {
    const html = `
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Old Dragon RPG - Sistema Completo</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            color: white;
        }
        
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        
        .header {
            text-align: center;
            margin-bottom: 40px;
        }
        
        .header h1 {
            font-size: 3rem;
            margin-bottom: 10px;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
        }
        
        .header p {
            font-size: 1.2rem;
            opacity: 0.9;
        }
        
        .grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
            gap: 30px;
            margin-bottom: 40px;
        }
        
        .card {
            background: rgba(255, 255, 255, 0.1);
            backdrop-filter: blur(10px);
            border-radius: 15px;
            padding: 30px;
            border: 1px solid rgba(255, 255, 255, 0.2);
            transition: transform 0.3s ease;
        }
        
        .card:hover {
            transform: translateY(-5px);
        }
        
        .card h2 {
            font-size: 1.5rem;
            margin-bottom: 20px;
            color: #fff;
        }
        
        .qr-container {
            text-align: center;
            margin: 20px 0;
        }
        
        .qr-code {
            background: white;
            padding: 20px;
            border-radius: 10px;
            display: inline-block;
            margin: 10px 0;
        }
        
        .status {
            display: flex;
            align-items: center;
            margin: 10px 0;
            padding: 10px;
            background: rgba(0,0,0,0.2);
            border-radius: 8px;
        }
        
        .status-dot {
            width: 12px;
            height: 12px;
            border-radius: 50%;
            margin-right: 10px;
        }
        
        .status-online { background: #4CAF50; }
        .status-offline { background: #f44336; }
        .status-warning { background: #ff9800; }
        
        .url-box {
            background: rgba(0,0,0,0.3);
            padding: 15px;
            border-radius: 8px;
            margin: 10px 0;
            font-family: monospace;
            word-break: break-all;
        }
        
        .btn {
            background: linear-gradient(45deg, #4CAF50, #45a049);
            color: white;
            border: none;
            padding: 12px 24px;
            border-radius: 8px;
            cursor: pointer;
            font-size: 1rem;
            margin: 5px;
            transition: all 0.3s ease;
        }
        
        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.2);
        }
        
        .btn-secondary {
            background: linear-gradient(45deg, #2196F3, #1976D2);
        }
        
        .btn-danger {
            background: linear-gradient(45deg, #f44336, #d32f2f);
        }
        
        .api-section {
            margin-top: 20px;
        }
        
        .endpoint {
            background: rgba(0,0,0,0.2);
            padding: 10px;
            margin: 5px 0;
            border-radius: 5px;
            font-family: monospace;
        }
        
        .method {
            display: inline-block;
            padding: 2px 8px;
            border-radius: 4px;
            font-size: 0.8rem;
            font-weight: bold;
            margin-right: 10px;
        }
        
        .method-get { background: #4CAF50; }
        .method-post { background: #2196F3; }
        
        @media (max-width: 768px) {
            .header h1 { font-size: 2rem; }
            .grid { grid-template-columns: 1fr; }
            .card { padding: 20px; }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🐉 Old Dragon RPG</h1>
            <p>Sistema Completo - Backend + Mobile App</p>
        </div>
        
        <div class="grid">
            <!-- Expo Mobile App -->
            <div class="card">
                <h2>📱 Aplicativo Mobile</h2>
                <div class="qr-container">
                    <div class="qr-code">
                        <div id="qrcode"></div>
                    </div>
                    <p>Escaneie o QR Code com o app Expo Go</p>
                </div>
                
                <div class="status">
                    <div class="status-dot" id="expo-status"></div>
                    <span id="expo-text">Verificando...</span>
                </div>
                
                <div class="url-box" id="expo-url">
                    Detectando URL do tunnel...
                </div>
                
                <button class="btn" onclick="refreshExpo()">🔄 Atualizar</button>
                <button class="btn btn-secondary" onclick="startExpo()">▶️ Iniciar Expo</button>
            </div>
            
            <!-- Backend API -->
            <div class="card">
                <h2>🔧 Backend API</h2>
                
                <div class="status">
                    <div class="status-dot status-online"></div>
                    <span>Backend Online</span>
                </div>
                
                <div class="url-box">
                    http://localhost:5000
                </div>
                
                <div class="api-section">
                    <h3>Endpoints Principais:</h3>
                    <div class="endpoint">
                        <span class="method method-get">GET</span>
                        /api/health
                    </div>
                    <div class="endpoint">
                        <span class="method method-post">POST</span>
                        /api/auth/register
                    </div>
                    <div class="endpoint">
                        <span class="method method-post">POST</span>
                        /api/auth/login
                    </div>
                    <div class="endpoint">
                        <span class="method method-get">GET</span>
                        /api/campaigns
                    </div>
                </div>
                
                <button class="btn" onclick="testAPI()">🧪 Testar API</button>
                <button class="btn btn-secondary" onclick="openAPI()">📖 Documentação</button>
            </div>
            
            <!-- Sistema -->
            <div class="card">
                <h2>⚙️ Sistema</h2>
                
                <div class="status">
                    <div class="status-dot status-online"></div>
                    <span>MySQL Conectado</span>
                </div>
                
                <div class="status">
                    <div class="status-dot" id="ngrok-status"></div>
                    <span id="ngrok-text">ngrok: Verificando...</span>
                </div>
                
                <div class="url-box">
                    🌍 Público: https://nonlustrously-nonsustainable-ronny.ngrok-free.dev
                </div>
                
                <button class="btn" onclick="checkStatus()">📊 Status</button>
                <button class="btn btn-secondary" onclick="viewLogs()">📋 Logs</button>
                <button class="btn btn-danger" onclick="restartSystem()">🔄 Reiniciar</button>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/qrcode@1.5.3/build/qrcode.min.js"></script>
    <script>
        let currentUrl = '';
        
        // Gerar QR Code
        function generateQR(url) {
            const qrContainer = document.getElementById('qrcode');
            qrContainer.innerHTML = '';
            
            if (url && url !== 'exp://detectando-tunnel.exp.direct') {
                QRCode.toCanvas(qrContainer, url, {
                    width: 200,
                    margin: 2,
                    color: {
                        dark: '#000000',
                        light: '#FFFFFF'
                    }
                }, function (error) {
                    if (error) {
                        qrContainer.innerHTML = '<p>Erro ao gerar QR Code</p>';
                    }
                });
            } else {
                qrContainer.innerHTML = '<p>Aguardando tunnel...</p>';
            }
        }
        
        // Atualizar status do Expo
        function updateExpoStatus() {
            fetch('/api/expo-status')
                .then(response => response.json())
                .then(data => {
                    const statusDot = document.getElementById('expo-status');
                    const statusText = document.getElementById('expo-text');
                    const urlBox = document.getElementById('expo-url');
                    
                    if (data.url && data.url !== currentUrl) {
                        currentUrl = data.url;
                        generateQR(data.url);
                        urlBox.textContent = data.url;
                    }
                    
                    statusText.textContent = \`Expo: \${data.status}\`;
                    
                    if (data.status.includes('Up') || data.status.includes('running')) {
                        statusDot.className = 'status-dot status-online';
                    } else if (data.status.includes('Restarting')) {
                        statusDot.className = 'status-dot status-warning';
                    } else {
                        statusDot.className = 'status-dot status-offline';
                    }
                })
                .catch(error => {
                    console.error('Erro ao verificar status:', error);
                });
        }
        
        // Funções dos botões
        function refreshExpo() {
            updateExpoStatus();
        }
        
        function startExpo() {
            fetch('/api/start-expo', { method: 'POST' })
                .then(response => response.json())
                .then(data => {
                    alert(data.message || 'Comando enviado');
                    setTimeout(updateExpoStatus, 3000);
                });
        }
        
        function testAPI() {
            fetch('/api/health')
                .then(response => response.json())
                .then(data => {
                    alert(\`API Status: \${data.status}\\nDatabase: \${data.database}\\nVersion: \${data.version}\`);
                })
                .catch(error => {
                    alert('Erro ao testar API: ' + error.message);
                });
        }
        
        function openAPI() {
            window.open('/api/health', '_blank');
        }
        
        function checkStatus() {
            window.open('/status', '_blank');
        }
        
        function viewLogs() {
            window.open('/logs', '_blank');
        }
        
        function restartSystem() {
            if (confirm('Tem certeza que deseja reiniciar o sistema?')) {
                fetch('/api/restart', { method: 'POST' })
                    .then(response => response.json())
                    .then(data => alert(data.message));
            }
        }
        
        // Atualizar status a cada 5 segundos
        setInterval(updateExpoStatus, 5000);
        updateExpoStatus(); // Primeira execução
        
        // Gerar QR inicial
        generateQR('exp://detectando-tunnel.exp.direct');
    </script>
</body>
</html>`;
    
    res.send(html);
});

// API para status do Expo
app.get('/api/expo-status', (req, res) => {
    res.json({
        url: currentTunnelUrl,
        status: expoStatus
    });
});

// API para iniciar Expo
app.post('/api/start-expo', (req, res) => {
    exec('docker-compose up -d expo-rpg', (error, stdout, stderr) => {
        if (error) {
            res.json({ success: false, message: 'Erro ao iniciar Expo: ' + error.message });
        } else {
            res.json({ success: true, message: 'Expo iniciado com sucesso' });
        }
    });
});

// Rotas da API RPG (mantidas do web-server-enhanced.js)
app.post('/api/auth/register', async (req, res) => {
    try {
        const { username, email, password } = req.body;

        if (!username || !email || !password) {
            return res.status(400).json({ error: 'Todos os campos são obrigatórios' });
        }

        const [existing] = await db.execute(
            'SELECT id FROM users WHERE username = ? OR email = ?',
            [username, email]
        );

        if (existing.length > 0) {
            return res.status(409).json({ error: 'Usuário ou email já existe' });
        }

        const passwordHash = password; // Senha simples para desenvolvimento

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

        if (password !== user.password_hash) {
            return res.status(401).json({ error: 'Credenciais inválidas' });
        }

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

// Outras rotas da API...
app.get('/api/campaigns', async (req, res) => {
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

app.get('/api/items', async (req, res) => {
    try {
        const [items] = await db.execute('SELECT * FROM items ORDER BY name');
        res.json({ success: true, items });
    } catch (error) {
        console.error('Erro ao buscar itens:', error);
        res.status(500).json({ error: 'Erro interno do servidor' });
    }
});

app.get('/api/spells', async (req, res) => {
    try {
        const [spells] = await db.execute('SELECT * FROM spells ORDER BY level, name');
        res.json({ success: true, spells });
    } catch (error) {
        console.error('Erro ao buscar magias:', error);
        res.status(500).json({ error: 'Erro interno do servidor' });
    }
});

// Health check
app.get('/api/health', async (req, res) => {
    try {
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

// Inicializar servidor
async function startServer() {
    try {
        await initDatabase();
        
        app.listen(PORT, '0.0.0.0', () => {
            console.log(`🚀 Servidor RPG Híbrido rodando na porta ${PORT}`);
            console.log(`🌐 Interface Web: http://localhost:${PORT}`);
            console.log(`🏥 Health check: http://localhost:${PORT}/api/health`);
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

startServer();
