#!/bin/bash

# Script para corrigir problemas e reiniciar sistema
set -e

# Cores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

log() {
    echo -e "${GREEN}[FIX] $1${NC}"
}

warn() {
    echo -e "${YELLOW}[FIX] ⚠️  $1${NC}"
}

error() {
    echo -e "${RED}[FIX] ❌ $1${NC}"
}

echo "🔧 Corrigindo problemas do sistema RPG..."

# 1. Parar containers com problemas
log "Parando containers com problemas..."
docker stop old-dragon-rpg rpg_backend rpg_mysql 2>/dev/null || true
docker rm old-dragon-rpg rpg_backend rpg_mysql 2>/dev/null || true

# 2. Limpar imagens com problemas
log "Limpando imagens com problemas..."
docker rmi rpgkt-backend rpgkt-expo-rpg 2>/dev/null || true

# 3. Reconstruir imagens
log "Reconstruindo imagens com correções..."
docker-compose build --no-cache

# 4. Iniciar containers
log "Iniciando containers corrigidos..."
docker-compose up -d

# 5. Aguardar MySQL
log "Aguardando MySQL inicializar..."
sleep 20

# 6. Verificar status
log "Verificando status dos containers..."
docker-compose ps

# 7. Mostrar logs se houver problemas
if ! docker-compose ps | grep -q "Up"; then
    warn "Alguns containers podem ter problemas. Mostrando logs..."
    docker-compose logs --tail=20
fi

log "✅ Correção concluída!"
echo ""
echo "📍 Para verificar:"
echo "  Status: docker-compose ps"
echo "  Logs: docker-compose logs -f"
echo "  Backend: curl http://localhost:5000/api/health"
