package com.rpg.items

import kotlin.random.Random

enum class TipoItem {
    ARMA,
    ARMADURA,
    ESCUDO,
    POCAO,
    ANEL,
    AMULETO,
    CONSUMIVEL,
    QUEST
}

interface Item {
    val nome: String
    val tipo: TipoItem
    val valor: Int
    val peso: Int
    val descricao: String
}

interface ItemEquipavel : Item {
    val bonus: Int
    val requisitos: Map<String, Int>
}

data class Arma(
    override val nome: String,
    val dano: String,
    override val bonus: Int,
    override val valor: Int,
    override val peso: Int,
    override val descricao: String,
    override val requisitos: Map<String, Int> = emptyMap(),
    val critico: Int = 20,
    val alcance: String = "Corpo a Corpo"
) : ItemEquipavel {
    override val tipo = TipoItem.ARMA
}

data class Armadura(
    override val nome: String,
    override val bonus: Int, // Bonus de CA
    override val valor: Int,
    override val peso: Int,
    override val descricao: String,
    override val requisitos: Map<String, Int> = emptyMap(),
    val penalidade: Int = 0 // Penalidade em testes de destreza
) : ItemEquipavel {
    override val tipo = TipoItem.ARMADURA
}

data class Escudo(
    override val nome: String,
    override val bonus: Int,
    override val valor: Int,
    override val peso: Int,
    override val descricao: String,
    override val requisitos: Map<String, Int> = emptyMap()
) : ItemEquipavel {
    override val tipo = TipoItem.ESCUDO
}

data class Pocao(
    override val nome: String,
    val efeito: String,
    val cura: String, // Ex: "2d8", "1d4+2"
    override val valor: Int,
    override val peso: Int,
    override val descricao: String
) : Item {
    override val tipo = TipoItem.POCAO
    
    fun usar(): Int {
        return rolarDado(cura)
    }
    
    private fun rolarDado(formula: String): Int {
        val partes = formula.split("+", "-")
        val dadoParte = partes[0].trim()
        val modificador = if (partes.size > 1) {
            val mod = partes[1].trim().toIntOrNull() ?: 0
            if (formula.contains("-")) -mod else mod
        } else 0
        
        val (quantidade, lados) = dadoParte.split("d").map { it.toInt() }
        
        var total = 0
        repeat(quantidade) {
            total += Random.nextInt(1, lados + 1)
        }
        
        return total + modificador
    }
}

data class Acessorio(
    override val nome: String,
    override val tipo: TipoItem,
    override val bonus: Int,
    val atributoAfetado: String, // "CA", "JP", "ATAQUE", "DANO"
    override val valor: Int,
    override val peso: Int,
    override val descricao: String,
    override val requisitos: Map<String, Int> = emptyMap()
) : ItemEquipavel

class Inventario {
    private val itens = mutableMapOf<Item, Int>()
    private val equipados = mutableMapOf<TipoItem, ItemEquipavel>()
    
    fun adicionarItem(item: Item, quantidade: Int = 1) {
        itens[item] = itens.getOrDefault(item, 0) + quantidade
    }
    
    fun removerItem(item: Item, quantidade: Int = 1): Boolean {
        val quantidadeAtual = itens[item] ?: return false
        
        return if (quantidadeAtual >= quantidade) {
            if (quantidadeAtual == quantidade) {
                itens.remove(item)
            } else {
                itens[item] = quantidadeAtual - quantidade
            }
            true
        } else {
            false
        }
    }
    
    fun equipar(item: ItemEquipavel): Boolean {
        if (!itens.containsKey(item)) return false
        
        // Desequipar item anterior do mesmo tipo
        equipados[item.tipo]?.let { itemAnterior ->
            desequipar(itemAnterior)
        }
        
        equipados[item.tipo] = item
        return true
    }
    
    fun desequipar(item: ItemEquipavel): Boolean {
        return equipados.remove(item.tipo) != null
    }
    
    fun getItemEquipado(tipo: TipoItem): ItemEquipavel? {
        return equipados[tipo]
    }
    
    fun getTodosItens(): Map<Item, Int> {
        return itens.toMap()
    }
    
    fun getItensEquipados(): Map<TipoItem, ItemEquipavel> {
        return equipados.toMap()
    }
    
    fun temItem(item: Item): Boolean {
        return itens.containsKey(item)
    }
    
    fun getQuantidade(item: Item): Int {
        return itens[item] ?: 0
    }
    
    fun getPesoTotal(): Int {
        var peso = 0
        itens.forEach { (item, quantidade) ->
            peso += item.peso * quantidade
        }
        return peso
    }
    
    fun getValorTotal(): Int {
        var valor = 0
        itens.forEach { (item, quantidade) ->
            valor += item.valor * quantidade
        }
        return valor
    }
    
