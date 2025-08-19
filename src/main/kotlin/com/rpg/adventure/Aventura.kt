package com.rpg.adventure

import com.rpg.character.Personagem
import com.rpg.core.factories.Monstro
import com.rpg.core.factories.MonstroFactory
import com.rpg.items.*
import kotlin.random.Random

enum class TipoEvento {
    COMBATE,
    TESOURO,
    ARMADILHA,
    PUZZLE,
    NARRATIVO,
    CHEFE,
    DESCANSO
}

data class EventoAventura(
    val id: String,
    val tipo: TipoEvento,
    val titulo: String,
    val descricao: String,
    val opcoes: List<String>,
    val consequencias: Map<Int, String>,
    val monstros: List<String> = emptyList(),
    val tesouro: Map<String, Int> = emptyMap(), // item -> quantidade
    val xpRecompensa: Int = 0,
    val proximosEventos: Map<Int, String> = emptyMap() // escolha -> próximo evento
)

data class AreaAventura(
    val id: String,
    val nome: String,
    val descricao: String,
    val eventos: List<EventoAventura>,
    val eventoInicial: String,
    val podeDescansar: Boolean = false
)

abstract class Aventura {
    abstract fun getNome(): String
    abstract fun getDescricao(): String
    abstract fun getAreas(): List<AreaAventura>
    abstract fun getAreaInicial(): String
    abstract fun getHistoriaIntroducao(): String
    abstract fun getHistoriaConclusao(): String
    
    fun getArea(id: String): AreaAventura? {
        return getAreas().find { it.id == id }
    }
    
    fun getEvento(areaId: String, eventoId: String): EventoAventura? {
        return getArea(areaId)?.eventos?.find { it.id == eventoId }
    }
}

class SegredoMontanhaGelada : Aventura() {
    
    private val monstroFactory = MonstroFactory()
    
    override fun getNome(): String = "O Segredo da Montanha Gelada"
    
    override fun getDescricao(): String = 
        "Uma aventura épica nas profundezas de uma montanha assombrada por um necromante maligno."
    
    override fun getAreaInicial(): String = "entrada_caverna"
    
    override fun getHistoriaIntroducao(): String = """
        ╔══════════════════════════════════════════════════════════════╗
        ║                O SEGREDO DA MONTANHA GELADA                  ║
        ╚══════════════════════════════════════════════════════════════╝
        
        Há semanas, estranhos acontecimentos assolam a região de Pedravale. 
        Mortos-vivos vagam pelas estradas durante a noite, atacando viajantes 
        e comerciantes. As colheitas começaram a apodrecer nos campos, e um 
        frio sobrenatural se espalha pela terra.
        
        Os anciãos da vila sussurram sobre antigas lendas da Montanha Gelada, 
        um pico distante onde, há muito tempo, um poderoso necromante foi 
        aprisionado. Dizem que seu túmulo foi selado com magia antiga, mas 
        os sinais sugerem que algo terrível está despertando.
        
        Você, corajoso aventureiro, foi contratado pelo Conselho da Vila para 
        investigar a montanha e descobrir a fonte desta maldição. Armado com 
        sua coragem e determinação, você se dirige às cavernas que levam ao 
        coração da montanha, onde antigos segredos aguardam para serem 
        revelados.
        
        Que os deuses protejam sua alma, pois você está prestes a enfrentar 
        horrores que desafiam a própria morte...
    """.trimIndent()
    
    override fun getHistoriaConclusao(): String = """
        ╔══════════════════════════════════════════════════════════════╗
        ║                        VITÓRIA!                              ║
        ╚══════════════════════════════════════════════════════════════╝
        
        Com a destruição do necromante Malachar, a maldição que assolava a 
        região finalmente se desfez. O frio sobrenatural se dissipa, as 
        colheitas voltam a crescer, e os mortos-vivos retornam ao descanso 
        eterno.
        
        Você emerge das profundezas da Montanha Gelada como um herói 
        verdadeiro. Sua coragem e determinação salvaram não apenas Pedravale, 
        mas toda a região das garras da morte e da escuridão.
        
        O Conselho da Vila o recebe com honras, e bardos já começam a compor 
        canções sobre seus feitos heroicos. Seu nome será lembrado por 
        gerações como aquele que enfrentou as trevas e emergiu vitorioso.
        
        Mas esta é apenas uma de suas muitas aventuras. O mundo está cheio 
        de mistérios, tesouros e perigos esperando por um herói corajoso 
        como você...
        
        Parabéns, aventureiro! Você completou "O Segredo da Montanha Gelada"!
    """.trimIndent()
    
    override fun getAreas(): List<AreaAventura> {
        return listOf(
            criarEntradaCaverna(),
            criarTuneisProfundos(),
            criarCamaraEcos(),
            criarSalaRituais(),
            criarSantuarioFinal()
        )
    }
    
