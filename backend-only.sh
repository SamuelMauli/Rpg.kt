#!/bin/bash

# Deploy apenas do backend (mais confiável)
set -e

echo "🔧 RPG Backend - Deploy Simplificado"
echo "===================================="

# Parar containers existentes
echo "⏹️  Parando containers..."
docker stop rpg_mysql rpg_backend 2>/dev/null || true
docker rm rpg_mysql rpg_backend 2>/dev/null || true

# Limpar imagens
echo "🧹 Limpando imagens..."
docker rmi rpgkt-backend 2>/dev/null || true

# Criar diretórios
mkdir -p logs uploads database

# Usar docker-compose simplificado
echo "🚀 Iniciando MySQL + Backend..."
docker-compose -f docker-compose-simple.yml up -d --build

# Aguardar
echo "⏳ Aguardando serviços (30s)..."
sleep 30

# Testar
echo "🧪 Testando backend..."
if curl -s http://localhost:5000/api/health; then
    echo ""
    echo "✅ Backend funcionando perfeitamente!"
else
    echo "❌ Problemas no backend"
    docker-compose -f docker-compose-simple.yml logs backend
fi

echo ""
echo "📍 URLs:"
echo "  🔧 Backend: http://localhost:5000"
echo "  🏥 Health: http://localhost:5000/api/health"
echo "  🗄️ MySQL: localhost:3306"
echo ""
echo "📊 Status: docker-compose -f docker-compose-simple.yml ps"
