import { DiceSystem } from '../../utils/DiceSystem';

export interface AttributeSet {
  strength: number;
  dexterity: number;
  constitution: number;
  intelligence: number;
  wisdom: number;
  charisma: number;
}

export interface AttributeModifiers {
  strength: number;
  dexterity: number;
  constitution: number;
  intelligence: number;
  wisdom: number;
  charisma: number;
}

export interface AttributeRoll {
  attribute: keyof AttributeSet;
  rolls: number[];
  total: number;
  modifier: number;
}

export enum GenerationMethod {
  STANDARD_ARRAY = 'standard_array',
  POINT_BUY = 'point_buy',
  ROLL_4D6_DROP_LOWEST = 'roll_4d6_drop_lowest',
  ROLL_3D6_STRAIGHT = 'roll_3d6_straight',
  ROLL_4D6_REROLL_ONES = 'roll_4d6_reroll_ones'
}

export class AttributeDistribution {
  private diceSystem: DiceSystem;
  private standardArray: number[] = [15, 14, 13, 12, 10, 8];
  private pointBuyPoints: number = 27;
  private pointBuyCosts: { [key: number]: number } = {
    8: 0, 9: 1, 10: 2, 11: 3, 12: 4, 13: 5,
    14: 7, 15: 9
  };

  constructor() {
    this.diceSystem = DiceSystem.getInstance();
  }

  /**
   * Gera atributos usando o método especificado
   */
  public generateAttributes(method: GenerationMethod): AttributeSet {
    switch (method) {
      case GenerationMethod.STANDARD_ARRAY:
        return this.generateStandardArray();
      case GenerationMethod.POINT_BUY:
        return this.generatePointBuy();
      case GenerationMethod.ROLL_4D6_DROP_LOWEST:
        return this.roll4d6DropLowest();
      case GenerationMethod.ROLL_3D6_STRAIGHT:
        return this.roll3d6Straight();
      case GenerationMethod.ROLL_4D6_REROLL_ONES:
        return this.roll4d6RerollOnes();
      default:
        return this.roll4d6DropLowest();
    }
  }

  /**
   * Gera atributos com rolagens detalhadas para exibição
   */
  public generateAttributesWithRolls(method: GenerationMethod): {
    attributes: AttributeSet;
    rolls: AttributeRoll[];
    total: number;
    average: number;
  } {
    const rolls: AttributeRoll[] = [];
    const attributeNames: (keyof AttributeSet)[] = [
      'strength', 'dexterity', 'constitution', 
      'intelligence', 'wisdom', 'charisma'
    ];

    let attributes: AttributeSet;

    switch (method) {
      case GenerationMethod.STANDARD_ARRAY:
        attributes = this.generateStandardArray();
        // Simular "rolagens" para o array padrão
        attributeNames.forEach(attr => {
          rolls.push({
            attribute: attr,
            rolls: [attributes[attr]],
            total: attributes[attr],
            modifier: this.getModifier(attributes[attr])
          });
        });
        break;

      case GenerationMethod.ROLL_4D6_DROP_LOWEST:
        attributes = { strength: 0, dexterity: 0, constitution: 0, intelligence: 0, wisdom: 0, charisma: 0 };
        attributeNames.forEach(attr => {
          const roll = this.diceSystem.rollAttribute();
          attributes[attr] = roll.total;
          rolls.push({
            attribute: attr,
            rolls: roll.rolls,
            total: roll.total,
            modifier: this.getModifier(roll.total)
          });
        });
        break;

      case GenerationMethod.ROLL_3D6_STRAIGHT:
        attributes = { strength: 0, dexterity: 0, constitution: 0, intelligence: 0, wisdom: 0, charisma: 0 };
        attributeNames.forEach(attr => {
          const roll = this.diceSystem.rollMultiple(3, 6);
          attributes[attr] = roll.total;
          rolls.push({
            attribute: attr,
            rolls: roll.rolls,
            total: roll.total,
            modifier: this.getModifier(roll.total)
          });
        });
        break;

      case GenerationMethod.ROLL_4D6_REROLL_ONES:
        attributes = { strength: 0, dexterity: 0, constitution: 0, intelligence: 0, wisdom: 0, charisma: 0 };
        attributeNames.forEach(attr => {
          const rollResult = this.roll4d6RerollOnesDetailed();
          attributes[attr] = rollResult.total;
          rolls.push({
            attribute: attr,
            rolls: rollResult.rolls,
            total: rollResult.total,
            modifier: this.getModifier(rollResult.total)
          });
        });
        break;

      default:
        return this.generateAttributesWithRolls(GenerationMethod.ROLL_4D6_DROP_LOWEST);
    }

    const total = Object.values(attributes).reduce((sum, val) => sum + val, 0);
    const average = total / 6;

    return { attributes, rolls, total, average };
  }

