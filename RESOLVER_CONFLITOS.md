# 🔧 Como Resolver Conflitos de Merge

## ⚠️ Problema

Você está vendo conflitos de merge com arquivos do Gradle (.gradle/**). Isso acontece porque arquivos de build foram commitados acidentalmente.

## ✅ Solução Rápida (Recomendada)

Execute estes comandos no seu computador local:

```bash
cd ~/Rpg.kt

# 1. Abortar qualquer merge em andamento
git merge --abort

# 2. Fazer backup das suas alterações locais (se houver)
git stash

# 3. Forçar atualização do remoto (sobrescrever local)
git fetch origin
git reset --hard origin/main

# 4. Restaurar suas alterações (se fez backup)
git stash pop

# 5. Limpar arquivos do Gradle
./gradlew clean
rm -rf .gradle build

# 6. Executar o jogo
./setup-permissions.sh
./install.sh
```

## 🔄 Solução Alternativa

Se preferir manter suas alterações locais e sobrescrever o remoto:

```bash
cd ~/Rpg.kt

# 1. Abortar merge
git merge --abort

# 2. Limpar arquivos de build
./gradlew clean
rm -rf .gradle build

# 3. Adicionar ao gitignore
echo ".gradle/" >> .gitignore
echo "build/" >> .gitignore

# 4. Commit e force push
git add -A
git commit -m "Limpar arquivos de build e atualizar gitignore"
git push origin main --force
```

## 📝 Explicação dos Conflitos

Os conflitos estão ocorrendo em:
- `.gradle/**` - Arquivos de cache do Gradle
- `build/**` - Arquivos compilados

Estes arquivos **NÃO devem** estar no Git, pois:
- São gerados automaticamente
- Diferem entre máquinas
- Causam conflitos desnecessários

## ✨ Prevenção Futura

O arquivo `.gitignore` já está configurado para ignorar estes arquivos:

```gitignore
.gradle/
build/
*.class
*.jar
*.log
```

Se os conflitos persistirem, execute:

```bash
# Remover arquivos do Git mas manter localmente
git rm -r --cached .gradle build
git commit -m "Remover arquivos de build do Git"
git push origin main --force
```

## 🆘 Se Nada Funcionar

Solução drástica (clonar novamente):

```bash
# Fazer backup do seu trabalho
cp -r ~/Rpg.kt ~/Rpg.kt.backup

# Remover repositório local
rm -rf ~/Rpg.kt

# Clonar novamente
git clone https://github.com/SamuelMauli/Rpg.kt.git
cd Rpg.kt

# Configurar e executar
./setup-permissions.sh
./install.sh
```

## 🎯 Resumo Rápido

**Para atualizar com a versão do GitHub:**
```bash
git fetch origin
git reset --hard origin/main
./gradlew clean
./setup-permissions.sh
./install.sh
```

**Para forçar sua versão local no GitHub:**
```bash
./gradlew clean
rm -rf .gradle build
git add -A
git commit -m "Atualizar projeto"
git push origin main --force
```

---

**Após resolver, você poderá executar o jogo normalmente! 🎮**

