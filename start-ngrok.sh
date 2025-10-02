#!/bin/bash

echo "🌐 Iniciando ngrok..."

# Parar ngrok existente
pkill ngrok 2>/dev/null || true
sleep 2

# Verificar se a porta 5000 está ocupada
if ! lsof -ti:5000 > /dev/null 2>&1; then
    echo "❌ Backend não está rodando na porta 5000"
    echo "Execute primeiro: sudo docker-compose -f docker-compose-full.yml up -d backend"
    exit 1
fi

# Configurar authtoken
ngrok config add-authtoken 33U5DE9pqa3Dk4GwWH8VdslC5iL_2Vrr1B4qxR6oc9mkEikah

# Tentar iniciar ngrok com domínio personalizado
echo "🔄 Tentando iniciar ngrok com domínio personalizado..."
if ngrok http 5000 --domain=nonlustrously-nonsustainable-ronny.ngrok-free.dev > logs/ngrok.log 2>&1 &
then
    NGROK_PID=$!
    echo "✅ ngrok iniciado com PID: $NGROK_PID"
    
    # Aguardar um pouco para ngrok inicializar
    sleep 5
    
    # Verificar se ngrok está funcionando
    if ps -p $NGROK_PID > /dev/null; then
        echo "✅ ngrok está rodando"
        echo "🌍 Público: https://nonlustrously-nonsustainable-ronny.ngrok-free.dev"
        echo "📊 Inspector: http://localhost:4040"
    else
        echo "❌ ngrok falhou ao iniciar"
        echo "📋 Log do ngrok:"
        cat logs/ngrok.log
        
        # Tentar sem domínio personalizado
        echo "🔄 Tentando sem domínio personalizado..."
        ngrok http 5000 > logs/ngrok-simple.log 2>&1 &
        NGROK_PID=$!
        sleep 3
        
        if ps -p $NGROK_PID > /dev/null; then
            echo "✅ ngrok funcionando sem domínio personalizado"
            echo "📊 Inspector: http://localhost:4040"
        else
            echo "❌ ngrok falhou completamente"
            cat logs/ngrok-simple.log
        fi
    fi
else
    echo "❌ Erro ao iniciar ngrok"
fi

# Mostrar processos ngrok
echo ""
echo "📊 Processos ngrok ativos:"
ps aux | grep ngrok | grep -v grep || echo "Nenhum processo ngrok encontrado"
