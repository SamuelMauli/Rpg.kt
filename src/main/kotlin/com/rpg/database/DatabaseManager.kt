package com.rpg.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Gerenciador expandido do banco de dados SQLite
 * Implementa todas as funcionalidades de persistência do jogo
 */
object DatabaseManager {
    private const val DB_PATH = "database/rpg_game_complete.db"
    private lateinit var database: Database
    
    fun init() {
        // Garantir que o diretório existe
        val dbFile = java.io.File(DB_PATH)
        dbFile.parentFile?.mkdirs()
        
        database = Database.connect("jdbc:sqlite:$DB_PATH", "org.sqlite.JDBC")
        
        transaction {
            // Criar todas as tabelas
            SchemaUtils.create(
                // Tabelas básicas
                PersonagensTable,
                ItensTable,
                InventarioTable,
                MonstrosTable,
                CombateHistoricoTable,
                LootTable,
                
                // Tabelas de mundo
                LocalizacoesTable,
                NPCsTable,
                DialogosTable,
                EventosTable,
                
                // Tabelas de quests
                QuestsTable,
                PersonagemQuestsTable,
                
                // Tabelas de magias
                MagiasTable,
                PersonagemMagiasTable,
                
                // Tabelas de progressão
                TalentosTable,
                PersonagemTalentosTable,
                ConquistasTable,
                PersonagemConquistasTable,
                
                // Tabelas de facções
                FaccoesTable,
                PersonagemReputacaoTable,
                
                // Tabelas de crafting
                ReceitasTable,
                PersonagemProfissoesTable,
                
                // Tabelas de sistema
                HistoricoAcoesTable,
                SaveStatesTable,
                CondicoesStatusTable,
                PersonagemCondicoesTable,
                LojasTable
            )
        }
        
        // Inicializar dados padrão
        inicializarDadosPadrao()
        println("✅ Banco de dados expandido inicializado com sucesso!")
    }
    
    private fun inicializarDadosPadrao() {
        transaction {
            // Verificar se já existem dados
            if (ItensTable.selectAll().count() == 0L) {
                inserirItensPadrao()
                println("📦 Itens padrão inseridos")
            }
            
            if (MonstrosTable.selectAll().count() == 0L) {
                inserirMonstrosPadrao()
                println("👹 Monstros padrão inseridos")
            }
            
            if (LocalizacoesTable.selectAll().count() == 0L) {
                inserirLocalizacoesPadrao()
                println("🗺️  Localizações padrão inseridas")
            }
            
            if (NPCsTable.selectAll().count() == 0L) {
                inserirNPCsPadrao()
                println("👥 NPCs padrão inseridos")
            }
            
            if (QuestsTable.selectAll().count() == 0L) {
                inserirQuestsPadrao()
                println("📜 Quests padrão inseridas")
            }
            
            if (MagiasTable.selectAll().count() == 0L) {
                inserirMagiasPadrao()
                println("✨ Magias padrão inseridas")
            }
            
            if (TalentosTable.selectAll().count() == 0L) {
                inserirTalentosPadrao()
                println("🎯 Talentos padrão inseridos")
            }
            
            if (ConquistasTable.selectAll().count() == 0L) {
                inserirConquistasPadrao()
                println("🏆 Conquistas padrão inseridas")
            }
            
            if (FaccoesTable.selectAll().count() == 0L) {
                inserirFaccoesPadrao()
                println("⚔️  Facções padrão inseridas")
            }
            
            if (ReceitasTable.selectAll().count() == 0L) {
                inserirReceitasPadrao()
                println("🔨 Receitas padrão inseridas")
            }
            
            if (CondicoesStatusTable.selectAll().count() == 0L) {
                inserirCondicoesPadrao()
                println("💫 Condições de status inseridas")
            }
            
            if (EventosTable.selectAll().count() == 0L) {
                inserirEventosPadrao()
                println("🎲 Eventos aleatórios inseridos")
            }
        }
    }
    
    // ==================== INSERÇÃO DE DADOS PADRÃO ====================
    
    private fun inserirItensPadrao() {
        // Armas Básicas
        ItensTable.insert {
            it[nome] = "Adaga"
            it[tipo] = "ARMA"
            it[dano] = "1d4"
            it[bonus] = 0
            it[valor] = 2
            it[peso] = 1
            it[descricao] = "Uma pequena adaga afiada"
            it[raridade] = "COMUM"
        }
        
        ItensTable.insert {
            it[nome] = "Espada Curta"
            it[tipo] = "ARMA"
            it[dano] = "1d6"
            it[bonus] = 0
            it[valor] = 10
            it[peso] = 3
            it[descricao] = "Uma espada curta básica"
            it[raridade] = "COMUM"
        }
        
        ItensTable.insert {
            it[nome] = "Espada Longa"
            it[tipo] = "ARMA"
            it[dano] = "1d8"
            it[bonus] = 0
            it[valor] = 15
            it[peso] = 4
            it[descricao] = "Uma espada longa de qualidade"
            it[raridade] = "COMUM"
        }
        
        ItensTable.insert {
            it[nome] = "Machado de Batalha"
            it[tipo] = "ARMA"
            it[dano] = "1d10"
            it[bonus] = 0
            it[valor] = 20
            it[peso] = 7
            it[descricao] = "Um machado pesado e poderoso"
            it[raridade] = "COMUM"
        }
        
        ItensTable.insert {
            it[nome] = "Arco Longo"
            it[tipo] = "ARMA"
            it[dano] = "1d8"
            it[bonus] = 0
            it[valor] = 75
            it[peso] = 3
            it[descricao] = "Arco para ataques à distância"
            it[raridade] = "COMUM"
        }
        
        // Armas Mágicas
        ItensTable.insert {
            it[nome] = "Espada Flamejante"
            it[tipo] = "ARMA"
            it[dano] = "1d8+1d6"
            it[bonus] = 2
            it[valor] = 500
            it[peso] = 4
            it[descricao] = "Espada envolta em chamas mágicas"
            it[raridade] = "RARO"
            it[requisitos] = "nivel:5"
        }
        
        // Armaduras
        ItensTable.insert {
            it[nome] = "Armadura de Couro"
            it[tipo] = "ARMADURA"
            it[dano] = ""
            it[bonus] = 2
            it[valor] = 5
            it[peso] = 15
            it[descricao] = "Armadura leve de couro"
            it[raridade] = "COMUM"
        }
        
        ItensTable.insert {
            it[nome] = "Cota de Malha"
            it[tipo] = "ARMADURA"
            it[dano] = ""
            it[bonus] = 5
            it[valor] = 75
            it[peso] = 40
            it[descricao] = "Armadura média de anéis entrelaçados"
            it[raridade] = "COMUM"
        }
        
        ItensTable.insert {
            it[nome] = "Armadura de Placas"
            it[tipo] = "ARMADURA"
            it[dano] = ""
            it[bonus] = 8
            it[valor] = 400
            it[peso] = 50
            it[descricao] = "Armadura pesada de placas de metal"
            it[raridade] = "INCOMUM"
        }
        
        ItensTable.insert {
            it[nome] = "Escudo"
            it[tipo] = "ESCUDO"
            it[dano] = ""
            it[bonus] = 1
            it[valor] = 10
            it[peso] = 10
            it[descricao] = "Escudo de madeira reforçado"
            it[raridade] = "COMUM"
        }
        
        // Poções
        ItensTable.insert {
            it[nome] = "Poção de Cura Menor"
            it[tipo] = "POCAO"
            it[dano] = "1d8"
            it[bonus] = 0
            it[valor] = 50
            it[peso] = 1
            it[descricao] = "Restaura 1d8 pontos de vida"
            it[raridade] = "COMUM"
        }
        
        ItensTable.insert {
            it[nome] = "Poção de Cura"
            it[tipo] = "POCAO"
            it[dano] = "2d8"
            it[bonus] = 0
            it[valor] = 100
            it[peso] = 1
            it[descricao] = "Restaura 2d8 pontos de vida"
            it[raridade] = "COMUM"
        }
        
        ItensTable.insert {
            it[nome] = "Poção de Cura Maior"
            it[tipo] = "POCAO"
            it[dano] = "4d8"
            it[bonus] = 0
            it[valor] = 200
            it[peso] = 1
            it[descricao] = "Restaura 4d8 pontos de vida"
            it[raridade] = "INCOMUM"
        }
        
        ItensTable.insert {
            it[nome] = "Poção de Força"
            it[tipo] = "POCAO"
            it[dano] = ""
            it[bonus] = 2
            it[valor] = 150
            it[peso] = 1
            it[descricao] = "Aumenta Força em +2 por 10 turnos"
            it[raridade] = "INCOMUM"
        }
        
        // Itens Mágicos
        ItensTable.insert {
            it[nome] = "Anel de Proteção +1"
            it[tipo] = "ANEL"
            it[dano] = ""
            it[bonus] = 1
            it[valor] = 500
            it[peso] = 0
            it[descricao] = "Aumenta CA em +1"
            it[raridade] = "RARO"
        }
        
        ItensTable.insert {
            it[nome] = "Amuleto da Sorte"
            it[tipo] = "AMULETO"
            it[dano] = ""
            it[bonus] = 1
            it[valor] = 300
            it[peso] = 0
            it[descricao] = "Concede +1 em todas as jogadas de proteção"
            it[raridade] = "RARO"
        }
        
        // Materiais de Crafting
        ItensTable.insert {
            it[nome] = "Minério de Ferro"
            it[tipo] = "MATERIAL"
            it[dano] = ""
            it[bonus] = 0
            it[valor] = 5
            it[peso] = 2
            it[descricao] = "Minério bruto de ferro"
            it[raridade] = "COMUM"
        }
        
        ItensTable.insert {
            it[nome] = "Couro Curtido"
            it[tipo] = "MATERIAL"
            it[dano] = ""
            it[bonus] = 0
            it[valor] = 3
            it[peso] = 1
            it[descricao] = "Couro tratado e pronto para uso"
            it[raridade] = "COMUM"
        }
        
        ItensTable.insert {
            it[nome] = "Erva Medicinal"
            it[tipo] = "MATERIAL"
            it[dano] = ""
            it[bonus] = 0
            it[valor] = 10
            it[peso] = 0
            it[descricao] = "Erva rara com propriedades curativas"
            it[raridade] = "INCOMUM"
        }
    }
    
