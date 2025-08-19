package com.rpg.items

enum class TipoItem {
    ARMA,
    ARMADURA,
    ESCUDO,
    POCAO,
    PERGAMINHO,
    ITEM_MAGICO,
    TESOURO,
    EQUIPAMENTO_GERAL,
    COMPONENTE_MAGIA
}

enum class RaridadeItem {
    COMUM,
    INCOMUM,
    RARO,
    MUITO_RARO,
    LENDARIO
}

abstract class Item {
    abstract fun getNome(): String
    abstract fun getDescricao(): String
    abstract fun getTipo(): TipoItem
    abstract fun getPeso(): Int
    abstract fun getValor(): Int // em moedas de ouro
    abstract fun getRaridade(): RaridadeItem

    open fun isMagico(): Boolean = false
    open fun isUsavel(): Boolean = false
    open fun isEquipavel(): Boolean = false

    open fun usar(): String {
        return if (isUsavel()) {
            "Você usa ${getNome()}."
        } else {
            "${getNome()} não pode ser usado."
        }
    }

    open fun equipar(): String {
        return if (isEquipavel()) {
            "Você equipa ${getNome()}."
        } else {
            "${getNome()} não pode ser equipado."
        }
    }

    open fun desequipar(): String {
        return if (isEquipavel()) {
            "Você desequipa ${getNome()}."
        } else {
            "${getNome()} não estava equipado."
        }
    }

    fun getResumoItem(): String {
        return buildString {
            appendLine("=== ${getNome().uppercase()} ===")
            appendLine(getDescricao())
            appendLine()
            appendLine("Tipo: ${getTipo()}")
            appendLine("Peso: ${getPeso()}")
            appendLine("Valor: ${getValor()} PO")
            appendLine("Raridade: ${getRaridade()}")
            if (isMagico()) appendLine("Item Mágico: Sim")
            if (isUsavel()) appendLine("Usável: Sim")
            if (isEquipavel()) appendLine("Equipável: Sim")
        }
    }
}

abstract class ItemEquipavel : Item() {
    abstract fun getBonusCA(): Int
    abstract fun getBonusAtaque(): Int
    abstract fun getBonusDano(): Int
    abstract fun getRestricoes(): List<String>

    override fun isEquipavel(): Boolean = true

    open fun podeSerEquipadoPor(classe: String, raca: String): Boolean {
        return getRestricoes().none { restricao ->
            restricao.contains(classe, ignoreCase = true) ||
            restricao.contains(raca, ignoreCase = true)
        }
    }
}

abstract class ItemUsavel : Item() {
    abstract fun getEfeito(): String
    abstract fun getUsosMaximos(): Int
    abstract fun getUsosRestantes(): Int
    abstract fun consumirUso()

    override fun isUsavel(): Boolean = getUsosRestantes() > 0

    override fun usar(): String {
        return if (isUsavel()) {
            consumirUso()
            "Você usa ${getNome()}. ${getEfeito()}"
        } else {
            "${getNome()} não possui mais usos."
        }
    }
}

// ... (o restante do arquivo permanece igual) ...
class EquipamentoGeral(
    private val nome: String,
    private val descricao: String,
    private val peso: Int,
    private val valor: Int,
    private val raridade: RaridadeItem = RaridadeItem.COMUM
) : Item() {

    override fun getNome(): String = nome
    override fun getDescricao(): String = descricao
    override fun getTipo(): TipoItem = TipoItem.EQUIPAMENTO_GERAL
    override fun getPeso(): Int = peso
    override fun getValor(): Int = valor
    override fun getRaridade(): RaridadeItem = raridade
}

class Tesouro(
    private val nome: String,
    private val descricao: String,
    private val peso: Int,
    private val valor: Int,
    private val raridade: RaridadeItem = RaridadeItem.COMUM
) : Item() {

    override fun getNome(): String = nome
    override fun getDescricao(): String = descricao
    override fun getTipo(): TipoItem = TipoItem.TESOURO
    override fun getPeso(): Int = peso
    override fun getValor(): Int = valor
    override fun getRaridade(): RaridadeItem = raridade
}

class ComponenteMagia(
    private val nome: String,
    private val descricao: String,
    private val peso: Int = 0,
    private val valor: Int,
    private val raridade: RaridadeItem = RaridadeItem.COMUM
) : Item() {

    override fun getNome(): String = nome
    override fun getDescricao(): String = descricao
    override fun getTipo(): TipoItem = TipoItem.COMPONENTE_MAGIA
    override fun getPeso(): Int = peso
    override fun getValor(): Int = valor
    override fun getRaridade(): RaridadeItem = raridade
}