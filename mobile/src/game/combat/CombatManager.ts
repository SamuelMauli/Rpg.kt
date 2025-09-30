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
      combateAtivo: true,
      logCombate: [`=== Combate Iniciado ===`, `Iniciativa do personagem: ${iniciativaPersonagem}`]
    };

    // Adicionar iniciativas dos monstros ao log
    monstros.forEach((monstro, index) => {
      this.estadoCombate!.logCombate.push(`Iniciativa ${monstro.nome}: ${iniciativaMonstros[index]}`);
    });

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

    // Adicionar resultado ao log
    this.estadoCombate.logCombate.push(resultado);

    // Se o combate ainda está ativo, processar turno dos monstros
    if (this.estadoCombate.combateAtivo) {
      const resultadoMonstros = this.processarTurnoMonstros();
      this.estadoCombate.logCombate.push(...resultadoMonstros);
      this.estadoCombate.turno++;
    }

    return resultado;
  }

  private atacarMonstro(indiceAlvo: number): string {
    if (!this.estadoCombate) return 'Erro: sem estado de combate';

    const monstroAlvo = this.estadoCombate.monstros[indiceAlvo];
    if (!monstroAlvo || monstroAlvo.pontosDeVida <= 0) {
      return 'Alvo inválido ou já morto!';
    }

    const personagem = this.estadoCombate.personagem;
    
    // Rolar ataque
    const roleAtaque = DiceUtils.rollAttack(
      personagem.baseDeAtaque,
      personagem.modificadores.forca
    );

    let resultado = `${personagem.nome} ataca ${monstroAlvo.nome}! `;
    resultado += `Rolagem: ${roleAtaque} vs CA ${monstroAlvo.classeDeArmadura}`;

    if (roleAtaque >= monstroAlvo.classeDeArmadura) {
      // Acertou - rolar dano
      const dano = DiceUtils.rollDamage('1d8', personagem.modificadores.forca);
      monstroAlvo.pontosDeVida -= dano;
      
      resultado += ` - ACERTOU! Dano: ${dano}`;
      
      if (monstroAlvo.pontosDeVida <= 0) {
        resultado += ` - ${monstroAlvo.nome} foi derrotado!`;
        this.verificarFimCombate();
      } else {
        resultado += ` - ${monstroAlvo.nome} tem ${monstroAlvo.pontosDeVida} PV restantes`;
      }
    } else {
      resultado += ' - ERROU!';
    }

    return resultado;
  }

  private tentarFuga(): string {
    if (!this.estadoCombate) return 'Erro: sem estado de combate';

    // Teste de fuga (50% de chance base)
    const sucessoFuga = DiceUtils.rollDie(6) >= 4;
    
    if (sucessoFuga) {
      this.estadoCombate.combateAtivo = false;
      return `${this.estadoCombate.personagem.nome} conseguiu fugir do combate!`;
    } else {
      return `${this.estadoCombate.personagem.nome} tentou fugir mas não conseguiu!`;
    }
  }

  private usarItem(item?: Item): string {
    if (!this.estadoCombate || !item) return 'Item inválido!';

    const personagem = this.estadoCombate.personagem;
    
    if (item.tipo === TipoItem.POCAO && item.nome.includes('Cura')) {
      const cura = DiceUtils.rollDamage('1d8') + 1;
      personagem.pontosDeVida = Math.min(
        personagem.pontosDeVida + cura,
        personagem.pontosVidaMaximos
      );
      
      // Remover item do inventário
      const index = personagem.inventario.findIndex(i => i.nome === item.nome);
      if (index >= 0) {
        personagem.inventario.splice(index, 1);
      }
      
      return `${personagem.nome} usou ${item.nome} e recuperou ${cura} pontos de vida!`;
    }

    return `${personagem.nome} usou ${item.nome}`;
  }

  private processarTurnoMonstros(): string[] {
    if (!this.estadoCombate) return ['Erro: sem estado de combate'];

    const resultados: string[] = [];
    const personagem = this.estadoCombate.personagem;

    for (const monstro of this.estadoCombate.monstros) {
      if (monstro.pontosDeVida <= 0) continue;

      // Monstro pode tentar fugir se estiver com pouca vida
      if (monstro.pontosDeVida <= monstro.pontosDeVidaMaximos * 0.25) {
        if (DiceUtils.rollDie(6) >= 5) {
          resultados.push(`${monstro.nome} fugiu do combate!`);
          monstro.pontosDeVida = 0; // Marcar como "morto" para remover
          continue;
        }
      }

      // Atacar personagem
      const roleAtaque = DiceUtils.rollAttack(monstro.baseDeAtaque, 0);
      let resultado = `${monstro.nome} ataca ${personagem.nome}! `;
      resultado += `Rolagem: ${roleAtaque} vs CA ${personagem.classeDeArmadura}`;

      if (roleAtaque >= personagem.classeDeArmadura) {
        const dano = DiceUtils.rollDamage(monstro.dano);
        personagem.pontosDeVida -= dano;
        
        resultado += ` - ACERTOU! Dano: ${dano}`;
        
        if (personagem.pontosDeVida <= 0) {
          resultado += ` - ${personagem.nome} foi derrotado!`;
          this.estadoCombate.combateAtivo = false;
        } else {
          resultado += ` - ${personagem.nome} tem ${personagem.pontosDeVida} PV restantes`;
        }
      } else {
        resultado += ' - ERROU!';
      }

      resultados.push(resultado);
    }

    return resultados;
  }

  private verificarFimCombate(): void {
    if (!this.estadoCombate) return;

    const monstrosVivos = this.estadoCombate.monstros.filter(m => m.pontosDeVida > 0);
    
    if (monstrosVivos.length === 0) {
      this.estadoCombate.combateAtivo = false;
      this.estadoCombate.logCombate.push('=== Vitória! Todos os monstros foram derrotados! ===');
      
      // Calcular experiência ganha
      const expGanha = this.estadoCombate.monstros.reduce((total, monstro) => 
        total + monstro.experiencia, 0
      );
      
      this.estadoCombate.personagem.experiencia += expGanha;
      this.estadoCombate.logCombate.push(`Experiência ganha: ${expGanha}`);
      
      // Gerar tesouro (simplificado)
      if (Math.random() < 0.3) { // 30% de chance de tesouro
        const tesouro: Item = {
          nome: 'Poção de Cura',
          tipo: TipoItem.POCAO,
          descricao: 'Restaura 1d8+1 pontos de vida',
          peso: 0.5,
          valor: 50
        };
        
        this.estadoCombate.personagem.inventario.push(tesouro);
        this.estadoCombate.logCombate.push(`Tesouro encontrado: ${tesouro.nome}`);
      }
    }
  }

  finalizarCombate(): ResultadoCombate | null {
    if (!this.estadoCombate) return null;

    const monstrosVivos = this.estadoCombate.monstros.filter(m => m.pontosDeVida > 0);
    const vitoria = monstrosVivos.length === 0;
    const personagemMorreu = this.estadoCombate.personagem.pontosDeVida <= 0;

    const experienciaGanha = vitoria ? 
      this.estadoCombate.monstros.reduce((total, monstro) => total + monstro.experiencia, 0) : 0;

    const tesouroEncontrado: Item[] = [];
    if (vitoria && Math.random() < 0.3) {
      tesouroEncontrado.push({
        nome: 'Poção de Cura',
        tipo: TipoItem.POCAO,
        descricao: 'Restaura 1d8+1 pontos de vida',
        peso: 0.5,
        valor: 50
      });
    }

    const resultado: ResultadoCombate = {
      vitoria,
      experienciaGanha,
      tesouroEncontrado,
      personagemMorreu,
      logCombate: [...this.estadoCombate.logCombate]
    };

    // Limpar estado de combate
    this.estadoCombate = null;

    return resultado;
  }

  getEstadoCombate(): EstadoCombate | null {
    return this.estadoCombate;
  }

  isCombateAtivo(): boolean {
    return this.estadoCombate?.combateAtivo || false;
  }
}
