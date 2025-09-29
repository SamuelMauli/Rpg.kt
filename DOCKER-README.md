# 🐳 OLD DRAGON RPG - DOCKER EDITION

## 🚀 SOLUÇÃO DEFINITIVA COM DOCKER

**Acabaram os problemas de dependências, rede e configuração!**

---

## ⚡ **EXECUÇÃO RÁPIDA:**

### **1. Clonar repositório:**
```bash
git clone https://github.com/SamuelMauli/Rpg.kt.git
cd Rpg.kt
```

### **2. Executar script Docker:**
```bash
./run-docker.sh
```

**Pronto! O script instala Docker (se necessário) e roda o jogo automaticamente.**

---

## 🐳 **O QUE O DOCKER FAZ:**

✅ **Instala todas as dependências** automaticamente
✅ **Configura Node.js 18** + Expo CLI
✅ **Baixa o projeto** completo
✅ **Resolve problemas de rede** com tunnel
✅ **Isola o ambiente** (não afeta seu sistema)
✅ **Funciona em qualquer sistema** (Windows, Linux, Mac)

---

## 📱 **COMO CONECTAR:**

1. **Execute** `./run-docker.sh`
2. **Aguarde** aparecer o QR code
3. **Baixe Expo Go** no celular
4. **Escaneie** o QR code
5. **Jogue!** 🎮

---

## 🔧 **COMANDOS DOCKER:**

### **Executar:**
```bash
./run-docker.sh
```

### **Parar:**
```bash
docker-compose down
```

### **Ver logs:**
```bash
docker logs old-dragon-rpg
```

### **Reconstruir:**
```bash
docker-compose up --build --force-recreate
```

---

## 🛠️ **MANUAL (SEM SCRIPT):**

```bash
# Instalar Docker (Ubuntu/WSL)
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Reiniciar terminal e executar:
docker-compose up --build
```

---

## 🌐 **PORTAS EXPOSTAS:**

- **8081**: Metro Bundler
- **19000**: Expo DevTools  
- **19001**: Expo CLI
- **19002**: Tunnel

---

## 🎯 **VANTAGENS DO DOCKER:**

🔥 **Zero configuração** - Funciona imediatamente
🔥 **Sem conflitos** - Ambiente isolado
🔥 **Multiplataforma** - Roda em qualquer SO
🔥 **Tunnel automático** - Resolve problemas de rede
🔥 **Sempre atualizado** - Baixa versão mais recente

---

## 🚨 **SOLUÇÃO DE PROBLEMAS:**

### **Docker não instalado:**
```bash
# O script instala automaticamente
./run-docker.sh
```

### **Permissão negada:**
```bash
sudo usermod -aG docker $USER
# Reiniciar terminal
```

### **Container não inicia:**
```bash
docker-compose down
docker-compose up --build --force-recreate
```

### **QR code não aparece:**
```bash
docker logs old-dragon-rpg -f
```

---

## 🎮 **FUNCIONALIDADES DO JOGO:**

✨ **RPG completo** baseado em Old Dragon 2
✨ **4 raças** e **4 classes** jogáveis
✨ **Sistema de combate** por turnos
✨ **5 áreas** de aventura épica
✨ **Sprites 8-bit** autênticos
✨ **Boss final**: Necromante Malachar

---

## 📋 **REQUISITOS:**

- **Docker** (instalado automaticamente)
- **Celular** com Expo Go
- **Conexão** com internet

---

## 🏆 **GARANTIA:**

**Se não funcionar com Docker, o problema não é do código! 🐳**

---

**Execute `./run-docker.sh` e jogue! ⚔️🐉**
