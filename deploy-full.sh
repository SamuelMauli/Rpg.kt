#!/bin/bash

# Deploy completo com interface QR do Expo
set -e

echo "🐉 Old Dragon RPG - Deploy Completo"
echo "===================================="

# Parar ngrok existente
echo "⏹️  Parando ngrok existente..."
pkill ngrok 2>/dev/null || true

# Parar containers existentes
echo "⏹️  Parando containers..."
docker stop rpg_mysql rpg_backend old-dragon-rpg 2>/dev/null || true
docker rm rpg_mysql rpg_backend old-dragon-rpg 2>/dev/null || true

# Limpar imagens
echo "🧹 Limpando imagens..."
docker rmi rpgkt-backend rpgkt-expo-rpg 2>/dev/null || true

# Criar diretórios
mkdir -p logs uploads database

# Criar .env
echo "⚙️  Criando configuração..."
cat > .env << 'EOF'
NODE_ENV=production
PORT=5000
DATABASE_URL=mysql://rpg_user:rpg_pass_2024@mysql:3306/rpg_database
MYSQL_ROOT_PASSWORD=rpg_root_2024
MYSQL_DATABASE=rpg_database
MYSQL_USER=rpg_user
MYSQL_PASSWORD=rpg_pass_2024
EXPO_DEVTOOLS_LISTEN_ADDRESS=0.0.0.0
REACT_NATIVE_PACKAGER_HOSTNAME=0.0.0.0
JWT_SECRET=rpg-system-jwt-secret-2024
EOF

# Iniciar MySQL primeiro
echo "🗄️  Iniciando MySQL..."
docker-compose -f docker-compose-full.yml up -d mysql

# Aguardar MySQL
echo "⏳ Aguardando MySQL (30s)..."
sleep 30

# Construir e iniciar backend
echo "🔧 Construindo backend híbrido..."
docker-compose -f docker-compose-full.yml build --no-cache backend
docker-compose -f docker-compose-full.yml up -d backend

# Aguardar backend
echo "⏳ Aguardando backend (15s)..."
sleep 15

# Testar backend
echo "🧪 Testando backend..."
if curl -s http://localhost:5000/api/health > /dev/null; then
    echo "✅ Backend funcionando!"
else
    echo "❌ Backend com problemas"
    docker-compose -f docker-compose-full.yml logs backend
    exit 1
fi

# Construir e iniciar Expo
echo "📱 Construindo Expo..."
docker-compose -f docker-compose-full.yml build --no-cache expo-rpg
docker-compose -f docker-compose-full.yml up -d expo-rpg

# Aguardar Expo
echo "⏳ Aguardando Expo (20s)..."
sleep 20

# Iniciar ngrok em background
echo "🌐 Iniciando ngrok..."
nohup ngrok http 5000 --domain=nonlustrously-nonsustainable-ronny.ngrok-free.dev > logs/ngrok.log 2>&1 &

# Aguardar ngrok
sleep 5

echo ""
echo "🎉 Deploy completo concluído!"
echo ""
echo "📍 URLs de acesso:"
echo "  🎮 Interface Principal: http://localhost:5000"
echo "  📱 Expo Metro: http://localhost:8081"
echo "  🔧 Backend API: http://localhost:5000/api/health"
echo "  🌍 Público: https://nonlustrously-nonsustainable-ronny.ngrok-free.dev"
echo "  📊 ngrok Inspector: http://localhost:4040"
echo ""
echo "📋 Comandos úteis:"
echo "  Status: docker-compose -f docker-compose-full.yml ps"
echo "  Logs: docker-compose -f docker-compose-full.yml logs -f"
echo "  Parar: docker-compose -f docker-compose-full.yml down"
echo ""
echo "🎯 ACESSE: http://localhost:5000"
echo "   Lá você verá o QR Code para escanear com o Expo Go!"
echo ""

# Mostrar status final
echo "📊 Status dos containers:"
docker-compose -f docker-compose-full.yml ps