    private fun criarEntradaCaverna(): AreaAventura {
        return AreaAventura(
            id = "entrada_caverna",
            nome = "Entrada da Caverna",
            descricao = "A entrada sombria da caverna se abre diante de você como uma boca gigantesca. " +
                        "Ar frio e úmido escapa das profundezas, carregando o cheiro de mofo e algo mais... " +
                        "algo morto. Cristais de gelo se formam nas paredes de pedra, e você pode ouvir " +
                        "ecos distantes vindos das profundezas.",
            eventoInicial = "chegada_entrada",
            podeDescansar = true,
            eventos = listOf(
                EventoAventura(
                    id = "chegada_entrada",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Chegada à Entrada",
                    descricao = "Você finalmente chega à entrada da Montanha Gelada. O vento frio corta " +
                               "sua pele enquanto você observa a abertura escura à sua frente. Pegadas " +
                               "estranhas na neve sugerem que criaturas não-naturais passaram por aqui " +
                               "recentemente.",
                    opcoes = listOf(
                        "Entrar na caverna imediatamente",
                        "Investigar as pegadas primeiro",
                        "Preparar equipamentos antes de entrar"
                    ),
                    consequencias = mapOf(
                        1 to "Você avança corajosamente para as trevas.",
                        2 to "As pegadas parecem ser de esqueletos... muitos esqueletos.",
                        3 to "Você verifica suas armas e se prepara mentalmente."
                    ),
                    proximosEventos = mapOf(
                        1 to "primeira_camara",
                        2 to "investigar_pegadas",
                        3 to "preparar_equipamentos"
                    )
                ),
                
                EventoAventura(
                    id = "investigar_pegadas",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Investigando as Pegadas",
                    descricao = "Examinando mais de perto, você confirma suas suspeitas: as pegadas são " +
                               "de esqueletos, pelo menos uma dúzia deles. Eles saíram da caverna " +
                               "recentemente e seguiram em direção à vila. Você também nota marcas " +
                               "de garras nas pedras próximas à entrada.",
                    opcoes = listOf("Entrar na caverna"),
                    consequencias = mapOf(1 to "Agora você sabe que está no caminho certo."),
                    xpRecompensa = 25,
                    proximosEventos = mapOf(1 to "primeira_camara")
                ),
                
                EventoAventura(
                    id = "preparar_equipamentos",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Preparando Equipamentos",
                    descricao = "Você toma um momento para verificar suas armas, ajustar sua armadura " +
                               "e mentalizar-se para os perigos à frente. Esta preparação pode ser " +
                               "crucial para sua sobrevivência.",
                    opcoes = listOf("Entrar na caverna"),
                    consequencias = mapOf(1 to "Você se sente mais confiante e preparado."),
                    xpRecompensa = 15,
                    proximosEventos = mapOf(1 to "primeira_camara")
                ),
                
                EventoAventura(
                    id = "primeira_camara",
                    tipo = TipoEvento.COMBATE,
                    titulo = "Primeira Câmara",
                    descricao = "Você entra em uma câmara ampla iluminada por cristais azulados que " +
                               "emitem uma luz fantasmagórica. No centro da sala, dois esqueletos " +
                               "guardam uma passagem que leva mais fundo na montanha. Eles se voltam " +
                               "para você com órbitas vazias brilhando com fogo sobrenatural.",
                    opcoes = listOf("Atacar os esqueletos", "Tentar passar furtivamente"),
                    monstros = listOf("esqueleto", "esqueleto"),
                    consequencias = mapOf(
                        1 to "Você saca sua arma e parte para o combate!",
                        2 to "Você tenta se esgueirar pelas sombras..."
                    ),
                    proximosEventos = mapOf(
                        1 to "pos_combate_esqueletos",
                        2 to "tentativa_furtiva"
                    )
                ),
                
                EventoAventura(
                    id = "tentativa_furtiva",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Tentativa Furtiva",
                    descricao = "Você tenta se mover silenciosamente pelas sombras, mas os mortos-vivos " +
                               "são sensíveis à presença dos vivos. Os esqueletos detectam sua presença " +
                               "e se voltam para você, rangendo os ossos ameaçadoramente.",
                    opcoes = listOf("Lutar contra os esqueletos"),
                    monstros = listOf("esqueleto", "esqueleto"),
                    consequencias = mapOf(1 to "O combate é inevitável!"),
                    proximosEventos = mapOf(1 to "pos_combate_esqueletos")
                ),
                
                EventoAventura(
                    id = "pos_combate_esqueletos",
                    tipo = TipoEvento.TESOURO,
                    titulo = "Após o Combate",
                    descricao = "Com os esqueletos destruídos, você examina a câmara. Entre os ossos " +
                               "espalhados, você encontra alguns itens úteis que pertenciam a " +
                               "aventureiros menos afortunados.",
                    opcoes = listOf("Coletar os itens e continuar"),
                    tesouro = mapOf("Poção de Cura" to 1, "Tocha" to 2),
                    xpRecompensa = 50,
                    consequencias = mapOf(1 to "Você coleta os itens e se prepara para continuar."),
                    proximosEventos = mapOf(1 to "tuneis_profundos")
                )
            )
        )
    }
    
