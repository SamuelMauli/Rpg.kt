package com.rpg.core.factories

import com.rpg.character.Personagem
import com.rpg.character.attributes.*
import com.rpg.character.races.*
import com.rpg.character.classes.*
import kotlin.random.Random

class PersonagemFactory {
    
    private val geradorAtributos = GeradorDeAtributos(RolagemClassica())
    
    fun criarPersonagemCompleto(
        nome: String,
        tipoRaca: String,
        tipoClasse: String,
        metodoRolagem: String = "classico",
        atributosPersonalizados: List<Int>? = null,
        alinhamento: Alinhamento? = null
    ): Personagem {
        
        val raca = criarRaca(tipoRaca)
        val classe = criarClasse(tipoClasse)
        val atributos = criarAtributos(metodoRolagem, atributosPersonalizados)
        val alinhamentoFinal = alinhamento ?: escolherAlinhamentoAleatorio(raca)
        val pontosVidaIniciais = calcularPontosVidaIniciais(classe, atributos)
        
        val personagem = Personagem(
            nome = nome,
            raca = raca,
            classe = classe,
            nivel = 1,
            atributos = atributos,
            pontosDeVida = pontosVidaIniciais,
            pontosDeVidaMaximos = pontosVidaIniciais,
            experiencia = 0,
            alinhamento = alinhamentoFinal,
            inventario = mutableListOf(),
            dinheiro = rolarDinheiroInicial(),
            magiasMemorizadas = mutableListOf(),
            talentosLadrao = inicializarTalentosLadrao(classe, atributos),
            armasComMaestria = mutableListOf(),
            equipamentos = mutableMapOf()
        )
        
        configurarHabilidadesIniciais(personagem)
        adicionarEquipamentoInicial(personagem)
        
        return personagem
    }
    
    fun criarPersonagemRapido(nome: String): Personagem {
        val racas = listOf("humano", "elfo", "anao", "halfling")
        val classes = listOf("guerreiro", "ladrao", "clerigo", "mago")
        
        val racaAleatoria = racas.random()
        val classeAleatoria = classes.random()
        
        return criarPersonagemCompleto(nome, racaAleatoria, classeAleatoria)
    }
    
    fun criarRaca(tipo: String): Raca {
        return when (tipo.lowercase()) {
            "humano" -> Humano()
            "elfo" -> Elfo()
            "anao", "anão" -> Anao()
            "halfling" -> Halfling()
            else -> throw IllegalArgumentException("Raça desconhecida: $tipo")
        }
    }
    
    fun criarClasse(tipo: String): Classe {
        return when (tipo.lowercase()) {
            "guerreiro" -> Guerreiro()
            "ladrao", "ladrão" -> Ladrao()
            "clerigo", "clérigo" -> Clerigo()
            "mago" -> Mago()
            else -> throw IllegalArgumentException("Classe desconhecida: $tipo")
        }
    }
    
    fun criarAtributos(metodo: String, valoresPersonalizados: List<Int>? = null): Atributos {
        return when (metodo.lowercase()) {
            "classico", "clássico" -> {
                geradorAtributos.gerarAtributosClassico()
            }
            "aventureiro" -> {
                if (valoresPersonalizados != null && valoresPersonalizados.size == 6) {
                    geradorAtributos.criarAtributosPersonalizados(
                        valoresPersonalizados[0], valoresPersonalizados[1],
                        valoresPersonalizados[2], valoresPersonalizados[3],
                        valoresPersonalizados[4], valoresPersonalizados[5]
                    )
                } else {
                    val (valores, _) = geradorAtributos.gerarAtributosAventureiro()
                    geradorAtributos.criarAtributosPersonalizados(
                        valores[0], valores[1], valores[2],
                        valores[3], valores[4], valores[5]
                    )
                }
            }
            "heroico", "heróico" -> {
                if (valoresPersonalizados != null && valoresPersonalizados.size == 6) {
                    geradorAtributos.criarAtributosPersonalizados(
                        valoresPersonalizados[0], valoresPersonalizados[1],
                        valoresPersonalizados[2], valoresPersonalizados[3],
                        valoresPersonalizados[4], valoresPersonalizados[5]
                    )
                } else {
                    val (valores, _) = geradorAtributos.gerarAtributosHeroico()
                    geradorAtributos.criarAtributosPersonalizados(
                        valores[0], valores[1], valores[2],
                        valores[3], valores[4], valores[5]
                    )
                }
            }
            else -> geradorAtributos.gerarAtributosClassico()
        }
    }
    
