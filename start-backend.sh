#!/bin/bash

echo "========================================"
echo "  OLD DRAGON 2 RPG - Backend API"
echo "========================================"
echo ""
echo "Iniciando servidor backend na porta 8080..."
echo ""

# Compilar o projeto
echo "Compilando projeto..."
./gradlew build --no-daemon

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Compilação concluída com sucesso!"
    echo ""
    echo "Iniciando servidor API..."
    echo "API disponível em: http://localhost:8080"
    echo ""
    echo "Endpoints disponíveis:"
    echo "  GET  /api/personagens"
    echo "  POST /api/personagens"
    echo "  GET  /api/personagens/{nome}"
    echo "  GET  /api/monstros"
    echo "  GET  /api/itens"
    echo "  POST /api/combate/iniciar"
    echo "  POST /api/personagens/distribuir-ponto"
    echo ""
    
    # Executar o servidor
    java -cp "build/libs/*:build/classes/kotlin/main" com.rpg.api.ApiServerKt
else
    echo ""
    echo "❌ Erro na compilação!"
    exit 1
fi