    private fun criarTuneisProfundos(): AreaAventura {
        return AreaAventura(
            id = "tuneis_profundos",
            nome = "Túneis Profundos",
            descricao = "Uma rede de túneis escuros se estende em várias direções. As paredes são " +
                        "cobertas por um musgo estranho que brilha fracamente, e você pode ouvir " +
                        "gotejamentos distantes ecoando pelas passagens. O ar aqui é ainda mais " +
                        "frio e carregado de energia necromântica.",
            eventoInicial = "tuneis_profundos",
            eventos = listOf(
                EventoAventura(
                    id = "tuneis_profundos",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Nos Túneis Profundos",
                    descricao = "Você se encontra em uma encruzilhada de túneis. Três passagens se " +
                               "abrem à sua frente: uma à esquerda que desce abruptamente, uma ao " +
                               "centro que segue em linha reta, e uma à direita que sobe ligeiramente. " +
                               "Cada uma emana uma aura diferente de perigo.",
                    opcoes = listOf(
                        "Seguir pelo túnel da esquerda (descendente)",
                        "Seguir pelo túnel central (reto)",
                        "Seguir pelo túnel da direita (ascendente)"
                    ),
                    consequencias = mapOf(
                        1 to "Você desce pelas profundezas da montanha.",
                        2 to "Você segue em frente pelo caminho mais direto.",
                        3 to "Você sobe em direção a câmaras superiores."
                    ),
                    proximosEventos = mapOf(
                        1 to "tunel_esquerda",
                        2 to "tunel_centro",
                        3 to "tunel_direita"
                    )
                ),
                
                EventoAventura(
                    id = "tunel_esquerda",
                    tipo = TipoEvento.ARMADILHA,
                    titulo = "Túnel da Esquerda - Armadilha",
                    descricao = "O túnel desce em uma espiral íngreme. Quando você está no meio da " +
                               "descida, seu pé pisa em uma pedra solta que ativa um mecanismo antigo. " +
                               "Dardos envenenados são disparados das paredes!",
                    opcoes = listOf(
                        "Tentar se esquivar rapidamente",
                        "Se proteger com o escudo",
                        "Rolar para frente"
                    ),
                    consequencias = mapOf(
                        1 to "Você tenta se esquivar dos dardos mortais.",
                        2 to "Você levanta seu escudo para se proteger.",
                        3 to "Você rola para frente tentando escapar."
                    ),
                    proximosEventos = mapOf(
                        1 to "pos_armadilha_esquiva",
                        2 to "pos_armadilha_escudo",
                        3 to "pos_armadilha_rolamento"
                    )
                ),
                
                EventoAventura(
                    id = "pos_armadilha_esquiva",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Esquiva da Armadilha",
                    descricao = "Seus reflexos rápidos salvam sua vida! Você consegue se esquivar da " +
                               "maioria dos dardos, mas um deles raspa seu braço, causando um ferimento " +
                               "leve. Felizmente, o veneno parece ter perdido sua potência com o tempo.",
                    opcoes = listOf("Continuar descendo"),
                    consequencias = mapOf(1 to "Você perde 2 pontos de vida mas continua."),
                    xpRecompensa = 30,
                    proximosEventos = mapOf(1 to "camara_ecos")
                ),
                
                EventoAventura(
                    id = "pos_armadilha_escudo",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Proteção com Escudo",
                    descricao = "Seu escudo absorve o impacto dos dardos com um som metálico. Alguns " +
                               "dardos ficam cravados na madeira e metal, mas você sai ileso. Uma " +
                               "estratégia sábia!",
                    opcoes = listOf("Continuar descendo"),
                    consequencias = mapOf(1 to "Você sai ileso da armadilha."),
                    xpRecompensa = 40,
                    proximosEventos = mapOf(1 to "camara_ecos")
                ),
                
                EventoAventura(
                    id = "pos_armadilha_rolamento",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Rolamento de Emergência",
                    descricao = "Você rola para frente em um movimento desesperado. A manobra funciona " +
                               "parcialmente - você evita a maioria dos dardos, mas bate contra a " +
                               "parede do túnel no final do rolamento.",
                    opcoes = listOf("Continuar descendo"),
                    consequencias = mapOf(1 to "Você perde 1 ponto de vida mas evitou o pior."),
                    xpRecompensa = 25,
                    proximosEventos = mapOf(1 to "camara_ecos")
                ),
                
                EventoAventura(
                    id = "tunel_centro",
                    tipo = TipoEvento.COMBATE,
                    titulo = "Túnel Central - Goblins",
                    descricao = "O túnel central leva a uma câmara onde três goblins estabeleceram " +
                               "um pequeno acampamento. Eles estão comendo algo que você prefere não " +
                               "identificar. Quando te veem, gritam em sua língua gutural e pegam " +
                               "suas armas improvisadas.",
                    opcoes = listOf("Atacar os goblins", "Tentar negociar"),
                    monstros = listOf("goblin", "goblin", "goblin"),
                    consequencias = mapOf(
                        1 to "Você parte para o ataque!",
                        2 to "Você tenta se comunicar com os goblins."
                    ),
                    proximosEventos = mapOf(
                        1 to "pos_combate_goblins",
                        2 to "negociacao_goblins"
                    )
                ),
                
                EventoAventura(
                    id = "negociacao_goblins",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Negociação com Goblins",
                    descricao = "Você tenta falar com os goblins, mas eles são hostis demais e " +
                               "assustados demais para ouvir. Um deles grita algo sobre 'mestre das " +
                               "sombras' antes de atacar você com uma clava improvisada.",
                    opcoes = listOf("Lutar contra os goblins"),
                    monstros = listOf("goblin", "goblin", "goblin"),
                    consequencias = mapOf(1 to "A negociação falhou, o combate é inevitável!"),
                    proximosEventos = mapOf(1 to "pos_combate_goblins")
                ),
                
                EventoAventura(
                    id = "pos_combate_goblins",
                    tipo = TipoEvento.TESOURO,
                    titulo = "Acampamento dos Goblins",
                    descricao = "Após derrotar os goblins, você examina seu acampamento improvisado. " +
                               "Entre os pertences sujos, você encontra algumas moedas e um mapa " +
                               "rudimentar que mostra parte dos túneis.",
                    opcoes = listOf("Coletar os itens e estudar o mapa"),
                    tesouro = mapOf("Mapa dos Túneis" to 1),
                    xpRecompensa = 75,
                    consequencias = mapOf(1 to "O mapa pode ser útil para navegar pelos túneis."),
                    proximosEventos = mapOf(1 to "camara_ecos")
                ),
                
                EventoAventura(
                    id = "tunel_direita",
                    tipo = TipoEvento.TESOURO,
                    titulo = "Túnel da Direita - Câmara do Tesouro",
                    descricao = "O túnel ascendente leva a uma pequena câmara que parece ter sido " +
                               "usada como depósito. Baús antigos e sacos de couro estão espalhados " +
                               "pelo chão. A maioria está vazia ou contém apenas poeira, mas um " +
                               "baú menor parece intacto.",
                    opcoes = listOf(
                        "Abrir o baú cuidadosamente",
                        "Verificar se há armadilhas primeiro",
                        "Ignorar o baú e continuar"
                    ),
                    consequencias = mapOf(
                        1 to "Você abre o baú com cuidado.",
                        2 to "Você examina o baú em busca de armadilhas.",
                        3 to "Você decide não arriscar e continua."
                    ),
                    proximosEventos = mapOf(
                        1 to "abrir_bau_direto",
                        2 to "verificar_bau",
                        3 to "camara_ecos"
                    )
                ),
                
                EventoAventura(
                    id = "abrir_bau_direto",
                    tipo = TipoEvento.TESOURO,
                    titulo = "Abrindo o Baú",
                    descricao = "Você abre o baú e... nada de ruim acontece! Dentro você encontra " +
                               "algumas moedas de ouro e uma poção que brilha com luz azulada.",
                    opcoes = listOf("Coletar os itens"),
                    tesouro = mapOf("Poção de Cura Grande" to 1),
                    xpRecompensa = 60,
                    consequencias = mapOf(1 to "Você encontrou itens valiosos!"),
                    proximosEventos = mapOf(1 to "camara_ecos")
                ),
                
                EventoAventura(
                    id = "verificar_bau",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Verificando Armadilhas",
                    descricao = "Sua cautela é recompensada! Você descobre uma agulha envenenada " +
                               "escondida na fechadura do baú. Após desarmá-la cuidadosamente, " +
                               "você pode abrir o baú com segurança.",
                    opcoes = listOf("Abrir o baú desarmado"),
                    tesouro = mapOf("Poção de Cura Grande" to 1, "Antídoto" to 1),
                    xpRecompensa = 80,
                    consequencias = mapOf(1 to "Sua cautela foi recompensada com itens extras!"),
                    proximosEventos = mapOf(1 to "camara_ecos")
                )
            )
        )
    }
    