    private fun inserirMonstrosPadrao() {
        // Monstros Nível 1-2 (Fracos)
        MonstrosTable.insert {
            it[nome] = "Rato Gigante"
            it[nivel] = 1
            it[pontosVida] = 3
            it[ca] = 10
            it[baseAtaque] = 0
            it[dano] = "1d3"
            it[xpRecompensa] = 10
            it[ouroMin] = 0
            it[ouroMax] = 2
            it[descricao] = "Um rato do tamanho de um cão pequeno"
            it[tipo] = "COMUM"
        }
        
        MonstrosTable.insert {
            it[nome] = "Goblin"
            it[nivel] = 1
            it[pontosVida] = 5
            it[ca] = 11
            it[baseAtaque] = 1
            it[dano] = "1d6"
            it[xpRecompensa] = 50
            it[ouroMin] = 1
            it[ouroMax] = 10
            it[descricao] = "Pequena criatura verde e maliciosa"
            it[tipo] = "COMUM"
        }
        
        MonstrosTable.insert {
            it[nome] = "Kobold"
            it[nivel] = 1
            it[pontosVida] = 4
            it[ca] = 10
            it[baseAtaque] = 0
            it[dano] = "1d4"
            it[xpRecompensa] = 25
            it[ouroMin] = 1
            it[ouroMax] = 5
            it[descricao] = "Criatura reptiliana covarde"
            it[tipo] = "COMUM"
        }
        
        MonstrosTable.insert {
            it[nome] = "Esqueleto"
            it[nivel] = 1
            it[pontosVida] = 6
            it[ca] = 12
            it[baseAtaque] = 1
            it[dano] = "1d6"
            it[xpRecompensa] = 50
            it[ouroMin] = 0
            it[ouroMax] = 5
            it[descricao] = "Morto-vivo animado por magia negra"
            it[tipo] = "COMUM"
        }
        
        MonstrosTable.insert {
            it[nome] = "Lobo"
            it[nivel] = 2
            it[pontosVida] = 10
            it[ca] = 12
            it[baseAtaque] = 2
            it[dano] = "1d6+1"
            it[xpRecompensa] = 75
            it[ouroMin] = 0
            it[ouroMax] = 3
            it[descricao] = "Lobo selvagem e faminto"
            it[tipo] = "COMUM"
        }
        
        // Monstros Nível 3-5 (Médios)
        MonstrosTable.insert {
            it[nome] = "Orc"
            it[nivel] = 3
            it[pontosVida] = 15
            it[ca] = 13
            it[baseAtaque] = 3
            it[dano] = "1d8"
            it[xpRecompensa] = 150
            it[ouroMin] = 5
            it[ouroMax] = 30
            it[descricao] = "Guerreiro brutal e selvagem"
            it[tipo] = "COMUM"
        }
        
        MonstrosTable.insert {
            it[nome] = "Hobgoblin"
            it[nivel] = 3
            it[pontosVida] = 18
            it[ca] = 14
            it[baseAtaque] = 3
            it[dano] = "1d8+1"
            it[xpRecompensa] = 200
            it[ouroMin] = 10
            it[ouroMax] = 40
            it[descricao] = "Goblin maior e mais disciplinado"
            it[tipo] = "COMUM"
        }
        
        MonstrosTable.insert {
            it[nome] = "Gnoll"
            it[nivel] = 4
            it[pontosVida] = 22
            it[ca] = 14
            it[baseAtaque] = 4
            it[dano] = "1d10"
            it[xpRecompensa] = 250
            it[ouroMin] = 10
            it[ouroMax] = 50
            it[descricao] = "Criatura hiena humanóide feroz"
            it[tipo] = "COMUM"
        }
        
        MonstrosTable.insert {
            it[nome] = "Ogro"
            it[nivel] = 5
            it[pontosVida] = 30
            it[ca] = 13
            it[baseAtaque] = 5
            it[dano] = "2d6"
            it[xpRecompensa] = 350
            it[ouroMin] = 20
            it[ouroMax] = 100
            it[descricao] = "Gigante estúpido mas muito forte"
            it[tipo] = "ELITE"
        }
        
        // Monstros Nível 6-8 (Fortes)
        MonstrosTable.insert {
            it[nome] = "Troll"
            it[nivel] = 6
            it[pontosVida] = 40
            it[ca] = 15
            it[baseAtaque] = 6
            it[dano] = "2d6+2"
            it[xpRecompensa] = 500
            it[ouroMin] = 30
            it[ouroMax] = 150
            it[descricao] = "Criatura regenerativa e perigosa"
            it[tipo] = "ELITE"
        }
        
        MonstrosTable.insert {
            it[nome] = "Wyvern"
            it[nivel] = 7
            it[pontosVida] = 50
            it[ca] = 16
            it[baseAtaque] = 7
            it[dano] = "2d8"
            it[xpRecompensa] = 700
            it[ouroMin] = 50
            it[ouroMax] = 200
            it[descricao] = "Dragão menor com cauda venenosa"
            it[tipo] = "ELITE"
        }
        
        MonstrosTable.insert {
            it[nome] = "Quimera"
            it[nivel] = 8
            it[pontosVida] = 60
            it[ca] = 17
            it[baseAtaque] = 8
            it[dano] = "3d6"
            it[xpRecompensa] = 900
            it[ouroMin] = 100
            it[ouroMax] = 300
            it[descricao] = "Besta híbrida com múltiplas cabeças"
            it[tipo] = "ELITE"
        }
        
        // Bosses
        MonstrosTable.insert {
            it[nome] = "Dragão Vermelho Jovem"
            it[nivel] = 10
            it[pontosVida] = 100
            it[ca] = 18
            it[baseAtaque] = 10
            it[dano] = "3d8"
            it[xpRecompensa] = 1500
            it[ouroMin] = 300
            it[ouroMax] = 800
            it[descricao] = "Dragão jovem de escamas vermelhas"
            it[tipo] = "BOSS"
        }
        
        MonstrosTable.insert {
            it[nome] = "Dragão Vermelho Ancião"
            it[nivel] = 15
            it[pontosVida] = 200
            it[ca] = 22
            it[baseAtaque] = 15
            it[dano] = "6d8"
            it[xpRecompensa] = 5000
            it[ouroMin] = 1000
            it[ouroMax] = 3000
            it[descricao] = "Dragão antigo de escamas vermelhas, terror dos céus"
            it[tipo] = "LENDARIO"
        }
        
        MonstrosTable.insert {
            it[nome] = "Lich Supremo"
            it[nivel] = 18
            it[pontosVida] = 150
            it[ca] = 20
            it[baseAtaque] = 12
            it[dano] = "4d10"
            it[xpRecompensa] = 8000
            it[ouroMin] = 2000
            it[ouroMax] = 5000
            it[descricao] = "Mago morto-vivo de poder inimaginável"
            it[tipo] = "LENDARIO"
        }
    }
    
