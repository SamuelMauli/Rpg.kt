package com.rpg.combat

import com.rpg.character.Personagem
import com.rpg.core.factories.Monstro
import kotlin.random.Random

enum class TipoAcao {
    ATAQUE_CORPO_A_CORPO,
    ATAQUE_A_DISTANCIA,
    ATAQUE_DESARMADO,
    MOVIMENTO,
    MANOBRA,
    ATIVACAO,
    FUGIR,
    RECUAR,
    CORRER
}

enum class AjusteAtaque(val modificador: Int) {
    MUITO_FACIL(5),
    FACIL(2),
    NORMAL(0),
    DIFICIL(-2),
    MUITO_DIFICIL(-5)
}

data class ResultadoAtaque(
    val acertou: Boolean,
    val dano: Int = 0,
    val critico: Boolean = false,
    val erro: Boolean = false,
    val nocaute: Boolean = false
)

data class CombatenteInfo(
    val nome: String,
    val ca: Int,
    val pontosVida: Int,
    val pontosVidaMaximos: Int,
    val baseAtaqueCorpo: Int,
    val baseAtaqueDistancia: Int,
    val moral: Int,
    val engajado: Boolean = false,
    val surpreso: Boolean = false,
    val agonizando: Boolean = false
)

class GerenciadorDeCombate {
    
    private var rodadaAtual = 0
    private var combateAtivo = false
    private var ordemIniciativa = mutableListOf<String>()
    private var combatentesAtivos = mutableMapOf<String, Any>() // Personagem ou Monstro
    private var statusCombatentes = mutableMapOf<String, CombatenteInfo>()
    private var historicoRodada = mutableListOf<String>()
    
    fun iniciarCombate(
        personagens: List<Personagem>,
        monstros: List<Monstro>
    ): String {
        combateAtivo = true
        rodadaAtual = 0
        ordemIniciativa.clear()
        combatentesAtivos.clear()
        statusCombatentes.clear()
        historicoRodada.clear()
        
        // Registrar combatentes
        personagens.forEach { personagem ->
            combatentesAtivos[personagem.nome] = personagem
            statusCombatentes[personagem.nome] = criarInfoPersonagem(personagem)
        }
        
        monstros.forEachIndexed { index, monstro ->
            val nomeUnico = "${monstro.nome} ${index + 1}"
            combatentesAtivos[nomeUnico] = monstro
            statusCombatentes[nomeUnico] = criarInfoMonstro(monstro)
        }
        
        // Teste de surpresa
        val resultadoSurpresa = testarSurpresa()
        
        return buildString {
            appendLine("=== COMBATE INICIADO ===")
            appendLine("Combatentes:")
            personagens.forEach { appendLine("• ${it.nome} (${it.raca.getNome()} ${it.classe.getNome()})") }
            monstros.forEachIndexed { index, monstro -> 
                appendLine("• ${monstro.nome} ${index + 1} (${monstro.pontosDeVida} PV)")
            }
            appendLine()
            appendLine(resultadoSurpresa)
        }
    }
    
    fun proximaRodada(): String {
        if (!combateAtivo) return "Nenhum combate ativo."
        
        rodadaAtual++
        historicoRodada.clear()
        
        // Determinar iniciativa se for primeira rodada ou após surpresa
        if (rodadaAtual == 1 || statusCombatentes.values.any { it.surpreso }) {
            determinarIniciativa()
        }
        
        return buildString {
            appendLine("=== RODADA $rodadaAtual ===")
            appendLine("Ordem de iniciativa:")
            ordemIniciativa.forEach { nome ->
                val info = statusCombatentes[nome]!!
                val status = when {
                    info.agonizando -> " (AGONIZANDO)"
                    info.surpreso -> " (SURPRESO)"
                    else -> ""
                }
                appendLine("• $nome (${info.pontosVida}/${info.pontosVidaMaximos} PV)$status")
            }
            appendLine()
        }
    }
    
