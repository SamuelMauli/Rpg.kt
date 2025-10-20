#!/bin/bash

echo "========================================"
echo "  OLD DRAGON 2 RPG - Backend API"
echo "  Modo Automático"
echo "========================================"
echo ""

# Detectar Gradle
GRADLE_CMD=""

# Tentar gradle do sistema
if command -v gradle &> /dev/null; then
    GRADLE_CMD="gradle"
    echo "✓ Usando Gradle do sistema: $(gradle --version | head -n 1)"
# Tentar gradle baixado localmente
elif [ -f "/home/ubuntu/gradle-8.5/bin/gradle" ]; then
    GRADLE_CMD="/home/ubuntu/gradle-8.5/bin/gradle"
    echo "✓ Usando Gradle local: /home/ubuntu/gradle-8.5/bin/gradle"
# Tentar gradlew
elif [ -f "./gradlew" ]; then
    GRADLE_CMD="./gradlew"
    echo "✓ Usando Gradle wrapper: ./gradlew"
else
    echo "❌ Gradle não encontrado!"
    echo ""
    echo "Instale o Gradle:"
    echo "  sudo apt install gradle"
    echo ""
    echo "Ou baixe manualmente:"
    echo "  cd ~"
    echo "  wget https://services.gradle.org/distributions/gradle-8.5-bin.zip"
    echo "  unzip gradle-8.5-bin.zip"
    echo ""
    exit 1
fi

echo ""
echo "Compilando projeto..."

# Compilar
$GRADLE_CMD clean compileKotlin --no-daemon

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Compilação concluída com sucesso!"
    echo ""
    
    # Criar JAR com dependências
    echo "Criando JAR executável..."
    $GRADLE_CMD jar --no-daemon
    
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
    echo ""
    echo "Verifique os erros acima."
    exit 1
fi

