package com.rpg.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object DatabaseManager {
    private const val DB_PATH = "rpg_game.db"
    private lateinit var database: Database
    
    fun init() {
        database = Database.connect("jdbc:sqlite:$DB_PATH", "org.sqlite.JDBC")
        
        transaction {
            SchemaUtils.create(
                PersonagensTable,
                ItensTable,
                InventarioTable,
                MonstrosTable,
                CombateHistoricoTable,
                LootTable
            )
        }
        
        // Inicializar dados padrão
        inicializarDadosPadrao()
    }
    
    private fun inicializarDadosPadrao() {
        transaction {
            // Verificar se já existem itens
            if (ItensTable.selectAll().count() == 0L) {
                inserirItensPadrao()
            }
            
            // Verificar se já existem monstros
            if (MonstrosTable.selectAll().count() == 0L) {
                inserirMonstrosPadrao()
            }
        }
    }
    
    private fun inserirItensPadrao() {
        // Armas
        ItensTable.insert {
            it[nome] = "Espada Curta"
            it[tipo] = "ARMA"
            it[dano] = "1d6"
            it[bonus] = 0
            it[valor] = 10
            it[peso] = 3
            it[descricao] = "Uma espada curta básica"
        }
        
        ItensTable.insert {
            it[nome] = "Espada Longa"
            it[tipo] = "ARMA"
            it[dano] = "1d8"
            it[bonus] = 0
            it[valor] = 15
            it[peso] = 4
            it[descricao] = "Uma espada longa de qualidade"
        }
        
        ItensTable.insert {
            it[nome] = "Machado de Batalha"
            it[tipo] = "ARMA"
            it[dano] = "1d10"
            it[bonus] = 0
            it[valor] = 20
            it[peso] = 7
            it[descricao] = "Um machado pesado e poderoso"
        }
        
        ItensTable.insert {
            it[nome] = "Arco Longo"
            it[tipo] = "ARMA"
            it[dano] = "1d8"
            it[bonus] = 0
            it[valor] = 75
            it[peso] = 3
            it[descricao] = "Arco para ataques à distância"
        }
        
        ItensTable.insert {
            it[nome] = "Adaga"
            it[tipo] = "ARMA"
            it[dano] = "1d4"
            it[bonus] = 0
            it[valor] = 2
            it[peso] = 1
            it[descricao] = "Uma pequena adaga afiada"
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
        }
        
        ItensTable.insert {
            it[nome] = "Cota de Malha"
            it[tipo] = "ARMADURA"
            it[dano] = ""
            it[bonus] = 5
            it[valor] = 75
            it[peso] = 40
            it[descricao] = "Armadura média de anéis entrelaçados"
        }
        
        ItensTable.insert {
            it[nome] = "Armadura de Placas"
            it[tipo] = "ARMADURA"
            it[dano] = ""
            it[bonus] = 8
            it[valor] = 400
            it[peso] = 50
            it[descricao] = "Armadura pesada de placas de metal"
        }
        
        ItensTable.insert {
            it[nome] = "Escudo"
            it[tipo] = "ESCUDO"
            it[dano] = ""
            it[bonus] = 1
            it[valor] = 10
            it[peso] = 10
            it[descricao] = "Escudo de madeira reforçado"
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
        }
        
        ItensTable.insert {
            it[nome] = "Poção de Cura"
            it[tipo] = "POCAO"
            it[dano] = "2d8"
            it[bonus] = 0
            it[valor] = 100
            it[peso] = 1
            it[descricao] = "Restaura 2d8 pontos de vida"
        }
        
        ItensTable.insert {
            it[nome] = "Poção de Cura Maior"
            it[tipo] = "POCAO"
            it[dano] = "4d8"
            it[bonus] = 0
            it[valor] = 200
            it[peso] = 1
            it[descricao] = "Restaura 4d8 pontos de vida"
        }
        
        // Itens especiais
        ItensTable.insert {
            it[nome] = "Anel de Proteção +1"
            it[tipo] = "ANEL"
            it[dano] = ""
            it[bonus] = 1
            it[valor] = 500
            it[peso] = 0
            it[descricao] = "Aumenta CA em +1"
        }
        
        ItensTable.insert {
            it[nome] = "Amuleto da Sorte"
            it[tipo] = "AMULETO"
            it[dano] = ""
            it[bonus] = 1
            it[valor] = 300
            it[peso] = 0
            it[descricao] = "Concede +1 em todas as jogadas de proteção"
        }
    }
    
    private fun inserirMonstrosPadrao() {
        // Monstros fracos (Nível 1-2)
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
        }
        
        // Monstros médios (Nível 3-5)
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
        }
        
        // Monstros fortes (Nível 6-8)
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
        }
        
        // Boss final (Nível 10)
        MonstrosTable.insert {
            it[nome] = "Dragão Vermelho"
            it[nivel] = 10
            it[pontosVida] = 100
            it[ca] = 20
            it[baseAtaque] = 10
            it[dano] = "4d8"
            it[xpRecompensa] = 2000
            it[ouroMin] = 500
            it[ouroMax] = 1000
            it[descricao] = "Dragão antigo de escamas vermelhas"
        }
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
        dinheiro: Int
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
            } get PersonagensTable.id
        }
    }
    
    fun carregarPersonagem(nome: String): PersonagemData? {
        return transaction {
            PersonagensTable.select { PersonagensTable.nome eq nome }
                .firstOrNull()?.let {
                    PersonagemData(
                        id = it[PersonagensTable.id],
                        nome = it[PersonagensTable.nome],
                        raca = it[PersonagensTable.raca],
                        classe = it[PersonagensTable.classe],
                        nivel = it[PersonagensTable.nivel],
                        forca = it[PersonagensTable.forca],
                        destreza = it[PersonagensTable.destreza],
                        constituicao = it[PersonagensTable.constituicao],
                        inteligencia = it[PersonagensTable.inteligencia],
                        sabedoria = it[PersonagensTable.sabedoria],
                        carisma = it[PersonagensTable.carisma],
                        pontosVida = it[PersonagensTable.pontosVida],
                        pontosVidaMaximos = it[PersonagensTable.pontosVidaMaximos],
                        experiencia = it[PersonagensTable.experiencia],
                        dinheiro = it[PersonagensTable.dinheiro]
                    )
                }
        }
    }
    
    fun atualizarPersonagem(
        nome: String,
        nivel: Int,
        pontosVida: Int,
        pontosVidaMaximos: Int,
        experiencia: Int,
        dinheiro: Int
    ) {
        transaction {
            PersonagensTable.update({ PersonagensTable.nome eq nome }) {
                it[PersonagensTable.nivel] = nivel
                it[PersonagensTable.pontosVida] = pontosVida
                it[PersonagensTable.pontosVidaMaximos] = pontosVidaMaximos
                it[PersonagensTable.experiencia] = experiencia
                it[PersonagensTable.dinheiro] = dinheiro
            }
        }
    }
    
    fun listarPersonagens(): List<PersonagemData> {
        return transaction {
            PersonagensTable.selectAll().map {
                PersonagemData(
                    id = it[PersonagensTable.id],
                    nome = it[PersonagensTable.nome],
                    raca = it[PersonagensTable.raca],
                    classe = it[PersonagensTable.classe],
                    nivel = it[PersonagensTable.nivel],
                    forca = it[PersonagensTable.forca],
                    destreza = it[PersonagensTable.destreza],
                    constituicao = it[PersonagensTable.constituicao],
                    inteligencia = it[PersonagensTable.inteligencia],
                    sabedoria = it[PersonagensTable.sabedoria],
                    carisma = it[PersonagensTable.carisma],
                    pontosVida = it[PersonagensTable.pontosVida],
                    pontosVidaMaximos = it[PersonagensTable.pontosVidaMaximos],
                    experiencia = it[PersonagensTable.experiencia],
                    dinheiro = it[PersonagensTable.dinheiro]
                )
            }
        }
    }
    
    fun obterMonstrosPorNivel(nivelMin: Int, nivelMax: Int): List<MonstroData> {
        return transaction {
            MonstrosTable.select { 
                (MonstrosTable.nivel greaterEq nivelMin) and (MonstrosTable.nivel lessEq nivelMax)
            }.map {
                MonstroData(
                    id = it[MonstrosTable.id],
                    nome = it[MonstrosTable.nome],
                    nivel = it[MonstrosTable.nivel],
                    pontosVida = it[MonstrosTable.pontosVida],
                    ca = it[MonstrosTable.ca],
                    baseAtaque = it[MonstrosTable.baseAtaque],
                    dano = it[MonstrosTable.dano],
                    xpRecompensa = it[MonstrosTable.xpRecompensa],
                    ouroMin = it[MonstrosTable.ouroMin],
                    ouroMax = it[MonstrosTable.ouroMax],
                    descricao = it[MonstrosTable.descricao]
                )
            }
        }
    }
    
    fun obterItensPorTipo(tipo: String): List<ItemData> {
        return transaction {
            ItensTable.select { ItensTable.tipo eq tipo }.map {
                ItemData(
                    id = it[ItensTable.id],
                    nome = it[ItensTable.nome],
                    tipo = it[ItensTable.tipo],
                    dano = it[ItensTable.dano],
                    bonus = it[ItensTable.bonus],
                    valor = it[ItensTable.valor],
                    peso = it[ItensTable.peso],
                    descricao = it[ItensTable.descricao]
                )
            }
        }
    }
    
    fun obterTodosItens(): List<ItemData> {
        return transaction {
            ItensTable.selectAll().map {
                ItemData(
                    id = it[ItensTable.id],
                    nome = it[ItensTable.nome],
                    tipo = it[ItensTable.tipo],
                    dano = it[ItensTable.dano],
                    bonus = it[ItensTable.bonus],
                    valor = it[ItensTable.valor],
                    peso = it[ItensTable.peso],
                    descricao = it[ItensTable.descricao]
                )
            }
        }
    }
}

data class PersonagemData(
    val id: Long,
    val nome: String,
    val raca: String,
    val classe: String,
    val nivel: Int,
    val forca: Int,
    val destreza: Int,
    val constituicao: Int,
    val inteligencia: Int,
    val sabedoria: Int,
    val carisma: Int,
    val pontosVida: Int,
    val pontosVidaMaximos: Int,
    val experiencia: Int,
    val dinheiro: Int
)

data class MonstroData(
    val id: Long,
    val nome: String,
    val nivel: Int,
    val pontosVida: Int,
    val ca: Int,
    val baseAtaque: Int,
    val dano: String,
    val xpRecompensa: Int,
    val ouroMin: Int,
    val ouroMax: Int,
    val descricao: String
)

data class ItemData(
    val id: Long,
    val nome: String,
    val tipo: String,
    val dano: String,
    val bonus: Int,
    val valor: Int,
    val peso: Int,
    val descricao: String
)