    fun executarAcao(
        nomeCombatente: String,
        tipoAcao: TipoAcao,
        nomeAlvo: String? = null,
        ajustes: List<AjusteAtaque> = emptyList()
    ): String {
        val combatente = combatentesAtivos[nomeCombatente] 
            ?: return "Combatente não encontrado: $nomeCombatente"
        
        val info = statusCombatentes[nomeCombatente]!!
        
        if (info.surpreso && rodadaAtual == 1) {
            return "$nomeCombatente está surpreso e perde a ação desta rodada."
        }
        
        if (info.agonizando) {
            return "$nomeCombatente está agonizando e não pode agir."
        }
        
        return when (tipoAcao) {
            TipoAcao.ATAQUE_CORPO_A_CORPO -> executarAtaqueCorpoACorpo(nomeCombatente, nomeAlvo!!, ajustes)
            TipoAcao.ATAQUE_A_DISTANCIA -> executarAtaqueADistancia(nomeCombatente, nomeAlvo!!, ajustes)
            TipoAcao.ATAQUE_DESARMADO -> executarAtaqueDesarmado(nomeCombatente, nomeAlvo!!, ajustes)
            TipoAcao.MOVIMENTO -> executarMovimento(nomeCombatente)
            TipoAcao.MANOBRA -> executarManobra(nomeCombatente)
            TipoAcao.FUGIR -> executarFuga(nomeCombatente)
            TipoAcao.RECUAR -> executarRecuo(nomeCombatente)
            TipoAcao.CORRER -> executarCorrida(nomeCombatente)
            TipoAcao.ATIVACAO -> executarAtivacao(nomeCombatente)
        }
    }
    
    fun finalizarRodada(): String {
        val resultado = StringBuilder()
        resultado.appendLine("=== FIM DA RODADA $rodadaAtual ===")
        
        // Verificar mortes
        val mortos = verificarMortes()
        if (mortos.isNotEmpty()) {
            resultado.appendLine("Mortos nesta rodada:")
            mortos.forEach { resultado.appendLine("• $it") }
        }
        
        // Verificar agonizantes
        val agonizantes = verificarAgonizantes()
        if (agonizantes.isNotEmpty()) {
            resultado.appendLine("Agonizando:")
            agonizantes.forEach { resultado.appendLine("• $it") }
        }
        
        // Testes de moral
        val resultadoMoral = verificarMoral()
        if (resultadoMoral.isNotEmpty()) {
            resultado.appendLine("Testes de Moral:")
            resultado.appendLine(resultadoMoral)
        }
        
        // Verificar fim do combate
        if (verificarFimCombate()) {
            combateAtivo = false
            resultado.appendLine()
            resultado.appendLine("=== COMBATE FINALIZADO ===")
        }
        
        return resultado.toString()
    }
    
    fun getStatusCombate(): String {
        return buildString {
            appendLine("=== STATUS DO COMBATE ===")
            appendLine("Rodada: $rodadaAtual")
            appendLine("Combatentes ativos:")
            
            statusCombatentes.forEach { (nome, info) ->
                val status = when {
                    info.pontosVida <= 0 -> " [MORTO]"
                    info.agonizando -> " [AGONIZANDO]"
                    info.surpreso -> " [SURPRESO]"
                    else -> ""
                }
                appendLine("• $nome: ${info.pontosVida}/${info.pontosVidaMaximos} PV$status")
            }
        }
    }
    
    private fun testarSurpresa(): String {
        val resultado = StringBuilder()
        resultado.appendLine("=== TESTE DE SURPRESA ===")
        
        // Simplificado: chance de 1-2 em 1d6 para cada lado
        val personagensSurpresos = Random.nextInt(1, 7) <= 2
        val monstrosSurpresos = Random.nextInt(1, 7) <= 2
        
        if (personagensSurpresos && monstrosSurpresos) {
            resultado.appendLine("Ambos os lados ficaram surpresos! Confusão momentânea.")
        } else if (personagensSurpresos) {
            resultado.appendLine("Os aventureiros foram pegos de surpresa!")
            statusCombatentes.keys.forEach { nome ->
                if (combatentesAtivos[nome] is Personagem) {
                    statusCombatentes[nome] = statusCombatentes[nome]!!.copy(surpreso = true)
                }
            }
        } else if (monstrosSurpresos) {
            resultado.appendLine("Os monstros foram pegos de surpresa!")
            statusCombatentes.keys.forEach { nome ->
                if (combatentesAtivos[nome] is Monstro) {
                    statusCombatentes[nome] = statusCombatentes[nome]!!.copy(surpreso = true)
                }
            }
        } else {
            resultado.appendLine("Nenhum lado foi pego de surpresa.")
        }
        
        return resultado.toString()
    }
    
