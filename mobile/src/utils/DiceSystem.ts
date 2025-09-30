export interface DiceRoll {
  dice: string;
  result: number;
  rolls: number[];
  modifier: number;
  total: number;
  critical: boolean;
  fumble: boolean;
}

export interface DiceAnimation {
  isRolling: boolean;
  currentValue: number;
  finalValue: number;
  duration: number;
}

export class DiceSystem {
  private static instance: DiceSystem;
  
  public static getInstance(): DiceSystem {
    if (!DiceSystem.instance) {
      DiceSystem.instance = new DiceSystem();
    }
    return DiceSystem.instance;
  }

  /**
   * Rola dados no formato XdY+Z (ex: 3d6+2, 1d20, 2d8-1)
   */
  public roll(diceNotation: string, modifier: number = 0): DiceRoll {
    const match = diceNotation.match(/(\d+)d(\d+)([+-]\d+)?/i);
    
    if (!match) {
      throw new Error(`Formato de dado inválido: ${diceNotation}`);
    }

    const numDice = parseInt(match[1]);
    const diceSize = parseInt(match[2]);
    const notationModifier = match[3] ? parseInt(match[3]) : 0;
    const totalModifier = modifier + notationModifier;

    const rolls: number[] = [];
    let sum = 0;

    for (let i = 0; i < numDice; i++) {
      const roll = Math.floor(Math.random() * diceSize) + 1;
      rolls.push(roll);
      sum += roll;
    }

    const total = sum + totalModifier;
    const critical = numDice === 1 && diceSize === 20 && rolls[0] === 20;
    const fumble = numDice === 1 && diceSize === 20 && rolls[0] === 1;

    return {
      dice: diceNotation,
      result: sum,
      rolls,
      modifier: totalModifier,
      total,
      critical,
      fumble
    };
  }

  /**
   * Rola um dado simples (1dX)
   */
  public rollSingle(sides: number, modifier: number = 0): DiceRoll {
    return this.roll(`1d${sides}`, modifier);
  }

  /**
   * Rola múltiplos dados do mesmo tipo
   */
  public rollMultiple(numDice: number, sides: number, modifier: number = 0): DiceRoll {
    return this.roll(`${numDice}d${sides}`, modifier);
  }

  /**
   * Rola dados com vantagem (rola 2d20, pega o maior)
   */
  public rollAdvantage(modifier: number = 0): DiceRoll {
    const roll1 = this.rollSingle(20);
    const roll2 = this.rollSingle(20);
    
    const bestRoll = roll1.total >= roll2.total ? roll1 : roll2;
    const total = bestRoll.result + modifier;

    return {
      dice: "2d20 (vantagem)",
      result: bestRoll.result,
      rolls: [roll1.rolls[0], roll2.rolls[0]],
      modifier,
      total,
      critical: bestRoll.critical,
      fumble: false // Vantagem cancela fumble
    };
  }

  /**
   * Rola dados com desvantagem (rola 2d20, pega o menor)
   */
  public rollDisadvantage(modifier: number = 0): DiceRoll {
    const roll1 = this.rollSingle(20);
    const roll2 = this.rollSingle(20);
    
    const worstRoll = roll1.total <= roll2.total ? roll1 : roll2;
    const total = worstRoll.result + modifier;

    return {
      dice: "2d20 (desvantagem)",
      result: worstRoll.result,
      rolls: [roll1.rolls[0], roll2.rolls[0]],
      modifier,
      total,
      critical: false, // Desvantagem cancela crítico
      fumble: worstRoll.fumble
    };
  }

  /**
   * Rola atributos usando 4d6, descarta o menor
   */
  public rollAttribute(): DiceRoll {
    const rolls: number[] = [];
    
    for (let i = 0; i < 4; i++) {
      rolls.push(Math.floor(Math.random() * 6) + 1);
    }

    rolls.sort((a, b) => b - a); // Ordena decrescente
    const bestThree = rolls.slice(0, 3); // Pega os 3 maiores
    const total = bestThree.reduce((sum, roll) => sum + roll, 0);

    return {
      dice: "4d6 (descarta menor)",
      result: total,
      rolls: rolls,
      modifier: 0,
      total,
      critical: total === 18,
      fumble: total === 3
    };
  }

