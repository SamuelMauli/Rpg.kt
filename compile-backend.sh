#!/bin/bash

echo "========================================"
echo "  OLD DRAGON 2 RPG - Backend API"
echo "  Compilação Manual"
echo "========================================"
echo ""

# Criar diretórios necessários
mkdir -p build/classes/kotlin/main
mkdir -p build/libs

echo "Compilando código Kotlin..."

# Compilar com kotlinc diretamente
kotlinc -cp ".:lib/*" \
    -d build/classes/kotlin/main \
    src/main/kotlin/com/rpg/**/*.kt \
    src/main/kotlin/com/rpg/*.kt

if [ $? -eq 0 ]; then
    echo "✅ Compilação concluída!"
    echo ""
    echo "Para executar, use:"
    echo "  ./run-backend.sh"
else
    echo "❌ Erro na compilação!"
    echo ""
    echo "Instale o Kotlin Compiler:"
    echo "  sudo apt install kotlin"
    echo ""
    echo "Ou use o Gradle instalado no sistema:"
    echo "  sudo apt install gradle"
    echo "  gradle build"
fi

