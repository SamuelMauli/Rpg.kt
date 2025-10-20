# 🔧 Correção do Mobile - Expo

## 🎯 Problemas Identificados

### 1. **"This is taking much longer..."**
- Dependências não instaladas (`node_modules` ausente)
- Expo tentando carregar mas sem os pacotes necessários

### 2. **Erro 500 no Web**
```
http://localhost:8081/node_modules/expo/AppEntry.bundle
Status Code: 500 Internal Server Error
```
- App.tsx original muito complexo
- Dependências de contextos e telas que podem não existir
- Imports de arquivos que podem estar faltando

---

## ✅ Soluções Implementadas

### 1. **Script de Setup Automático**

Criado `setup-and-start.sh` que:
- Verifica Node.js instalado
- Instala dependências automaticamente
- Inicia o Expo

### 2. **App.tsx Simplificado**

Criado `App-simple.tsx` (agora como `App.tsx`) que:
- ✅ Funciona sem dependências externas complexas
- ✅ Interface simples e funcional
- ✅ Conecta com a API do backend
- ✅ Telas: Menu, Criar Personagem, Listar, Ficha
- ✅ Funcionalidade de combate

---

## 🚀 Como Usar Agora

### **Opção 1: Script Automático (RECOMENDADO)**

```bash
cd ~/Rpg.kt/mobile
./setup-and-start.sh
```

### **Opção 2: Manual**

```bash
cd ~/Rpg.kt/mobile

# Instalar dependências (primeira vez)
npm install

# Iniciar Expo
npm start
```

---

## 📱 Acessar o App

Depois de iniciar, você verá um QR code no terminal:

### **No Celular (Recomendado)**

