#!/bin/bash

echo "🖥️ INICIANDO OLD DRAGON RPG COM INTERFACE WEB"
echo "=============================================="

# Parar containers existentes
echo "🛑 Parando containers existentes..."
docker-compose down 2>/dev/null || docker compose down 2>/dev/null || true

# Remover imagens antigas
echo "🧹 Limpando imagens antigas..."
docker rmi old-dragon-rpg_expo-rpg 2>/dev/null || true

# Construir e executar
echo "🚀 Construindo container com interface web..."
if command -v docker-compose &> /dev/null; then
    docker-compose up --build -d
else
    docker compose up --build -d
fi

# Aguardar container iniciar
echo "⏳ Aguardando container iniciar..."
sleep 10

# Executar comando para mostrar QR code na web
echo "🖥️ Configurando interface web..."
docker exec old-dragon-rpg sh -c "
    echo '🖥️ Iniciando Expo com interface web...'
    npx expo start --tunnel --web --port 8081 &
    sleep 5
    echo '✅ Interface web disponível em http://localhost:8081'
    echo '📱 QR code deve aparecer na página'
    tail -f /dev/null
"