    fun getRacasDisponiveis(): List<String> {
        return listOf("Humano", "Elfo", "Anão", "Halfling")
    }
    
    fun getClassesDisponiveis(): List<String> {
        return listOf("Guerreiro", "Ladrão", "Clérigo", "Mago")
    }
    
    fun getMetodosRolagemDisponiveis(): List<String> {
        return listOf("Clássico", "Aventureiro", "Heróico")
    }
    
    private fun escolherAlinhamentoAleatorio(raca: Raca): Alinhamento {
        val alinhamentosPermitidos = raca.getAlinhamentosPermitidos()
        return if (alinhamentosPermitidos.isNotEmpty()) {
            alinhamentosPermitidos.random()
        } else {
            Alinhamento.values().random()
        }
    }
    
    private fun calcularPontosVidaIniciais(classe: Classe, atributos: Atributos): Int {
        val dadoVida = classe.getDadoDeVida()
        val modificadorConstituicao = atributos.getModificadorConstituicao()
        val pontos = dadoVida + modificadorConstituicao
        return maxOf(1, pontos) // Mínimo 1 ponto de vida
    }
    
    private fun rolarDinheiroInicial(): Int {
        return Random.nextInt(30, 181) // 3d6 * 10 PO
    }
    
    private fun inicializarTalentosLadrao(classe: Classe, atributos: Atributos): MutableMap<String, Int> {
        val talentos = mutableMapOf<String, Int>()
        
        if (classe is Ladrao) {
            val modificadorDestreza = atributos.getModificadorDestreza()
            val pontosExtras = maxOf(0, modificadorDestreza)
            
            talentos["armadilha"] = 2
            talentos["arrombar"] = 2
            talentos["escalar"] = 2
            talentos["furtividade"] = 2
            talentos["punga"] = 2
            
            // Distribuir pontos extras do modificador de Destreza
            repeat(pontosExtras) {
                val talentosDisponiveis = talentos.keys.toList()
                val talentoEscolhido = talentosDisponiveis.random()
                if (talentos[talentoEscolhido]!! < 5) {
                    talentos[talentoEscolhido] = talentos[talentoEscolhido]!! + 1
                }
            }
        }
        
        return talentos
    }
    
    private fun configurarHabilidadesIniciais(personagem: Personagem) {
        // Configurar habilidades específicas da classe
        when (personagem.classe) {
            is Guerreiro -> {
                // Escolher primeira arma para maestria
                personagem.armasComMaestria.add("espada_longa")
            }
            is Mago -> {
                // Adicionar magias iniciais ao grimório
                personagem.magiasMemorizadas.add("Mísseis Mágicos")
                personagem.magiasMemorizadas.add("Detectar Magia")
            }
        }
    }
    
    private fun adicionarEquipamentoInicial(personagem: Personagem) {
        // Equipamento básico baseado na classe
        when (personagem.classe) {
            is Guerreiro -> {
                personagem.adicionarItem("Espada Longa")
                personagem.adicionarItem("Armadura de Couro")
                personagem.adicionarItem("Escudo")
                personagem.equipamentos["arma"] = "espada_longa"
                personagem.equipamentos["armadura"] = "couro"
                personagem.equipamentos["escudo"] = "escudo_pequeno"
            }
            is Ladrao -> {
                personagem.adicionarItem("Adaga")
                personagem.adicionarItem("Armadura de Couro")
                personagem.adicionarItem("Ferramentas de Ladrão")
                personagem.equipamentos["arma"] = "adaga"
                personagem.equipamentos["armadura"] = "couro"
            }
            is Clerigo -> {
                personagem.adicionarItem("Maça")
                personagem.adicionarItem("Armadura de Couro")
                personagem.adicionarItem("Escudo")
                personagem.adicionarItem("Símbolo Sagrado")
                personagem.equipamentos["arma"] = "maca"
                personagem.equipamentos["armadura"] = "couro"
                personagem.equipamentos["escudo"] = "escudo_pequeno"
            }
            is Mago -> {
                personagem.adicionarItem("Cajado")
                personagem.adicionarItem("Grimório")
                personagem.adicionarItem("Componentes de Magia")
                personagem.equipamentos["arma"] = "cajado"
            }
        }
        
        // Equipamento comum
        personagem.adicionarItem("Mochila")
        personagem.adicionarItem("Corda (15 metros)")
        personagem.adicionarItem("Tocha (5 unidades)")
        personagem.adicionarItem("Ração de Viagem (7 dias)")
        personagem.adicionarItem("Odre de Água")
    }
}

