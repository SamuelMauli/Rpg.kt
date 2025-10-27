# 🐳 Docker Setup - Old Dragon 2 RPG

## 📋 Pré-requisitos

- Docker instalado ([Download](https://docs.docker.com/get-docker/))
- Docker Compose instalado ([Download](https://docs.docker.com/compose/install/))

## 🚀 Início Rápido

### Opção 1: Script Automatizado (Recomendado)

```bash
./docker-start.sh
```

### Opção 2: Comandos Manuais

```bash
# Construir a imagem
docker-compose build

# Iniciar o jogo
docker-compose up

# Ou em modo detached (background)
docker-compose up -d

# Ver logs
docker-compose logs -f

# Parar o jogo
docker-compose down
```

## 🎮 Como Jogar

1. Execute o script ou comando docker-compose
2. O jogo iniciará automaticamente
3. Siga as instruções no terminal
4. Use Ctrl+C para sair

## 📁 Estrutura de Volumes

O Docker monta os seguintes diretórios:

- `./database` - Banco de dados SQLite (persistente)
- `./logs` - Logs do jogo (persistente)

## 🔧 Configuração

### Dockerfile

O Dockerfile usa:
- **Base**: OpenJDK 17 Slim
- **Build**: Gradle
- **Runtime**: JVM

### docker-compose.yml

Configurações:
- **Container**: old-dragon-rpg-game
- **Modo**: Interativo (stdin_open + tty)
- **Volumes**: database e logs
- **Restart**: unless-stopped

## 🐛 Solução de Problemas

### Erro: "Docker não encontrado"

Instale o Docker:
```bash
# Ubuntu/Debian
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Ou siga: https://docs.docker.com/get-docker/
```

### Erro: "Permissão negada"

Adicione seu usuário ao grupo docker:
```bash
sudo usermod -aG docker $USER
newgrp docker
```

### Erro ao compilar

Limpe e reconstrua:
```bash
docker-compose down
docker-compose build --no-cache
docker-compose up
```

### Container não inicia

Verifique logs:
```bash
docker-compose logs
```

## 📊 Comandos Úteis

```bash
# Ver containers rodando
docker ps

# Ver todos os containers
docker ps -a

# Acessar shell do container
docker exec -it old-dragon-rpg-game bash

# Remover imagens antigas
docker image prune

# Limpar tudo
docker system prune -a
```

## 🔄 Atualização

Para atualizar o jogo:

```bash
# Parar container
docker-compose down

# Atualizar código
git pull

# Reconstruir
docker-compose build

# Iniciar
docker-compose up
```

## 📝 Notas

- O banco de dados é criado automaticamente na primeira execução
- Os dados são persistidos no diretório `./database`
- O jogo roda em modo interativo no terminal
- Use `docker-compose up -d` para rodar em background (não recomendado para jogos interativos)

## 🎯 Exemplo de Uso

```bash
# Clone o repositório
git clone https://github.com/SamuelMauli/Rpg.kt.git
cd Rpg.kt

# Execute o script
./docker-start.sh

# Ou manualmente
docker-compose up
```

## 🆘 Suporte

Se encontrar problemas:

1. Verifique os logs: `docker-compose logs`
2. Limpe e reconstrua: `docker-compose down && docker-compose build --no-cache`
3. Consulte a documentação do Docker
4. Abra uma issue no GitHub

---

**Desenvolvido com ❤️ usando Docker** 🐳

