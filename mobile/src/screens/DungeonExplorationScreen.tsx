import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
  Alert,
  Dimensions,
  Modal,
} from 'react-native';
import { EstadoJogo } from '../types/GameTypes';
import { useGame } from '../contexts/GameContext';
import { DungeonGenerator, DungeonRoom, RoomType, DungeonMap } from '../game/dungeon/DungeonGenerator';
import { DungeonMapComponent } from '../components/DungeonMap';
import { SaveSystem } from '../utils/SaveSystem';
import Sprite, { SpriteType, SpriteUtils } from '../components/SpriteSystem';

interface DungeonExplorationScreenProps {
  onNavigate: (screen: EstadoJogo) => void;
}

interface PlayerPosition {
  x: number;
  y: number;
}

interface ExplorationState {
  currentDungeon: DungeonMap | null;
  playerPosition: PlayerPosition;
  visitedRooms: Set<string>;
  currentRoom: DungeonRoom | null;
  dungeonLevel: number;
  isInCombat: boolean;
}

const { width, height } = Dimensions.get('window');

export default function DungeonExplorationScreen({ onNavigate }: DungeonExplorationScreenProps) {
  const { state, updatePersonagem } = useGame();
  
  // Estados da exploração
  const [explorationState, setExplorationState] = useState<ExplorationState>({
    currentDungeon: null,
    playerPosition: { x: 0, y: 0 },
    visitedRooms: new Set(),
    currentRoom: null,
    dungeonLevel: 1,
    isInCombat: false
  });
  
  // Estados da interface
  const [showMap, setShowMap] = useState(false);
  const [showRoomDetails, setShowRoomDetails] = useState(false);
  const [actionLog, setActionLog] = useState<string[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  
  // Sistema de geração
  const [dungeonGenerator] = useState(new DungeonGenerator());
  const [saveSystem] = useState(new SaveSystem());

  useEffect(() => {
    initializeDungeon();
  }, []);

  const initializeDungeon = async () => {
    setIsLoading(true);
    
    try {
      // Tentar carregar dungeon salva
      const savedDungeon = await saveSystem.loadDungeonProgress();
      
      if (savedDungeon) {
        setExplorationState(savedDungeon);
        addToLog('Progresso da dungeon carregado.');
      } else {
        // Gerar nova dungeon
        generateNewDungeon();
      }
    } catch (error) {
      console.error('Erro ao carregar dungeon:', error);
      generateNewDungeon();
    } finally {
      setIsLoading(false);
    }
  };

  const generateNewDungeon = () => {
    const newDungeon = dungeonGenerator.generateDungeon(
      explorationState.dungeonLevel,
      { width: 10, height: 10 }
    );
    
    const entranceRoom = newDungeon.rooms.find(room => room.type === RoomType.ENTRANCE);
    const startPosition = entranceRoom ? { x: entranceRoom.x, y: entranceRoom.y } : { x: 0, y: 0 };
    
    const newState: ExplorationState = {
      currentDungeon: newDungeon,
      playerPosition: startPosition,
      visitedRooms: new Set([getRoomKey(startPosition.x, startPosition.y)]),
      currentRoom: entranceRoom || null,
      dungeonLevel: explorationState.dungeonLevel,
      isInCombat: false
    };
    
    setExplorationState(newState);
    addToLog(`Nova dungeon gerada - Nível ${explorationState.dungeonLevel}`);
    addToLog('Você entra na dungeon...');
    
    // Salvar progresso
    saveSystem.saveDungeonProgress(newState);
  };

  const getRoomKey = (x: number, y: number): string => {
    return `${x},${y}`;
  };

  const getCurrentRoom = (): DungeonRoom | null => {
    if (!explorationState.currentDungeon) return null;
    
    return explorationState.currentDungeon.rooms.find(
      room => room.x === explorationState.playerPosition.x && 
              room.y === explorationState.playerPosition.y
    ) || null;
  };

  const canMoveTo = (direction: 'north' | 'south' | 'east' | 'west'): boolean => {
    const currentRoom = getCurrentRoom();
    if (!currentRoom) return false;
    
    return currentRoom.connections[direction] || false;
  };

  const movePlayer = (direction: 'north' | 'south' | 'east' | 'west') => {
    if (!canMoveTo(direction) || explorationState.isInCombat) return;
    
    const { x, y } = explorationState.playerPosition;
    let newX = x;
    let newY = y;
    
    switch (direction) {
      case 'north':
        newY -= 1;
        break;
      case 'south':
        newY += 1;
        break;
      case 'east':
        newX += 1;
        break;
      case 'west':
        newX -= 1;
        break;
    }
    
    const newPosition = { x: newX, y: newY };
    const roomKey = getRoomKey(newX, newY);
    const newRoom = explorationState.currentDungeon?.rooms.find(
      room => room.x === newX && room.y === newY
    );
    
    if (!newRoom) return;
    
    // Atualizar estado
    const newVisitedRooms = new Set(explorationState.visitedRooms);
    const isFirstVisit = !newVisitedRooms.has(roomKey);
    newVisitedRooms.add(roomKey);
    
    setExplorationState(prev => ({
      ...prev,
      playerPosition: newPosition,
      visitedRooms: newVisitedRooms,
      currentRoom: newRoom
    }));
    
    // Log da movimentação
    addToLog(`Você se move para ${getDirectionName(direction)}.`);
    
    if (isFirstVisit) {
      exploreRoom(newRoom);
    }
    
    // Salvar progresso
    saveSystem.saveDungeonProgress({
      ...explorationState,
      playerPosition: newPosition,
      visitedRooms: newVisitedRooms,
      currentRoom: newRoom
    });
  };

  const exploreRoom = (room: DungeonRoom) => {
    addToLog(`Você entra em ${getRoomDescription(room)}.`);
    
    // Processar eventos da sala
    switch (room.type) {
      case RoomType.MONSTER:
        if (!room.cleared) {
          startCombat(room);
        }
        break;
        
      case RoomType.TREASURE:
        if (!room.cleared) {
          findTreasure(room);
        }
        break;
        
      case RoomType.TRAP:
        if (!room.cleared) {
          triggerTrap(room);
        }
        break;
        
      case RoomType.BOSS:
        if (!room.cleared) {
          startBossFight(room);
        }
        break;
        
      case RoomType.EXIT:
        offerDungeonExit();
        break;
    }
  };

  const startCombat = (room: DungeonRoom) => {
    setExplorationState(prev => ({ ...prev, isInCombat: true }));
    
    const monsters = room.content?.monsters || [];
    const monsterNames = monsters.map(m => m.name).join(', ');
    
    addToLog(`⚔️ Combate iniciado contra: ${monsterNames}!`);
    
    Alert.alert(
      'Combate!',
      `Você encontrou ${monsterNames}. Prepare-se para a batalha!`,
      [
        {
          text: 'Lutar',
          onPress: () => {
            // Navegar para tela de combate
            onNavigate(EstadoJogo.COMBATE);
          }
        },
        {
          text: 'Tentar Fugir',
          onPress: () => attemptFlee()
        }
      ]
    );
  };

  const startBossFight = (room: DungeonRoom) => {
    setExplorationState(prev => ({ ...prev, isInCombat: true }));
    
    const boss = room.content?.monsters?.[0];
    const bossName = boss?.name || 'Chefe da Dungeon';
    
    addToLog(`👑 Você encontrou o chefe da dungeon: ${bossName}!`);
    
    Alert.alert(
      'Chefe da Dungeon!',
      `${bossName} bloqueia sua passagem. Esta será uma batalha épica!`,
      [
        {
          text: 'Enfrentar o Chefe',
          onPress: () => {
            onNavigate(EstadoJogo.COMBATE);
          }
        }
      ]
    );
  };

  const findTreasure = (room: DungeonRoom) => {
    const treasure = room.content?.treasure;
    if (!treasure) return;
    
    addToLog(`💰 Você encontrou um tesouro!`);
    
    let treasureDescription = '';
    let goldFound = 0;
    const itemsFound: string[] = [];
    
    if (treasure.gold > 0) {
      goldFound = treasure.gold;
      treasureDescription += `${goldFound} moedas de ouro`;
    }
    
    if (treasure.items && treasure.items.length > 0) {
      treasure.items.forEach(item => {
        itemsFound.push(item.name);
      });
      
      if (treasureDescription) treasureDescription += ' e ';
      treasureDescription += itemsFound.join(', ');
    }
    
    Alert.alert(
      'Tesouro Encontrado!',
      `Você encontrou: ${treasureDescription}`,
      [
        {
          text: 'Pegar Tudo',
          onPress: () => {
            // Adicionar ouro ao personagem
            if (state.personagem && goldFound > 0) {
              updatePersonagem({
                ...state.personagem,
                dinheiro: state.personagem.dinheiro + goldFound
              });
            }
            
            // Marcar sala como limpa
            room.cleared = true;
            addToLog(`Você coletou o tesouro.`);
          }
        }
      ]
    );
  };

  const triggerTrap = (room: DungeonRoom) => {
    const trap = room.content?.trap;
    if (!trap) return;
    
    addToLog(`⚠️ Você ativou uma armadilha: ${trap.name}!`);
    
    // Teste de destreza para evitar a armadilha
    const dexterityCheck = Math.floor(Math.random() * 20) + 1;
    const dexterityModifier = state.personagem ? 
      Math.floor((state.personagem.atributos.destreza - 10) / 2) : 0;
    const totalCheck = dexterityCheck + dexterityModifier;
    
    if (totalCheck >= trap.difficulty) {
      addToLog(`🎯 Você evitou a armadilha com sucesso! (${totalCheck} vs ${trap.difficulty})`);
      room.cleared = true;
    } else {
      const damage = Math.floor(Math.random() * trap.damage) + 1;
      addToLog(`💥 A armadilha te atingiu! Você sofreu ${damage} de dano.`);
      
      // Aplicar dano ao personagem
      if (state.personagem) {
        const newHp = Math.max(0, state.personagem.pontosDeVida - damage);
        updatePersonagem({
          ...state.personagem,
          pontosDeVida: newHp
        });
        
        if (newHp <= 0) {
          Alert.alert('Você Morreu!', 'A armadilha foi fatal...');
          onNavigate(EstadoJogo.MENU);
          return;
        }
      }
      
      room.cleared = true;
    }
  };

  const attemptFlee = () => {
    const fleeCheck = Math.floor(Math.random() * 20) + 1;
    
    if (fleeCheck >= 10) {
      addToLog('Você conseguiu fugir do combate!');
      setExplorationState(prev => ({ ...prev, isInCombat: false }));
    } else {
      addToLog('Você não conseguiu fugir!');
      // Inimigos atacam
      onNavigate(EstadoJogo.COMBATE);
    }
  };

  const offerDungeonExit = () => {
    Alert.alert(
      'Saída da Dungeon',
      'Você encontrou a saída da dungeon. Deseja sair ou continuar explorando?',
      [
        {
          text: 'Continuar Explorando',
          style: 'cancel'
        },
        {
          text: 'Sair da Dungeon',
          onPress: () => {
            addToLog('Você saiu da dungeon.');
            onNavigate(EstadoJogo.AVENTURA);
          }
        },
        {
          text: 'Próximo Nível',
          onPress: () => {
            setExplorationState(prev => ({ ...prev, dungeonLevel: prev.dungeonLevel + 1 }));
            generateNewDungeon();
          }
        }
      ]
    );
  };

  const addToLog = (message: string) => {
    setActionLog(prev => [...prev.slice(-9), message]); // Manter apenas as últimas 10 mensagens
  };

  const getDirectionName = (direction: string): string => {
    const names = {
      north: 'norte',
      south: 'sul',
      east: 'leste',
      west: 'oeste'
    };
    return names[direction as keyof typeof names] || direction;
  };

  const getRoomDescription = (room: DungeonRoom): string => {
    const descriptions = {
      [RoomType.ENTRANCE]: 'a entrada da dungeon',
      [RoomType.EMPTY]: 'uma sala vazia',
      [RoomType.MONSTER]: room.cleared ? 'uma sala onde você derrotou monstros' : 'uma sala com monstros',
      [RoomType.TREASURE]: room.cleared ? 'uma sala de tesouro vazia' : 'uma sala de tesouro',
      [RoomType.TRAP]: room.cleared ? 'uma sala com armadilha desarmada' : 'uma sala suspeita',
      [RoomType.BOSS]: room.cleared ? 'a sala do chefe derrotado' : 'a sala do chefe',
      [RoomType.EXIT]: 'a saída da dungeon'
    };
    
    return descriptions[room.type] || 'uma sala misteriosa';
  };

  const getRoomIcon = (room: DungeonRoom): string => {
    if (room.cleared) return '✅';
    
    const icons = {
      [RoomType.ENTRANCE]: '🚪',
      [RoomType.EMPTY]: '⬜',
      [RoomType.MONSTER]: '👹',
      [RoomType.TREASURE]: '💰',
      [RoomType.TRAP]: '⚠️',
      [RoomType.BOSS]: '👑',
      [RoomType.EXIT]: '🚪'
    };
    
    return icons[room.type] || '❓';
  };

  const currentRoom = getCurrentRoom();

  if (isLoading) {
    return (
      <View style={[styles.container, styles.centered]}>
        <Text style={styles.loadingText}>Gerando dungeon...</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.title}>Exploração da Dungeon</Text>
        <Text style={styles.levelText}>Nível {explorationState.dungeonLevel}</Text>
        <Text style={styles.positionText}>
          Posição: ({explorationState.playerPosition.x}, {explorationState.playerPosition.y})
        </Text>
      </View>

      {/* Informações da sala atual */}
      <View style={styles.roomInfo}>
        <View style={styles.roomHeader}>
          <Text style={styles.roomIcon}>
            {currentRoom ? getRoomIcon(currentRoom) : '❓'}
          </Text>
          <Text style={styles.roomName}>
            {currentRoom ? getRoomDescription(currentRoom) : 'Localização desconhecida'}
          </Text>
        </View>
        
        {currentRoom && (
          <TouchableOpacity
            style={styles.detailsButton}
            onPress={() => setShowRoomDetails(true)}
          >
            <Text style={styles.detailsButtonText}>Detalhes</Text>
          </TouchableOpacity>
        )}
      </View>

      {/* Controles de movimento */}
      <View style={styles.movementControls}>
        <View style={styles.movementRow}>
          <TouchableOpacity
            style={[
              styles.movementButton,
              !canMoveTo('north') && styles.disabledButton
            ]}
            onPress={() => movePlayer('north')}
            disabled={!canMoveTo('north') || explorationState.isInCombat}
          >
            <Text style={styles.movementButtonText}>⬆️ Norte</Text>
          </TouchableOpacity>
        </View>
        
        <View style={styles.movementRow}>
          <TouchableOpacity
            style={[
              styles.movementButton,
              !canMoveTo('west') && styles.disabledButton
            ]}
            onPress={() => movePlayer('west')}
            disabled={!canMoveTo('west') || explorationState.isInCombat}
          >
            <Text style={styles.movementButtonText}>⬅️ Oeste</Text>
          </TouchableOpacity>
          
          <TouchableOpacity
            style={[
              styles.movementButton,
              !canMoveTo('east') && styles.disabledButton
            ]}
            onPress={() => movePlayer('east')}
            disabled={!canMoveTo('east') || explorationState.isInCombat}
          >
            <Text style={styles.movementButtonText}>➡️ Leste</Text>
          </TouchableOpacity>
        </View>
        
        <View style={styles.movementRow}>
          <TouchableOpacity
            style={[
              styles.movementButton,
              !canMoveTo('south') && styles.disabledButton
            ]}
            onPress={() => movePlayer('south')}
            disabled={!canMoveTo('south') || explorationState.isInCombat}
          >
            <Text style={styles.movementButtonText}>⬇️ Sul</Text>
          </TouchableOpacity>
        </View>
      </View>

      {/* Log de ações */}
      <View style={styles.actionLog}>
        <Text style={styles.logTitle}>Log de Ações:</Text>
        <ScrollView style={styles.logScroll}>
          {actionLog.map((message, index) => (
            <Text key={index} style={styles.logMessage}>
              {message}
            </Text>
          ))}
        </ScrollView>
      </View>

      {/* Botões de ação */}
      <View style={styles.actionButtons}>
        <TouchableOpacity
          style={styles.actionButton}
          onPress={() => setShowMap(true)}
        >
          <Text style={styles.actionButtonText}>🗺️ Mapa</Text>
        </TouchableOpacity>
        
        <TouchableOpacity
          style={styles.actionButton}
          onPress={() => onNavigate(EstadoJogo.INVENTARIO)}
        >
          <Text style={styles.actionButtonText}>🎒 Inventário</Text>
        </TouchableOpacity>
        
        <TouchableOpacity
          style={styles.actionButton}
          onPress={() => {
            Alert.alert(
              'Sair da Dungeon',
              'Tem certeza que deseja sair? Seu progresso será salvo.',
              [
                { text: 'Cancelar', style: 'cancel' },
                { 
                  text: 'Sair', 
                  onPress: () => {
                    saveSystem.saveDungeonProgress(explorationState);
                    onNavigate(EstadoJogo.AVENTURA);
                  }
                }
              ]
            );
          }}
        >
          <Text style={styles.actionButtonText}>🚪 Sair</Text>
        </TouchableOpacity>
      </View>

      {/* Modal do mapa */}
      <Modal
        visible={showMap}
        transparent={true}
        animationType="fade"
        onRequestClose={() => setShowMap(false)}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.mapModalContent}>
            <Text style={styles.mapTitle}>Mapa da Dungeon</Text>
            
            {explorationState.currentDungeon && (
              <DungeonMapComponent
                dungeon={explorationState.currentDungeon}
                playerPosition={explorationState.playerPosition}
                visitedRooms={explorationState.visitedRooms}
                size={{ width: width * 0.8, height: height * 0.6 }}
              />
            )}
            
            <TouchableOpacity
              style={styles.closeButton}
              onPress={() => setShowMap(false)}
            >
              <Text style={styles.closeButtonText}>Fechar</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>

      {/* Modal de detalhes da sala */}
      <Modal
        visible={showRoomDetails}
        transparent={true}
        animationType="fade"
        onRequestClose={() => setShowRoomDetails(false)}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.detailsModalContent}>
            {currentRoom && (
              <>
                <Text style={styles.detailsTitle}>
                  {getRoomIcon(currentRoom)} {getRoomDescription(currentRoom)}
                </Text>
                
                <Text style={styles.detailsText}>
                  Tipo: {currentRoom.type}
                </Text>
                
                <Text style={styles.detailsText}>
                  Status: {currentRoom.cleared ? 'Explorada' : 'Não explorada'}
                </Text>
                
                <Text style={styles.detailsText}>
                  Conexões:
                </Text>
                
                {Object.entries(currentRoom.connections).map(([direction, connected]) => (
                  <Text key={direction} style={styles.connectionText}>
                    • {getDirectionName(direction)}: {connected ? 'Sim' : 'Não'}
                  </Text>
                ))}
                
                {currentRoom.content && (
                  <View style={styles.contentSection}>
                    <Text style={styles.contentTitle}>Conteúdo:</Text>
                    
                    {currentRoom.content.monsters && currentRoom.content.monsters.length > 0 && (
                      <Text style={styles.contentText}>
                        Monstros: {currentRoom.content.monsters.map(m => m.name).join(', ')}
                      </Text>
                    )}
                    
                    {currentRoom.content.treasure && (
                      <Text style={styles.contentText}>
                        Tesouro: {currentRoom.content.treasure.gold} PO
                      </Text>
                    )}
                    
                    {currentRoom.content.trap && (
                      <Text style={styles.contentText}>
                        Armadilha: {currentRoom.content.trap.name}
                      </Text>
                    )}
                  </View>
                )}
              </>
            )}
            
            <TouchableOpacity
              style={styles.closeButton}
              onPress={() => setShowRoomDetails(false)}
            >
              <Text style={styles.closeButtonText}>Fechar</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#1a1a1a',
  },
  centered: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  loadingText: {
    color: '#FFD700',
    fontSize: 18,
    fontWeight: 'bold',
  },
  header: {
    backgroundColor: 'rgba(139, 69, 19, 0.9)',
    padding: 15,
    borderBottomWidth: 2,
    borderBottomColor: '#D2691E',
  },
  title: {
    color: '#FFD700',
    fontSize: 20,
    fontWeight: 'bold',
    textAlign: 'center',
  },
  levelText: {
    color: '#FFF',
    fontSize: 14,
    textAlign: 'center',
    marginTop: 5,
  },
  positionText: {
    color: '#CCC',
    fontSize: 12,
    textAlign: 'center',
    marginTop: 2,
  },
  roomInfo: {
    backgroundColor: 'rgba(255, 255, 255, 0.1)',
    padding: 15,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  roomHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    flex: 1,
  },
  roomIcon: {
    fontSize: 24,
    marginRight: 10,
  },
  roomName: {
    color: '#FFF',
    fontSize: 16,
    fontWeight: 'bold',
    flex: 1,
  },
  detailsButton: {
    backgroundColor: '#8B4513',
    borderRadius: 6,
    paddingHorizontal: 12,
    paddingVertical: 6,
  },
  detailsButtonText: {
    color: '#FFD700',
    fontSize: 12,
    fontWeight: 'bold',
  },
  movementControls: {
    padding: 20,
    alignItems: 'center',
  },
  movementRow: {
    flexDirection: 'row',
    justifyContent: 'center',
    marginVertical: 5,
  },
  movementButton: {
    backgroundColor: '#8B4513',
    borderWidth: 2,
    borderColor: '#D2691E',
    borderRadius: 8,
    paddingVertical: 12,
    paddingHorizontal: 20,
    marginHorizontal: 10,
    minWidth: 100,
    alignItems: 'center',
  },
  disabledButton: {
    backgroundColor: '#444',
    borderColor: '#666',
  },
  movementButtonText: {
    color: '#FFD700',
    fontSize: 14,
    fontWeight: 'bold',
  },
  actionLog: {
    flex: 1,
    backgroundColor: 'rgba(255, 255, 255, 0.05)',
    margin: 15,
    borderRadius: 8,
    padding: 10,
  },
  logTitle: {
    color: '#FFD700',
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  logScroll: {
    flex: 1,
  },
  logMessage: {
    color: '#FFF',
    fontSize: 12,
    marginBottom: 5,
    lineHeight: 16,
  },
  actionButtons: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    padding: 15,
    backgroundColor: 'rgba(139, 69, 19, 0.5)',
  },
  actionButton: {
    backgroundColor: '#8B4513',
    borderWidth: 2,
    borderColor: '#D2691E',
    borderRadius: 8,
    paddingVertical: 10,
    paddingHorizontal: 15,
    alignItems: 'center',
    flex: 1,
    marginHorizontal: 5,
  },
  actionButtonText: {
    color: '#FFD700',
    fontSize: 12,
    fontWeight: 'bold',
  },
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.8)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  mapModalContent: {
    backgroundColor: '#2a2a2a',
    borderRadius: 12,
    padding: 20,
    margin: 20,
    maxWidth: width * 0.95,
    maxHeight: height * 0.9,
    borderWidth: 2,
    borderColor: '#D2691E',
  },
  mapTitle: {
    color: '#FFD700',
    fontSize: 18,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 15,
  },
  detailsModalContent: {
    backgroundColor: '#2a2a2a',
    borderRadius: 12,
    padding: 20,
    margin: 20,
    maxWidth: width * 0.9,
    borderWidth: 2,
    borderColor: '#D2691E',
  },
  detailsTitle: {
    color: '#FFD700',
    fontSize: 18,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 15,
  },
  detailsText: {
    color: '#FFF',
    fontSize: 14,
    marginBottom: 8,
  },
  connectionText: {
    color: '#CCC',
    fontSize: 12,
    marginLeft: 10,
    marginBottom: 4,
  },
  contentSection: {
    marginTop: 15,
    paddingTop: 15,
    borderTopWidth: 1,
    borderTopColor: '#444',
  },
  contentTitle: {
    color: '#FFD700',
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  contentText: {
    color: '#CCC',
    fontSize: 12,
    marginBottom: 5,
  },
  closeButton: {
    backgroundColor: '#8B4513',
    borderWidth: 2,
    borderColor: '#D2691E',
    borderRadius: 8,
    paddingVertical: 12,
    alignItems: 'center',
    marginTop: 15,
  },
  closeButtonText: {
    color: '#FFD700',
    fontSize: 16,
    fontWeight: 'bold',
  },
});