    private fun inserirLocalizacoesPadrao() {
        // Localizações Iniciais
        LocalizacoesTable.insert {
            it[nome] = "Vila Inicial"
            it[descricao] = "Uma pequena vila pacífica cercada por campos verdejantes. Aqui você pode encontrar comerciantes, descansar na taverna e aceitar missões dos moradores."
            it[tipo] = "VILA"
            it[nivelMinimo] = 1
            it[nivelMaximo] = 3
            it[monstrosDisponiveis] = "[]"
            it[npcsDisponiveis] = "[1,2,3,4,5]"
            it[itensDisponiveis] = "[1,2,3,4,5,7,8,11,12]"
            it[conexoes] = "[\"Floresta Sombria\",\"Estrada do Rei\"]"
            it[descoberta] = true
        }
        
        LocalizacoesTable.insert {
            it[nome] = "Floresta Sombria"
            it[descricao] = "Uma floresta densa onde a luz do sol mal penetra. Sons estranhos ecoam entre as árvores antigas."
            it[tipo] = "FLORESTA"
            it[nivelMinimo] = 1
            it[nivelMaximo] = 5
            it[monstrosDisponiveis] = "[1,2,3,5]" // Goblin, Kobold, Esqueleto, Lobo
            it[npcsDisponiveis] = "[]"
            it[itensDisponiveis] = "[17,18]"
            it[conexoes] = "[\"Vila Inicial\",\"Ruínas Antigas\"]"
            it[descoberta] = false
        }
        
        LocalizacoesTable.insert {
            it[nome] = "Estrada do Rei"
            it[descricao] = "A principal estrada comercial que conecta as grandes cidades do reino. Mercadores viajam por aqui, mas bandidos também."
            it[tipo] = "ESTRADA"
            it[nivelMinimo] = 2
            it[nivelMaximo] = 6
            it[monstrosDisponiveis] = "[2,6,7]" // Goblin, Orc, Hobgoblin
            it[npcsDisponiveis] = "[6]"
            it[itensDisponiveis] = "[]"
            it[conexoes] = "[\"Vila Inicial\",\"Cidade de Thornhaven\"]"
            it[descoberta] = false
        }
        
        LocalizacoesTable.insert {
            it[nome] = "Ruínas Antigas"
            it[descricao] = "Restos de uma civilização perdida. Magias antigas ainda ressoam nestas pedras milenares."
            it[tipo] = "RUINAS"
            it[nivelMinimo] = 3
            it[nivelMaximo] = 7
            it[monstrosDisponiveis] = "[4,7,8]" // Esqueleto, Hobgoblin, Gnoll
            it[npcsDisponiveis] = "[]"
            it[itensDisponiveis] = "[6,15,16]"
            it[conexoes] = "[\"Floresta Sombria\",\"Catacumbas Profundas\"]"
            it[descoberta] = false
        }
        
        LocalizacoesTable.insert {
            it[nome] = "Cidade de Thornhaven"
            it[descricao] = "Uma próspera cidade murada, centro de comércio e cultura. Aqui você encontra de tudo: lojas, guildas, tavernas e muito mais."
            it[tipo] = "CIDADE"
            it[nivelMinimo] = 1
            it[nivelMaximo] = 20
            it[monstrosDisponiveis] = "[]"
            it[npcsDisponiveis] = "[7,8,9,10,11,12]"
            it[itensDisponiveis] = "[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16]"
            it[conexoes] = "[\"Estrada do Rei\",\"Montanhas Gélidas\",\"Porto de Maré Alta\"]"
            it[descoberta] = false
        }
        
        LocalizacoesTable.insert {
            it[nome] = "Catacumbas Profundas"
            it[descricao] = "Um labirinto subterrâneo de túmulos e passagens. O ar é pesado com o cheiro de morte e magia negra."
            it[tipo] = "MASMORRA"
            it[nivelMinimo] = 5
            it[nivelMaximo] = 10
            it[monstrosDisponiveis] = "[4,9,10]" // Esqueleto, Ogro, Troll
            it[npcsDisponiveis] = "[]"
            it[itensDisponiveis] = "[6,14,15,16]"
            it[conexoes] = "[\"Ruínas Antigas\"]"
            it[descoberta] = false
        }
        
        LocalizacoesTable.insert {
            it[nome] = "Montanhas Gélidas"
            it[descricao] = "Picos nevados que tocam as nuvens. O frio é intenso e criaturas perigosas habitam estas alturas."
            it[tipo] = "MONTANHA"
            it[nivelMinimo] = 7
            it[nivelMaximo] = 12
            it[monstrosDisponiveis] = "[9,10,11,12]" // Ogro, Troll, Wyvern, Quimera
            it[npcsDisponiveis] = "[]"
            it[itensDisponiveis] = "[17,18,19]"
            it[conexoes] = "[\"Cidade de Thornhaven\",\"Covil do Dragão\"]"
            it[descoberta] = false
        }
        
        LocalizacoesTable.insert {
            it[nome] = "Porto de Maré Alta"
            it[descricao] = "Uma cidade portuária movimentada. Navios de todas as nações atracam aqui, trazendo mercadorias exóticas e aventureiros."
            it[tipo] = "CIDADE"
            it[nivelMinimo] = 3
            it[nivelMaximo] = 15
            it[monstrosDisponiveis] = "[]"
            it[npcsDisponiveis] = "[13,14,15]"
            it[itensDisponiveis] = "[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16]"
            it[conexoes] = "[\"Cidade de Thornhaven\",\"Ilha dos Piratas\"]"
            it[descoberta] = false
        }
        
        LocalizacoesTable.insert {
            it[nome] = "Covil do Dragão"
            it[descricao] = "Uma caverna massiva no coração da montanha. Tesouros incontáveis brilham na escuridão, guardados por um dragão ancestral."
            it[tipo] = "COVIL"
            it[nivelMinimo] = 10
            it[nivelMaximo] = 20
            it[monstrosDisponiveis] = "[13,14]" // Dragões
            it[npcsDisponiveis] = "[]"
            it[itensDisponiveis] = "[6,14,15,16]"
            it[conexoes] = "[\"Montanhas Gélidas\"]"
            it[descoberta] = false
        }
        
        LocalizacoesTable.insert {
            it[nome] = "Torre do Lich"
            it[descricao] = "Uma torre negra que desafia as leis da natureza. Energia necromântica pulsa em suas paredes."
            it[tipo] = "TORRE"
            it[nivelMinimo] = 15
            it[nivelMaximo] = 20
            it[monstrosDisponiveis] = "[15]" // Lich
            it[npcsDisponiveis] = "[]"
            it[itensDisponiveis] = "[6,14,15,16]"
            it[conexoes] = "[\"Pântano Maldito\"]"
            it[descoberta] = false
        }
    }
    
