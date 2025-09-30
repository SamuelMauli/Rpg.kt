package com.rpg.items

import kotlin.random.Random

// ... (Enums permanecem iguais) ...
enum class TipoArma {
    CORPO_A_CORPO,
    A_DISTANCIA,
    ARREMESSO
}

enum class TamanhoArma {
    PEQUENA,
    MEDIA,
    GRANDE
}

enum class GrupoArma {
    CORTANTES,
    PERFURANTES,
    IMPACTANTES,
    DISPAROS,
    HASTES,
    ARREMESSO
}


abstract class Arma : ItemEquipavel() {
    abstract fun getDano(): String
    abstract fun getTamanho(): TamanhoArma
    abstract fun getGrupo(): GrupoArma
    abstract fun getTipoArma(): TipoArma
    abstract fun getAlcance(): Int // em metros, 0 para corpo a corpo
    abstract fun isArmaDeGuerra(): Boolean
    
    override fun getTipo(): TipoItem = TipoItem.ARMA
    override fun getBonusCA(): Int = 0 // Armas não dão bônus de CA
    
    fun rolarDano(critico: Boolean = false, bonusDano: Int = 0): Int {
        val danoBase = when (getDano()) {
            "1d4" -> Random.nextInt(1, 5)
            "1d6" -> Random.nextInt(1, 7)
            "1d8" -> Random.nextInt(1, 9)
            "1d10" -> Random.nextInt(1, 11)
            "1d12" -> Random.nextInt(1, 13)
            "2d4" -> Random.nextInt(2, 9)
            "2d6" -> Random.nextInt(2, 13)
            else -> 1
        }
        
        val danoTotal = danoBase + getBonusDano() + bonusDano
        return if (critico) danoTotal * 2 else danoTotal
    }
    
    fun isArmaPermitidaParaClasse(classe: String): Boolean {
        return when (classe.lowercase()) {
            "guerreiro" -> true // Pode usar todas as armas
            "ladrao", "ladrão" -> getTamanho() != TamanhoArma.GRANDE
            "clerigo", "clérigo" -> getGrupo() == GrupoArma.IMPACTANTES
            "mago" -> getTamanho() == TamanhoArma.PEQUENA
            else -> false
        }
    }
}

// ... (o restante das classes de Armas permanece igual, elas já estavam corretas) ...
// Armas Corpo a Corpo
class EspadaCurta : Arma() {
    override fun getNome(): String = "Espada Curta"
    override fun getDescricao(): String = "Uma lâmina curta e versátil, ideal para combate próximo."
    override fun getDano(): String = "1d6"
    override fun getTamanho(): TamanhoArma = TamanhoArma.PEQUENA
    override fun getGrupo(): GrupoArma = GrupoArma.CORTANTES
    override fun getTipoArma(): TipoArma = TipoArma.CORPO_A_CORPO
    override fun getAlcance(): Int = 0
    override fun isArmaDeGuerra(): Boolean = true
    override fun getPeso(): Int = 3
    override fun getValor(): Int = 10
    override fun getRaridade(): RaridadeItem = RaridadeItem.COMUM
    override fun getBonusAtaque(): Int = 0
    override fun getBonusDano(): Int = 0
    override fun getRestricoes(): List<String> = emptyList()
}

class EspadaLonga : Arma() {
    override fun getNome(): String = "Espada Longa"
    override fun getDescricao(): String = "Uma espada de lâmina longa, a arma favorita dos guerreiros."
    override fun getDano(): String = "1d8"
    override fun getTamanho(): TamanhoArma = TamanhoArma.MEDIA
    override fun getGrupo(): GrupoArma = GrupoArma.CORTANTES
    override fun getTipoArma(): TipoArma = TipoArma.CORPO_A_CORPO
    override fun getAlcance(): Int = 0
    override fun isArmaDeGuerra(): Boolean = true
    override fun getPeso(): Int = 4
    override fun getValor(): Int = 15
    override fun getRaridade(): RaridadeItem = RaridadeItem.COMUM
    override fun getBonusAtaque(): Int = 0
    override fun getBonusDano(): Int = 0
    override fun getRestricoes(): List<String> = listOf("Clérigo não pode usar")
}

class Adaga : Arma() {
    override fun getNome(): String = "Adaga"
    override fun getDescricao(): String = "Uma lâmina pequena e afiada, fácil de esconder."
    override fun getDano(): String = "1d4"
    override fun getTamanho(): TamanhoArma = TamanhoArma.PEQUENA
    override fun getGrupo(): GrupoArma = GrupoArma.PERFURANTES
    override fun getTipoArma(): TipoArma = TipoArma.CORPO_A_CORPO
    override fun getAlcance(): Int = 0
    override fun isArmaDeGuerra(): Boolean = false
    override fun getPeso(): Int = 1
    override fun getValor(): Int = 2
    override fun getRaridade(): RaridadeItem = RaridadeItem.COMUM
    override fun getBonusAtaque(): Int = 0
    override fun getBonusDano(): Int = 0
    override fun getRestricoes(): List<String> = listOf("Clérigo não pode usar")
}

class Maca : Arma() {
    override fun getNome(): String = "Maça"
    override fun getDescricao(): String = "Uma arma impactante pesada, favorita dos clérigos."
    override fun getDano(): String = "1d6"
    override fun getTamanho(): TamanhoArma = TamanhoArma.MEDIA
    override fun getGrupo(): GrupoArma = GrupoArma.IMPACTANTES
    override fun getTipoArma(): TipoArma = TipoArma.CORPO_A_CORPO
    override fun getAlcance(): Int = 0
    override fun isArmaDeGuerra(): Boolean = true
    override fun getPeso(): Int = 4
    override fun getValor(): Int = 5
    override fun getRaridade(): RaridadeItem = RaridadeItem.COMUM
    override fun getBonusAtaque(): Int = 0
    override fun getBonusDano(): Int = 0
    override fun getRestricoes(): List<String> = emptyList()
}

