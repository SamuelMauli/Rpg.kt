#!/bin/bash

# Script principal de deploy do RPG System
set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para log colorido
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}"
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] ⚠️  $1${NC}"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ❌ $1${NC}"
}

info() {
    echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')] ℹ️  $1${NC}"
}

# Banner
echo -e "${BLUE}"
echo "╔══════════════════════════════════════════════════════════════╗"
echo "║                    RPG SYSTEM DEPLOY SCRIPT                 ║"
echo "║                     Old Dragon RPG                          ║"
echo "╚══════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

# Verificar se Docker está instalado
if ! command -v docker &> /dev/null; then
    error "Docker não está instalado. Instalando..."
    curl -fsSL https://get.docker.com -o get-docker.sh
    sudo sh get-docker.sh
    sudo usermod -aG docker $USER
    log "Docker instalado com sucesso!"
fi

# Verificar se Docker Compose está instalado
if ! command -v docker-compose &> /dev/null; then
    error "Docker Compose não está instalado. Instalando..."
    sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    log "Docker Compose instalado com sucesso!"
fi

# Função para parar serviços existentes
stop_services() {
    log "Parando serviços existentes..."
    
    # Parar containers específicos se estiverem rodando
    docker stop old-dragon-rpg rpg_backend rpg_mysql 2>/dev/null || true
    docker rm old-dragon-rpg rpg_backend rpg_mysql 2>/dev/null || true
    
    # Parar via docker-compose
    docker-compose down --remove-orphans 2>/dev/null || true
    
    # Limpar containers órfãos
    docker container prune -f 2>/dev/null || true
    
    log "Containers existentes removidos"
}

# Função para criar diretórios necessários
create_directories() {
    log "Criando diretórios necessários..."
    mkdir -p logs uploads database 2>/dev/null || true
    
    # Tentar alterar permissões, mas não falhar se não conseguir
    chmod 755 logs uploads database 2>/dev/null || {
        warn "Não foi possível alterar permissões dos diretórios (normal em alguns sistemas)"
    }
    
    log "Diretórios criados"
}

# Função para criar arquivo .env se não existir
create_env_file() {
    if [ ! -f .env ]; then
        log "Criando arquivo .env..."
        cat > .env << EOF
# Configurações do Sistema RPG
NODE_ENV=production
PORT=5000

# Configurações do Banco de Dados
DATABASE_URL=mysql://rpg_user:rpg_pass_2024@mysql:3306/rpg_database
MYSQL_ROOT_PASSWORD=rpg_root_2024
MYSQL_DATABASE=rpg_database
MYSQL_USER=rpg_user
MYSQL_PASSWORD=rpg_pass_2024

# Configurações do Expo/React Native
EXPO_DEVTOOLS_LISTEN_ADDRESS=0.0.0.0
REACT_NATIVE_PACKAGER_HOSTNAME=0.0.0.0

# Configurações do ngrok
NGROK_AUTHTOKEN=33U5DE9pqa3Dk4GwWH8VdslC5iL_2Vrr1B4qxR6oc9mkEikah
NGROK_DOMAIN=nonlustrously-nonsustainable-ronny.ngrok-free.dev

# Configurações de Segurança
JWT_SECRET=rpg-system-jwt-secret-2024
BCRYPT_ROUNDS=12
EOF
        log "Arquivo .env criado com configurações padrão"
    else
        info "Arquivo .env já existe"
    fi
}

# Função para construir e iniciar containers
start_containers() {
    log "Construindo e iniciando containers..."
    
    # Build da aplicação
    docker-compose build --no-cache
    
    # Iniciar serviços
    docker-compose up -d
    
    # Aguardar MySQL ficar pronto
    log "Aguardando MySQL ficar pronto..."
    sleep 15
    
    # Verificar se os containers estão rodando
    if docker-compose ps | grep -q "Up"; then
        log "Containers iniciados com sucesso!"
    else
        error "Erro ao iniciar containers"
        docker-compose logs
        exit 1
    fi
}

