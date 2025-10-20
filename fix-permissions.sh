#!/bin/bash

echo "========================================"
echo "  Corrigindo Permissões"
echo "========================================"
echo ""

# Criar diretório de logs se não existir
if [ ! -d "logs" ]; then
    echo "Criando diretório logs..."
    mkdir -p logs
fi

# Corrigir permissões
echo "Corrigindo permissões do diretório logs..."
chmod -R 755 logs

# Criar arquivos de log se não existirem
touch logs/access.log
touch logs/error.log

# Dar permissão de escrita
chmod 666 logs/*.log

echo ""
echo "✅ Permissões corrigidas!"
echo ""
echo "Arquivos criados:"
ls -la logs/

echo ""
echo "Agora você pode executar:"
echo "  npm start"

