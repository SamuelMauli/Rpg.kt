#!/bin/bash

echo "📱 Iniciando apenas Expo..."

# Verificar se backend está rodando
if ! curl -s http://localhost:5000/api/health > /dev/null; then
    echo "❌ Backend não está rodando!"
    echo "Execute primeiro: sudo ./deploy-working.sh"
    exit 1
fi

# Parar Expo existente
echo "⏹️  Parando Expo existente..."
docker stop old-dragon-rpg 2>/dev/null || true
docker rm old-dragon-rpg 2>/dev/null || true

# Limpar imagem do Expo
docker rmi rpgkt-expo-rpg 2>/dev/null || true

# Construir Expo
echo "🔧 Construindo Expo..."
if docker-compose -f docker-compose-full.yml build expo-rpg; then
    echo "✅ Expo construído"
    
    # Iniciar Expo
    echo "▶️  Iniciando Expo..."
    if docker-compose -f docker-compose-full.yml up -d expo-rpg; then
        echo "✅ Expo iniciado com sucesso!"
        
        # Aguardar um pouco
        sleep 10
        
        # Verificar status
        echo "📊 Status do Expo:"
        docker-compose -f docker-compose-full.yml ps expo-rpg
        
        echo ""
        echo "📍 URLs do Expo:"
        echo "  📱 Metro: http://localhost:8081"
        echo "  🎮 Interface: http://localhost:5000 (para ver QR Code)"
        
    else
        echo "❌ Falha ao iniciar Expo"
        docker-compose -f docker-compose-full.yml logs expo-rpg
    fi
else
    echo "❌ Falha ao construir Expo"
    echo ""
    echo "💡 Possíveis soluções:"
    echo "1. Verificar conexão com internet"
    echo "2. Tentar novamente em alguns minutos"
    echo "3. Usar apenas o backend (já está funcionando)"
fi

echo ""
echo "🎯 Acesse http://localhost:5000 para ver a interface completa!"