    private fun inserirNPCsPadrao() {
        // NPCs da Vila Inicial
        NPCsTable.insert {
            it[nome] = "Aldric, o Ferreiro"
            it[tipo] = "VENDEDOR"
            it[localizacao] = "Vila Inicial"
            it[dialogoInicial] = "Bem-vindo à minha forja! Tenho as melhores armas e armaduras da região."
            it[dialogoRepeticao] = "Precisa de equipamento novo, aventureiro?"
            it[vendedor] = true
            it[itensVenda] = "[1,2,3,4,5,7,8,10]"
            it[questsDisponiveis] = "[1]"
            it[reputacaoNecessaria] = 0
        }
        
        NPCsTable.insert {
            it[nome] = "Elara, a Alquimista"
            it[tipo] = "VENDEDOR"
            it[localizacao] = "Vila Inicial"
            it[dialogoInicial] = "Minhas poções são as mais eficazes que você encontrará. Feitas com ingredientes raros!"
            it[dialogoRepeticao] = "Precisa de poções para sua jornada?"
            it[vendedor] = true
            it[itensVenda] = "[11,12,13,14,19]"
            it[questsDisponiveis] = "[2]"
            it[reputacaoNecessaria] = 0
        }
        
        NPCsTable.insert {
            it[nome] = "Capitão Roderick"
            it[tipo] = "QUEST_GIVER"
            it[localizacao] = "Vila Inicial"
            it[dialogoInicial] = "Aventureiro! A vila precisa de sua ajuda. Goblins têm atacado nossos fazendeiros."
            it[dialogoRepeticao] = "Tem mais alguma missão para mim, Capitão?"
            it[vendedor] = false
            it[questsDisponiveis] = "[3,4]"
            it[reputacaoNecessaria] = 0
            it[faccao] = "Guarda da Vila"
        }
        
        NPCsTable.insert {
            it[nome] = "Sábio Meridian"
            it[tipo] = "MESTRE"
            it[localizacao] = "Vila Inicial"
            it[dialogoInicial] = "Ah, um jovem aventureiro! Posso ensinar-lhe os segredos das artes arcanas... por um preço."
            it[dialogoRepeticao] = "Pronto para aprender mais magias?"
            it[vendedor] = false
            it[questsDisponiveis] = "[5]"
            it[reputacaoNecessaria] = 0
        }
        
        NPCsTable.insert {
            it[nome] = "Innkeeper Marta"
            it[tipo] = "VENDEDOR"
            it[localizacao] = "Vila Inicial"
            it[dialogoInicial] = "Bem-vindo à Taverna do Dragão Dourado! Temos comida, bebida e quartos limpos."
            it[dialogoRepeticao] = "Precisa descansar?"
            it[vendedor] = true
            it[itensVenda] = "[11,12]"
            it[reputacaoNecessaria] = 0
        }
        
        // NPCs da Estrada
        NPCsTable.insert {
            it[nome] = "Mercador Viajante"
            it[tipo] = "VENDEDOR"
            it[localizacao] = "Estrada do Rei"
            it[dialogoInicial] = "Olá, viajante! Tenho mercadorias exóticas de terras distantes!"
            it[dialogoRepeticao] = "Quer ver minha seleção especial?"
            it[vendedor] = true
            it[itensVenda] = "[6,14,15,16]"
            it[reputacaoNecessaria] = 0
        }
        
        // NPCs de Thornhaven
        NPCsTable.insert {
            it[nome] = "Mestre da Guilda de Guerreiros"
            it[tipo] = "MESTRE"
            it[localizacao] = "Cidade de Thornhaven"
            it[dialogoInicial] = "Quer se tornar um verdadeiro guerreiro? Posso treiná-lo nas artes marciais."
            it[dialogoRepeticao] = "Pronto para mais treinamento?"
            it[vendedor] = false
            it[questsDisponiveis] = "[6]"
            it[reputacaoNecessaria] = 0
            it[faccao] = "Guilda de Guerreiros"
        }
        
        NPCsTable.insert {
            it[nome] = "Archmage Silvanus"
            it[tipo] = "MESTRE"
            it[localizacao] = "Cidade de Thornhaven"
            it[dialogoInicial] = "A magia é uma arte que requer dedicação e estudo. Posso guiá-lo neste caminho."
            it[dialogoRepeticao] = "Busca mais conhecimento arcano?"
            it[vendedor] = false
            it[questsDisponiveis] = "[7]"
            it[reputacaoNecessaria] = 100
            it[faccao] = "Círculo Arcano"
        }
        
        NPCsTable.insert {
            it[nome] = "Alto Sacerdote Theron"
            it[tipo] = "MESTRE"
            it[localizacao] = "Cidade de Thornhaven"
            it[dialogoInicial] = "A luz divina guia aqueles que têm fé. Você busca as bênçãos dos deuses?"
            it[dialogoRepeticao] = "Precisa de cura ou bênçãos?"
            it[vendedor] = false
            it[questsDisponiveis] = "[8]"
            it[reputacaoNecessaria] = 0
            it[faccao] = "Templo da Luz"
        }
        
        NPCsTable.insert {
            it[nome] = "Shadowmaster Vex"
            it[tipo] = "MESTRE"
            it[localizacao] = "Cidade de Thornhaven"
            it[dialogoInicial] = "Psiu... você parece alguém que sabe guardar segredos. A Guilda das Sombras pode ter trabalho para você."
            it[dialogoRepeticao] = "Mais trabalhos nas sombras?"
            it[vendedor] = false
            it[questsDisponiveis] = "[9,10]"
            it[reputacaoNecessaria] = 0
            it[faccao] = "Guilda das Sombras"
        }
        
        NPCsTable.insert {
            it[nome] = "Comerciante de Artefatos"
            it[tipo] = "VENDEDOR"
            it[localizacao] = "Cidade de Thornhaven"
            it[dialogoInicial] = "Itens mágicos raros e poderosos! Só para aventureiros de elite."
            it[dialogoRepeticao] = "Pronto para investir em poder real?"
            it[vendedor] = true
            it[itensVenda] = "[6,14,15,16]"
            it[reputacaoNecessaria] = 200
        }
        
        // NPCs do Porto
        NPCsTable.insert {
            it[nome] = "Capitão Barbanegra"
            it[tipo] = "QUEST_GIVER"
            it[localizacao] = "Porto de Maré Alta"
            it[dialogoInicial] = "Arrr! Você tem cara de quem não tem medo de aventura no mar!"
            it[dialogoRepeticao] = "Pronto para zarpar?"
            it[vendedor] = false
            it[questsDisponiveis] = "[11]"
            it[reputacaoNecessaria] = 0
        }
        
        NPCsTable.insert {
            it[nome] = "Comerciante Exótico"
            it[tipo] = "VENDEDOR"
            it[localizacao] = "Porto de Maré Alta"
            it[dialogoInicial] = "Mercadorias das Ilhas Distantes! Você não encontrará isso em nenhum outro lugar!"
            it[dialogoRepeticao] = "Quer ver as novidades?"
            it[vendedor] = true
            it[itensVenda] = "[6,13,14,15,16]"
            it[reputacaoNecessaria] = 0
        }
        
        NPCsTable.insert {
            it[nome] = "Oráculo Mystara"
            it[tipo] = "QUEST_GIVER"
            it[localizacao] = "Porto de Maré Alta"
            it[dialogoInicial] = "As estrelas falam de um grande destino... seu destino, aventureiro."
            it[dialogoRepeticao] = "O futuro se revela para aqueles que ousam olhar..."
            it[vendedor] = false
            it[questsDisponiveis] = "[12]"
            it[reputacaoNecessaria] = 300
        }
    }
    
