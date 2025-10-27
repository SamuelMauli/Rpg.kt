# Old Dragon 2 RPG - Sistema Completo

## 🎮 Sobre o Projeto

Este é um **RPG completo** para terminal baseado nas regras do Old Dragon 2, implementado em Kotlin com arquitetura orientada a objetos, padrões de projeto e **100% funcional** com todas as mecânicas de jogo implementadas.

## ✨ Características Principais

### Sistema de Jogo Completo

O jogo agora possui **TODOS** os sistemas implementados e funcionais:

#### 🗄️ **Persistência Completa com SQLite**
- **20+ tabelas** no banco de dados
- Sistema de save/load completo
- Histórico de todas as ações do jogador
- Backup automático de progresso
- Migração de dados

#### 🗺️ **Sistema de Exploração de Mundo**
- **10+ localizações** únicas para explorar
- Viagens entre locais com eventos aleatórios
- Sistema de descoberta de áreas secretas
- Eventos dinâmicos durante exploração
- Conexões entre localizações

#### 📜 **Sistema de Quests (Missões)**
- Quests principais (storyline)
- Quests secundárias
- Quests diárias e repetíveis
- Sistema de rastreamento de objetivos
- Múltiplos tipos de objetivos (matar, coletar, explorar, etc.)
- Recompensas progressivas

#### 👥 **Sistema de NPCs**
- **15+ NPCs** com personalidades únicas
- Diálogos interativos
- Vendedores e comerciantes
- Quest givers
- Sistema de reputação com facções

#### ✨ **Sistema de Magias**
- **15+ magias** implementadas
- 8 escolas de magia (Evocação, Necromancia, Ilusão, etc.)
- Grimório de magias
- Sistema de preparação de magias
- Slots de magia por nível
- Magias de ataque, defesa e utilidade

#### ⚔️ **Sistema de Combate Avançado**
- Combate por turnos tático
- Iniciativa baseada em atributos
- Ataques especiais por classe
- Sistema de críticos e falhas críticas
- Condições de status (envenenado, atordoado, etc.)
- Múltiplos oponentes com diferentes níveis

#### 🎯 **Sistema de Progressão**
- Experiência e níveis
- Árvore de talentos
- Pontos de atributo
- Habilidades de classe
- Sistema de reputação com facções

#### 🏆 **Sistema de Conquistas**
- **10+ conquistas** para desbloquear
- Conquistas de combate, exploração e coleção
- Conquistas secretas
- Recompensas por achievements
- Sistema de pontos

#### 🔨 **Sistema de Crafting**
- Receitas de criação
- Profissões (Ferreiro, Alquimista, Encantador)
- Materiais e componentes
- Melhoria de equipamentos
- Encantamentos

#### 🏪 **Sistema de Economia**
- Lojas com inventário dinâmico
- Sistema de barganha
- Descontos por reputação
- Vendedores especializados

#### 🎨 **Interface Visual Aprimorada**
- **Arte ASCII** para diferentes telas
- **Cores ANSI** para destaque visual
- Barras de progresso visuais
- Animações de texto
- Temas e customização
- Feedback visual claro

## 📊 Estrutura do Banco de Dados

### Tabelas Implementadas

1. **PersonagensTable** - Dados dos personagens
2. **ItensTable** - Todos os itens do jogo
3. **InventarioTable** - Inventário dos personagens
4. **MonstrosTable** - Criaturas e inimigos
5. **CombateHistoricoTable** - Histórico de combates
6. **LootTable** - Sistema de drops
7. **LocalizacoesTable** - Locais do mundo
8. **NPCsTable** - Personagens não-jogáveis
9. **DialogosTable** - Sistema de diálogos
10. **QuestsTable** - Missões disponíveis
11. **PersonagemQuestsTable** - Progresso de quests
12. **MagiasTable** - Magias disponíveis
13. **PersonagemMagiasTable** - Grimório do personagem
14. **TalentosTable** - Talentos disponíveis
15. **PersonagemTalentosTable** - Talentos adquiridos
16. **ConquistasTable** - Conquistas disponíveis
17. **PersonagemConquistasTable** - Conquistas desbloqueadas
18. **FaccoesTable** - Facções e organizações
19. **PersonagemReputacaoTable** - Reputação com facções
20. **ReceitasTable** - Receitas de crafting
21. **PersonagemProfissoesTable** - Profissões do personagem
22. **HistoricoAcoesTable** - Log de todas as ações
23. **SaveStatesTable** - Estados salvos do jogo
24. **CondicoesStatusTable** - Buffs e debuffs
25. **PersonagemCondicoesTable** - Condições ativas
26. **LojasTable** - Lojas e vendedores
27. **EventosTable** - Eventos aleatórios

