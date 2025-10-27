package com.rpg.magic

import com.rpg.character.Personagem
import kotlin.random.Random

/**
 * Sistema de Magias
 * Gerencia grimório, conjuração e efeitos mágicos
 */
class MagicSystem {
    
    data class Magia(
        val id: Long,
        val nome: String,
        val nivel: Int,
        val escola: EscolaMagia,
        val classe: String,
        val alcance: Alcance,
        val duracao: String,
        val componentes: String,
        val tempoConjuracao: TempoConjuracao,
        val dano: String?,
        val tipoDano: TipoDano?,
        val efeito: String,
        val descricao: String,
        val custoMana: Int
    )
    
    enum class EscolaMagia {
        EVOCACAO,       // Magias de dano e energia
        ILUSAO,         // Magias de engano
        NECROMANCIA,    // Magias de morte e vida
        ABJURACAO,      // Magias de proteção
        TRANSMUTACAO,   // Magias de transformação
        ENCANTAMENTO,   // Magias de controle mental
        ADIVINHACAO,    // Magias de conhecimento
        CONJURACAO      // Magias de invocação
    }
    
    enum class Alcance {
        PESSOAL,        // Apenas o conjurador
        TOQUE,          // Toque físico
        CURTO,          // 30 pés
        MEDIO,          // 60 pés
        LONGO           // 120 pés
    }
    
    enum class TempoConjuracao {
        ACAO,           // 1 ação
        ACAO_BONUS,     // 1 ação bônus
        REACAO,         // 1 reação
        MINUTO,         // 1 minuto
        RITUAL          // 10 minutos
    }
    
    enum class TipoDano {
        FOGO, GELO, RAIO, ACIDO, VENENO,
        NECROTICO, RADIANTE, FORCA, TROVAO, PSIQUICO
    }
    
    data class Grimorio(
        val magias: MutableList<Magia> = mutableListOf(),
        val magiasPreparadas: MutableSet<Long> = mutableSetOf(),
        val slotsDisponiveis: MutableMap<Int, Int> = mutableMapOf()
    ) {
        fun adicionarMagia(magia: Magia): Boolean {
            if (magias.any { it.id == magia.id }) {
                return false
            }
            magias.add(magia)
            return true
        }
        
        fun prepararMagia(magiaId: Long): Boolean {
            val magia = magias.find { it.id == magiaId } ?: return false
            
            // Verificar se tem slots disponíveis
            val slots = slotsDisponiveis[magia.nivel] ?: 0
            if (slots <= 0) {
                return false
            }
            
            magiasPreparadas.add(magiaId)
            return true
        }
        
        fun desprepararMagia(magiaId: Long) {
            magiasPreparadas.remove(magiaId)
        }
        
        fun getMagiasPreparadas(): List<Magia> {
            return magias.filter { magiasPreparadas.contains(it.id) }
        }
        
        fun restaurarSlots() {
            // Restaurar slots após descanso
            slotsDisponiveis[1] = 4
            slotsDisponiveis[2] = 3
            slotsDisponiveis[3] = 3
            slotsDisponiveis[4] = 2
            slotsDisponiveis[5] = 2
            slotsDisponiveis[6] = 1
        }
    }
    
    /**
     * Conjura uma magia
     */
    fun conjurarMagia(
        magia: Magia,
        conjurador: Personagem,
        alvo: Any? = null
    ): ResultadoMagia {
        // Verificar se tem mana suficiente
        // TODO: Implementar sistema de mana no personagem
        
        // Verificar se a magia está preparada
        // TODO: Verificar grimório
        
        // Calcular efeito da magia
        val resultado = when (magia.escola) {
            EscolaMagia.EVOCACAO -> conjurarEvocacao(magia, conjurador, alvo)
            EscolaMagia.ABJURACAO -> conjurarAbjuracao(magia, conjurador, alvo)
            EscolaMagia.NECROMANCIA -> conjurarNecromancia(magia, conjurador, alvo)
            EscolaMagia.ILUSAO -> conjurarIlusao(magia, conjurador, alvo)
            EscolaMagia.TRANSMUTACAO -> conjurarTransmutacao(magia, conjurador, alvo)
            EscolaMagia.ENCANTAMENTO -> conjurarEncantamento(magia, conjurador, alvo)
            EscolaMagia.ADIVINHACAO -> conjurarAdivinhacao(magia, conjurador, alvo)
            EscolaMagia.CONJURACAO -> conjurarConjuracao(magia, conjurador, alvo)
        }
        
        return resultado
    }
    
    private fun conjurarEvocacao(magia: Magia, conjurador: Personagem, alvo: Any?): ResultadoMagia {
        if (magia.dano == null) {
            return ResultadoMagia(
                sucesso = false,
                mensagem = "Magia de evocação sem dano definido."
            )
        }
        
        val dano = rolarDano(magia.dano)
        val modificador = when (conjurador.classe.getNome()) {
            "Mago" -> conjurador.atributos.getModificadorInteligencia()
            "Clérigo" -> conjurador.atributos.getModificadorSabedoria()
            else -> 0
        }
        
        val danoTotal = dano + modificador
        
        return ResultadoMagia(
            sucesso = true,
            mensagem = "${conjurador.nome} conjura ${magia.nome}!",
            dano = danoTotal,
            tipoDano = magia.tipoDano,
            efeito = magia.efeito
        )
    }
    
