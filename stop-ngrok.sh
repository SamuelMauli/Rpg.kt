#!/bin/bash

echo "⏹️  Parando ngrok existente..."

# Parar todos os processos ngrok
pkill ngrok 2>/dev/null || true

# Aguardar um pouco
sleep 2

# Verificar se ainda há processos
if pgrep ngrok > /dev/null; then
    echo "🔄 Forçando parada do ngrok..."
    pkill -9 ngrok 2>/dev/null || true
    sleep 2
fi

# Verificar portas ocupadas
if lsof -ti:4040 > /dev/null 2>&1; then
    echo "🔄 Liberando porta 4040..."
    kill -9 $(lsof -ti:4040) 2>/dev/null || true
fi

echo "✅ ngrok parado com sucesso!"

# Mostrar processos ngrok restantes (se houver)
if pgrep ngrok > /dev/null; then
    echo "⚠️  Ainda há processos ngrok rodando:"
    ps aux | grep ngrok | grep -v grep
else
    echo "✅ Nenhum processo ngrok encontrado"
fi
