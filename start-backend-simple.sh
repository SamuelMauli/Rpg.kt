#!/bin/bash

echo "========================================"
echo "  OLD DRAGON 2 RPG - Backend API"
echo "  Modo Simplificado"
echo "========================================"
echo ""

# Verificar se gradle está instalado
if ! command -v gradle &> /dev/null; then
    echo "❌ Gradle não encontrado!"
    echo ""
    echo "Instale o Gradle:"
    echo "  sudo apt install gradle"
    echo ""
    echo "Ou no Arch Linux:"
    echo "  sudo pacman -S gradle"
    echo ""
    exit 1
fi

echo "Usando Gradle do sistema: $(gradle --version | head -n 1)"
echo ""
echo "Compilando projeto..."

# Compilar com gradle do sistema
gradle build --no-daemon

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Compilação concluída com sucesso!"
    echo ""
    echo "Iniciando servidor API..."
    echo "API disponível em: http://localhost:8080"
    echo ""
    
    # Executar o servidor
    java -cp "build/libs/*:build/classes/kotlin/main" com.rpg.api.ApiServerKt
else
    echo ""
    echo "❌ Erro na compilação!"
    exit 1
fi

