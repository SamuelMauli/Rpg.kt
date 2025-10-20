#!/bin/bash

echo "========================================"
echo "  OLD DRAGON 2 RPG - Mobile Setup"
echo "========================================"
echo ""

# Verificar se Node.js está instalado
if ! command -v node &> /dev/null; then
    echo "❌ Node.js não encontrado!"
    echo ""
    echo "Instale o Node.js:"
    echo "  curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -"
    echo "  sudo apt install nodejs"
    exit 1
fi

echo "✓ Node.js: $(node --version)"
echo "✓ npm: $(npm --version)"
echo ""

# Verificar se node_modules existe
if [ ! -d "node_modules" ]; then
    echo "📦 Instalando dependências..."
    echo "   Isso pode levar alguns minutos..."
    echo ""
    npm install
    
    if [ $? -ne 0 ]; then
        echo ""
        echo "❌ Erro ao instalar dependências!"
        echo ""
        echo "Tente:"
        echo "  rm -rf node_modules package-lock.json"
        echo "  npm install"
        exit 1
    fi
    
    echo ""
    echo "✅ Dependências instaladas!"
else
    echo "✓ Dependências já instaladas"
fi

echo ""
echo "========================================"
echo "  Iniciando Expo..."
echo "========================================"
echo ""
echo "Para acessar:"
echo "  📱 Mobile: Escaneie o QR code com Expo Go"
echo "  🌐 Web: Pressione 'w' no terminal"
echo "  🤖 Android: Pressione 'a' no terminal"
echo "  🍎 iOS: Pressione 'i' no terminal"
echo ""
echo "Para parar: Pressione Ctrl+C"
echo ""

# Iniciar Expo
npm start