    private fun criarCamaraEcos(): AreaAventura {
        return AreaAventura(
            id = "camara_ecos",
            nome = "Câmara dos Ecos",
            descricao = "Uma vasta câmara circular com um teto abobadado que se perde na escuridão. " +
                        "Cada som ecoa múltiplas vezes, criando uma cacofonia perturbadora. No centro " +
                        "da câmara há um poço profundo cercado por runas antigas que brilham fracamente.",
            eventoInicial = "camara_ecos",
            podeDescansar = true,
            eventos = listOf(
                EventoAventura(
                    id = "camara_ecos",
                    tipo = TipoEvento.PUZZLE,
                    titulo = "A Câmara dos Ecos",
                    descricao = "Você entra na impressionante Câmara dos Ecos. O poço central emite " +
                               "uma luz azul pálida, e você pode ouvir sussurros vindos de suas " +
                               "profundezas. As runas ao redor parecem formar algum tipo de padrão " +
                               "ou código. Há três passagens saindo desta câmara.",
                    opcoes = listOf(
                        "Examinar as runas ao redor do poço",
                        "Olhar dentro do poço",
                        "Seguir pela passagem norte",
                        "Seguir pela passagem leste",
                        "Seguir pela passagem oeste"
                    ),
                    consequencias = mapOf(
                        1 to "Você se aproxima das runas misteriosas.",
                        2 to "Você se debruça sobre o poço profundo.",
                        3 to "Você segue pela passagem que leva ao norte.",
                        4 to "Você segue pela passagem que leva ao leste.",
                        5 to "Você segue pela passagem que leva ao oeste."
                    ),
                    proximosEventos = mapOf(
                        1 to "examinar_runas",
                        2 to "olhar_poco",
                        3 to "passagem_norte",
                        4 to "sala_rituais",
                        5 to "passagem_oeste"
                    )
                ),
                
                EventoAventura(
                    id = "examinar_runas",
                    tipo = TipoEvento.PUZZLE,
                    titulo = "Examinando as Runas",
                    descricao = "As runas são de uma linguagem antiga, mas você consegue decifrar " +
                               "algumas palavras: 'proteção', 'selamento' e 'despertar'. Parece " +
                               "que este poço foi usado para selar algo poderoso. Você nota que " +
                               "algumas runas estão rachadas e sua luz está fraca.",
                    opcoes = listOf(
                        "Tentar reparar as runas rachadas",
                        "Deixar as runas como estão",
                        "Investigar mais profundamente"
                    ),
                    consequencias = mapOf(
                        1 to "Você tenta canalizar energia para reparar as runas.",
                        2 to "Você decide não mexer com magia antiga.",
                        3 to "Você estuda as runas mais detalhadamente."
                    ),
                    xpRecompensa = 100,
                    proximosEventos = mapOf(
                        1 to "reparar_runas",
                        2 to "escolher_passagem",
                        3 to "investigar_runas"
                    )
                ),
                
                EventoAventura(
                    id = "reparar_runas",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Reparando as Runas",
                    descricao = "Você coloca suas mãos sobre as runas rachadas e sente uma energia " +
                               "antiga fluir através de você. As runas brilham mais intensamente " +
                               "por um momento, e você sente que fortaleceu o selamento. Isso pode " +
                               "ser útil mais tarde.",
                    opcoes = listOf("Escolher uma passagem para seguir"),
                    xpRecompensa = 150,
                    consequencias = mapOf(1 to "Você fortaleceu o selamento antigo."),
                    proximosEventos = mapOf(1 to "escolher_passagem")
                ),
                
                EventoAventura(
                    id = "investigar_runas",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Investigação Profunda",
                    descricao = "Estudando as runas mais cuidadosamente, você descobre que elas " +
                               "contam a história de um necromante chamado Malachar que foi " +
                               "aprisionado nestas profundezas há séculos. As runas também " +
                               "revelam que ele está tentando quebrar o selamento.",
                    opcoes = listOf("Escolher uma passagem para seguir"),
                    xpRecompensa = 120,
                    consequencias = mapOf(1 to "Você aprendeu sobre o necromante Malachar."),
                    proximosEventos = mapOf(1 to "escolher_passagem")
                ),
                
                EventoAventura(
                    id = "olhar_poco",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Olhando no Poço",
                    descricao = "Você se debruça sobre a borda do poço e olha para baixo. A " +
                               "profundidade parece infinita, mas você pode ver uma luz vermelha " +
                               "pulsante muito no fundo. Os sussurros ficam mais altos, e por um " +
                               "momento você ouve uma voz grave dizendo: 'Venha... junte-se a mim...'",
                    opcoes = listOf("Recuar rapidamente do poço"),
                    consequencias = mapOf(1 to "Você se afasta do poço perturbador."),
                    xpRecompensa = 50,
                    proximosEventos = mapOf(1 to "escolher_passagem")
                ),
                
                EventoAventura(
                    id = "escolher_passagem",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Escolhendo o Caminho",
                    descricao = "Agora você deve escolher qual passagem seguir para continuar " +
                               "sua jornada pelas profundezas da montanha.",
                    opcoes = listOf(
                        "Passagem Norte",
                        "Passagem Leste",
                        "Passagem Oeste"
                    ),
                    consequencias = mapOf(
                        1 to "Você segue para o norte.",
                        2 to "Você segue para o leste.",
                        3 to "Você segue para o oeste."
                    ),
                    proximosEventos = mapOf(
                        1 to "passagem_norte",
                        2 to "sala_rituais",
                        3 to "passagem_oeste"
                    )
                ),
                
                EventoAventura(
                    id = "passagem_norte",
                    tipo = TipoEvento.COMBATE,
                    titulo = "Passagem Norte - Zumbis",
                    descricao = "A passagem norte leva a um corredor estreito onde dois zumbis " +
                               "vagam sem rumo. Quando sentem sua presença, eles se voltam para " +
                               "você com movimentos desajeitados mas determinados. Seus olhos " +
                               "brilham com fome insaciável.",
                    opcoes = listOf("Lutar contra os zumbis"),
                    monstros = listOf("zumbi", "zumbi"),
                    consequencias = mapOf(1 to "Você enfrenta os mortos-vivos!"),
                    proximosEventos = mapOf(1 to "pos_combate_zumbis")
                ),
                
                EventoAventura(
                    id = "pos_combate_zumbis",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Após Derrotar os Zumbis",
                    descricao = "Com os zumbis destruídos, você continua pelo corredor e encontra " +
                               "uma escadaria que desce ainda mais fundo na montanha. O ar fica " +
                               "mais frio e carregado de energia necromântica a cada passo.",
                    opcoes = listOf("Descer as escadas"),
                    xpRecompensa = 80,
                    consequencias = mapOf(1 to "Você desce para níveis mais profundos."),
                    proximosEventos = mapOf(1 to "sala_rituais")
                ),
                
                EventoAventura(
                    id = "passagem_oeste",
                    tipo = TipoEvento.TESOURO,
                    titulo = "Passagem Oeste - Biblioteca Abandonada",
                    descricao = "A passagem oeste leva a uma antiga biblioteca. As estantes estão " +
                               "cobertas de poeira e teias de aranha, mas alguns livros ainda " +
                               "estão legíveis. Você encontra um grimório que contém conhecimento " +
                               "sobre combate a mortos-vivos.",
                    opcoes = listOf("Estudar o grimório"),
                    tesouro = mapOf("Grimório Anti-Mortos" to 1),
                    xpRecompensa = 100,
                    consequencias = mapOf(1 to "Você aprende técnicas contra mortos-vivos."),
                    proximosEventos = mapOf(1 to "sala_rituais")
                )
            )
        )
    }
    
