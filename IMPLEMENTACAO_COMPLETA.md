# Implementação Completa do Sistema RPG

## 📋 Resumo das Implementações

Este documento descreve todas as implementações realizadas no sistema de RPG Old Dragon 2.

## 🗄️ Banco de Dados

### Tecnologia
- **SQLite** com **Exposed ORM** (Kotlin)
- Persistência completa de dados do jogo

### Tabelas Implementadas

1. **personagens** - Armazena dados dos personagens jogáveis
   - Atributos (Força, Destreza, Constituição, Inteligência, Sabedoria, Carisma)
   - Status (PV, PV máximo, nível, XP, dinheiro)
   - Informações básicas (nome, raça, classe)

2. **itens** - Catálogo de todos os itens do jogo
   - 14 itens pré-cadastrados
   - Tipos: ARMA, ARMADURA, ESCUDO, POCAO, ANEL, AMULETO
   - Propriedades: dano, bônus, valor, peso

3. **inventario** - Relaciona personagens com seus itens
   - Quantidade de cada item
   - Status de equipamento

4. **monstros** - Catálogo de oponentes
   - 11 monstros diferentes (níveis 1-10)
   - Estatísticas de combate
   - Recompensas (XP e ouro)

5. **combate_historico** - Registro de batalhas
   - Resultado (vitória/derrota/fuga)
   - Recompensas obtidas

6. **loot** - Sistema de drops de itens

## ⚔️ Sistema de Combate

### Mecânicas Implementadas

#### Iniciativa
- Baseada em Destreza e Sabedoria
- Rolagem de d20 para determinar ordem

#### Ataques
- **Base de Ataque (BAC/BAD)**: Calculado por classe e nível
- **Modificadores**: Força (corpo a corpo) e Destreza (distância)
- **Rolagem**: d20 + Base de Ataque + Modificadores vs CA do alvo

#### Críticos
- **Chance base**: 5% (natural 20)
- **Modificável** por equipamentos e habilidades
- **Multiplicador**: 2x o dano base

#### Esquiva
- **Chance base**: 5% + (Modificador de Destreza × 2)%
- Permite evitar ataques mesmo quando acertariam

#### Dano
- **Fórmula**: Dado de dano + Modificador de Força/Destreza + Bônus de equipamento
- **Tipos de dado**: 1d4, 1d6, 1d8, 1d10, 2d6, 2d8, 3d6, 4d8
- **Dano mínimo**: 1 (mesmo com modificadores negativos)

### Interface de Batalha

```
╔═══════════════════════════════════════════════════════════════╗
║                        BATALHA!                               ║
╚═══════════════════════════════════════════════════════════════╝

┌─────────────────────────────────┐
│         NOME DO HERÓI           │
│    Humano Guerreiro - Nível 3   │
├─────────────────────────────────┤
│ PV: 25/30                       │
│ [████████████████░░░░] 83%      │
│ CA: 15                          │
│ Ataque: +5                      │
└─────────────────────────────────┘

                ⚔️  VS  ⚔️

┌─────────────────────────────────┐
│             ORC                 │
│           Nível 3               │
├─────────────────────────────────┤
│ PV: 15                          │
│ [████████████████████] 100%     │
│ CA: 13                          │
│ Ataque: +3                      │
└─────────────────────────────────┘
```

## 👾 Oponentes

### 11 Monstros Implementados

#### Nível 1-2 (Fracos)
1. **Kobold** - 4 PV, CA 10, Dano 1d4, XP 25
2. **Goblin** - 5 PV, CA 11, Dano 1d6, XP 50
3. **Esqueleto** - 6 PV, CA 12, Dano 1d6, XP 50

#### Nível 3-5 (Médios)
4. **Orc** - 15 PV, CA 13, Dano 1d8, XP 150
5. **Hobgoblin** - 18 PV, CA 14, Dano 1d8+1, XP 200
6. **Gnoll** - 22 PV, CA 14, Dano 1d10, XP 250
7. **Ogro** - 30 PV, CA 13, Dano 2d6, XP 350

#### Nível 6-8 (Fortes)
8. **Troll** - 40 PV, CA 15, Dano 2d6+2, XP 500
9. **Wyvern** - 50 PV, CA 16, Dano 2d8, XP 700
10. **Quimera** - 60 PV, CA 17, Dano 3d6, XP 900