  /**
   * Calcula modificadores de atributos
   */
  public getModifiers(attributes: AttributeSet): AttributeModifiers {
    return {
      strength: this.getModifier(attributes.strength),
      dexterity: this.getModifier(attributes.dexterity),
      constitution: this.getModifier(attributes.constitution),
      intelligence: this.getModifier(attributes.intelligence),
      wisdom: this.getModifier(attributes.wisdom),
      charisma: this.getModifier(attributes.charisma)
    };
  }

  /**
   * Calcula modificador individual
   */
  public getModifier(score: number): number {
    return Math.floor((score - 10) / 2);
  }

  /**
   * Aplica modificadores raciais aos atributos
   */
  public applyRacialModifiers(
    attributes: AttributeSet, 
    racialModifiers: Partial<AttributeSet>
  ): AttributeSet {
    const result = { ...attributes };
    
    Object.entries(racialModifiers).forEach(([attr, modifier]) => {
      if (modifier !== undefined) {
        result[attr as keyof AttributeSet] += modifier;
        // Garantir que não passe dos limites (3-18 para atributos base)
        result[attr as keyof AttributeSet] = Math.max(3, Math.min(18, result[attr as keyof AttributeSet]));
      }
    });

    return result;
  }

  /**
   * Valida se um conjunto de atributos é válido
   */
  public validateAttributes(attributes: AttributeSet): {
    valid: boolean;
    errors: string[];
    warnings: string[];
  } {
    const errors: string[] = [];
    const warnings: string[] = [];

    // Verificar limites
    Object.entries(attributes).forEach(([attr, value]) => {
      if (value < 3) {
        errors.push(`${attr} não pode ser menor que 3`);
      }
      if (value > 18) {
        errors.push(`${attr} não pode ser maior que 18`);
      }
      if (value < 8) {
        warnings.push(`${attr} está muito baixo (${value})`);
      }
    });

    // Verificar total muito baixo
    const total = Object.values(attributes).reduce((sum, val) => sum + val, 0);
    if (total < 70) {
      warnings.push(`Total de atributos muito baixo (${total})`);
    }

    // Verificar se todos os atributos são muito baixos
    const highAttributes = Object.values(attributes).filter(val => val >= 13).length;
    if (highAttributes === 0) {
      warnings.push('Nenhum atributo alto (13+)');
    }

    return {
      valid: errors.length === 0,
      errors,
      warnings
    };
  }

  /**
   * Calcula pontos de vida baseados em constituição e classe
   */
  public calculateHitPoints(
    constitution: number, 
    hitDie: number, 
    level: number = 1
  ): number {
    const conModifier = this.getModifier(constitution);
    let hitPoints = hitDie + conModifier; // Primeiro nível sempre máximo

    // Níveis adicionais
    for (let i = 2; i <= level; i++) {
      const roll = this.diceSystem.rollHitPoints(hitDie, conModifier);
      hitPoints += roll.total;
    }

    return Math.max(1, hitPoints); // Mínimo 1 PV
  }

  /**
   * Sugere distribuição de atributos para uma classe
   */
  public suggestAttributeDistribution(
    className: string,
    availablePoints: number[] = this.standardArray
  ): { [key in keyof AttributeSet]: number } {
    const suggestions: { [key: string]: (keyof AttributeSet)[] } = {
      'guerreiro': ['strength', 'constitution', 'dexterity', 'wisdom', 'charisma', 'intelligence'],
      'ladrao': ['dexterity', 'constitution', 'strength', 'intelligence', 'wisdom', 'charisma'],
      'mago': ['intelligence', 'constitution', 'dexterity', 'wisdom', 'charisma', 'strength'],
      'clerigo': ['wisdom', 'constitution', 'strength', 'charisma', 'dexterity', 'intelligence']
    };

    const priority = suggestions[className.toLowerCase()] || suggestions['guerreiro'];
    const sortedPoints = [...availablePoints].sort((a, b) => b - a);
    
    const result: { [key in keyof AttributeSet]: number } = {
      strength: 10,
      dexterity: 10,
      constitution: 10,
      intelligence: 10,
      wisdom: 10,
      charisma: 10
    };

    priority.forEach((attr, index) => {
      if (index < sortedPoints.length) {
        result[attr] = sortedPoints[index];
      }
    });

    return result;
  }

  /**
   * Calcula custo em pontos para point buy
   */
  public calculatePointBuyCost(attributes: AttributeSet): number {
    return Object.values(attributes).reduce((total, score) => {
      return total + (this.pointBuyCosts[score] || 0);
    }, 0);
  }

