# Makefile para RPG System
.PHONY: help deploy start stop restart status logs clean dev mysql

# Cores
GREEN := \033[0;32m
YELLOW := \033[1;33m
BLUE := \033[0;34m
NC := \033[0m

help: ## Mostrar ajuda
	@echo -e "$(BLUE)RPG System - Old Dragon RPG$(NC)"
	@echo ""
	@echo -e "$(GREEN)Comandos disponíveis:$(NC)"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  $(YELLOW)%-15s$(NC) %s\n", $$1, $$2}' $(MAKEFILE_LIST)

deploy: ## Deploy completo da aplicação
	@echo -e "$(GREEN)🚀 Iniciando deploy completo...$(NC)"
	./deploy.sh deploy

start: ## Iniciar serviços
	@echo -e "$(GREEN)▶️  Iniciando serviços...$(NC)"
	./deploy.sh start

stop: ## Parar serviços
	@echo -e "$(YELLOW)⏹️  Parando serviços...$(NC)"
	./deploy.sh stop

restart: ## Reiniciar serviços
	@echo -e "$(YELLOW)🔄 Reiniciando serviços...$(NC)"
	./deploy.sh restart

status: ## Mostrar status dos serviços
	@echo -e "$(BLUE)📊 Status dos serviços:$(NC)"
	./deploy.sh status

logs: ## Mostrar logs dos containers
	@echo -e "$(BLUE)📋 Logs dos containers:$(NC)"
	./deploy.sh logs

ngrok: ## Configurar e iniciar ngrok
	@echo -e "$(GREEN)🌐 Configurando ngrok...$(NC)"
	./deploy.sh ngrok

clean: ## Limpar containers e volumes
	@echo -e "$(YELLOW)🧹 Limpando containers e volumes...$(NC)"
	docker-compose down --volumes --remove-orphans
	docker system prune -f

build: ## Construir imagens Docker
	@echo -e "$(GREEN)🔨 Construindo imagens Docker...$(NC)"
	docker-compose build --no-cache

mysql: ## Conectar ao MySQL
	@echo -e "$(BLUE)🗄️  Conectando ao MySQL...$(NC)"
	docker-compose exec mysql mysql -u rpg_user -prpg_pass_2024 rpg_database

expo: ## Executar Expo em modo desenvolvimento
	@echo -e "$(GREEN)📱 Iniciando Expo...$(NC)"
	npm start

install: ## Instalar dependências do sistema
	@echo -e "$(GREEN)📦 Instalando dependências...$(NC)"
	sudo apt update
	sudo apt install -y docker.io docker-compose nodejs npm
	sudo usermod -aG docker $$USER

health: ## Verificar saúde da aplicação
	@echo -e "$(BLUE)🏥 Verificando saúde da aplicação...$(NC)"
	@echo "=== Expo App ==="
	@curl -s http://localhost:8081 || echo "Expo não está respondendo"
	@echo ""
	@echo "=== Backend ==="
	@curl -s http://localhost:5000 || echo "Backend não está respondendo"

monitor: ## Monitorar recursos do sistema
	@echo -e "$(BLUE)📈 Monitorando recursos...$(NC)"
	@echo "=== Uso de CPU e Memória ==="
	@docker stats --no-stream
	@echo ""
	@echo "=== Espaço em Disco ==="
	@df -h
	@echo ""
	@echo "=== Containers Ativos ==="
	@docker ps

# Comando padrão
.DEFAULT_GOAL := help