#### Nível 10 (Boss)
11. **Dragão Vermelho** - 100 PV, CA 20, Dano 4d8, XP 2000

## 🎒 Sistema de Itens

### 14 Itens Implementados

#### Armas (5)
- **Adaga**: 1d4, +0, 2 PO
- **Espada Curta**: 1d6, +0, 10 PO
- **Espada Longa**: 1d8, +0, 15 PO
- **Machado de Batalha**: 1d10, +0, 20 PO
- **Arco Longo**: 1d8, +0, 75 PO

#### Armaduras (3)
- **Armadura de Couro**: CA +2, 5 PO
- **Cota de Malha**: CA +5, 75 PO
- **Armadura de Placas**: CA +8, 400 PO

#### Escudo (1)
- **Escudo**: CA +1, 10 PO

#### Poções (3)
- **Poção de Cura Menor**: 1d8 PV, 50 PO
- **Poção de Cura**: 2d8 PV, 100 PO
- **Poção de Cura Maior**: 4d8 PV, 200 PO

#### Acessórios (2)
- **Anel de Proteção +1**: CA +1, 500 PO
- **Amuleto da Sorte**: JP +1, 300 PO

### Sistema de Loot

#### Geração Procedural
- Chance de drop baseada no nível do monstro:
  - Nível 1-2: 30%
  - Nível 3-5: 50%
  - Nível 6-8: 70%
  - Nível 9+: 90%

#### Qualidade dos Itens
- Itens mais poderosos de monstros mais fortes
- Chance adicional de 40% para poções

## 📈 Sistema de XP e Progressão

### Tabela de XP (Níveis 1-20)

| Nível | XP Necessário | Nível | XP Necessário |
|-------|---------------|-------|---------------|
| 1     | 0             | 11    | 750.000       |
| 2     | 2.000         | 12    | 1.000.000     |
| 3     | 4.000         | 13    | 1.250.000     |
| 4     | 8.000         | 14    | 1.500.000     |
| 5     | 16.000        | 15    | 1.750.000     |
| 6     | 32.000        | 16    | 2.000.000     |
| 7     | 64.000        | 17    | 2.250.000     |
| 8     | 125.000       | 18    | 2.500.000     |
| 9     | 250.000       | 19    | 2.750.000     |
| 10    | 500.000       | 20    | 3.000.000     |

### Ganho de XP
- Baseado no nível do monstro
- Ajustado pela diferença de nível entre personagem e monstro
- Multiplicadores:
  - Monstro 5+ níveis acima: 1.5x
  - Monstro 3-4 níveis acima: 1.25x
  - Mesma faixa de nível: 1.0x
  - Monstro mais fraco: 0.5x - 0.9x

### Progressão de Nível

#### Pontos de Vida
- **Níveis 1-10**: Rola dado de vida da classe + Modificador de Constituição
- **Níveis 11+**: Valor fixo (2 + Modificador de Constituição)
- **Mínimo**: 1 PV por nível

#### Pontos de Atributo
- **A cada 4 níveis**: Ganha 1 ponto de atributo
- Pode ser distribuído em qualquer atributo
- Aumentar Constituição também aumenta PV retroativamente

## 🎮 Interface de Distribuição de Pontos

```
╔═══════════════════════════════════════╗
║     DISTRIBUIÇÃO DE ATRIBUTOS         ║
╚═══════════════════════════════════════╝

Pontos disponíveis: 2

Atributos atuais:
1. Força:        15 (+2)
2. Destreza:     14 (+1)
3. Constituição: 13 (+1)
4. Inteligência: 10 (+0)
5. Sabedoria:    12 (+0)
6. Carisma:      11 (+0)

Digite o número do atributo para adicionar 1 ponto
Digite 0 para voltar
```

## 📊 Modificadores de Status

### Cálculo de Modificadores

| Atributo | Modificador |
|----------|-------------|
| 3        | -3          |
| 4-5      | -2          |
| 6-8      | -1          |
| 9-12     | 0           |
| 13-15    | +1          |
| 16-17    | +2          |
| 18       | +3          |

### Aplicação dos Modificadores

#### Força
- **Ataque corpo a corpo**: BAC + Mod. Força
- **Dano corpo a corpo**: Dano base + Mod. Força
- **Carga máxima**: Valor de Força