    private fun inserirQuestsPadrao() {
        // Quests Iniciais (Nível 1-3)
        QuestsTable.insert {
            it[nome] = "Forja Quebrada"
            it[descricao] = "O ferreiro Aldric precisa de minério de ferro para consertar sua bigorna."
            it[tipo] = "SECUNDARIA"
            it[npcId] = 1
            it[nivelMinimo] = 1
            it[objetivos] = "[{\"tipo\":\"coletar\",\"item\":\"Minério de Ferro\",\"quantidade\":5}]"
            it[recompensaXP] = 100
            it[recompensaOuro] = 50
            it[recompensaItens] = "[2]"
            it[historia] = "Aldric está frustrado. Sua bigorna quebrou e ele precisa de minério para consertá-la."
        }
        
        QuestsTable.insert {
            it[nome] = "Ervas Raras"
            it[descricao] = "Elara precisa de ervas medicinais da Floresta Sombria para suas poções."
            it[tipo] = "SECUNDARIA"
            it[npcId] = 2
            it[nivelMinimo] = 1
            it[objetivos] = "[{\"tipo\":\"coletar\",\"item\":\"Erva Medicinal\",\"quantidade\":3}]"
            it[recompensaXP] = 75
            it[recompensaOuro] = 30
            it[recompensaItens] = "[11,12]"
            it[historia] = "As ervas que Elara precisa só crescem nas profundezas da Floresta Sombria."
        }
        
        QuestsTable.insert {
            it[nome] = "Caçada aos Goblins"
            it[descricao] = "Elimine 10 goblins que estão atacando fazendeiros."
            it[tipo] = "PRINCIPAL"
            it[npcId] = 3
            it[nivelMinimo] = 1
            it[objetivos] = "[{\"tipo\":\"matar\",\"monstro\":\"Goblin\",\"quantidade\":10}]"
            it[recompensaXP] = 200
            it[recompensaOuro] = 100
            it[recompensaItens] = "[3]"
            it[historia] = "Goblins da Floresta Sombria têm atacado fazendas próximas. O Capitão Roderick precisa que você os elimine."
        }
        
        QuestsTable.insert {
            it[nome] = "Patrulha da Estrada"
            it[descricao] = "Patrulhe a Estrada do Rei e elimine bandidos."
            it[tipo] = "DIARIA"
            it[npcId] = 3
            it[nivelMinimo] = 2
            it[objetivos] = "[{\"tipo\":\"matar\",\"monstro\":\"Orc\",\"quantidade\":5}]"
            it[recompensaXP] = 150
            it[recompensaOuro] = 75
            it[historia] = "A Estrada do Rei precisa ser mantida segura para os mercadores."
        }
        
        QuestsTable.insert {
            it[nome] = "Primeiro Grimório"
            it[descricao] = "O Sábio Meridian quer que você recupere um grimório antigo das Ruínas."
            it[tipo] = "PRINCIPAL"
            it[npcId] = 4
            it[nivelMinimo] = 3
            it[objetivos] = "[{\"tipo\":\"explorar\",\"local\":\"Ruínas Antigas\"},{\"tipo\":\"coletar\",\"item\":\"Grimório Antigo\",\"quantidade\":1}]"
            it[recompensaXP] = 300
            it[recompensaOuro] = 150
            it[recompensaItens] = "[15]"
            it[historia] = "Um grimório de grande poder foi perdido nas Ruínas Antigas. Meridian precisa dele para seus estudos."
        }
        
        // Quests de Thornhaven (Nível 4-8)
        QuestsTable.insert {
            it[nome] = "Provação do Guerreiro"
            it[descricao] = "Derrote um Ogro para provar seu valor como guerreiro."
            it[tipo] = "PRINCIPAL"
            it[npcId] = 7
            it[nivelMinimo] = 5
            it[objetivos] = "[{\"tipo\":\"matar\",\"monstro\":\"Ogro\",\"quantidade\":1}]"
            it[recompensaXP] = 500
            it[recompensaOuro] = 200
            it[recompensaItens] = "[4]"
            it[historia] = "Para ser aceito na Guilda de Guerreiros, você deve provar sua força contra um Ogro."
        }
        
        QuestsTable.insert {
            it[nome] = "Segredos Arcanos"
            it[descricao] = "Colete 3 cristais de mana das Catacumbas Profundas."
            it[tipo] = "PRINCIPAL"
            it[npcId] = 8
            it[nivelMinimo] = 6
            it[objetivos] = "[{\"tipo\":\"coletar\",\"item\":\"Cristal de Mana\",\"quantidade\":3}]"
            it[recompensaXP] = 700
            it[recompensaOuro] = 300
            it[historia] = "Cristais de mana pura são necessários para rituais arcanos avançados."
        }
        
        QuestsTable.insert {
            it[nome] = "Purificação do Templo"
            it[descricao] = "Elimine mortos-vivos que profanam o templo antigo."
            it[tipo] = "PRINCIPAL"
            it[npcId] = 9
            it[nivelMinimo] = 5
            it[objetivos] = "[{\"tipo\":\"matar\",\"monstro\":\"Esqueleto\",\"quantidade\":20}]"
            it[recompensaXP] = 600
            it[recompensaOuro] = 250
            it[historia] = "Um templo sagrado foi tomado por mortos-vivos. A luz divina deve ser restaurada."
        }
        
        QuestsTable.insert {
            it[nome] = "Contrato nas Sombras"
            it[descricao] = "Elimine discretamente um alvo marcado pela Guilda."
            it[tipo] = "SECUNDARIA"
            it[npcId] = 10
            it[nivelMinimo] = 4
            it[objetivos] = "[{\"tipo\":\"assassinar\",\"alvo\":\"Mercador Corrupto\",\"quantidade\":1}]"
            it[recompensaXP] = 400
            it[recompensaOuro] = 500
            it[historia] = "A Guilda das Sombras tem seus próprios métodos de justiça."
        }
        
        QuestsTable.insert {
            it[nome] = "Roubo do Século"
            it[descricao] = "Infiltre-se no palácio e roube a Coroa Real."
            it[tipo] = "PRINCIPAL"
            it[npcId] = 10
            it[nivelMinimo] = 8
            it[objetivos] = "[{\"tipo\":\"roubar\",\"item\":\"Coroa Real\",\"quantidade\":1}]"
            it[recompensaXP] = 1500
            it[recompensaOuro] = 2000
            it[historia] = "O maior desafio para qualquer ladrão: roubar a Coroa Real do palácio mais bem guardado do reino."
        }
        
        // Quests Avançadas (Nível 9+)
        QuestsTable.insert {
            it[nome] = "Tesouro Pirata"
            it[descricao] = "Encontre o tesouro lendário do Capitão Caveira Sangrenta."
            it[tipo] = "PRINCIPAL"
            it[npcId] = 12
            it[nivelMinimo] = 7
            it[objetivos] = "[{\"tipo\":\"explorar\",\"local\":\"Ilha dos Piratas\"},{\"tipo\":\"coletar\",\"item\":\"Baú do Tesouro\",\"quantidade\":1}]"
            it[recompensaXP] = 1200
            it[recompensaOuro] = 1500
            it[historia] = "Lendas falam de um tesouro imenso escondido na Ilha dos Piratas."
        }
        
        QuestsTable.insert {
            it[nome] = "Profecia Sombria"
            it[descricao] = "Descubra o significado da profecia sobre o retorno do Lich."
            it[tipo] = "PRINCIPAL"
            it[npcId] = 14
            it[nivelMinimo] = 12
            it[objetivos] = "[{\"tipo\":\"investigar\",\"local\":\"Torre do Lich\"},{\"tipo\":\"derrotar\",\"boss\":\"Lich Supremo\",\"quantidade\":1}]"
            it[recompensaXP] = 5000
            it[recompensaOuro] = 5000
            it[historia] = "Uma profecia antiga fala do retorno de um Lich poderoso. Você deve impedir isso."
        }
    }
    
    private fun inserirMagiasPadrao() {
        // Magias de Nível 1
        MagiasTable.insert {
            it[nome] = "Mísseis Mágicos"
            it[nivel] = 1
            it[escola] = "EVOCACAO"
            it[classe] = "MAGO"
            it[alcance] = "MEDIO"
            it[duracao] = "INSTANTANEO"
            it[componentes] = "V,S"
            it[tempoConjuracao] = "ACAO"
            it[dano] = "3d4+3"
            it[tipoDano] = "FORCA"
            it[efeito] = "Cria 3 dardos de energia mágica que acertam automaticamente"
            it[descricao] = "Você cria três dardos brilhantes de força mágica. Cada dardo acerta uma criatura à sua escolha que você possa ver dentro do alcance."
            it[custoMana] = 10
        }
        
        MagiasTable.insert {
            it[nome] = "Curar Ferimentos"
            it[nivel] = 1
            it[escola] = "EVOCACAO"
            it[classe] = "CLERIGO"
            it[alcance] = "TOQUE"
            it[duracao] = "INSTANTANEO"
            it[componentes] = "V,S"
            it[tempoConjuracao] = "ACAO"
            it[dano] = "1d8+2"
            it[efeito] = "Restaura pontos de vida"
            it[descricao] = "Uma criatura que você tocar recupera pontos de vida."
            it[custoMana] = 10
        }
        
        MagiasTable.insert {
            it[nome] = "Escudo Arcano"
            it[nivel] = 1
            it[escola] = "ABJURACAO"
            it[classe] = "MAGO"
            it[alcance] = "PESSOAL"
            it[duracao] = "1 RODADA"
            it[componentes] = "V,S"
            it[tempoConjuracao] = "REACAO"
            it[efeito] = "+5 CA até o próximo turno"
            it[descricao] = "Uma barreira invisível de força mágica aparece e o protege."
            it[custoMana] = 10
        }
        
        MagiasTable.insert {
            it[nome] = "Detectar Magia"
            it[nivel] = 1
            it[escola] = "ADIVINHACAO"
            it[classe] = "MAGO,CLERIGO"
            it[alcance] = "PESSOAL"
            it[duracao] = "CONCENTRACAO"
            it[componentes] = "V,S"
            it[tempoConjuracao] = "ACAO"
            it[efeito] = "Detecta presença de magia"
            it[descricao] = "Você sente a presença de magia dentro de 30 pés de você."
            it[custoMana] = 10
        }
        
        // Magias de Nível 2
        MagiasTable.insert {
            it[nome] = "Bola de Fogo Menor"
            it[nivel] = 2
            it[escola] = "EVOCACAO"
            it[classe] = "MAGO"
            it[alcance] = "MEDIO"
            it[duracao] = "INSTANTANEO"
            it[componentes] = "V,S,M"
            it[tempoConjuracao] = "ACAO"
            it[dano] = "4d6"
            it[tipoDano] = "FOGO"
            it[efeito] = "Explosão de fogo em área"
            it[descricao] = "Uma esfera brilhante de fogo surge de seu dedo e explode no ponto escolhido."
            it[custoMana] = 20
        }
        
        MagiasTable.insert {
            it[nome] = "Restauração Menor"
            it[nivel] = 2
            it[escola] = "ABJURACAO"
            it[classe] = "CLERIGO"
            it[alcance] = "TOQUE"
            it[duracao] = "INSTANTANEO"
            it[componentes] = "V,S"
            it[tempoConjuracao] = "ACAO"
            it[dano] = "2d8+4"
            it[efeito] = "Cura doenças e restaura PV"
            it[descricao] = "Você toca uma criatura e pode curar uma doença ou condição que a esteja afligindo."
            it[custoMana] = 20
        }
        
        MagiasTable.insert {
            it[nome] = "Invisibilidade"
            it[nivel] = 2
            it[escola] = "ILUSAO"
            it[classe] = "MAGO"
            it[alcance] = "TOQUE"
            it[duracao] = "CONCENTRACAO"
            it[componentes] = "V,S,M"
            it[tempoConjuracao] = "ACAO"
            it[efeito] = "Torna alvo invisível"
            it[descricao] = "Uma criatura que você tocar se torna invisível até a magia acabar."
            it[custoMana] = 20
        }
        
        // Magias de Nível 3
        MagiasTable.insert {
            it[nome] = "Bola de Fogo"
            it[nivel] = 3
            it[escola] = "EVOCACAO"
            it[classe] = "MAGO"
            it[alcance] = "LONGO"
            it[duracao] = "INSTANTANEO"
            it[componentes] = "V,S,M"
            it[tempoConjuracao] = "ACAO"
            it[dano] = "8d6"
            it[tipoDano] = "FOGO"
            it[efeito] = "Grande explosão de fogo"
            it[descricao] = "Uma esfera brilhante de fogo surge e explode com um rugido em um ponto à sua escolha."
            it[custoMana] = 30
        }
        
        MagiasTable.insert {
            it[nome] = "Reviver os Mortos"
            it[nivel] = 3
            it[escola] = "NECROMANCIA"
            it[classe] = "CLERIGO"
            it[alcance] = "TOQUE"
            it[duracao] = "INSTANTANEO"
            it[componentes] = "V,S,M"
            it[tempoConjuracao] = "1 MINUTO"
            it[efeito] = "Ressuscita criatura morta"
            it[descricao] = "Você retorna uma criatura morta à vida, desde que ela não esteja morta há mais de 1 minuto."
            it[custoMana] = 30
        }
        
        MagiasTable.insert {
            it[nome] = "Relâmpago"
            it[nivel] = 3
            it[escola] = "EVOCACAO"
            it[classe] = "MAGO"
            it[alcance] = "PESSOAL"
            it[duracao] = "INSTANTANEO"
            it[componentes] = "V,S,M"
            it[tempoConjuracao] = "ACAO"
            it[dano] = "8d6"
            it[tipoDano] = "ELETRICO"
            it[efeito] = "Linha de relâmpago"
            it[descricao] = "Um raio de luz azul surge de sua mão em uma linha de 100 pés de comprimento e 5 pés de largura."
            it[custoMana] = 30
        }
        
        // Magias de Nível 4+
        MagiasTable.insert {
            it[nome] = "Tempestade de Gelo"
            it[nivel] = 4
            it[escola] = "EVOCACAO"
            it[classe] = "MAGO"
            it[alcance] = "LONGO"
            it[duracao] = "INSTANTANEO"
            it[componentes] = "V,S,M"
            it[tempoConjuracao] = "ACAO"
            it[dano] = "10d6"
            it[tipoDano] = "GELO"
            it[efeito] = "Tempestade de granizo congelante"
            it[descricao] = "Granizo do tamanho de pedras cai em uma área cilíndrica."
            it[custoMana] = 40
        }
        
        MagiasTable.insert {
            it[nome] = "Cura em Massa"
            it[nivel] = 5
            it[escola] = "EVOCACAO"
            it[classe] = "CLERIGO"
            it[alcance] = "MEDIO"
            it[duracao] = "INSTANTANEO"
            it[componentes] = "V,S"
            it[tempoConjuracao] = "ACAO"
            it[dano] = "5d8+10"
            it[efeito] = "Cura múltiplos alvos"
            it[descricao] = "Energia curativa se espalha de você para curar criaturas feridas."
            it[custoMana] = 50
        }
        
        MagiasTable.insert {
            it[nome] = "Desintegração"
            it[nivel] = 6
             it[escola] = "EVOCACAO"
            it[classe] = "MAGO"
            it[alcance] = "MEDIO"
            it[duracao] = "INSTANTANEO"
            it[componentes] = "V,S,M"
            it[tempoConjuracao] = "ACAO"
            it[dano] = "15d6"
            it[tipoDano] = "FORCA"
            it[efeito] = "Desintegra alvo completamente"
            it[descricao] = "Um fino raio verde sai de sua mão. Uma criatura atingida sofre dano massivo."
            it[custoMana] = 60
        }
    }
    
