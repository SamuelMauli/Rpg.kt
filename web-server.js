const express = require('express');
const { exec } = require('child_process');
const QRCode = require('qrcode');
const fs = require('fs');
const app = express();

let currentTunnelUrl = null;
let expoStatus = 'Iniciando...';

// Função para detectar URL do tunnel dinamicamente
function detectTunnelUrl() {
  return new Promise((resolve) => {
    // Tentar múltiplas formas de detectar a URL
    const methods = [
      // Método 1: Verificar logs do Expo
      () => {
        exec('npx expo url --tunnel 2>/dev/null', (error, stdout) => {
          if (!error && stdout.trim()) {
            const url = stdout.trim();
            if (url.startsWith('exp://')) {
              resolve(url);
              return;
            }
          }
        });
      },
      
      // Método 2: Verificar arquivo de estado do Expo
      () => {
        try {
          const expoDir = '/tmp/.expo';
          if (fs.existsSync(expoDir)) {
            const files = fs.readdirSync(expoDir);
            for (const file of files) {
              if (file.includes('state')) {
                const content = fs.readFileSync(`${expoDir}/${file}`, 'utf8');
                const match = content.match(/exp:\/\/[^"'\s]+/);
                if (match) {
                  resolve(match[0]);
                  return;
                }
              }
            }
          }
        } catch (e) {}
      },
      
      // Método 3: Verificar processo Expo
      () => {
        exec('ps aux | grep expo | grep tunnel', (error, stdout) => {
          if (!error) {
            const match = stdout.match(/exp:\/\/[^\s]+/);
            if (match) {
              resolve(match[0]);
              return;
            }
          }
        });
      },
      
      // Método 4: Verificar logs do Metro
      () => {
        exec('curl -s http://localhost:19000/logs 2>/dev/null', (error, stdout) => {
          if (!error) {
            const match = stdout.match(/exp:\/\/[^"'\s]+/);
            if (match) {
              resolve(match[0]);
              return;
            }
          }
        });
      }
    ];
    
    // Executar métodos sequencialmente
    let methodIndex = 0;
    const tryNextMethod = () => {
      if (methodIndex < methods.length) {
        methods[methodIndex]();
        methodIndex++;
        setTimeout(tryNextMethod, 1000);
      } else {
        // Se nenhum método funcionou, usar URL padrão
        resolve('exp://tunnel-url-detectando.exp.direct');
      }
    };
    
    tryNextMethod();
  });
}

// Função para verificar status do Expo
function checkExpoStatus() {
  exec('curl -s http://localhost:8081 2>/dev/null', (error, stdout) => {
    if (!error && stdout.includes('launchAsset')) {
      expoStatus = 'Conectado ✅';
    } else {
      expoStatus = 'Iniciando... ⏳';
    }
  });
}

// Atualizar URL do tunnel a cada 5 segundos
setInterval(async () => {
  try {
    const newUrl = await detectTunnelUrl();
    if (newUrl && newUrl !== currentTunnelUrl) {
      currentTunnelUrl = newUrl;
      console.log(`🔄 Nova URL detectada: ${currentTunnelUrl}`);
    }
    checkExpoStatus();
  } catch (error) {
    console.log('❌ Erro ao detectar URL:', error.message);
  }
}, 5000);

// Detectar URL inicial
detectTunnelUrl().then(url => {
  currentTunnelUrl = url;
  console.log(`🎯 URL inicial: ${currentTunnelUrl}`);
});

// Rota principal que mostra QR code dinâmico
app.get('/', async (req, res) => {
  try {
    // Se ainda não temos URL, tentar detectar novamente
    if (!currentTunnelUrl) {
      currentTunnelUrl = await detectTunnelUrl();
    }
    
    // Gerar QR code com URL atual
    const qrCodeDataURL = await QRCode.toDataURL(currentTunnelUrl, {
      width: 300,
      margin: 2,
      color: {
        dark: '#000000',
        light: '#FFFFFF'
      }
    });
    
    const html = `
    <!DOCTYPE html>
    <html>
    <head>
        <title>Old Dragon RPG Mobile - QR Code Dinâmico</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <style>
            body {
                background: linear-gradient(135deg, #1a1a1a, #2d2d2d);
                color: #fff;
                font-family: 'Segoe UI', Arial, sans-serif;
                text-align: center;
                padding: 20px;
                margin: 0;
                min-height: 100vh;
            }
            .container {
                max-width: 800px;
                margin: 0 auto;
                background: rgba(0,0,0,0.3);
                border-radius: 15px;
                padding: 30px;
                box-shadow: 0 10px 30px rgba(0,0,0,0.5);
            }
            .header {
                margin-bottom: 30px;
            }
            .title {
                font-size: 2.5em;
                margin: 0;
                background: linear-gradient(45deg, #ff6b6b, #4ecdc4);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
                background-clip: text;
            }
            .subtitle {
                font-size: 1.2em;
                color: #ccc;
                margin: 10px 0;
            }
            .qr-section {
                background: white;
                padding: 30px;
                border-radius: 15px;
                display: inline-block;
                margin: 20px 0;
                box-shadow: 0 5px 15px rgba(0,0,0,0.3);
            }
            .qr-code img {
                border-radius: 10px;
            }
            .url-section {
                background: #333;
                padding: 20px;
                border-radius: 10px;
                margin: 20px 0;
                border-left: 4px solid #4ecdc4;
            }
            .url-text {
                font-family: 'Courier New', monospace;
                font-size: 1.1em;
                color: #4ecdc4;
                word-break: break-all;
                margin: 10px 0;
            }
            .instructions {
                text-align: left;
                background: rgba(255,255,255,0.1);
                padding: 25px;
                border-radius: 10px;
                margin: 20px 0;
                backdrop-filter: blur(10px);
            }
            .instructions h3 {
                color: #ff6b6b;
                margin-top: 0;
            }
            .instructions ol {
                line-height: 1.8;
            }
            .instructions li {
                margin: 10px 0;
            }
            .status {
                background: rgba(76, 175, 80, 0.2);
                border: 1px solid #4caf50;
                padding: 15px;
                border-radius: 8px;
                margin: 20px 0;
                font-weight: bold;
            }
            .refresh-info {
                color: #999;
                font-size: 0.9em;
                margin-top: 20px;
            }
            .copy-btn {
                background: #4ecdc4;
                color: white;
                border: none;
                padding: 10px 20px;
                border-radius: 5px;
                cursor: pointer;
                margin: 10px;
                font-size: 1em;
            }
            .copy-btn:hover {
                background: #45b7aa;
            }
            @keyframes pulse {
                0% { opacity: 1; }
                50% { opacity: 0.7; }
                100% { opacity: 1; }
            }
            .loading {
                animation: pulse 2s infinite;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="header">
                <h1 class="title">🎮 Old Dragon RPG Mobile</h1>
                <p class="subtitle">📱 Escaneie o QR Code com Expo Go</p>
            </div>
            
            <div class="qr-section">
                <div class="qr-code">
                    <img src="${qrCodeDataURL}" alt="QR Code Dinâmico" />
                </div>
            </div>
            
            <div class="url-section">
                <strong>🔗 URL Dinâmica Atual:</strong>
                <div class="url-text" id="tunnelUrl">${currentTunnelUrl}</div>
                <button class="copy-btn" onclick="copyUrl()">📋 Copiar URL</button>
            </div>
            
            <div class="status">
                <strong>📡 Status:</strong> ${expoStatus}
                <br>
                <strong>🔄 Última atualização:</strong> ${new Date().toLocaleTimeString()}
            </div>
            
            <div class="instructions">
                <h3>📋 Como conectar:</h3>
                <ol>
                    <li><strong>📱 Baixe Expo Go</strong> no seu celular (Android/iOS)</li>
                    <li><strong>📷 Escaneie o QR code</strong> acima com o Expo Go</li>
                    <li><strong>⌨️ Ou digite a URL</strong> manualmente no Expo Go</li>
                    <li><strong>⏳ Aguarde carregar</strong> (1-2 minutos na primeira vez)</li>
                    <li><strong>🎮 Jogue!</strong> Crie seu herói e explore dungeons! ⚔️🐉</li>
                </ol>
                
                <h3>🔧 Solução de problemas:</h3>
                <ul>
                    <li><strong>QR code não funciona:</strong> Use a URL manual</li>
                    <li><strong>App não carrega:</strong> Aguarde mais tempo ou recarregue</li>
                    <li><strong>Erro de conexão:</strong> Verifique se está na mesma rede WiFi</li>
                </ul>
            </div>
            
            <div class="refresh-info">
                <em>🔄 Página atualiza automaticamente a cada 10 segundos para detectar nova URL</em>
            </div>
        </div>
        
        <script>
            function copyUrl() {
                const url = document.getElementById('tunnelUrl').textContent;
                navigator.clipboard.writeText(url).then(() => {
                    alert('📋 URL copiada para a área de transferência!');
                });
            }
            
            // Atualizar página a cada 10 segundos para pegar nova URL
            setTimeout(() => {
                location.reload();
            }, 10000);
            
            // Mostrar loading enquanto detecta URL
            if (document.getElementById('tunnelUrl').textContent.includes('detectando')) {
                document.querySelector('.qr-section').classList.add('loading');
            }
        </script>
    </body>
    </html>
    `;
    
    res.send(html);
  } catch (error) {
    res.status(500).send(`
      <h1>❌ Erro</h1>
      <p>Erro ao gerar QR code: ${error.message}</p>
      <p>Tentando detectar URL do tunnel...</p>
      <script>setTimeout(() => location.reload(), 3000);</script>
    `);
  }
});

// Rota para API que retorna URL atual
app.get('/api/tunnel-url', (req, res) => {
  res.json({
    url: currentTunnelUrl,
    status: expoStatus,
    timestamp: new Date().toISOString()
  });
});

// Iniciar servidor na porta 8081
app.listen(8081, '0.0.0.0', () => {
  console.log('🖥️ Servidor web dinâmico rodando em http://localhost:8081');
  console.log('📱 QR Code dinâmico será detectado automaticamente');
  console.log('🔄 URL do tunnel será atualizada em tempo real');
});
