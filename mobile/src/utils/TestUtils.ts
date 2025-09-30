import { DiceSystem } from './DiceSystem';
import { AttributeDistribution, GenerationMethod } from '../game/character/AttributeDistribution';
import { DungeonGenerator } from '../game/dungeon/DungeonGenerator';
import { InventorySystem } from '../game/inventory/InventorySystem';

/**
 * Utilitários para testes e validação do sistema de jogo
 */
export class TestUtils {
  private diceSystem: DiceSystem;
  private attributeDistribution: AttributeDistribution;
  private dungeonGenerator: DungeonGenerator;

  constructor() {
    this.diceSystem = DiceSystem.getInstance();
    this.attributeDistribution = new AttributeDistribution();
    this.dungeonGenerator = new DungeonGenerator();
  }

  /**
   * Testa o sistema de dados
   */
  public testDiceSystem(): TestResult {
    const results: string[] = [];
    let passed = 0;
    let total = 0;

    // Teste 1: Rolagem básica
    total++;
    try {
      const roll = this.diceSystem.roll(20);
      if (roll.result >= 1 && roll.result <= 20) {
        results.push('✅ Rolagem básica d20: PASSOU');
        passed++;
      } else {
        results.push(`❌ Rolagem básica d20: FALHOU (resultado: ${roll.result})`);
      }
    } catch (error) {
      results.push(`❌ Rolagem básica d20: ERRO (${error})`);
    }

    // Teste 2: Múltiplas rolagens
    total++;
    try {
      const rolls = this.diceSystem.rollMultiple(3, 6);
      if (rolls.rolls.length === 3 && rolls.total >= 3 && rolls.total <= 18) {
        results.push('✅ Múltiplas rolagens 3d6: PASSOU');
        passed++;
      } else {
        results.push(`❌ Múltiplas rolagens 3d6: FALHOU (total: ${rolls.total})`);
      }
    } catch (error) {
      results.push(`❌ Múltiplas rolagens 3d6: ERRO (${error})`);
    }

    // Teste 3: Rolagem de atributo
    total++;
    try {
      const attributeRoll = this.diceSystem.rollAttribute();
      if (attributeRoll.total >= 3 && attributeRoll.total <= 18 && attributeRoll.rolls.length === 4) {
        results.push('✅ Rolagem de atributo: PASSOU');
        passed++;
      } else {
        results.push(`❌ Rolagem de atributo: FALHOU (total: ${attributeRoll.total})`);
      }
    } catch (error) {
      results.push(`❌ Rolagem de atributo: ERRO (${error})`);
    }

    // Teste 4: Rolagem com modificador
    total++;
    try {
      const modifiedRoll = this.diceSystem.rollWithModifier(20, 5);
      if (modifiedRoll.total >= 6 && modifiedRoll.total <= 25) {
        results.push('✅ Rolagem com modificador: PASSOU');
        passed++;
      } else {
        results.push(`❌ Rolagem com modificador: FALHOU (total: ${modifiedRoll.total})`);
      }
    } catch (error) {
      results.push(`❌ Rolagem com modificador: ERRO (${error})`);
    }

    return {
      category: 'Sistema de Dados',
      passed,
      total,
      results
    };
  }

  /**
   * Testa o sistema de distribuição de atributos
   */
  public testAttributeDistribution(): TestResult {
    const results: string[] = [];
    let passed = 0;
    let total = 0;

    // Teste 1: Geração de atributos
    total++;
    try {
      const attributes = this.attributeDistribution.generateAttributes(GenerationMethod.ROLL_4D6_DROP_LOWEST);
      const values = Object.values(attributes);
      const allValid = values.every(val => val >= 3 && val <= 18);
      
      if (allValid && values.length === 6) {
        results.push('✅ Geração de atributos: PASSOU');
        passed++;
      } else {
        results.push(`❌ Geração de atributos: FALHOU (valores inválidos)`);
      }
    } catch (error) {
      results.push(`❌ Geração de atributos: ERRO (${error})`);
    }

    // Teste 2: Cálculo de modificadores
    total++;
    try {
      const testAttributes = {
        strength: 16,
        dexterity: 14,
        constitution: 12,
        intelligence: 10,
        wisdom: 8,
        charisma: 6
      };
      
      const modifiers = this.attributeDistribution.getModifiers(testAttributes);
      const expectedModifiers = { strength: 3, dexterity: 2, constitution: 1, intelligence: 0, wisdom: -1, charisma: -2 };
      
      const modifiersMatch = Object.entries(expectedModifiers).every(
        ([key, value]) => modifiers[key as keyof typeof modifiers] === value
      );
      
      if (modifiersMatch) {
        results.push('✅ Cálculo de modificadores: PASSOU');
        passed++;
      } else {
        results.push(`❌ Cálculo de modificadores: FALHOU`);
      }
    } catch (error) {
      results.push(`❌ Cálculo de modificadores: ERRO (${error})`);
    }

    // Teste 3: Aplicação de modificadores raciais
    total++;
    try {
      const baseAttributes = {
        strength: 10,
        dexterity: 10,
        constitution: 10,
        intelligence: 10,
        wisdom: 10,
        charisma: 10
      };
      
      const racialModifiers = { strength: 2, dexterity: -1 };
      const modifiedAttributes = this.attributeDistribution.applyRacialModifiers(baseAttributes, racialModifiers);
      
      if (modifiedAttributes.strength === 12 && modifiedAttributes.dexterity === 9) {
        results.push('✅ Modificadores raciais: PASSOU');
        passed++;
      } else {
        results.push(`❌ Modificadores raciais: FALHOU`);
      }
    } catch (error) {
      results.push(`❌ Modificadores raciais: ERRO (${error})`);
    }

    // Teste 4: Validação de atributos
    total++;
    try {
      const validAttributes = {
        strength: 15,
        dexterity: 14,
        constitution: 13,
        intelligence: 12,
        wisdom: 10,
        charisma: 8
      };
      
      const invalidAttributes = {
        strength: 25, // Muito alto
        dexterity: 2,  // Muito baixo
        constitution: 13,
        intelligence: 12,
        wisdom: 10,
        charisma: 8
      };
      
      const validResult = this.attributeDistribution.validateAttributes(validAttributes);
      const invalidResult = this.attributeDistribution.validateAttributes(invalidAttributes);
      
      if (validResult.valid && !invalidResult.valid) {
        results.push('✅ Validação de atributos: PASSOU');
        passed++;
      } else {
        results.push(`❌ Validação de atributos: FALHOU`);
      }
    } catch (error) {
      results.push(`❌ Validação de atributos: ERRO (${error})`);
    }

    return {
      category: 'Distribuição de Atributos',
      passed,
      total,
      results
    };
  }

