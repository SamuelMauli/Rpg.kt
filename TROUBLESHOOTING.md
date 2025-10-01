# 🔧 Guia de Solução de Problemas - RPG System

## 🚨 Problemas Comuns e Soluções

### ❌ Erro: "Container name already in use"

**Problema**: Container com nome conflitante já existe
```
Error response from daemon: Conflict. The container name "/old-dragon-rpg" is already in use
```

**Solução**:
```bash
# Opção 1: Limpeza automática
./cleanup.sh

# Opção 2: Via Makefile
make clean

# Opção 3: Manual
docker stop old-dragon-rpg rpg_backend rpg_mysql
docker rm old-dragon-rpg rpg_backend rpg_mysql
```

### ❌ Erro: "Permission denied" nos diretórios

**Problema**: Não consegue criar ou alterar permissões de diretórios
```
chmod: changing permissions of 'logs': Operation not permitted
```

**Solução**: O script foi atualizado para ignorar esse erro. É normal em alguns sistemas.

### ❌ MySQL não conecta

**Problema**: Backend não consegue conectar com MySQL

**Solução**:
```bash
# Verificar se MySQL está rodando
docker-compose ps

# Verificar logs do MySQL
docker-compose logs mysql

# Reiniciar apenas o MySQL
docker-compose restart mysql

# Aguardar mais tempo (MySQL demora para inicializar)
sleep 30
```

### ❌ Expo não inicia

**Problema**: Aplicação Expo não carrega

**Solução**:
```bash
# Verificar logs do container Expo
docker-compose logs expo-rpg

# Reconstruir imagem
docker-compose build --no-cache expo-rpg

# Verificar se as portas estão livres
netstat -tulpn | grep :8081
```

### ❌ ngrok não funciona

**Problema**: ngrok não consegue criar túnel

**Solução**:
```bash
# Verificar se ngrok está instalado
which ngrok

# Reconfigurar ngrok
./deploy.sh ngrok

# Verificar logs
tail -f logs/ngrok.log

# Testar manualmente
ngrok http 8081
```

## 🛠️ Comandos de Diagnóstico

### Verificar Status Geral
```bash
# Status completo
./deploy.sh status

# Via Makefile
make status

# Health check
make health
```

### Verificar Containers
```bash
# Listar todos os containers
docker ps -a

# Verificar logs específicos
docker-compose logs mysql
docker-compose logs expo-rpg
docker-compose logs backend
```

### Verificar Rede e Portas
```bash
# Verificar portas em uso
netstat -tulpn | grep -E ":(3306|5000|8081|19000)"

# Testar conectividade
curl http://localhost:8081
curl http://localhost:5000/api/health
```

### Verificar Banco de Dados
```bash
# Conectar ao MySQL
make mysql

# Ou manualmente
docker-compose exec mysql mysql -u rpg_user -prpg_pass_2024 rpg_database

# Verificar tabelas
SHOW TABLES;
```

## 🔄 Procedimentos de Recuperação

### Limpeza Completa e Reinício
```bash
# 1. Limpeza completa
./cleanup.sh

# 2. Deploy novo
./deploy.sh deploy

# 3. Verificar status
./deploy.sh status
```

### Reconstrução de Imagens
```bash
# Reconstruir todas as imagens
make build

# Ou via docker-compose
docker-compose build --no-cache
```

### Reset do Banco de Dados
```bash
# Parar serviços
./deploy.sh stop

# Remover volume do MySQL (CUIDADO: apaga todos os dados)
docker volume rm rpgkt_mysql_data

# Reiniciar
./deploy.sh deploy
```

## 📋 Checklist de Verificação

Antes de reportar problemas, verifique:

- [ ] Docker está instalado e rodando
- [ ] Docker Compose está instalado
- [ ] Portas 3306, 5000, 8081 estão livres
- [ ] Não há containers conflitantes rodando
- [ ] Arquivo .env existe (criado automaticamente)
- [ ] Diretórios logs, uploads, database existem

## 🆘 Comandos de Emergência

### Parar Tudo
```bash
# Parar todos os containers Docker
docker stop $(docker ps -aq)

# Parar ngrok
pkill -f ngrok
```

### Limpar Tudo
```bash
# Limpeza forçada (CUIDADO: remove tudo)
make force-clean

# Ou manualmente
docker system prune -af --volumes
```

### Verificar Recursos
```bash
# Monitorar recursos
make monitor

# Verificar espaço em disco
df -h

# Verificar memória
free -h
```

## 📞 Suporte

Se os problemas persistirem:

1. Execute `./deploy.sh status` e anote a saída
2. Verifique os logs: `./deploy.sh logs`
3. Tente a limpeza completa: `./cleanup.sh`
4. Se necessário, reinicie o sistema

**Lembre-se**: O sistema foi projetado para ser resiliente. Na maioria dos casos, `./cleanup.sh` seguido de `./deploy.sh deploy` resolve os problemas.
