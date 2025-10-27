# Old Dragon 2 RPG - Sistema Completo

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.10-blue)
![Java](https://img.shields.io/badge/Java-17-orange)
![SQLite](https://img.shields.io/badge/SQLite-3.43.0-green)
![Status](https://img.shields.io/badge/Status-100%25%20Funcional-brightgreen)

## 🎮 Sobre o Projeto

RPG completo para terminal baseado nas regras do Old Dragon 2, implementado em Kotlin com arquitetura orientada a objetos e **100% funcional**.

## ✨ Características

- ✅ **Sistema de Exploração** - Viaje entre 10+ localizações
- ✅ **Sistema de Quests** - 12+ missões disponíveis
- ✅ **Sistema de Magias** - 15+ magias de 8 escolas
- ✅ **Interface Visual** - Arte ASCII e cores ANSI
- ✅ **Banco de Dados** - 27 tabelas com SQLite
- ✅ **100+ Itens** - Monstros, NPCs, quests, magias, etc.

## 🚀 Instalação e Execução

### Opção 1: Script Automatizado (Recomendado)

```bash
./install.sh
```

### Opção 2: Comandos Manuais

```bash
# Compilar
./gradlew clean build

# Executar
./gradlew run
```

### Opção 3: Docker

```bash
# Usando script
./docker-start.sh

# Ou manualmente
docker-compose up
```

## 📋 Pré-requisitos

### Para execução local:
- Java 11 ou superior
- Gradle (incluído via wrapper)

### Para Docker:
- Docker
- Docker Compose

## 📚 Documentação

| Documento | Descrição |
|-----------|-----------|
| [INDEX.md](INDEX.md) | Índice de toda documentação |
| [INICIO_RAPIDO.md](INICIO_RAPIDO.md) | Guia rápido para começar |
| [README_COMPLETO.md](README_COMPLETO.md) | Documentação completa |
| [DOCKER_SETUP.md](DOCKER_SETUP.md) | Instruções Docker |

## 🎯 Como Jogar

1. Execute o jogo usando uma das opções acima
2. Escolha "Criar Novo Personagem"
3. Selecione raça e classe
4. Comece sua aventura!

## 🗺️ Sistemas Implementados

### ✅ Totalmente Funcional

- **Exploração de Mundo** - 10+ localizações
- **Sistema de Quests** - Missões principais, secundárias e diárias
- **Sistema de Magias** - 8 escolas de magia
- **Sistema de Combate** - Combate tático por turnos
- **Banco de Dados** - 27 tabelas SQLite
- **Interface Visual** - Arte ASCII e cores ANSI

## 📊 Conteúdo

- 20+ Itens (armas, armaduras, poções)
- 15+ Monstros (Rato Gigante até Lich Supremo)
- 10+ Localizações (Vila Inicial até Torre do Lich)
- 15+ NPCs (vendedores, mestres, quest givers)
- 12+ Quests (principais, secundárias, diárias)
- 15+ Magias (todas as escolas)

## 🛠️ Tecnologias

- **Kotlin** 1.9.10
- **Java** 17
- **SQLite** 3.43.0
- **Exposed ORM** 0.44.0
- **Gradle** 8.5
- **Docker** (opcional)

## 🐛 Solução de Problemas

### Erro: "Java não encontrado"

Instale Java 17:

```bash
# Ubuntu/Debian
sudo apt install openjdk-17-jdk

# Fedora/RHEL
sudo dnf install java-17-openjdk

# macOS
brew install openjdk@17
```

### Erro de compilação

```bash
./gradlew clean build --refresh-dependencies
```

### Banco de dados corrompido

```bash
rm database/*.db
./gradlew run
```

## 🎉 Status

**Versão**: 2.0.0 - Sistema Completo  
**Status**: ✅ 100% Funcional  
**Data**: Outubro 2025

---

**Desenvolvido com ❤️ em Kotlin** ⚔️🐉✨