    private fun inserirTalentosPadrao() {
        // Talentos de Combate
        TalentosTable.insert {
            it[nome] = "Ataque Poderoso"
            it[descricao] = "Você pode sacrificar precisão por poder bruto"
            it[tipo] = "COMBATE"
            it[classe] = "GUERREIRO"
            it[nivelMinimo] = 1
            it[efeito] = "-2 ataque, +4 dano"
            it[custoTalento] = 1
        }
        
        TalentosTable.insert {
            it[nome] = "Ataque Furtivo"
            it[descricao] = "Causa dano extra quando ataca com vantagem"
            it[tipo] = "COMBATE"
            it[classe] = "LADRAO"
            it[nivelMinimo] = 1
            it[efeito] = "+2d6 dano furtivo"
            it[custoTalento] = 1
        }
        
        TalentosTable.insert {
            it[nome] = "Defesa Aprimorada"
            it[descricao] = "Sua experiência em combate melhora sua defesa"
            it[tipo] = "COMBATE"
            it[nivelMinimo] = 3
            it[efeito] = "+2 CA"
            it[custoTalento] = 1
        }
        
        TalentosTable.insert {
            it[nome] = "Ataque Duplo"
            it[descricao] = "Você pode atacar duas vezes por turno"
            it[tipo] = "COMBATE"
            it[classe] = "GUERREIRO"
            it[nivelMinimo] = 5
            it[prerequisitos] = "{\"nivel\":5,\"classe\":\"GUERREIRO\"}"
            it[efeito] = "2 ataques por turno"
            it[custoTalento] = 2
        }
        
        // Talentos de Magia
        TalentosTable.insert {
            it[nome] = "Magia Potencializada"
            it[descricao] = "Suas magias causam mais dano"
            it[tipo] = "MAGIA"
            it[classe] = "MAGO"
            it[nivelMinimo] = 3
            it[efeito] = "+2 dano por dado de magia"
            it[custoTalento] = 1
        }
        
        TalentosTable.insert {
            it[nome] = "Conjuração Rápida"
            it[descricao] = "Você pode conjurar magias mais rapidamente"
            it[tipo] = "MAGIA"
            it[nivelMinimo] = 5
            it[efeito] = "Conjurar 1 magia como ação bônus"
            it[custoTalento] = 2
        }
        
        TalentosTable.insert {
            it[nome] = "Foco Arcano"
            it[descricao] = "Aumenta seu poder mágico"
            it[tipo] = "MAGIA"
            it[classe] = "MAGO,CLERIGO"
            it[nivelMinimo] = 1
            it[efeito] = "+1 CD de magias"
            it[custoTalento] = 1
        }
        
        // Talentos Gerais
        TalentosTable.insert {
            it[nome] = "Sortudo"
            it[descricao] = "A sorte favorece você"
            it[tipo] = "GERAL"
            it[nivelMinimo] = 1
            it[efeito] = "Pode rolar novamente 1 dado por dia"
            it[custoTalento] = 1
        }
        
        TalentosTable.insert {
            it[nome] = "Resistente"
            it[descricao] = "Você é excepcionalmente resistente"
            it[tipo] = "GERAL"
            it[nivelMinimo] = 1
            it[efeito] = "+2 PV por nível"
            it[custoTalento] = 1
        }
        
        TalentosTable.insert {
            it[nome] = "Atlético"
            it[descricao] = "Você é excepcionalmente atlético"
            it[tipo] = "GERAL"
            it[nivelMinimo] = 1
            it[efeito] = "+2 em testes de Força e Destreza"
            it[custoTalento] = 1
        }
    }
    
