import AsyncStorage from '@react-native-async-storage/async-storage';
import { Personagem } from '../game/classes/Personagem';
import { Dungeon } from '../game/dungeon/DungeonGenerator';

export interface GameSave {
  id: string;
  name: string;
  timestamp: number;
  version: string;
  personagem: any; // Serialized Personagem
  dungeon?: any; // Serialized Dungeon
  gameState: {
    currentScreen: string;
    location: string;
    questProgress: { [key: string]: any };
    inventory: string[];
    experience: number;
    level: number;
    gold: number;
  };
  statistics: {
    playtime: number;
    monstersKilled: number;
    dungeonsCompleted: number;
    treasuresFound: number;
    deathCount: number;
  };
}

export interface SaveSlot {
  id: string;
  name: string;
  timestamp: number;
  preview: {
    characterName: string;
    level: number;
    location: string;
    playtime: number;
  };
}

export class SaveSystem {
  private static instance: SaveSystem;
  private static readonly SAVE_PREFIX = 'rpg_save_';
  private static readonly SAVE_LIST_KEY = 'rpg_save_list';
  private static readonly CURRENT_VERSION = '1.0.0';
  private static readonly MAX_SAVES = 10;

  public static getInstance(): SaveSystem {
    if (!SaveSystem.instance) {
      SaveSystem.instance = new SaveSystem();
    }
    return SaveSystem.instance;
  }

  /**
   * Salva o jogo atual
   */
  public async saveGame(
    personagem: Personagem,
    dungeon: Dungeon | null,
    gameState: any,
    statistics: any,
    saveName?: string
  ): Promise<string> {
    try {
      const saveId = `save_${Date.now()}`;
      const timestamp = Date.now();
      
      const gameSave: GameSave = {
        id: saveId,
        name: saveName || `${personagem.nome} - ${new Date().toLocaleDateString()}`,
        timestamp,
        version: SaveSystem.CURRENT_VERSION,
        personagem: this.serializePersonagem(personagem),
        dungeon: dungeon ? this.serializeDungeon(dungeon) : null,
        gameState: {
          currentScreen: gameState.currentScreen || 'AVENTURA',
          location: gameState.location || 'Cidade',
          questProgress: gameState.questProgress || {},
          inventory: gameState.inventory || [],
          experience: personagem.experiencia,
          level: personagem.nivel,
          gold: personagem.dinheiro
        },
        statistics: {
          playtime: statistics.playtime || 0,
          monstersKilled: statistics.monstersKilled || 0,
          dungeonsCompleted: statistics.dungeonsCompleted || 0,
          treasuresFound: statistics.treasuresFound || 0,
          deathCount: statistics.deathCount || 0
        }
      };

      // Salvar o arquivo de save
      await AsyncStorage.setItem(
        SaveSystem.SAVE_PREFIX + saveId,
        JSON.stringify(gameSave)
      );

      // Atualizar lista de saves
      await this.updateSaveList(saveId, gameSave);

      return saveId;
    } catch (error) {
      console.error('Erro ao salvar jogo:', error);
      throw new Error('Falha ao salvar o jogo');
    }
  }

  /**
   * Carrega um jogo salvo
   */
  public async loadGame(saveId: string): Promise<GameSave> {
    try {
      const saveData = await AsyncStorage.getItem(SaveSystem.SAVE_PREFIX + saveId);
      
      if (!saveData) {
        throw new Error('Save não encontrado');
      }

      const gameSave: GameSave = JSON.parse(saveData);
      
      // Verificar compatibilidade de versão
      if (gameSave.version !== SaveSystem.CURRENT_VERSION) {
        console.warn('Versão do save diferente da atual, tentando migração...');
        // Aqui poderia implementar migração de saves antigos
      }

      return gameSave;
    } catch (error) {
      console.error('Erro ao carregar jogo:', error);
      throw new Error('Falha ao carregar o jogo');
    }
  }

  /**
   * Lista todos os saves disponíveis
   */
  public async getSaveList(): Promise<SaveSlot[]> {
    try {
      const saveListData = await AsyncStorage.getItem(SaveSystem.SAVE_LIST_KEY);
      
      if (!saveListData) {
        return [];
      }

      const saveList: SaveSlot[] = JSON.parse(saveListData);
      
      // Ordenar por timestamp (mais recente primeiro)
      return saveList.sort((a, b) => b.timestamp - a.timestamp);
    } catch (error) {
      console.error('Erro ao listar saves:', error);
      return [];
    }
  }

