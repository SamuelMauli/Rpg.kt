# RPG.kt - Enhanced Edition 🎮

Um jogo de RPG completo baseado no sistema Old Dragon, desenvolvido em Kotlin (terminal) e React Native (mobile), com funcionalidades avançadas e interface visual 8-bits.

## 🚀 Novas Funcionalidades Implementadas

### 🎲 Sistema de Dados Avançado
- **Múltiplos métodos de rolagem**: 3d6, 4d6 (drop lowest), Point Buy, Standard Array
- **Animações visuais** de dados rolando
- **Modificadores automáticos** baseados em atributos
- **Histórico de rolagens** para transparência

### ⚔️ Sistema de Combate Melhorado
- **Interface visual aprimorada** com sprites 8-bits
- **Animações de combate** (ataque, defesa, dano, morte)
- **Sistema de turnos** com iniciativa
- **Efeitos visuais** para magias e habilidades especiais
- **Log detalhado** de todas as ações

### 🎒 Sistema de Inventário Avançado
- **Interface visual moderna** com grid de slots
- **Categorização automática** de itens
- **Sistema de equipamentos** com bônus de atributos
- **Peso e capacidade** realistas
- **Busca e filtros** para organização
- **Raridade de itens** com cores distintivas

### 🏰 Gerador de Dungeons Procedurais
- **Geração aleatória** de dungeons com algoritmos avançados
- **Tipos de salas**: Entrada, Monstros, Tesouros, Armadilhas, Chefe, Saída
- **Sistema de conectividade** garantindo acessibilidade
- **Mapa visual** com fog of war
- **Salvamento de progresso** automático

### 👤 Criação de Personagem Completa
- **Interface passo-a-passo** intuitiva
- **4 raças disponíveis**: Humano, Elfo, Anão, Halfling
- **4 classes disponíveis**: Guerreiro, Ladrão, Clérigo, Mago
- **Modificadores raciais** automáticos
- **Validação de atributos** em tempo real
- **Preview visual** do personagem

### 🎨 Assets Visuais 8-bits
- **Sprites de monstros**: Goblin, Orc, Skeleton, Zombie, Necromancer
- **Sprites de classes**: Warrior, Rogue, Cleric, Mage
- **Ícones de itens**: Armas, Armaduras, Poções, Pergaminhos
- **Animações fluidas** e efeitos visuais
- **Estilo pixel art** autêntico

## 📁 Estrutura do Projeto

```
Rpg.kt/
├── src/main/kotlin/com/rpg/          # Versão terminal (Kotlin)
│   ├── Main.kt
│   ├── character/
│   ├── combat/
│   └── core/
├── mobile/                           # Versão mobile (React Native)
│   ├── App.tsx
│   ├── src/
│   │   ├── components/              # Componentes reutilizáveis
│   │   │   ├── DiceRoller.tsx
│   │   │   ├── DungeonMap.tsx
│   │   │   └── SpriteSystem.tsx
│   │   ├── game/                    # Lógica do jogo
│   │   │   ├── character/
│   │   │   ├── dungeon/
│   │   │   └── inventory/
│   │   ├── screens/                 # Telas do jogo
│   │   │   ├── EnhancedCombatScreen.tsx
│   │   │   ├── EnhancedInventoryScreen.tsx
│   │   │   ├── EnhancedCharacterCreationScreen.tsx
│   │   │   └── DungeonExplorationScreen.tsx
│   │   ├── utils/                   # Utilitários
│   │   │   ├── DiceSystem.ts
│   │   │   ├── SaveSystem.ts
│   │   │   └── TestUtils.ts
│   │   └── types/
│   └── assets/sprites/              # Assets visuais 8-bits
└── README_ENHANCED.md
```

## 🛠️ Tecnologias Utilizadas

### Backend/Terminal
- **Kotlin** - Linguagem principal
- **Sistema Old Dragon** - Regras de RPG

### Mobile
- **React Native** - Framework mobile
- **TypeScript** - Tipagem estática
- **Animated API** - Animações nativas
- **AsyncStorage** - Persistência local

