import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
  Image,
  Alert,
  Dimensions,
  Modal,
  TextInput,
} from 'react-native';
import { EstadoJogo } from '../types/GameTypes';
import { useGame } from '../contexts/GameContext';
import { InventorySystem, Item, ItemType, ItemRarity, InventorySlot } from '../game/inventory/InventorySystem';

interface EnhancedInventoryScreenProps {
  onNavigate: (screen: EstadoJogo) => void;
}

enum InventoryTab {
  ALL = 'all',
  WEAPONS = 'weapons',
  ARMOR = 'armor',
  POTIONS = 'potions',
  MISC = 'misc'
}

const { width } = Dimensions.get('window');
const SLOT_SIZE = 60;
const SLOTS_PER_ROW = Math.floor((width - 40) / (SLOT_SIZE + 8));

export default function EnhancedInventoryScreen({ onNavigate }: EnhancedInventoryScreenProps) {
  const { state, updatePersonagem } = useGame();
  const [inventory] = useState(new InventorySystem(30));
  const [activeTab, setActiveTab] = useState<InventoryTab>(InventoryTab.ALL);
  const [selectedItem, setSelectedItem] = useState<{ item: Item; slot: InventorySlot } | null>(null);
  const [showItemModal, setShowItemModal] = useState(false);
  const [showSortModal, setShowSortModal] = useState(false);
  const [searchText, setSearchText] = useState('');
  const [attributePoints, setAttributePoints] = useState(0);

  useEffect(() => {
    // Inicializar inventário com alguns itens de exemplo
    initializeInventory();
  }, []);

  const initializeInventory = () => {
    // Adicionar alguns itens de exemplo
    const exampleItems = [
      { id: 'sword_short', quantity: 1 },
      { id: 'leather_armor', quantity: 1 },
      { id: 'shield_small', quantity: 1 },
      { id: 'potion_healing', quantity: 3 },
      { id: 'potion_strength', quantity: 1 },
      { id: 'scroll_magic_missile', quantity: 2 },
      { id: 'gem_ruby', quantity: 1 },
      { id: 'gold_coins', quantity: 150 }
    ];

    // Simular adição de itens (em um jogo real, isso viria do estado do jogo)
    // inventory.addItem(ItemFactory.createItem(item.id)!, item.quantity);
    
    // Simular pontos de atributo disponíveis
    setAttributePoints(5);
  };

  const getFilteredItems = (): InventorySlot[] => {
    let items = inventory.getItems();

    // Filtrar por aba
    if (activeTab !== InventoryTab.ALL) {
      const typeMap = {
        [InventoryTab.WEAPONS]: ItemType.WEAPON,
        [InventoryTab.ARMOR]: [ItemType.ARMOR, ItemType.SHIELD],
        [InventoryTab.POTIONS]: [ItemType.POTION, ItemType.SCROLL],
        [InventoryTab.MISC]: [ItemType.TREASURE, ItemType.TOOL, ItemType.MISC]
      };

      const allowedTypes = typeMap[activeTab];
      items = items.filter(slot => {
        if (!slot.item) return false;
        return Array.isArray(allowedTypes) 
          ? allowedTypes.includes(slot.item.type)
          : slot.item.type === allowedTypes;
      });
    }

    // Filtrar por busca
    if (searchText) {
      items = items.filter(slot => 
        slot.item?.name.toLowerCase().includes(searchText.toLowerCase()) ||
        slot.item?.description.toLowerCase().includes(searchText.toLowerCase())
      );
    }

    return items;
  };

  const handleItemPress = (slot: InventorySlot) => {
    if (!slot.item) return;
    
    setSelectedItem({ item: slot.item, slot });
    setShowItemModal(true);
  };

  const handleItemAction = (action: 'use' | 'equip' | 'drop' | 'sell') => {
    if (!selectedItem) return;

    const { item, slot } = selectedItem;

    switch (action) {
      case 'use':
        if (item.usable) {
          useItem(item);
        } else {
          Alert.alert('Erro', 'Este item não pode ser usado.');
        }
        break;

      case 'equip':
        if (item.equipable) {
          equipItem(item);
        } else {
          Alert.alert('Erro', 'Este item não pode ser equipado.');
        }
        break;

      case 'drop':
        dropItem(item);
        break;

      case 'sell':
        sellItem(item);
        break;
    }

    setShowItemModal(false);
    setSelectedItem(null);
  };

  const useItem = (item: Item) => {
    if (!state.personagem) return;

    switch (item.id) {
      case 'potion_healing':
        const healAmount = 8; // 2d4+2 médio
        const newHp = Math.min(
          state.personagem.pontosDeVidaMaximos,
          state.personagem.pontosDeVida + healAmount
        );
        
        updatePersonagem({
          ...state.personagem,
          pontosDeVida: newHp
        });

        inventory.removeItem(item.id, 1);
        Alert.alert('Item Usado', `Você recuperou ${healAmount} pontos de vida!`);
        break;

      case 'potion_strength':
        Alert.alert('Item Usado', 'Sua força aumentou temporariamente!');
        inventory.removeItem(item.id, 1);
        break;

      case 'scroll_magic_missile':
        Alert.alert('Pergaminho Usado', 'Você lança mísseis mágicos!');
        inventory.removeItem(item.id, 1);
        break;

      default:
        Alert.alert('Item Usado', `Você usou ${item.name}.`);
        inventory.removeItem(item.id, 1);
        break;
    }
  };

  const equipItem = (item: Item) => {
    const success = inventory.equipItem(item.id);
    
    if (success) {
      Alert.alert('Item Equipado', `${item.name} foi equipado!`);
      
      // Atualizar stats do personagem baseado no equipamento
      if (state.personagem) {
        const bonuses = inventory.getEquipmentBonuses();
        // Aplicar bônus ao personagem (implementação específica do jogo)
      }
    } else {
      Alert.alert('Erro', 'Não foi possível equipar este item.');
    }
  };

  const dropItem = (item: Item) => {
    Alert.alert(
      'Descartar Item',
      `Tem certeza que deseja descartar ${item.name}?`,
      [
        { text: 'Cancelar', style: 'cancel' },
        { 
          text: 'Descartar', 
          style: 'destructive',
          onPress: () => {
            inventory.removeItem(item.id, 1);
            Alert.alert('Item Descartado', `${item.name} foi descartado.`);
          }
        }
      ]
    );
  };

  const sellItem = (item: Item) => {
    const sellPrice = Math.floor(item.value * 0.5); // 50% do valor
    
    Alert.alert(
      'Vender Item',
      `Vender ${item.name} por ${sellPrice} moedas de ouro?`,
      [
        { text: 'Cancelar', style: 'cancel' },
        { 
          text: 'Vender',
          onPress: () => {
            inventory.removeItem(item.id, 1);
            inventory.addGold(sellPrice);
            
            if (state.personagem) {
              updatePersonagem({
                ...state.personagem,
                dinheiro: state.personagem.dinheiro + sellPrice
              });
            }
            
            Alert.alert('Item Vendido', `Você vendeu ${item.name} por ${sellPrice} moedas!`);
          }
        }
      ]
    );
  };

  const handleSort = (sortBy: 'name' | 'type' | 'value' | 'rarity') => {
    inventory.sortInventory(sortBy);
    setShowSortModal(false);
  };

  const handleAttributeDistribution = () => {
    Alert.alert(
      'Distribuir Pontos de Atributo',
      `Você tem ${attributePoints} pontos para distribuir.`,
      [
        { text: 'Força', onPress: () => distributeAttributePoint('strength') },
        { text: 'Destreza', onPress: () => distributeAttributePoint('dexterity') },
        { text: 'Constituição', onPress: () => distributeAttributePoint('constitution') },
        { text: 'Inteligência', onPress: () => distributeAttributePoint('intelligence') },
        { text: 'Sabedoria', onPress: () => distributeAttributePoint('wisdom') },
        { text: 'Carisma', onPress: () => distributeAttributePoint('charisma') },
        { text: 'Cancelar', style: 'cancel' }
      ]
    );
  };

  const distributeAttributePoint = (attribute: string) => {
    if (attributePoints <= 0 || !state.personagem) return;

    const newAttributes = { ...state.personagem.atributos };
    
    switch (attribute) {
      case 'strength':
        newAttributes.forca = Math.min(18, newAttributes.forca + 1);
        break;
      case 'dexterity':
        newAttributes.destreza = Math.min(18, newAttributes.destreza + 1);
        break;
      case 'constitution':
        newAttributes.constituicao = Math.min(18, newAttributes.constituicao + 1);
        break;
      case 'intelligence':
        newAttributes.inteligencia = Math.min(18, newAttributes.inteligencia + 1);
        break;
      case 'wisdom':
        newAttributes.sabedoria = Math.min(18, newAttributes.sabedoria + 1);
        break;
      case 'charisma':
        newAttributes.carisma = Math.min(18, newAttributes.carisma + 1);
        break;
    }

    updatePersonagem({
      ...state.personagem,
      atributos: newAttributes
    });

    setAttributePoints(prev => prev - 1);
    
    Alert.alert('Ponto Distribuído', `${attribute} aumentado em 1!`);
  };

  const getItemRarityColor = (rarity: ItemRarity): string => {
    switch (rarity) {
      case ItemRarity.COMMON:
        return '#FFFFFF';
      case ItemRarity.UNCOMMON:
        return '#1EFF00';
      case ItemRarity.RARE:
        return '#0070DD';
      case ItemRarity.EPIC:
        return '#A335EE';
      case ItemRarity.LEGENDARY:
        return '#FF8000';
      default:
        return '#FFFFFF';
    }
  };

  const getItemIcon = (item: Item): string => {
    switch (item.type) {
      case ItemType.WEAPON:
        return '⚔️';
      case ItemType.ARMOR:
        return '🛡️';
      case ItemType.SHIELD:
        return '🛡️';
      case ItemType.POTION:
        return '🧪';
      case ItemType.SCROLL:
        return '📜';
      case ItemType.TREASURE:
        return '💎';
      case ItemType.TOOL:
        return '🔧';
      default:
        return '📦';
    }
  };

  const renderInventorySlot = (slot: InventorySlot, index: number) => {
    const isEmpty = !slot.item;
    
    return (
      <TouchableOpacity
        key={index}
        style={[
          styles.inventorySlot,
          isEmpty && styles.emptySlot,
          slot.equipped && styles.equippedSlot
        ]}
        onPress={() => !isEmpty && handleItemPress(slot)}
      >
        {!isEmpty && (
          <>
            <Text style={styles.itemIcon}>
              {getItemIcon(slot.item!)}
            </Text>
            
            {slot.quantity > 1 && (
              <View style={styles.quantityBadge}>
                <Text style={styles.quantityText}>{slot.quantity}</Text>
              </View>
            )}
            
            {slot.equipped && (
              <View style={styles.equippedBadge}>
                <Text style={styles.equippedText}>E</Text>
              </View>
            )}
            
            <View 
              style={[
                styles.rarityBorder,
                { borderColor: getItemRarityColor(slot.item!.rarity) }
              ]} 
            />
          </>
        )}
      </TouchableOpacity>
    );
  };

  const renderItemModal = () => {
    if (!selectedItem) return null;

    const { item, slot } = selectedItem;

    return (
      <Modal
        visible={showItemModal}
        transparent={true}
        animationType="fade"
        onRequestClose={() => setShowItemModal(false)}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.itemModalContent}>
            <View style={styles.itemHeader}>
              <Text style={[styles.itemName, { color: getItemRarityColor(item.rarity) }]}>
                {item.name}
              </Text>
              <Text style={styles.itemType}>
                {item.type.charAt(0).toUpperCase() + item.type.slice(1)}
              </Text>
            </View>

            <Text style={styles.itemDescription}>
              {item.description}
            </Text>

            <View style={styles.itemStats}>
              <Text style={styles.statText}>Valor: {item.value} PO</Text>
              <Text style={styles.statText}>Peso: {item.weight} kg</Text>
              {slot.quantity > 1 && (
                <Text style={styles.statText}>Quantidade: {slot.quantity}</Text>
              )}
            </View>

            {Object.keys(item.properties).length > 0 && (
              <View style={styles.itemProperties}>
                <Text style={styles.propertiesTitle}>Propriedades:</Text>
                {Object.entries(item.properties).map(([key, value]) => (
                  <Text key={key} style={styles.propertyText}>
                    • {key}: {value}
                  </Text>
                ))}
              </View>
            )}

            <View style={styles.itemActions}>
              {item.usable && (
                <TouchableOpacity
                  style={[styles.actionButton, styles.useButton]}
                  onPress={() => handleItemAction('use')}
                >
                  <Text style={styles.actionButtonText}>Usar</Text>
                </TouchableOpacity>
              )}

              {item.equipable && (
                <TouchableOpacity
                  style={[styles.actionButton, styles.equipButton]}
                  onPress={() => handleItemAction('equip')}
                >
                  <Text style={styles.actionButtonText}>
                    {slot.equipped ? 'Desequipar' : 'Equipar'}
                  </Text>
                </TouchableOpacity>
              )}

              <TouchableOpacity
                style={[styles.actionButton, styles.sellButton]}
                onPress={() => handleItemAction('sell')}
              >
                <Text style={styles.actionButtonText}>Vender</Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={[styles.actionButton, styles.dropButton]}
                onPress={() => handleItemAction('drop')}
              >
                <Text style={styles.actionButtonText}>Descartar</Text>
              </TouchableOpacity>
            </View>

            <TouchableOpacity
              style={styles.closeButton}
              onPress={() => setShowItemModal(false)}
            >
              <Text style={styles.closeButtonText}>Fechar</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
    );
  };

  const renderSortModal = () => (
    <Modal
      visible={showSortModal}
      transparent={true}
      animationType="fade"
      onRequestClose={() => setShowSortModal(false)}
    >
      <View style={styles.modalOverlay}>
        <View style={styles.sortModalContent}>
          <Text style={styles.sortTitle}>Ordenar Inventário</Text>
          
          <TouchableOpacity
            style={styles.sortOption}
            onPress={() => handleSort('name')}
          >
            <Text style={styles.sortOptionText}>Por Nome</Text>
          </TouchableOpacity>
          
          <TouchableOpacity
            style={styles.sortOption}
            onPress={() => handleSort('type')}
          >
            <Text style={styles.sortOptionText}>Por Tipo</Text>
          </TouchableOpacity>
          
          <TouchableOpacity
            style={styles.sortOption}
            onPress={() => handleSort('value')}
          >
            <Text style={styles.sortOptionText}>Por Valor</Text>
          </TouchableOpacity>
          
          <TouchableOpacity
            style={styles.sortOption}
            onPress={() => handleSort('rarity')}
          >
            <Text style={styles.sortOptionText}>Por Raridade</Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={styles.closeButton}
            onPress={() => setShowSortModal(false)}
          >
            <Text style={styles.closeButtonText}>Cancelar</Text>
          </TouchableOpacity>
        </View>
      </View>
    </Modal>
  );

  const filteredItems = getFilteredItems();

  return (
    <View style={styles.container}>
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.title}>Inventário</Text>
        <View style={styles.headerInfo}>
          <Text style={styles.goldText}>💰 {inventory.getGold()} PO</Text>
          <Text style={styles.weightText}>
            ⚖️ {inventory.getTotalWeight().toFixed(1)} kg
          </Text>
        </View>
      </View>

      {/* Pontos de Atributo */}
      {attributePoints > 0 && (
        <TouchableOpacity
          style={styles.attributePointsContainer}
          onPress={handleAttributeDistribution}
        >
          <Text style={styles.attributePointsText}>
            🎯 {attributePoints} pontos de atributo disponíveis - Toque para distribuir
          </Text>
        </TouchableOpacity>
      )}

      {/* Abas */}
      <View style={styles.tabContainer}>
        <ScrollView horizontal showsHorizontalScrollIndicator={false}>
          {Object.values(InventoryTab).map(tab => (
            <TouchableOpacity
              key={tab}
              style={[styles.tab, activeTab === tab && styles.activeTab]}
              onPress={() => setActiveTab(tab)}
            >
              <Text style={[styles.tabText, activeTab === tab && styles.activeTabText]}>
                {tab.charAt(0).toUpperCase() + tab.slice(1)}
              </Text>
            </TouchableOpacity>
          ))}
        </ScrollView>
      </View>

      {/* Barra de busca e controles */}
      <View style={styles.controlsContainer}>
        <TextInput
          style={styles.searchInput}
          placeholder="Buscar itens..."
          placeholderTextColor="#888"
          value={searchText}
          onChangeText={setSearchText}
        />
        
        <TouchableOpacity
          style={styles.sortButton}
          onPress={() => setShowSortModal(true)}
        >
          <Text style={styles.sortButtonText}>📊</Text>
        </TouchableOpacity>
      </View>

      {/* Grid de inventário */}
      <ScrollView style={styles.inventoryContainer}>
        <View style={styles.inventoryGrid}>
          {Array.from({ length: inventory['maxSlots'] }, (_, index) => {
            const slot = inventory['slots'][index] || { item: null, quantity: 0, equipped: false };
            return renderInventorySlot(slot, index);
          })}
        </View>
      </ScrollView>

      {/* Informações do inventário */}
      <View style={styles.inventoryInfo}>
        <Text style={styles.infoText}>
          Slots: {inventory.getItems().length}/{inventory['maxSlots']}
        </Text>
        <Text style={styles.infoText}>
          Valor Total: {inventory.getTotalValue()} PO
        </Text>
      </View>

      {/* Botão de voltar */}
      <TouchableOpacity
        style={styles.backButton}
        onPress={() => onNavigate(EstadoJogo.AVENTURA)}
      >
        <Text style={styles.backButtonText}>Voltar</Text>
      </TouchableOpacity>

      {renderItemModal()}
      {renderSortModal()}
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
  },
  title: {
    color: '#FFD700',
    fontSize: 24,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 10,
  },
  headerInfo: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  goldText: {
    color: '#FFD700',
    fontSize: 16,
    fontWeight: 'bold',
  },
  weightText: {
    color: '#FFF',
    fontSize: 16,
  },
  attributePointsContainer: {
    backgroundColor: 'rgba(255, 215, 0, 0.2)',
    padding: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#FFD700',
  },
  attributePointsText: {
    color: '#FFD700',
    fontSize: 14,
    fontWeight: 'bold',
    textAlign: 'center',
  },
  tabContainer: {
    backgroundColor: 'rgba(139, 69, 19, 0.5)',
    paddingVertical: 10,
  },
  tab: {
    paddingHorizontal: 20,
    paddingVertical: 8,
    marginHorizontal: 5,
    borderRadius: 20,
    backgroundColor: 'rgba(255, 255, 255, 0.1)',
  },
  activeTab: {
    backgroundColor: '#FFD700',
  },
  tabText: {
    color: '#FFF',
    fontSize: 14,
    fontWeight: 'bold',
  },
  activeTabText: {
    color: '#000',
  },
  controlsContainer: {
    flexDirection: 'row',
    padding: 10,
    gap: 10,
  },
  searchInput: {
    flex: 1,
    backgroundColor: 'rgba(255, 255, 255, 0.1)',
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 8,
    color: '#FFF',
    fontSize: 14,
  },
  sortButton: {
    backgroundColor: '#8B4513',
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 8,
    justifyContent: 'center',
  },
  sortButtonText: {
    fontSize: 16,
  },
  inventoryContainer: {
    flex: 1,
    padding: 10,
  },
  inventoryGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  inventorySlot: {
    width: SLOT_SIZE,
    height: SLOT_SIZE,
    backgroundColor: 'rgba(255, 255, 255, 0.1)',
    borderRadius: 8,
    borderWidth: 2,
    borderColor: '#666',
    justifyContent: 'center',
    alignItems: 'center',
    position: 'relative',
  },
  emptySlot: {
    backgroundColor: 'rgba(255, 255, 255, 0.05)',
  },
  equippedSlot: {
    backgroundColor: 'rgba(255, 215, 0, 0.2)',
    borderColor: '#FFD700',
  },
  itemIcon: {
    fontSize: 24,
  },
  quantityBadge: {
    position: 'absolute',
    top: 2,
    right: 2,
    backgroundColor: '#FF4444',
    borderRadius: 8,
    minWidth: 16,
    height: 16,
    justifyContent: 'center',
    alignItems: 'center',
  },
  quantityText: {
    color: '#FFF',
    fontSize: 10,
    fontWeight: 'bold',
  },
  equippedBadge: {
    position: 'absolute',
    bottom: 2,
    left: 2,
    backgroundColor: '#4CAF50',
    borderRadius: 6,
    width: 12,
    height: 12,
    justifyContent: 'center',
    alignItems: 'center',
  },
  equippedText: {
    color: '#FFF',
    fontSize: 8,
    fontWeight: 'bold',
  },
  rarityBorder: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    borderRadius: 6,
    borderWidth: 2,
  },
  inventoryInfo: {
    backgroundColor: 'rgba(139, 69, 19, 0.5)',
    padding: 10,
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  infoText: {
    color: '#FFF',
    fontSize: 12,
  },
  backButton: {
    backgroundColor: '#8B4513',
    borderWidth: 2,
    borderColor: '#D2691E',
    borderRadius: 8,
    paddingVertical: 12,
    alignItems: 'center',
    margin: 15,
  },
  backButtonText: {
    color: '#FFD700',
    fontSize: 16,
    fontWeight: 'bold',
  },
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.8)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  itemModalContent: {
    backgroundColor: '#2a2a2a',
    borderRadius: 12,
    padding: 20,
    margin: 20,
    maxWidth: width * 0.9,
    borderWidth: 2,
    borderColor: '#D2691E',
  },
  itemHeader: {
    alignItems: 'center',
    marginBottom: 15,
  },
  itemName: {
    fontSize: 20,
    fontWeight: 'bold',
    textAlign: 'center',
  },
  itemType: {
    color: '#CCC',
    fontSize: 14,
    marginTop: 4,
  },
  itemDescription: {
    color: '#FFF',
    fontSize: 14,
    textAlign: 'center',
    marginBottom: 15,
    lineHeight: 20,
  },
  itemStats: {
    marginBottom: 15,
  },
  statText: {
    color: '#CCC',
    fontSize: 12,
    marginBottom: 4,
  },
  itemProperties: {
    marginBottom: 15,
  },
  propertiesTitle: {
    color: '#FFD700',
    fontSize: 14,
    fontWeight: 'bold',
    marginBottom: 8,
  },
  propertyText: {
    color: '#FFF',
    fontSize: 12,
    marginBottom: 4,
  },
  itemActions: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
    marginBottom: 15,
  },
  actionButton: {
    flex: 1,
    minWidth: 80,
    paddingVertical: 8,
    paddingHorizontal: 12,
    borderRadius: 6,
    alignItems: 'center',
  },
  useButton: {
    backgroundColor: '#4CAF50',
  },
  equipButton: {
    backgroundColor: '#2196F3',
  },
  sellButton: {
    backgroundColor: '#FF9800',
  },
  dropButton: {
    backgroundColor: '#F44336',
  },
  actionButtonText: {
    color: '#FFF',
    fontSize: 12,
    fontWeight: 'bold',
  },
  closeButton: {
    backgroundColor: '#8B4513',
    borderWidth: 2,
    borderColor: '#D2691E',
    borderRadius: 8,
    paddingVertical: 12,
    alignItems: 'center',
  },
  closeButtonText: {
    color: '#FFD700',
    fontSize: 16,
    fontWeight: 'bold',
  },
  sortModalContent: {
    backgroundColor: '#2a2a2a',
    borderRadius: 12,
    padding: 20,
    margin: 20,
    borderWidth: 2,
    borderColor: '#D2691E',
  },
  sortTitle: {
    color: '#FFD700',
    fontSize: 18,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 20,
  },
  sortOption: {
    backgroundColor: '#8B4513',
    borderRadius: 8,
    paddingVertical: 12,
    paddingHorizontal: 16,
    marginBottom: 10,
    alignItems: 'center',
  },
  sortOptionText: {
    color: '#FFD700',
    fontSize: 16,
    fontWeight: 'bold',
  },
});
