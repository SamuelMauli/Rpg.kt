package com.rpg.character.classes

class Guerreiro : Classe() {
    override fun getNome(): String = "Guerreiro"
    
    override fun getDescricao(): String = 
        "Aventureiros especializados em combate, sempre na linha de frente e mortais " +
        "quando desembainham suas armas."
    
    override fun getDadoDeVida(): Int = 10
    
    override fun getProgressaoExperiencia(): Map<Int, ProgressaoExperiencia> {
        return mapOf(
            1 to ProgressaoExperiencia(1, 0, 0),
            2 to ProgressaoExperiencia(2, 2000, 2500),
            3 to ProgressaoExperiencia(3, 4000, 5000),
            4 to ProgressaoExperiencia(4, 7000, 8500),
            5 to ProgressaoExperiencia(5, 10000, 11500),
            6 to ProgressaoExperiencia(6, 20000, 23000),
            7 to ProgressaoExperiencia(7, 30000, 33000),
            8 to ProgressaoExperiencia(8, 40000, 43000),
            9 to ProgressaoExperiencia(9, 50000, 53000),
            10 to ProgressaoExperiencia(10, 100000, 106000)
        )
    }
    
    override fun getProgressaoAtributos(): Map<Int, ProgressaoAtributos> {
        return mapOf(
            1 to ProgressaoAtributos(1, "10", 1, 5),
            2 to ProgressaoAtributos(2, "+1d10", 2, 5),
            3 to ProgressaoAtributos(3, "+1d10", 3, 6),
            4 to ProgressaoAtributos(4, "+1d10", 4, 6),
            5 to ProgressaoAtributos(5, "+1d10", 5, 8),
            6 to ProgressaoAtributos(6, "+1d10", 6, 8),
            7 to ProgressaoAtributos(7, "+1d10", 7, 10),
            8 to ProgressaoAtributos(8, "+1d10", 8, 10),
            9 to ProgressaoAtributos(9, "+1d10", 9, 11),
            10 to ProgressaoAtributos(10, "+1d10", 10, 11)
        )
    }
    
    override fun getHabilidades(): List<HabilidadeDeClasse> {
        return listOf(
            Aparar(),
            MaestriaEmArma(),
            AtaqueExtra()
        )
    }
    
    override fun getArmasPermitidas(): List<TipoArma> {
        return listOf(TipoArma.PEQUENA, TipoArma.MEDIA, TipoArma.GRANDE)
    }
    
    override fun getArmadurasPermitidas(): List<TipoArmadura> {
        return listOf(TipoArmadura.LEVE, TipoArmadura.MEDIA, TipoArmadura.PESADA)
    }
    
    override fun getItensPermitidos(): List<TipoItem> {
        return listOf(
            TipoItem.ARMA,
            TipoItem.ARMADURA,
            TipoItem.ESCUDO,
            TipoItem.ITEM_MAGICO
            // Não pode usar cajados, varinhas e pergaminhos mágicos (exceto proteção)
        )
    }
    
    override fun getRestricoes(): List<String> {
        return listOf(
            "Não pode usar cajados, varinhas e pergaminhos mágicos",
            "Pode usar pergaminhos de proteção"
        )
    }
    
    fun getCarreiraGuerreiro(): Map<String, String> {
        return mapOf(
            "Níveis Baixos" to "Homem comum com perícia em armas que se torna aventureiro",
            "Desenvolvimento" to "Torna-se herói da vila, acumula experiência e riquezas",
            "Fama" to "Expande sua reputação para além das fronteiras locais",
            "Reconhecimento" to "Nome reconhecido em terras distantes",
            "Foco" to "Acumular recursos e tornar-se grande líder militar",
            "Objetivo" to "Credenciar-se para coisas grandes através do poder",
            "Meta Final" to "A nobreza o espera com riqueza, poder e conforto"
        )
    }
    
    fun getEspecializacoesDisponiveis(): List<String> {
        return listOf("Bárbaro", "Paladino")
    }
}