    private fun determinarIniciativa() {
        val iniciativaPersonagens = mutableListOf<String>()
        val iniciativaMonstros = mutableListOf<String>()
        
        statusCombatentes.forEach { (nome, info) ->
            if (!info.surpreso) {
                when (combatentesAtivos[nome]) {
                    is Personagem -> {
                        val personagem = combatentesAtivos[nome] as Personagem
                        val atributoMaior = maxOf(personagem.atributos.destreza, personagem.atributos.sabedoria)
                        val rolagem = Random.nextInt(1, 21)
                        if (rolagem <= atributoMaior) {
                            iniciativaPersonagens.add(nome)
                        } else {
                            iniciativaMonstros.add(nome)
                        }
                    }
                    is Monstro -> iniciativaMonstros.add(nome)
                }
            }
        }
        
        // Ordem: sucessos na iniciativa > monstros > falhas na iniciativa
        ordemIniciativa.clear()
        ordemIniciativa.addAll(iniciativaPersonagens.shuffled())
        ordemIniciativa.addAll(iniciativaMonstros.shuffled())
        
        // Adicionar surpresos no final
        statusCombatentes.forEach { (nome, info) ->
            if (info.surpreso && !ordemIniciativa.contains(nome)) {
                ordemIniciativa.add(nome)
            }
        }
    }
    
    private fun executarAtaqueCorpoACorpo(atacante: String, alvo: String, ajustes: List<AjusteAtaque>): String {
        val combatenteAtacante = combatentesAtivos[atacante] ?: return "Atacante não encontrado"
        val combatenteAlvo = combatentesAtivos[alvo] ?: return "Alvo não encontrado"
        val infoAlvo = statusCombatentes[alvo] ?: return "Info do alvo não encontrada"
        
        if (infoAlvo.pontosVida <= 0) {
            return "$alvo já está morto!"
        }
        
        val baseAtaque = when (combatenteAtacante) {
            is Personagem -> combatenteAtacante.getBAC()
            is Monstro -> combatenteAtacante.baseAtaque
            else -> 0
        }
        
        val modificadorAjustes = ajustes.sumOf { it.modificador }
        val rolagem = Random.nextInt(1, 21)
        val totalAtaque = rolagem + baseAtaque + modificadorAjustes
        
        val resultado = when {
            rolagem == 1 -> ResultadoAtaque(false, erro = true)
            rolagem == 20 -> {
                val dano = calcularDano(combatenteAtacante, true)
                ResultadoAtaque(true, dano, critico = true)
            }
            totalAtaque >= infoAlvo.ca -> {
                val dano = calcularDano(combatenteAtacante, false)
                ResultadoAtaque(true, dano)
            }
            else -> ResultadoAtaque(false)
        }
        
        return processarResultadoAtaque(atacante, alvo, resultado, "corpo a corpo")
    }
    
    private fun executarAtaqueADistancia(atacante: String, alvo: String, ajustes: List<AjusteAtaque>): String {
        val combatenteAtacante = combatentesAtivos[atacante] ?: return "Atacante não encontrado"
        val combatenteAlvo = combatentesAtivos[alvo] ?: return "Alvo não encontrado"
        val infoAlvo = statusCombatentes[alvo] ?: return "Info do alvo não encontrada"
        
        if (infoAlvo.pontosVida <= 0) {
            return "$alvo já está morto!"
        }
        
        val baseAtaque = when (combatenteAtacante) {
            is Personagem -> combatenteAtacante.getBAD()
            is Monstro -> combatenteAtacante.baseAtaque
            else -> 0
        }
        
        val modificadorAjustes = ajustes.sumOf { it.modificador }
        val rolagem = Random.nextInt(1, 21)
        val totalAtaque = rolagem + baseAtaque + modificadorAjustes
        
        val resultado = when {
            rolagem == 1 -> ResultadoAtaque(false, erro = true)
            rolagem == 20 -> {
                val dano = calcularDanoDistancia(combatenteAtacante, true)
                ResultadoAtaque(true, dano, critico = true)
            }
            totalAtaque >= infoAlvo.ca -> {
                val dano = calcularDanoDistancia(combatenteAtacante, false)
                ResultadoAtaque(true, dano)
            }
            else -> ResultadoAtaque(false)
        }
        
        return processarResultadoAtaque(atacante, alvo, resultado, "à distância")
    }
    
