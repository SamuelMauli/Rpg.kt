package com.rpg.api

import com.google.gson.Gson
import com.rpg.character.Personagem
import com.rpg.character.attributes.Atributos
import com.rpg.character.classes.*
import com.rpg.character.races.*
import com.rpg.combat.CombatSystem
import com.rpg.core.ExperienceSystem
import com.rpg.core.GerenciadorAtributos
import com.rpg.database.DatabaseManager
import com.rpg.items.GerenciadorLoot
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.random.Random

data class CriarPersonagemRequest(
    val nome: String,
    val raca: String,
    val classe: String,
    val alinhamento: String
)

data class IniciarCombateRequest(
    val personagemNome: String
)

data class DistribuirPontoRequest(
    val personagemNome: String,
    val atributo: String
)

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)

class ApiServer {
    
    private val experienceSystem = ExperienceSystem()
    private val gerenciadorAtributos = GerenciadorAtributos()
    private val combatSystem = CombatSystem()
    private val gerenciadorLoot = GerenciadorLoot()
    
    fun start(port: Int = 8080) {
        DatabaseManager.init()
        
        embeddedServer(Netty, port = port) {
            install(ContentNegotiation) {
                gson {
                    setPrettyPrinting()
                }
            }
            
            install(CORS) {
                anyHost()
                allowHeader(HttpHeaders.ContentType)
                allowMethod(HttpMethod.Get)
                allowMethod(HttpMethod.Post)
                allowMethod(HttpMethod.Put)
                allowMethod(HttpMethod.Delete)
            }
            
            routing {
                // Health check
                get("/") {
                    call.respondText("RPG API Server is running!", ContentType.Text.Plain)
                }
                
                // Criar personagem
                post("/api/personagens") {
                    try {
                        val request = call.receive<CriarPersonagemRequest>()
                        
                        val raca = when (request.raca.lowercase()) {
                            "humano" -> Humano()
                            "elfo" -> Elfo()
                            "anao" -> Anao()
                            "halfling" -> Halfling()
                            else -> Humano()
                        }
                        
                        val classe = when (request.classe.lowercase()) {
                            "guerreiro" -> Guerreiro()
                            "mago" -> Mago()
                            "clerigo" -> Clerigo()
                            "ladrao" -> Ladrao()
                            else -> Guerreiro()
                        }
                        
                        val atributos = Atributos(
                            forca = rolarAtributo(),
                            destreza = rolarAtributo(),
                            constituicao = rolarAtributo(),
                            inteligencia = rolarAtributo(),
                            sabedoria = rolarAtributo(),
                            carisma = rolarAtributo()
                        )
                        
                        val pvInicial = classe.getDadoDeVida() + atributos.getModificadorConstituicao()
                        
                        val alinhamento = when (request.alinhamento.lowercase()) {
                            "leal_e_bom" -> Alinhamento.LEAL_E_BOM
                            "neutro" -> Alinhamento.NEUTRO
                            "caotico_e_mal" -> Alinhamento.CAOTICO_E_MAL
                            else -> Alinhamento.NEUTRO
                        }
                        
                        val id = DatabaseManager.salvarPersonagem(
                            nome = request.nome,
                            raca = raca.getNome(),
                            classe = classe.getNome(),
                            nivel = 1,
                            forca = atributos.forca,
                            destreza = atributos.destreza,
                            constituicao = atributos.constituicao,
                            inteligencia = atributos.inteligencia,
                            sabedoria = atributos.sabedoria,
                            carisma = atributos.carisma,
                            pontosVida = pvInicial,
                            pontosVidaMaximos = pvInicial,
                            experiencia = 0,
                            dinheiro = 100
                        )
                        
                        call.respond(ApiResponse(
                            success = true,
                            data = mapOf(
                                "id" to id,
                                "nome" to request.nome,
                                "raca" to raca.getNome(),
                                "classe" to classe.getNome(),
                                "nivel" to 1,
                                "atributos" to mapOf(
                                    "forca" to atributos.forca,
                                    "destreza" to atributos.destreza,
                                    "constituicao" to atributos.constituicao,
                                    "inteligencia" to atributos.inteligencia,
                                    "sabedoria" to atributos.sabedoria,
                                    "carisma" to atributos.carisma
                                ),
                                "pontosVida" to pvInicial,
                                "pontosVidaMaximos" to pvInicial,
                                "experiencia" to 0,
                                "dinheiro" to 100
                            ),
                            message = "Personagem criado com sucesso!"
                        ))
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(
                            success = false,
                            message = "Erro ao criar personagem: ${e.message}"
                        ))
                    }
                }
                
                // Listar personagens
                get("/api/personagens") {
                    try {
                        val personagens = DatabaseManager.listarPersonagens()
                        call.respond(ApiResponse(
                            success = true,
                            data = personagens
                        ))
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(
                            success = false,
                            message = "Erro ao listar personagens: ${e.message}"
                        ))
                    }
                }
                
                // Obter personagem por nome
                get("/api/personagens/{nome}") {
                    try {
                        val nome = call.parameters["nome"] ?: throw IllegalArgumentException("Nome não fornecido")
                        val personagem = DatabaseManager.carregarPersonagem(nome)
                        
                        if (personagem != null) {
                            call.respond(ApiResponse(
                                success = true,
                                data = personagem
                            ))
                        } else {
                            call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(
                                success = false,
                                message = "Personagem não encontrado"
                            ))
                        }
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(
                            success = false,
                            message = "Erro ao buscar personagem: ${e.message}"
                        ))
                    }
                }
                
                // Listar monstros
                get("/api/monstros") {
                    try {
                        val nivelMin = call.request.queryParameters["nivelMin"]?.toIntOrNull() ?: 1
                        val nivelMax = call.request.queryParameters["nivelMax"]?.toIntOrNull() ?: 10
                        
                        val monstros = DatabaseManager.obterMonstrosPorNivel(nivelMin, nivelMax)
                        call.respond(ApiResponse(
                            success = true,
                            data = monstros
                        ))
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(
                            success = false,
                            message = "Erro ao listar monstros: ${e.message}"
                        ))
                    }
                }
                
                // Listar itens
                get("/api/itens") {
                    try {
                        val tipo = call.request.queryParameters["tipo"]
                        
                        val itens = if (tipo != null) {
                            DatabaseManager.obterItensPorTipo(tipo)
                        } else {
                            DatabaseManager.obterTodosItens()
                        }
                        
                        call.respond(ApiResponse(
                            success = true,
                            data = itens
                        ))
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(
                            success = false,
                            message = "Erro ao listar itens: ${e.message}"
                        ))
                    }
                }
                
                // Iniciar combate
                post("/api/combate/iniciar") {
                    try {
                        val request = call.receive<IniciarCombateRequest>()
                        val personagemData = DatabaseManager.carregarPersonagem(request.personagemNome)
                            ?: throw IllegalArgumentException("Personagem não encontrado")
                        
                        // Reconstruir personagem
                        val raca = when (personagemData.raca) {
                            "Humano" -> Humano()
                            "Elfo" -> Elfo()
                            "Anão" -> Anao()
                            "Halfling" -> Halfling()
                            else -> Humano()
                        }
                        
                        val classe = when (personagemData.classe) {
                            "Guerreiro" -> Guerreiro()
                            "Mago" -> Mago()
                            "Clérigo" -> Clerigo()
                            "Ladrão" -> Ladrao()
                            else -> Guerreiro()
                        }
                        
                        val atributos = Atributos(
                            forca = personagemData.forca,
                            destreza = personagemData.destreza,
                            constituicao = personagemData.constituicao,
                            inteligencia = personagemData.inteligencia,
                            sabedoria = personagemData.sabedoria,
                            carisma = personagemData.carisma
                        )
                        
                        val personagem = Personagem(
                            nome = personagemData.nome,
                            raca = raca,
                            classe = classe,
                            nivel = personagemData.nivel,
                            atributos = atributos,
                            pontosDeVida = personagemData.pontosVida,
                            pontosDeVidaMaximos = personagemData.pontosVidaMaximos,
                            experiencia = personagemData.experiencia,
                            alinhamento = Alinhamento.NEUTRO,
                            dinheiro = personagemData.dinheiro
                        )
                        
                        // Selecionar monstro
                        val monstros = DatabaseManager.obterMonstrosPorNivel(
                            maxOf(1, personagem.nivel - 1),
                            personagem.nivel + 2
                        )
                        
                        if (monstros.isEmpty()) {
                            throw IllegalStateException("Nenhum monstro disponível")
                        }
                        
                        val monstro = monstros.random()
                        
                        // Executar combate
                        val resultado = combatSystem.iniciarCombate(personagem, monstro)
                        
                        // Processar resultado
                        if (resultado.vencedor == "PERSONAGEM") {
                            personagem.dinheiro += resultado.ouroGanho
                            resultado.itensGanhos.forEach { personagem.adicionarItem(it) }
                            
                            val resultadoXP = experienceSystem.adicionarExperiencia(personagem, resultado.xpGanho)
                            
                            if (resultadoXP.pontosAtributoGanhos > 0) {
                                gerenciadorAtributos.adicionarPontosAtributo(personagem.nome, resultadoXP.pontosAtributoGanhos)
                            }
                            
                            // Salvar progresso
                            DatabaseManager.atualizarPersonagem(
                                nome = personagem.nome,
                                nivel = personagem.nivel,
                                pontosVida = personagem.pontosDeVida,
                                pontosVidaMaximos = personagem.pontosDeVidaMaximos,
                                experiencia = personagem.experiencia,
                                dinheiro = personagem.dinheiro
                            )
                            
                            call.respond(ApiResponse(
                                success = true,
                                data = mapOf(
                                    "resultado" to "vitoria",
                                    "log" to resultado.log,
                                    "xpGanho" to resultado.xpGanho,
                                    "ouroGanho" to resultado.ouroGanho,
                                    "itensGanhos" to resultado.itensGanhos,
                                    "nivelAtual" to personagem.nivel,
                                    "pontosVida" to personagem.pontosDeVida,
                                    "experiencia" to personagem.experiencia,
                                    "dinheiro" to personagem.dinheiro,
                                    "pontosAtributoDisponiveis" to gerenciadorAtributos.getPontosDisponiveis(personagem.nome)
                                )
                            ))
                        } else {
                            personagem.dinheiro = personagem.dinheiro / 2
                            personagem.pontosDeVida = personagem.pontosDeVidaMaximos
                            
                            DatabaseManager.atualizarPersonagem(
                                nome = personagem.nome,
                                nivel = personagem.nivel,
                                pontosVida = personagem.pontosDeVida,
                                pontosVidaMaximos = personagem.pontosDeVidaMaximos,
                                experiencia = personagem.experiencia,
                                dinheiro = personagem.dinheiro
                            )
                            
                            call.respond(ApiResponse(
                                success = true,
                                data = mapOf(
                                    "resultado" to "derrota",
                                    "log" to resultado.log,
                                    "pontosVida" to personagem.pontosDeVida,
                                    "dinheiro" to personagem.dinheiro
                                )
                            ))
                        }
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(
                            success = false,
                            message = "Erro ao iniciar combate: ${e.message}"
                        ))
                    }
                }
                
                // Distribuir ponto de atributo
                post("/api/personagens/distribuir-ponto") {
                    try {
                        val request = call.receive<DistribuirPontoRequest>()
                        val personagemData = DatabaseManager.carregarPersonagem(request.personagemNome)
                            ?: throw IllegalArgumentException("Personagem não encontrado")
                        
                        // Reconstruir personagem
                        val raca = when (personagemData.raca) {
                            "Humano" -> Humano()
                            "Elfo" -> Elfo()
                            "Anão" -> Anao()
                            "Halfling" -> Halfling()
                            else -> Humano()
                        }
                        
                        val classe = when (personagemData.classe) {
                            "Guerreiro" -> Guerreiro()
                            "Mago" -> Mago()
                            "Clérigo" -> Clerigo()
                            "Ladrão" -> Ladrao()
                            else -> Guerreiro()
                        }
                        
                        val atributos = Atributos(
                            forca = personagemData.forca,
                            destreza = personagemData.destreza,
                            constituicao = personagemData.constituicao,
                            inteligencia = personagemData.inteligencia,
                            sabedoria = personagemData.sabedoria,
                            carisma = personagemData.carisma
                        )
                        
                        val personagem = Personagem(
                            nome = personagemData.nome,
                            raca = raca,
                            classe = classe,
                            nivel = personagemData.nivel,
                            atributos = atributos,
                            pontosDeVida = personagemData.pontosVida,
                            pontosDeVidaMaximos = personagemData.pontosVidaMaximos,
                            experiencia = personagemData.experiencia,
                            alinhamento = Alinhamento.NEUTRO,
                            dinheiro = personagemData.dinheiro
                        )
                        
                        val resultado = gerenciadorAtributos.distribuirPonto(personagem, request.atributo)
                        
                        if (resultado.sucesso) {
                            // Salvar alterações
                            DatabaseManager.atualizarPersonagem(
                                nome = personagem.nome,
                                nivel = personagem.nivel,
                                pontosVida = personagem.pontosDeVida,
                                pontosVidaMaximos = personagem.pontosDeVidaMaximos,
                                experiencia = personagem.experiencia,
                                dinheiro = personagem.dinheiro
                            )
                            
                            call.respond(ApiResponse(
                                success = true,
                                data = mapOf(
                                    "atributos" to mapOf(
                                        "forca" to personagem.atributos.forca,
                                        "destreza" to personagem.atributos.destreza,
                                        "constituicao" to personagem.atributos.constituicao,
                                        "inteligencia" to personagem.atributos.inteligencia,
                                        "sabedoria" to personagem.atributos.sabedoria,
                                        "carisma" to personagem.atributos.carisma
                                    ),
                                    "pontosDisponiveis" to gerenciadorAtributos.getPontosDisponiveis(personagem.nome)
                                ),
                                message = resultado.mensagem
                            ))
                        } else {
                            call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(
                                success = false,
                                message = resultado.mensagem
                            ))
                        }
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(
                            success = false,
                            message = "Erro ao distribuir ponto: ${e.message}"
                        ))
                    }
                }
            }
        }.start(wait = true)
    }
    
    private fun rolarAtributo(): Int {
        val rolagens = List(4) { Random.nextInt(1, 7) }
        return rolagens.sorted().drop(1).sum()
    }
}

fun main() {
    println("Iniciando servidor API RPG...")
    val server = ApiServer()
    server.start(8080)
}