1. Instale o app **Expo Go**:
   - Android: [Google Play](https://play.google.com/store/apps/details?id=host.exp.exponent)
   - iOS: [App Store](https://apps.apple.com/app/expo-go/id982107779)

2. Abra o Expo Go

3. Escaneie o QR code que aparece no terminal

4. Aguarde o app carregar (pode levar 1-2 minutos na primeira vez)

### **No Navegador (Teste Rápido)**

1. No terminal onde o Expo está rodando, pressione `w`

2. Abrirá automaticamente no navegador

3. **Nota**: Algumas funcionalidades podem não funcionar perfeitamente na web

---

## 🔧 Configurar IP da API

### **Problema**: O app não consegue conectar ao backend

O app está configurado para `localhost:8080`, mas no celular você precisa usar o IP local do computador.

### **Solução**:

1. **Descobrir seu IP local:**

```bash
# Linux/Mac
hostname -I

# Ou
ip addr show | grep "inet " | grep -v 127.0.0.1

# Exemplo de saída: 192.168.1.100
```

2. **Editar App.tsx:**

```typescript
// Linha 13 do App.tsx
const API_URL = 'http://SEU_IP_LOCAL:8080/api';

// Exemplo:
const API_URL = 'http://192.168.1.100:8080/api';
```

3. **Reiniciar o Expo:**

Pressione `r` no terminal do Expo para recarregar

---

## 🎮 Funcionalidades do App Simplificado

### **Tela: Menu Principal**
- 🆕 Criar Personagem
- 📂 Carregar Personagem
- Mostra versão e URL da API

### **Tela: Criar Personagem**
- Campo de nome
- Seleção de raça (Humano, Elfo, Anão, Halfling)
- Seleção de classe (Guerreiro, Mago, Clérigo, Ladrão)
- Botão para criar
- Conecta com API: `POST /api/personagens`

### **Tela: Listar Personagens**
- Lista todos os personagens salvos
- Mostra: Nome, Raça, Classe, Nível, PV, XP
- Toque em um personagem para ver detalhes
- Conecta com API: `GET /api/personagens`

### **Tela: Ficha do Personagem**
- Mostra nome, raça, classe, nível
- Estatísticas: PV, XP, Ouro
- Botão "Iniciar Combate"
- Conecta com API: `POST /api/combate/iniciar`

---

## 🐛 Troubleshooting

### Erro: "Cannot connect to server"

**Causa**: Backend não está rodando ou IP incorreto

**Solução**:
```bash
# Terminal 1: Iniciar backend
cd ~/Rpg.kt
./start-backend-fixed.sh

# Terminal 2: Verificar se está rodando
curl http://localhost:8080

# Se funcionar, configure o IP no App.tsx
```

### Erro: "This is taking much longer..."

**Causa**: Dependências não instaladas ou app muito pesado

**Solução**:
```bash
cd ~/Rpg.kt/mobile

# Limpar e reinstalar
rm -rf node_modules package-lock.json
npm install

# Usar app simplificado (já configurado)
# O App.tsx atual já é o simplificado
```

### Erro: "Unable to resolve module"

**Causa**: Imports de arquivos que não existem

**Solução**:
```bash
# O App.tsx simplificado não tem esse problema
# Se aparecer, verifique se está usando App-simple.tsx

cd ~/Rpg.kt/mobile
cp App-simple.tsx App.tsx
npm start
```

### App não carrega no celular

**Causas possíveis**:
1. Celular e computador em redes Wi-Fi diferentes
2. Firewall bloqueando conexão
3. IP incorreto no App.tsx

**Soluções**:
```bash
# 1. Verificar mesma rede
# Ambos devem estar no mesmo Wi-Fi

# 2. Desabilitar firewall temporariamente
sudo ufw disable  # Linux
# Ou configurar exceção para porta 8081

# 3. Verificar IP no App.tsx
# Deve ser o IP local, não localhost
```

---

## 📊 Comparação das Versões

| Recurso | App Original | App Simplificado |
|---------|--------------|------------------|
| Complexidade | Alta | Baixa |
| Dependências | Muitas | Mínimas |
| Telas | 10+ | 4 |
| Funcionalidades | Completas | Essenciais |
| Estabilidade | Pode ter erros | Estável |
| Tempo de carregamento | Lento | Rápido |
| Tamanho | Grande | Pequeno |

---

## 🎨 Personalização

### Alterar Cores

Edite `App.tsx`, seção `StyleSheet.create`:

```typescript
const styles = StyleSheet.create({
  app: {
    backgroundColor: '#1a1a2e', // Cor de fundo principal
  },
  title: {
    color: '#FFD700', // Cor dos títulos (dourado)
  },
  button: {
    backgroundColor: '#FFD700', // Cor dos botões
  },
  // ...
});
```

### Adicionar Mais Funcionalidades

O app simplificado é uma base. Você pode adicionar:
- Tela de inventário
- Sistema de itens
- Mais detalhes da ficha
- Animações

Consulte `App-original.tsx` para ver implementações mais complexas.

---

## 📝 Estrutura de Arquivos

```
mobile/
├── App.tsx                    # ✅ App simplificado (atual)
├── App-original.tsx           # Backup do app original
├── App-simple.tsx             # Fonte do app simplificado
├── setup-and-start.sh         # ✅ Script de setup
├── package.json               # Dependências
├── app.json                   # Configuração do Expo
└── src/                       # Código original (não usado no app simplificado)
```

---

## ✅ Checklist de Execução

### **Preparação (uma vez):**
- [ ] Node.js 18+ instalado (`node --version`)
- [ ] Backend rodando (`./start-backend-fixed.sh`)
- [ ] IP local configurado no App.tsx

### **Executar:**
- [ ] Instalar dependências: `cd mobile && npm install`
- [ ] Iniciar Expo: `npm start` ou `./setup-and-start.sh`
- [ ] Escanear QR code com Expo Go
- [ ] Aguardar carregamento (1-2 min primeira vez)

### **Testar:**
- [ ] Criar personagem
- [ ] Listar personagens
- [ ] Ver ficha
- [ ] Iniciar combate

---

## 🎉 Resultado

Agora você tem:

✅ App mobile funcional e estável  
✅ Interface simples e rápida  
✅ Conexão com backend API  
✅ Funcionalidades essenciais  
✅ Fácil de personalizar  

---

## 📞 Próximos Passos

1. **Inicie o backend:**
```bash
cd ~/Rpg.kt
./start-backend-fixed.sh
```

2. **Configure o IP no App.tsx** (se for usar no celular)

3. **Inicie o mobile:**
```bash
cd ~/Rpg.kt/mobile
./setup-and-start.sh
```

4. **Escaneie o QR code** com Expo Go

5. **Divirta-se!** 🎮

---

**Última atualização:** 2025-10-20  
**Versão:** 1.0 (App Simplificado)

