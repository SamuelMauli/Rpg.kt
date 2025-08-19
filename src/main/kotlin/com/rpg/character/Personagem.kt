package com.rpg.character

import com.rpg.character.attributes.Atributos
import com.rpg.character.races.Raca
import com.rpg.character.races.Alinhamento
import com.rpg.character.classes.Classe
import com.rpg.character.classes.HabilidadeDeClasse
import com.rpg.character.races.HabilidadeRacial
import kotlin.random.Random

data class Personagem(
    val nome: String,
    val raca: Raca,
    val classe: Classe,
    var nivel: Int = 1,
    val atributos: Atributos,
    var pontosDeVida: Int,
    val pontosDeVidaMaximos: Int,
    var experiencia: Int = 0,
    val alinhamento: Alinhamento,
    var inventario: MutableList<String> = mutableListOf(),
    var dinheiro: Int = 0,
    var magiasMemorizadas: MutableList<String> = mutableListOf(),
    var talentosLadrao: MutableMap<String, Int> = mutableMapOf(),
    var armasComMaestria: MutableList<String> = mutableListOf(),
    var equipamentos: MutableMap<String, String> = mutableMapOf()
) {
    
    fun getBAC(): Int {
        val baseAtaque = classe.getBaseAtaque(nivel)
        val modificadorForca = atributos.getModificadorForca()
        return baseAtaque + modificadorForca
    }
    
    fun getBAD(): Int {
        val baseAtaque = classe.getBaseAtaque(nivel)
        val modificadorDestreza = atributos.getModificadorDestreza()
        return baseAtaque + modificadorDestreza
    }
    
    fun getCA(): Int {
        val caBase = 10
        val modificadorDestreza = atributos.getModificadorDestreza()
        val bonusArmadura = getBonusArmadura()
        val bonusEscudo = getBonusEscudo()
        
        return caBase + modificadorDestreza + bonusArmadura + bonusEscudo
    }
    
    fun getJP(): Int {
        val jpBase = classe.getJogadaProtecao(nivel)
        return jpBase
    }
    
    fun getJPMorteVeneno(): Int {
        return getJP() + getBonusJPRacial("morte_veneno")
    }
    
    fun getJPHastesMagicas(): Int {
        return getJP() + getBonusJPRacial("hastes_magicas")
    }
    
    fun getJPPetrificacaoParalisia(): Int {
        return getJP() + getBonusJPRacial("petrificacao_paralisia")
    }
    
    fun getJPSoproDragao(): Int {
        return getJP() + getBonusJPRacial("sopro_dragao")
    }
    
    fun getJPFeiticosMagias(): Int {
        return getJP() + getBonusJPRacial("feiticos_magias")
    }
    
    fun getCargaMaxima(): Int {
        val maiorValor = maxOf(atributos.forca, atributos.constituicao)
        return maiorValor
    }
    
    fun getCargaAtual(): Int {
        return inventario.size // Simplificado - cada item = 1 de carga
    }
    
    fun estaSobrecarregado(): Boolean {
        return getCargaAtual() > getCargaMaxima()
    }
    
    fun getMovimento(): Int {
        val movimentoBase = raca.getMovimentoBase()
        return if (estaSobrecarregado()) movimentoBase / 2 else movimentoBase
    }
    
    fun getXpNecessarioProximoNivel(): Int {
        return classe.getXpNecessario(nivel + 1)
    }
    
    fun podeSubirNivel(): Boolean {
        return experiencia >= getXpNecessarioProximoNivel()
    }
    
    fun subirNivel(): Boolean {
        if (!podeSubirNivel()) return false
        
        nivel++
        val pontosVidaAdicionais = rolarPontosVidaAdicionais()
        pontosDeVida += pontosVidaAdicionais
        
        return true
    }
    
    fun receberDano(dano: Int): Boolean {
        pontosDeVida = maxOf(0, pontosDeVida - dano)
        return pontosDeVida <= 0
    }
    
    fun curarDano(cura: Int) {
        pontosDeVida = minOf(pontosDeVidaMaximos, pontosDeVida + cura)
    }
    
    fun estaMorto(): Boolean = pontosDeVida <= 0
    
    fun estaAgonizando(): Boolean = pontosDeVida <= 0 && pontosDeVida > -10
    
    fun getHabilidadesRaciais(): List<HabilidadeRacial> {
        return raca.getHabilidades()
    }
    
    fun getHabilidadesClasse(): List<HabilidadeDeClasse> {
        return classe.getHabilidadesPorNivel(nivel)
    }
    
    fun adicionarItem(item: String) {
        inventario.add(item)
    }
    
    fun removerItem(item: String): Boolean {
        return inventario.remove(item)
    }
    
    fun temItem(item: String): Boolean {
        return inventario.contains(item)
    }
    
    fun getResumoPersonagem(): String {
        return buildString {
            appendLine("=== ${nome.uppercase()} ===")
            appendLine("${raca.getNome()} ${classe.getNome()} - Nível $nivel")
            appendLine("Alinhamento: $alinhamento")
            appendLine()
            appendLine("=== ATRIBUTOS ===")
            appendLine("Força: ${atributos.forca} (${atributos.getModificadorForca().let { if (it >= 0) "+$it" else "$it" }})")
            appendLine("Destreza: ${atributos.destreza} (${atributos.getModificadorDestreza().let { if (it >= 0) "+$it" else "$it" }})")
            appendLine("Constituição: ${atributos.constituicao} (${atributos.getModificadorConstituicao().let { if (it >= 0) "+$it" else "$it" }})")
            appendLine("Inteligência: ${atributos.inteligencia} (${atributos.getModificadorInteligencia().let { if (it >= 0) "+$it" else "$it" }})")
            appendLine("Sabedoria: ${atributos.sabedoria} (${atributos.getModificadorSabedoria().let { if (it >= 0) "+$it" else "$it" }})")
            appendLine("Carisma: ${atributos.carisma} (${atributos.getModificadorCarisma().let { if (it >= 0) "+$it" else "$it" }})")
            appendLine()
            appendLine("=== COMBATE ===")
            appendLine("Pontos de Vida: $pontosDeVida/$pontosDeVidaMaximos")
            appendLine("Classe de Armadura: ${getCA()}")
            appendLine("Base de Ataque (Corpo a Corpo): ${getBAC()}")
            appendLine("Base de Ataque (À Distância): ${getBAD()}")
            appendLine("Jogada de Proteção: ${getJP()}")
            appendLine()
            appendLine("=== OUTROS ===")
            appendLine("Experiência: $experiencia/${getXpNecessarioProximoNivel()}")
            appendLine("Movimento: ${getMovimento()} metros")
            appendLine("Carga: ${getCargaAtual()}/${getCargaMaxima()}")
            appendLine("Dinheiro: $dinheiro PO")
        }
    }
    
    private fun getBonusArmadura(): Int {
        val armadura = equipamentos["armadura"]
        return when (armadura) {
            "couro" -> 2
            "couro_batido" -> 3
            "cota_de_malha" -> 5
            "cota_de_placas" -> 7
            "armadura_de_placas" -> 8
            else -> 0
        }
    }
    
    private fun getBonusEscudo(): Int {
        val escudo = equipamentos["escudo"]
        return if (escudo != null) 1 else 0
    }
    
    private fun getBonusJPRacial(tipo: String): Int {
        // Implementar bônus raciais específicos para JPs
        return 0
    }
    
    private fun rolarPontosVidaAdicionais(): Int {
        val dadoVida = classe.getDadoDeVida()
        val modificadorConstituicao = atributos.getModificadorConstituicao()
        val pontos = Random.nextInt(1, dadoVida + 1) + modificadorConstituicao
        return maxOf(1, pontos) // Mínimo 1 ponto por nível
    }
}

