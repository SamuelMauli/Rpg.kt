export enum RoomType {
  EMPTY = 'empty',
  MONSTER = 'monster',
  TREASURE = 'treasure',
  TRAP = 'trap',
  BOSS = 'boss',
  ENTRANCE = 'entrance',
  EXIT = 'exit',
  SHOP = 'shop',
  SHRINE = 'shrine'
}

export enum Direction {
  NORTH = 'north',
  SOUTH = 'south',
  EAST = 'east',
  WEST = 'west'
}

export interface Room {
  id: string;
  x: number;
  y: number;
  type: RoomType;
  visited: boolean;
  cleared: boolean;
  connections: { [key in Direction]?: string };
  description: string;
  monsters?: string[];
  treasure?: string[];
  trap?: {
    type: string;
    difficulty: number;
    damage: string;
    detected: boolean;
    disarmed: boolean;
  };
  boss?: {
    name: string;
    level: number;
    defeated: boolean;
  };
}

export interface Dungeon {
  id: string;
  name: string;
  level: number;
  size: { width: number; height: number };
  rooms: { [key: string]: Room };
  currentRoomId: string;
  entranceId: string;
  exitId: string;
  bossRoomId: string;
  completed: boolean;
  explorationPercentage: number;
}

export class DungeonGenerator {
  private static instance: DungeonGenerator;
  
  public static getInstance(): DungeonGenerator {
    if (!DungeonGenerator.instance) {
      DungeonGenerator.instance = new DungeonGenerator();
    }
    return DungeonGenerator.instance;
  }

  /**
   * Gera uma nova dungeon aleatória
   */
  public generateDungeon(level: number, size: 'small' | 'medium' | 'large' = 'medium'): Dungeon {
    const dimensions = this.getDungeonDimensions(size);
    const dungeonId = `dungeon_${Date.now()}`;
    
    const dungeon: Dungeon = {
      id: dungeonId,
      name: this.generateDungeonName(),
      level,
      size: dimensions,
      rooms: {},
      currentRoomId: '',
      entranceId: '',
      exitId: '',
      bossRoomId: '',
      completed: false,
      explorationPercentage: 0
    };

    // Gerar layout básico
    this.generateLayout(dungeon);
    
    // Conectar salas
    this.connectRooms(dungeon);
    
    // Distribuir tipos de salas
    this.distributeRoomTypes(dungeon);
    
    // Gerar conteúdo das salas
    this.populateRooms(dungeon);
    
    // Definir sala inicial
    dungeon.currentRoomId = dungeon.entranceId;

    return dungeon;
  }

  private getDungeonDimensions(size: string): { width: number; height: number } {
    switch (size) {
      case 'small':
        return { width: 5, height: 5 };
      case 'medium':
        return { width: 8, height: 8 };
      case 'large':
        return { width: 12, height: 12 };
      default:
        return { width: 8, height: 8 };
    }
  }

  private generateDungeonName(): string {
    const prefixes = [
      'Cavernas', 'Ruínas', 'Catacumbas', 'Fortaleza', 'Torre',
      'Labirinto', 'Cripta', 'Templo', 'Masmorra', 'Calabouço'
    ];
    
    const suffixes = [
      'Sombrias', 'Perdidas', 'Amaldiçoadas', 'Esquecidas', 'Profundas',
      'do Terror', 'da Morte', 'do Caos', 'dos Mortos', 'do Desespero'
    ];

    const prefix = prefixes[Math.floor(Math.random() * prefixes.length)];
    const suffix = suffixes[Math.floor(Math.random() * suffixes.length)];
    
    return `${prefix} ${suffix}`;
  }

  private generateLayout(dungeon: Dungeon): void {
    const { width, height } = dungeon.size;
    const numRooms = Math.floor((width * height) * 0.4); // 40% das células terão salas
    
    const usedPositions = new Set<string>();
    
    for (let i = 0; i < numRooms; i++) {
      let x, y, posKey;
      let attempts = 0;
      
      do {
        x = Math.floor(Math.random() * width);
        y = Math.floor(Math.random() * height);
        posKey = `${x},${y}`;
        attempts++;
      } while (usedPositions.has(posKey) && attempts < 100);
      
      if (attempts < 100) {
        usedPositions.add(posKey);
        
        const roomId = `room_${x}_${y}`;
        dungeon.rooms[roomId] = {
          id: roomId,
          x,
          y,
          type: RoomType.EMPTY,
          visited: false,
          cleared: false,
          connections: {},
          description: ''
        };
      }
    }
  }

