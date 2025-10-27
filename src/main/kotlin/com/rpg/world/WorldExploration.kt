package com.rpg.world

import com.rpg.character.Personagem
import kotlin.random.Random

/**
 * Sistema de Exploração do Mundo
 * Gerencia viagens, descobertas e eventos aleatórios
 */
class WorldExploration {
    
    data class Localizacao(
        val id: Long,
        val nome: String,
        val descricao: String,
        val tipo: TipoLocalizacao,
        val nivelMinimo: Int,
        val nivelMaximo: Int,
        val monstrosDisponiveis: List<Long>,
        val npcsDisponiveis: List<Long>,
        val conexoes: List<String>,
        var descoberta: Boolean = false
    )
    
    enum class TipoLocalizacao {
        VILA, CIDADE, FLORESTA, MONTANHA, MASMORRA, 
        RUINAS, ESTRADA, COVIL, TORRE, PANTANO, 
        DESERTO, CAVERNA, TEMPLO, PORTO, ILHA
    }
    
    data class EventoAleatorio(
        val nome: String,
        val descricao: String,
        val tipo: TipoEvento,
        val opcoes: List<OpcaoEvento>
    )
    
    enum class TipoEvento {
        COMBATE, TESOURO, ENCONTRO, ARMADILHA, MISTERIO
    }
    
    data class OpcaoEvento(
        val texto: String,
        val resultado: ResultadoEvento,
        val teste: TipoTeste? = null
    )
    
    enum class ResultadoEvento {
        COMBATE, TESOURO, DANO, CURA, XP, OURO, ITEM, NADA
    }
    
    enum class TipoTeste {
        FORCA, DESTREZA, CONSTITUICAO, INTELIGENCIA, SABEDORIA, CARISMA
    }
    
    private val localizacoesDescoberta = mutableSetOf<String>()
    private val localizacaoAtual = "Vila Inicial"
    
    /**
     * Viaja para uma nova localização
     */
    fun viajar(personagem: Personagem, destino: String): ResultadoViagem {
        val distancia = calcularDistancia(localizacaoAtual, destino)
        val eventos = mutableListOf<EventoAleatorio>()
        
        // Chance de eventos aleatórios durante a viagem
        for (i in 1..distancia) {
            if (Random.nextInt(100) < 30) { // 30% de chance por segmento
                eventos.add(gerarEventoAleatorio(personagem.nivel))
            }
        }
        
        // Marcar localização como descoberta
        if (!localizacoesDescoberta.contains(destino)) {
            localizacoesDescoberta.add(destino)
        }
        
        return ResultadoViagem(
            sucesso = true,
            destino = destino,
            distancia = distancia,
            eventos = eventos,
            xpGanho = distancia * 10,
            mensagem = "Você viajou para $destino"
        )
    }
    
    /**
     * Explora a localização atual
     */
    fun explorar(personagem: Personagem, localizacao: String): ResultadoExploracao {
        val descobertas = mutableListOf<String>()
        val itensEncontrados = mutableListOf<String>()
        var xpGanho = 0
        
        // Chance de encontrar itens
        if (Random.nextInt(100) < 40) {
            itensEncontrados.add("Poção de Cura Menor")
            descobertas.add("Você encontrou uma Poção de Cura Menor!")
        }
        
        // Chance de encontrar ouro
        if (Random.nextInt(100) < 50) {
            val ouro = Random.nextInt(10, 50)
            descobertas.add("Você encontrou $ouro moedas de ouro!")
        }
        
        // Chance de descobrir segredos
        if (Random.nextInt(100) < 20) {
            descobertas.add("Você descobriu uma passagem secreta!")
            xpGanho += 100
        }
        
        // XP por exploração
        xpGanho += 50
        
        return ResultadoExploracao(
            descobertas = descobertas,
            itensEncontrados = itensEncontrados,
            xpGanho = xpGanho,
            mensagem = "Você explorou $localizacao"
        )
    }
    