    private fun criarSalaRituais(): AreaAventura {
        return AreaAventura(
            id = "sala_rituais",
            nome = "Sala dos Rituais",
            descricao = "Uma câmara hexagonal com símbolos necromânticos gravados nas paredes. " +
                        "Círculos mágicos estão desenhados no chão com o que parece ser sangue " +
                        "seco. Velas negras queimam com chamas verdes, e o ar vibra com energia " +
                        "sinistra. Esta claramente era usada para rituais sombrios.",
            eventoInicial = "sala_rituais",
            eventos = listOf(
                EventoAventura(
                    id = "sala_rituais",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "A Sala dos Rituais",
                    descricao = "Você entra na sinistra Sala dos Rituais. O ambiente é opressivo, " +
                               "e você pode sentir o peso de incontáveis atos malignos realizados " +
                               "aqui. No centro da sala há um altar de pedra manchado de sangue, " +
                               "e uma passagem ao fundo leva ao que parece ser o coração da montanha.",
                    opcoes = listOf(
                        "Examinar o altar",
                        "Investigar os círculos mágicos",
                        "Seguir direto para a passagem final",
                        "Tentar purificar a sala"
                    ),
                    consequencias = mapOf(
                        1 to "Você se aproxima do altar sinistro.",
                        2 to "Você examina os círculos necromânticos.",
                        3 to "Você ignora a sala e segue em frente.",
                        4 to "Você tenta limpar a energia maligna do local."
                    ),
                    proximosEventos = mapOf(
                        1 to "examinar_altar",
                        2 to "investigar_circulos",
                        3 to "santuario_final",
                        4 to "purificar_sala"
                    )
                ),
                
                EventoAventura(
                    id = "examinar_altar",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Examinando o Altar",
                    descricao = "O altar de pedra negra está coberto de runas que pulsam com " +
                               "energia maligna. Você pode ver que foi usado para sacrifícios " +
                               "recentes - há sangue fresco em algumas das ranhuras. Tocando " +
                               "o altar, você tem visões perturbadoras de rituais sombrios.",
                    opcoes = listOf(
                        "Tentar quebrar o altar",
                        "Deixar o altar intacto",
                        "Procurar por itens escondidos"
                    ),
                    consequencias = mapOf(
                        1 to "Você tenta destruir a fonte de poder maligno.",
                        2 to "Você decide não mexer com o altar.",
                        3 to "Você procura por compartimentos secretos."
                    ),
                    proximosEventos = mapOf(
                        1 to "quebrar_altar",
                        2 to "continuar_aventura",
                        3 to "buscar_segredos"
                    )
                ),
                
                EventoAventura(
                    id = "quebrar_altar",
                    tipo = TipoEvento.COMBATE,
                    titulo = "Quebrando o Altar",
                    descricao = "Quando você ergue sua arma para destruir o altar, a energia " +
                               "maligna acumulada se manifesta como espíritos sombrios que " +
                               "atacam para proteger seu foco de poder. Você deve enfrentá-los " +
                               "para quebrar o altar!",
                    opcoes = listOf("Lutar contra os espíritos"),
                    monstros = listOf("esqueleto", "esqueleto"),
                    xpRecompensa = 200,
                    consequencias = mapOf(1 to "Você enfrenta as manifestações do mal!"),
                    proximosEventos = mapOf(1 to "altar_quebrado")
                ),
                
                EventoAventura(
                    id = "altar_quebrado",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Altar Destruído",
                    descricao = "Com um estrondo ensurdecedor, o altar se parte ao meio. Uma onda " +
                               "de energia purificadora varre a sala, apagando as velas negras e " +
                               "fazendo os símbolos nas paredes desaparecerem. Você enfraqueceu " +
                               "significativamente o poder do necromante!",
                    opcoes = listOf("Continuar para o santuário final"),
                    xpRecompensa = 250,
                    consequencias = mapOf(1 to "O poder do necromante foi enfraquecido!"),
                    proximosEventos = mapOf(1 to "santuario_final")
                ),
                
                EventoAventura(
                    id = "investigar_circulos",
                    tipo = TipoEvento.PUZZLE,
                    titulo = "Investigando os Círculos",
                    descricao = "Os círculos mágicos no chão formam um padrão complexo. Você " +
                               "reconhece alguns símbolos de invocação e controle de mortos-vivos. " +
                               "Parece que estes círculos são usados para amplificar o poder " +
                               "necromântico do usuário.",
                    opcoes = listOf(
                        "Tentar interromper os círculos",
                        "Estudar os símbolos para aprender",
                        "Deixar os círculos intactos"
                    ),
                    consequencias = mapOf(
                        1 to "Você tenta quebrar o padrão mágico.",
                        2 to "Você estuda a magia necromântica.",
                        3 to "Você não mexe nos círculos."
                    ),
                    proximosEventos = mapOf(
                        1 to "interromper_circulos",
                        2 to "estudar_necromancia",
                        3 to "continuar_aventura"
                    )
                ),
                
                EventoAventura(
                    id = "interromper_circulos",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Interrompendo os Círculos",
                    descricao = "Você risca linhas através dos círculos mágicos, quebrando o " +
                               "padrão cuidadosamente desenhado. Imediatamente você sente a " +
                               "energia necromântica da sala diminuir. Isso deve dificultar " +
                               "os rituais do necromante.",
                    opcoes = listOf("Continuar para o santuário"),
                    xpRecompensa = 150,
                    consequencias = mapOf(1 to "Você sabotou os rituais necromânticos."),
                    proximosEventos = mapOf(1 to "santuario_final")
                ),
                
                EventoAventura(
                    id = "estudar_necromancia",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Estudando Necromancia",
                    descricao = "Você estuda os símbolos cuidadosamente, aprendendo sobre as " +
                               "técnicas usadas pelo necromante. Este conhecimento pode ser " +
                               "útil no confronto final, mas também deixa sua alma um pouco " +
                               "mais sombria.",
                    opcoes = listOf("Continuar para o santuário"),
                    xpRecompensa = 100,
                    consequencias = mapOf(1 to "Você aprendeu técnicas necromânticas."),
                    proximosEventos = mapOf(1 to "santuario_final")
                ),
                
                EventoAventura(
                    id = "purificar_sala",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Purificando a Sala",
                    descricao = "Você se concentra em canalizar energia positiva para limpar a " +
                               "sala da influência maligna. É um processo exaustivo, mas você " +
                               "consegue purificar parcialmente o ambiente. As velas se apagam " +
                               "e alguns símbolos desaparecem das paredes.",
                    opcoes = listOf("Continuar para o santuário"),
                    xpRecompensa = 180,
                    consequencias = mapOf(1 to "Você purificou parcialmente a sala."),
                    proximosEventos = mapOf(1 to "santuario_final")
                ),
                
                EventoAventura(
                    id = "buscar_segredos",
                    tipo = TipoEvento.TESOURO,
                    titulo = "Procurando Segredos",
                    descricao = "Sua busca cuidadosa revela um compartimento secreto no altar. " +
                               "Dentro você encontra um amuleto que pulsa com energia protetora - " +
                               "provavelmente um item usado por um clérigo que tentou parar o " +
                               "necromante no passado.",
                    opcoes = listOf("Pegar o amuleto e continuar"),
                    tesouro = mapOf("Amuleto de Proteção" to 1),
                    xpRecompensa = 120,
                    consequencias = mapOf(1 to "Você encontrou um amuleto protetor!"),
                    proximosEventos = mapOf(1 to "santuario_final")
                ),
                
                EventoAventura(
                    id = "continuar_aventura",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Continuando a Aventura",
                    descricao = "Você decide não se demorar mais nesta sala sinistra e segue " +
                               "em direção à passagem que leva ao coração da montanha, onde " +
                               "certamente encontrará o necromante.",
                    opcoes = listOf("Seguir para o santuário final"),
                    consequencias = mapOf(1 to "Você se dirige ao confronto final."),
                    proximosEventos = mapOf(1 to "santuario_final")
                )
            )
        )
    }
    
