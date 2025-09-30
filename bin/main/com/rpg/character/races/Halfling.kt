package com.rpg.character.races

class Halfling : Raca() {
    override fun getNome(): String = "Halfling"
    
    override fun getDescricao(): String = 
        "Os mais perspicazes e curiosos aventureiros. Pequenos, roliços, metódicos e corajosos, " +
        "são um povo pacífico e rural que habita colinas verdejantes."
    
    override fun getMovimentoBase(): Int = 6
    
    override fun getInfravisao(): Int = 0
    
    override fun getAlinhamentosPermitidos(): List<Alinhamento> = listOf(
        Alinhamento.LEAL_E_NEUTRO,
        Alinhamento.NEUTRO,
        Alinhamento.CAOTICO_E_NEUTRO,
        Alinhamento.NEUTRO_E_BOM,
        Alinhamento.NEUTRO_E_MAL
    )
    
    override fun getHabilidades(): List<HabilidadeRacial> = listOf(
        Furtivos(),
        Destemidos(),
        BonsDeMira(),
        Pequenos(),
        RestricoesHalfling()
    )
    
    override fun getRestricoes(): List<String> = listOf(
        "Usam apenas armaduras de couro (exceto se feitas especialmente)",
        "Armas médias são usadas como duas mãos",
        "Não podem usar armas grandes",
        "Movimento reduzido (6 metros ao invés de 9)",
        "Não possuem infravisão",
        "Tendem à neutralidade"
    )
    
    fun getCaracteristicasEspeciais(): Map<String, String> {
        return mapOf(
            "Furtividade Natural" to "Especialistas em se esconder e passar despercebidos",
            "Coragem" to "Resistentes a efeitos que afetem força de vontade",
            "Pontaria" to "Excelentes com armas de arremesso",
            "Agilidade" to "Difíceis de acertar para criaturas grandes",
            "Curiosidade" to "Magnetismo por coisas desconhecidas"
        )
    }
    
    fun getPerguntasRoleplay(): List<String> {
        return listOf(
            "O que seu halfling sentiu quando decidiu se aventurar?",
            "O que seu halfling busca enquanto se aventura?",
            "O que seu halfling planeja fazer com o que ganhar quando se aposentar?"
        )
    }
    
    fun getTracosFisicos(): List<String> {
        return listOf(
            "Aparência similar a mini-humanos",
            "Orelhas levemente pontiagudas",
            "Pés grandes, robustos e peludos",
            "Sempre descalços",
            "Feições bonachonas com bochechas rosadas",
            "Cabelos encaracolados e volumosos"
        )
    }
    
    fun getCultura(): Map<String, String> {
        return mapOf(
            "Habitação" to "Colinas verdejantes, comunidades bucólicas pequenas",
            "Estilo de Vida" to "Pacífico e rural, vivem do que plantam e criam",
            "Valores" to "Conforto, tranquilidade, curiosidade, amizade",
            "Alimentação" to "6 refeições por dia, gostam de comer com fartura",
            "Entretenimento" to "Histórias longas, piadas, leitura à beira da lareira",
            "Motivação" to "Contradição entre comodidade e curiosidade"
        )
    }
    
    fun getIdealDeVida(): String {
        return "Viver em paz sob o signo da calma, trabalhar diariamente, descansar à beira do rio, " +
               "ler, comer bem e ver o tempo passar em tranquilidade, longe da correria e preocupações."
    }
}

