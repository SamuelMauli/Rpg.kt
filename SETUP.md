# 🚀 Setup Rápido - RPG System

## ⚡ Instalação em 1 Comando

```bash
./install-and-deploy.sh
```

**Pronto!** Este comando faz tudo automaticamente:
- ✅ Instala Docker e Docker Compose
- ✅ Instala e configura ngrok
- ✅ Cria banco MySQL com schema RPG
- ✅ Inicia backend API
- ✅ Inicia aplicação Expo
- ✅ Configura domínio público

## 🎯 URLs de Acesso

Após a instalação:
- 🎮 **Expo App**: http://localhost:8081
- 🔧 **Backend API**: http://localhost:5000
- 🏥 **Health Check**: http://localhost:5000/api/health
- 🌍 **Público**: https://nonlustrously-nonsustainable-ronny.ngrok-free.dev
- 📊 **ngrok Inspector**: http://localhost:4040

## 🛠️ Comandos Úteis

```bash
# Ver status dos containers
sudo docker-compose ps

# Ver logs em tempo real
sudo docker-compose logs -f

# Parar sistema
sudo docker-compose down

# Reiniciar sistema
sudo docker-compose restart

# Conectar ao MySQL
sudo docker-compose exec mysql mysql -u rpg_user -prpg_pass_2024 rpg_database
```

## 🔧 Se Algo Der Errado

### Problema: Docker não funciona
```bash
# Reiniciar Docker
sudo systemctl restart docker

# Ou reiniciar o sistema
sudo reboot
```

### Problema: Containers não iniciam
```bash
# Limpar tudo e recomeçar
sudo docker system prune -af --volumes
./install-and-deploy.sh
```

### Problema: Permissões do Docker
```bash
# Ativar grupo docker
newgrp docker

# Ou fazer logout/login
```

## 📋 O Que o Sistema Inclui

### 🗄️ Banco de Dados MySQL
- Usuários e autenticação
- Personagens com atributos D&D
- Campanhas multiplayer
- Sistema de itens e equipamentos
- Magias e habilidades
- Inventário por personagem
- Logs de jogo

### 🔧 Backend API
- Autenticação JWT
- Endpoints RESTful
- Integração MySQL
- Logs estruturados
- Health checks

### 🎮 Frontend Expo
- Interface React Native
- Compatível com web e mobile
- Hot reload para desenvolvimento

### 🌐 Exposição Pública
- ngrok com domínio personalizado
- SSL automático
- Inspeção de tráfego

## 🎯 Primeiro Uso

1. **Execute a instalação**: `./install-and-deploy.sh`
2. **Aguarde** a instalação completar (pode demorar alguns minutos)
3. **Acesse** http://localhost:8081 no navegador
4. **Teste a API** em http://localhost:5000/api/health
5. **Acesse publicamente** via https://nonlustrously-nonsustainable-ronny.ngrok-free.dev

## 📞 Suporte

Se tiver problemas:
1. Verifique se todos os containers estão rodando: `sudo docker-compose ps`
2. Veja os logs: `sudo docker-compose logs -f`
3. Tente reiniciar: `sudo docker-compose restart`
4. Se necessário, limpe tudo e reinstale

**O sistema foi projetado para funcionar automaticamente!** 🎉
