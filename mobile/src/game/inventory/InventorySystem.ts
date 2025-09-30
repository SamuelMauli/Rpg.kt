export enum ItemType {
  WEAPON = 'weapon',
  ARMOR = 'armor',
  SHIELD = 'shield',
  POTION = 'potion',
  SCROLL = 'scroll',
  TREASURE = 'treasure',
  TOOL = 'tool',
  MISC = 'misc'
}

export enum ItemRarity {
  COMMON = 'common',
  UNCOMMON = 'uncommon',
  RARE = 'rare',
  EPIC = 'epic',
  LEGENDARY = 'legendary'
}

export interface Item {
  id: string;
  name: string;
  description: string;
  type: ItemType;
  rarity: ItemRarity;
  value: number;
  weight: number;
  stackable: boolean;
  maxStack: number;
  usable: boolean;
  equipable: boolean;
  properties: { [key: string]: any };
  sprite?: string;
}

export interface InventorySlot {
  item: Item | null;
  quantity: number;
  equipped: boolean;
}

export interface Equipment {
  weapon?: Item;
  armor?: Item;
  shield?: Item;
  helmet?: Item;
  boots?: Item;
  gloves?: Item;
  ring1?: Item;
  ring2?: Item;
  amulet?: Item;
}

export class InventorySystem {
  private slots: InventorySlot[];
  private maxSlots: number;
  private equipment: Equipment;
  private gold: number;

  constructor(maxSlots: number = 30) {
    this.maxSlots = maxSlots;
    this.slots = Array(maxSlots).fill(null).map(() => ({
      item: null,
      quantity: 0,
      equipped: false
    }));
    this.equipment = {};
    this.gold = 0;
  }

  /**
   * Adiciona um item ao inventário
   */
  public addItem(item: Item, quantity: number = 1): boolean {
    if (quantity <= 0) return false;

    // Verificar se o item é empilhável e já existe no inventário
    if (item.stackable) {
      const existingSlot = this.slots.find(slot => 
        slot.item && slot.item.id === item.id && slot.quantity < item.maxStack
      );

      if (existingSlot) {
        const spaceAvailable = item.maxStack - existingSlot.quantity;
        const toAdd = Math.min(quantity, spaceAvailable);
        existingSlot.quantity += toAdd;
        quantity -= toAdd;

        if (quantity === 0) return true;
      }
    }

    // Procurar slots vazios para o restante
    while (quantity > 0) {
      const emptySlot = this.slots.find(slot => slot.item === null);
      if (!emptySlot) return false; // Inventário cheio

      const toAdd = item.stackable ? Math.min(quantity, item.maxStack) : 1;
      emptySlot.item = { ...item };
      emptySlot.quantity = toAdd;
      quantity -= toAdd;
    }

    return true;
  }

  /**
   * Remove um item do inventário
   */
  public removeItem(itemId: string, quantity: number = 1): boolean {
    let remaining = quantity;

    for (const slot of this.slots) {
      if (slot.item && slot.item.id === itemId && remaining > 0) {
        const toRemove = Math.min(remaining, slot.quantity);
        slot.quantity -= toRemove;
        remaining -= toRemove;

        if (slot.quantity === 0) {
          slot.item = null;
          slot.equipped = false;
        }
      }
    }

    return remaining === 0;
  }

  /**
   * Usa um item consumível
   */
  public useItem(itemId: string): Item | null {
    const slot = this.slots.find(slot => 
      slot.item && slot.item.id === itemId && slot.item.usable
    );

    if (!slot || !slot.item) return null;

    const item = { ...slot.item };
    this.removeItem(itemId, 1);
    
    return item;
  }