  /**
   * Deleta um save
   */
  public async deleteSave(saveId: string): Promise<void> {
    try {
      // Remover arquivo de save
      await AsyncStorage.removeItem(SaveSystem.SAVE_PREFIX + saveId);
      
      // Atualizar lista de saves
      const saveList = await this.getSaveList();
      const updatedList = saveList.filter(save => save.id !== saveId);
      
      await AsyncStorage.setItem(
        SaveSystem.SAVE_LIST_KEY,
        JSON.stringify(updatedList)
      );
    } catch (error) {
      console.error('Erro ao deletar save:', error);
      throw new Error('Falha ao deletar o save');
    }
  }

  /**
   * Salva automaticamente (autosave)
   */
  public async autoSave(
    personagem: Personagem,
    dungeon: Dungeon | null,
    gameState: any,
    statistics: any
  ): Promise<void> {
    try {
      const autoSaveId = 'autosave';
      const timestamp = Date.now();
      
      const gameSave: GameSave = {
        id: autoSaveId,
        name: `AutoSave - ${personagem.nome}`,
        timestamp,
        version: SaveSystem.CURRENT_VERSION,
        personagem: this.serializePersonagem(personagem),
        dungeon: dungeon ? this.serializeDungeon(dungeon) : null,
        gameState: {
          currentScreen: gameState.currentScreen || 'AVENTURA',
          location: gameState.location || 'Cidade',
          questProgress: gameState.questProgress || {},
          inventory: gameState.inventory || [],
          experience: personagem.experiencia,
          level: personagem.nivel,
          gold: personagem.dinheiro
        },
        statistics
      };

      await AsyncStorage.setItem(
        SaveSystem.SAVE_PREFIX + autoSaveId,
        JSON.stringify(gameSave)
      );
    } catch (error) {
      console.error('Erro no autosave:', error);
      // Não lança erro para não interromper o jogo
    }
  }

  /**
   * Carrega o autosave
   */
  public async loadAutoSave(): Promise<GameSave | null> {
    try {
      const autoSaveData = await AsyncStorage.getItem(SaveSystem.SAVE_PREFIX + 'autosave');
      
      if (!autoSaveData) {
        return null;
      }

      return JSON.parse(autoSaveData);
    } catch (error) {
      console.error('Erro ao carregar autosave:', error);
      return null;
    }
  }

  /**
   * Verifica se existe autosave
   */
  public async hasAutoSave(): Promise<boolean> {
    try {
      const autoSaveData = await AsyncStorage.getItem(SaveSystem.SAVE_PREFIX + 'autosave');
      return autoSaveData !== null;
    } catch (error) {
      return false;
    }
  }

  /**
   * Exporta um save para compartilhamento
   */
  public async exportSave(saveId: string): Promise<string> {
    try {
      const gameSave = await this.loadGame(saveId);
      return JSON.stringify(gameSave);
    } catch (error) {
      console.error('Erro ao exportar save:', error);
      throw new Error('Falha ao exportar o save');
    }
  }

  /**
   * Importa um save de dados externos
   */
  public async importSave(saveData: string, saveName?: string): Promise<string> {
    try {
      const gameSave: GameSave = JSON.parse(saveData);
      
      // Validar estrutura do save
      if (!this.validateSaveStructure(gameSave)) {
        throw new Error('Estrutura do save inválida');
      }

      // Gerar novo ID para evitar conflitos
      const newSaveId = `imported_${Date.now()}`;
      gameSave.id = newSaveId;
      gameSave.timestamp = Date.now();
      
      if (saveName) {
        gameSave.name = saveName;
      }

      // Salvar
      await AsyncStorage.setItem(
        SaveSystem.SAVE_PREFIX + newSaveId,
        JSON.stringify(gameSave)
      );

      // Atualizar lista
      await this.updateSaveList(newSaveId, gameSave);

      return newSaveId;
    } catch (error) {
      console.error('Erro ao importar save:', error);
      throw new Error('Falha ao importar o save');
    }
  }

