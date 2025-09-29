import { 
  Personagem, 
  Monstro, 
  EstadoCombate, 
  ResultadoCombate,
  Item,
  TipoItem 
} from '../../types/GameTypes';
import { DiceUtils } from '../../utils/DiceUtils';
import { MonsterFactory } from '../monsters/MonsterFactory';

export class CombatManager {
  private estadoCombate: EstadoCombate | null = null;

  iniciarCombate(personagem: Personagem, monstros: Monstro[]): EstadoCombate {
    // Rolar iniciativa
    const iniciativaPersonagem = DiceUtils.rollInitiative(personagem.modificadores.destreza);
    const iniciativaMonstros = monstros.map(monstro => 
      DiceUtils.rollInitiative(0) // Monstros usam iniciativa base
    );

    this.estadoCombate = {
      personagem,
      monstros: [...monstros],
      turno: 1,
      iniciativaPersonagem,
      iniciativaMonstros,
      combateAtivo: true
    };

    return this.estadoCombate;
  }

  processarTurnoPersonagem(acao: 'atacar' | 'fugir' | 'usar_item', alvo?: number, item?: Item): string {
    if (!this.estadoCombate || !this.estadoCombate.combateAtivo) {
      return 'Não há combate ativo!';
    }

    let resultado = '';

    switch (acao) {
      case 'atacar':
        resultado = this.atacarMonstro(alvo || 0);
        break;
      case 'fugir':
        resultado = this.tentarFuga();
        break;
      case 'usar_item':
        resultado = this.usarItem(item);
        break;
    }

    // Verificar se o combate ainda está ativo
    if (this.estadoCombate.combateAtivo) {
      resultado += '\n\n' + this.processarTurnoMonstros();
    }

    // Verificar condições de fim de combate
    this.verificarFimCombate();

    return resultado;
  }

  private atacarMonstro(indiceAlvo: number): string {
    if (!this.estadoCombate) return '';

    const { personagem, monstros } = this.estadoCombate;
    
    if (indiceAlvo >= monstros.length || monstros[indiceAlvo].pontosDeVida <= 0) {
      return 'Alvo inválido!';
    }

    const alvo = monstros[indiceAlvo];
    
    // Rolar ataque
    const rolagemAtaque = DiceUtils.rollAttack(
      personagem.baseAtaque,
      personagem.modificadores.forca
    );

    let resultado = `${personagem.nome} ataca ${alvo.nome}!\n`;
    resultado += `Rolagem de ataque: ${rolagemAtaque} vs CA ${alvo.classeArmadura}\n`;

    if (rolagemAtaque >= alvo.classeArmadura) {
      // Acerto! Rolar dano
      const dano = this.calcularDanoPersonagem(personagem);
      MonsterFactory.receberDano(alvo, dano);
      
      resultado += `Acertou! Causou ${dano} pontos de dano.\n`;
      resultado += `${alvo.nome} agora tem ${alvo.pontosDeVida} PV.`;

      if (MonsterFactory.estaMorto(alvo)) {
        resultado += `\n${alvo.nome} foi derrotado!`;
      }
    } else {
      resultado += 'Errou o ataque!';
    }

    return resultado;
  }

  private tentarFuga(): string {
    if (!this.estadoCombate) return '';

    // Teste de fuga baseado em Destreza
    const sucessoFuga = DiceUtils.rollAttributeCheck(
      this.estadoCombate.personagem.atributos.destreza,
      15
    );

    if (sucessoFuga) {
      this.estadoCombate.combateAtivo = false;
      return `${this.estadoCombate.personagem.nome} conseguiu fugir do combate!`;
    } else {
      return `${this.estadoCombate.personagem.nome} tentou fugir mas não conseguiu escapar!`;
    }
  }

  private usarItem(item?: Item): string {
    if (!this.estadoCombate || !item) return 'Item inválido!';

    // Implementar uso de itens (poções, etc.)
    if (item.tipo === 'pocao') {
      // Exemplo: poção de cura
      if (item.nome.toLowerCase().includes('cura')) {
        const cura = DiceUtils.rollFromString('1d8+1');
        this.estadoCombate.personagem.pontosVida = Math.min(
          this.estadoCombate.personagem.pontosVidaMaximos,
          this.estadoCombate.personagem.pontosVida + cura
        );
        return `${this.estadoCombate.personagem.nome} usou ${item.nome} e recuperou ${cura} PV!`;
      }
    }

    return `${this.estadoCombate.personagem.nome} usou ${item.nome}.`;
  }

