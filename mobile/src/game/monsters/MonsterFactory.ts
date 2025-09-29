import { Monstro, EstatisticasMonstro } from '../../types/GameTypes';
import { DiceUtils } from '../../utils/DiceUtils';

export class MonsterFactory {
  private static readonly bestiario: Record<string, EstatisticasMonstro> = {
    rato_gigante: {
      nome: 'Rato Gigante',
      tipo: 'animal',
      dadosVida: 1,
      ca: 7,
      baseAtaque: 1,
      dano: '1d4',
      movimento: 12,
      moral: 5,
      tesouro: 'nenhum',
      xp: 10,
      habilidades: [],
      tamanho: 'pequeno'
    },
    
    kobold: {
      nome: 'Kobold',
      tipo: 'humanoide',
      dadosVida: 1,
      ca: 7,
      baseAtaque: 1,
      dano: '1d6',
      movimento: 6,
      moral: 6,
      tesouro: 'individual',
      xp: 10,
      habilidades: [],
      tamanho: 'pequeno'
    },
    
    goblin: {
      nome: 'Goblin',
      tipo: 'humanoide',
      dadosVida: 1,
      ca: 6,
      baseAtaque: 1,
      dano: '1d6',
      movimento: 6,
      moral: 7,
      tesouro: 'individual',
      xp: 10,
      habilidades: [],
      tamanho: 'pequeno'
    },
    
    orc: {
      nome: 'Orc',
      tipo: 'humanoide',
      dadosVida: 1,
      ca: 6,
      baseAtaque: 1,
      dano: '1d8',
      movimento: 9,
      moral: 8,
      tesouro: 'individual',
      xp: 10,
      habilidades: [],
      tamanho: 'médio'
    },
    
    bugbear: {
      nome: 'Bugbear',
      tipo: 'humanoide',
      dadosVida: 3,
      ca: 5,
      baseAtaque: 3,
      dano: '2d4',
      movimento: 9,
      moral: 9,
      tesouro: 'individual',
      xp: 35,
      habilidades: [],
      tamanho: 'grande'
    },
    
    esqueleto: {
      nome: 'Esqueleto',
      tipo: 'morto-vivo',
      dadosVida: 1,
      ca: 7,
      baseAtaque: 1,
      dano: '1d6',
      movimento: 6,
      moral: 12,
      tesouro: 'nenhum',
      xp: 10,
      habilidades: ['imune_sono', 'imune_encantamento'],
      tamanho: 'médio'
    },
    
    zumbi: {
      nome: 'Zumbi',
      tipo: 'morto-vivo',
      dadosVida: 2,
      ca: 8,
      baseAtaque: 2,
      dano: '1d8',
      movimento: 6,
      moral: 12,
      tesouro: 'nenhum',
      xp: 20,
      habilidades: ['imune_sono', 'imune_encantamento'],
      tamanho: 'médio'
    },
    
    necromante: {
      nome: 'Necromante',
      tipo: 'humanoide',
      dadosVida: 3,
      ca: 9,
      baseAtaque: 2,
      dano: '1d4',
      movimento: 9,
      moral: 9,
      tesouro: 'magico',
      xp: 65,
      habilidades: ['magias_3_nivel', 'afastar_vivos'],
      tamanho: 'médio'
    }
  };

  static criarMonstro(tipo: string, quantidade: number = 1): Monstro[] {
    const estatisticas = this.bestiario[tipo.toLowerCase()];
    if (!estatisticas) {
      throw new Error(`Monstro desconhecido: ${tipo}`);
    }

    const monstros: Monstro[] = [];
    for (let i = 0; i < quantidade; i++) {
      monstros.push(this.criarMonstroIndividual(estatisticas));
    }
    return monstros;
  }

  static criarGrupoAleatorio(nivelDesafio: number): Monstro[] {
    const monstrosDisponiveis = this.getMonstrosPorNivel(nivelDesafio);
    const tipoEscolhido = monstrosDisponiveis[Math.floor(Math.random() * monstrosDisponiveis.length)];
    const quantidade = this.getQuantidadePorNivel(nivelDesafio);
    
    return this.criarMonstro(tipoEscolhido, quantidade);
  }

