#!/bin/bash

# Script de inicialização do Old Dragon 2 RPG via Docker

echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║           OLD DRAGON 2 RPG - DOCKER SETUP                     ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""

# Verificar se Docker está instalado
if ! command -v docker &> /dev/null; then
    echo "❌ Docker não está instalado!"
    echo "Por favor, instale o Docker primeiro:"
    echo "   https://docs.docker.com/get-docker/"
    exit 1
fi

# Verificar se Docker Compose está instalado
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose não está instalado!"
    echo "Por favor, instale o Docker Compose primeiro:"
    echo "   https://docs.docker.com/compose/install/"
    exit 1
fi

echo "✅ Docker e Docker Compose encontrados!"
echo ""

# Criar diretórios necessários
echo "📁 Criando diretórios necessários..."
mkdir -p database logs

# Parar containers existentes
echo "🛑 Parando containers existentes..."
docker-compose down

# Construir a imagem
echo "🔨 Construindo imagem Docker..."
docker-compose build

# Iniciar o container
echo "🚀 Iniciando Old Dragon 2 RPG..."
echo ""
echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║  O jogo será iniciado em modo interativo.                     ║"
echo "║  Use Ctrl+C para sair.                                        ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""

# Executar em modo interativo
docker-compose up

# Cleanup ao sair
echo ""
echo "🧹 Limpando..."
docker-compose down