  private connectRooms(dungeon: Dungeon): void {
    const rooms = Object.values(dungeon.rooms);
    
    // Conectar salas adjacentes com probabilidade
    rooms.forEach(room => {
      const adjacentPositions = [
        { x: room.x, y: room.y - 1, dir: Direction.NORTH, opposite: Direction.SOUTH },
        { x: room.x, y: room.y + 1, dir: Direction.SOUTH, opposite: Direction.NORTH },
        { x: room.x + 1, y: room.y, dir: Direction.EAST, opposite: Direction.WEST },
        { x: room.x - 1, y: room.y, dir: Direction.WEST, opposite: Direction.EAST }
      ];

      adjacentPositions.forEach(pos => {
        const adjacentRoom = rooms.find(r => r.x === pos.x && r.y === pos.y);
        if (adjacentRoom && Math.random() < 0.7) { // 70% chance de conexão
          room.connections[pos.dir] = adjacentRoom.id;
          adjacentRoom.connections[pos.opposite] = room.id;
        }
      });
    });

    // Garantir que todas as salas sejam acessíveis
    this.ensureConnectivity(dungeon);
  }

  private ensureConnectivity(dungeon: Dungeon): void {
    const rooms = Object.values(dungeon.rooms);
    if (rooms.length === 0) return;

    const visited = new Set<string>();
    const queue = [rooms[0].id];
    visited.add(rooms[0].id);

    // BFS para encontrar salas conectadas
    while (queue.length > 0) {
      const currentId = queue.shift()!;
      const currentRoom = dungeon.rooms[currentId];
      
      Object.values(currentRoom.connections).forEach(connectedId => {
        if (connectedId && !visited.has(connectedId)) {
          visited.add(connectedId);
          queue.push(connectedId);
        }
      });
    }

    // Conectar salas isoladas
    rooms.forEach(room => {
      if (!visited.has(room.id)) {
        const nearestConnectedRoom = this.findNearestConnectedRoom(room, visited, rooms);
        if (nearestConnectedRoom) {
          this.createConnection(room, nearestConnectedRoom);
        }
      }
    });
  }

  private findNearestConnectedRoom(isolatedRoom: Room, connectedRooms: Set<string>, allRooms: Room[]): Room | null {
    let nearest: Room | null = null;
    let minDistance = Infinity;

    allRooms.forEach(room => {
      if (connectedRooms.has(room.id)) {
        const distance = Math.abs(room.x - isolatedRoom.x) + Math.abs(room.y - isolatedRoom.y);
        if (distance < minDistance) {
          minDistance = distance;
          nearest = room;
        }
      }
    });

    return nearest;
  }

  private createConnection(room1: Room, room2: Room): void {
    const dx = room2.x - room1.x;
    const dy = room2.y - room1.y;

    if (Math.abs(dx) > Math.abs(dy)) {
      // Conexão horizontal
      if (dx > 0) {
        room1.connections[Direction.EAST] = room2.id;
        room2.connections[Direction.WEST] = room1.id;
      } else {
        room1.connections[Direction.WEST] = room2.id;
        room2.connections[Direction.EAST] = room1.id;
      }
    } else {
      // Conexão vertical
      if (dy > 0) {
        room1.connections[Direction.SOUTH] = room2.id;
        room2.connections[Direction.NORTH] = room1.id;
      } else {
        room1.connections[Direction.NORTH] = room2.id;
        room2.connections[Direction.SOUTH] = room1.id;
      }
    }
  }

  private distributeRoomTypes(dungeon: Dungeon): void {
    const rooms = Object.values(dungeon.rooms);
    
    // Definir entrada (sala mais próxima do canto inferior esquerdo)
    const entrance = rooms.reduce((closest, room) => {
      const distanceToCorner = room.x + room.y;
      const closestDistance = closest.x + closest.y;
      return distanceToCorner < closestDistance ? room : closest;
    });
    entrance.type = RoomType.ENTRANCE;
    dungeon.entranceId = entrance.id;

    // Definir boss (sala mais distante da entrada)
    const boss = rooms.reduce((farthest, room) => {
      if (room.id === entrance.id) return farthest;
      const distance = Math.abs(room.x - entrance.x) + Math.abs(room.y - entrance.y);
      const farthestDistance = Math.abs(farthest.x - entrance.x) + Math.abs(farthest.y - entrance.y);
      return distance > farthestDistance ? room : farthest;
    });
    boss.type = RoomType.BOSS;
    dungeon.bossRoomId = boss.id;

    // Definir saída (próxima ao boss)
    const exitCandidates = rooms.filter(room => 
      room.id !== entrance.id && 
      room.id !== boss.id &&
      (Math.abs(room.x - boss.x) + Math.abs(room.y - boss.y)) <= 2
    );
    
    if (exitCandidates.length > 0) {
      const exit = exitCandidates[Math.floor(Math.random() * exitCandidates.length)];
      exit.type = RoomType.EXIT;
      dungeon.exitId = exit.id;
    }

    // Distribuir outros tipos de salas
    const remainingRooms = rooms.filter(room => room.type === RoomType.EMPTY);
    const numTreasure = Math.floor(remainingRooms.length * 0.15); // 15% tesouros
    const numTraps = Math.floor(remainingRooms.length * 0.10); // 10% armadilhas
    const numMonsters = Math.floor(remainingRooms.length * 0.40); // 40% monstros
    const numShops = Math.min(1, Math.floor(remainingRooms.length * 0.05)); // 5% lojas
    const numShrines = Math.min(2, Math.floor(remainingRooms.length * 0.08)); // 8% santuários

    this.assignRoomTypes(remainingRooms, RoomType.TREASURE, numTreasure);
    this.assignRoomTypes(remainingRooms, RoomType.TRAP, numTraps);
    this.assignRoomTypes(remainingRooms, RoomType.MONSTER, numMonsters);
    this.assignRoomTypes(remainingRooms, RoomType.SHOP, numShops);
    this.assignRoomTypes(remainingRooms, RoomType.SHRINE, numShrines);
  }

