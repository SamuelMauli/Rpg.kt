# 🎮 Old Dragon RPG Mobile

**RPG completo baseado no sistema Old Dragon 2 para dispositivos móveis**

## 🚀 COMO EXECUTAR NO SEU WSL

### 1️⃣ **Pré-requisitos**
```bash
# Instalar Node.js
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# Instalar Expo CLI
npm install -g @expo/cli

# Instalar Expo Go no celular (Android/iOS)
```

### 2️⃣ **Baixar e Executar**
```bash
# Clonar repositório
git clone https://github.com/SamuelMauli/Rpg.kt.git
cd Rpg.kt/mobile

# Instalar dependências
npm install

# Iniciar o jogo
npm start
```

### 3️⃣ **Conectar Celular**
1. Abra o **Expo Go** no seu celular
2. Escaneie o **QR code** que aparece no terminal
3. **Jogue!** 🎮

---

## 🎯 **FUNCIONALIDADES COMPLETAS**

### ✅ **Sistema de Jogo**
- [x] **4 Raças**: Humano, Elfo, Anão, Halfling
- [x] **4 Classes**: Guerreiro, Ladrão, Clérigo, Mago  
- [x] **Sistema de Dados**: Rolagem automática d20/d6
- [x] **Atributos**: Força, Destreza, Constituição, Inteligência, Sabedoria, Carisma
- [x] **Modificadores**: Calculados automaticamente

### ⚔️ **Combate**
- [x] **Sistema por Turnos**: Fiel ao Old Dragon 2
- [x] **Múltiplos Inimigos**: Até 5 monstros simultâneos
- [x] **Iniciativa**: Baseada em Destreza
- [x] **CA e Base de Ataque**: Sistema completo
- [x] **Fuga**: Possibilidade de escapar

### 🏰 **Aventura**
- [x] **5 Áreas Únicas**: Caverna → Santuário do Necromante
- [x] **Eventos Narrativos**: Escolhas que afetam o jogo
- [x] **Boss Final**: Necromante Malachar
- [x] **Sistema de Progressão**: XP e níveis

### 🎨 **Visual 8-bit**
- [x] **Sprites de Personagens**: 4 classes únicas
- [x] **Sprites de Monstros**: 5 tipos diferentes
- [x] **Backgrounds**: 3 ambientes temáticos
- [x] **Interface**: Botões e ícones pixel art

---

## 🐉 **MONSTROS DISPONÍVEIS**

| Nível | Monstros | Dificuldade |
|-------|----------|-------------|
| 1 | Rato Gigante, Kobold | Fácil |
| 2 | Goblin, Esqueleto | Médio |
| 3 | Orc, Zumbi | Difícil |
| 4 | Bugbear | Muito Difícil |
| 5 | Necromante | Boss Final |

---

## 🎲 **COMO JOGAR**

### **1. Criação de Personagem**
1. Digite o **nome** do seu herói
2. Escolha uma **raça** (cada uma tem bônus únicos)
3. Escolha uma **classe** (define habilidades)
4. **Role os dados** para atributos

### **2. Aventura**
- Leia as **descrições** das áreas
- Faça **escolhas estratégicas**
- Gerencie seus **pontos de vida**
- Explore **5 áreas diferentes**

### **3. Combate**
- **Selecione o alvo** tocando no monstro
- Toque em **"ATACAR"** para lutar
- Use **"FUGIR"** estrategicamente
- Acompanhe o **log de combate**

### **4. Progressão**
- **Ganhe XP** derrotando inimigos
- **Suba de nível** automaticamente
- **Encontre tesouros** explorando

---

## 🛠️ **COMANDOS ÚTEIS**

```bash
# Iniciar jogo
npm start

# Limpar cache (se der problema)
npx expo start --clear

# Modo tunnel (se QR code não funcionar)
npx expo start --tunnel

# Verificar problemas
npx expo doctor
```

---

## 🔧 **SOLUÇÃO DE PROBLEMAS**

### ❌ **QR Code não funciona**
```bash
npx expo start --tunnel
```

### ❌ **App não carrega**
```bash
npx expo start --clear
```

### ❌ **Erro de dependências**
```bash
rm -rf node_modules package-lock.json
npm install
```

### ❌ **Expo Go não conecta**
- Verifique se celular e PC estão na **mesma rede WiFi**
- Use **modo tunnel**
- Reinicie o **Expo Go**

---

## 📱 **COMPATIBILIDADE**

- **Android**: 5.0+ (API 21+)
- **iOS**: 11.0+
- **Node.js**: 16.0+
- **Expo SDK**: 49+

---

## 🏆 **DICAS DE JOGO**

### **Escolha de Classe**
- **Guerreiro**: Melhor para iniciantes (alta resistência)
- **Ladrão**: Ágil, ataques furtivos
- **Clérigo**: Equilibrado, pode curar
- **Mago**: Poderoso mas frágil

### **Estratégias de Combate**
- Observe a **CA dos inimigos**
- Gerencie seus **pontos de vida**
- Use **fuga estrategicamente**
- Monstros podem **fugir se feridos**

---

## 🎉 **PRONTO PARA JOGAR!**

Agora você tem um **RPG completo** no seu celular!

**Que sua aventura seja épica! ⚔️🐉**

### **Comandos Rápidos:**
```bash
cd ~/Rpg.kt/mobile
npm install
npm start
# Escaneie QR code com Expo Go
```
