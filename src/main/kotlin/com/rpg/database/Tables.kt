package com.rpg.database

import org.jetbrains.exposed.sql.Table

// ==================== TABELAS EXISTENTES ====================

object PersonagensTable : Table("personagens") {
    val id = long("id").autoIncrement()
    val nome = varchar("nome", 100)
    val raca = varchar("raca", 50)
    val classe = varchar("classe", 50)
    val nivel = integer("nivel")
    val forca = integer("forca")
    val destreza = integer("destreza")
    val constituicao = integer("constituicao")
    val inteligencia = integer("inteligencia")
    val sabedoria = integer("sabedoria")
    val carisma = integer("carisma")
    val pontosVida = integer("pontos_vida")
    val pontosVidaMaximos = integer("pontos_vida_maximos")
    val experiencia = integer("experiencia")
    val dinheiro = integer("dinheiro")
    val localizacaoAtual = varchar("localizacao_atual", 100).default("Vila Inicial")
    val pontosAtributo = integer("pontos_atributo").default(0)
    val pontosTalento = integer("pontos_talento").default(0)
    
    override val primaryKey = PrimaryKey(id)
}

object ItensTable : Table("itens") {
    val id = long("id").autoIncrement()
    val nome = varchar("nome", 100)
    val tipo = varchar("tipo", 50) // ARMA, ARMADURA, POCAO, ESCUDO, ANEL, AMULETO, MATERIAL, QUEST
    val dano = varchar("dano", 20) // Ex: "1d8", "2d6"
    val bonus = integer("bonus") // Bonus de ataque/defesa
    val valor = integer("valor") // Valor em ouro
    val peso = integer("peso") // Peso em unidades
    val descricao = text("descricao")
    val raridade = varchar("raridade", 20).default("COMUM") // COMUM, INCOMUM, RARO, EPICO, LENDARIO
    val requisitos = varchar("requisitos", 200).nullable() // Ex: "nivel:5,forca:12"
    
    override val primaryKey = PrimaryKey(id)
}

object InventarioTable : Table("inventario") {
    val id = long("id").autoIncrement()
    val personagemId = long("personagem_id").references(PersonagensTable.id)
    val itemId = long("item_id").references(ItensTable.id)
    val quantidade = integer("quantidade")
    val equipado = bool("equipado")
    
    override val primaryKey = PrimaryKey(id)
}

object MonstrosTable : Table("monstros") {
    val id = long("id").autoIncrement()
    val nome = varchar("nome", 100)
    val nivel = integer("nivel")
    val pontosVida = integer("pontos_vida")
    val ca = integer("ca") // Classe de Armadura
    val baseAtaque = integer("base_ataque")
    val dano = varchar("dano", 20) // Ex: "1d8", "2d6+2"
    val xpRecompensa = integer("xp_recompensa")
    val ouroMin = integer("ouro_min")
    val ouroMax = integer("ouro_max")
    val descricao = text("descricao")
    val tipo = varchar("tipo", 50).default("COMUM") // COMUM, ELITE, BOSS, LENDARIO
    val habilidades = text("habilidades").nullable() // JSON com habilidades especiais
    
    override val primaryKey = PrimaryKey(id)
}

object CombateHistoricoTable : Table("combate_historico") {
    val id = long("id").autoIncrement()
    val personagemId = long("personagem_id").references(PersonagensTable.id)
    val monstroNome = varchar("monstro_nome", 100)
    val resultado = varchar("resultado", 20) // VITORIA, DERROTA, FUGA
    val xpGanho = integer("xp_ganho")
    val ouroGanho = integer("ouro_ganho")
    val data = varchar("data", 50)
    val turnosDecorridos = integer("turnos_decorridos").default(0)
    val danoTotal = integer("dano_total").default(0)
    
    override val primaryKey = PrimaryKey(id)
}

object LootTable : Table("loot") {
    val id = long("id").autoIncrement()
    val monstroId = long("monstro_id").references(MonstrosTable.id)
    val itemId = long("item_id").references(ItensTable.id)
    val chance = integer("chance") // Porcentagem de drop (0-100)
    
    override val primaryKey = PrimaryKey(id)
}

// ==================== NOVAS TABELAS ====================

