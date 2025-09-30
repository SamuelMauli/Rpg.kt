package com.rpg.core

import com.rpg.items.*

data class SlotInventario(
    val item: Item,
    var quantidade: Int = 1
) {
    fun getValorTotal(): Int = item.getValor() * quantidade
    fun getPesoTotal(): Int = item.getPeso() * quantidade
}

class Inventario(
    private val capacidadeMaxima: Int
) {
    private val itens = mutableMapOf<String, SlotInventario>()
    private val equipamentos = mutableMapOf<String, ItemEquipavel>()
    
    fun adicionarItem(item: Item, quantidade: Int = 1): Boolean {
        val nomeItem = item.getNome()
        
        // Verificar se há espaço
        if (getPesoTotal() + (item.getPeso() * quantidade) > capacidadeMaxima) {
            return false
        }
        
        // Se o item já existe, aumentar quantidade
        if (itens.containsKey(nomeItem)) {
            val slotExistente = itens[nomeItem]!!
            itens[nomeItem] = slotExistente.copy(quantidade = slotExistente.quantidade + quantidade)
        } else {
            itens[nomeItem] = SlotInventario(item, quantidade)
        }
        
        return true
    }
    
    fun removerItem(nomeItem: String, quantidade: Int = 1): Item? {
        val slot = itens[nomeItem] ?: return null
        
        return if (slot.quantidade > quantidade) {
            itens[nomeItem] = slot.copy(quantidade = slot.quantidade - quantidade)
            slot.item
        } else {
            itens.remove(nomeItem)
            slot.item
        }
    }
    
    fun temItem(nomeItem: String, quantidade: Int = 1): Boolean {
        val slot = itens[nomeItem] ?: return false
        return slot.quantidade >= quantidade
    }
    
    fun getItem(nomeItem: String): Item? {
        return itens[nomeItem]?.item
    }
    
    fun getQuantidadeItem(nomeItem: String): Int {
        return itens[nomeItem]?.quantidade ?: 0
    }
    
    fun equiparItem(item: ItemEquipavel, slot: String): Boolean {
        if (!itens.containsKey(item.getNome())) {
            return false
        }
        
        // Desequipar item atual se houver
        equipamentos[slot]?.let { itemAtual ->
            desequiparItem(slot)
        }
        
        equipamentos[slot] = item
        return true
    }
    
    fun desequiparItem(slot: String): ItemEquipavel? {
        return equipamentos.remove(slot)
    }
    
    fun getItemEquipado(slot: String): ItemEquipavel? {
        return equipamentos[slot]
    }
    
    fun getItensEquipados(): Map<String, ItemEquipavel> {
        return equipamentos.toMap()
    }
    
    fun getTodosItens(): List<SlotInventario> {
        return itens.values.toList()
    }
    
    fun getPesoTotal(): Int {
        return itens.values.sumOf { it.getPesoTotal() } + 
               equipamentos.values.sumOf { it.getPeso() }
    }
    
    fun getValorTotal(): Int {
        return itens.values.sumOf { it.getValorTotal() } + 
               equipamentos.values.sumOf { it.getValor() }
    }
    
    fun getCapacidadeUsada(): Int = getPesoTotal()
    
    fun getCapacidadeRestante(): Int = capacidadeMaxima - getPesoTotal()
    
    fun estaCheio(): Boolean = getPesoTotal() >= capacidadeMaxima
    
    fun organizarPorTipo(): Map<TipoItem, List<SlotInventario>> {
        return itens.values.groupBy { it.item.getTipo() }
    }
    
    fun buscarItens(termo: String): List<SlotInventario> {
        return itens.values.filter { 
            it.item.getNome().contains(termo, ignoreCase = true) ||
            it.item.getDescricao().contains(termo, ignoreCase = true)
        }
    }
    
    fun getResumoInventario(): String {
        return buildString {
            appendLine("=== INVENTÁRIO ===")
            appendLine("Capacidade: ${getPesoTotal()}/$capacidadeMaxima")
            appendLine("Valor Total: ${getValorTotal()} PO")
            appendLine()
            
            if (equipamentos.isNotEmpty()) {
                appendLine("=== EQUIPAMENTOS ===")
                equipamentos.forEach { (slot, item) ->
                    appendLine("$slot: ${item.getNome()}")
                }
                appendLine()
            }
            
            if (itens.isNotEmpty()) {
                appendLine("=== ITENS ===")
                val itensPorTipo = organizarPorTipo()
                
                itensPorTipo.forEach { (tipo, slots) ->
                    appendLine("--- ${tipo} ---")
                    slots.forEach { slot ->
                        val quantidade = if (slot.quantidade > 1) " (${slot.quantidade}x)" else ""
                        appendLine("• ${slot.item.getNome()}$quantidade - ${slot.item.getPeso()} kg - ${slot.item.getValor()} PO")
                    }
                    appendLine()
                }
            } else {
                appendLine("Inventário vazio.")
            }
        }
    }
    
    fun getResumoEquipamentos(): String {
        return buildString {
            appendLine("=== EQUIPAMENTOS ===")
            
            if (equipamentos.isEmpty()) {
                appendLine("Nenhum item equipado.")
                return@buildString
            }
            
            equipamentos.forEach { (slot, item) ->
                appendLine("$slot: ${item.getNome()}")
                when (item) {
                    is Arma -> {
                        appendLine("  Dano: ${item.getDano()}")
                        appendLine("  Tipo: ${item.getTipoArma()}")
                        if (item.getAlcance() > 0) {
                            appendLine("  Alcance: ${item.getAlcance()}m")
                        }
                    }
                    is Armadura -> {
                        appendLine("  CA: +${item.getCABase()}")
                        appendLine("  Tipo: ${item.getTipoArmadura()}")
                    }
                    is Escudo -> {
                        appendLine("  CA: +${item.getBonusCAEscudo()}")
                    }
                }
                appendLine()
            }
        }
    }
}

