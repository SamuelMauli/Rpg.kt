#!/bin/bash

echo "Old Dragon 2 RPG - Terminal Adventure"
echo "===================================="
echo ""

# Verificar se o Kotlin está instalado
if ! command -v kotlin &> /dev/null; then
    echo "Erro: Kotlin não está instalado."
    echo "Por favor, instale o Kotlin para executar o jogo."
    echo ""
    echo "Instruções de instalação:"
    echo "1. Instale o Java 11 ou superior"
    echo "2. Instale o SDKMAN! e depois o Kotlin (sdk install kotlin)"
    echo "3. Execute este script novamente"
    exit 1
fi

echo "Compilando o projeto..."

# Criar diretório de build se não existir
mkdir -p build/classes

# --- CORREÇÃO AQUI ---
# Encontra todos os arquivos .kt e passa todos de uma vez para o compilador
SOURCES=$(find src/main/kotlin -name "*.kt")
kotlinc $SOURCES -d build/classes

if [ $? -eq 0 ]; then
    echo "Compilação concluída com sucesso!"
    echo ""
    echo "Iniciando o jogo..."
    echo ""
    
    # Executar o jogo
    kotlin -cp build/classes com.rpg.MainKt
else
    echo "Erro na compilação. Verifique os arquivos de código."
    exit 1
fi