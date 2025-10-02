#!/bin/bash

# Deploy que realmente funciona
set -e

echo "🐉 Old Dragon RPG - Deploy Funcional"
echo "===================================="

# Parar tudo primeiro
echo "⏹️  Parando tudo..."
pkill ngrok 2>/dev/null || true
docker stop rpg_mysql rpg_backend old-dragon-rpg 2>/dev/null || true
docker rm rpg_mysql rpg_backend old-dragon-rpg 2>/dev/null || true

# Limpar imagens problemáticas
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

# Iniciar apenas MySQL + Backend primeiro
echo "🗄️  Iniciando MySQL..."
docker-compose -f docker-compose-simple.yml up -d mysql

echo "⏳ Aguardando MySQL (30s)..."
sleep 30

echo "🔧 Construindo backend híbrido..."
docker-compose -f docker-compose-simple.yml build --no-cache backend
docker-compose -f docker-compose-simple.yml up -d backend

echo "⏳ Aguardando backend (15s)..."
sleep 15

# Testar backend
echo "🧪 Testando backend..."
if curl -s http://localhost:5000/api/health > /dev/null; then
    echo "✅ Backend funcionando!"
else
    echo "❌ Backend com problemas"
    docker-compose -f docker-compose-simple.yml logs backend
    exit 1
fi

# Tentar construir Expo (opcional)
echo "📱 Tentando construir Expo..."
if docker-compose -f docker-compose-full.yml build expo-rpg 2>/dev/null; then
    echo "✅ Expo construído com sucesso"
    
    echo "▶️  Iniciando Expo..."
    if docker-compose -f docker-compose-full.yml up -d expo-rpg; then
        echo "✅ Expo iniciado"
    else
        echo "⚠️  Expo falhou ao iniciar, mas backend está funcionando"
    fi
else
    echo "⚠️  Expo falhou na construção, mas backend está funcionando"
fi

# Iniciar ngrok
echo "🌐 Iniciando ngrok..."
./start-ngrok.sh &

echo ""
echo "🎉 Deploy concluído!"
echo ""
echo "📍 URLs principais:"
echo "  🎮 Interface: http://localhost:5000"
echo "  🔧 Backend: http://localhost:5000/api/health"
echo "  📱 Expo: http://localhost:8081 (se funcionou)"
echo ""
echo "📊 Status:"
docker-compose -f docker-compose-simple.yml ps

echo ""
echo "🎯 ACESSE: http://localhost:5000"
echo "   Interface completa com controles!"

# Aguardar um pouco e mostrar status do ngrok
sleep 5
echo ""
echo "🌐 Status do ngrok:"
if pgrep ngrok > /dev/null; then
    echo "✅ ngrok está rodando"
    echo "📊 Inspector: http://localhost:4040"
else
    echo "❌ ngrok não está rodando"
    echo "💡 Execute manualmente: ./start-ngrok.sh"
fi