  private assignRoomTypes(rooms: Room[], type: RoomType, count: number): void {
    const availableRooms = rooms.filter(room => room.type === RoomType.EMPTY);
    
    for (let i = 0; i < count && availableRooms.length > 0; i++) {
      const randomIndex = Math.floor(Math.random() * availableRooms.length);
      const room = availableRooms.splice(randomIndex, 1)[0];
      room.type = type;
    }
  }

  private populateRooms(dungeon: Dungeon): void {
    Object.values(dungeon.rooms).forEach(room => {
      switch (room.type) {
        case RoomType.ENTRANCE:
          room.description = "A entrada da dungeon. Você pode sentir o ar frio e úmido vindo das profundezas.";
          break;
          
        case RoomType.EXIT:
          room.description = "Uma escadaria leva para cima, de volta à superfície. A luz do dia filtra através das pedras.";
          break;
          
        case RoomType.BOSS:
          room.description = "Uma sala imponente com teto alto. Você sente uma presença poderosa aqui.";
          room.boss = this.generateBoss(dungeon.level);
          break;
          
        case RoomType.MONSTER:
          room.description = this.generateRoomDescription('monster');
          room.monsters = this.generateMonsters(dungeon.level);
          break;
          
        case RoomType.TREASURE:
          room.description = this.generateRoomDescription('treasure');
          room.treasure = this.generateTreasure(dungeon.level);
          break;
          
        case RoomType.TRAP:
          room.description = this.generateRoomDescription('trap');
          room.trap = this.generateTrap(dungeon.level);
          break;
          
        case RoomType.SHOP:
          room.description = "Um comerciante misterioso montou sua tenda aqui. Suas mercadorias brilham na penumbra.";
          break;
          
        case RoomType.SHRINE:
          room.description = "Um antigo santuário dedicado a uma divindade esquecida. Uma aura de paz permeia o local.";
          break;
          
        default:
          room.description = this.generateRoomDescription('empty');
          break;
      }
    });
  }

  private generateRoomDescription(type: string): string {
    const descriptions = {
      empty: [
        "Uma sala vazia com paredes de pedra úmidas.",
        "Um corredor estreito com ecos distantes.",
        "Uma câmara circular com pilares antigos.",
        "Uma sala pequena com marcas de garras nas paredes."
      ],
      monster: [
        "Você ouve rosnados vindos das sombras.",
        "Ossos espalhados pelo chão indicam perigo.",
        "Pegadas estranhas marcam o chão de terra.",
        "Um cheiro pútrido preenche o ar."
      ],
      treasure: [
        "Algo brilha no canto da sala.",
        "Um baú antigo repousa contra a parede.",
        "Moedas espalhadas capturam a luz da tocha.",
        "Uma urna decorada chama sua atenção."
      ],
      trap: [
        "O chão parece instável aqui.",
        "Você nota marcas suspeitas nas paredes.",
        "Algo não está certo nesta sala.",
        "Mecanismos antigos são visíveis no teto."
      ]
    };

    const typeDescriptions = descriptions[type as keyof typeof descriptions] || descriptions.empty;
    return typeDescriptions[Math.floor(Math.random() * typeDescriptions.length)];
  }

