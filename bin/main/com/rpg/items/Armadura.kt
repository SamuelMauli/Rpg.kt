package com.rpg.items

enum class TipoArmadura {
    LEVE,
    MEDIA,
    PESADA
}

abstract class Armadura : ItemEquipavel() {
    abstract fun getTipoArmadura(): TipoArmadura
    abstract fun getCABase(): Int
    abstract fun getMaxModDestreza(): Int
    abstract fun getPenalidadeMovimento(): Int
    abstract fun isMetalica(): Boolean
    
    override fun getTipo(): TipoItem = TipoItem.ARMADURA
    override fun getBonusAtaque(): Int = 0 // Armaduras não dão bônus de ataque
    override fun getBonusDano(): Int = 0 // Armaduras não dão bônus de dano
    override fun getBonusCA(): Int = getCABase()
    
    fun isPermitidaParaClasse(classe: String): Boolean {
        return when (classe.lowercase()) {
            "guerreiro" -> true // Pode usar todas as armaduras
            "clerigo", "clérigo" -> true // Pode usar todas as armaduras
            "ladrao", "ladrão" -> getTipoArmadura() == TipoArmadura.LEVE
            "mago" -> false // Não pode usar armaduras
            else -> false
        }
    }
    
    fun isPermitidaParaDruida(): Boolean {
        return !isMetalica() // Druidas não podem usar armaduras metálicas
    }
}

// Armaduras Leves
class ArmaduraDeCouro : Armadura() {
    override fun getNome(): String = "Armadura de Couro"
    override fun getDescricao(): String = "Armadura feita de couro curtido, leve e flexível."
    override fun getTipoArmadura(): TipoArmadura = TipoArmadura.LEVE
    override fun getCABase(): Int = 2
    override fun getMaxModDestreza(): Int = Int.MAX_VALUE
    override fun getPenalidadeMovimento(): Int = 0
    override fun isMetalica(): Boolean = false
    override fun getPeso(): Int = 15
    override fun getValor(): Int = 5
    override fun getRaridade(): RaridadeItem = RaridadeItem.COMUM
    override fun getRestricoes(): List<String> = listOf("Mago não pode usar")
}

class ArmaduraDeCouroBatido : Armadura() {
    override fun getNome(): String = "Armadura de Couro Batido"
    override fun getDescricao(): String = "Couro endurecido e reforçado com tachas metálicas."
    override fun getTipoArmadura(): TipoArmadura = TipoArmadura.LEVE
    override fun getCABase(): Int = 3
    override fun getMaxModDestreza(): Int = Int.MAX_VALUE
    override fun getPenalidadeMovimento(): Int = 0
    override fun isMetalica(): Boolean = false
    override fun getPeso(): Int = 20
    override fun getValor(): Int = 20
    override fun getRaridade(): RaridadeItem = RaridadeItem.COMUM
    override fun getRestricoes(): List<String> = listOf("Mago não pode usar")
}

// Armaduras Médias
class CotaDeMalha : Armadura() {
    override fun getNome(): String = "Cota de Malha"
    override fun getDescricao(): String = "Armadura feita de anéis de metal entrelaçados."
    override fun getTipoArmadura(): TipoArmadura = TipoArmadura.MEDIA
    override fun getCABase(): Int = 5
    override fun getMaxModDestreza(): Int = 2
    override fun getPenalidadeMovimento(): Int = 0
    override fun isMetalica(): Boolean = true
    override fun getPeso(): Int = 40
    override fun getValor(): Int = 75
    override fun getRaridade(): RaridadeItem = RaridadeItem.COMUM
    override fun getRestricoes(): List<String> = listOf("Mago não pode usar", "Ladrão perde habilidades")
}

class CotaDePlacas : Armadura() {
    override fun getNome(): String = "Cota de Placas"
    override fun getDescricao(): String = "Cota de malha reforçada com placas metálicas."
    override fun getTipoArmadura(): TipoArmadura = TipoArmadura.MEDIA
    override fun getCABase(): Int = 7
    override fun getMaxModDestreza(): Int = 1
    override fun getPenalidadeMovimento(): Int = 0
    override fun isMetalica(): Boolean = true
    override fun getPeso(): Int = 50
    override fun getValor(): Int = 400
    override fun getRaridade(): RaridadeItem = RaridadeItem.INCOMUM
    override fun getRestricoes(): List<String> = listOf("Mago não pode usar", "Ladrão perde habilidades")
}

// Armaduras Pesadas
class ArmaduraDePlacas : Armadura() {
    override fun getNome(): String = "Armadura de Placas"
    override fun getDescricao(): String = "Armadura completa feita de placas de metal articuladas."
    override fun getTipoArmadura(): TipoArmadura = TipoArmadura.PESADA
    override fun getCABase(): Int = 8
    override fun getMaxModDestreza(): Int = 0
    override fun getPenalidadeMovimento(): Int = 3
    override fun isMetalica(): Boolean = true
    override fun getPeso(): Int = 70
    override fun getValor(): Int = 1500
    override fun getRaridade(): RaridadeItem = RaridadeItem.RARO
    override fun getRestricoes(): List<String> = listOf("Mago não pode usar", "Ladrão perde habilidades")
}

// Escudos
abstract class Escudo : ItemEquipavel() {
    abstract fun getBonusCAEscudo(): Int
    abstract fun podeUsarComArmasDuasMaos(): Boolean
    
    override fun getTipo(): TipoItem = TipoItem.ESCUDO
    override fun getBonusAtaque(): Int = 0
    override fun getBonusDano(): Int = 0
    override fun getBonusCA(): Int = getBonusCAEscudo()
    
    fun isPermitidoParaClasse(classe: String): Boolean {
        return when (classe.lowercase()) {
            "guerreiro" -> true
            "clerigo", "clérigo" -> true
            "ladrao", "ladrão" -> false // Escudos impedem habilidades de ladrão
            "mago" -> false // Magos não podem usar escudos
            else -> false
        }
    }
}

class EscudoPequeno : Escudo() {
    override fun getNome(): String = "Escudo Pequeno"
    override fun getDescricao(): String = "Um escudo leve e manobrável."
    override fun getBonusCAEscudo(): Int = 1
    override fun podeUsarComArmasDuasMaos(): Boolean = false
    override fun getPeso(): Int = 5
    override fun getValor(): Int = 3
    override fun getRaridade(): RaridadeItem = RaridadeItem.COMUM
    override fun getRestricoes(): List<String> = listOf("Ladrão perde habilidades", "Mago não pode usar")
}

class EscudoGrande : Escudo() {
    override fun getNome(): String = "Escudo Grande"
    override fun getDescricao(): String = "Um escudo grande que oferece proteção superior."
    override fun getBonusCAEscudo(): Int = 2
    override fun podeUsarComArmasDuasMaos(): Boolean = false
    override fun getPeso(): Int = 10
    override fun getValor(): Int = 7
    override fun getRaridade(): RaridadeItem = RaridadeItem.COMUM
    override fun getRestricoes(): List<String> = listOf("Ladrão perde habilidades", "Mago não pode usar")
}