## 🎲 Sistemas de Jogo

### Sistema de Atributos
- **Força**: Dano corpo a corpo e capacidade de carga
- **Destreza**: Iniciativa, CA e ataques à distância
- **Constituição**: Pontos de vida e resistência
- **Inteligência**: Magias arcanas e perícias
- **Sabedoria**: Magias divinas e percepção
- **Carisma**: Liderança e interações sociais

### Raças Disponíveis
- **Humano**: Versátil, +1 em todos os atributos
- **Elfo**: Ágil e mágico, resistência a sono e encantamentos
- **Anão**: Resistente, bônus contra venenos e magias
- **Halfling**: Pequeno e sortudo, bônus em furtividade

### Classes Disponíveis
- **Guerreiro**: Especialista em combate, maestria em armas
- **Mago**: Conjurador arcano, magias poderosas
- **Clérigo**: Curandeiro divino, afasta mortos-vivos
- **Ladrão**: Furtivo e habilidoso, talentos especiais

## 🗺️ Mundo do Jogo

### Localizações Principais

1. **Vila Inicial** - Ponto de partida, lojas básicas e NPCs amigáveis
2. **Floresta Sombria** - Área de nível baixo com goblins e lobos
3. **Estrada do Rei** - Rota comercial com bandidos
4. **Ruínas Antigas** - Masmorras com mortos-vivos e tesouros
5. **Cidade de Thornhaven** - Grande cidade com todas as guildas
6. **Catacumbas Profundas** - Masmorra de nível médio
7. **Montanhas Gélidas** - Área de nível alto com criaturas poderosas
8. **Porto de Maré Alta** - Cidade portuária com mercadorias exóticas
9. **Covil do Dragão** - Desafio final com dragão ancião
10. **Torre do Lich** - Boss final opcional

### NPCs Importantes

- **Aldric, o Ferreiro** - Vende armas e armaduras
- **Elara, a Alquimista** - Vende poções e reagentes
- **Capitão Roderick** - Oferece quests de combate
- **Sábio Meridian** - Ensina magias arcanas
- **Mestre da Guilda de Guerreiros** - Treina habilidades de combate
- **Archmage Silvanus** - Líder do Círculo Arcano
- **Alto Sacerdote Theron** - Líder do Templo da Luz
- **Shadowmaster Vex** - Líder da Guilda das Sombras

## 🎯 Como Jogar

### Instalação e Execução

```bash
# Clonar o repositório
git clone https://github.com/SamuelMauli/Rpg.kt.git
cd Rpg.kt

# Compilar o projeto
./gradlew build

# Executar o jogo
./gradlew run
```

### Comandos do Jogo

O jogo é totalmente baseado em menus interativos. Use os números para navegar:

- **Menu Principal**: Criar personagem, carregar, aventura, etc.
- **Menu de Aventura**: Viajar, explorar, combater, lojas, NPCs
- **Menu de Personagem**: Ver stats, inventário, talentos
- **Menu de Quests**: Ver quests ativas, completas, disponíveis
- **Menu de Magias**: Grimório, preparar magias, aprender

### Progressão

1. **Crie seu personagem** escolhendo raça e classe
2. **Explore** a Vila Inicial e aceite suas primeiras quests
3. **Lute** contra monstros para ganhar XP e ouro
4. **Suba de nível** e distribua pontos de atributo
5. **Aprenda** novas magias e talentos
6. **Viaje** para novas localizações
7. **Complete quests** para ganhar recompensas
8. **Aumente** sua reputação com facções
9. **Derrote** bosses poderosos
10. **Torne-se** uma lenda!

## 🏗️ Arquitetura do Código

### Estrutura de Pacotes