// Sistema de Localizações
object LocalizacoesTable : Table("localizacoes") {
    val id = long("id").autoIncrement()
    val nome = varchar("nome", 100)
    val descricao = text("descricao")
    val tipo = varchar("tipo", 50) // VILA, CIDADE, MASMORRA, FLORESTA, MONTANHA, etc.
    val nivelMinimo = integer("nivel_minimo").default(1)
    val nivelMaximo = integer("nivel_max").default(20)
    val monstrosDisponiveis = text("monstros_disponiveis") // JSON array de IDs
    val npcsDisponiveis = text("npcs_disponiveis") // JSON array de IDs
    val itensDisponiveis = text("itens_disponiveis").nullable() // JSON array de IDs
    val conexoes = text("conexoes") // JSON array de localizações conectadas
    val descoberta = bool("descoberta").default(false)
    
    override val primaryKey = PrimaryKey(id)
}

// Sistema de NPCs
object NPCsTable : Table("npcs") {
    val id = long("id").autoIncrement()
    val nome = varchar("nome", 100)
    val tipo = varchar("tipo", 50) // VENDEDOR, QUEST_GIVER, MESTRE, GUARDA, CIVIL
    val localizacao = varchar("localizacao", 100)
    val dialogoInicial = text("dialogo_inicial")
    val dialogoRepeticao = text("dialogo_repeticao").nullable()
    val vendedor = bool("vendedor").default(false)
    val itensVenda = text("itens_venda").nullable() // JSON array de IDs
    val questsDisponiveis = text("quests_disponiveis").nullable() // JSON array de IDs
    val reputacaoNecessaria = integer("reputacao_necessaria").default(0)
    val faccao = varchar("faccao", 50).nullable()
    
    override val primaryKey = PrimaryKey(id)
}

// Sistema de Missões (Quests)
object QuestsTable : Table("quests") {
    val id = long("id").autoIncrement()
    val nome = varchar("nome", 100)
    val descricao = text("descricao")
    val tipo = varchar("tipo", 50) // PRINCIPAL, SECUNDARIA, DIARIA, REPETICAO
    val npcId = long("npc_id").references(NPCsTable.id).nullable()
    val nivelMinimo = integer("nivel_minimo").default(1)
    val objetivos = text("objetivos") // JSON array de objetivos
    val recompensaXP = integer("recompensa_xp")
    val recompensaOuro = integer("recompensa_ouro")
    val recompensaItens = text("recompensa_itens").nullable() // JSON array de IDs
    val requisitos = text("requisitos").nullable() // JSON de pré-requisitos
    val historia = text("historia").nullable() // Texto narrativo da quest
    
    override val primaryKey = PrimaryKey(id)
}

// Progresso de Quests do Personagem
object PersonagemQuestsTable : Table("personagem_quests") {
    val id = long("id").autoIncrement()
    val personagemId = long("personagem_id").references(PersonagensTable.id)
    val questId = long("quest_id").references(QuestsTable.id)
    val status = varchar("status", 20) // ATIVA, COMPLETA, FALHADA, DISPONIVEL
    val progresso = text("progresso") // JSON com progresso dos objetivos
    val dataInicio = varchar("data_inicio", 50).nullable()
    val dataCompleta = varchar("data_completa", 50).nullable()
    
    override val primaryKey = PrimaryKey(id)
}

// Sistema de Magias
object MagiasTable : Table("magias") {
    val id = long("id").autoIncrement()
    val nome = varchar("nome", 100)
    val nivel = integer("nivel") // Nível da magia (1-9)
    val escola = varchar("escola", 50) // EVOCACAO, ILUSAO, NECROMANCIA, ABJURACAO, etc.
    val classe = varchar("classe", 50) // MAGO, CLERIGO, ambos
    val alcance = varchar("alcance", 50) // TOQUE, CURTO, MEDIO, LONGO
    val duracao = varchar("duracao", 50) // INSTANTANEO, CONCENTRACAO, PERMANENTE
    val componentes = varchar("componentes", 100) // V, S, M
    val tempoConjuracao = varchar("tempo_conjuracao", 50) // ACAO, ACAO_BONUS, REACAO
    val dano = varchar("dano", 50).nullable() // Ex: "3d6", "2d8+2"
    val tipoDano = varchar("tipo_dano", 50).nullable() // FOGO, GELO, RAIO, NECROTICO, etc.
    val efeito = text("efeito") // Descrição do efeito
    val descricao = text("descricao")
    val custoMana = integer("custo_mana").default(0)
    
    override val primaryKey = PrimaryKey(id)
}

// Grimório do Personagem
object PersonagemMagiasTable : Table("personagem_magias") {
    val id = long("id").autoIncrement()
    val personagemId = long("personagem_id").references(PersonagensTable.id)
    val magiaId = long("magia_id").references(MagiasTable.id)
    val preparada = bool("preparada").default(false)
    val usosRestantes = integer("usos_restantes").nullable()
    
    override val primaryKey = PrimaryKey(id)
}

