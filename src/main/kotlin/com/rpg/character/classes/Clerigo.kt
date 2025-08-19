package com.rpg.character.classes

class Clerigo : Classe() {
    override fun getNome(): String = "Clérigo"
    
    override fun getDescricao(): String = 
        "Aventureiro religioso e devoto das forças divinas do panteão. Determinado e virtuoso, " +
        "protege a todos usando seus milagres divinos."
    
    override fun getDadoDeVida(): Int = 8
    
    override fun getProgressaoExperiencia(): Map<Int, ProgressaoExperiencia> {
        return mapOf(
            1 to ProgressaoExperiencia(1, 0, 0),
            2 to ProgressaoExperiencia(2, 1500, 2000),
            3 to ProgressaoExperiencia(3, 3000, 4000),
            4 to ProgressaoExperiencia(4, 5500, 7000),
            5 to ProgressaoExperiencia(5, 8500, 10000),
            6 to ProgressaoExperiencia(6, 17000, 20000),
            7 to ProgressaoExperiencia(7, 27000, 30000),
            8 to ProgressaoExperiencia(8, 37000, 40000),
            9 to ProgressaoExperiencia(9, 47000, 50000),
            10 to ProgressaoExperiencia(10, 94000, 100000)
        )
    }
    
    override fun getProgressaoAtributos(): Map<Int, ProgressaoAtributos> {
        return mapOf(
            1 to ProgressaoAtributos(1, "8", 1, 5),
            2 to ProgressaoAtributos(2, "+1d8", 1, 5),
            3 to ProgressaoAtributos(3, "+1d8", 1, 5),
            4 to ProgressaoAtributos(4, "+1d8", 3, 7),
            5 to ProgressaoAtributos(5, "+1d8", 3, 7),
            6 to ProgressaoAtributos(6, "+1d8", 3, 7),
            7 to ProgressaoAtributos(7, "+1d8", 5, 9),
            8 to ProgressaoAtributos(8, "+1d8", 5, 9),
            9 to ProgressaoAtributos(9, "+1d8", 5, 9),
            10 to ProgressaoAtributos(10, "+1d8", 7, 11)
        )
    }
    
    override fun getHabilidades(): List<HabilidadeDeClasse> {
        return listOf(
            MagiasDivinas(),
            AfastarMortosVivos(),
            CuraMilagrosa()
        )
    }
    
    override fun getArmasPermitidas(): List<TipoArma> {
        return listOf(TipoArma.PEQUENA, TipoArma.MEDIA, TipoArma.GRANDE)
        // Mas apenas armas impactantes
    }
    
    override fun getArmadurasPermitidas(): List<TipoArmadura> {
        return listOf(TipoArmadura.LEVE, TipoArmadura.MEDIA, TipoArmadura.PESADA)
    }
    
    override fun getItensPermitidos(): List<TipoItem> {
        return listOf(
            TipoItem.ARMA,
            TipoItem.ARMADURA,
            TipoItem.ESCUDO,
            TipoItem.ITEM_MAGICO,
            TipoItem.CAJADO,
            TipoItem.VARINHA,
            TipoItem.PERGAMINHO
            // Pode usar todos os itens mágicos desde que sejam ordeiros
        )
    }
    
    override fun getRestricoes(): List<String> {
        return listOf(
            "Só pode usar armas impactantes (não cortantes ou perfurantes)",
            "Usar armas proibidas faz perder magias até realizar penitência",
            "Só pode usar itens mágicos ordeiros",
            "Deve seguir os dogmas de sua divindade"
        )
    }
    
    fun getCarreiraClérigo(): Map<String, String> {
        return mapOf(
            "Início" to "Sacerdote recém-ordenado, treinado para defender dogmas divinos",
            "Missão" to "Estimular, convencer e inspirar fiéis através do poder divino",
            "Propósito" to "Expurgar o mundo do caos através dos ensinamentos de seu deus",
            "Vocação" to "Ser a arma de deus para livrar o mundo do pecado",
            "Reconhecimento" to "Ser avaliado pelos superiores e tornar-se inspiração",
            "Luta" to "Incansável combate contra o caos e as forças malignas"
        )
    }
    
    fun getTiposArmasPermitidas(): List<String> {
        return listOf(
            "Maça",
            "Martelo de Guerra",
            "Mangual",
            "Clava",
            "Bastão",
            "Marreta",
            "Martelo",
            "Bordão"
        )
    }
    
    fun getTiposArmasProibidas(): List<String> {
        return listOf(
            "Espada (cortante)",
            "Adaga (perfurante)",
            "Lança (perfurante)",
            "Arco (perfurante)",
            "Besta (perfurante)"
        )
    }
    
    fun getEspecializacoesDisponiveis(): List<String> {
        return listOf("Druida", "Acadêmico")
    }
}

