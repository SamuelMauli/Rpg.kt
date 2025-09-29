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
} from 'react-native';
import { EstadoJogo } from '../types/GameTypes';
import { useGame } from '../contexts/GameContext';
import { CombatManager } from '../game/combat/CombatManager';
import { MonsterFactory } from '../game/monsters/MonsterFactory';

interface CombatScreenProps {
  onNavigate: (screen: EstadoJogo) => void;
}

const { width } = Dimensions.get('window');

export default function CombatScreen({ onNavigate }: CombatScreenProps) {
  const { state, updatePersonagem, addExperiencia, setCombate } = useGame();
  const [combatManager] = useState(new CombatManager());
  const [combatLog, setCombatLog] = useState<string[]>([]);
  const [selectedTarget, setSelectedTarget] = useState(0);

  useEffect(() => {
    if (state.estadoCombate) {
      addToCombatLog('=== COMBATE INICIADO ===');
      addToCombatLog(combatManager.getDescricaoSituacao());
    }
  }, []);

  const addToCombatLog = (message: string) => {
    setCombatLog(prev => [...prev, message]);
  };

  const handlePlayerAction = (action: 'atacar' | 'fugir' | 'usar_item') => {
    if (!state.estadoCombate || !combatManager.isCombateAtivo()) {
      return;
    }

    const result = combatManager.processarTurnoPersonagem(action, selectedTarget);
    addToCombatLog(result);

    // Verificar se o combate terminou
    if (!combatManager.isCombateAtivo()) {
      const combatResult = combatManager.finalizarCombate();
      if (combatResult) {
        if (combatResult.vitoria) {
          addToCombatLog('=== VITÓRIA! ===');
          addToCombatLog(`Experiência ganha: ${combatResult.experienciaGanha}`);
          
          // Adicionar experiência
          addExperiencia(combatResult.experienciaGanha);
          
          // Adicionar tesouro
          if (combatResult.tesouroEncontrado.length > 0) {
            combatResult.tesouroEncontrado.forEach(item => {
              addToCombatLog(`Você encontrou: ${item.nome}`);
            });
          }

          setTimeout(() => {
            Alert.alert(
              'Vitória!',
              `Você derrotou os inimigos e ganhou ${combatResult.experienciaGanha} pontos de experiência!`,
              [{ text: 'Continuar', onPress: () => returnToAdventure() }]
            );
          }, 1000);
        } else {
          addToCombatLog('=== DERROTA ===');
          setTimeout(() => {
            Alert.alert(
              'Derrota!',
              'Seu personagem foi derrotado...',
              [{ text: 'Voltar ao Menu', onPress: () => onNavigate(EstadoJogo.MENU_PRINCIPAL) }]
            );
          }, 1000);
        }
      }
    }
  };

  const returnToAdventure = () => {
    setCombate(null);
    onNavigate(EstadoJogo.AVENTURA);
  };

  const getCharacterSprite = () => {
    if (!state.personagem) return require('../../assets/sprites/warrior_sprite.png');
    
    switch (state.personagem.classe.getNome().toLowerCase()) {
      case 'guerreiro':
        return require('../../assets/sprites/warrior_sprite.png');
      case 'ladrão':
        return require('../../assets/sprites/rogue_sprite.png');
      case 'clérigo':
        return require('../../assets/sprites/cleric_sprite.png');
      case 'mago':
        return require('../../assets/sprites/mage_sprite.png');
      default:
        return require('../../assets/sprites/warrior_sprite.png');
    }
  };

  const getMonsterSprite = (monsterName: string) => {
    switch (monsterName.toLowerCase()) {
      case 'goblin':
        return require('../../assets/sprites/goblin_sprite.png');
      case 'orc':
        return require('../../assets/sprites/orc_sprite.png');
      case 'esqueleto':
        return require('../../assets/sprites/skeleton_sprite.png');
      case 'zumbi':
        return require('../../assets/sprites/zombie_sprite.png');
      case 'necromante':
        return require('../../assets/sprites/necromancer_sprite.png');
      default:
        return require('../../assets/sprites/goblin_sprite.png');
    }
  };

  const monstersAlive = combatManager.getMonstrosVivos();

  if (!state.estadoCombate || !state.personagem) {
    return (
      <View style={styles.container}>
        <Text style={styles.errorText}>Erro: Estado de combate inválido</Text>
        <TouchableOpacity
          style={styles.button}
          onPress={() => onNavigate(EstadoJogo.AVENTURA)}
        >
          <Text style={styles.buttonText}>Voltar</Text>
        </TouchableOpacity>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      {/* Header com informações do personagem */}
      <View style={styles.header}>
        <View style={styles.characterInfo}>
          <Text style={styles.characterName}>{state.personagem.nome}</Text>
          <View style={styles.healthBar}>
            <View style={styles.healthBarBg}>
              <View 
                style={[
                  styles.healthBarFill, 
                  { 
                    width: `${(state.personagem.pontosVida / state.personagem.pontosVidaMaximos) * 100}%` 
                  }
                ]} 
              />
            </View>
            <Text style={styles.healthText}>
              {state.personagem.pontosVida}/{state.personagem.pontosVidaMaximos} PV
            </Text>
          </View>
        </View>
      </View>

      {/* Campo de batalha */}
      <View style={styles.battleField}>
        {/* Personagem */}
        <View style={styles.characterSide}>
          <Image source={getCharacterSprite()} style={styles.characterSprite} />
          <Text style={styles.spriteLabel}>{state.personagem.nome}</Text>
        </View>

        {/* VS */}
        <Text style={styles.vsText}>VS</Text>

        {/* Monstros */}
        <View style={styles.monsterSide}>
          {monstersAlive.map((monster, index) => (
            <TouchableOpacity
              key={index}
              style={[
                styles.monsterContainer,
                selectedTarget === index && styles.selectedTarget
              ]}
              onPress={() => setSelectedTarget(index)}
            >
              <Image 
                source={getMonsterSprite(monster.nome)} 
                style={styles.monsterSprite} 
              />
              <Text style={styles.spriteLabel}>{monster.nome}</Text>
              <Text style={styles.monsterHealth}>{monster.pontosDeVida} PV</Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>

      {/* Log de combate */}
      <ScrollView 
        style={styles.combatLog}
        ref={ref => ref?.scrollToEnd({ animated: true })}
      >
        {combatLog.map((entry, index) => (
          <Text key={index} style={styles.logEntry}>
            {entry}
          </Text>
        ))}
      </ScrollView>

      {/* Ações de combate */}
      {combatManager.isCombateAtivo() && (
        <View style={styles.actionsContainer}>
          <TouchableOpacity
            style={styles.actionButton}
            onPress={() => handlePlayerAction('atacar')}
          >
            <Text style={styles.actionButtonText}>ATACAR</Text>
          </TouchableOpacity>
          
          <TouchableOpacity
            style={[styles.actionButton, styles.fleeButton]}
            onPress={() => handlePlayerAction('fugir')}
          >
            <Text style={styles.actionButtonText}>FUGIR</Text>
          </TouchableOpacity>
        </View>
      )}

      {/* Indicador de alvo selecionado */}
      {monstersAlive.length > 1 && (
        <View style={styles.targetInfo}>
          <Text style={styles.targetText}>
            Alvo: {monstersAlive[selectedTarget]?.nome || 'Nenhum'}
          </Text>
        </View>
      )}
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
  characterInfo: {
    alignItems: 'center',
  },
  characterName: {
    color: '#FFD700',
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 8,
  },
  healthBar: {
    alignItems: 'center',
  },
  healthBarBg: {
    width: 200,
    height: 20,
    backgroundColor: '#333',
    borderRadius: 10,
    borderWidth: 2,
    borderColor: '#666',
    overflow: 'hidden',
  },
  healthBarFill: {
    height: '100%',
    backgroundColor: '#FF4444',
    borderRadius: 8,
  },
  healthText: {
    color: '#FFF',
    fontSize: 12,
    marginTop: 4,
  },
  battleField: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-around',
    padding: 20,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
  },
  characterSide: {
    alignItems: 'center',
    flex: 1,
  },
  characterSprite: {
    width: 80,
    height: 80,
    resizeMode: 'contain',
  },
  vsText: {
    color: '#FFD700',
    fontSize: 24,
    fontWeight: 'bold',
    textShadowColor: '#000',
    textShadowOffset: { width: 2, height: 2 },
    textShadowRadius: 4,
  },
  monsterSide: {
    flex: 1,
    alignItems: 'center',
  },
  monsterContainer: {
    alignItems: 'center',
    marginVertical: 5,
    padding: 8,
    borderRadius: 8,
    borderWidth: 2,
    borderColor: 'transparent',
  },
  selectedTarget: {
    borderColor: '#FFD700',
    backgroundColor: 'rgba(255, 215, 0, 0.2)',
  },
  monsterSprite: {
    width: 60,
    height: 60,
    resizeMode: 'contain',
  },
  spriteLabel: {
    color: '#FFF',
    fontSize: 12,
    fontWeight: 'bold',
    textAlign: 'center',
    marginTop: 4,
  },
  monsterHealth: {
    color: '#FF4444',
    fontSize: 10,
    textAlign: 'center',
  },
  combatLog: {
    backgroundColor: '#000',
    maxHeight: 150,
    padding: 10,
    borderTopWidth: 2,
    borderTopColor: '#333',
  },
  logEntry: {
    color: '#CCC',
    fontSize: 12,
    marginBottom: 4,
    lineHeight: 16,
  },
  actionsContainer: {
    flexDirection: 'row',
    padding: 15,
    gap: 10,
    backgroundColor: 'rgba(139, 69, 19, 0.9)',
    borderTopWidth: 2,
    borderTopColor: '#D2691E',
  },
  actionButton: {
    flex: 1,
    backgroundColor: '#8B4513',
    borderWidth: 2,
    borderColor: '#D2691E',
    borderRadius: 8,
    paddingVertical: 15,
    alignItems: 'center',
  },
  fleeButton: {
    backgroundColor: '#666',
    borderColor: '#888',
  },
  actionButtonText: {
    color: '#FFD700',
    fontSize: 16,
    fontWeight: 'bold',
  },
  targetInfo: {
    backgroundColor: 'rgba(0, 0, 0, 0.8)',
    padding: 8,
    alignItems: 'center',
  },
  targetText: {
    color: '#FFD700',
    fontSize: 14,
    fontWeight: 'bold',
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
