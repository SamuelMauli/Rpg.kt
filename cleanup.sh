#!/bin/bash

# Script para limpar containers e resolver conflitos
set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log() {
    echo -e "${GREEN}[CLEANUP] $1${NC}"
}

warn() {
    echo -e "${YELLOW}[CLEANUP] ⚠️  $1${NC}"
}

error() {
    echo -e "${RED}[CLEANUP] ❌ $1${NC}"
}

log "Iniciando limpeza completa do sistema..."

# Parar todos os containers relacionados
log "Parando containers..."
docker stop old-dragon-rpg rpg_backend rpg_mysql 2>/dev/null || true

# Remover containers
log "Removendo containers..."
docker rm old-dragon-rpg rpg_backend rpg_mysql 2>/dev/null || true

# Parar via docker-compose
log "Parando via docker-compose..."
docker-compose down --remove-orphans --volumes 2>/dev/null || true

# Limpar containers órfãos
log "Limpando containers órfãos..."
docker container prune -f 2>/dev/null || true

# Limpar imagens não utilizadas
log "Limpando imagens não utilizadas..."
docker image prune -f 2>/dev/null || true

# Limpar volumes órfãos
log "Limpando volumes órfãos..."
docker volume prune -f 2>/dev/null || true

# Limpar redes órfãs
log "Limpando redes órfãs..."
docker network prune -f 2>/dev/null || true

# Parar processos ngrok se estiverem rodando
log "Parando ngrok..."
pkill -f ngrok 2>/dev/null || true

log "✅ Limpeza completa finalizada!"
log "Agora você pode executar: ./deploy.sh deploy"
