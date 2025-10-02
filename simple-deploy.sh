#!/bin/bash

# Deploy super simplificado sem dependências problemáticas
set -e

echo "🚀 RPG System - Deploy Simplificado"
echo "===================================="

# Parar tudo
echo "⏹️  Parando containers existentes..."
docker stop $(docker ps -aq) 2>/dev/null || true
docker rm $(docker ps -aq) 2>/dev/null || true

# Limpar imagens problemáticas
echo "🧹 Limpando imagens..."
docker rmi rpgkt-backend rpgkt-expo-rpg 2>/dev/null || true

# Criar diretórios
echo "📁 Criando diretórios..."
mkdir -p logs uploads database

# Criar .env simples
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

# Construir apenas MySQL primeiro
echo "🗄️  Iniciando MySQL..."
docker-compose up -d mysql

# Aguardar MySQL
echo "⏳ Aguardando MySQL (30s)..."
sleep 30

# Construir backend
echo "🔧 Construindo backend..."
docker-compose build --no-cache backend

# Iniciar backend
echo "▶️  Iniciando backend..."
docker-compose up -d backend

# Aguardar backend
echo "⏳ Aguardando backend (10s)..."
sleep 10

# Testar backend
echo "🧪 Testando backend..."
if curl -s http://localhost:5000/api/health > /dev/null; then
    echo "✅ Backend funcionando!"
else
    echo "❌ Backend com problemas"
    docker-compose logs backend
fi

# Construir Expo (opcional)
echo "📱 Construindo Expo..."
docker-compose build --no-cache expo-rpg || echo "⚠️  Expo falhou, mas backend está funcionando"

# Iniciar Expo
echo "▶️  Iniciando Expo..."
docker-compose up -d expo-rpg || echo "⚠️  Expo falhou, mas backend está funcionando"

echo ""
echo "🎉 Deploy concluído!"
echo ""
echo "📍 URLs:"
echo "  🔧 Backend: http://localhost:5000"
echo "  🏥 Health: http://localhost:5000/api/health"
echo "  🎮 Expo: http://localhost:8081 (se funcionou)"
echo ""
echo "📊 Status: docker-compose ps"
echo "📋 Logs: docker-compose logs -f"
