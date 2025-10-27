#!/bin/bash

# Script para configurar permissões de todos os arquivos executáveis

echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║        CONFIGURANDO PERMISSÕES - OLD DRAGON 2 RPG             ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""

echo "🔧 Corrigindo permissões dos scripts..."

# Scripts principais
chmod +x install.sh 2>/dev/null && echo "✅ install.sh"
chmod +x docker-start.sh 2>/dev/null && echo "✅ docker-start.sh"
chmod +x gradlew 2>/dev/null && echo "✅ gradlew"

# Scripts de backend
chmod +x start-backend.sh 2>/dev/null && echo "✅ start-backend.sh"
chmod +x start-backend-fixed.sh 2>/dev/null && echo "✅ start-backend-fixed.sh"
chmod +x start-backend-simple.sh 2>/dev/null && echo "✅ start-backend-simple.sh"
chmod +x compile-backend.sh 2>/dev/null && echo "✅ compile-backend.sh"
chmod +x test-api.sh 2>/dev/null && echo "✅ test-api.sh"
chmod +x run-docker.sh 2>/dev/null && echo "✅ run-docker.sh"
chmod +x stop-ngrok.sh 2>/dev/null && echo "✅ stop-ngrok.sh"

# Scripts mobile
if [ -d "mobile" ]; then
    echo ""
    echo "🔧 Corrigindo permissões dos scripts mobile..."
    find mobile -name "*.sh" -exec chmod +x {} \; 2>/dev/null
    echo "✅ Scripts mobile corrigidos"
fi

# Criar diretórios necessários
echo ""
echo "📁 Criando diretórios necessários..."
mkdir -p database logs uploads 2>/dev/null
echo "✅ Diretórios criados"

echo ""
echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║                    ✅ CONFIGURAÇÃO COMPLETA!                   ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""
echo "Agora você pode executar:"
echo ""
echo "  🎮 JOGAR O RPG:"
echo "     ./install.sh         - Instalação e execução local"
echo "     ./docker-start.sh    - Executar com Docker"
echo "     ./gradlew run        - Executar diretamente"
echo ""
echo "  🔧 DESENVOLVIMENTO:"
echo "     ./gradlew build      - Compilar o projeto"
echo "     ./gradlew clean      - Limpar build"
echo ""