    /**
     * Gera um evento aleatório baseado no nível do personagem
     */
    private fun gerarEventoAleatorio(nivel: Int): EventoAleatorio {
        val tiposEvento = TipoEvento.values()
        val tipo = tiposEvento.random()
        
        return when (tipo) {
            TipoEvento.COMBATE -> EventoAleatorio(
                nome = "Encontro Hostil",
                descricao = "Você encontra criaturas hostis no caminho!",
                tipo = tipo,
                opcoes = listOf(
                    OpcaoEvento("Lutar", ResultadoEvento.COMBATE),
                    OpcaoEvento("Fugir", ResultadoEvento.NADA, TipoTeste.DESTREZA)
                )
            )
            
            TipoEvento.TESOURO -> EventoAleatorio(
                nome = "Baú Abandonado",
                descricao = "Você encontra um baú abandonado na estrada!",
                tipo = tipo,
                opcoes = listOf(
                    OpcaoEvento("Abrir", ResultadoEvento.TESOURO),
                    OpcaoEvento("Verificar armadilhas", ResultadoEvento.TESOURO, TipoTeste.INTELIGENCIA),
                    OpcaoEvento("Ignorar", ResultadoEvento.NADA)
                )
            )
            
            TipoEvento.ENCONTRO -> EventoAleatorio(
                nome = "Viajante Misterioso",
                descricao = "Um viajante encapuzado se aproxima de você.",
                tipo = tipo,
                opcoes = listOf(
                    OpcaoEvento("Conversar", ResultadoEvento.XP, TipoTeste.CARISMA),
                    OpcaoEvento("Ignorar", ResultadoEvento.NADA)
                )
            )
            
            TipoEvento.ARMADILHA -> EventoAleatorio(
                nome = "Armadilha Oculta",
                descricao = "Você aciona uma armadilha escondida!",
                tipo = tipo,
                opcoes = listOf(
                    OpcaoEvento("Esquivar", ResultadoEvento.NADA, TipoTeste.DESTREZA),
                    OpcaoEvento("Resistir", ResultadoEvento.DANO, TipoTeste.CONSTITUICAO)
                )
            )
            
            TipoEvento.MISTERIO -> EventoAleatorio(
                nome = "Runas Antigas",
                descricao = "Você encontra runas misteriosas gravadas em uma pedra.",
                tipo = tipo,
                opcoes = listOf(
                    OpcaoEvento("Investigar", ResultadoEvento.XP, TipoTeste.INTELIGENCIA),
                    OpcaoEvento("Tocar as runas", ResultadoEvento.ITEM),
                    OpcaoEvento("Ignorar", ResultadoEvento.NADA)
                )
            )
        }
    }
    
    /**
     * Calcula distância entre localizações (simplificado)
     */
    private fun calcularDistancia(origem: String, destino: String): Int {
        // Simulação simples de distância
        return Random.nextInt(1, 5)
    }
    
    /**
     * Verifica se pode viajar para destino
     */
    fun podeViajar(origem: String, destino: String): Boolean {
        // TODO: Verificar conexões no banco de dados
        return true
    }
    
    /**
     * Obtém localizações disponíveis a partir da atual
     */
    fun getLocalizacoesDisponiveis(localizacaoAtual: String): List<String> {
        // TODO: Buscar do banco de dados
        return when (localizacaoAtual) {
            "Vila Inicial" -> listOf("Floresta Sombria", "Estrada do Rei")
            "Floresta Sombria" -> listOf("Vila Inicial", "Ruínas Antigas")
            "Estrada do Rei" -> listOf("Vila Inicial", "Cidade de Thornhaven")
            "Ruínas Antigas" -> listOf("Floresta Sombria", "Catacumbas Profundas")
            "Cidade de Thornhaven" -> listOf("Estrada do Rei", "Montanhas Gélidas", "Porto de Maré Alta")
            else -> listOf("Vila Inicial")
        }
    }
    
    data class ResultadoViagem(
        val sucesso: Boolean,
        val destino: String,
        val distancia: Int,
        val eventos: List<EventoAleatorio>,
        val xpGanho: Int,
        val mensagem: String
    )
    
    data class ResultadoExploracao(
        val descobertas: List<String>,
        val itensEncontrados: List<String>,
        val xpGanho: Int,
        val mensagem: String
    )
}