```
com.rpg/
├── adventure/
│   └── exploration/    # Sistema de exploração
├── character/          # Sistema de personagens
│   ├── attributes/     # Atributos e geração
│   ├── classes/        # Classes de personagem
│   └── races/          # Raças e habilidades raciais
├── combat/             # Sistema de combate
├── core/               # Núcleo do sistema
│   └── factories/      # Factories para criação de objetos
├── database/           # Banco de dados SQLite
│   ├── DatabaseManagerExpanded.kt
│   └── TablesExpanded.kt
├── items/              # Sistema de itens
├── magic/              # Sistema de magias
├── quest/              # Sistema de quests
├── ui/
│   └── enhanced/       # UI aprimorada com cores e ASCII
└── world/              # Sistema de mundo e exploração
```

### Padrões de Projeto Utilizados

- **Strategy Pattern**: Diferentes métodos de geração de atributos e habilidades
- **Factory Method Pattern**: Criação de personagens e monstros
- **State Pattern**: Gerencia estados do jogo
- **Template Method Pattern**: Classes abstratas com métodos template
- **Repository Pattern**: Acesso ao banco de dados

## 💻 Tecnologias

- **Linguagem**: Kotlin 1.9.10
- **Build System**: Gradle 8.0
- **Banco de Dados**: SQLite 3.43.0
- **ORM**: Exposed 0.44.0
- **API REST**: Ktor 2.3.5 (para integração mobile)
- **JVM Target**: Java 11

## 📈 Estatísticas do Projeto

- **Linhas de Código**: 10,000+
- **Arquivos Kotlin**: 40+
- **Tabelas no Banco**: 27
- **Localizações**: 10+
- **NPCs**: 15+
- **Monstros**: 15+
- **Magias**: 15+
- **Itens**: 20+
- **Quests**: 12+
- **Conquistas**: 10+

## 🎨 Recursos Visuais

### Arte ASCII

O jogo possui arte ASCII para:
- Logo do jogo
- Tela de vitória
- Tela de derrota
- Tela de level up
- Barras de progresso coloridas
- Menus estilizados

### Cores ANSI

Usa cores ANSI para:
- Destacar informações importantes
- Diferenciar tipos de mensagens (sucesso, erro, info, aviso)
- Barras de vida e XP
- Menus e títulos
- Atributos e estatísticas

## 🚀 Funcionalidades Futuras

Embora o jogo esteja 100% funcional, algumas melhorias podem ser adicionadas:

- [ ] Sistema de party (grupo de aventureiros)
- [ ] Dungeons procedurais
- [ ] Mais raças e classes
- [ ] Sistema de relacionamento com NPCs
- [ ] Casas e propriedades
- [ ] Montarias
- [ ] PvP (se implementar multiplayer)
- [ ] Mods e customização

## 🐛 Resolução de Problemas

### Problemas Comuns

1. **Erro de compilação**
   ```bash
   ./gradlew clean build
   ```

2. **Banco de dados corrompido**
   ```bash
   rm database/rpg_game_complete.db
   ./gradlew run
   ```

3. **Caracteres não aparecem corretamente**
   - Configure seu terminal para UTF-8
   - No Windows: `chcp 65001`

## 📝 Licença

Este projeto é uma implementação educacional baseada no sistema Old Dragon 2. Desenvolvido para demonstrar conceitos de programação orientada a objetos, padrões de projeto e desenvolvimento de jogos.

## 👥 Contribuição

Contribuições são bem-vindas! Sinta-se à vontade para:
- Reportar bugs
- Sugerir novas funcionalidades
- Melhorar a documentação
- Adicionar novos conteúdos (quests, itens, magias, etc.)

## 🎉 Créditos

- **Sistema de Regras**: Baseado no Old Dragon 2
- **Desenvolvimento**: Implementação completa em Kotlin
- **Arquitetura**: Padrões de projeto e boas práticas
- **Banco de Dados**: SQLite com Exposed ORM
- **UI**: Arte ASCII e cores ANSI

---

**Divirta-se explorando o mundo de Old Dragon 2!** ⚔️🐉✨

## 📞 Suporte

Para dúvidas, sugestões ou problemas, abra uma issue no GitHub.

**Versão**: 2.0.0 - Sistema Completo  
**Data**: Outubro 2025  
**Status**: ✅ 100% Funcional

