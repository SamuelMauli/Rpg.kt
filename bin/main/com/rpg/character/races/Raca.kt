package com.rpg.character.races

abstract class Raca {
    abstract fun getNome(): String
    abstract fun getDescricao(): String
    abstract fun getMovimentoBase(): Int
    abstract fun getInfravisao(): Int
    abstract fun getAlinhamentosPermitidos(): List<Alinhamento>
    abstract fun getHabilidades(): List<HabilidadeRacial>
    abstract fun getRestricoes(): List<String>
    
    fun temInfravisao(): Boolean = getInfravisao() > 0
    
    fun podeUsarAlinhamento(alinhamento: Alinhamento): Boolean {
        return getAlinhamentosPermitidos().contains(alinhamento) || 
               getAlinhamentosPermitidos().isEmpty()
    }
    
    fun getResumoRacial(): String {
        return buildString {
            appendLine("=== ${getNome().uppercase()} ===")
            appendLine(getDescricao())
            appendLine()
            appendLine("Movimento: ${getMovimentoBase()} metros")
            if (temInfravisao()) {
                appendLine("Infravisão: ${getInfravisao()} metros")
            } else {
                appendLine("Infravisão: Não possui")
            }
            appendLine()
            appendLine("Habilidades Raciais:")
            getHabilidades().forEach { habilidade ->
                appendLine("• ${habilidade.getNome()}: ${habilidade.getDescricao()}")
            }
            if (getRestricoes().isNotEmpty()) {
                appendLine()
                appendLine("Restrições:")
                getRestricoes().forEach { restricao ->
                    appendLine("• $restricao")
                }
            }
        }
    }
}

