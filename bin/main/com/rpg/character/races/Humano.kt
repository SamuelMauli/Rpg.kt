package com.rpg.character.races

class Humano : Raca() {
    override fun getNome(): String = "Humano"
    
    override fun getDescricao(): String = 
        "Os mais comuns, versáteis e adaptáveis aventureiros. Possuem grande variabilidade " +
        "cultural e facilidade de adaptação a qualquer ambiente."
    
    override fun getMovimentoBase(): Int = 9
    
    override fun getInfravisao(): Int = 0
    
    override fun getAlinhamentosPermitidos(): List<Alinhamento> = 
        Alinhamento.values().toList()
    
    override fun getHabilidades(): List<HabilidadeRacial> = listOf(
        Aprendizado(),
        Adaptabilidade()
    )
    
    override fun getRestricoes(): List<String> = emptyList()
    
    fun getCaracteristicasEspeciais(): Map<String, String> {
        return mapOf(
            "Versatilidade" to "Podem escolher qualquer classe sem restrições",
            "Adaptação" to "Se adaptam rapidamente a novos ambientes e situações",
            "Aprendizado Rápido" to "Ganham experiência 10% mais rápido que outras raças",
            "Flexibilidade Social" to "Podem ter qualquer alinhamento"
        )
    }
    
    fun getPerguntasRoleplay(): List<String> {
        return listOf(
            "O que seu humano usa como motivo para aventurar-se?",
            "O que seu humano respeita? O que ele busca defender?",
            "O que seu humano odeia? O que ele deseja destruir?"
        )
    }
}