  /**
   * Rola pontos de vida para subir de nível
   */
  public rollHitPoints(hitDie: number, constitutionModifier: number): DiceRoll {
    const roll = this.rollSingle(hitDie, constitutionModifier);
    
    // Garante pelo menos 1 ponto de vida
    if (roll.total < 1) {
      roll.total = 1;
    }

    return roll;
  }

  /**
   * Rola teste de resistência/salvamento
   */
  public rollSavingThrow(baseValue: number, modifier: number = 0): DiceRoll {
    const roll = this.rollSingle(20, modifier);
    const success = roll.total >= baseValue;

    return {
      ...roll,
      dice: `1d20 vs ${baseValue}`,
      critical: roll.critical || (roll.total >= baseValue + 10),
      fumble: roll.fumble || (roll.total <= baseValue - 10)
    };
  }

  /**
   * Rola teste de moral para monstros
   */
  public rollMorale(morale: number): DiceRoll {
    const roll = this.rollMultiple(2, 6); // 2d6
    const success = roll.total <= morale;

    return {
      ...roll,
      dice: `2d6 vs ${morale}`,
      critical: success && roll.total <= 3,
      fumble: !success && roll.total >= 11
    };
  }

  /**
   * Rola iniciativa
   */
  public rollInitiative(dexterityScore: number, wisdomScore: number): DiceRoll {
    const roll = this.rollSingle(20);
    const targetNumber = Math.max(dexterityScore, wisdomScore);
    const success = roll.total <= targetNumber;

    return {
      ...roll,
      dice: `1d20 vs ${targetNumber}`,
      critical: success && roll.critical,
      fumble: !success && roll.fumble
    };
  }

  /**
   * Rola dano de arma
   */
  public rollWeaponDamage(weaponDamage: string, strengthModifier: number = 0, critical: boolean = false): DiceRoll {
    const roll = this.roll(weaponDamage, strengthModifier);
    
    if (critical) {
      // Crítico dobra o dano
      roll.total = roll.total * 2;
      roll.critical = true;
    }

    // Garante pelo menos 1 de dano
    if (roll.total < 1) {
      roll.total = 1;
    }

    return roll;
  }

  /**
   * Simula animação de dado rolando
   */
  public createDiceAnimation(finalValue: number, duration: number = 1000): DiceAnimation {
    return {
      isRolling: true,
      currentValue: 1,
      finalValue,
      duration
    };
  }

  /**
   * Formata resultado do dado para exibição
   */
  public formatRoll(roll: DiceRoll): string {
    let result = `${roll.dice}: `;
    
    if (roll.rolls.length > 1) {
      result += `[${roll.rolls.join(', ')}]`;
    } else {
      result += roll.rolls[0];
    }

    if (roll.modifier !== 0) {
      result += ` ${roll.modifier >= 0 ? '+' : ''}${roll.modifier}`;
    }

    result += ` = ${roll.total}`;

    if (roll.critical) {
      result += ' (CRÍTICO!)';
    } else if (roll.fumble) {
      result += ' (FALHA CRÍTICA!)';
    }

    return result;
  }

  /**
   * Gera múltiplos atributos para criação de personagem
   */
  public generateAttributes(): { [key: string]: DiceRoll } {
    return {
      forca: this.rollAttribute(),
      destreza: this.rollAttribute(),
      constituicao: this.rollAttribute(),
      inteligencia: this.rollAttribute(),
      sabedoria: this.rollAttribute(),
      carisma: this.rollAttribute()
    };
  }

  /**
   * Rola tesouro aleatório
   */
  public rollTreasure(treasureType: 'individual' | 'hoard' | 'magic'): DiceRoll {
    switch (treasureType) {
      case 'individual':
        return this.rollMultiple(3, 6); // 3d6 moedas de cobre
      case 'hoard':
        return this.rollMultiple(1, 100); // 1d100 moedas de ouro
      case 'magic':
        return this.rollSingle(100); // 1d100 para item mágico
      default:
        return this.rollSingle(6);
    }
  }
}

export default DiceSystem;