# Função para configurar ngrok
setup_ngrok() {
    log "Configurando ngrok..."
    
    # Verificar se ngrok está instalado
    if ! command -v ngrok &> /dev/null; then
        log "Instalando ngrok..."
        curl -s https://ngrok-agent.s3.amazonaws.com/ngrok.asc | sudo tee /etc/apt/trusted.gpg.d/ngrok.asc >/dev/null
        echo "deb https://ngrok-agent.s3.amazonaws.com buster main" | sudo tee /etc/apt/sources.list.d/ngrok.list
        sudo apt update && sudo apt install ngrok
    fi
    
    # Configurar authtoken
    ngrok config add-authtoken 33U5DE9pqa3Dk4GwWH8VdslC5iL_2Vrr1B4qxR6oc9mkEikah
    
    # Copiar arquivo de configuração
    if [ -f "ngrok.yml" ]; then
        cp ngrok.yml ~/.ngrok2/ngrok.yml
        log "Configuração do ngrok aplicada"
    fi
}

# Função para iniciar ngrok
start_ngrok() {
    log "Iniciando ngrok..."
    
    if command -v ngrok &> /dev/null; then
        # Iniciar ngrok em background
        nohup ngrok start rpg-app > logs/ngrok.log 2>&1 &
        sleep 3
        
        log "ngrok iniciado! Verifique os logs em logs/ngrok.log"
        log "Domínio: https://nonlustrously-nonsustainable-ronny.ngrok-free.dev"
    else
        warn "ngrok não está instalado"
    fi
}

# Função para mostrar status
show_status() {
    echo ""
    log "Status dos serviços:"
    echo ""
    
    # Status do Docker
    if docker-compose ps | grep -q "Up"; then
        echo -e "  ${GREEN}✅ Docker Containers: Rodando${NC}"
    else
        echo -e "  ${RED}❌ Docker Containers: Parados${NC}"
    fi
    
    # Status do MySQL
    if docker-compose exec mysql mysqladmin ping -h localhost --silent 2>/dev/null; then
        echo -e "  ${GREEN}✅ MySQL: Conectado${NC}"
    else
        echo -e "  ${RED}❌ MySQL: Desconectado${NC}"
    fi
    
    # Status da aplicação Expo
    if curl -s http://localhost:8081 > /dev/null 2>&1; then
        echo -e "  ${GREEN}✅ Expo App: Online${NC}"
    else
        echo -e "  ${RED}❌ Expo App: Offline${NC}"
    fi
    
    # Status do backend
    if curl -s http://localhost:5000 > /dev/null 2>&1; then
        echo -e "  ${GREEN}✅ Backend: Online${NC}"
    else
        echo -e "  ${RED}❌ Backend: Offline${NC}"
    fi
    
    # Status do ngrok
    if pgrep -f "ngrok" > /dev/null; then
        echo -e "  ${GREEN}✅ ngrok: Rodando${NC}"
    else
        echo -e "  ${YELLOW}⚠️  ngrok: Parado${NC}"
    fi
    
    echo ""
    log "URLs de acesso:"
    echo "  🎮 Expo App Local: http://localhost:8081"
    echo "  🔧 Backend Local: http://localhost:5000"
    echo "  🌍 Público: https://nonlustrously-nonsustainable-ronny.ngrok-free.dev"
    echo "  📊 ngrok Inspector: http://localhost:4040"
    echo ""
}

# Função para mostrar logs
show_logs() {
    log "Mostrando logs dos containers..."
    docker-compose logs -f
}

# Função principal
main() {
    case "${1:-deploy}" in
        "deploy")
            log "Iniciando deploy completo..."
            stop_services
            create_directories
            create_env_file
            start_containers
            setup_ngrok
            start_ngrok
            show_status
            ;;
        "start")
            log "Iniciando serviços..."
            start_containers
            start_ngrok
            show_status
            ;;
        "stop")
            log "Parando serviços..."
            stop_services
            pkill -f ngrok 2>/dev/null || true
            log "Serviços parados"
            ;;
        "restart")
            log "Reiniciando serviços..."
            stop_services
            start_containers
            start_ngrok
            show_status
            ;;
        "status")
            show_status
            ;;
        "logs")
            show_logs
            ;;
        "ngrok")
            setup_ngrok
            start_ngrok
            ;;
        *)
            echo "Uso: $0 {deploy|start|stop|restart|status|logs|ngrok}"
            echo ""
            echo "Comandos:"
            echo "  deploy   - Deploy completo (padrão)"
            echo "  start    - Iniciar serviços"
            echo "  stop     - Parar serviços"
            echo "  restart  - Reiniciar serviços"
            echo "  status   - Mostrar status"
            echo "  logs     - Mostrar logs"
            echo "  ngrok    - Configurar e iniciar ngrok"
            exit 1
            ;;
    esac
}

# Executar função principal
main "$@"
