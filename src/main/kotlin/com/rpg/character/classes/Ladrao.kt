package com.rpg.character.classes

class Ladrao : Classe() {
    override fun getNome(): String = "Ladrão"
    
    override fun getDescricao(): String = 
        "Aventureiro tático, perito em sobreviver em masmorras. Furtivo, traiçoeiro e " +
        "fatal quando ataca suas vítimas sorrateiramente."
    
    override fun getDadoDeVida(): Int = 6
    
    override fun getProgressaoExperiencia(): Map<Int, ProgressaoExperiencia> {
        return mapOf(
            1 to ProgressaoExperiencia(1, 0, 0),
            2 to ProgressaoExperiencia(2, 1000, 1500),
            3 to ProgressaoExperiencia(3, 2000, 3000),
            4 to ProgressaoExperiencia(4, 4000, 5500),
            5 to ProgressaoExperiencia(5, 7000, 8500),
            6 to ProgressaoExperiencia(6, 14000, 17000),
            7 to ProgressaoExperiencia(7, 24000, 27000),
            8 to ProgressaoExperiencia(8, 34000, 37000),
            9 to ProgressaoExperiencia(9, 44000, 47000),
            10 to ProgressaoExperiencia(10, 88000, 94000)
        )
    }
    
    override fun getProgressaoAtributos(): Map<Int, ProgressaoAtributos> {
        return mapOf(
            1 to ProgressaoAtributos(1, "6", 1, 5),
            2 to ProgressaoAtributos(2, "+1d6", 1, 5),
            3 to ProgressaoAtributos(3, "+1d6", 2, 5),
            4 to ProgressaoAtributos(4, "+1d6", 2, 5),
            5 to ProgressaoAtributos(5, "+1d6", 3, 8),
            6 to ProgressaoAtributos(6, "+1d6", 3, 8),
            7 to ProgressaoAtributos(7, "+1d6", 4, 8),
            8 to ProgressaoAtributos(8, "+1d6", 4, 8),
            9 to ProgressaoAtributos(9, "+1d6", 5, 11),
            10 to ProgressaoAtributos(10, "+1d6", 5, 11)
        )
    }
    
    override fun getHabilidades(): List<HabilidadeDeClasse> {
        return listOf(
            AtaqueFurtivo(),
            OuvirRuidos(),
            TalentosLadrao()
        )
    }
    
    override fun getArmasPermitidas(): List<TipoArma> {
        return listOf(TipoArma.PEQUENA, TipoArma.MEDIA)
        // Armas grandes geram ataques difíceis
    }
    
    override fun getArmadurasPermitidas(): List<TipoArmadura> {
        return listOf(TipoArmadura.LEVE)
        // Armaduras médias/pesadas e escudos impedem habilidades e protegem só metade
    }
    
    override fun getItensPermitidos(): List<TipoItem> {
        return listOf(
            TipoItem.ARMA,
            TipoItem.ARMADURA,
            TipoItem.ITEM_MAGICO
            // Não pode usar cajados, varinhas e pergaminhos mágicos (exceto proteção)
        )
    }
    
    override fun getRestricoes(): List<String> {
        return listOf(
            "Apenas armas pequenas ou médias (grandes causam ataques difíceis)",
            "Apenas armaduras leves (médias/pesadas impedem habilidades)",
            "Escudos impedem uso das habilidades e protegem apenas metade da CA",
            "Não pode usar cajados, varinhas e pergaminhos mágicos",
            "Pode usar pergaminhos de proteção"
        )
    }
    
    fun getCarreiraLadrao(): Map<String, String> {
        return mapOf(
            "Início" to "Sobrevivente que superou tudo através de perspicácia e destreza",
            "Evolução" to "De batedor de carteira descalço a grande ladino reconhecido",
            "Habilidades" to "Praticar para ficar melhor, mais eficiente, rápido e mortal",
            "Hierarquia" to "Galgar a perigosa hierarquia do submundo",
            "Reconhecimento" to "Nem sempre os melhores são premiados, mas os influentes",
            "Objetivo" to "Tornar-se poderoso e influente no submundo"
        )
    }
    
    fun getTalentosDescricao(): Map<String, String> {
        return mapOf(
            "Armadilha" to "Detecta e desarma silenciosamente armadilhas em lugares ou objetos",
            "Arrombar" to "Destranca fechaduras, cadeados e trancas sem danificá-las",
            "Escalar" to "Escala qualquer superfície, mesmo lisas ou escorregadias",
            "Furtividade" to "Se esconde nas sombras e anda em silêncio furtivamente",
            "Punga" to "Retira pequenos itens de bolsos e realiza truques de prestidigitação"
        )
    }
    
    fun getEspecializacoesDisponiveis(): List<String> {
        return listOf("Ranger", "Bardo")
    }
}

