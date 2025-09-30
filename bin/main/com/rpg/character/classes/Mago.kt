package com.rpg.character.classes

class Mago : Classe() {
    override fun getNome(): String = "Mago"
    
    override fun getDescricao(): String = 
        "Aventureiro estudioso, especializado nas artes arcanas, dedicado a conjurar " +
        "magias escritas em grimórios e pergaminhos."
    
    override fun getDadoDeVida(): Int = 4
    
    override fun getProgressaoExperiencia(): Map<Int, ProgressaoExperiencia> {
        return mapOf(
            1 to ProgressaoExperiencia(1, 0, 0),
            2 to ProgressaoExperiencia(2, 2500, 3000),
            3 to ProgressaoExperiencia(3, 5000, 6000),
            4 to ProgressaoExperiencia(4, 8500, 10000),
            5 to ProgressaoExperiencia(5, 11500, 13000),
            6 to ProgressaoExperiencia(6, 23000, 26000),
            7 to ProgressaoExperiencia(7, 33000, 36000),
            8 to ProgressaoExperiencia(8, 43000, 46000),
            9 to ProgressaoExperiencia(9, 53000, 56000),
            10 to ProgressaoExperiencia(10, 106000, 112000)
        )
    }
    
    override fun getProgressaoAtributos(): Map<Int, ProgressaoAtributos> {
        return mapOf(
            1 to ProgressaoAtributos(1, "4", 0, 5),
            2 to ProgressaoAtributos(2, "+1d4", 1, 5),
            3 to ProgressaoAtributos(3, "+1d4", 1, 5),
            4 to ProgressaoAtributos(4, "+1d4", 1, 5),
            5 to ProgressaoAtributos(5, "+1d4", 2, 7),
            6 to ProgressaoAtributos(6, "+1d4", 2, 7),
            7 to ProgressaoAtributos(7, "+1d4", 2, 7),
            8 to ProgressaoAtributos(8, "+1d4", 3, 7),
            9 to ProgressaoAtributos(9, "+1d4", 3, 7),
            10 to ProgressaoAtributos(10, "+1d4", 3, 10)
        )
    }
    
    override fun getHabilidades(): List<HabilidadeDeClasse> {
        return listOf(
            MagiasArcanas(),
            DetectarMagias(),
            LerMagias(),
            Grimorio()
        )
    }
    
    override fun getArmasPermitidas(): List<TipoArma> {
        return listOf(TipoArma.PEQUENA)
        // Armas médias e grandes geram ataques difíceis
    }
    
    override fun getArmadurasPermitidas(): List<TipoArmadura> {
        return emptyList()
        // Não pode usar armaduras ou escudos (impede conjuração e protege só metade)
    }
    
    override fun getItensPermitidos(): List<TipoItem> {
        return listOf(
            TipoItem.ARMA,
            TipoItem.ITEM_MAGICO,
            TipoItem.CAJADO,
            TipoItem.VARINHA,
            TipoItem.PERGAMINHO
        )
    }
    
    override fun getRestricoes(): List<String> {
        return listOf(
            "Apenas armas pequenas (médias/grandes causam ataques difíceis)",
            "Não pode usar armaduras ou escudos",
            "Armaduras/escudos impedem conjuração e protegem apenas metade da CA",
            "Sem grimório, perde capacidade de memorizar novas magias"
        )
    }
    
    fun getCarreiraMago(): Map<String, String> {
        return mapOf(
            "Motivação" to "Aficionado por obter conhecimento arcano",
            "Sacrifícios" to "Estrada, desconforto, masmorras frias, companhias tediosas",
            "Objetivo" to "Acesso ao conhecimento em carreira onde poder = conhecimento",
            "Busca" to "Conhecimentos ancestrais em tumbas milenares profanadas",
            "Evolução" to "Conhecimento se reverte em poder e torna-se temido",
            "Medo" to "Natural ter medo daquilo que não se compreende",
            "Meta Final" to "Estabelecer-se e estudar tudo que juntou ao longo da vida"
        )
    }
    
    fun getTiposArmasPermitidas(): List<String> {
        return listOf(
            "Adaga",
            "Punhal",
            "Dardo",
            "Faca",
            "Cajado (como arma)",
            "Bordão"
        )
    }
    
    fun getTiposArmasProibidas(): List<String> {
        return listOf(
            "Espada (média/grande)",
            "Machado (médio/grande)",
            "Lança (média/grande)",
            "Martelo de Guerra (grande)",
            "Todas as armas grandes"
        )
    }
    
    fun getEspecializacoesDisponiveis(): List<String> {
        return listOf("Ilusionista", "Necromante")
    }
    
    fun getCirculosMaximos(): Map<Int, Int> {
        return mapOf(
            1 to 1, 2 to 1, 3 to 2, 4 to 2, 5 to 3,
            6 to 3, 7 to 4, 8 to 4, 9 to 5, 10 to 5
        )
    }
}