    fun listarItens(): String {
        return buildString {
            appendLine("═══ INVENTÁRIO ═══")
            appendLine()
            
            if (itens.isEmpty()) {
                appendLine("Inventário vazio")
            } else {
                itens.forEach { (item, quantidade) ->
                    val equipado = if (equipados.values.contains(item)) " [EQUIPADO]" else ""
                    appendLine("• ${item.nome} x$quantidade$equipado")
                    appendLine("  ${item.descricao}")
                    appendLine("  Valor: ${item.valor} PO | Peso: ${item.peso}")
                    
                    when (item) {
                        is Arma -> appendLine("  Dano: ${item.dano} | Bonus: +${item.bonus}")
                        is Armadura -> appendLine("  CA: +${item.bonus} | Penalidade: ${item.penalidade}")
                        is Escudo -> appendLine("  CA: +${item.bonus}")
                        is Pocao -> appendLine("  Efeito: ${item.efeito} (${item.valor})")
                        is Acessorio -> appendLine("  ${item.atributoAfetado}: +${item.bonus}")
                    }
                    appendLine()
                }
            }
            
            appendLine("Peso total: ${getPesoTotal()}")
            appendLine("Valor total: ${getValorTotal()} PO")
        }
    }
}

class GerenciadorLoot {
    
    fun gerarLootAleatorio(nivelMonstro: Int): List<Item> {
        val itens = mutableListOf<Item>()
        
        // Chance de drop baseada no nível
        val chanceDrop = when {
            nivelMonstro <= 2 -> 30
            nivelMonstro <= 5 -> 50
            nivelMonstro <= 8 -> 70
            else -> 90
        }
        
        if (Random.nextInt(1, 101) <= chanceDrop) {
            itens.add(gerarItemPorNivel(nivelMonstro))
        }
        
        // Chance adicional de poção
        if (Random.nextInt(1, 101) <= 40) {
            itens.add(gerarPocao(nivelMonstro))
        }
        
        return itens
    }
    
    private fun gerarItemPorNivel(nivel: Int): Item {
        return when (nivel) {
            1 -> when (Random.nextInt(1, 4)) {
                1 -> Arma("Adaga", "1d4", 0, 2, 1, "Uma pequena adaga afiada")
                2 -> Armadura("Armadura de Couro", 2, 5, 15, "Armadura leve de couro")
                else -> Escudo("Escudo Pequeno", 1, 5, 5, "Escudo leve de madeira")
            }
            2, 3 -> when (Random.nextInt(1, 4)) {
                1 -> Arma("Espada Curta", "1d6", 0, 10, 3, "Uma espada curta básica")
                2 -> Armadura("Couro Batido", 3, 10, 20, "Armadura de couro reforçado")
                else -> Escudo("Escudo Médio", 1, 10, 10, "Escudo de madeira reforçado")
            }
            4, 5 -> when (Random.nextInt(1, 5)) {
                1 -> Arma("Espada Longa", "1d8", 1, 50, 4, "Uma espada longa de qualidade", critico = 19)
                2 -> Arma("Machado de Batalha", "1d10", 0, 20, 7, "Um machado pesado e poderoso")
                3 -> Armadura("Cota de Malha", 5, 75, 40, "Armadura média de anéis entrelaçados")
                else -> Acessorio("Anel de Proteção +1", TipoItem.ANEL, 1, "CA", 500, 0, "Aumenta CA em +1")
            }
            6, 7, 8 -> when (Random.nextInt(1, 5)) {
                1 -> Arma("Espada Longa +1", "1d8", 2, 200, 4, "Espada longa mágica", critico = 18)
                2 -> Arma("Arco Longo +1", "1d8", 1, 150, 3, "Arco longo de qualidade superior", alcance = "30m")
                3 -> Armadura("Armadura de Placas", 8, 400, 50, "Armadura pesada de placas de metal")
                else -> Acessorio("Amuleto da Sorte", TipoItem.AMULETO, 2, "JP", 600, 0, "Concede +2 em jogadas de proteção")
            }
            else -> when (Random.nextInt(1, 4)) {
                1 -> Arma("Espada Longa +2", "1d8", 3, 500, 4, "Espada longa mágica poderosa", critico = 17)
                2 -> Armadura("Armadura de Placas +1", 9, 800, 50, "Armadura de placas encantada")
                else -> Acessorio("Anel de Poder", TipoItem.ANEL, 3, "ATAQUE", 1000, 0, "Concede +3 em ataques")
            }
        }
    }
    
    private fun gerarPocao(nivel: Int): Pocao {
        return when {
            nivel <= 3 -> Pocao("Poção de Cura Menor", "Cura", "1d8", 50, 1, "Restaura 1d8 pontos de vida")
            nivel <= 6 -> Pocao("Poção de Cura", "Cura", "2d8", 100, 1, "Restaura 2d8 pontos de vida")
            else -> Pocao("Poção de Cura Maior", "Cura", "4d8", 200, 1, "Restaura 4d8 pontos de vida")
        }
    }
}