  private processarTurnoMonstros(): string {
    if (!this.estadoCombate) return '';

    let resultado = '';
    const { personagem, monstros } = this.estadoCombate;

    for (let i = 0; i < monstros.length; i++) {
      const monstro = monstros[i];
      
      if (MonsterFactory.estaMorto(monstro)) continue;

      // Teste de moral se o monstro estiver ferido
      if (monstro.pontosDeVida < monstro.dadosDeVida * 4) { // Menos de 50% da vida
        if (!MonsterFactory.testarMoral(monstro)) {
          resultado += `${monstro.nome} foge do combate!\n`;
          monstro.pontosDeVida = 0; // Remove do combate
          continue;
        }
      }

      // Atacar o personagem
      const rolagemAtaque = DiceUtils.rollAttack(monstro.baseAtaque, 0);
      
      resultado += `${monstro.nome} ataca ${personagem.nome}!\n`;
      resultado += `Rolagem de ataque: ${rolagemAtaque} vs CA ${personagem.classeArmadura}\n`;

      if (rolagemAtaque >= personagem.classeArmadura) {
        const dano = MonsterFactory.rolarDano(monstro);
        personagem.pontosVida = Math.max(0, personagem.pontosVida - dano);
        
        resultado += `Acertou! Causou ${dano} pontos de dano.\n`;
        resultado += `${personagem.nome} agora tem ${personagem.pontosVida} PV.\n`;
      } else {
        resultado += 'Errou o ataque!\n';
      }
    }

    this.estadoCombate.turno++;
    return resultado;
  }

  private calcularDanoPersonagem(personagem: Personagem): number {
    // Dano base + modificador de Força
    // Por simplicidade, usando 1d6 como dano base
    const danoBase = DiceUtils.rollDie(6);
    return Math.max(1, danoBase + personagem.modificadores.forca);
  }

  private verificarFimCombate(): void {
    if (!this.estadoCombate) return;

    const { personagem, monstros } = this.estadoCombate;

    // Personagem morreu
    if (personagem.pontosVida <= 0) {
      this.estadoCombate.combateAtivo = false;
      return;
    }

    // Todos os monstros foram derrotados
    const monstrosVivos = monstros.filter(m => !MonsterFactory.estaMorto(m));
    if (monstrosVivos.length === 0) {
      this.estadoCombate.combateAtivo = false;
      return;
    }
  }

  finalizarCombate(): ResultadoCombate | null {
    if (!this.estadoCombate) return null;

    const { personagem, monstros } = this.estadoCombate;
    const monstrosVivos = monstros.filter(m => !MonsterFactory.estaMorto(m));
    const vitoria = personagem.pontosVida > 0 && monstrosVivos.length === 0;

    let experienciaGanha = 0;
    const tesouroEncontrado: Item[] = [];

    if (vitoria) {
      // Calcular experiência ganha
      experienciaGanha = monstros.reduce((total, monstro) => {
        if (MonsterFactory.estaMorto(monstro)) {
          return total + monstro.experiencia;
        }
        return total;
      }, 0);

      // Gerar tesouro (simplificado)
      if (Math.random() < 0.3) { // 30% de chance de tesouro
        tesouroEncontrado.push({
          nome: 'Poção de Cura',
          tipo: TipoItem.POCAO,
          descricao: 'Restaura 1d8+1 pontos de vida',
          peso: 0.5,
          valor: 50
        });
      }
    }

    const resultado: ResultadoCombate = {
      vitoria,
      experienciaGanha,
      tesouroEncontrado,
      danoRecebido: this.estadoCombate.personagem.pontosVidaMaximos - this.estadoCombate.personagem.pontosVida
    };

    this.estadoCombate = null;
    return resultado;
  }

  getEstadoCombate(): EstadoCombate | null {
    return this.estadoCombate;
  }

  isCombateAtivo(): boolean {
    return this.estadoCombate?.combateAtivo || false;
  }

  getMonstrosVivos(): Monstro[] {
    if (!this.estadoCombate) return [];
    return this.estadoCombate.monstros.filter(m => !MonsterFactory.estaMorto(m));
  }

  getDescricaoSituacao(): string {
    if (!this.estadoCombate) return 'Nenhum combate ativo.';

    const { personagem, monstros, turno } = this.estadoCombate;
    const monstrosVivos = this.getMonstrosVivos();

    let descricao = `=== TURNO ${turno} ===\n\n`;
    descricao += `${personagem.nome}: ${personagem.pontosVida}/${personagem.pontosVidaMaximos} PV\n\n`;
    descricao += 'INIMIGOS:\n';
    
    monstrosVivos.forEach((monstro, index) => {
      descricao += `${index + 1}. ${monstro.nome}: ${monstro.pontosDeVida} PV\n`;
    });

    return descricao;
  }
}
