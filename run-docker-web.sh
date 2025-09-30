#!/bin/bash

echo "🖥️ INICIANDO OLD DRAGON RPG COM QR CODE DINÂMICO"
echo "================================================"

# Verificar se Docker está instalado
if ! command -v docker &> /dev/null; then
    echo "❌ Docker não está instalado!"
    echo "🔧 Instalando Docker..."
    curl -fsSL https://get.docker.com -o get-docker.sh
    sudo sh get-docker.sh
    sudo usermod -aG docker $USER
    echo "✅ Docker instalado! Reinicie o terminal e execute novamente."
    exit 1
fi

# Parar containers existentes
echo "🛑 Parando containers existentes..."
docker stop old-dragon-rpg 2>/dev/null || true
docker rm old-dragon-rpg 2>/dev/null || true

# Remover imagens antigas
echo "🧹 Limpando imagens antigas..."
docker rmi old-dragon-rpg-web 2>/dev/null || true

# Construir imagem com servidor web dinâmico
echo "🚀 Construindo container com QR code dinâmico..."
docker build -f Dockerfile-web -t old-dragon-rpg-web .

# Executar container
echo "▶️ Iniciando container..."
docker run -d \
  --name old-dragon-rpg \
  -p 8081:8081 \
  -p 19000:19000 \
  -p 19001:19001 \
  -p 19002:19002 \
  old-dragon-rpg-web

# Aguardar inicialização
echo "⏳ Aguardando inicialização..."
sleep 5

# Mostrar logs em tempo real
echo "📋 Logs do container:"
echo "===================="
docker logs -f old-dragon-rpg &

# Aguardar um pouco e mostrar instruções
sleep 10
echo ""
echo "🎉 CONTAINER INICIADO COM SUCESSO!"
echo "=================================="
echo ""
echo "🖥️  Acesse: http://localhost:8081"
echo "📱 QR Code será detectado automaticamente"
echo "🔄 URL do tunnel atualiza em tempo real"
echo ""
echo "📋 Comandos úteis:"
echo "  docker logs old-dragon-rpg     # Ver logs"
echo "  docker stop old-dragon-rpg     # Parar"
echo "  docker start old-dragon-rpg    # Iniciar"
echo ""
echo "⏹️  Para parar os logs: Ctrl+C"