    private fun criarSantuarioFinal(): AreaAventura {
        return AreaAventura(
            id = "santuario_final",
            nome = "Santuário do Necromante",
            descricao = "O coração da montanha se revela como uma vasta câmara abobadada, " +
                        "iluminada por cristais vermelhos que pulsam como um coração batendo. " +
                        "No centro, sobre uma plataforma elevada, está o sarcófago aberto do " +
                        "necromante Malachar. Energia sombria flui ao redor da câmara como " +
                        "tentáculos invisíveis.",
            eventoInicial = "santuario_final",
            eventos = listOf(
                EventoAventura(
                    id = "santuario_final",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "O Santuário Final",
                    descricao = "Você entra no santuário e imediatamente sente o peso de séculos " +
                               "de maldade concentrada. No centro da câmara, uma figura esquelética " +
                               "vestida em robes negros se ergue lentamente do sarcófago. Seus " +
                               "olhos brilham com fogo verde, e sua voz ecoa como o vento através " +
                               "de túmulos: 'Finalmente... um visitante digno vem até mim...'",
                    opcoes = listOf(
                        "Desafiar o necromante diretamente",
                        "Tentar negociar",
                        "Atacar imediatamente"
                    ),
                    consequencias = mapOf(
                        1 to "Você enfrenta Malachar com coragem.",
                        2 to "Você tenta falar com o necromante.",
                        3 to "Você ataca sem hesitação."
                    ),
                    proximosEventos = mapOf(
                        1 to "desafio_malachar",
                        2 to "negociar_malachar",
                        3 to "combate_final"
                    )
                ),
                
                EventoAventura(
                    id = "desafio_malachar",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Desafiando Malachar",
                    descricao = "Você se posiciona corajosamente diante do necromante e declara: " +
                               "'Malachar! Sua maldição sobre estas terras termina hoje!' O " +
                               "necromante ri, um som como ossos se quebrando: 'Tolo mortal... " +
                               "você não compreende o poder que enfrenta. Mas admiro sua coragem. " +
                               "Morrerá com honra!'",
                    opcoes = listOf("Preparar-se para o combate final"),
                    xpRecompensa = 100,
                    consequencias = mapOf(1 to "O confronto final está prestes a começar!"),
                    proximosEventos = mapOf(1 to "combate_final")
                ),
                
                EventoAventura(
                    id = "negociar_malachar",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Tentativa de Negociação",
                    descricao = "Você tenta falar com Malachar: 'Não precisa ser assim. Você pode " +
                               "parar com esta maldição.' O necromante inclina a cabeça: 'Interessante... " +
                               "há séculos ninguém tenta conversar comigo. Mas não, pequeno mortal. " +
                               "Minha vingança contra os vivos deve continuar. Junte-se a mim... " +
                               "ou pereça!'",
                    opcoes = listOf(
                        "Recusar e lutar",
                        "Fingir aceitar para pegar de surpresa"
                    ),
                    consequencias = mapOf(
                        1 to "Você rejeita a oferta do necromante.",
                        2 to "Você tenta enganar Malachar."
                    ),
                    proximosEventos = mapOf(
                        1 to "combate_final",
                        2 to "ataque_surpresa"
                    )
                ),
                
                EventoAventura(
                    id = "ataque_surpresa",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Ataque Surpresa",
                    descricao = "Você finge considerar a oferta e se aproxima lentamente. No último " +
                               "momento, você saca sua arma e ataca! Malachar, pego de surpresa, " +
                               "recua: 'Traição! Você pagará caro por essa artimanha!' Você " +
                               "conseguiu uma vantagem inicial no combate.",
                    opcoes = listOf("Pressionar o ataque"),
                    xpRecompensa = 150,
                    consequencias = mapOf(1 to "Você tem vantagem no combate inicial!"),
                    proximosEventos = mapOf(1 to "combate_final_vantagem")
                ),
                
                EventoAventura(
                    id = "combate_final",
                    tipo = TipoEvento.CHEFE,
                    titulo = "Confronto com Malachar",
                    descricao = "O necromante Malachar se ergue em toda sua terrível majestade. " +
                               "Energia sombria flui ao seu redor como uma tempestade, e ele " +
                               "invoca esqueletos para ajudá-lo na batalha. Este é o confronto " +
                               "final - tudo pelo que você lutou leva a este momento!",
                    opcoes = listOf("Lutar com todas as suas forças!"),
                    monstros = listOf("necromante"),
                    xpRecompensa = 500,
                    consequencias = mapOf(1 to "A batalha final começou!"),
                    proximosEventos = mapOf(1 to "vitoria_final")
                ),
                
                EventoAventura(
                    id = "combate_final_vantagem",
                    tipo = TipoEvento.CHEFE,
                    titulo = "Confronto com Vantagem",
                    descricao = "Aproveitando sua vantagem inicial, você pressiona o ataque contra " +
                               "Malachar. O necromante, ainda se recuperando da surpresa, luta " +
                               "para se defender enquanto invoca seus servos mortos-vivos. Sua " +
                               "estratégia astuta pode fazer a diferença nesta batalha!",
                    opcoes = listOf("Continuar o ataque!"),
                    monstros = listOf("necromante"),
                    xpRecompensa = 600,
                    consequencias = mapOf(1 to "Você tem vantagem na batalha final!"),
                    proximosEventos = mapOf(1 to "vitoria_final")
                ),
                
                EventoAventura(
                    id = "vitoria_final",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Vitória Final",
                    descricao = "Com um grito de agonia que ecoa por toda a montanha, Malachar " +
                               "desaba. Sua forma esquelética se desfaz em poeira, e a energia " +
                               "sombria que permeava o santuário se dissipa como névoa ao vento. " +
                               "Os cristais vermelhos perdem seu brilho sinistro, e pela primeira " +
                               "vez em séculos, a paz retorna à Montanha Gelada.",
                    opcoes = listOf("Explorar o santuário", "Sair da montanha imediatamente"),
                    tesouro = mapOf("Cajado de Malachar" to 1, "Coroa do Necromante" to 1),
                    xpRecompensa = 1000,
                    consequencias = mapOf(
                        1 to "Você explora o santuário em busca de tesouros.",
                        2 to "Você sai rapidamente da montanha."
                    ),
                    proximosEventos = mapOf(
                        1 to "explorar_tesouro_final",
                        2 to "final_aventura"
                    )
                ),
                
                EventoAventura(
                    id = "explorar_tesouro_final",
                    tipo = TipoEvento.TESOURO,
                    titulo = "Tesouro Final",
                    descricao = "Explorando o santuário de Malachar, você encontra um tesouro " +
                               "acumulado ao longo de séculos: moedas de ouro antigas, gemas " +
                               "preciosas, e artefatos mágicos. Entre os tesouros, você encontra " +
                               "também um diário que conta a trágica história de como Malachar " +
                               "se tornou um necromante.",
                    opcoes = listOf("Coletar os tesouros e partir"),
                    tesouro = mapOf(
                        "Tesouro de Malachar" to 1000,
                        "Gemas Preciosas" to 10,
                        "Diário de Malachar" to 1
                    ),
                    xpRecompensa = 200,
                    consequencias = mapOf(1 to "Você coletou um tesouro imenso!"),
                    proximosEventos = mapOf(1 to "final_aventura")
                ),
                
                EventoAventura(
                    id = "final_aventura",
                    tipo = TipoEvento.NARRATIVO,
                    titulo = "Final da Aventura",
                    descricao = "Você emerge da Montanha Gelada como um herói verdadeiro. A " +
                               "maldição foi quebrada, o necromante foi derrotado, e a paz " +
                               "retornou à região. Sua jornada épica chegou ao fim, mas suas " +
                               "aventuras estão apenas começando...",
                    opcoes = listOf("Retornar à vila como herói"),
                    xpRecompensa = 500,
                    consequencias = mapOf(1 to "Você completou a aventura com sucesso!"),
                    proximosEventos = mapOf()
                )
            )
        )
    }
}

