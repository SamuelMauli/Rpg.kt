package com.rpg.character.races

class Elfo : Raca() {
    override fun getNome(): String = "Elfo"
    
    override fun getDescricao(): String = 
        "Misteriosos, longevos e graciosos habitantes das florestas. Vivem até 1000 anos " +
        "e valorizam a qualidade sobre a quantidade em todas as suas ações."
    
    override fun getMovimentoBase(): Int = 9
    
    override fun getInfravisao(): Int = 18
    
    override fun getAlinhamentosPermitidos(): List<Alinhamento> = listOf(
        Alinhamento.LEAL_E_NEUTRO,
        Alinhamento.NEUTRO,
        Alinhamento.CAOTICO_E_NEUTRO,
        Alinhamento.NEUTRO_E_BOM,
        Alinhamento.NEUTRO_E_MAL
    )
    
    override fun getHabilidades(): List<HabilidadeRacial> = listOf(
        PercepcaoNatural(),
        Graciosos(),
        ArmaRacialElfo(),
        ImunidadesElficas()
    )
    
    override fun getRestricoes(): List<String> = listOf(
        "Tendem à neutralidade em seus alinhamentos",
        "Dificuldade em criar laços com outras raças inicialmente"
    )
    
    fun getCaracteristicasEspeciais(): Map<String, String> {
        return mapOf(
            "Longevidade" to "Vivem até 1000 anos, com perspectiva temporal única",
            "Perfeccionismo" to "Preferem qualidade sobre quantidade em tudo",
            "Infravisão" to "Enxergam no escuro até 18 metros",
            "Maestria Arcana" to "Afinidade natural com magia arcana",
            "Percepção Aguçada" to "Detectam portas secretas naturalmente"
        )
    }
    
    fun getPerguntasRoleplay(): List<String> {
        return listOf(
            "O que seu elfo busca provar? Para si ou para seus iguais?",
            "O que seu elfo pensa sobre estar com outras raças?",
            "O que seu elfo sente por estar longe de sua terra?"
        )
    }
    
    fun getTracosFisicos(): List<String> {
        return listOf(
            "Orelhas pontudas e longas",
            "Olhos amendoados e delicados",
            "Aparência esguia e franzina",
            "Cabelos longos e bem cuidados",
            "Altura ligeiramente menor que humanos"
        )
    }
}

