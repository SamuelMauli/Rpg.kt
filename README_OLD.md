# Old Dragon 2 RPG - Terminal Adventure

Um RPG completo para terminal baseado nas regras do Old Dragon 2, implementado em Kotlin com arquitetura orientada a objetos e padrões de projeto.

## 📖 Sobre o Projeto

Este projeto implementa um RPG completo de fantasia medieval que roda no terminal, baseado no sistema de regras do Old Dragon 2. O jogo inclui criação de personagens, sistema de combate, exploração, uma aventura narrativa completa e muito mais.

### ✨ Características Principais

- **Sistema de Regras Completo**: Baseado no Old Dragon 2 com atributos, raças, classes e mecânicas fiéis ao original
- **Arquitetura Robusta**: Implementação OOP com padrões de projeto (Strategy, Factory, State, etc.)
- **Aventura Narrativa**: "O Segredo da Montanha Gelada" - uma aventura épica com múltiplas áreas e chefe final
- **Sistema de Combate**: Combate por turnos com iniciativa, ataques, dano e moral
- **Progressão de Personagem**: Sistema de experiência, subida de nível e desenvolvimento de habilidades
- **Interface Intuitiva**: Interface de terminal amigável com navegação por menus

## 🎮 Como Jogar

### Pré-requisitos

- Java 11 ou superior
- Kotlin 1.8 ou superior
- Terminal com suporte a UTF-8

### Compilação e Execução

1. **Clone ou extraia o projeto**
```bash
cd rpg-old-dragon
```

2. **Compile o projeto**
```bash
./gradlew build
```

3. **Execute o jogo**
```bash
./gradlew run
```

Ou execute diretamente:
```bash
kotlin -cp build/classes/kotlin/main com.rpg.MainKt
```

### Comandos Especiais

Durante o jogo, você pode usar os seguintes comandos a qualquer momento:

- `help` ou `ajuda` - Mostra a ajuda
- `status` - Mostra o status atual do jogo
- `clear` ou `limpar` - Limpa a tela
- `voltar` ou `back` - Volta ao estado anterior
- `reiniciar` ou `restart` - Reinicia o jogo
- `quit`, `sair` ou `exit` - Sai do jogo

## 🏗️ Arquitetura do Projeto

### Estrutura de Pacotes

```
com.rpg/
├── adventure/          # Sistema de aventuras narrativas
├── character/          # Sistema de personagens
│   ├── attributes/     # Atributos e geração
│   ├── classes/        # Classes de personagem
│   └── races/          # Raças e habilidades raciais
├── combat/             # Sistema de combate
├── core/               # Núcleo do sistema
│   ├── factories/      # Factories para criação de objetos
│   └── states/         # Estados do jogo (padrão State)
└── items/              # Sistema de itens e inventário
```

### Padrões de Projeto Utilizados

#### 1. **Strategy Pattern**
- `MetodoRolagemAtributos`: Diferentes métodos de geração de atributos
- `HabilidadeRacial` e `HabilidadeDeClasse`: Diferentes habilidades

#### 2. **Factory Method Pattern**
- `PersonagemFactory`: Criação de personagens completos
- `MonstroFactory`: Criação de monstros para combate

#### 3. **State Pattern**
- `GameStateManager`: Gerencia estados do jogo (Menu, Criação, Aventura, Combate, etc.)
- Estados específicos para cada fase do jogo

#### 4. **Template Method Pattern**
- `Raca` e `Classe`: Classes abstratas com métodos template

#### 5. **Observer Pattern**
- Sistema de eventos de combate e progressão

## 🎲 Sistema de Regras

### Atributos
- **Força**: Determina dano corpo a corpo e capacidade de carga
- **Destreza**: Afeta iniciativa, CA e ataques à distância
- **Constituição**: Define pontos de vida e resistência
- **Inteligência**: Influencia magias arcanas e perícias
- **Sabedoria**: Afeta magias divinas e percepção
- **Carisma**: Determina liderança e interações sociais

### Raças Disponíveis
- **Humano**: Versátil, +1 em todos os atributos
- **Elfo**: Ágil e mágico, resistência a sono e encantamentos
- **Anão**: Resistente, bônus contra venenos e magias
- **Halfling**: Pequeno e sortudo, bônus em furtividade

### Classes Disponíveis
- **Guerreiro**: Especialista em combate, maestria em armas
- **Ladrão**: Furtivo e habilidoso, talentos especiais
- **Clérigo**: Curandeiro divino, afasta mortos-vivos
- **Mago**: Conjurador arcano, magias poderosas

### Sistema de Combate
- **Iniciativa**: Baseada em Destreza + 1d6
- **Ataques**: 1d20 + modificadores vs CA do alvo
- **Dano**: Baseado na arma + modificador de Força
- **Moral**: Monstros podem fugir quando feridos

## 🗺️ A Aventura: "O Segredo da Montanha Gelada"

Uma aventura épica que leva os jogadores através de cinco áreas distintas:

