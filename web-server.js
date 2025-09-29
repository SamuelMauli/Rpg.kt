const express = require('express');
const { exec } = require('child_process');
const QRCode = require('qrcode');
const app = express();

// Servir arquivos estáticos
app.use(express.static('public'));

// Rota principal que mostra QR code
app.get('/', async (req, res) => {
  try {
    // URL do tunnel Expo
    const tunnelUrl = 'exp://ut_vpkk-anonymous-8081.exp.direct';
    
    // Gerar QR code
    const qrCodeDataURL = await QRCode.toDataURL(tunnelUrl);
    
    const html = `
    <!DOCTYPE html>
    <html>
    <head>
        <title>Old Dragon RPG Mobile - QR Code</title>
        <style>
            body {
                background: #000;
                color: #fff;
                font-family: Arial, sans-serif;
                text-align: center;
                padding: 50px;
            }
            .container {
                max-width: 600px;
                margin: 0 auto;
            }
            .qr-code {
                background: white;
                padding: 20px;
                border-radius: 10px;
                display: inline-block;
                margin: 20px 0;
            }
            .url {
                background: #333;
                padding: 10px;
                border-radius: 5px;
                font-family: monospace;
                margin: 20px 0;
            }
            .instructions {
                text-align: left;
                background: #222;
                padding: 20px;
                border-radius: 10px;
                margin: 20px 0;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h1>🎮 Old Dragon RPG Mobile</h1>
            <h2>📱 Escaneie o QR Code com Expo Go</h2>
            
            <div class="qr-code">
                <img src="${qrCodeDataURL}" alt="QR Code" />
            </div>
            
            <div class="url">
                <strong>URL Manual:</strong><br>
                ${tunnelUrl}
            </div>
            
            <div class="instructions">
                <h3>📋 Como conectar:</h3>
                <ol>
                    <li><strong>Baixe Expo Go</strong> no seu celular</li>
                    <li><strong>Escaneie o QR code</strong> acima</li>
                    <li><strong>Ou digite a URL</strong> manualmente no Expo Go</li>
                    <li><strong>Aguarde carregar</strong> (1-2 minutos)</li>
                    <li><strong>Jogue!</strong> ⚔️🐉</li>
                </ol>
            </div>
            
            <p><em>Tunnel Status: Connected ✅</em></p>
        </div>
        
        <script>
            // Atualizar página a cada 30 segundos
            setTimeout(() => location.reload(), 30000);
        </script>
    </body>
    </html>
    `;
    
    res.send(html);
  } catch (error) {
    res.status(500).send(`Erro: ${error.message}`);
  }
});

// Iniciar servidor na porta 8081
app.listen(8081, '0.0.0.0', () => {
  console.log('🖥️ Servidor web rodando em http://localhost:8081');
  console.log('📱 QR Code disponível na página');
});