class GerenciadorAventura {
    private var aventuraAtual: Aventura? = null
    private var areaAtual: String = ""
    private var eventoAtual: String = ""
    private var eventosCompletados = mutableSetOf<String>()
    
    fun iniciarAventura(aventura: Aventura): String {
        aventuraAtual = aventura
        areaAtual = aventura.getAreaInicial()
        eventoAtual = aventura.getArea(areaAtual)?.eventoInicial ?: ""
        eventosCompletados.clear()
        
        return buildString {
            appendLine(aventura.getHistoriaIntroducao())
            appendLine()
            appendLine("=== AVENTURA INICIADA ===")
            appendLine("Aventura: ${aventura.getNome()}")
            appendLine("Área Inicial: ${aventura.getArea(areaAtual)?.nome}")
        }
    }
    
    fun getEventoAtual(): EventoAventura? {
        return aventuraAtual?.getEvento(areaAtual, eventoAtual)
    }
    
    fun processarEscolha(escolha: Int): String {
        val evento = getEventoAtual() ?: return "Erro: Nenhum evento ativo."
        
        if (escolha < 1 || escolha > evento.opcoes.size) {
            return "Escolha inválida."
        }
        
        val consequencia = evento.consequencias[escolha] ?: ""
        val proximoEvento = evento.proximosEventos[escolha]
        
        // Marcar evento como completado
        eventosCompletados.add("${areaAtual}_${eventoAtual}")
        
        // Avançar para próximo evento ou área
        if (proximoEvento != null) {
            // Verificar se é mudança de área
            val novaArea = aventuraAtual?.getAreas()?.find { area ->
                area.eventos.any { it.id == proximoEvento }
            }
            
            if (novaArea != null && novaArea.id != areaAtual) {
                areaAtual = novaArea.id
            }
            
            eventoAtual = proximoEvento
        }
        
        return buildString {
            appendLine(consequencia)
            
            // Verificar se é o final da aventura
            if (proximoEvento == null || eventoAtual == "final_aventura") {
                appendLine()
                appendLine(aventuraAtual?.getHistoriaConclusao() ?: "Aventura concluída!")
            }
        }
    }
    
    fun getStatusAventura(): String {
        val aventura = aventuraAtual ?: return "Nenhuma aventura ativa."
        val area = aventura.getArea(areaAtual)
        val evento = getEventoAtual()
        
        return buildString {
            appendLine("=== STATUS DA AVENTURA ===")
            appendLine("Aventura: ${aventura.getNome()}")
            appendLine("Área Atual: ${area?.nome ?: "Desconhecida"}")
            appendLine("Evento Atual: ${evento?.titulo ?: "Desconhecido"}")
            appendLine("Eventos Completados: ${eventosCompletados.size}")
        }
    }
    
    fun isAventuraCompleta(): Boolean {
        return eventoAtual == "final_aventura" || getEventoAtual() == null
    }
    
    fun podeDescansar(): Boolean {
        return aventuraAtual?.getArea(areaAtual)?.podeDescansar ?: false
    }
}