  /**
   * Equipa um item
   */
  public equipItem(itemId: string): boolean {
    const slot = this.slots.find(slot => 
      slot.item && slot.item.id === itemId && slot.item.equipable
    );

    if (!slot || !slot.item) return false;

    const item = slot.item;
    const equipSlot = this.getEquipmentSlot(item.type);

    if (!equipSlot) return false;

    // Desequipar item atual se houver
    if (this.equipment[equipSlot]) {
      this.unequipItem(equipSlot);
    }

    // Equipar novo item
    this.equipment[equipSlot] = item;
    slot.equipped = true;

    return true;
  }

  /**
   * Desequipa um item
   */
  public unequipItem(equipSlot: keyof Equipment): boolean {
    const item = this.equipment[equipSlot];
    if (!item) return false;

    // Encontrar o slot no inventário
    const slot = this.slots.find(s => s.item && s.item.id === item.id);
    if (slot) {
      slot.equipped = false;
    }

    delete this.equipment[equipSlot];
    return true;
  }

  /**
   * Obtém todos os itens do inventário
   */
  public getItems(): InventorySlot[] {
    return this.slots.filter(slot => slot.item !== null);
  }

  /**
   * Obtém itens por tipo
   */
  public getItemsByType(type: ItemType): InventorySlot[] {
    return this.slots.filter(slot => slot.item && slot.item.type === type);
  }

  /**
   * Verifica se tem um item específico
   */
  public hasItem(itemId: string, quantity: number = 1): boolean {
    const totalQuantity = this.slots
      .filter(slot => slot.item && slot.item.id === itemId)
      .reduce((total, slot) => total + slot.quantity, 0);

    return totalQuantity >= quantity;
  }

  /**
   * Obtém a quantidade de um item
   */
  public getItemQuantity(itemId: string): number {
    return this.slots
      .filter(slot => slot.item && slot.item.id === itemId)
      .reduce((total, slot) => total + slot.quantity, 0);
  }

  /**
   * Calcula o peso total do inventário
   */
  public getTotalWeight(): number {
    return this.slots
      .filter(slot => slot.item)
      .reduce((total, slot) => total + (slot.item!.weight * slot.quantity), 0);
  }

  /**
   * Calcula o valor total do inventário
   */
  public getTotalValue(): number {
    return this.slots
      .filter(slot => slot.item)
      .reduce((total, slot) => total + (slot.item!.value * slot.quantity), 0);
  }

  /**
   * Obtém slots vazios
   */
  public getEmptySlots(): number {
    return this.slots.filter(slot => slot.item === null).length;
  }

  /**
   * Verifica se o inventário está cheio
   */
  public isFull(): boolean {
    return this.getEmptySlots() === 0;
  }

  /**
   * Ordena o inventário
   */
  public sortInventory(sortBy: 'name' | 'type' | 'value' | 'rarity' = 'type'): void {
    const items = this.getItems();
    
    items.sort((a, b) => {
      if (!a.item || !b.item) return 0;
      
      switch (sortBy) {
        case 'name':
          return a.item.name.localeCompare(b.item.name);
        case 'type':
          return a.item.type.localeCompare(b.item.type);
        case 'value':
          return b.item.value - a.item.value;
        case 'rarity':
          const rarityOrder = {
            [ItemRarity.COMMON]: 1,
            [ItemRarity.UNCOMMON]: 2,
            [ItemRarity.RARE]: 3,
            [ItemRarity.EPIC]: 4,
            [ItemRarity.LEGENDARY]: 5
          };
          return rarityOrder[b.item.rarity] - rarityOrder[a.item.rarity];
        default:
          return 0;
      }
    });

    // Reorganizar slots
    this.slots = Array(this.maxSlots).fill(null).map(() => ({
      item: null,
      quantity: 0,
      equipped: false
    }));

    items.forEach((slot, index) => {
      this.slots[index] = slot;
    });
  }

  /**
   * Adiciona ouro
   */
  public addGold(amount: number): void {
    this.gold = Math.max(0, this.gold + amount);
  }

  /**
   * Remove ouro
   */
  public removeGold(amount: number): boolean {
    if (this.gold >= amount) {
      this.gold -= amount;
      return true;
    }
    return false;
  }