  private static getMonstrosPorNivel(nivel: number): string[] {
    switch (nivel) {
      case 1:
        return ['rato_gigante', 'kobold'];
      case 2:
        return ['goblin', 'esqueleto'];
      case 3:
        return ['orc', 'zumbi'];
      case 4:
        return ['bugbear'];
      case 5:
        return ['necromante'];
      default:
        return ['goblin'];
    }
  }

  private static getQuantidadePorNivel(nivel: number): number {
    switch (nivel) {
      case 1:
        return DiceUtils.rollDie(4) + 1; // 2-5 monstros
      case 2:
        return DiceUtils.rollDie(3); // 1-3 monstros
      case 3:
        return DiceUtils.rollDie(2); // 1-2 monstros
      case 4:
      case 5:
        return 1; // 1 monstro
      default:
        return 1;
    }
  }

  private static criarMonstroIndividual(stats: EstatisticasMonstro): Monstro {
    const pontosVida = this.rolarPontosVida(stats.dadosVida);
    
    return {
      nome: stats.nome,
      tipo: stats.tipo,
      dadosDeVida: stats.dadosVida,
      pontosDeVida: pontosVida,
      classeArmadura: stats.ca,
      baseAtaque: stats.baseAtaque,
      dano: stats.dano,
      movimento: stats.movimento,
      moral: stats.moral,
      tesouro: stats.tesouro,
      habilidadesEspeciais: [...stats.habilidades],
      experiencia: stats.xp,
      tamanho: stats.tamanho
    };
  }

  private static rolarPontosVida(dadosVida: number): number {
    return DiceUtils.rollDice(dadosVida, 8); // d8 por DV
  }

  static getMonstrosDisponiveis(): string[] {
    return Object.keys(this.bestiario);
  }

  static getEstatisticas(tipo: string): EstatisticasMonstro | undefined {
    return this.bestiario[tipo.toLowerCase()];
  }

  static rolarDano(monstro: Monstro): number {
    return DiceUtils.rollFromString(monstro.dano);
  }

  static testarMoral(monstro: Monstro): boolean {
    const rolagem = DiceUtils.rollDice(2, 6); // 2d6
    return rolagem <= monstro.moral;
  }

  static estaMorto(monstro: Monstro): boolean {
    return monstro.pontosDeVida <= 0;
  }

  static receberDano(monstro: Monstro, dano: number): void {
    monstro.pontosDeVida = Math.max(0, monstro.pontosDeVida - dano);
  }

  static getSpriteName(monstro: Monstro): string {
    switch (monstro.nome.toLowerCase()) {
      case 'goblin':
        return 'goblin_sprite';
      case 'orc':
        return 'orc_sprite';
      case 'esqueleto':
        return 'skeleton_sprite';
      case 'zumbi':
        return 'zombie_sprite';
      case 'necromante':
        return 'necromancer_sprite';
      default:
        return 'goblin_sprite'; // sprite padrão
    }
  }

  static getMonsterDescription(monstro: Monstro): string {
    const descriptions: Record<string, string> = {
      'rato gigante': 'Um roedor do tamanho de um cão pequeno, com dentes afiados e olhos vermelhos brilhantes.',
      'kobold': 'Uma criatura humanoide pequena e covarde, com pele escamosa e olhos amarelos.',
      'goblin': 'Um humanoide pequeno e malicioso, com pele verde e orelhas pontudas.',
      'orc': 'Um guerreiro brutal e selvagem, com presas proeminentes e músculos poderosos.',
      'bugbear': 'Um humanoide grande e peludo, conhecido por sua força e crueldade.',
      'esqueleto': 'Os restos animados de um guerreiro morto, movido por magia necromântica.',
      'zumbi': 'Um morto-vivo em decomposição, que caminha lentamente em busca de carne fresca.',
      'necromante': 'Um mago sombrio que domina as artes da morte e controla os mortos-vivos.'
    };

    return descriptions[monstro.nome.toLowerCase()] || 'Uma criatura misteriosa e perigosa.';
  }
}
