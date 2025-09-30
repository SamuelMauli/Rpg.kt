package com.rpg.character.attributes

class GeradorDeAtributos(private var metodo: MetodoRolagemAtributos) {
    
    fun setMetodo(novoMetodo: MetodoRolagemAtributos) {
        this.metodo = novoMetodo
    }
    
    fun gerarAtributosClassico(): Atributos {
        val metodoClassico = RolagemClassica()
        val valores = metodoClassico.rolarAtributos()
        return Atributos(
            forca = valores[0],
            destreza = valores[1],
            constituicao = valores[2],
            inteligencia = valores[3],
            sabedoria = valores[4],
            carisma = valores[5]
        )
    }
    
    fun gerarAtributosAventureiro(): Pair<List<Int>, String> {
        val metodoAventureiro = RolagemAventureiro()
        val valores = metodoAventureiro.rolarAtributos().sortedDescending()
        return Pair(valores, "Distribua estes valores nos atributos como desejar: ${valores.joinToString(", ")}")
    }
    
    fun gerarAtributosHeroico(): Pair<List<Int>, String> {
        val metodoHeroico = RolagemHeroica()
        val valores = metodoHeroico.rolarAtributos().sortedDescending()
        return Pair(valores, "Distribua estes valores nos atributos como desejar: ${valores.joinToString(", ")}")
    }
    
    fun criarAtributosPersonalizados(
        forca: Int,
        destreza: Int,
        constituicao: Int,
        inteligencia: Int,
        sabedoria: Int,
        carisma: Int
    ): Atributos {
        return Atributos(forca, destreza, constituicao, inteligencia, sabedoria, carisma)
    }
    
    fun validarAtributo(valor: Int): Boolean {
        return valor in 3..18
    }
    
    fun validarConjuntoAtributos(valores: List<Int>): Boolean {
        return valores.size == 6 && valores.all { validarAtributo(it) }
    }
    
    fun obterMetodosDisponiveis(): List<MetodoRolagemAtributos> {
        return listOf(
            RolagemClassica(),
            RolagemAventureiro(),
            RolagemHeroica()
        )
    }
}

