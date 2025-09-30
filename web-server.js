const express = require('express');
const { exec } = require('child_process');
const QRCode = require('qrcode');
const fs = require('fs');
const app = express();

let currentTunnelUrl = 'exp://detectando-tunnel.exp.direct';
let expoStatus = 'Iniciando...';

// Função simplificada para detectar URL do tunnel
function detectTunnelUrl() {
  return new Promise((resolve) => {
    console.log('🔍 Detectando URL do tunnel...');
    
    // Método 1: Verificar logs do Expo
    exec('cat /tmp/expo.log | grep -o "exp://[^\\s]*" | tail -1', (error, stdout) => {
      if (!error && stdout.trim()) {
        const url = stdout.trim();
        console.log(`✅ URL encontrada nos logs: ${url}`);
        resolve(url);
        return;
      }
      
      // Método 2: Tentar comando expo url
      exec('cd /app && npx expo url --tunnel 2>/dev/null', (error2, stdout2) => {
        if (!error2 && stdout2.trim() && stdout2.includes('exp://')) {
          const url = stdout2.trim();
          console.log(`✅ URL encontrada via comando: ${url}`);
          resolve(url);
          return;
        }
        
        // Método 3: Verificar se há processo expo rodando
        exec('ps aux | grep expo | grep -v grep', (error3, stdout3) => {
          if (!error3 && stdout3) {
            console.log('📡 Processo Expo detectado, usando URL padrão');
            // Gerar URL baseada no hostname do container
            exec('hostname -i', (error4, ip) => {
              if (!error4 && ip.trim()) {
                const containerIp = ip.trim();
                resolve(`exp://${containerIp}:19000`);
              } else {
                resolve('exp://tunnel-em-processo.exp.direct');
              }
            });
          } else {
            console.log('❌ Nenhum processo Expo encontrado');
            resolve('exp://aguardando-tunnel.exp.direct');
          }
        });
      });
    });
  });
}

// Função para verificar status do Expo
function checkExpoStatus() {
  exec('ps aux | grep expo | grep -v grep', (error, stdout) => {
    if (!error && stdout) {
      expoStatus = 'Expo rodando ✅';
    } else {
      expoStatus = 'Iniciando Expo... ⏳';
    }
  });
}

// Atualizar URL do tunnel a cada 10 segundos
setInterval(async () => {
  try {
    const newUrl = await detectTunnelUrl();
    if (newUrl && newUrl !== currentTunnelUrl) {
      currentTunnelUrl = newUrl;
      console.log(`🔄 URL atualizada: ${currentTunnelUrl}`);
    }
    checkExpoStatus();
  } catch (error) {
    console.log('❌ Erro ao detectar URL:', error.message);
  }
}, 10000);

// Detectar URL inicial após 5 segundos
setTimeout(async () => {
  currentTunnelUrl = await detectTunnelUrl();
  console.log(`🎯 URL inicial detectada: ${currentTunnelUrl}`);
}, 5000);

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
            }
            .copy-btn:hover {
                background: #45b7aa;
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
                <strong>🔗 URL Atual:</strong>
                <div class="url-text" id="tunnelUrl">${currentTunnelUrl}</div>
                <button class="copy-btn" onclick="copyUrl()">📋 Copiar</button>
            </div>
            
            <div class="status">
                <strong>📡 Status:</strong> ${expoStatus}<br>
                <strong>🕒 Atualizado:</strong> ${new Date().toLocaleTimeString()}
            </div>
            
            <div class="instructions">
                <h3>📋 Como conectar:</h3>
                <ol>
                    <li><strong>Baixe Expo Go</strong> no celular</li>
                    <li><strong>Escaneie o QR code</strong> acima</li>
                    <li><strong>Ou digite a URL</strong> manualmente</li>
                    <li><strong>Aguarde carregar</strong> (1-2 min)</li>
                    <li><strong>Jogue!</strong> ⚔️🐉</li>
                </ol>
            </div>
            
            <p><em>🔄 Página atualiza automaticamente a cada 15 segundos</em></p>
        </div>
        
        <script>
            function copyUrl() {
                const url = document.getElementById('tunnelUrl').textContent;
                navigator.clipboard.writeText(url).then(() => {
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
      <h1>❌ Erro</h1>
      <p>Erro: ${error.message}</p>
      <p>URL atual: ${currentTunnelUrl}</p>
      <script>setTimeout(() => location.reload(), 5000);</script>
    `);
  }
});

// Iniciar servidor
app.listen(8081, '0.0.0.0', () => {
  console.log('🖥️ Servidor web rodando em http://localhost:8081');
  console.log('📱 QR Code será gerado automaticamente');
});