  /**
   * Testa o gerador de dungeons
   */
  public testDungeonGenerator(): TestResult {
    const results: string[] = [];
    let passed = 0;
    let total = 0;

    // Teste 1: Geração básica de dungeon
    total++;
    try {
      const dungeon = this.dungeonGenerator.generateDungeon(1, { width: 5, height: 5 });
      
      if (dungeon.rooms.length > 0 && dungeon.level === 1) {
        results.push('✅ Geração básica de dungeon: PASSOU');
        passed++;
      } else {
        results.push(`❌ Geração básica de dungeon: FALHOU`);
      }
    } catch (error) {
      results.push(`❌ Geração básica de dungeon: ERRO (${error})`);
    }

    // Teste 2: Presença de sala de entrada
    total++;
    try {
      const dungeon = this.dungeonGenerator.generateDungeon(1, { width: 8, height: 8 });
      const hasEntrance = dungeon.rooms.some(room => room.type === 'entrance');
      
      if (hasEntrance) {
        results.push('✅ Sala de entrada presente: PASSOU');
        passed++;
      } else {
        results.push(`❌ Sala de entrada presente: FALHOU`);
      }
    } catch (error) {
      results.push(`❌ Sala de entrada presente: ERRO (${error})`);
    }

    // Teste 3: Conectividade das salas
    total++;
    try {
      const dungeon = this.dungeonGenerator.generateDungeon(1, { width: 6, height: 6 });
      const allRoomsConnected = dungeon.rooms.every(room => 
        Object.values(room.connections).some(connected => connected)
      );
      
      if (allRoomsConnected) {
        results.push('✅ Conectividade das salas: PASSOU');
        passed++;
      } else {
        results.push(`❌ Conectividade das salas: FALHOU`);
      }
    } catch (error) {
      results.push(`❌ Conectividade das salas: ERRO (${error})`);
    }

    return {
      category: 'Gerador de Dungeons',
      passed,
      total,
      results
    };
  }

  /**
   * Testa o sistema de inventário
   */
  public testInventorySystem(): TestResult {
    const results: string[] = [];
    let passed = 0;
    let total = 0;

    // Teste 1: Criação de inventário
    total++;
    try {
      const inventory = new InventorySystem(10);
      
      if (inventory.getItems().length === 0) {
        results.push('✅ Criação de inventário: PASSOU');
        passed++;
      } else {
        results.push(`❌ Criação de inventário: FALHOU`);
      }
    } catch (error) {
      results.push(`❌ Criação de inventário: ERRO (${error})`);
    }

    // Teste 2: Adição de ouro
    total++;
    try {
      const inventory = new InventorySystem(10);
      inventory.addGold(100);
      
      if (inventory.getGold() === 100) {
        results.push('✅ Adição de ouro: PASSOU');
        passed++;
      } else {
        results.push(`❌ Adição de ouro: FALHOU`);
      }
    } catch (error) {
      results.push(`❌ Adição de ouro: ERRO (${error})`);
    }

    // Teste 3: Remoção de ouro
    total++;
    try {
      const inventory = new InventorySystem(10);
      inventory.addGold(100);
      const removed = inventory.removeGold(50);
      
      if (removed && inventory.getGold() === 50) {
        results.push('✅ Remoção de ouro: PASSOU');
        passed++;
      } else {
        results.push(`❌ Remoção de ouro: FALHOU`);
      }
    } catch (error) {
      results.push(`❌ Remoção de ouro: ERRO (${error})`);
    }

    // Teste 4: Tentativa de remoção de ouro insuficiente
    total++;
    try {
      const inventory = new InventorySystem(10);
      inventory.addGold(30);
      const removed = inventory.removeGold(50);
      
      if (!removed && inventory.getGold() === 30) {
        results.push('✅ Proteção contra ouro insuficiente: PASSOU');
        passed++;
      } else {
        results.push(`❌ Proteção contra ouro insuficiente: FALHOU`);
      }
    } catch (error) {
      results.push(`❌ Proteção contra ouro insuficiente: ERRO (${error})`);
    }

    return {
      category: 'Sistema de Inventário',
      passed,
      total,
      results
    };
  }

