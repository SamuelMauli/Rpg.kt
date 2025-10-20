# 🔧 Solução de Erros - RPG.kt

## Problemas Identificados e Soluções

---

## ❌ Erro 1: Gradle Wrapper Timeout

### Problema
```
Exception in thread "main" java.io.IOException: 
Downloading from https://services.gradle.org/distributions/gradle-8.5-bin.zip failed: timeout (10000ms)
```

### Causa
O Gradle wrapper está tentando baixar o Gradle da internet mas está tendo timeout.

### Soluções

#### Opção 1: Usar Gradle do Sistema (RECOMENDADO)

```bash
# Instalar Gradle no sistema
sudo pacman -S gradle  # Arch Linux
# ou
sudo apt install gradle  # Ubuntu/Debian

# Usar script alternativo
./start-backend-simple.sh
```

#### Opção 2: Aumentar Timeout do Wrapper

Edite `gradle/wrapper/gradle-wrapper.properties`:
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip
networkTimeout=60000
```

#### Opção 3: Baixar Gradle Manualmente

```bash
# Baixar Gradle
cd ~
wget https://services.gradle.org/distributions/gradle-8.5-bin.zip

# Extrair
unzip gradle-8.5-bin.zip

# Adicionar ao PATH
export PATH=$PATH:~/gradle-8.5/bin

# Voltar ao projeto e compilar
cd ~/Rpg.kt
gradle build
```

#### Opção 4: Usar Gradle Wrapper Existente

Se você já tem o Gradle wrapper baixado em outro projeto:

```bash
# Copiar de outro projeto
cp -r /caminho/outro/projeto/gradle ~/.gradle/wrapper/dists/

# Ou usar o gradle do sistema
gradle wrapper --gradle-version 8.5
```

---

## ❌ Erro 2: Permissão Negada em Logs

### Problema
```
Error: EACCES: permission denied, open '/home/samuel/Rpg.kt/logs/access.log'
```

### Causa
O diretório `logs/` não existe ou não tem permissões de escrita.

### Solução Rápida

```bash
# Executar script de correção
./fix-permissions.sh
```

### Solução Manual

```bash
# Criar diretório
mkdir -p logs

# Dar permissões
chmod -R 755 logs

# Criar arquivos de log
touch logs/access.log logs/error.log

# Dar permissão de escrita
chmod 666 logs/*.log
```

### Solução Alternativa: Desabilitar Logs em Arquivo

Edite `web-server-enhanced.js` e comente as linhas de log:

```javascript
// const accessLogStream = fs.createWriteStream(
//   path.join(__dirname, 'logs/access.log'),
//   { flags: 'a' }
// );
```

---

## 🚀 Guia Completo de Execução

### 1. Preparar Ambiente

```bash
cd ~/Rpg.kt

# Corrigir permissões
./fix-permissions.sh

# Instalar Gradle (se necessário)
sudo pacman -S gradle  # ou sudo apt install gradle
```

### 2. Compilar Backend

```bash
# Opção A: Com Gradle do sistema
./start-backend-simple.sh

# Opção B: Com wrapper (se funcionar)
./start-backend.sh

# Opção C: Manual
gradle build
java -cp "build/libs/*:build/classes/kotlin/main" com.rpg.api.ApiServerKt
```

### 3. Executar Frontend Mobile

```bash
cd mobile

# Instalar dependências (primeira vez)
npm install

# Iniciar
npm start
```

---

## 🔍 Verificações

### Verificar se Gradle está instalado

```bash
gradle --version
```

**Saída esperada:**
```
------------------------------------------------------------
Gradle 8.5
------------------------------------------------------------
```

### Verificar se Java está instalado

```bash
java -version
```

**Saída esperada:**
```
openjdk version "11.0.x" ou superior
```

### Verificar se Node.js está instalado

```bash
node --version
npm --version
```

**Saída esperada:**
```
v18.x.x ou superior
9.x.x ou superior
```

---

## 📦 Instalação de Dependências

### Arch Linux

```bash
# Instalar tudo de uma vez
sudo pacman -S jdk-openjdk gradle nodejs npm kotlin

# Verificar instalação
java -version
gradle --version
node --version
kotlinc -version
```

### Ubuntu/Debian

```bash
# Instalar Java
sudo apt install openjdk-11-jdk

# Instalar Gradle
sudo apt install gradle

# Instalar Node.js
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install nodejs

# Instalar Kotlin
sudo snap install kotlin --classic
```

---

## 🐛 Outros Problemas Comuns

### Problema: "Command not found: kotlinc"

**Solução:**
```bash
# Instalar Kotlin compiler
sudo pacman -S kotlin  # Arch
sudo snap install kotlin --classic  # Ubuntu
```

### Problema: "Port 8080 already in use"

**Solução:**
```bash
# Encontrar processo usando a porta
lsof -i :8080

# Matar processo
kill -9 <PID>

# Ou usar outra porta
# Edite ApiServer.kt e mude a porta
```

### Problema: "Cannot connect to MySQL"

**Solução:**

O backend Kotlin usa SQLite, não MySQL. Se você está vendo erro de MySQL, é do web-server-enhanced.js.

```bash
# Opção 1: Instalar MySQL
sudo pacman -S mysql
sudo systemctl start mysqld

# Opção 2: Usar apenas o backend Kotlin
# Ignore o web-server-enhanced.js e use apenas:
./start-backend-simple.sh
```

### Problema: "Module not found" no mobile

**Solução:**
```bash
cd mobile
rm -rf node_modules package-lock.json
npm install
```

---

## 📝 Resumo dos Scripts Criados

| Script | Descrição |
|--------|-----------|
| `start-backend.sh` | Script original (usa wrapper) |
| `start-backend-simple.sh` | Usa Gradle do sistema |
| `compile-backend.sh` | Compilação manual com kotlinc |
| `fix-permissions.sh` | Corrige permissões de logs |

---

## 🎯 Fluxo Recomendado

1. **Primeira vez:**
```bash
./fix-permissions.sh
sudo pacman -S gradle  # ou apt install gradle
```

2. **Iniciar backend:**
```bash
./start-backend-simple.sh
```

3. **Iniciar mobile (em outro terminal):**
```bash
cd mobile
npm start
```

4. **Acessar:**
- Backend: http://localhost:8080
- Mobile: Escanear QR code com Expo Go

---

## 💡 Dicas

1. **Use Gradle do sistema** ao invés do wrapper se tiver problemas de rede
2. **Sempre execute fix-permissions.sh** antes de iniciar pela primeira vez
3. **Mantenha Java 11+** instalado
4. **Use Node.js 18+** para o mobile
5. **Certifique-se** de estar na mesma rede Wi-Fi (mobile e backend)

---

## 📞 Ainda com Problemas?

Se ainda tiver problemas:

1. Verifique os logs:
```bash
cat logs/error.log
```

2. Execute com debug:
```bash
gradle build --debug
```

3. Teste a API manualmente:
```bash
curl http://localhost:8080/
```

---

**Última atualização:** 2025-10-20

