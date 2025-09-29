#!/bin/bash

echo "🐳 INICIANDO OLD DRAGON RPG COM DOCKER"
echo "======================================"

# Verificar se Docker está instalado
if ! command -v docker &> /dev/null; then
    echo "❌ Docker não está instalado!"
    echo "🔧 Instalando Docker..."
    
    # Instalar Docker
    curl -fsSL https://get.docker.com -o get-docker.sh
    sudo sh get-docker.sh
    sudo usermod -aG docker $USER
    
    echo "✅ Docker instalado! Reinicie o terminal e execute novamente."
    exit 1
fi

# Verificar se Docker Compose está disponível
if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    echo "🔧 Instalando Docker Compose..."
    sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
fi

# Parar containers existentes
echo "🛑 Parando containers existentes..."
docker-compose down 2>/dev/null || docker compose down 2>/dev/null || true

# Remover imagens antigas
echo "🧹 Limpando imagens antigas..."
docker rmi old-dragon-rpg_expo-rpg 2>/dev/null || true

# Construir e executar
echo "🚀 Construindo e iniciando container..."
if command -v docker-compose &> /dev/null; then
    docker-compose up --build
else
    docker compose up --build
fi