#### Destreza
- **Ataque à distância**: BAD + Mod. Destreza
- **Classe de Armadura**: CA base + Mod. Destreza
- **Esquiva**: 5% + (Mod. Destreza × 2)%
- **Iniciativa**: Teste baseado em Destreza

#### Constituição
- **Pontos de Vida**: PV por nível + Mod. Constituição
- **Resistência**: Afeta Jogadas de Proteção

#### Inteligência
- **Magias arcanas**: Determina magias extras para Magos

#### Sabedoria
- **Magias divinas**: Determina magias extras para Clérigos
- **Iniciativa**: Teste baseado em Sabedoria

#### Carisma
- **Reações**: Afeta interações sociais
- **Seguidores**: Determina número máximo

## 🎯 Características Técnicas

### Arquitetura
```
src/main/kotlin/com/rpg/
├── GameController.kt          # Controlador principal do jogo
├── MainKt.kt                  # Ponto de entrada
├── character/                 # Sistema de personagens
│   ├── Personagem.kt
│   ├── attributes/
│   ├── classes/
│   └── races/
├── combat/                    # Sistema de combate
│   ├── CombatSystem.kt       # Lógica de combate
│   └── GerenciadorDeCombate.kt
├── core/                      # Sistemas centrais
│   ├── ExperienceSystem.kt   # XP e progressão
│   └── factories/
├── database/                  # Banco de dados
│   ├── DatabaseManager.kt    # Gerenciador principal
│   └── Tables.kt             # Definição das tabelas
├── items/                     # Sistema de itens
│   └── ItemSystem.kt         # Itens, inventário, loot
└── ui/                        # Interface do usuário
    └── BattleUI.kt           # Interface de batalha
```

### Dependências
```kotlin
dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.xerial:sqlite-jdbc:3.43.0.0")
    implementation("org.jetbrains.exposed:exposed-core:0.44.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.44.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.44.0")
}
```

## 🚀 Como Executar

### Compilar
```bash
./gradlew build
```

### Executar
```bash
./gradlew run
```

### Gerar JAR
```bash
./gradlew jar
```

O JAR será gerado em: `build/libs/rpg-old-dragon-1.0.0.jar`

## 💾 Persistência de Dados

### Banco de Dados
- Arquivo: `rpg_game.db` (criado automaticamente)
- Localização: Diretório raiz do projeto

### Salvamento Automático
- Após cada combate
- Ao distribuir pontos de atributo
- Ao criar novo personagem
- Manualmente pelo menu

## 🎲 Mecânicas de Jogo

### Criação de Personagem
1. Escolher nome
2. Selecionar raça (Humano, Elfo, Anão, Halfling)
3. Selecionar classe (Guerreiro, Mago, Clérigo, Ladrão)
4. Atributos gerados automaticamente (4d6, descarta menor)
5. Escolher alinhamento
6. Dinheiro inicial: 100 PO

### Fluxo de Combate
1. Encontro com monstro (baseado no nível do personagem)
2. Teste de iniciativa
3. Rodadas de combate:
   - Atacar
   - Defender
   - Usar item
   - Fugir
4. Resolução:
   - Vitória: XP, ouro, itens
   - Derrota: Perde metade do ouro, ressurge em taverna

### Progressão
1. Ganhar XP em combates
2. Subir de nível automaticamente
3. Ganhar PV e pontos de atributo
4. Distribuir pontos manualmente
5. Comprar/encontrar equipamentos melhores

## 📝 Notas de Implementação

### Melhorias Realizadas
- ✅ Sistema de combate completamente funcional
- ✅ Banco de dados com persistência
- ✅ Interface visual aprimorada
- ✅ Sistema de XP balanceado
- ✅ Múltiplos oponentes variados
- ✅ Sistema de loot procedural
- ✅ Modificadores baseados em atributos
- ✅ Distribuição de pontos de atributo
- ✅ Salvamento e carregamento de personagens

### Possíveis Expansões Futuras
- Sistema de magias completo
- Mais raças e classes
- Dungeons com múltiplos andares
- Sistema de crafting
- Multiplayer
- Quests e missões
- NPCs e diálogos
- Sistema de reputação

## 📄 Licença

Este projeto é baseado nas regras do Old Dragon 2 RPG.

---

**Desenvolvido com ❤️ em Kotlin**