### Assets
- **Pixel Art 8-bits** - Estilo visual retrô
- **PNG com transparência** - Sprites otimizados

## 🎯 Funcionalidades Principais

### 1. Criação de Personagem
- Escolha de nome, raça e classe
- Distribuição de atributos com múltiplos métodos
- Cálculo automático de modificadores
- Preview visual em tempo real

### 2. Sistema de Combate
- Combate baseado em turnos
- Animações de ações (ataque, defesa, magia)
- Cálculo automático de dano e CA
- Sistema de iniciativa

### 3. Exploração de Dungeons
- Geração procedural de mapas
- Navegação com controles direcionais
- Descoberta de salas e tesouros
- Sistema de armadilhas e eventos

### 4. Gerenciamento de Inventário
- Interface visual com drag & drop
- Categorização automática
- Sistema de equipamentos
- Cálculo de peso e valor

### 5. Sistema de Progressão
- Ganho de experiência
- Aumento de nível
- Distribuição de pontos de atributo
- Novas habilidades

## 🧪 Sistema de Testes

O projeto inclui um sistema completo de testes automatizados:

### Testes Funcionais
- ✅ Sistema de dados
- ✅ Distribuição de atributos
- ✅ Gerador de dungeons
- ✅ Sistema de inventário

### Testes de Performance
- ⚡ Geração de atributos (1000x)
- ⚡ Rolagem de dados (10000x)
- ⚡ Geração de dungeons (100x)

### Execução dos Testes
Os testes são executados automaticamente na inicialização em modo de desenvolvimento.

## 🚀 Como Executar

### Versão Terminal (Kotlin)
```bash
cd Rpg.kt
./gradlew run
```

### Versão Mobile (React Native)
```bash
cd Rpg.kt/mobile
npm install
npx react-native run-android  # ou run-ios
```

## 🎮 Como Jogar

### 1. Criação de Personagem
1. Escolha um nome épico
2. Selecione uma raça (cada uma com bônus únicos)
3. Escolha uma classe (determina habilidades)
4. Distribua pontos de atributo
5. Confirme a criação

### 2. Exploração
1. Use os controles direcionais para navegar
2. Explore salas para encontrar tesouros
3. Enfrente monstros em combate
4. Colete itens e equipamentos
5. Avance para níveis mais profundos

### 3. Combate
1. Escolha suas ações (Atacar, Defender, Magia)
2. Role dados para determinar sucesso
3. Gerencie seus pontos de vida
4. Use itens estrategicamente

### 4. Progressão
1. Ganhe experiência derrotando inimigos
2. Suba de nível para ficar mais forte
3. Distribua pontos de atributo
4. Encontre equipamentos melhores

## 🔧 Configurações Avançadas

### Métodos de Geração de Atributos
- **3d6 Direto**: Clássico, mais desafiador
- **4d6 Drop Lowest**: Padrão, equilibrado
- **4d6 Reroll 1s**: Heróico, mais poderoso
- **Standard Array**: 15,14,13,12,10,8
- **Point Buy**: 27 pontos para distribuir

### Tipos de Dungeon
- **Nível 1-3**: Dungeons pequenas (5x5 a 8x8)
- **Nível 4-6**: Dungeons médias (10x10 a 12x12)
- **Nível 7+**: Dungeons grandes (15x15+)

## 🐛 Resolução de Problemas

### Problemas Comuns
1. **Sprites não carregam**: Verifique se os assets estão na pasta correta
2. **Testes falhando**: Execute `npm install` para dependências
3. **Performance lenta**: Ative modo de produção

### Logs de Debug
Os logs detalhados estão disponíveis no console do desenvolvedor.

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature
3. Implemente os testes
4. Faça commit das alterações
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo LICENSE para detalhes.

## 🎉 Agradecimentos

- **Sistema Old Dragon** - Regras de RPG
- **Comunidade React Native** - Framework mobile
- **Pixel Art Community** - Inspiração visual
- **RPG Community** - Feedback e sugestões

---

**Desenvolvido com ❤️ por Manus AI**

*Um jogo de RPG completo que combina a nostalgia dos jogos clássicos com tecnologia moderna!*