### 1. **Entrada da Caverna**
- Primeira impressão da montanha assombrada
- Encontros iniciais com mortos-vivos
- Tutorial das mecânicas básicas

### 2. **Túneis Profundos**
- Rede de túneis com múltiplos caminhos
- Armadilhas antigas e inimigos variados
- Escolhas que afetam a progressão

### 3. **Câmara dos Ecos**
- Enigmas e runas místicas
- Sistema de selamento antigo
- Hub central com múltiplas saídas

### 4. **Sala dos Rituais**
- Atmosfera sinistra e opressiva
- Altares necromânticos e círculos mágicos
- Preparação para o confronto final

### 5. **Santuário do Necromante**
- Confronto épico com Malachar
- Múltiplas estratégias de combate
- Final cinematográfico e recompensas

## 💻 Detalhes Técnicos

### Tecnologias Utilizadas
- **Linguagem**: Kotlin 1.8
- **Build System**: Gradle 8.0
- **JVM Target**: Java 11
- **Paradigma**: Orientação a Objetos

### Características do Código
- **Clean Code**: Código limpo e bem documentado
- **SOLID Principles**: Aplicação dos princípios SOLID
- **Single Responsibility**: Cada classe tem uma responsabilidade específica
- **Extensibilidade**: Fácil adição de novas raças, classes e aventuras
- **Testabilidade**: Estrutura preparada para testes unitários

### Performance
- **Inicialização Rápida**: Carregamento instantâneo do jogo
- **Baixo Consumo**: Uso mínimo de memória e CPU
- **Responsividade**: Interface fluida e responsiva

## 🎯 Funcionalidades Implementadas

### ✅ Sistema de Personagem
- [x] Criação completa de personagens
- [x] 4 raças com habilidades únicas
- [x] 4 classes com progressão
- [x] Sistema de atributos e modificadores
- [x] Cálculo automático de CA, PV, etc.

### ✅ Sistema de Combate
- [x] Combate por turnos
- [x] Iniciativa e surpresa
- [x] Ataques corpo a corpo e à distância
- [x] Sistema de dano e cura
- [x] Moral de monstros

### ✅ Sistema de Progressão
- [x] Ganho de experiência
- [x] Subida de nível automática
- [x] Melhoria de habilidades
- [x] Aquisição de novas capacidades

### ✅ Sistema de Itens
- [x] Inventário completo
- [x] Armas, armaduras e itens diversos
- [x] Sistema de equipamentos
- [x] Poções e itens consumíveis

### ✅ Aventura Narrativa
- [x] História envolvente
- [x] 5 áreas distintas
- [x] Múltiplos tipos de eventos
- [x] Chefe final épico
- [x] Finais alternativos

### ✅ Interface e Usabilidade
- [x] Interface de terminal intuitiva
- [x] Navegação por menus
- [x] Comandos especiais
- [x] Sistema de ajuda
- [x] Feedback visual claro

## 🚀 Extensibilidade

O projeto foi desenvolvido com extensibilidade em mente:

### Adicionando Novas Raças
```kotlin
class NovaRaca : Raca() {
    override fun getNome() = "Nova Raça"
    override fun getModificadoresAtributos() = mapOf("forca" to 2)
    override fun getHabilidades() = listOf(NovaHabilidadeRacial())
}
```

### Adicionando Novas Classes
```kotlin
class NovaClasse : Classe() {
    override fun getNome() = "Nova Classe"
    override fun getDadoDeVida() = 8
    override fun getHabilidades() = listOf(NovaHabilidadeClasse())
}
```

### Adicionando Novas Aventuras
```kotlin
class NovaAventura : Aventura() {
    override fun getNome() = "Nova Aventura"
    override fun getAreas() = listOf(criarNovaArea())
}
```

## 🐛 Resolução de Problemas

### Problemas Comuns

1. **Erro de compilação**
   - Verifique se o Java 11+ está instalado
   - Execute `./gradlew clean build`

2. **Caracteres não aparecem corretamente**
   - Configure seu terminal para UTF-8
   - No Windows, use `chcp 65001`

3. **Jogo não inicia**
   - Verifique as permissões do arquivo
   - Execute `chmod +x gradlew` no Linux/Mac

## 📝 Licença

Este projeto é uma implementação educacional baseada no sistema Old Dragon 2. Desenvolvido para demonstrar conceitos de programação orientada a objetos e padrões de projeto.

## 👥 Contribuição

O projeto foi desenvolvido como uma demonstração completa de RPG em terminal. Sugestões e melhorias são bem-vindas!

## 🎉 Créditos

- **Sistema de Regras**: Baseado no Old Dragon 2
- **Desenvolvimento**: Implementação completa em Kotlin
- **Arquitetura**: Padrões de projeto e boas práticas
- **Aventura**: História original "O Segredo da Montanha Gelada"

---

**Divirta-se explorando as profundezas da Montanha Gelada e enfrentando o necromante Malachar!** ⚔️🏔️