class MarteloDeGuerra : Arma() {
    override fun getNome(): String = "Martelo de Guerra"
    override fun getDescricao(): String = "Um martelo pesado e devastador em combate."
    override fun getDano(): String = "1d8"
    override fun getTamanho(): TamanhoArma = TamanhoArma.GRANDE
    override fun getGrupo(): GrupoArma = GrupoArma.IMPACTANTES
    override fun getTipoArma(): TipoArma = TipoArma.CORPO_A_CORPO
    override fun getAlcance(): Int = 0
    override fun isArmaDeGuerra(): Boolean = true
    override fun getPeso(): Int = 6
    override fun getValor(): Int = 12
    override fun getRaridade(): RaridadeItem = RaridadeItem.COMUM
    override fun getBonusAtaque(): Int = 0
    override fun getBonusDano(): Int = 0
    override fun getRestricoes(): List<String> = listOf("Ladrão tem ataques difíceis")
}

class Cajado : Arma() {
    override fun getNome(): String = "Cajado"
    override fun getDescricao(): String = "Um bastão longo, útil tanto como arma quanto como foco mágico."
    override fun getDano(): String = "1d6"
    override fun getTamanho(): TamanhoArma = TamanhoArma.MEDIA
    override fun getGrupo(): GrupoArma = GrupoArma.IMPACTANTES
    override fun getTipoArma(): TipoArma = TipoArma.CORPO_A_CORPO
    override fun getAlcance(): Int = 0
    override fun isArmaDeGuerra(): Boolean = false
    override fun getPeso(): Int = 4
    override fun getValor(): Int = 0
    override fun getRaridade(): RaridadeItem = RaridadeItem.COMUM
    override fun getBonusAtaque(): Int = 0
    override fun getBonusDano(): Int = 0
    override fun getRestricoes(): List<String> = emptyList()
}

// Armas à Distância
class ArcoLongo : Arma() {
    override fun getNome(): String = "Arco Longo"
    override fun getDescricao(): String = "Um arco poderoso com grande alcance."
    override fun getDano(): String = "1d8"
    override fun getTamanho(): TamanhoArma = TamanhoArma.GRANDE
    override fun getGrupo(): GrupoArma = GrupoArma.DISPAROS
    override fun getTipoArma(): TipoArma = TipoArma.A_DISTANCIA
    override fun getAlcance(): Int = 150
    override fun isArmaDeGuerra(): Boolean = true
    override fun getPeso(): Int = 3
    override fun getValor(): Int = 75
    override fun getRaridade(): RaridadeItem = RaridadeItem.COMUM
    override fun getBonusAtaque(): Int = 0
    override fun getBonusDano(): Int = 0
    override fun getRestricoes(): List<String> = listOf("Clérigo não pode usar", "Ladrão tem ataques difíceis")
}

class BestaPesada : Arma() {
    override fun getNome(): String = "Besta Pesada"
    override fun getDescricao(): String = "Uma besta poderosa que dispara virotes com força."
    override fun getDano(): String = "1d10"
    override fun getTamanho(): TamanhoArma = TamanhoArma.GRANDE
    override fun getGrupo(): GrupoArma = GrupoArma.DISPAROS
    override fun getTipoArma(): TipoArma = TipoArma.A_DISTANCIA
    override fun getAlcance(): Int = 120
    override fun isArmaDeGuerra(): Boolean = true
    override fun getPeso(): Int = 8
    override fun getValor(): Int = 50
    override fun getRaridade(): RaridadeItem = RaridadeItem.COMUM
    override fun getBonusAtaque(): Int = 0
    override fun getBonusDano(): Int = 0
    override fun getRestricoes(): List<String> = listOf("Clérigo não pode usar", "Ladrão tem ataques difíceis")
}

// Armas de Arremesso
class LancaArremesso : Arma() {
    override fun getNome(): String = "Lança de Arremesso"
    override fun getDescricao(): String = "Uma lança leve ideal para ser arremessada."
    override fun getDano(): String = "1d6"
    override fun getTamanho(): TamanhoArma = TamanhoArma.MEDIA
    override fun getGrupo(): GrupoArma = GrupoArma.ARREMESSO
    override fun getTipoArma(): TipoArma = TipoArma.ARREMESSO
    override fun getAlcance(): Int = 30
    override fun isArmaDeGuerra(): Boolean = true
    override fun getPeso(): Int = 2
    override fun getValor(): Int = 1
    override fun getRaridade(): RaridadeItem = RaridadeItem.COMUM
    override fun getBonusAtaque(): Int = 0
    override fun getBonusDano(): Int = 0
    override fun getRestricoes(): List<String> = listOf("Clérigo não pode usar")
}

class MachadoArremesso : Arma() {
    override fun getNome(): String = "Machado de Arremesso"
    override fun getDescricao(): String = "Um machado pequeno e equilibrado para arremesso."
    override fun getDano(): String = "1d6"
    override fun getTamanho(): TamanhoArma = TamanhoArma.PEQUENA
    override fun getGrupo(): GrupoArma = GrupoArma.ARREMESSO
    override fun getTipoArma(): TipoArma = TipoArma.ARREMESSO
    override fun getAlcance(): Int = 20
    override fun isArmaDeGuerra(): Boolean = true
    override fun getPeso(): Int = 2
    override fun getValor(): Int = 1
    override fun getRaridade(): RaridadeItem = RaridadeItem.COMUM
    override fun getBonusAtaque(): Int = 0
    override fun getBonusDano(): Int = 0
    override fun getRestricoes(): List<String> = listOf("Clérigo não pode usar")
}