  /**
   * Obtém quantidade de ouro
   */
  public getGold(): number {
    return this.gold;
  }

  /**
   * Obtém equipamentos atuais
   */
  public getEquipment(): Equipment {
    return { ...this.equipment };
  }

  /**
   * Calcula bônus de equipamentos
   */
  public getEquipmentBonuses(): {
    attackBonus: number;
    damageBonus: number;
    armorClass: number;
    savingThrows: number;
    attributes: { [key: string]: number };
  } {
    const bonuses = {
      attackBonus: 0,
      damageBonus: 0,
      armorClass: 0,
      savingThrows: 0,
      attributes: {
        strength: 0,
        dexterity: 0,
        constitution: 0,
        intelligence: 0,
        wisdom: 0,
        charisma: 0
      }
    };

    Object.values(this.equipment).forEach(item => {
      if (item && item.properties) {
        bonuses.attackBonus += item.properties.attackBonus || 0;
        bonuses.damageBonus += item.properties.damageBonus || 0;
        bonuses.armorClass += item.properties.armorClass || 0;
        bonuses.savingThrows += item.properties.savingThrows || 0;

        // Bônus de atributos
        Object.keys(bonuses.attributes).forEach(attr => {
          bonuses.attributes[attr] += item.properties[attr] || 0;
        });
      }
    });

    return bonuses;
  }

  private getEquipmentSlot(itemType: ItemType): keyof Equipment | null {
    switch (itemType) {
      case ItemType.WEAPON:
        return 'weapon';
      case ItemType.ARMOR:
        return 'armor';
      case ItemType.SHIELD:
        return 'shield';
      default:
        return null;
    }
  }

  /**
   * Serializa o inventário para salvamento
   */
  public serialize(): any {
    return {
      slots: this.slots,
      equipment: this.equipment,
      gold: this.gold,
      maxSlots: this.maxSlots
    };
  }

  /**
   * Carrega inventário de dados salvos
   */
  public deserialize(data: any): void {
    this.slots = data.slots || [];
    this.equipment = data.equipment || {};
    this.gold = data.gold || 0;
    this.maxSlots = data.maxSlots || 30;

    // Garantir que temos o número correto de slots
    while (this.slots.length < this.maxSlots) {
      this.slots.push({
        item: null,
        quantity: 0,
        equipped: false
      });
    }
  }
}

/**
 * Factory para criar itens predefinidos
 */
