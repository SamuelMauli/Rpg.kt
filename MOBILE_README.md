# OLD DRAGON 2 RPG - Mobile & Backend

## 📱 Visão Geral

Este projeto contém um sistema RPG completo com:
- **Backend API REST** em Kotlin/Ktor
- **Frontend Mobile** em React Native/Expo
- **Banco de Dados** SQLite com persistência
- **Sistema de Combate** completo
- **Progressão de Personagens** com XP e níveis

---

## 🚀 Como Executar

### Pré-requisitos

#### Para o Backend (Kotlin)
- Java JDK 11 ou superior
- Gradle (incluído via wrapper)

#### Para o Mobile (React Native)
- Node.js 18 ou superior
- npm ou yarn
- Expo CLI

---

## 🖥️ Executando o Backend

### Opção 1: Script Automático

```bash
./start-backend.sh
```

### Opção 2: Manual

```bash
# Compilar
./gradlew build

# Executar
java -cp "build/libs/*:build/classes/kotlin/main" com.rpg.api.ApiServerKt
```

O backend estará disponível em: **http://localhost:8080**

---

## 📱 Executando o Mobile

### 1. Instalar Dependências

```bash
cd mobile
npm install
```

### 2. Iniciar Expo

```bash
npm start
```

### 3. Abrir no Dispositivo

- **Android**: Escaneie o QR code com o app Expo Go
- **iOS**: Escaneie o QR code com a câmera
- **Web**: Pressione `w` no terminal

---

## 🔌 API Endpoints

### Personagens

#### Criar Personagem
```http
POST /api/personagens
Content-Type: application/json

{
  "nome": "Aragorn",
  "raca": "humano",
  "classe": "guerreiro",
  "alinhamento": "leal_e_bom"
}
```

#### Listar Personagens
```http
GET /api/personagens
```

#### Obter Personagem
```http
GET /api/personagens/{nome}
```

### Combate

#### Iniciar Combate
```http
POST /api/combate/iniciar
Content-Type: application/json

{
  "personagemNome": "Aragorn"
}
```

### Atributos

#### Distribuir Ponto de Atributo
```http
POST /api/personagens/distribuir-ponto
Content-Type: application/json

{
  "personagemNome": "Aragorn",
  "atributo": "forca"
}
```

### Dados do Jogo

#### Listar Monstros
```http
GET /api/monstros?nivelMin=1&nivelMax=5
```

#### Listar Itens
```http
GET /api/itens?tipo=ARMA
```

---

## 📂 Estrutura do Projeto

```
Rpg.kt/
├── src/main/kotlin/com/rpg/
│   ├── api/
│   │   └── ApiServer.kt          # Backend API REST
│   ├── character/                # Sistema de personagens
│   ├── combat/                   # Sistema de combate
│   ├── core/                     # Sistemas centrais (XP, etc)
│   ├── database/                 # Banco de dados SQLite
│   ├── items/                    # Sistema de itens
│   └── ui/                       # Interface (console)
│
├── mobile/
│   ├── src/
│   │   ├── screens/              # Telas do app
│   │   ├── components/           # Componentes reutilizáveis
│   │   ├── contexts/             # Contextos React
│   │   ├── types/                # Tipos TypeScript
│   │   └── utils/                # Utilitários
│   ├── assets/                   # Imagens e recursos
│   ├── App.tsx                   # Componente principal
│   └── package.json              # Dependências
│
├── build.gradle.kts              # Configuração Gradle
├── start-backend.sh              # Script para iniciar backend
└── MOBILE_README.md              # Esta documentação
```

---

## 🎮 Funcionalidades Implementadas

### Backend API
- ✅ CRUD completo de personagens
- ✅ Sistema de combate automático
- ✅ Cálculo de XP e progressão
- ✅ Sistema de loot e itens
- ✅ Distribuição de pontos de atributo
- ✅ Persistência em banco de dados
- ✅ CORS habilitado para mobile

### Mobile App
- ✅ Tela de menu principal
- ✅ Criação de personagens
- ✅ Seleção de raça e classe
- ✅ Visualização de ficha
- ✅ Sistema de combate
- ✅ Inventário
- ✅ Exploração de dungeons
- ✅ Interface responsiva
- ✅ Animações e efeitos

---

## 🎨 Telas do Mobile

