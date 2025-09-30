import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
  Dimensions,
  Modal,
} from 'react-native';
import { Dungeon, Room, RoomType, Direction } from '../game/dungeon/DungeonGenerator';

interface DungeonMapProps {
  dungeon: Dungeon;
  onRoomSelect?: (roomId: string) => void;
  showFullMap?: boolean;
  interactive?: boolean;
}

const { width, height } = Dimensions.get('window');
const CELL_SIZE = 40;
const WALL_THICKNESS = 2;

export default function DungeonMap({ 
  dungeon, 
  onRoomSelect, 
  showFullMap = false,
  interactive = false 
}: DungeonMapProps) {
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [selectedRoom, setSelectedRoom] = useState<Room | null>(null);

  const getRoomColor = (room: Room): string => {
    if (!room.visited && !showFullMap) return '#333'; // Não explorado
    
    if (room.id === dungeon.currentRoomId) return '#FFD700'; // Sala atual
    
    switch (room.type) {
      case RoomType.ENTRANCE:
        return '#4CAF50'; // Verde
      case RoomType.EXIT:
        return '#2196F3'; // Azul
      case RoomType.BOSS:
        return room.boss?.defeated ? '#9C27B0' : '#F44336'; // Roxo se derrotado, vermelho se não
      case RoomType.MONSTER:
        return room.cleared ? '#795548' : '#FF5722'; // Marrom se limpo, laranja se não
      case RoomType.TREASURE:
        return room.cleared ? '#607D8B' : '#FFC107'; // Cinza se coletado, amarelo se não
      case RoomType.TRAP:
        return room.trap?.disarmed ? '#8BC34A' : '#E91E63'; // Verde claro se desarmada, rosa se não
      case RoomType.SHOP:
        return '#9E9E9E'; // Cinza
      case RoomType.SHRINE:
        return '#00BCD4'; // Ciano
      default:
        return room.visited ? '#666' : '#333'; // Cinza escuro se visitado, preto se não
    }
  };

  const getRoomIcon = (room: Room): string => {
    if (!room.visited && !showFullMap) return '?';
    
    switch (room.type) {
      case RoomType.ENTRANCE:
        return '🚪';
      case RoomType.EXIT:
        return '🚪';
      case RoomType.BOSS:
        return room.boss?.defeated ? '👑' : '💀';
      case RoomType.MONSTER:
        return room.cleared ? '💀' : '👹';
      case RoomType.TREASURE:
        return room.cleared ? '📦' : '💰';
      case RoomType.TRAP:
        return room.trap?.disarmed ? '🔧' : '⚠️';
      case RoomType.SHOP:
        return '🏪';
      case RoomType.SHRINE:
        return '⛪';
      default:
        return room.id === dungeon.currentRoomId ? '👤' : '';
    }
  };

  const handleRoomPress = (room: Room) => {
    if (!interactive) return;
    
    setSelectedRoom(room);
    setIsModalVisible(true);
    
    if (onRoomSelect) {
      onRoomSelect(room.id);
    }
  };

  const renderRoom = (room: Room) => {
    const roomStyle = {
      position: 'absolute' as const,
      left: room.x * (CELL_SIZE + WALL_THICKNESS),
      top: room.y * (CELL_SIZE + WALL_THICKNESS),
      width: CELL_SIZE,
      height: CELL_SIZE,
      backgroundColor: getRoomColor(room),
      borderWidth: 1,
      borderColor: room.id === dungeon.currentRoomId ? '#FFD700' : '#888',
      borderRadius: 4,
      justifyContent: 'center' as const,
      alignItems: 'center' as const,
    };

    return (
      <TouchableOpacity
        key={room.id}
        style={roomStyle}
        onPress={() => handleRoomPress(room)}
        disabled={!interactive}
      >
        <Text style={styles.roomIcon}>
          {getRoomIcon(room)}
        </Text>
        {room.id === dungeon.currentRoomId && (
          <View style={styles.currentRoomIndicator} />
        )}
      </TouchableOpacity>
    );
  };

  const renderConnections = () => {
    const connections: JSX.Element[] = [];
    
    Object.values(dungeon.rooms).forEach(room => {
      if (!room.visited && !showFullMap) return;
      
      Object.entries(room.connections).forEach(([direction, targetRoomId]) => {
        if (!targetRoomId) return;
        
        const targetRoom = dungeon.rooms[targetRoomId];
        if (!targetRoom || (!targetRoom.visited && !showFullMap)) return;
        
        const connectionStyle = this.getConnectionStyle(room, direction as Direction);
        
        connections.push(
          <View
            key={`${room.id}-${direction}`}
            style={[styles.connection, connectionStyle]}
          />
        );
      });
    });
    
    return connections;
  };

  const getConnectionStyle = (room: Room, direction: Direction) => {
    const baseX = room.x * (CELL_SIZE + WALL_THICKNESS);
    const baseY = room.y * (CELL_SIZE + WALL_THICKNESS);
    
    switch (direction) {
      case Direction.NORTH:
        return {
          left: baseX + CELL_SIZE / 2 - 1,
          top: baseY - WALL_THICKNESS,
          width: 2,
          height: WALL_THICKNESS,
        };
      case Direction.SOUTH:
        return {
          left: baseX + CELL_SIZE / 2 - 1,
          top: baseY + CELL_SIZE,
          width: 2,
          height: WALL_THICKNESS,
        };
      case Direction.EAST:
        return {
          left: baseX + CELL_SIZE,
          top: baseY + CELL_SIZE / 2 - 1,
          width: WALL_THICKNESS,
          height: 2,
        };
      case Direction.WEST:
        return {
          left: baseX - WALL_THICKNESS,
          top: baseY + CELL_SIZE / 2 - 1,
          width: WALL_THICKNESS,
          height: 2,
        };
      default:
        return {};
    }
  };

  const mapWidth = dungeon.size.width * (CELL_SIZE + WALL_THICKNESS);
  const mapHeight = dungeon.size.height * (CELL_SIZE + WALL_THICKNESS);

  const renderRoomDetails = () => {
    if (!selectedRoom) return null;

    return (
      <Modal
        visible={isModalVisible}
        transparent={true}
        animationType="fade"
        onRequestClose={() => setIsModalVisible(false)}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.modalContent}>
            <Text style={styles.modalTitle}>
              {selectedRoom.type.charAt(0).toUpperCase() + selectedRoom.type.slice(1)}
            </Text>
            
            <Text style={styles.modalDescription}>
              {selectedRoom.description}
            </Text>
            
            <View style={styles.modalInfo}>
              <Text style={styles.infoText}>
                Posição: ({selectedRoom.x}, {selectedRoom.y})
              </Text>
              <Text style={styles.infoText}>
                Visitado: {selectedRoom.visited ? 'Sim' : 'Não'}
              </Text>
              <Text style={styles.infoText}>
                Limpo: {selectedRoom.cleared ? 'Sim' : 'Não'}
              </Text>
            </View>

            {selectedRoom.monsters && selectedRoom.monsters.length > 0 && (
              <View style={styles.modalSection}>
                <Text style={styles.sectionTitle}>Monstros:</Text>
                {selectedRoom.monsters.map((monster, index) => (
                  <Text key={index} style={styles.listItem}>• {monster}</Text>
                ))}
              </View>
            )}

            {selectedRoom.treasure && selectedRoom.treasure.length > 0 && (
              <View style={styles.modalSection}>
                <Text style={styles.sectionTitle}>Tesouro:</Text>
                {selectedRoom.treasure.map((item, index) => (
                  <Text key={index} style={styles.listItem}>• {item}</Text>
                ))}
              </View>
            )}

            {selectedRoom.trap && (
              <View style={styles.modalSection}>
                <Text style={styles.sectionTitle}>Armadilha:</Text>
                <Text style={styles.listItem}>• {selectedRoom.trap.type}</Text>
                <Text style={styles.listItem}>
                  • Detectada: {selectedRoom.trap.detected ? 'Sim' : 'Não'}
                </Text>
                <Text style={styles.listItem}>
                  • Desarmada: {selectedRoom.trap.disarmed ? 'Sim' : 'Não'}
                </Text>
              </View>
            )}

            {selectedRoom.boss && (
              <View style={styles.modalSection}>
                <Text style={styles.sectionTitle}>Chefe:</Text>
                <Text style={styles.listItem}>• {selectedRoom.boss.name}</Text>
                <Text style={styles.listItem}>• Nível: {selectedRoom.boss.level}</Text>
                <Text style={styles.listItem}>
                  • Derrotado: {selectedRoom.boss.defeated ? 'Sim' : 'Não'}
                </Text>
              </View>
            )}

            <TouchableOpacity
              style={styles.closeButton}
              onPress={() => setIsModalVisible(false)}
            >
              <Text style={styles.closeButtonText}>Fechar</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
    );
  };

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.dungeonName}>{dungeon.name}</Text>
        <Text style={styles.explorationText}>
          Exploração: {dungeon.explorationPercentage}%
        </Text>
      </View>

      <ScrollView
        style={styles.mapContainer}
        contentContainerStyle={{
          width: mapWidth + 40,
          height: mapHeight + 40,
          padding: 20,
        }}
        showsHorizontalScrollIndicator={true}
        showsVerticalScrollIndicator={true}
        bounces={false}
      >
        <View style={{ width: mapWidth, height: mapHeight }}>
          {/* Renderizar conexões primeiro (atrás das salas) */}
          {renderConnections()}
          
          {/* Renderizar salas */}
          {Object.values(dungeon.rooms).map(room => renderRoom(room))}
        </View>
      </ScrollView>

      <View style={styles.legend}>
        <View style={styles.legendRow}>
          <View style={[styles.legendItem, { backgroundColor: '#FFD700' }]} />
          <Text style={styles.legendText}>Atual</Text>
          
          <View style={[styles.legendItem, { backgroundColor: '#4CAF50' }]} />
          <Text style={styles.legendText}>Entrada</Text>
          
          <View style={[styles.legendItem, { backgroundColor: '#2196F3' }]} />
          <Text style={styles.legendText}>Saída</Text>
        </View>
        
        <View style={styles.legendRow}>
          <View style={[styles.legendItem, { backgroundColor: '#F44336' }]} />
          <Text style={styles.legendText}>Chefe</Text>
          
          <View style={[styles.legendItem, { backgroundColor: '#FF5722' }]} />
          <Text style={styles.legendText}>Monstro</Text>
          
          <View style={[styles.legendItem, { backgroundColor: '#FFC107' }]} />
          <Text style={styles.legendText}>Tesouro</Text>
        </View>
      </View>

      {renderRoomDetails()}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#1a1a1a',
  },
  header: {
    backgroundColor: 'rgba(139, 69, 19, 0.9)',
    padding: 15,
    borderBottomWidth: 2,
    borderBottomColor: '#D2691E',
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  dungeonName: {
    color: '#FFD700',
    fontSize: 18,
    fontWeight: 'bold',
  },
  explorationText: {
    color: '#FFF',
    fontSize: 14,
  },
  mapContainer: {
    flex: 1,
    backgroundColor: '#000',
  },
  roomIcon: {
    fontSize: 16,
  },
  currentRoomIndicator: {
    position: 'absolute',
    top: -2,
    right: -2,
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: '#FFD700',
  },
  connection: {
    position: 'absolute',
    backgroundColor: '#888',
  },
  legend: {
    backgroundColor: 'rgba(139, 69, 19, 0.9)',
    padding: 10,
    borderTopWidth: 2,
    borderTopColor: '#D2691E',
  },
  legendRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginVertical: 2,
  },
  legendItem: {
    width: 16,
    height: 16,
    borderRadius: 2,
    marginRight: 8,
    marginLeft: 16,
  },
  legendText: {
    color: '#FFF',
    fontSize: 12,
    marginRight: 16,
  },
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.8)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  modalContent: {
    backgroundColor: '#2a2a2a',
    borderRadius: 12,
    padding: 20,
    margin: 20,
    maxWidth: width * 0.9,
    maxHeight: height * 0.8,
    borderWidth: 2,
    borderColor: '#D2691E',
  },
  modalTitle: {
    color: '#FFD700',
    fontSize: 20,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 10,
  },
  modalDescription: {
    color: '#FFF',
    fontSize: 14,
    textAlign: 'center',
    marginBottom: 15,
    lineHeight: 20,
  },
  modalInfo: {
    marginBottom: 15,
  },
  infoText: {
    color: '#CCC',
    fontSize: 12,
    marginBottom: 4,
  },
  modalSection: {
    marginBottom: 15,
  },
  sectionTitle: {
    color: '#FFD700',
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 8,
  },
  listItem: {
    color: '#FFF',
    fontSize: 14,
    marginBottom: 4,
  },
  closeButton: {
    backgroundColor: '#8B4513',
    borderWidth: 2,
    borderColor: '#D2691E',
    borderRadius: 8,
    paddingVertical: 12,
    alignItems: 'center',
    marginTop: 10,
  },
  closeButtonText: {
    color: '#FFD700',
    fontSize: 16,
    fontWeight: 'bold',
  },
});
