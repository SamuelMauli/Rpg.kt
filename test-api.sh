#!/bin/bash

echo "========================================"
echo "  Testando API do RPG"
echo "========================================"
echo ""

API_URL="http://localhost:8080"

# Testar se API está rodando
echo "1. Testando conexão com a API..."
response=$(curl -s -o /dev/null -w "%{http_code}" $API_URL)

if [ "$response" = "200" ]; then
    echo "✅ API está rodando!"
else
    echo "❌ API não está respondendo (HTTP $response)"
    echo ""
    echo "Inicie o backend primeiro:"
    echo "  ./start-backend-fixed.sh"
    exit 1
fi

echo ""
echo "2. Testando endpoint de personagens..."
curl -s "$API_URL/api/personagens" | head -c 200
echo ""

echo ""
echo "3. Criando personagem de teste..."
curl -s -X POST "$API_URL/api/personagens" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "TesteHeroi",
    "raca": "humano",
    "classe": "guerreiro",
    "alinhamento": "neutro"
  }' | head -c 300

echo ""
echo ""
echo "4. Listando monstros..."
curl -s "$API_URL/api/monstros?nivelMin=1&nivelMax=3" | head -c 200

echo ""
echo ""
echo "5. Listando itens..."
curl -s "$API_URL/api/itens?tipo=ARMA" | head -c 200

echo ""
echo ""
echo "✅ Testes concluídos!"
echo ""
echo "Para mais testes, acesse:"
echo "  http://localhost:8080"

