#!/bin/bash

# Script completo: Instalar Docker + Deploy RPG System
set -e

# Cores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

log() {
    echo -e "${GREEN}[INSTALL] $1${NC}"
}

warn() {
    echo -e "${YELLOW}[INSTALL] ⚠️  $1${NC}"
}

error() {
    echo -e "${RED}[INSTALL] ❌ $1${NC}"
}

info() {
    echo -e "${BLUE}[INSTALL] ℹ️  $1${NC}"
}

echo -e "${BLUE}"
echo "╔══════════════════════════════════════════════════════════════╗"
echo "║              RPG SYSTEM - INSTALAÇÃO COMPLETA               ║"
echo "║                    Old Dragon RPG                           ║"
echo "╚══════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

# Verificar se é root ou tem sudo
if [[ $EUID -eq 0 ]]; then
    SUDO=""
else
    SUDO="sudo"
    if ! command -v sudo &> /dev/null; then
        error "Este script precisa de privilégios sudo. Execute como root ou instale sudo."
        exit 1
    fi
fi

# Função para instalar Docker
install_docker() {
    log "Instalando Docker..."
    
    # Atualizar repositórios
    $SUDO apt update
    
    # Instalar dependências
    $SUDO apt install -y \
        apt-transport-https \
        ca-certificates \
        curl \
        gnupg \
        lsb-release
    
    # Adicionar chave GPG do Docker
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | $SUDO gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
    
    # Adicionar repositório do Docker
    echo \
        "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu \
        $(lsb_release -cs) stable" | $SUDO tee /etc/apt/sources.list.d/docker.list > /dev/null
    
    # Atualizar e instalar Docker
    $SUDO apt update
    $SUDO apt install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
    
    # Adicionar usuário ao grupo docker
    $SUDO usermod -aG docker $USER
    
    # Iniciar Docker
    $SUDO systemctl start docker
    $SUDO systemctl enable docker
    
    log "Docker instalado com sucesso!"
}

# Função para instalar Docker Compose
install_docker_compose() {
    log "Instalando Docker Compose..."
    
    # Baixar Docker Compose
    $SUDO curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    
    # Dar permissão de execução
    $SUDO chmod +x /usr/local/bin/docker-compose
    
    # Criar link simbólico
    $SUDO ln -sf /usr/local/bin/docker-compose /usr/bin/docker-compose
    
    log "Docker Compose instalado com sucesso!"
}

# Função para instalar ngrok
install_ngrok() {
    log "Instalando ngrok..."
    
    # Adicionar repositório do ngrok
    curl -s https://ngrok-agent.s3.amazonaws.com/ngrok.asc | $SUDO tee /etc/apt/trusted.gpg.d/ngrok.asc >/dev/null
    echo "deb https://ngrok-agent.s3.amazonaws.com buster main" | $SUDO tee /etc/apt/sources.list.d/ngrok.list
    
    # Instalar ngrok
    $SUDO apt update && $SUDO apt install -y ngrok
    
    log "ngrok instalado com sucesso!"
}

# Verificar e instalar Docker
if ! command -v docker &> /dev/null; then
    install_docker
    
    warn "Docker foi instalado. Você precisa fazer logout e login novamente para usar Docker sem sudo."
    warn "Ou execute: newgrp docker"
    
    # Tentar usar newgrp para ativar o grupo docker
    info "Tentando ativar grupo docker..."
    newgrp docker << 'EOF'
echo "Grupo docker ativado!"
EOF
else
    log "Docker já está instalado"
fi

# Verificar e instalar Docker Compose
if ! command -v docker-compose &> /dev/null; then
    install_docker_compose
else
    log "Docker Compose já está instalado"
fi

# Verificar e instalar ngrok
if ! command -v ngrok &> /dev/null; then
    install_ngrok
else
    log "ngrok já está instalado"
fi

# Aguardar um pouco para Docker inicializar
log "Aguardando Docker inicializar..."
sleep 5

# Testar Docker
log "Testando Docker..."
if $SUDO docker run --rm hello-world > /dev/null 2>&1; then
    log "✅ Docker está funcionando!"
else
    warn "Docker pode precisar de reinicialização do sistema"
fi

# Criar diretórios necessários
log "Criando diretórios..."
mkdir -p logs uploads database

# Criar arquivo .env
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

# Configurar ngrok
log "Configurando ngrok..."
ngrok config add-authtoken 33U5DE9pqa3Dk4GwWH8VdslC5iL_2Vrr1B4qxR6oc9mkEikah 2>/dev/null || true

# Parar containers existentes
log "Limpando containers existentes..."
$SUDO docker stop $(docker ps -aq) 2>/dev/null || true
$SUDO docker rm $(docker ps -aq) 2>/dev/null || true

# Iniciar containers
log "Iniciando containers RPG..."
$SUDO docker-compose build --no-cache
$SUDO docker-compose up -d

# Aguardar MySQL
log "Aguardando MySQL inicializar..."
sleep 30

# Verificar status
log "Verificando status..."
if $SUDO docker-compose ps | grep -q "Up"; then
    log "✅ Sistema RPG iniciado com sucesso!"
else
    error "❌ Erro ao iniciar sistema"
    $SUDO docker-compose logs
    exit 1
fi

# Iniciar ngrok
log "Iniciando ngrok..."
nohup ngrok http 8081 --domain=nonlustrously-nonsustainable-ronny.ngrok-free.dev > logs/ngrok.log 2>&1 &

echo ""
echo -e "${GREEN}🎉 INSTALAÇÃO E DEPLOY CONCLUÍDOS COM SUCESSO! 🎉${NC}"
echo ""
echo "📍 URLs de acesso:"
echo "  🎮 Expo App Local: http://localhost:8081"
echo "  🔧 Backend Local: http://localhost:5000"
echo "  🏥 Health Check: http://localhost:5000/api/health"
echo "  🌍 Público: https://nonlustrously-nonsustainable-ronny.ngrok-free.dev"
echo "  📊 ngrok Inspector: http://localhost:4040"
echo ""
echo "📋 Comandos úteis:"
echo "  Status: sudo docker-compose ps"
echo "  Logs: sudo docker-compose logs -f"
echo "  Parar: sudo docker-compose down"
echo ""
echo -e "${BLUE}Sistema RPG pronto para uso! 🎮${NC}"

# Mostrar informações adicionais
echo ""
info "Se você teve problemas com permissões do Docker:"
info "1. Faça logout e login novamente"
info "2. Ou execute: newgrp docker"
info "3. Depois use os comandos sem sudo"