    private fun inserirConquistasPadrao() {
        // Conquistas de Combate
        ConquistasTable.insert {
            it[nome] = "Primeira Vitória"
            it[descricao] = "Vença seu primeiro combate"
            it[tipo] = "COMBATE"
            it[condicao] = "{\"tipo\":\"vitorias\",\"quantidade\":1}"
            it[recompensaXP] = 50
            it[recompensaOuro] = 25
            it[pontos] = 10
            it[secreta] = false
        }
        
        ConquistasTable.insert {
            it[nome] = "Caçador de Goblins"
            it[descricao] = "Derrote 50 goblins"
            it[tipo] = "COMBATE"
            it[condicao] = "{\"tipo\":\"matar\",\"monstro\":\"Goblin\",\"quantidade\":50}"
            it[recompensaXP] = 200
            it[recompensaOuro] = 100
            it[recompensaTitulo] = "Caçador de Goblins"
            it[pontos] = 25
            it[secreta] = false
        }
        
        ConquistasTable.insert {
            it[nome] = "Matador de Dragões"
            it[descricao] = "Derrote um dragão"
            it[tipo] = "COMBATE"
            it[condicao] = "{\"tipo\":\"matar\",\"monstro\":\"Dragão\",\"quantidade\":1}"
            it[recompensaXP] = 1000
            it[recompensaOuro] = 1000
            it[recompensaTitulo] = "Matador de Dragões"
            it[pontos] = 100
            it[secreta] = false
        }
        
        // Conquistas de Exploração
        ConquistasTable.insert {
            it[nome] = "Explorador Iniciante"
            it[descricao] = "Descubra 5 localizações"
            it[tipo] = "EXPLORACAO"
            it[condicao] = "{\"tipo\":\"descobrir\",\"quantidade\":5}"
            it[recompensaXP] = 100
            it[pontos] = 15
            it[secreta] = false
        }
        
        ConquistasTable.insert {
            it[nome] = "Cartógrafo"
            it[descricao] = "Descubra todas as localizações do mundo"
            it[tipo] = "EXPLORACAO"
            it[condicao] = "{\"tipo\":\"descobrir\",\"quantidade\":10}"
            it[recompensaXP] = 500
            it[recompensaOuro] = 500
            it[recompensaTitulo] = "Cartógrafo"
            it[pontos] = 50
            it[secreta] = false
        }
        
        // Conquistas de Coleção
        ConquistasTable.insert {
            it[nome] = "Colecionador"
            it[descricao] = "Possua 50 itens diferentes"
            it[tipo] = "COLECAO"
            it[condicao] = "{\"tipo\":\"itens_unicos\",\"quantidade\":50}"
            it[recompensaXP] = 300
            it[pontos] = 30
            it[secreta] = false
        }
        
        ConquistasTable.insert {
            it[nome] = "Mestre Arcano"
            it[descricao] = "Aprenda todas as magias disponíveis"
            it[tipo] = "COLECAO"
            it[condicao] = "{\"tipo\":\"magias\",\"quantidade\":15}"
            it[recompensaXP] = 1000
            it[recompensaTitulo] = "Mestre Arcano"
            it[pontos] = 75
            it[secreta] = false
        }
        
        // Conquistas Secretas
        ConquistasTable.insert {
            it[nome] = "Imortal"
            it[descricao] = "Alcance nível 20 sem morrer"
            it[tipo] = "SECRETA"
            it[condicao] = "{\"tipo\":\"nivel\",\"quantidade\":20,\"mortes\":0}"
            it[recompensaXP] = 5000
            it[recompensaOuro] = 5000
            it[recompensaTitulo] = "O Imortal"
            it[pontos] = 200
            it[secreta] = true
        }
        
        ConquistasTable.insert {
            it[nome] = "Lenda Viva"
            it[descricao] = "Complete todas as quests principais"
            it[tipo] = "SECRETA"
            it[condicao] = "{\"tipo\":\"quests_principais\",\"quantidade\":10}"
            it[recompensaXP] = 3000
            it[recompensaOuro] = 3000
            it[recompensaTitulo] = "Lenda Viva"
            it[pontos] = 150
            it[secreta] = true
        }
    }
    
    private fun inserirFaccoesPadrao() {
        FaccoesTable.insert {
            it[nome] = "Guarda da Vila"
            it[descricao] = "A força de segurança local que protege os cidadãos"
            it[tipo] = "GUILDA"
            it[lider] = "Capitão Roderick"
            it[sede] = "Vila Inicial"
            it[recompensas] = "{\"HONRADO\":{\"desconto\":10},\"EXALTADO\":{\"desconto\":20,\"item\":\"Armadura da Guarda\"}}"
        }
        
        FaccoesTable.insert {
            it[nome] = "Guilda de Guerreiros"
            it[descricao] = "Organização de combatentes de elite"
            it[tipo] = "GUILDA"
            it[lider] = "Mestre da Guilda"
            it[sede] = "Cidade de Thornhaven"
            it[recompensas] = "{\"HONRADO\":{\"talento\":\"Ataque Duplo\"},\"EXALTADO\":{\"item\":\"Espada Lendária\"}}"
        }
        
        FaccoesTable.insert {
            it[nome] = "Círculo Arcano"
            it[descricao] = "Sociedade de magos e estudiosos das artes arcanas"
            it[tipo] = "ORDEM"
            it[lider] = "Archmage Silvanus"
            it[sede] = "Cidade de Thornhaven"
            it[recompensas] = "{\"HONRADO\":{\"magias\":5},\"EXALTADO\":{\"item\":\"Cajado Supremo\"}}"
        }
        
        FaccoesTable.insert {
            it[nome] = "Templo da Luz"
            it[descricao] = "Ordem religiosa dedicada aos deuses da luz e cura"
            it[tipo] = "ORDEM"
            it[lider] = "Alto Sacerdote Theron"
            it[sede] = "Cidade de Thornhaven"
            it[recompensas] = "{\"HONRADO\":{\"cura_gratis\":true},\"EXALTADO\":{\"item\":\"Amuleto Sagrado\"}}"
        }
        
        FaccoesTable.insert {
            it[nome] = "Guilda das Sombras"
            it[descricao] = "Organização secreta de ladrões e assassinos"
            it[tipo] = "CULTO"
            it[lider] = "Shadowmaster Vex"
            it[sede] = "Cidade de Thornhaven"
            it[recompensas] = "{\"HONRADO\":{\"contratos_especiais\":true},\"EXALTADO\":{\"item\":\"Adaga das Sombras\"}}"
        }
    }
    
    private fun inserirReceitasPadrao() {
        // Receitas de Ferreiro
        ReceitasTable.insert {
            it[nome] = "Forjar Espada de Ferro"
            it[profissao] = "FERREIRO"
            it[nivelNecessario] = 1
            it[itemResultado] = 3 // Espada Longa
            it[quantidadeResultado] = 1
            it[materiais] = "[{\"itemId\":17,\"quantidade\":3}]" // Minério de Ferro
            it[custoOuro] = 10
            it[tempoCrafting] = 30
            it[descricao] = "Forja uma espada longa de ferro de qualidade"
        }
        
        ReceitasTable.insert {
            it[nome] = "Forjar Armadura de Placas"
            it[profissao] = "FERREIRO"
            it[nivelNecessario] = 5
            it[itemResultado] = 9 // Armadura de Placas
            it[quantidadeResultado] = 1
            it[materiais] = "[{\"itemId\":17,\"quantidade\":10}]"
            it[custoOuro] = 200
            it[tempoCrafting] = 120
            it[descricao] = "Forja uma armadura pesada de placas de metal"
        }
        
        // Receitas de Alquimista
        ReceitasTable.insert {
            it[nome] = "Preparar Poção de Cura"
            it[profissao] = "ALQUIMISTA"
            it[nivelNecessario] = 1
            it[itemResultado] = 12 // Poção de Cura
            it[quantidadeResultado] = 1
            it[materiais] = "[{\"itemId\":19,\"quantidade\":2}]" // Erva Medicinal
            it[custoOuro] = 25
            it[tempoCrafting] = 15
            it[descricao] = "Prepara uma poção que restaura pontos de vida"
        }
        
        ReceitasTable.insert {
            it[nome] = "Preparar Poção de Força"
            it[profissao] = "ALQUIMISTA"
            it[nivelNecessario] = 3
            it[itemResultado] = 14 // Poção de Força
            it[quantidadeResultado] = 1
            it[materiais] = "[{\"itemId\":19,\"quantidade\":3}]"
            it[custoOuro] = 75
            it[tempoCrafting] = 30
            it[descricao] = "Prepara uma poção que aumenta a força temporariamente"
        }
        
        // Receitas de Encantador
        ReceitasTable.insert {
            it[nome] = "Encantar Arma +1"
            it[profissao] = "ENCANTADOR"
            it[nivelNecessario] = 5
            it[itemResultado] = 6 // Espada Flamejante (exemplo)
            it[quantidadeResultado] = 1
            it[materiais] = "[{\"itemId\":3,\"quantidade\":1}]" // Espada Longa base
            it[custoOuro] = 300
            it[tempoCrafting] = 60
            it[descricao] = "Encanta uma arma com poder mágico"
        }
    }
    
    private fun inserirCondicoesPadrao() {
        // Buffs
        CondicoesStatusTable.insert {
            it[nome] = "Bênção"
            it[tipo] = "BUFF"
            it[descricao] = "Abençoado pelos deuses"
            it[efeito] = "{\"ataque\":2,\"defesa\":2}"
            it[duracao] = 10
            it[icone] = "✨"
        }
        
        CondicoesStatusTable.insert {
            it[nome] = "Força Aprimorada"
            it[tipo] = "BUFF"
            it[descricao] = "Força aumentada magicamente"
            it[efeito] = "{\"forca\":4,\"dano\":2}"
            it[duracao] = 10
            it[icone] = "💪"
        }
        
        CondicoesStatusTable.insert {
            it[nome] = "Velocidade"
            it[tipo] = "BUFF"
            it[descricao] = "Movimentos acelerados"
            it[efeito] = "{\"destreza\":4,\"iniciativa\":2}"
            it[duracao] = 10
            it[icone] = "⚡"
        }
        
        CondicoesStatusTable.insert {
            it[nome] = "Escudo Mágico"
            it[tipo] = "BUFF"
            it[descricao] = "Protegido por magia"
            it[efeito] = "{\"ca\":5}"
            it[duracao] = 5
            it[icone] = "🛡️"
        }
        
        // Debuffs
        CondicoesStatusTable.insert {
            it[nome] = "Envenenado"
            it[tipo] = "DEBUFF"
            it[descricao] = "Sofrendo com veneno"
            it[efeito] = "{\"dano_por_turno\":5,\"constituicao\":-2}"
            it[duracao] = 5
            it[icone] = "☠️"
        }
        
        CondicoesStatusTable.insert {
            it[nome] = "Atordoado"
            it[tipo] = "DEBUFF"
            it[descricao] = "Incapaz de agir efetivamente"
            it[efeito] = "{\"ataque\":-4,\"defesa\":-4}"
            it[duracao] = 2
            it[icone] = "😵"
        }
        
        CondicoesStatusTable.insert {
            it[nome] = "Queimando"
            it[tipo] = "DEBUFF"
            it[descricao] = "Em chamas"
            it[efeito] = "{\"dano_por_turno\":10}"
            it[duracao] = 3
            it[icone] = "🔥"
        }
        
        CondicoesStatusTable.insert {
            it[nome] = "Congelado"
            it[tipo] = "DEBUFF"
            it[descricao] = "Movimentos reduzidos pelo frio"
            it[efeito] = "{\"destreza\":-4,\"iniciativa\":-2}"
            it[duracao] = 3
            it[icone] = "❄️"
        }
        
        CondicoesStatusTable.insert {
            it[nome] = "Amaldiçoado"
            it[tipo] = "DEBUFF"
            it[descricao] = "Sob efeito de maldição"
            it[efeito] = "{\"todos_atributos\":-2}"
            it[duracao] = -1 // Permanente até remover
            it[icone] = "👿"
        }
    }
    