  private generateBoss(dungeonLevel: number): any {
    const bosses = [
      { name: "Rei Goblin", baseLevel: 1 },
      { name: "Necromante Sombrio", baseLevel: 2 },
      { name: "Dragão Jovem", baseLevel: 3 },
      { name: "Lich Ancião", baseLevel: 4 },
      { name: "Demônio Maior", baseLevel: 5 }
    ];

    const appropriateBosses = bosses.filter(boss => boss.baseLevel <= dungeonLevel + 1);
    const selectedBoss = appropriateBosses[Math.floor(Math.random() * appropriateBosses.length)];

    return {
      name: selectedBoss.name,
      level: dungeonLevel + 1,
      defeated: false
    };
  }

  private generateMonsters(dungeonLevel: number): string[] {
    const monstersByLevel = {
      1: ['Goblin', 'Rato Gigante', 'Esqueleto'],
      2: ['Orc', 'Zumbi', 'Aranha Gigante'],
      3: ['Ogro', 'Ghoul', 'Wight'],
      4: ['Troll', 'Wraith', 'Minotauro'],
      5: ['Dragão Menor', 'Vampiro', 'Demônio']
    };

    const availableMonsters = monstersByLevel[Math.min(dungeonLevel, 5) as keyof typeof monstersByLevel];
    const numMonsters = Math.floor(Math.random() * 3) + 1; // 1-3 monstros
    
    const monsters: string[] = [];
    for (let i = 0; i < numMonsters; i++) {
      const monster = availableMonsters[Math.floor(Math.random() * availableMonsters.length)];
      monsters.push(monster);
    }
    
    return monsters;
  }

  private generateTreasure(dungeonLevel: number): string[] {
    const treasures = [
      'Moedas de Ouro',
      'Gema Preciosa',
      'Poção de Cura',
      'Pergaminho Mágico',
      'Arma Encantada',
      'Armadura Mágica',
      'Anel de Proteção',
      'Amuleto da Sorte'
    ];

    const numItems = Math.floor(Math.random() * 2) + 1; // 1-2 itens
    const selectedTreasures: string[] = [];
    
    for (let i = 0; i < numItems; i++) {
      const treasure = treasures[Math.floor(Math.random() * treasures.length)];
      selectedTreasures.push(treasure);
    }
    
    return selectedTreasures;
  }

  private generateTrap(dungeonLevel: number): any {
    const traps = [
      { type: 'Poço Oculto', damage: '1d6', baseDifficulty: 12 },
      { type: 'Dardos Venenosos', damage: '1d4+poison', baseDifficulty: 14 },
      { type: 'Lâminas Rotativas', damage: '2d4', baseDifficulty: 15 },
      { type: 'Gás Tóxico', damage: '1d6+poison', baseDifficulty: 16 },
      { type: 'Raio Mágico', damage: '2d6', baseDifficulty: 18 }
    ];

    const trap = traps[Math.floor(Math.random() * traps.length)];
    
    return {
      type: trap.type,
      difficulty: trap.baseDifficulty + dungeonLevel,
      damage: trap.damage,
      detected: false,
      disarmed: false
    };
  }

  /**
   * Calcula a porcentagem de exploração da dungeon
   */
  public calculateExplorationPercentage(dungeon: Dungeon): number {
    const totalRooms = Object.keys(dungeon.rooms).length;
    const visitedRooms = Object.values(dungeon.rooms).filter(room => room.visited).length;
    
    return totalRooms > 0 ? Math.floor((visitedRooms / totalRooms) * 100) : 0;
  }

  /**
   * Verifica se a dungeon foi completada
   */
  public isDungeonCompleted(dungeon: Dungeon): boolean {
    const bossRoom = dungeon.rooms[dungeon.bossRoomId];
    return bossRoom?.boss?.defeated || false;
  }

  /**
   * Obtém salas adjacentes à sala atual
   */
  public getAdjacentRooms(dungeon: Dungeon, roomId: string): Room[] {
    const currentRoom = dungeon.rooms[roomId];
    if (!currentRoom) return [];

    const adjacentRooms: Room[] = [];
    Object.values(currentRoom.connections).forEach(connectedId => {
      if (connectedId && dungeon.rooms[connectedId]) {
        adjacentRooms.push(dungeon.rooms[connectedId]);
      }
    });

    return adjacentRooms;
  }

  /**
   * Move o jogador para uma nova sala
   */
  public moveToRoom(dungeon: Dungeon, direction: Direction): boolean {
    const currentRoom = dungeon.rooms[dungeon.currentRoomId];
    if (!currentRoom) return false;

    const targetRoomId = currentRoom.connections[direction];
    if (!targetRoomId || !dungeon.rooms[targetRoomId]) return false;

    dungeon.currentRoomId = targetRoomId;
    dungeon.rooms[targetRoomId].visited = true;
    dungeon.explorationPercentage = this.calculateExplorationPercentage(dungeon);

    return true;
  }
}

export default DungeonGenerator;
