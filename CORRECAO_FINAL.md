# ✅ Correção Final - Problema Resolvido!

## 🎯 Problema Identificado

O erro `ClassNotFoundException: com.rpg.api.ApiServerKt` ocorria porque:

1. O comando `gradle` não estava no PATH do sistema
2. O script `start-backend-simple.sh` executava `gradle build` mas não `gradle compileKotlin`
3. A classe `ApiServer.kt` não estava sendo compilada

## ✅ Solução Implementada

### Novo Script: `start-backend-fixed.sh`

Este script:
- ✅ Detecta automaticamente onde o Gradle está instalado
- ✅ Tenta 3 locais diferentes: sistema, local, wrapper
- ✅ Executa `clean compileKotlin` para garantir compilação
- ✅ Cria JAR com dependências
- ✅ Inicia o servidor corretamente

---

## 🚀 Como Usar Agora

### 1. Executar o Backend

```bash
cd ~/Rpg.kt
./start-backend-fixed.sh
```

**Saída esperada:**
```
========================================
  OLD DRAGON 2 RPG - Backend API
  Modo Automático
========================================

✓ Usando Gradle local: /home/ubuntu/gradle-8.5/bin/gradle

Compilando projeto...
BUILD SUCCESSFUL in 22s

✅ Compilação concluída com sucesso!

Criando JAR executável...

Iniciando servidor API...
API disponível em: http://localhost:8080

Endpoints disponíveis:
  GET  /api/personagens
  POST /api/personagens
  ...
```

### 2. Testar a API (em outro terminal)

```bash
cd ~/Rpg.kt
./test-api.sh
```

Isso vai:
- Verificar se a API está rodando
- Criar um personagem de teste
- Listar monstros e itens
- Mostrar exemplos de uso

---

## 📝 Scripts Disponíveis

| Script | Descrição | Quando Usar |
|--------|-----------|-------------|
| `start-backend-fixed.sh` | ✅ **RECOMENDADO** - Detecta Gradle automaticamente | Sempre |
| `start-backend-simple.sh` | Usa `gradle` do PATH | Se gradle estiver no PATH |
| `start-backend.sh` | Usa wrapper (pode dar timeout) | Não recomendado |
| `test-api.sh` | Testa se a API está funcionando | Após iniciar backend |
| `fix-permissions.sh` | Corrige permissões de logs | Antes da primeira execução |

---

## 🔍 Verificações

### Verificar se Gradle foi encontrado

```bash
# Tentar no sistema
which gradle

# Tentar local
ls -la /home/ubuntu/gradle-8.5/bin/gradle

# Tentar wrapper
ls -la ./gradlew
```

### Verificar se a compilação funcionou

```bash
# Deve listar ApiServerKt.class
find build/classes/kotlin/main -name "*ApiServer*"
```

**Saída esperada:**
```
build/classes/kotlin/main/com/rpg/api/ApiServer.class
build/classes/kotlin/main/com/rpg/api/ApiServerKt.class
...
```

### Verificar se a API está rodando

```bash
curl http://localhost:8080
```

**Saída esperada:**
```
RPG API Server is running!
```

---

## 🐛 Troubleshooting

### Erro: "Gradle não encontrado"

**Solução 1: Usar Gradle local (já baixado)**
```bash
# O Gradle já foi baixado em /home/ubuntu/gradle-8.5
# O script deve detectar automaticamente
./start-backend-fixed.sh
```

**Solução 2: Adicionar ao PATH**
```bash
export PATH=$PATH:/home/ubuntu/gradle-8.5/bin
./start-backend-fixed.sh
```

**Solução 3: Instalar no sistema**
```bash
sudo apt install gradle
./start-backend-fixed.sh
```

### Erro: "Port 8080 already in use"

```bash
# Encontrar processo
lsof -i :8080

# Matar processo
kill -9 <PID>

# Ou usar outra porta (edite ApiServer.kt)
```

### Erro: "Permission denied"

```bash
# Dar permissão de execução
chmod +x start-backend-fixed.sh

# Corrigir permissões de logs
./fix-permissions.sh
```

---

## 📊 Fluxo Completo

```
1. Preparar ambiente (uma vez)
   └─> ./fix-permissions.sh

2. Iniciar backend
   └─> ./start-backend-fixed.sh
   
3. Testar API (outro terminal)
   └─> ./test-api.sh
   
4. Usar API
   └─> curl http://localhost:8080/api/...
   
5. Iniciar mobile (outro terminal)
   └─> cd mobile && npm start
```

---

## 🎮 Exemplos de Uso da API

### Criar Personagem

```bash
curl -X POST http://localhost:8080/api/personagens \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Aragorn",
    "raca": "humano",
    "classe": "guerreiro",
    "alinhamento": "leal_e_bom"
  }'
```

### Listar Personagens

```bash
curl http://localhost:8080/api/personagens
```

### Iniciar Combate

```bash
curl -X POST http://localhost:8080/api/combate/iniciar \
  -H "Content-Type: application/json" \
  -d '{
    "personagemNome": "Aragorn"
  }'
```

### Listar Monstros

```bash
curl "http://localhost:8080/api/monstros?nivelMin=1&nivelMax=5"
```

### Listar Itens

```bash
curl "http://localhost:8080/api/itens?tipo=ARMA"
```

---

## 📱 Configurar Mobile

### 1. Descobrir IP Local

```bash
# Linux
ip addr show | grep "inet " | grep -v 127.0.0.1

# Ou
hostname -I
```

### 2. Configurar API no Mobile

Se o mobile tiver um arquivo de configuração (ex: `config.ts`):

```typescript
export const API_URL = 'http://SEU_IP:8080/api';
// Exemplo: http://192.168.1.100:8080/api
```

### 3. Iniciar Mobile

```bash
cd mobile
npm install  # primeira vez
npm start
```

---

## ✅ Checklist Final

Antes de iniciar:

- [ ] Gradle está disponível (sistema, local ou wrapper)
- [ ] Permissões de logs corrigidas (`./fix-permissions.sh`)
- [ ] Porta 8080 está livre
- [ ] Java 11+ instalado (`java -version`)

Para executar:

- [ ] Backend iniciado (`./start-backend-fixed.sh`)
- [ ] API testada (`./test-api.sh` ou `curl`)
- [ ] Mobile configurado com IP correto
- [ ] Mobile iniciado (`npm start`)

---

## 🎉 Resultado

Agora você tem:

✅ Script que detecta Gradle automaticamente  
✅ Compilação garantida do ApiServer  
✅ Backend API funcionando  
✅ Script de testes  
✅ Documentação completa  

**Tudo pronto para usar!** 🚀

---

## 📞 Suporte

Se ainda tiver problemas:

1. Verifique os logs de compilação
2. Execute `./test-api.sh` para diagnóstico
3. Consulte `SOLUCAO_ERROS.md` para mais detalhes

---

**Última atualização:** 2025-10-20  
**Versão:** 2.0 (Correção Final)