    private fun executarAtaqueDesarmado(atacante: String, alvo: String, ajustes: List<AjusteAtaque>): String {
        val combatenteAtacante = combatentesAtivos[atacante] ?: return "Atacante não encontrado"
        val infoAlvo = statusCombatentes[alvo] ?: return "Info do alvo não encontrada"
        
        if (infoAlvo.pontosVida <= 0) {
            return "$alvo já está morto!"
        }
        
        val baseAtaque = when (combatenteAtacante) {
            is Personagem -> combatenteAtacante.getBAC()
            is Monstro -> combatenteAtacante.baseAtaque
            else -> 0
        }
        
        val modificadorAjustes = ajustes.sumOf { it.modificador }
        val rolagem = Random.nextInt(1, 21)
        val totalAtaque = rolagem + baseAtaque + modificadorAjustes
        
        val acertou = when {
            rolagem == 1 -> false
            rolagem == 20 -> true
            else -> totalAtaque >= infoAlvo.ca
        }
        
        if (acertou) {
            val modificadorForca = when (combatenteAtacante) {
                is Personagem -> combatenteAtacante.atributos.getModificadorForca()
                else -> 0
            }
            
            val chanceNocaute = maxOf(1, modificadorForca)
            val nocauteou = Random.nextInt(1, 7) <= chanceNocaute
            
            return if (nocauteou) {
                "$atacante acerta um golpe desarmado em $alvo e o nocauteia!"
            } else {
                "$atacante acerta um golpe desarmado em $alvo, mas não causa dano significativo."
            }
        } else {
            return "$atacante erra o golpe desarmado em $alvo."
        }
    }
    
    private fun executarMovimento(combatente: String): String {
        return "$combatente se move pelo campo de batalha."
    }
    
    private fun executarManobra(combatente: String): String {
        return "$combatente realiza uma manobra (trocar arma, beber poção, etc.)."
    }
    
    private fun executarFuga(combatente: String): String {
        statusCombatentes.remove(combatente)
        combatentesAtivos.remove(combatente)
        ordemIniciativa.remove(combatente)
        return "$combatente foge do combate!"
    }
    
    private fun executarRecuo(combatente: String): String {
        val info = statusCombatentes[combatente]!!
        statusCombatentes[combatente] = info.copy(engajado = false)
        return "$combatente recua cuidadosamente do combate corpo a corpo."
    }
    
    private fun executarCorrida(combatente: String): String {
        val info = statusCombatentes[combatente]!!
        statusCombatentes[combatente] = info.copy(engajado = false)
        return "$combatente corre para uma nova posição no campo de batalha."
    }
    
    private fun executarAtivacao(combatente: String): String {
        return "$combatente ativa uma habilidade especial ou lança uma magia."
    }
    
    private fun calcularDano(combatente: Any, critico: Boolean): Int {
        val danoBase = when (combatente) {
            is Personagem -> Random.nextInt(1, 9) + combatente.atributos.getModificadorForca() // Simplificado
            is Monstro -> combatente.rolarDano()
            else -> 1
        }
        
        return if (critico) danoBase * 2 else maxOf(1, danoBase)
    }
    
    private fun calcularDanoDistancia(combatente: Any, critico: Boolean): Int {
        val danoBase = when (combatente) {
            is Personagem -> Random.nextInt(1, 7) // Sem modificador de força
            is Monstro -> combatente.rolarDano()
            else -> 1
        }
        
        return if (critico) danoBase * 2 else maxOf(1, danoBase)
    }
    
    private fun processarResultadoAtaque(atacante: String, alvo: String, resultado: ResultadoAtaque, tipoAtaque: String): String {
        return if (resultado.acertou) {
            aplicarDano(alvo, resultado.dano)
            val critico = if (resultado.critico) " (CRÍTICO!)" else ""
            "$atacante acerta um ataque $tipoAtaque em $alvo causando ${resultado.dano} pontos de dano$critico."
        } else {
            val erro = if (resultado.erro) " (ERRO CRÍTICO!)" else ""
            "$atacante erra o ataque $tipoAtaque em $alvo$erro."
        }
    }
    
