const express = require('express');
const { exec } = require('child_process');
const QRCode = require('qrcode');
const fs = require('fs');
const app = express();

let currentTunnelUrl = 'exp://detectando-tunnel.exp.direct';
let expoStatus = 'Iniciando...';

// Função para detectar URL do tunnel do Expo
function detectTunnelUrl() {
  return new Promise((resolve) => {
    console.log('🔍 Detectando URL do tunnel...');
    
    // Método 1: Verificar logs do Expo para encontrar tunnel URL
    exec('cat /tmp/expo.log | grep -o "exp://[^\\s]*\\.exp\\.direct" | tail -1', (error, stdout) => {
      if (!error && stdout.trim()) {
        const url = stdout.trim();
        console.log(`✅ URL do tunnel encontrada nos logs: ${url}`);
        resolve(url);
        return;
      }
      
      // Método 2: Verificar se há URL tunnel no JSON do Expo
      exec('curl -s http://localhost:8081 2>/dev/null | grep -o "http://[^"]*\\.exp\\.direct"', (error2, stdout2) => {
        if (!error2 && stdout2.trim()) {
          const httpUrl = stdout2.trim();
          const expUrl = httpUrl.replace('http://', 'exp://').replace(':8081', '');
          console.log(`✅ URL do tunnel encontrada via API: ${expUrl}`);
          resolve(expUrl);
          return;
        }
        
        // Método 3: Extrair do JSON completo
        exec('curl -s http://localhost:8081 2>/dev/null', (error3, stdout3) => {
          if (!error3 && stdout3.includes('hostUri')) {
            try {
              const json = JSON.parse(stdout3);
              if (json.extra && json.extra.expoClient && json.extra.expoClient.hostUri) {
                const hostUri = json.extra.expoClient.hostUri;
                const expUrl = `exp://${hostUri}`;
                console.log(`✅ URL extraída do JSON: ${expUrl}`);
                resolve(expUrl);
                return;
              }
            } catch (e) {
              console.log('❌ Erro ao parsear JSON:', e.message);
            }
          }
          
          // Fallback: usar URL padrão
          console.log('⚠️ Usando URL padrão');
          resolve('exp://aguardando-tunnel.exp.direct');
        });
      });
    });
  });
}

// Função para verificar status do Expo
function checkExpoStatus() {
  exec('curl -s http://localhost:8081 2>/dev/null', (error, stdout) => {
    if (!error && stdout.includes('launchAsset')) {
      expoStatus = 'Tunnel conectado ✅';
    } else {
      expoStatus = 'Conectando tunnel... ⏳';
    }
  });
}

// Atualizar URL do tunnel a cada 10 segundos
setInterval(async () => {
  try {
    const newUrl = await detectTunnelUrl();
    if (newUrl && newUrl !== currentTunnelUrl && !newUrl.includes('aguardando')) {
      currentTunnelUrl = newUrl;
      console.log(`🔄 URL atualizada: ${currentTunnelUrl}`);
    }
    checkExpoStatus();
  } catch (error) {
    console.log('❌ Erro ao detectar URL:', error.message);
  }
}, 10000);

// Detectar URL inicial após 10 segundos
setTimeout(async () => {
  currentTunnelUrl = await detectTunnelUrl();
  console.log(`🎯 URL inicial detectada: ${currentTunnelUrl}`);
}, 10000);

