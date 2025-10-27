# 🚀 Instalação Rápida - Old Dragon 2 RPG

## ⚠️ Problema de Permissão?

Se você receber erro de permissão ao executar os scripts, execute primeiro:

```bash
chmod +x setup-permissions.sh
./setup-permissions.sh
```

Ou manualmente:

```bash
chmod +x install.sh docker-start.sh gradlew
```

## 🎮 Executar o Jogo

### Método 1: Script de Instalação (Recomendado)

```bash
./install.sh
```

### Método 2: Docker

```bash
./docker-start.sh
```

### Método 3: Gradle Direto

```bash
./gradlew clean build
./gradlew run
```

## 📋 Pré-requisitos

### Para execução local:
- **Java 11+** instalado
- **Gradle** (incluído via wrapper)

### Para Docker:
- **Docker** instalado
- **Docker Compose** instalado

## 🔧 Instalação do Java

### Ubuntu/Debian
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

### Fedora/RHEL
```bash
sudo dnf install java-17-openjdk
```

### macOS
```bash
brew install openjdk@17
```

### Windows
Baixe e instale do site oficial:
https://adoptium.net/

## 🐳 Instalação do Docker

### Linux
```bash
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER
```

### macOS/Windows
Baixe Docker Desktop:
https://www.docker.com/products/docker-desktop

## ✅ Verificar Instalação

```bash
# Verificar Java
java -version

# Verificar Docker
docker --version
docker-compose --version
```

## 🎯 Primeira Execução

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/SamuelMauli/Rpg.kt.git
   cd Rpg.kt
   ```

2. **Configure permissões:**
   ```bash
   chmod +x setup-permissions.sh
   ./setup-permissions.sh
   ```

3. **Execute o jogo:**
   ```bash
   ./install.sh
   ```

## 🐛 Problemas Comuns

### "Permission denied"
```bash
chmod +x install.sh
./install.sh
```

### "Java not found"
Instale Java 17 (veja seção acima)

### "Docker not found"
Instale Docker (veja seção acima)

### Erro de compilação
```bash
./gradlew clean build --refresh-dependencies
```

## 📞 Ajuda

Para mais informações, consulte:
- [README.md](README.md) - Documentação principal
- [DOCKER_SETUP.md](DOCKER_SETUP.md) - Guia Docker
- [INDEX.md](INDEX.md) - Índice completo

---

**Boa aventura! ⚔️🐉✨**