    private fun aplicarDano(alvo: String, dano: Int) {
        val info = statusCombatentes[alvo]!!
        val novosPV = info.pontosVida - dano
        
        statusCombatentes[alvo] = info.copy(pontosVida = novosPV)
        
        // Atualizar o objeto do combatente também
        when (val combatente = combatentesAtivos[alvo]) {
            is Personagem -> combatente.receberDano(dano)
            is Monstro -> combatente.receberDano(dano)
        }
    }
    
    private fun verificarMortes(): List<String> {
        val mortos = mutableListOf<String>()
        
        statusCombatentes.forEach { (nome, info) ->
            if (info.pontosVida <= -10) {
                mortos.add(nome)
                combatentesAtivos.remove(nome)
                ordemIniciativa.remove(nome)
            }
        }
        
        return mortos
    }
    
    private fun verificarAgonizantes(): List<String> {
        val agonizantes = mutableListOf<String>()
        
        statusCombatentes.forEach { (nome, info) ->
            if (info.pontosVida <= 0 && info.pontosVida > -10 && !info.agonizando) {
                statusCombatentes[nome] = info.copy(agonizando = true)
                agonizantes.add(nome)
            }
        }
        
        return agonizantes
    }
    
    private fun verificarMoral(): String {
        val resultado = StringBuilder()
        
        // Separar por grupos (personagens vs monstros)
        val personagensVivos = statusCombatentes.filter { 
            combatentesAtivos[it.key] is Personagem && it.value.pontosVida > 0 
        }.size
        
        val monstrosVivos = statusCombatentes.filter { 
            combatentesAtivos[it.key] is Monstro && it.value.pontosVida > 0 
        }.size
        
        val totalMonstros = combatentesAtivos.values.count { it is Monstro }
        val monstrosMortos = totalMonstros - monstrosVivos
        
        // Teste de moral se metade dos monstros morreu
        if (monstrosMortos >= totalMonstros / 2 && monstrosVivos > 0) {
            resultado.appendLine("Metade dos monstros foi derrotada! Testando moral...")
            
            statusCombatentes.forEach { (nome, info) ->
                if (combatentesAtivos[nome] is Monstro && info.pontosVida > 0) {
                    val rolagem = Random.nextInt(2, 13) // 2d6
                    if (rolagem > info.moral) {
                        resultado.appendLine("$nome falha no teste de moral e foge!")
                        combatentesAtivos.remove(nome)
                        ordemIniciativa.remove(nome)
                    } else {
                        resultado.appendLine("$nome resiste ao impulso de fugir.")
                    }
                }
            }
        }
        
        return resultado.toString()
    }
    
    private fun verificarFimCombate(): Boolean {
        val personagensVivos = statusCombatentes.count { 
            combatentesAtivos[it.key] is Personagem && it.value.pontosVida > 0 
        }
        
        val monstrosVivos = statusCombatentes.count { 
            combatentesAtivos[it.key] is Monstro && it.value.pontosVida > 0 
        }
        
        return personagensVivos == 0 || monstrosVivos == 0
    }
    
    private fun criarInfoPersonagem(personagem: Personagem): CombatenteInfo {
        return CombatenteInfo(
            nome = personagem.nome,
            ca = personagem.getCA(),
            pontosVida = personagem.pontosDeVida,
            pontosVidaMaximos = personagem.pontosDeVidaMaximos,
            baseAtaqueCorpo = personagem.getBAC(),
            baseAtaqueDistancia = personagem.getBAD(),
            moral = 12 // Personagens não fazem teste de moral normalmente
        )
    }
    
    private fun criarInfoMonstro(monstro: Monstro): CombatenteInfo {
        return CombatenteInfo(
            nome = monstro.nome,
            ca = monstro.classeArmadura,
            pontosVida = monstro.pontosDeVida,
            pontosVidaMaximos = monstro.pontosDeVida,
            baseAtaqueCorpo = monstro.baseAtaque,
            baseAtaqueDistancia = monstro.baseAtaque,
            moral = monstro.moral
        )
    }
}

