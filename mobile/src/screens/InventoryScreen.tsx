import React from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
  Image,
} from 'react-native';
import { EstadoJogo } from '../types/GameTypes';
import { useGame } from '../contexts/GameContext';

interface InventoryScreenProps {
  onNavigate: (screen: EstadoJogo) => void;
}

export default function InventoryScreen({ onNavigate }: InventoryScreenProps) {
  const { state } = useGame();

  if (!state.personagem) {
    return (
      <View style={styles.container}>
        <Text style={styles.errorText}>Nenhum personagem encontrado</Text>
        <TouchableOpacity
          style={styles.button}
          onPress={() => onNavigate(EstadoJogo.MENU_PRINCIPAL)}
        >
          <Text style={styles.buttonText}>Voltar ao Menu</Text>
        </TouchableOpacity>
      </View>
    );
  }

  const { personagem } = state;

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Inventário</Text>
        <TouchableOpacity
          style={styles.backButton}
          onPress={() => onNavigate(EstadoJogo.AVENTURA)}
        >
          <Text style={styles.buttonText}>Voltar</Text>
        </TouchableOpacity>
      </View>

      <ScrollView style={styles.content}>
        {/* Informações do inventário */}
        <View style={styles.inventoryInfo}>
          <Text style={styles.sectionTitle}>Capacidade</Text>
          <Text style={styles.infoText}>
            Peso: {personagem.inventario.pesoAtual}/{personagem.inventario.pesoMaximo} kg
          </Text>
          <Text style={styles.infoText}>
            Itens: {personagem.inventario.itens.length}/{personagem.inventario.capacidade}
          </Text>
          <Text style={styles.infoText}>
            Dinheiro: {personagem.dinheiro} moedas de ouro
          </Text>
        </View>

        {/* Lista de itens */}
        <View style={styles.itemsSection}>
          <Text style={styles.sectionTitle}>Itens</Text>
          {personagem.inventario.itens.length === 0 ? (
            <Text style={styles.emptyText}>Inventário vazio</Text>
          ) : (
            personagem.inventario.itens.map((item, index) => (
              <View key={index} style={styles.itemContainer}>
                <View style={styles.itemIcon}>
                  <Image
                    source={getItemIcon(item.tipo)}
                    style={styles.iconImage}
                  />
                </View>
                <View style={styles.itemInfo}>
                  <Text style={styles.itemName}>{item.nome}</Text>
                  <Text style={styles.itemDescription}>{item.descricao}</Text>
                  <Text style={styles.itemStats}>
                    Peso: {item.peso}kg | Valor: {item.valor} mo
                  </Text>
                </View>
              </View>
            ))
          )}
        </View>
      </ScrollView>
    </View>
  );
}

function getItemIcon(tipo: string) {
  switch (tipo) {
    case 'arma':
      return require('../../assets/sprites/sword_icon.png');
    case 'armadura':
    case 'escudo':
      return require('../../assets/sprites/shield_icon.png');
    case 'pocao':
      return require('../../assets/sprites/potion_icon.png');
    default:
      return require('../../assets/sprites/sword_icon.png');
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#1a1a1a',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 20,
    paddingTop: 40,
    backgroundColor: 'rgba(139, 69, 19, 0.9)',
    borderBottomWidth: 2,
    borderBottomColor: '#D2691E',
  },
  title: {
    color: '#FFD700',
    fontSize: 24,
    fontWeight: 'bold',
  },
  backButton: {
    backgroundColor: '#666',
    borderWidth: 2,
    borderColor: '#888',
    borderRadius: 6,
    paddingVertical: 8,
    paddingHorizontal: 15,
  },
  content: {
    flex: 1,
    padding: 20,
  },
  inventoryInfo: {
    backgroundColor: '#333',
    borderRadius: 8,
    padding: 15,
    marginBottom: 20,
    borderWidth: 2,
    borderColor: '#555',
  },
  sectionTitle: {
    color: '#FFD700',
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  infoText: {
    color: '#FFF',
    fontSize: 14,
    marginBottom: 5,
  },
  itemsSection: {
    flex: 1,
  },
  emptyText: {
    color: '#888',
    fontSize: 16,
    textAlign: 'center',
    marginTop: 20,
    fontStyle: 'italic',
  },
  itemContainer: {
    flexDirection: 'row',
    backgroundColor: '#333',
    borderRadius: 8,
    padding: 12,
    marginBottom: 10,
    borderWidth: 2,
    borderColor: '#555',
  },
  itemIcon: {
    width: 40,
    height: 40,
    marginRight: 12,
    justifyContent: 'center',
    alignItems: 'center',
  },
  iconImage: {
    width: 32,
    height: 32,
    resizeMode: 'contain',
  },
  itemInfo: {
    flex: 1,
  },
  itemName: {
    color: '#FFD700',
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 4,
  },
  itemDescription: {
    color: '#CCC',
    fontSize: 12,
    marginBottom: 4,
  },
  itemStats: {
    color: '#888',
    fontSize: 10,
  },
  errorText: {
    color: '#FF4444',
    fontSize: 18,
    textAlign: 'center',
    margin: 20,
  },
  button: {
    backgroundColor: '#8B4513',
    borderWidth: 2,
    borderColor: '#D2691E',
    borderRadius: 8,
    paddingVertical: 12,
    paddingHorizontal: 20,
    alignItems: 'center',
    margin: 20,
  },
  buttonText: {
    color: '#FFD700',
    fontSize: 16,
    fontWeight: 'bold',
  },
});
