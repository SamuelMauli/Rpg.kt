package com.rpg.character.races

class Anao : Raca() {
    override fun getNome(): String = "Anão"
    
    override fun getDescricao(): String = 
        "Orgulhosos habitantes dos Salões sob a Montanha. Honrados, perfeccionistas e reservados, " +
        "são conhecidos por sua habilidade em forja e mineração."
    
    override fun getMovimentoBase(): Int = 6
    
    override fun getInfravisao(): Int = 18
    
    override fun getAlinhamentosPermitidos(): List<Alinhamento> = listOf(
        Alinhamento.LEAL_E_BOM,
        Alinhamento.LEAL_E_NEUTRO,
        Alinhamento.LEAL_E_MAL,
        Alinhamento.NEUTRO_E_BOM,
        Alinhamento.NEUTRO,
        Alinhamento.NEUTRO_E_MAL
    )
    
    override fun getHabilidades(): List<HabilidadeRacial> = listOf(
        Mineradores(),
        Vigoroso(),
        RestricaoArmasGrandes(),
        Inimigos()
    )
    
    override fun getRestricoes(): List<String> = listOf(
        "Não podem usar armas grandes (exceto as forjadas por anões)",
        "Movimento reduzido (6 metros ao invés de 9)",
        "Tendem à ordem em seus alinhamentos",
        "Pouca aptidão às artes arcanas"
    )
    
    fun getCaracteristicasEspeciais(): Map<String, String> {
        return mapOf(
            "Forja Ancestral" to "Exímios forjadores, trabalham o metal à sua vontade",
            "Infravisão" to "Enxergam no escuro até 18 metros",
            "Resistência Física" to "Muito resistentes a efeitos corporais",
            "Conhecimento de Pedra" to "Detectam anomalias em estruturas de pedra",
            "Inimigos Tradicionais" to "Vantagem em combate contra orcs, ogros e hobgoblins"
        )
    }
    
    fun getPerguntasRoleplay(): List<String> {
        return listOf(
            "Do que seu anão mais se orgulha das suas tradições?",
            "O que seu anão busca provar ou realizar?",
            "O que seu anão possui como principal obsessão?"
        )
    }
    
    fun getTracosFisicos(): List<String> {
        return listOf(
            "Aparência robusta e rústica",
            "Corpulentos com grande barba",
            "Pensamento lógico acurado",
            "Habilidosos em engenharia e construção",
            "Estatura menor que humanos"
        )
    }
    
    fun getCultura(): Map<String, String> {
        return mapOf(
            "Habitação" to "Salões sob a Montanha - locais sagrados",
            "Atividades" to "Mineração, forja, reverência aos antepassados",
            "Celebrações" to "Cerveja e bebidas fortes, canções de trabalho",
            "Valores" to "Honra, tradição, orgulho racial, perfeição no trabalho",
            "Organização" to "Vivem em clãs, família acima do individual"
        )
    }
}

