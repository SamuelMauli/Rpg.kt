#!/bin/bash

# Script de instalação e execução do Old Dragon 2 RPG

echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║        OLD DRAGON 2 RPG - INSTALAÇÃO E EXECUÇÃO              ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""

# Verificar Java
echo "🔍 Verificando Java..."
if ! command -v java &> /dev/null; then
    echo "❌ Java não encontrado!"
    echo ""
    echo "Por favor, instale Java 11 ou superior:"
    echo ""
    echo "Ubuntu/Debian:"
    echo "  sudo apt update"
    echo "  sudo apt install openjdk-17-jdk"
    echo ""
    echo "Fedora/RHEL:"
    echo "  sudo dnf install java-17-openjdk"
    echo ""
    echo "macOS:"
    echo "  brew install openjdk@17"
    echo ""
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
echo "✅ Java $JAVA_VERSION encontrado!"
echo ""

# Criar diretórios necessários
echo "📁 Criando diretórios necessários..."
mkdir -p database logs

# Dar permissão ao gradlew
echo "🔧 Configurando permissões..."
chmod +x gradlew

# Compilar o projeto
echo "🔨 Compilando o projeto..."
./gradlew clean build -x test

if [ $? -ne 0 ]; then
    echo ""
    echo "❌ Erro ao compilar o projeto!"
    echo "Verifique os erros acima e tente novamente."
    exit 1
fi

echo ""
echo "✅ Compilação concluída com sucesso!"
echo ""

# Executar o jogo
echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║              INICIANDO OLD DRAGON 2 RPG...                    ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""

./gradlew run --console=plain