  /**
   * Executa todos os testes
   */
  public runAllTests(): TestSuite {
    const testResults = [
      this.testDiceSystem(),
      this.testAttributeDistribution(),
      this.testDungeonGenerator(),
      this.testInventorySystem()
    ];

    const totalPassed = testResults.reduce((sum, result) => sum + result.passed, 0);
    const totalTests = testResults.reduce((sum, result) => sum + result.total, 0);

    return {
      results: testResults,
      summary: {
        totalPassed,
        totalTests,
        successRate: totalTests > 0 ? (totalPassed / totalTests) * 100 : 0
      }
    };
  }

  /**
   * Testa a performance do sistema
   */
  public runPerformanceTests(): PerformanceTestResult {
    const results: PerformanceTest[] = [];

    // Teste de performance: Geração de atributos
    const attributeStart = Date.now();
    for (let i = 0; i < 1000; i++) {
      this.attributeDistribution.generateAttributes(GenerationMethod.ROLL_4D6_DROP_LOWEST);
    }
    const attributeTime = Date.now() - attributeStart;
    
    results.push({
      name: 'Geração de Atributos (1000x)',
      duration: attributeTime,
      operations: 1000,
      opsPerSecond: 1000 / (attributeTime / 1000)
    });

    // Teste de performance: Rolagem de dados
    const diceStart = Date.now();
    for (let i = 0; i < 10000; i++) {
      this.diceSystem.roll(20);
    }
    const diceTime = Date.now() - diceStart;
    
    results.push({
      name: 'Rolagem de Dados (10000x)',
      duration: diceTime,
      operations: 10000,
      opsPerSecond: 10000 / (diceTime / 1000)
    });

    // Teste de performance: Geração de dungeon
    const dungeonStart = Date.now();
    for (let i = 0; i < 100; i++) {
      this.dungeonGenerator.generateDungeon(1, { width: 10, height: 10 });
    }
    const dungeonTime = Date.now() - dungeonStart;
    
    results.push({
      name: 'Geração de Dungeon (100x)',
      duration: dungeonTime,
      operations: 100,
      opsPerSecond: 100 / (dungeonTime / 1000)
    });

    return {
      tests: results,
      totalDuration: results.reduce((sum, test) => sum + test.duration, 0)
    };
  }

  /**
   * Gera dados de teste para desenvolvimento
   */
  public generateTestData(): TestData {
    return {
      characters: [
        {
          name: 'Guerreiro Teste',
          race: 'Humano',
          class: 'Guerreiro',
          level: 1,
          attributes: this.attributeDistribution.generateAttributes(GenerationMethod.STANDARD_ARRAY),
          hitPoints: 12
        },
        {
          name: 'Mago Teste',
          race: 'Elfo',
          class: 'Mago',
          level: 1,
          attributes: this.attributeDistribution.generateAttributes(GenerationMethod.ROLL_4D6_DROP_LOWEST),
          hitPoints: 6
        }
      ],
      dungeons: [
        this.dungeonGenerator.generateDungeon(1, { width: 5, height: 5 }),
        this.dungeonGenerator.generateDungeon(2, { width: 8, height: 8 }),
        this.dungeonGenerator.generateDungeon(3, { width: 12, height: 12 })
      ],
      sampleRolls: Array.from({ length: 20 }, () => this.diceSystem.roll(20))
    };
  }
}

// Interfaces para os resultados dos testes
export interface TestResult {
  category: string;
  passed: number;
  total: number;
  results: string[];
}

export interface TestSuite {
  results: TestResult[];
  summary: {
    totalPassed: number;
    totalTests: number;
    successRate: number;
  };
}

export interface PerformanceTest {
  name: string;
  duration: number;
  operations: number;
  opsPerSecond: number;
}

export interface PerformanceTestResult {
  tests: PerformanceTest[];
  totalDuration: number;
}

export interface TestData {
  characters: Array<{
    name: string;
    race: string;
    class: string;
    level: number;
    attributes: any;
    hitPoints: number;
  }>;
  dungeons: any[];
  sampleRolls: any[];
}

// Singleton para facilitar o uso
let testUtilsInstance: TestUtils | null = null;

export function getTestUtils(): TestUtils {
  if (!testUtilsInstance) {
    testUtilsInstance = new TestUtils();
  }
  return testUtilsInstance;
}

export default TestUtils;