### 1. Menu Principal
- Novo Jogo
- Carregar Jogo
- Configurações

### 2. Criação de Personagem
- Escolha de nome
- Seleção de raça (Humano, Elfo, Anão, Halfling)
- Seleção de classe (Guerreiro, Mago, Clérigo, Ladrão)
- Visualização de atributos

### 3. Ficha do Personagem
- Atributos (For, Des, Con, Int, Sab, Car)
- PV atual/máximo
- Nível e XP
- Dinheiro
- Equipamentos

### 4. Combate
- Interface visual de batalha
- Ações (Atacar, Defender, Item, Fugir)
- Log de combate
- Animações de ataque

### 5. Inventário
- Lista de itens
- Equipar/Desequipar
- Usar poções
- Vender itens

### 6. Exploração
- Mapa de dungeon
- Encontros aleatórios
- Tesouros
- Armadilhas

---

## 🔧 Configuração

### Alterar Porta do Backend

Edite `src/main/kotlin/com/rpg/api/ApiServer.kt`:

```kotlin
fun start(port: Int = 8080) {
    // Altere 8080 para a porta desejada
}
```

### Alterar URL da API no Mobile

Edite `mobile/src/config/api.ts`:

```typescript
export const API_URL = 'http://SEU_IP:8080/api';
```

**Nota**: Para testar no celular, use o IP local da sua máquina (ex: `http://192.168.1.100:8080`)

---

## 🐛 Troubleshooting

### Backend não inicia

1. Verifique se a porta 8080 está livre:
```bash
lsof -i :8080
```

2. Compile novamente:
```bash
./gradlew clean build
```

### Mobile não conecta ao backend

1. Verifique se o backend está rodando
2. Use o IP local ao invés de `localhost`
3. Desabilite firewall temporariamente
4. Verifique se está na mesma rede

### Erro de compilação

1. Limpe o cache do Gradle:
```bash
./gradlew clean
rm -rf build/
```

2. Reinstale dependências do mobile:
```bash
cd mobile
rm -rf node_modules package-lock.json
npm install
```

---

## 📊 Tecnologias Utilizadas

### Backend
- **Kotlin** 1.9.10
- **Ktor** 2.3.5 (Framework web)
- **Exposed** 0.44.0 (ORM)
- **SQLite** 3.43.0 (Banco de dados)
- **Gson** (Serialização JSON)

### Mobile
- **React Native** 0.81.4
- **Expo** 54.0.10
- **TypeScript** 5.9.2
- **Axios** 1.7.9 (HTTP client)
- **React Navigation** 7.0.15

---

## 🎯 Próximos Passos

### Melhorias Sugeridas

1. **Autenticação**
   - Login de usuário
   - JWT tokens
   - Sessões persistentes

2. **Multiplayer**
   - WebSockets para combate em tempo real
   - Chat entre jogadores
   - Guildas

3. **Mais Conteúdo**
   - Mais raças e classes
   - Sistema de magias completo
   - Crafting de itens
   - Quests e missões

4. **Interface**
   - Mais animações
   - Sons e música
   - Imagens para monstros e itens
   - Mapas visuais

5. **Performance**
   - Cache de dados
   - Otimização de queries
   - Lazy loading

---

## 📝 Exemplos de Uso

### Criar e Usar Personagem via API

```bash
# 1. Criar personagem
curl -X POST http://localhost:8080/api/personagens \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Gandalf",
    "raca": "humano",
    "classe": "mago",
    "alinhamento": "leal_e_bom"
  }'

# 2. Iniciar combate
curl -X POST http://localhost:8080/api/combate/iniciar \
  -H "Content-Type: application/json" \
  -d '{
    "personagemNome": "Gandalf"
  }'

# 3. Ver personagem atualizado
curl http://localhost:8080/api/personagens/Gandalf
```

---

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

---

## 📄 Licença

Este projeto é baseado nas regras do Old Dragon 2 RPG.

---

## 👥 Autores

- **Backend & Sistema de Jogo**: Implementação completa em Kotlin
- **Mobile**: Interface React Native/Expo

---

## 📞 Suporte

Para problemas ou dúvidas:
1. Verifique a seção de Troubleshooting
2. Consulte a documentação da API
3. Abra uma issue no repositório

---

**Desenvolvido com ❤️ em Kotlin e React Native**

