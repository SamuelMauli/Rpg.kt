package com.rpg.character.attributes

data class Atributos(
    val forca: Int,
    val destreza: Int,
    val constituicao: Int,
    val inteligencia: Int,
    val sabedoria: Int,
    val carisma: Int
) {
    fun getModificador(atributo: Int): Int {
        return atributo.toModificador()
    }
    
    fun getModificadorForca(): Int = forca.toModificador()
    fun getModificadorDestreza(): Int = destreza.toModificador()
    fun getModificadorConstituicao(): Int = constituicao.toModificador()
    fun getModificadorInteligencia(): Int = inteligencia.toModificador()
    fun getModificadorSabedoria(): Int = sabedoria.toModificador()
    fun getModificadorCarisma(): Int = carisma.toModificador()
    
    fun getMagiasExtrasArcanas(circulo: Int): Int {
        return when (circulo) {
            1 -> when (inteligencia) {
                in 13..14 -> 1
                in 15..16 -> 1
                in 17..18 -> 2
                in 19..20 -> 2
                else -> 0
            }
            2 -> when (inteligencia) {
                in 15..16 -> 1
                in 17..18 -> 1
                in 19..20 -> 2
                else -> 0
            }
            3 -> when (inteligencia) {
                in 17..18 -> 1
                in 19..20 -> 1
                else -> 0
            }
            else -> 0
        }
    }
    
    fun getMagiasExtrasDivinas(circulo: Int): Int {
        return when (circulo) {
            1 -> when (sabedoria) {
                in 13..14 -> 1
                in 15..16 -> 1
                in 17..18 -> 2
                in 19..20 -> 2
                else -> 0
            }
            2 -> when (sabedoria) {
                in 15..16 -> 1
                in 17..18 -> 1
                in 19..20 -> 2
                else -> 0
            }
            3 -> when (sabedoria) {
                in 17..18 -> 1
                in 19..20 -> 1
                else -> 0
            }
            else -> 0
        }
    }
}

fun Int.toModificador(): Int {
    return when (this) {
        in 2..3 -> -3
        in 4..5 -> -2
        in 6..8 -> -1
        in 9..12 -> 0
        in 13..14 -> 1
        in 15..16 -> 2
        in 17..18 -> 3
        in 19..20 -> 4
        else -> 0
    }
}

