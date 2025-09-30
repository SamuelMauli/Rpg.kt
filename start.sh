#!/bin/bash

echo "🚀 Iniciando Old Dragon RPG..."
echo "📡 Iniciando Expo com tunnel..."

# Iniciar Expo em background
npx expo start --tunnel > /tmp/expo.log 2>&1 &
EXPO_PID=$!

echo "⏳ Aguardando tunnel conectar (30s)..."
sleep 30

echo "🖥️ Iniciando servidor web..."
echo "📱 QR Code será detectado automaticamente"
echo "🔗 Acesse: http://localhost:8081"

# Iniciar servidor web
node web-server.js
