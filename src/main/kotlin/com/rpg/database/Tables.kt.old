package com.rpg.database

import org.jetbrains.exposed.sql.Table

object PersonagensTable : Table("personagens") {
    val id = long("id").autoIncrement()
    val nome = varchar("nome", 100)
    val raca = varchar("raca", 50)
    val classe = varchar("classe", 50)
    val nivel = integer("nivel")
    val forca = integer("forca")
    val destreza = integer("destreza")
    val constituicao = integer("constituicao")
    val inteligencia = integer("inteligencia")
    val sabedoria = integer("sabedoria")
    val carisma = integer("carisma")
    val pontosVida = integer("pontos_vida")
    val pontosVidaMaximos = integer("pontos_vida_maximos")
    val experiencia = integer("experiencia")
    val dinheiro = integer("dinheiro")
    
    override val primaryKey = PrimaryKey(id)
}

object ItensTable : Table("itens") {
    val id = long("id").autoIncrement()
    val nome = varchar("nome", 100)
    val tipo = varchar("tipo", 50) // ARMA, ARMADURA, POCAO, ESCUDO, ANEL, AMULETO
    val dano = varchar("dano", 20) // Ex: "1d8", "2d6"
    val bonus = integer("bonus") // Bonus de ataque/defesa
    val valor = integer("valor") // Valor em ouro
    val peso = integer("peso") // Peso em unidades
    val descricao = text("descricao")
    
    override val primaryKey = PrimaryKey(id)
}

object InventarioTable : Table("inventario") {
    val id = long("id").autoIncrement()
    val personagemId = long("personagem_id").references(PersonagensTable.id)
    val itemId = long("item_id").references(ItensTable.id)
    val quantidade = integer("quantidade")
    val equipado = bool("equipado")
    
    override val primaryKey = PrimaryKey(id)
}

object MonstrosTable : Table("monstros") {
    val id = long("id").autoIncrement()
    val nome = varchar("nome", 100)
    val nivel = integer("nivel")
    val pontosVida = integer("pontos_vida")
    val ca = integer("ca") // Classe de Armadura
    val baseAtaque = integer("base_ataque")
    val dano = varchar("dano", 20) // Ex: "1d8", "2d6+2"
    val xpRecompensa = integer("xp_recompensa")
    val ouroMin = integer("ouro_min")
    val ouroMax = integer("ouro_max")
    val descricao = text("descricao")
    
    override val primaryKey = PrimaryKey(id)
}

object CombateHistoricoTable : Table("combate_historico") {
    val id = long("id").autoIncrement()
    val personagemId = long("personagem_id").references(PersonagensTable.id)
    val monstroNome = varchar("monstro_nome", 100)
    val resultado = varchar("resultado", 20) // VITORIA, DERROTA, FUGA
    val xpGanho = integer("xp_ganho")
    val ouroGanho = integer("ouro_ganho")
    val data = varchar("data", 50)
    
    override val primaryKey = PrimaryKey(id)
}

object LootTable : Table("loot") {
    val id = long("id").autoIncrement()
    val monstroId = long("monstro_id").references(MonstrosTable.id)
    val itemId = long("item_id").references(ItensTable.id)
    val chance = integer("chance") // Porcentagem de drop (0-100)
    
    override val primaryKey = PrimaryKey(id)
}