    private fun conjurarAbjuracao(magia: Magia, conjurador: Personagem, alvo: Any?): ResultadoMagia {
        // Magias de proteção
        return ResultadoMagia(
            sucesso = true,
            mensagem = "${conjurador.nome} conjura ${magia.nome}!",
            efeito = magia.efeito,
            bonusCA = 5 // Exemplo: Escudo Arcano
        )
    }
    
    private fun conjurarNecromancia(magia: Magia, conjurador: Personagem, alvo: Any?): ResultadoMagia {
        // Magias de cura ou dano necrótico
        if (magia.dano != null) {
            val cura = rolarDano(magia.dano)
            val modificador = conjurador.atributos.getModificadorSabedoria()
            val curaTotal = cura + modificador
            
            return ResultadoMagia(
                sucesso = true,
                mensagem = "${conjurador.nome} conjura ${magia.nome}!",
                cura = curaTotal,
                efeito = magia.efeito
            )
        }
        
        return ResultadoMagia(
            sucesso = true,
            mensagem = "${conjurador.nome} conjura ${magia.nome}!",
            efeito = magia.efeito
        )
    }
    
    private fun conjurarIlusao(magia: Magia, conjurador: Personagem, alvo: Any?): ResultadoMagia {
        return ResultadoMagia(
            sucesso = true,
            mensagem = "${conjurador.nome} conjura ${magia.nome}!",
            efeito = magia.efeito
        )
    }
    
    private fun conjurarTransmutacao(magia: Magia, conjurador: Personagem, alvo: Any?): ResultadoMagia {
        return ResultadoMagia(
            sucesso = true,
            mensagem = "${conjurador.nome} conjura ${magia.nome}!",
            efeito = magia.efeito
        )
    }
    
    private fun conjurarEncantamento(magia: Magia, conjurador: Personagem, alvo: Any?): ResultadoMagia {
        return ResultadoMagia(
            sucesso = true,
            mensagem = "${conjurador.nome} conjura ${magia.nome}!",
            efeito = magia.efeito
        )
    }
    
    private fun conjurarAdivinhacao(magia: Magia, conjurador: Personagem, alvo: Any?): ResultadoMagia {
        return ResultadoMagia(
            sucesso = true,
            mensagem = "${conjurador.nome} conjura ${magia.nome}!",
            efeito = magia.efeito
        )
    }
    
    private fun conjurarConjuracao(magia: Magia, conjurador: Personagem, alvo: Any?): ResultadoMagia {
        return ResultadoMagia(
            sucesso = true,
            mensagem = "${conjurador.nome} conjura ${magia.nome}!",
            efeito = magia.efeito
        )
    }
    
    /**
     * Rola dano de uma magia
     */
    private fun rolarDano(formulaDano: String): Int {
        // Exemplo: "3d6+2" ou "2d8"
        val regex = Regex("""(\d+)d(\d+)(\+\d+)?""")
        val match = regex.find(formulaDano) ?: return 0
        
        val quantidade = match.groupValues[1].toInt()
        val dado = match.groupValues[2].toInt()
        val bonus = match.groupValues[3].removePrefix("+").toIntOrNull() ?: 0
        
        var total = bonus
        repeat(quantidade) {
            total += Random.nextInt(1, dado + 1)
        }
        
        return total
    }
    
    /**
     * Aprende uma nova magia
     */
    fun aprenderMagia(personagem: Personagem, magia: Magia, grimorio: Grimorio): ResultadoMagia {
        // Verificar se a classe pode aprender esta magia
        val classesPermitidas = magia.classe.split(",").map { it.trim() }
        if (!classesPermitidas.contains(personagem.classe.getNome())) {
            return ResultadoMagia(
                sucesso = false,
                mensagem = "Sua classe não pode aprender esta magia."
            )
        }
        
        // Verificar nível
        if (personagem.nivel < magia.nivel) {
            return ResultadoMagia(
                sucesso = false,
                mensagem = "Você precisa ser nível ${magia.nivel} para aprender esta magia."
            )
        }
        
        // Adicionar ao grimório
        if (!grimorio.adicionarMagia(magia)) {
            return ResultadoMagia(
                sucesso = false,
                mensagem = "Você já conhece esta magia."
            )
        }
        
        return ResultadoMagia(
            sucesso = true,
            mensagem = "Você aprendeu ${magia.nome}!",
            efeito = "Nova magia adicionada ao grimório"
        )
    }
    
    data class ResultadoMagia(
        val sucesso: Boolean,
        val mensagem: String,
        val dano: Int = 0,
        val cura: Int = 0,
        val tipoDano: TipoDano? = null,
        val efeito: String = "",
        val bonusCA: Int = 0,
        val duracao: Int = 0
    )
}