  /**
   * Verifica se uma distribuição point buy é válida
   */
  public isValidPointBuy(attributes: AttributeSet): boolean {
    const cost = this.calculatePointBuyCost(attributes);
    const allInRange = Object.values(attributes).every(score => score >= 8 && score <= 15);
    
    return cost <= this.pointBuyPoints && allInRange;
  }

  private generateStandardArray(): AttributeSet {
    // Retorna array padrão em ordem aleatória
    const shuffled = [...this.standardArray].sort(() => Math.random() - 0.5);
    
    return {
      strength: shuffled[0],
      dexterity: shuffled[1],
      constitution: shuffled[2],
      intelligence: shuffled[3],
      wisdom: shuffled[4],
      charisma: shuffled[5]
    };
  }

  private generatePointBuy(): AttributeSet {
    // Distribuição básica equilibrada para point buy
    return {
      strength: 13,
      dexterity: 12,
      constitution: 13,
      intelligence: 10,
      wisdom: 12,
      charisma: 10
    };
  }

  private roll4d6DropLowest(): AttributeSet {
    return {
      strength: this.diceSystem.rollAttribute().total,
      dexterity: this.diceSystem.rollAttribute().total,
      constitution: this.diceSystem.rollAttribute().total,
      intelligence: this.diceSystem.rollAttribute().total,
      wisdom: this.diceSystem.rollAttribute().total,
      charisma: this.diceSystem.rollAttribute().total
    };
  }

  private roll3d6Straight(): AttributeSet {
    return {
      strength: this.diceSystem.rollMultiple(3, 6).total,
      dexterity: this.diceSystem.rollMultiple(3, 6).total,
      constitution: this.diceSystem.rollMultiple(3, 6).total,
      intelligence: this.diceSystem.rollMultiple(3, 6).total,
      wisdom: this.diceSystem.rollMultiple(3, 6).total,
      charisma: this.diceSystem.rollMultiple(3, 6).total
    };
  }

  private roll4d6RerollOnes(): AttributeSet {
    return {
      strength: this.roll4d6RerollOnesDetailed().total,
      dexterity: this.roll4d6RerollOnesDetailed().total,
      constitution: this.roll4d6RerollOnesDetailed().total,
      intelligence: this.roll4d6RerollOnesDetailed().total,
      wisdom: this.roll4d6RerollOnesDetailed().total,
      charisma: this.roll4d6RerollOnesDetailed().total
    };
  }

  private roll4d6RerollOnesDetailed(): { rolls: number[]; total: number } {
    const rolls: number[] = [];
    
    for (let i = 0; i < 4; i++) {
      let roll = Math.floor(Math.random() * 6) + 1;
      
      // Reroll ones
      if (roll === 1) {
        roll = Math.floor(Math.random() * 6) + 1;
      }
      
      rolls.push(roll);
    }
    
    // Sort and drop lowest
    rolls.sort((a, b) => b - a);
    const bestThree = rolls.slice(0, 3);
    const total = bestThree.reduce((sum, roll) => sum + roll, 0);
    
    return { rolls, total };
  }

  /**
   * Gera múltiplos conjuntos de atributos para escolha
   */
  public generateMultipleSets(
    method: GenerationMethod, 
    count: number = 6
  ): Array<{ attributes: AttributeSet; total: number; average: number }> {
    const sets: Array<{ attributes: AttributeSet; total: number; average: number }> = [];
    
    for (let i = 0; i < count; i++) {
      const attributes = this.generateAttributes(method);
      const total = Object.values(attributes).reduce((sum, val) => sum + val, 0);
      const average = total / 6;
      
      sets.push({ attributes, total, average });
    }
    
    return sets.sort((a, b) => b.total - a.total); // Ordenar por total decrescente
  }

  /**
   * Calcula estatísticas de um conjunto de atributos
   */
  public getAttributeStatistics(attributes: AttributeSet): {
    total: number;
    average: number;
    highest: number;
    lowest: number;
    modifierSum: number;
    positiveModifiers: number;
    negativeModifiers: number;
  } {
    const values = Object.values(attributes);
    const modifiers = Object.values(this.getModifiers(attributes));
    
    return {
      total: values.reduce((sum, val) => sum + val, 0),
      average: values.reduce((sum, val) => sum + val, 0) / 6,
      highest: Math.max(...values),
      lowest: Math.min(...values),
      modifierSum: modifiers.reduce((sum, mod) => sum + mod, 0),
      positiveModifiers: modifiers.filter(mod => mod > 0).length,
      negativeModifiers: modifiers.filter(mod => mod < 0).length
    };
  }
}

export default AttributeDistribution;