// Rota principal que mostra QR code
app.get('/', async (req, res) => {
  try {
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
        <title>Old Dragon RPG Mobile - QR Code</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <style>
            body {
                background: linear-gradient(135deg, #1a1a1a, #2d2d2d);
                color: #fff;
                font-family: Arial, sans-serif;
                text-align: center;
                padding: 20px;
                margin: 0;
                min-height: 100vh;
            }
            .container {
                max-width: 600px;
                margin: 0 auto;
                background: rgba(0,0,0,0.3);
                border-radius: 15px;
                padding: 30px;
                box-shadow: 0 10px 30px rgba(0,0,0,0.5);
            }
            .title {
                font-size: 2.5em;
                margin: 0 0 10px 0;
                color: #4ecdc4;
                text-shadow: 2px 2px 4px rgba(0,0,0,0.5);
            }
            .subtitle {
                font-size: 1.2em;
                color: #ccc;
                margin-bottom: 30px;
            }
            .qr-section {
                background: white;
                padding: 20px;
                border-radius: 15px;
                display: inline-block;
                margin: 20px 0;
                box-shadow: 0 5px 15px rgba(0,0,0,0.3);
            }
            .url-section {
                background: #333;
                padding: 20px;
                border-radius: 10px;
                margin: 20px 0;
                border-left: 4px solid #4ecdc4;
            }
            .url-text {
                font-family: monospace;
                font-size: 1.1em;
                color: #4ecdc4;
                word-break: break-all;
                margin: 10px 0;
                background: #222;
                padding: 10px;
                border-radius: 5px;
            }
            .status {
                background: rgba(76, 175, 80, 0.2);
                border: 1px solid #4caf50;
                padding: 15px;
                border-radius: 8px;
                margin: 20px 0;
            }
            .instructions {
                text-align: left;
                background: rgba(255,255,255,0.1);
                padding: 20px;
                border-radius: 10px;
                margin: 20px 0;
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
            .warning {
                background: rgba(255, 193, 7, 0.2);
                border: 1px solid #ffc107;
                padding: 15px;
                border-radius: 8px;
                margin: 20px 0;
                color: #ffc107;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h1 class="title">🎮 Old Dragon RPG</h1>
            <p class="subtitle">📱 Escaneie o QR Code com Expo Go</p>
            
            <div class="qr-section">
                <img src="${qrCodeDataURL}" alt="QR Code" />
            </div>
            
            <div class="url-section">
                <strong>🔗 URL do Tunnel:</strong>
                <div class="url-text" id="tunnelUrl">${currentTunnelUrl}</div>
                <button class="copy-btn" onclick="copyUrl()">📋 Copiar URL</button>
            </div>
            
            <div class="status">
                <strong>📡 Status:</strong> ${expoStatus}<br>
                <strong>🕒 Última verificação:</strong> ${new Date().toLocaleTimeString()}
            </div>
            
            ${currentTunnelUrl.includes('detectando') || currentTunnelUrl.includes('aguardando') ? 
              '<div class="warning"><strong>⚠️ Aguardando:</strong> Tunnel ainda conectando... A URL será atualizada automaticamente.</div>' : ''}
            
            <div class="instructions">
                <h3>📋 Como conectar:</h3>
                <ol>
                    <li><strong>📱 Baixe Expo Go</strong> no seu celular (Android/iOS)</li>
                    <li><strong>📷 Escaneie o QR code</strong> acima com o Expo Go</li>
                    <li><strong>⌨️ Ou digite a URL</strong> manualmente no Expo Go</li>
                    <li><strong>⏳ Aguarde carregar</strong> (1-2 minutos na primeira vez)</li>
                    <li><strong>🎮 Jogue!</strong> Crie seu herói e explore! ⚔️🐉</li>
                </ol>
                
                <h3>🔧 Se não funcionar:</h3>
                <ul>
                    <li>Aguarde a URL do tunnel ser detectada</li>
                    <li>Verifique se está na mesma rede WiFi</li>
                    <li>Tente recarregar a página</li>
                </ul>
            </div>
            
            <p><em>🔄 Página atualiza automaticamente a cada 15 segundos</em></p>
        </div>
        
        <script>
            function copyUrl() {
                const url = document.getElementById('tunnelUrl').textContent;
                navigator.clipboard.writeText(url).then(() => {
                    alert('📋 URL copiada para a área de transferência!');
                }).catch(() => {
                    // Fallback para navegadores mais antigos
                    const textArea = document.createElement('textarea');
                    textArea.value = url;
                    document.body.appendChild(textArea);
                    textArea.select();
                    document.execCommand('copy');
                    document.body.removeChild(textArea);
                    alert('📋 URL copiada!');
                });
            }
            
            // Auto-refresh a cada 15 segundos
            setTimeout(() => location.reload(), 15000);
        </script>
    </body>
    </html>
    `;
    
    res.send(html);
  } catch (error) {
    res.status(500).send(`
      <h1>❌ Erro ao gerar QR Code</h1>
      <p>Erro: ${error.message}</p>
      <p>URL atual: ${currentTunnelUrl}</p>
      <p>Recarregando em 5 segundos...</p>
      <script>setTimeout(() => location.reload(), 5000);</script>
    `);
  }
});

// Rota para API que retorna informações
app.get('/api/status', (req, res) => {
  res.json({
    tunnelUrl: currentTunnelUrl,
    status: expoStatus,
    timestamp: new Date().toISOString()
  });
});

// Iniciar servidor na porta 3000 (diferente do Expo)
const PORT = 3000;
app.listen(PORT, '0.0.0.0', () => {
  console.log(`🖥️ Servidor web rodando em http://localhost:${PORT}`);
  console.log('📱 QR Code será gerado automaticamente');
  console.log('🔍 Detectando URL do tunnel Expo...');
});