// Sistema de Talentos
object TalentosTable : Table("talentos") {
    val id = long("id").autoIncrement()
    val nome = varchar("nome", 100)
    val descricao = text("descricao")
    val tipo = varchar("tipo", 50) // COMBATE, MAGIA, SOCIAL, GERAL
    val classe = varchar("classe", 50).nullable() // Específico de classe ou NULL para geral
    val nivelMinimo = integer("nivel_minimo").default(1)
    val prerequisitos = text("prerequisitos").nullable() // JSON de pré-requisitos
    val efeito = text("efeito") // Descrição do efeito mecânico
    val custoTalento = integer("custo_talento").default(1)
    
    override val primaryKey = PrimaryKey(id)
}

// Talentos do Personagem
object PersonagemTalentosTable : Table("personagem_talentos") {
    val id = long("id").autoIncrement()
    val personagemId = long("personagem_id").references(PersonagensTable.id)
    val talentoId = long("talento_id").references(TalentosTable.id)
    val nivel = integer("nivel").default(1) // Alguns talentos podem ter níveis
    val dataAquisicao = varchar("data_aquisicao", 50)
    
    override val primaryKey = PrimaryKey(id)
}

// Sistema de Conquistas (Achievements)
object ConquistasTable : Table("conquistas") {
    val id = long("id").autoIncrement()
    val nome = varchar("nome", 100)
    val descricao = text("descricao")
    val tipo = varchar("tipo", 50) // COMBATE, EXPLORACAO, COLECAO, SOCIAL, SECRETA
    val condicao = text("condicao") // JSON com condições para desbloquear
    val recompensaXP = integer("recompensa_xp").default(0)
    val recompensaOuro = integer("recompensa_ouro").default(0)
    val recompensaTitulo = varchar("recompensa_titulo", 100).nullable()
    val pontos = integer("pontos").default(10) // Pontos de achievement
    val secreta = bool("secreta").default(false)
    
    override val primaryKey = PrimaryKey(id)
}

// Conquistas do Personagem
object PersonagemConquistasTable : Table("personagem_conquistas") {
    val id = long("id").autoIncrement()
    val personagemId = long("personagem_id").references(PersonagensTable.id)
    val conquistaId = long("conquista_id").references(ConquistasTable.id)
    val desbloqueada = bool("desbloqueada").default(false)
    val progresso = integer("progresso").default(0)
    val dataDesbloqueio = varchar("data_desbloqueio", 50).nullable()
    
    override val primaryKey = PrimaryKey(id)
}

// Sistema de Facções e Reputação
object FaccoesTable : Table("faccoes") {
    val id = long("id").autoIncrement()
    val nome = varchar("nome", 100)
    val descricao = text("descricao")
    val tipo = varchar("tipo", 50) // GUILDA, REINO, ORDEM, CULTO, etc.
    val lider = varchar("lider", 100)
    val sede = varchar("sede", 100)
    val recompensas = text("recompensas") // JSON com recompensas por nível de reputação
    
    override val primaryKey = PrimaryKey(id)
}

// Reputação do Personagem com Facções
object PersonagemReputacaoTable : Table("personagem_reputacao") {
    val id = long("id").autoIncrement()
    val personagemId = long("personagem_id").references(PersonagensTable.id)
    val faccaoId = long("faccao_id").references(FaccoesTable.id)
    val pontos = integer("pontos").default(0) // -1000 a +1000
    val nivel = varchar("nivel", 20).default("NEUTRO") // ODIADO, HOSTIL, NEUTRO, AMIGAVEL, HONRADO, EXALTADO
    
    override val primaryKey = PrimaryKey(id)
}

// Sistema de Crafting - Receitas
object ReceitasTable : Table("receitas") {
    val id = long("id").autoIncrement()
    val nome = varchar("nome", 100)
    val profissao = varchar("profissao", 50) // FERREIRO, ALQUIMISTA, ENCANTADOR, etc.
    val nivelNecessario = integer("nivel_necessario").default(1)
    val itemResultado = long("item_resultado").references(ItensTable.id)
    val quantidadeResultado = integer("quantidade_resultado").default(1)
    val materiais = text("materiais") // JSON array de {itemId, quantidade}
    val custoOuro = integer("custo_ouro").default(0)
    val tempoCrafting = integer("tempo_crafting").default(1) // Em minutos
    val descricao = text("descricao")
    
    override val primaryKey = PrimaryKey(id)
}

