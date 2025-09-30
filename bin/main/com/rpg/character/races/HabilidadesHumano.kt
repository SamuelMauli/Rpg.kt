package com.rpg.character.races

class Aprendizado : HabilidadeRacialBase() {
    override fun getNome(): String = "Aprendizado"
    
    override fun getDescricao(): String = 
        "Humanos aprendem mais rapidamente, recebendo +10% de experiência (XP)"
    
    fun calcularXpComBonus(xpBase: Int): Int {
        return (xpBase * 1.1).toInt()
    }
}

class Adaptabilidade : HabilidadeRacialBase() {
    override fun getNome(): String = "Adaptabilidade"
    
    override fun getDescricao(): String = 
        "Humanos são adaptáveis, recebendo +1 em uma Jogada de Proteção à escolha"
    
    enum class TipoJogadaProtecao {
        MORTE_E_VENENO,
        HASTES_MAGICAS,
        PETRIFICACAO_E_PARALISIA,
        SOPRO_DE_DRAGAO,
        FEITICOS_E_MAGIAS
    }
    
    fun aplicarBonusJP(tipoJP: TipoJogadaProtecao): Int {
        return 1
    }
}

