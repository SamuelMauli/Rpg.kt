// Utilitários para rolagem de dados
export class DiceUtils {
  /**
   * Rola um dado de N lados
   */
  static rollDie(sides: number): number {
    return Math.floor(Math.random() * sides) + 1;
  }

  /**
   * Rola múltiplos dados
   */
  static rollDice(count: number, sides: number): number {
    let total = 0;
    for (let i = 0; i < count; i++) {
      total += this.rollDie(sides);
    }
    return total;
  }

  /**
   * Rola dados com modificador
   */
  static rollWithModifier(count: number, sides: number, modifier: number): number {
    return this.rollDice(count, sides) + modifier;
  }

  /**
   * Rola dados baseado em string (ex: "2d6+3", "1d20")
   */
  static rollFromString(diceString: string): number {
    const cleanString = diceString.toLowerCase().replace(/\s/g, '');
    
    // Padrão: XdY+Z ou XdY-Z ou XdY
    const match = cleanString.match(/^(\d+)?d(\d+)([+-]\d+)?$/);
    
    if (!match) {
      throw new Error(`Formato de dado inválido: ${diceString}`);
    }

    const count = parseInt(match[1] || '1');
    const sides = parseInt(match[2]);
    const modifier = parseInt(match[3] || '0');

    return this.rollDice(count, sides) + modifier;
  }

  /**
   * Rola 3d6 para atributos
   */
  static rollAttribute(): number {
    return this.rollDice(3, 6);
  }

  /**
   * Rola 4d6 e descarta o menor (método alternativo para atributos)
   */
  static rollAttributeHeroic(): number {
    const rolls = [
      this.rollDie(6),
      this.rollDie(6),
      this.rollDie(6),
      this.rollDie(6)
    ].sort((a, b) => b - a);
    
    // Soma os 3 maiores
    return rolls[0] + rolls[1] + rolls[2];
  }

  /**
   * Calcula modificador de atributo (Old Dragon 2)
   */
  static getAttributeModifier(attribute: number): number {
    if (attribute <= 3) return -3;
    if (attribute <= 5) return -2;
    if (attribute <= 8) return -1;
    if (attribute <= 12) return 0;
    if (attribute <= 15) return 1;
    if (attribute <= 17) return 2;
    return 3;
  }

  /**
   * Rola iniciativa (1d6 + modificador de Destreza)
   */
  static rollInitiative(dexterityModifier: number): number {
    return this.rollDie(6) + dexterityModifier;
  }

  /**
   * Rola ataque (1d20 + modificadores)
   */
  static rollAttack(baseAttack: number, attributeModifier: number): number {
    return this.rollDie(20) + baseAttack + attributeModifier;
  }

  /**
   * Rola teste de moral (2d6)
   */
  static rollMorale(): number {
    return this.rollDice(2, 6);
  }

  /**
   * Rola pontos de vida por nível
   */
  static rollHitPoints(hitDie: number, constitution: number): number {
    const roll = this.rollDie(hitDie);
    const modifier = this.getAttributeModifier(constitution);
    return Math.max(1, roll + modifier);
  }

  /**
   * Rola dano baseado em string de dano
   */
  static rollDamage(damageString: string): number {
    return this.rollFromString(damageString);
  }

  /**
   * Rola múltiplos dados e retorna array com resultados individuais
   */
  static rollMultiple(count: number, sides: number): number[] {
    const results: number[] = [];
    for (let i = 0; i < count; i++) {
      results.push(this.rollDie(sides));
    }
    return results;
  }

  /**
   * Rola com vantagem (rola 2 dados e pega o maior)
   */
  static rollWithAdvantage(sides: number): number {
    const roll1 = this.rollDie(sides);
    const roll2 = this.rollDie(sides);
    return Math.max(roll1, roll2);
  }

  /**
   * Rola com desvantagem (rola 2 dados e pega o menor)
   */
  static rollWithDisadvantage(sides: number): number {
    const roll1 = this.rollDie(sides);
    const roll2 = this.rollDie(sides);
    return Math.min(roll1, roll2);
  }

  /**
   * Verifica se um teste foi bem-sucedido
   */
  static checkSuccess(roll: number, target: number): boolean {
    return roll >= target;
  }

  /**
   * Rola teste de atributo (1d20 + modificador vs dificuldade)
   */
  static rollAttributeCheck(attribute: number, difficulty: number = 15): boolean {
    const modifier = this.getAttributeModifier(attribute);
    const roll = this.rollDie(20) + modifier;
    return roll >= difficulty;
  }
}

// Constantes para facilitar o uso
export const DICE = {
  D4: 4,
  D6: 6,
  D8: 8,
  D10: 10,
  D12: 12,
  D20: 20,
  D100: 100
} as const;

// Dificuldades padrão para testes
export const DIFFICULTY = {
  VERY_EASY: 5,
  EASY: 10,
  MEDIUM: 15,
  HARD: 20,
  VERY_HARD: 25,
  NEARLY_IMPOSSIBLE: 30
} as const;
