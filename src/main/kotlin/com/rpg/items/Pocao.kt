package com.rpg.items

import kotlin.random.Random

abstract class Pocao : ItemUsavel() {
    override fun getTipo(): TipoItem = TipoItem.POCAO
    override fun getUsosMaximos(): Int = 1 // Poções são consumidas em um uso
    
    protected var usada = false
    
    override fun getUsosRestantes(): Int = if (usada) 0 else 1
    
    override fun consumirUso() {
        usada = true
    }
}

class PocaoDeCura : Pocao() {
    private val cura = Random.nextInt(1, 9) + 1 // 1d8+1
    
    override fun getNome(): String = "Poção de Cura"
    override fun getDescricao(): String = "Uma poção mágica que restaura pontos de vida."
    override fun getEfeito(): String = "Restaura $cura pontos de vida."
    override fun getPeso(): Int = 0
    override fun getValor(): Int = 50
    override fun getRaridade(): RaridadeItem = RaridadeItem.COMUM
    override fun isMagico(): Boolean = true
    
    fun getCura(): Int = cura
}

class PocaoDeCuraGrande : Pocao() {
    private val cura = Random.nextInt(2, 17) + 2 // 2d8+2
    
    override fun getNome(): String = "Poção de Cura Grande"
    override fun getDescricao(): String = "Uma poção mágica poderosa que restaura muitos pontos de vida."
    override fun getEfeito(): String = "Restaura $cura pontos de vida."
    override fun getPeso(): Int = 0
    override fun getValor(): Int = 200
    override fun getRaridade(): RaridadeItem = RaridadeItem.INCOMUM
    override fun isMagico(): Boolean = true
    
    fun getCura(): Int = cura
}

class PocaoDeVelocidade : Pocao() {
    override fun getNome(): String = "Poção de Velocidade"
    override fun getDescricao(): String = "Aumenta a velocidade de movimento e ataques por 10 rodadas."
    override fun getEfeito(): String = "Dobra a velocidade de movimento e concede um ataque extra por 10 rodadas."
    override fun getPeso(): Int = 0
    override fun getValor(): Int = 300
    override fun getRaridade(): RaridadeItem = RaridadeItem.RARO
    override fun isMagico(): Boolean = true
}

class PocaoDeInvisibilidade : Pocao() {
    override fun getNome(): String = "Poção de Invisibilidade"
    override fun getDescricao(): String = "Torna o usuário invisível por até 1 hora."
    override fun getEfeito(): String = "Você se torna invisível por até 1 hora ou até atacar."
    override fun getPeso(): Int = 0
    override fun getValor(): Int = 500
    override fun getRaridade(): RaridadeItem = RaridadeItem.RARO
    override fun isMagico(): Boolean = true
}

class PocaoDeForça : Pocao() {
    override fun getNome(): String = "Poção de Força"
    override fun getDescricao(): String = "Aumenta temporariamente a força do usuário."
    override fun getEfeito(): String = "Sua Força aumenta para 18 por 1 hora."
    override fun getPeso(): Int = 0
    override fun getValor(): Int = 400
    override fun getRaridade(): RaridadeItem = RaridadeItem.INCOMUM
    override fun isMagico(): Boolean = true
}

class Antidoto : Pocao() {
    override fun getNome(): String = "Antídoto"
    override fun getDescricao(): String = "Neutraliza venenos no organismo."
    override fun getEfeito(): String = "Remove todos os efeitos de veneno."
    override fun getPeso(): Int = 0
    override fun getValor(): Int = 100
    override fun getRaridade(): RaridadeItem = RaridadeItem.COMUM
    override fun isMagico(): Boolean = false
}

// Pergaminhos
abstract class Pergaminho : ItemUsavel() {
    abstract fun getCirculoMagia(): Int
    abstract fun getNomeMagia(): String
    abstract fun getEfeitoMagia(): String
    
    override fun getTipo(): TipoItem = TipoItem.PERGAMINHO
    override fun getUsosMaximos(): Int = 1
    override fun isMagico(): Boolean = true
    
    protected var usado = false
    
    override fun getUsosRestantes(): Int = if (usado) 0 else 1
    
    override fun consumirUso() {
        usado = true
    }
    
    override fun getEfeito(): String = "Lança a magia ${getNomeMagia()}: ${getEfeitoMagia()}"
}

class PergaminhoMisseisMagicos : Pergaminho() {
    private val dano = Random.nextInt(1, 5) + 1 // 1d4+1
    
    override fun getNome(): String = "Pergaminho de Mísseis Mágicos"
    override fun getDescricao(): String = "Um pergaminho contendo a magia Mísseis Mágicos."
    override fun getCirculoMagia(): Int = 1
    override fun getNomeMagia(): String = "Mísseis Mágicos"
    override fun getEfeitoMagia(): String = "Dispara um míssil mágico que causa $dano pontos de dano."
    override fun getPeso(): Int = 0
    override fun getValor(): Int = 25
    override fun getRaridade(): RaridadeItem = RaridadeItem.COMUM
    
    fun getDano(): Int = dano
}

class PergaminhoBolaFogo : Pergaminho() {
    private val dano = Random.nextInt(6, 49) // 6d8
    
    override fun getNome(): String = "Pergaminho de Bola de Fogo"
    override fun getDescricao(): String = "Um pergaminho contendo a poderosa magia Bola de Fogo."
    override fun getCirculoMagia(): Int = 3
    override fun getNomeMagia(): String = "Bola de Fogo"
    override fun getEfeitoMagia(): String = "Cria uma explosão que causa $dano pontos de dano em área."
    override fun getPeso(): Int = 0
    override fun getValor(): Int = 150
    override fun getRaridade(): RaridadeItem = RaridadeItem.INCOMUM
    
    fun getDano(): Int = dano
}

class PergaminhoProtecao : Pergaminho() {
    override fun getNome(): String = "Pergaminho de Proteção"
    override fun getDescricao(): String = "Oferece proteção mágica contra criaturas específicas."
    override fun getCirculoMagia(): Int = 2
    override fun getNomeMagia(): String = "Proteção"
    override fun getEfeitoMagia(): String = "Cria uma barreira que repele criaturas específicas por 10 minutos."
    override fun getPeso(): Int = 0
    override fun getValor(): Int = 100
    override fun getRaridade(): RaridadeItem = RaridadeItem.COMUM
}