export class ItemFactory {
  private static items: { [key: string]: Item } = {
    // Armas
    'sword_short': {
      id: 'sword_short',
      name: 'Espada Curta',
      description: 'Uma espada leve e versátil.',
      type: ItemType.WEAPON,
      rarity: ItemRarity.COMMON,
      value: 10,
      weight: 3,
      stackable: false,
      maxStack: 1,
      usable: false,
      equipable: true,
      properties: {
        damage: '1d6',
        attackBonus: 0,
        damageBonus: 0
      }
    },
    'sword_long': {
      id: 'sword_long',
      name: 'Espada Longa',
      description: 'Uma espada de duas mãos poderosa.',
      type: ItemType.WEAPON,
      rarity: ItemRarity.COMMON,
      value: 15,
      weight: 4,
      stackable: false,
      maxStack: 1,
      usable: false,
      equipable: true,
      properties: {
        damage: '1d8',
        attackBonus: 0,
        damageBonus: 0
      }
    },
    'bow_long': {
      id: 'bow_long',
      name: 'Arco Longo',
      description: 'Um arco para ataques à distância.',
      type: ItemType.WEAPON,
      rarity: ItemRarity.COMMON,
      value: 12,
      weight: 2,
      stackable: false,
      maxStack: 1,
      usable: false,
      equipable: true,
      properties: {
        damage: '1d6',
        range: 150,
        attackBonus: 0,
        damageBonus: 0
      }
    },

    // Armaduras
    'leather_armor': {
      id: 'leather_armor',
      name: 'Armadura de Couro',
      description: 'Proteção básica de couro.',
      type: ItemType.ARMOR,
      rarity: ItemRarity.COMMON,
      value: 20,
      weight: 10,
      stackable: false,
      maxStack: 1,
      usable: false,
      equipable: true,
      properties: {
        armorClass: 2
      }
    },
    'chain_mail': {
      id: 'chain_mail',
      name: 'Cota de Malha',
      description: 'Armadura de anéis metálicos entrelaçados.',
      type: ItemType.ARMOR,
      rarity: ItemRarity.UNCOMMON,
      value: 50,
      weight: 25,
      stackable: false,
      maxStack: 1,
      usable: false,
      equipable: true,
      properties: {
        armorClass: 5
      }
    },

    // Escudos
    'shield_small': {
      id: 'shield_small',
      name: 'Escudo Pequeno',
      description: 'Um escudo leve para defesa.',
      type: ItemType.SHIELD,
      rarity: ItemRarity.COMMON,
      value: 8,
      weight: 5,
      stackable: false,
      maxStack: 1,
      usable: false,
      equipable: true,
      properties: {
        armorClass: 1
      }
    },

    // Poções
    'potion_healing': {
      id: 'potion_healing',
      name: 'Poção de Cura',
      description: 'Restaura pontos de vida.',
      type: ItemType.POTION,
      rarity: ItemRarity.COMMON,
      value: 25,
      weight: 0.5,
      stackable: true,
      maxStack: 10,
      usable: true,
      equipable: false,
      properties: {
        healing: '2d4+2'
      }
    },
    'potion_strength': {
      id: 'potion_strength',
      name: 'Poção de Força',
      description: 'Aumenta temporariamente a força.',
      type: ItemType.POTION,
      rarity: ItemRarity.UNCOMMON,
      value: 50,
      weight: 0.5,
      stackable: true,
      maxStack: 5,
      usable: true,
      equipable: false,
      properties: {
        strengthBonus: 4,
        duration: 600 // 10 minutos
      }
    },

    // Pergaminhos
    'scroll_magic_missile': {
      id: 'scroll_magic_missile',
      name: 'Pergaminho de Mísseis Mágicos',
      description: 'Lança mísseis mágicos no alvo.',
      type: ItemType.SCROLL,
      rarity: ItemRarity.COMMON,
      value: 30,
      weight: 0.1,
      stackable: true,
      maxStack: 5,
      usable: true,
      equipable: false,
      properties: {
        spell: 'magic_missile',
        damage: '3d4+3'
      }
    },

    // Tesouros
    'gem_ruby': {
      id: 'gem_ruby',
      name: 'Rubi',
      description: 'Uma gema preciosa vermelha.',
      type: ItemType.TREASURE,
      rarity: ItemRarity.RARE,
      value: 100,
      weight: 0.1,
      stackable: true,
      maxStack: 10,
      usable: false,
      equipable: false,
      properties: {}
    },
    'gold_coins': {
      id: 'gold_coins',
      name: 'Moedas de Ouro',
      description: 'Moedas de ouro puro.',
      type: ItemType.TREASURE,
      rarity: ItemRarity.COMMON,
      value: 1,
      weight: 0.02,
      stackable: true,
      maxStack: 1000,
      usable: false,
      equipable: false,
      properties: {}
    }
  };

  public static createItem(itemId: string): Item | null {
    const template = this.items[itemId];
    if (!template) return null;

    return { ...template };
  }

  public static getAllItems(): Item[] {
    return Object.values(this.items).map(item => ({ ...item }));
  }

  public static getItemsByType(type: ItemType): Item[] {
    return Object.values(this.items)
      .filter(item => item.type === type)
      .map(item => ({ ...item }));
  }

  public static getItemsByRarity(rarity: ItemRarity): Item[] {
    return Object.values(this.items)
      .filter(item => item.rarity === rarity)
      .map(item => ({ ...item }));
  }
}

export default InventorySystem;