// Profissões do Personagem
object PersonagemProfissoesTable : Table("personagem_profissoes") {
    val id = long("id").autoIncrement()
    val personagemId = long("personagem_id").references(PersonagensTable.id)
    val profissao = varchar("profissao", 50)
    val nivel = integer("nivel").default(1)
    val experiencia = integer("experiencia").default(0)
    
    override val primaryKey = PrimaryKey(id)
}

// Histórico de Ações do Personagem
object HistoricoAcoesTable : Table("historico_acoes") {
    val id = long("id").autoIncrement()
    val personagemId = long("personagem_id").references(PersonagensTable.id)
    val tipo = varchar("tipo", 50) // COMBATE, VIAGEM, QUEST, COMPRA, VENDA, LEVEL_UP, etc.
    val acao = text("acao") // Descrição da ação
    val detalhes = text("detalhes").nullable() // JSON com detalhes adicionais
    val timestamp = varchar("timestamp", 50)
    val localizacao = varchar("localizacao", 100).nullable()
    
    override val primaryKey = PrimaryKey(id)
}

// Sistema de Save States
object SaveStatesTable : Table("save_states") {
    val id = long("id").autoIncrement()
    val personagemId = long("personagem_id").references(PersonagensTable.id)
    val nome = varchar("nome", 100) // Nome do save
    val estadoCompleto = text("estado_completo") // JSON com todo o estado do jogo
    val timestamp = varchar("timestamp", 50)
    val localizacao = varchar("localizacao", 100)
    val nivel = integer("nivel")
    val screenshot = text("screenshot").nullable() // Arte ASCII do momento
    
    override val primaryKey = PrimaryKey(id)
}

// Sistema de Eventos Aleatórios
object EventosTable : Table("eventos") {
    val id = long("id").autoIncrement()
    val nome = varchar("nome", 100)
    val descricao = text("descricao")
    val tipo = varchar("tipo", 50) // COMBATE, TESOURO, ENCONTRO, ARMADILHA, MISTERIO
    val localizacao = varchar("localizacao", 100).nullable() // NULL = qualquer lugar
    val chance = integer("chance").default(10) // Porcentagem de ocorrer
    val nivelMinimo = integer("nivel_minimo").default(1)
    val opcoes = text("opcoes") // JSON com opções e consequências
    val recompensas = text("recompensas").nullable() // JSON com possíveis recompensas
    
    override val primaryKey = PrimaryKey(id)
}

// Diálogos e Conversas
object DialogosTable : Table("dialogos") {
    val id = long("id").autoIncrement()
    val npcId = long("npc_id").references(NPCsTable.id)
    val texto = text("texto")
    val opcoes = text("opcoes") // JSON array de opções de resposta
    val condicoes = text("condicoes").nullable() // JSON de condições para aparecer
    val consequencias = text("consequencias").nullable() // JSON de efeitos das escolhas
    val proximoDialogoId = long("proximo_dialogo_id").nullable()
    
    override val primaryKey = PrimaryKey(id)
}

// Condições de Status (Buffs/Debuffs)
object CondicoesStatusTable : Table("condicoes_status") {
    val id = long("id").autoIncrement()
    val nome = varchar("nome", 100)
    val tipo = varchar("tipo", 20) // BUFF, DEBUFF
    val descricao = text("descricao")
    val efeito = text("efeito") // JSON com efeitos mecânicos
    val duracao = integer("duracao") // Em turnos, -1 = permanente
    val icone = varchar("icone", 10).nullable() // Emoji ou símbolo
    
    override val primaryKey = PrimaryKey(id)
}

// Condições Ativas no Personagem
object PersonagemCondicoesTable : Table("personagem_condicoes") {
    val id = long("id").autoIncrement()
    val personagemId = long("personagem_id").references(PersonagensTable.id)
    val condicaoId = long("condicao_id").references(CondicoesStatusTable.id)
    val turnosRestantes = integer("turnos_restantes")
    val intensidade = integer("intensidade").default(1) // Nível do efeito
    
    override val primaryKey = PrimaryKey(id)
}

// Lojas e Inventário de Vendedores
object LojasTable : Table("lojas") {
    val id = long("id").autoIncrement()
    val npcId = long("npc_id").references(NPCsTable.id)
    val nome = varchar("nome", 100)
    val tipo = varchar("tipo", 50) // FERREIRO, TAVERNA, ALQUIMISTA, GERAL, etc.
    val inventario = text("inventario") // JSON array de {itemId, quantidade, preco}
    val descontoReputacao = integer("desconto_reputacao").default(0) // % de desconto por reputação
    val atualizacaoInventario = varchar("atualizacao_inventario", 50) // Frequência de atualização
    
    override val primaryKey = PrimaryKey(id)
}