    private fun inserirEventosPadrao() {
        // Eventos de Combate
        EventosTable.insert {
            it[nome] = "Emboscada de Bandidos"
            it[descricao] = "Você é emboscado por bandidos na estrada!"
            it[tipo] = "COMBATE"
            it[localizacao] = "Estrada do Rei"
            it[chance] = 20
            it[nivelMinimo] = 2
            it[opcoes] = "[{\"texto\":\"Lutar\",\"resultado\":\"combate\"},{\"texto\":\"Fugir\",\"resultado\":\"fuga\"}]"
            it[recompensas] = "{\"ouro\":50,\"xp\":100}"
        }
        
        EventosTable.insert {
            it[nome] = "Ataque de Lobos"
            it[descricao] = "Uma matilha de lobos famintos cerca você!"
            it[tipo] = "COMBATE"
            it[localizacao] = "Floresta Sombria"
            it[chance] = 30
            it[nivelMinimo] = 1
            it[opcoes] = "[{\"texto\":\"Lutar\",\"resultado\":\"combate\"},{\"texto\":\"Intimidar\",\"resultado\":\"teste_carisma\"}]"
            it[recompensas] = "{\"xp\":75}"
        }
        
        // Eventos de Tesouro
        EventosTable.insert {
            it[nome] = "Baú Escondido"
            it[descricao] = "Você encontra um baú escondido entre as ruínas!"
            it[tipo] = "TESOURO"
            it[chance] = 15
            it[nivelMinimo] = 1
            it[opcoes] = "[{\"texto\":\"Abrir\",\"resultado\":\"tesouro\"},{\"texto\":\"Verificar armadilhas\",\"resultado\":\"teste_destreza\"}]"
            it[recompensas] = "{\"ouro\":100,\"item_aleatorio\":true}"
        }
        
        EventosTable.insert {
            it[nome] = "Mercador Perdido"
            it[descricao] = "Um mercador perdido oferece vender itens com desconto!"
            it[tipo] = "ENCONTRO"
            it[chance] = 10
            it[nivelMinimo] = 1
            it[opcoes] = "[{\"texto\":\"Comprar\",\"resultado\":\"loja\"},{\"texto\":\"Ajudar\",\"resultado\":\"quest_rapida\"}]"
            it[recompensas] = "{\"desconto\":30}"
        }
        
        // Eventos de Mistério
        EventosTable.insert {
            it[nome] = "Runas Misteriosas"
            it[descricao] = "Você encontra runas brilhantes gravadas em uma pedra antiga."
            it[tipo] = "MISTERIO"
            it[chance] = 5
            it[nivelMinimo] = 3
            it[opcoes] = "[{\"texto\":\"Investigar\",\"resultado\":\"teste_inteligencia\"},{\"texto\":\"Ignorar\",\"resultado\":\"nada\"}]"
            it[recompensas] = "{\"magia\":true,\"xp\":200}"
        }
        
        // Eventos de Armadilha
        EventosTable.insert {
            it[nome] = "Armadilha de Flechas"
            it[descricao] = "Você aciona uma armadilha antiga! Flechas voam em sua direção!"
            it[tipo] = "ARMADILHA"
            it[chance] = 15
            it[nivelMinimo] = 2
            it[opcoes] = "[{\"texto\":\"Esquivar\",\"resultado\":\"teste_destreza\"},{\"texto\":\"Escudo\",\"resultado\":\"teste_constituicao\"}]"
            it[recompensas] = "{\"dano\":\"2d6\"}"
        }
        
        EventosTable.insert {
            it[nome] = "Poço Oculto"
            it[descricao] = "O chão cede sob seus pés! Você cai em um poço!"
            it[tipo] = "ARMADILHA"
            it[chance] = 10
            it[nivelMinimo] = 1
            it[opcoes] = "[{\"texto\":\"Agarrar borda\",\"resultado\":\"teste_destreza\"},{\"texto\":\"Cair\",\"resultado\":\"dano\"}]"
            it[recompensas] = "{\"dano\":\"3d6\"}"
        }
    }
    
    // ==================== FUNÇÕES DE UTILIDADE ====================
    
    fun getCurrentTimestamp(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }
    
    fun salvarPersonagem(
        nome: String,
        raca: String,
        classe: String,
        nivel: Int,
        forca: Int,
        destreza: Int,
        constituicao: Int,
        inteligencia: Int,
        sabedoria: Int,
        carisma: Int,
        pontosVida: Int,
        pontosVidaMaximos: Int,
        experiencia: Int,
        dinheiro: Int,
        localizacaoAtual: String = "Vila Inicial"
    ): Long {
        return transaction {
            PersonagensTable.insert {
                it[PersonagensTable.nome] = nome
                it[PersonagensTable.raca] = raca
                it[PersonagensTable.classe] = classe
                it[PersonagensTable.nivel] = nivel
                it[PersonagensTable.forca] = forca
                it[PersonagensTable.destreza] = destreza
                it[PersonagensTable.constituicao] = constituicao
                it[PersonagensTable.inteligencia] = inteligencia
                it[PersonagensTable.sabedoria] = sabedoria
                it[PersonagensTable.carisma] = carisma
                it[PersonagensTable.pontosVida] = pontosVida
                it[PersonagensTable.pontosVidaMaximos] = pontosVidaMaximos
                it[PersonagensTable.experiencia] = experiencia
                it[PersonagensTable.dinheiro] = dinheiro
                it[PersonagensTable.localizacaoAtual] = localizacaoAtual
            } get PersonagensTable.id
        }
    }
    
    fun atualizarPersonagem(
        nome: String,
        nivel: Int? = null,
        pontosVida: Int? = null,
        experiencia: Int? = null,
        dinheiro: Int? = null,
        localizacaoAtual: String? = null
    ) {
        transaction {
            PersonagensTable.update({ PersonagensTable.nome eq nome }) {
                nivel?.let { valor -> it[PersonagensTable.nivel] = valor }
                pontosVida?.let { valor -> it[PersonagensTable.pontosVida] = valor }
                experiencia?.let { valor -> it[PersonagensTable.experiencia] = valor }
                dinheiro?.let { valor -> it[PersonagensTable.dinheiro] = valor }
                localizacaoAtual?.let { valor -> it[PersonagensTable.localizacaoAtual] = valor }
            }
        }
    }
    
    fun registrarAcao(
        personagemId: Long,
        tipo: String,
        acao: String,
        detalhes: String? = null,
        localizacao: String? = null
    ) {
        transaction {
            HistoricoAcoesTable.insert {
                it[HistoricoAcoesTable.personagemId] = personagemId
                it[HistoricoAcoesTable.tipo] = tipo
                it[HistoricoAcoesTable.acao] = acao
                it[HistoricoAcoesTable.detalhes] = detalhes
                it[HistoricoAcoesTable.timestamp] = getCurrentTimestamp()
                it[HistoricoAcoesTable.localizacao] = localizacao
            }
        }
    }
    
    fun salvarEstadoJogo(
        personagemId: Long,
        nome: String,
        estadoCompleto: String,
        localizacao: String,
        nivel: Int
    ): Long {
        return transaction {
            SaveStatesTable.insert {
                it[SaveStatesTable.personagemId] = personagemId
                it[SaveStatesTable.nome] = nome
                it[SaveStatesTable.estadoCompleto] = estadoCompleto
                it[SaveStatesTable.timestamp] = getCurrentTimestamp()
                it[SaveStatesTable.localizacao] = localizacao
                it[SaveStatesTable.nivel] = nivel
            } get SaveStatesTable.id
        }
    }
}

