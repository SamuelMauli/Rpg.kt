package com.rpg.character.classes

abstract class Classe {
    abstract fun getNome(): String
    abstract fun getDescricao(): String
    abstract fun getDadoDeVida(): Int
    abstract fun getProgressaoExperiencia(): Map<Int, ProgressaoExperiencia>
    abstract fun getProgressaoAtributos(): Map<Int, ProgressaoAtributos>
    abstract fun getHabilidades(): List<HabilidadeDeClasse>
    abstract fun getArmasPermitidas(): List<TipoArma>
    abstract fun getArmadurasPermitidas(): List<TipoArmadura>
    abstract fun getItensPermitidos(): List<TipoItem>
    abstract fun getRestricoes(): List<String>
    
    fun podeUsarArma(tipoArma: TipoArma): Boolean {
        return getArmasPermitidas().contains(tipoArma)
    }
    
    fun podeUsarArmadura(tipoArmadura: TipoArmadura): Boolean {
        return getArmadurasPermitidas().contains(tipoArmadura)
    }
    
    fun podeUsarItem(tipoItem: TipoItem): Boolean {
        return getItensPermitidos().contains(tipoItem)
    }
    
    fun getHabilidadesPorNivel(nivel: Int): List<HabilidadeDeClasse> {
        return getHabilidades().filter { it.getNivelRequerido() <= nivel }
    }
    
    fun getXpNecessario(nivel: Int): Int {
        return getProgressaoExperiencia()[nivel]?.xpNecessario ?: 0
    }
    
    fun getXpEspecialista(nivel: Int): Int {
        return getProgressaoExperiencia()[nivel]?.xpEspecialista ?: 0
    }
    
    fun getBaseAtaque(nivel: Int): Int {
        return getProgressaoAtributos()[nivel]?.baseAtaque ?: 1
    }
    
    fun getJogadaProtecao(nivel: Int): Int {
        return getProgressaoAtributos()[nivel]?.jogadaProtecao ?: 5
    }
    
    fun getPontosDeVidaAdicionais(nivel: Int): String {
        return getProgressaoAtributos()[nivel]?.pontosDeVida ?: "+1d${getDadoDeVida()}"
    }
    
    fun getResumoClasse(): String {
        return buildString {
            appendLine("=== ${getNome().uppercase()} ===")
            appendLine(getDescricao())
            appendLine()
            appendLine("Dado de Vida: d${getDadoDeVida()}")
            appendLine()
            appendLine("Armas Permitidas: ${getArmasPermitidas().joinToString(", ")}")
            appendLine("Armaduras Permitidas: ${getArmadurasPermitidas().joinToString(", ")}")
            appendLine()
            appendLine("Habilidades de Classe:")
            getHabilidades().forEach { habilidade ->
                appendLine("• Nível ${habilidade.getNivelRequerido()}: ${habilidade.getNome()}")
                appendLine("  ${habilidade.getDescricao()}")
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

