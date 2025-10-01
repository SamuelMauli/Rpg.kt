#!/bin/bash

# Script de deploy simplificado e robusto
set -e

# Cores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

log() {
    echo -e "${GREEN}[DEPLOY] $1${NC}"
}

warn() {
    echo -e "${YELLOW}[DEPLOY] ⚠️  $1${NC}"
}

error() {
    echo -e "${RED}[DEPLOY] ❌ $1${NC}"
}

echo "🚀 RPG System - Deploy Simplificado"
echo "=================================="

# 1. Parar containers existentes
log "Parando containers existentes..."
docker stop old-dragon-rpg rpg_backend rpg_mysql 2>/dev/null || true
docker rm old-dragon-rpg rpg_backend rpg_mysql 2>/dev/null || true
docker-compose down --remove-orphans 2>/dev/null || true

# 2. Criar diretórios (sem chmod)
log "Criando diretórios..."
mkdir -p logs uploads database

# 3. Criar .env se não existir
if [ ! -f .env ]; then
    log "Criando arquivo .env..."
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
NGROK_AUTHTOKEN=33U5DE9pqa3Dk4GwWH8VdslC5iL_2Vrr1B4qxR6oc9mkEikah
NGROK_DOMAIN=nonlustrously-nonsustainable-ronny.ngrok-free.dev
JWT_SECRET=rpg-system-jwt-secret-2024
BCRYPT_ROUNDS=12
EOF
fi

# 4. Construir e iniciar containers
log "Construindo e iniciando containers..."
docker-compose up -d --build

# 5. Aguardar MySQL
log "Aguardando MySQL inicializar..."
sleep 20

# 6. Verificar status
log "Verificando status dos serviços..."

if docker-compose ps | grep -q "Up"; then
    log "✅ Containers iniciados com sucesso!"
else
    error "❌ Erro ao iniciar containers"
    docker-compose logs
    exit 1
fi

# 7. Mostrar URLs
echo ""
log "🎉 Deploy concluído com sucesso!"
echo ""
echo "📍 URLs de acesso:"
echo "  🎮 Expo App: http://localhost:8081"
echo "  🔧 Backend: http://localhost:5000"
echo "  🏥 Health: http://localhost:5000/api/health"
echo ""
echo "📊 Para verificar status: docker-compose ps"
echo "📋 Para ver logs: docker-compose logs -f"
echo ""

# 8. Configurar ngrok se disponível
if command -v ngrok &> /dev/null; then
    log "Configurando ngrok..."
    ngrok config add-authtoken 33U5DE9pqa3Dk4GwWH8VdslC5iL_2Vrr1B4qxR6oc9mkEikah 2>/dev/null || true
    
    # Iniciar ngrok em background
    nohup ngrok http 8081 --domain=nonlustrously-nonsustainable-ronny.ngrok-free.dev > logs/ngrok.log 2>&1 &
    
    echo "  🌍 Público: https://nonlustrously-nonsustainable-ronny.ngrok-free.dev"
    echo "  📊 ngrok Inspector: http://localhost:4040"
else
    warn "ngrok não está instalado. Instale com: sudo apt install ngrok"
fi

echo ""
log "Sistema RPG pronto para uso! 🎮"