  /**
   * Limpa todos os saves (reset completo)
   */
  public async clearAllSaves(): Promise<void> {
    try {
      const saveList = await this.getSaveList();
      
      // Remover todos os arquivos de save
      for (const save of saveList) {
        await AsyncStorage.removeItem(SaveSystem.SAVE_PREFIX + save.id);
      }
      
      // Remover autosave
      await AsyncStorage.removeItem(SaveSystem.SAVE_PREFIX + 'autosave');
      
      // Limpar lista
      await AsyncStorage.removeItem(SaveSystem.SAVE_LIST_KEY);
    } catch (error) {
      console.error('Erro ao limpar saves:', error);
      throw new Error('Falha ao limpar os saves');
    }
  }

  private async updateSaveList(saveId: string, gameSave: GameSave): Promise<void> {
    const saveList = await this.getSaveList();
    
    // Remover save existente com mesmo ID (se houver)
    const filteredList = saveList.filter(save => save.id !== saveId);
    
    // Adicionar novo save
    const newSaveSlot: SaveSlot = {
      id: saveId,
      name: gameSave.name,
      timestamp: gameSave.timestamp,
      preview: {
        characterName: gameSave.personagem.nome,
        level: gameSave.personagem.nivel,
        location: gameSave.gameState.location,
        playtime: gameSave.statistics.playtime
      }
    };
    
    filteredList.unshift(newSaveSlot);
    
    // Manter apenas os últimos saves (limite)
    const limitedList = filteredList.slice(0, SaveSystem.MAX_SAVES);
    
    await AsyncStorage.setItem(
      SaveSystem.SAVE_LIST_KEY,
      JSON.stringify(limitedList)
    );
  }

  private serializePersonagem(personagem: Personagem): any {
    return {
      nome: personagem.nome,
      raca: {
        nome: personagem.raca.getNome(),
        // Adicionar outros dados necessários da raça
      },
      classe: {
        nome: personagem.classe.getNome(),
        // Adicionar outros dados necessários da classe
      },
      nivel: personagem.nivel,
      atributos: {
        forca: personagem.atributos.forca,
        destreza: personagem.atributos.destreza,
        constituicao: personagem.atributos.constituicao,
        inteligencia: personagem.atributos.inteligencia,
        sabedoria: personagem.atributos.sabedoria,
        carisma: personagem.atributos.carisma
      },
      pontosDeVida: personagem.pontosDeVida,
      pontosDeVidaMaximos: personagem.pontosDeVidaMaximos,
      experiencia: personagem.experiencia,
      alinhamento: personagem.alinhamento,
      inventario: personagem.inventario,
      dinheiro: personagem.dinheiro,
      magiasMemorizadas: personagem.magiasMemorizadas,
      talentosLadrao: personagem.talentosLadrao,
      armasComMaestria: personagem.armasComMaestria,
      equipamentos: personagem.equipamentos
    };
  }

  private serializeDungeon(dungeon: Dungeon): any {
    return {
      id: dungeon.id,
      name: dungeon.name,
      level: dungeon.level,
      size: dungeon.size,
      rooms: dungeon.rooms,
      currentRoomId: dungeon.currentRoomId,
      entranceId: dungeon.entranceId,
      exitId: dungeon.exitId,
      bossRoomId: dungeon.bossRoomId,
      completed: dungeon.completed,
      explorationPercentage: dungeon.explorationPercentage
    };
  }

  private validateSaveStructure(gameSave: any): boolean {
    return (
      gameSave &&
      typeof gameSave.id === 'string' &&
      typeof gameSave.name === 'string' &&
      typeof gameSave.timestamp === 'number' &&
      typeof gameSave.version === 'string' &&
      gameSave.personagem &&
      gameSave.gameState &&
      gameSave.statistics
    );
  }

  /**
   * Obtém estatísticas de uso do sistema de save
   */
  public async getSaveStatistics(): Promise<{
    totalSaves: number;
    oldestSave: number;
    newestSave: number;
    totalSize: number;
  }> {
    try {
      const saveList = await this.getSaveList();
      
      if (saveList.length === 0) {
        return {
          totalSaves: 0,
          oldestSave: 0,
          newestSave: 0,
          totalSize: 0
        };
      }

      const timestamps = saveList.map(save => save.timestamp);
      
      return {
        totalSaves: saveList.length,
        oldestSave: Math.min(...timestamps),
        newestSave: Math.max(...timestamps),
        totalSize: 0 // Poderia calcular tamanho real se necessário
      };
    } catch (error) {
      console.error('Erro ao obter estatísticas:', error);
      return {
        totalSaves: 0,
        oldestSave: 0,
        newestSave: 0,
        totalSize: 0
      };
    }
  }
}

export default SaveSystem;
