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
  Animated,
  Modal,
} from 'react-native';
import { EstadoJogo } from '../types/GameTypes';
import { useGame } from '../contexts/GameContext';
import { CombatManager } from '../game/combat/CombatManager';
import { MonsterFactory } from '../game/monsters/MonsterFactory';
import DiceRoller from '../components/DiceRoller';
import { DiceSystem, DiceRoll } from '../utils/DiceSystem';

interface EnhancedCombatScreenProps {
  onNavigate: (screen: EstadoJogo) => void;
}

enum CombatAction {
  ATTACK_MELEE = 'attack_melee',
  ATTACK_RANGED = 'attack_ranged',
  DEFEND = 'defend',
  USE_ITEM = 'use_item',
  CAST_SPELL = 'cast_spell',
  FLEE = 'flee'
}

const { width, height } = Dimensions.get('window');

export default function EnhancedCombatScreen({ onNavigate }: EnhancedCombatScreenProps) {
  const { state, updatePersonagem, addExperiencia, setCombate } = useGame();
  const [combatManager] = useState(new CombatManager());
  const [combatLog, setCombatLog] = useState<string[]>([]);
  const [selectedTarget, setSelectedTarget] = useState(0);
  const [selectedAction, setSelectedAction] = useState<CombatAction | null>(null);
  const [showDiceModal, setShowDiceModal] = useState(false);
  const [currentDiceRoll, setCurrentDiceRoll] = useState<DiceRoll | null>(null);
  const [isPlayerTurn, setIsPlayerTurn] = useState(true);
  const [turnCounter, setTurnCounter] = useState(1);
  const [combatPhase, setCombatPhase] = useState<'initiative' | 'combat' | 'resolution'>('initiative');
  
  // Animações
  const [shakeAnimation] = useState(new Animated.Value(0));
  const [flashAnimation] = useState(new Animated.Value(0));
  const [scaleAnimation] = useState(new Animated.Value(1));

  const diceSystem = DiceSystem.getInstance();

  useEffect(() => {
    if (state.estadoCombate) {
      initializeCombat();
    }
  }, []);

  const initializeCombat = () => {
    addToCombatLog('=== COMBATE INICIADO ===');
    addToCombatLog(combatManager.getDescricaoSituacao());
    
    // Rolar iniciativa
    rollInitiative();
  };

  const rollInitiative = () => {
    setCombatPhase('initiative');
    addToCombatLog('=== ROLAGEM DE INICIATIVA ===');
    
    if (state.personagem) {
      const initiativeRoll = diceSystem.rollInitiative(
        state.personagem.atributos.destreza,
        state.personagem.atributos.sabedoria
      );
      
      addToCombatLog(`${state.personagem.nome}: ${diceSystem.formatRoll(initiativeRoll)}`);
      
      if (initiativeRoll.total <= Math.max(state.personagem.atributos.destreza, state.personagem.atributos.sabedoria)) {
        addToCombatLog(`${state.personagem.nome} age primeiro!`);
        setIsPlayerTurn(true);
      } else {
        addToCombatLog('Os monstros agem primeiro!');
        setIsPlayerTurn(false);
        setTimeout(() => processMonsterTurn(), 1500);
      }
    }
    
    setCombatPhase('combat');
  };

  const addToCombatLog = (message: string) => {
    setCombatLog(prev => [...prev, message]);
  };

  const handleActionSelect = (action: CombatAction) => {
    setSelectedAction(action);
    
    switch (action) {
      case CombatAction.ATTACK_MELEE:
        performMeleeAttack();
        break;
      case CombatAction.ATTACK_RANGED:
        performRangedAttack();
        break;
      case CombatAction.DEFEND:
        performDefend();
        break;
      case CombatAction.USE_ITEM:
        showItemMenu();
        break;
      case CombatAction.CAST_SPELL:
        showSpellMenu();
        break;
      case CombatAction.FLEE:
        attemptFlee();
        break;
    }
  };

  const performMeleeAttack = () => {
    if (!state.personagem) return;
    
    const attackRoll = diceSystem.rollSingle(20, state.personagem.getBAC());
    setCurrentDiceRoll(attackRoll);
    setShowDiceModal(true);
    
    setTimeout(() => {
      const monsters = combatManager.getMonstrosVivos();
      const target = monsters[selectedTarget];
      
      if (target && attackRoll.total >= target.classeArmadura) {
        // Acertou
        const damageRoll = diceSystem.rollWeaponDamage(
          '1d8', // Espada longa padrão
          state.personagem.atributos.getModificadorForca(),
          attackRoll.critical
        );
        
        addToCombatLog(`${state.personagem.nome} ataca ${target.nome}!`);
        addToCombatLog(`Ataque: ${diceSystem.formatRoll(attackRoll)}`);
        addToCombatLog(`Dano: ${diceSystem.formatRoll(damageRoll)}`);
        
        // Aplicar dano
        target.pontosDeVida -= damageRoll.total;
        
        if (target.pontosDeVida <= 0) {
          addToCombatLog(`${target.nome} foi derrotado!`);
          triggerDeathAnimation();
        } else {
          triggerHitAnimation();
        }
        
        if (attackRoll.critical) {
          addToCombatLog('ACERTO CRÍTICO!');
          triggerCriticalAnimation();
        }
      } else {
        // Errou
        addToCombatLog(`${state.personagem.nome} erra o ataque em ${target?.nome}!`);
        addToCombatLog(`Ataque: ${diceSystem.formatRoll(attackRoll)}`);
        
        if (attackRoll.fumble) {
          addToCombatLog('FALHA CRÍTICA!');
          triggerFumbleAnimation();
        }
      }
      
      setShowDiceModal(false);
      endPlayerTurn();
    }, 2000);
  };

  const performRangedAttack = () => {
    if (!state.personagem) return;
    
    const attackRoll = diceSystem.rollSingle(20, state.personagem.getBAD());
    setCurrentDiceRoll(attackRoll);
    setShowDiceModal(true);
    
    setTimeout(() => {
      const monsters = combatManager.getMonstrosVivos();
      const target = monsters[selectedTarget];
      
      if (target && attackRoll.total >= target.classeArmadura) {
        const damageRoll = diceSystem.rollWeaponDamage(
          '1d6', // Arco longo padrão
          0, // Sem modificador de força para ataques à distância
          attackRoll.critical
        );
        
        addToCombatLog(`${state.personagem.nome} dispara em ${target.nome}!`);
        addToCombatLog(`Ataque: ${diceSystem.formatRoll(attackRoll)}`);
        addToCombatLog(`Dano: ${diceSystem.formatRoll(damageRoll)}`);
        
        target.pontosDeVida -= damageRoll.total;
        
        if (target.pontosDeVida <= 0) {
          addToCombatLog(`${target.nome} foi derrotado!`);
        }
      } else {
        addToCombatLog(`${state.personagem.nome} erra o disparo!`);
        addToCombatLog(`Ataque: ${diceSystem.formatRoll(attackRoll)}`);
      }
      
      setShowDiceModal(false);
      endPlayerTurn();
    }, 2000);
  };

  const performDefend = () => {
    addToCombatLog(`${state.personagem?.nome} assume posição defensiva!`);
    addToCombatLog('CA aumentada em +2 até o próximo turno.');
    endPlayerTurn();
  };

  const showItemMenu = () => {
    Alert.alert(
      'Usar Item',
      'Escolha um item do inventário:',
      [
        { text: 'Poção de Cura', onPress: () => useItem('Poção de Cura') },
        { text: 'Poção de Força', onPress: () => useItem('Poção de Força') },
        { text: 'Cancelar', style: 'cancel' }
      ]
    );
  };

  const showSpellMenu = () => {
    Alert.alert(
      'Lançar Magia',
      'Escolha uma magia:',
      [
        { text: 'Mísseis Mágicos', onPress: () => castSpell('Mísseis Mágicos') },
        { text: 'Curar Ferimentos', onPress: () => castSpell('Curar Ferimentos') },
        { text: 'Cancelar', style: 'cancel' }
      ]
    );
  };

  const useItem = (itemName: string) => {
    if (!state.personagem) return;
    
    switch (itemName) {
      case 'Poção de Cura':
        const healRoll = diceSystem.rollMultiple(2, 4, 2); // 2d4+2
        state.personagem.pontosDeVida = Math.min(
          state.personagem.pontosDeVidaMaximos,
          state.personagem.pontosDeVida + healRoll.total
        );
        addToCombatLog(`${state.personagem.nome} usa uma Poção de Cura!`);
        addToCombatLog(`Cura: ${diceSystem.formatRoll(healRoll)} pontos de vida.`);
        break;
        
      case 'Poção de Força':
        addToCombatLog(`${state.personagem.nome} usa uma Poção de Força!`);
        addToCombatLog('Força aumentada em +4 por 10 turnos.');
        break;
    }
    
    endPlayerTurn();
  };

  const castSpell = (spellName: string) => {
    if (!state.personagem) return;
    
    switch (spellName) {
      case 'Mísseis Mágicos':
        const missileRoll = diceSystem.rollMultiple(3, 4, 3); // 3d4+3
        const monsters = combatManager.getMonstrosVivos();
        const target = monsters[selectedTarget];
        
        if (target) {
          target.pontosDeVida -= missileRoll.total;
          addToCombatLog(`${state.personagem.nome} lança Mísseis Mágicos em ${target.nome}!`);
          addToCombatLog(`Dano: ${diceSystem.formatRoll(missileRoll)}`);
          
          if (target.pontosDeVida <= 0) {
            addToCombatLog(`${target.nome} foi derrotado!`);
          }
        }
        break;
        
      case 'Curar Ferimentos':
        const healRoll = diceSystem.rollSingle(8, 1); // 1d8+1
        state.personagem.pontosDeVida = Math.min(
          state.personagem.pontosDeVidaMaximos,
          state.personagem.pontosDeVida + healRoll.total
        );
        addToCombatLog(`${state.personagem.nome} lança Curar Ferimentos!`);
        addToCombatLog(`Cura: ${diceSystem.formatRoll(healRoll)} pontos de vida.`);
        break;
    }
    
    endPlayerTurn();
  };

  const attemptFlee = () => {
    const fleeRoll = diceSystem.rollSingle(6);
    
    if (fleeRoll.total >= 4) { // 50% chance
      addToCombatLog(`${state.personagem?.nome} consegue fugir do combate!`);
      setTimeout(() => {
        setCombate(null);
        onNavigate(EstadoJogo.AVENTURA);
      }, 1500);
    } else {
      addToCombatLog(`${state.personagem?.nome} não consegue fugir!`);
      endPlayerTurn();
    }
  };

  const endPlayerTurn = () => {
    setSelectedAction(null);
    setIsPlayerTurn(false);
    
    // Verificar se o combate terminou
    if (checkCombatEnd()) {
      return;
    }
    
    // Turno dos monstros
    setTimeout(() => processMonsterTurn(), 1000);
  };

  const processMonsterTurn = () => {
    const monsters = combatManager.getMonstrosVivos();
    
    monsters.forEach((monster, index) => {
      if (monster.pontosDeVida > 0 && state.personagem) {
        const attackRoll = diceSystem.rollSingle(20, monster.baseAtaque);
        
        if (attackRoll.total >= state.personagem.getCA()) {
          const damageRoll = diceSystem.roll(monster.dano);
          state.personagem.pontosDeVida -= damageRoll.total;
          
          addToCombatLog(`${monster.nome} ataca ${state.personagem.nome}!`);
          addToCombatLog(`Dano: ${diceSystem.formatRoll(damageRoll)}`);
          
          if (state.personagem.pontosDeVida <= 0) {
            addToCombatLog(`${state.personagem.nome} foi derrotado!`);
            handlePlayerDefeat();
            return;
          }
        } else {
          addToCombatLog(`${monster.nome} erra o ataque!`);
        }
      }
    });
    
    // Próximo turno
    setTurnCounter(prev => prev + 1);
    setIsPlayerTurn(true);
  };

  const checkCombatEnd = (): boolean => {
    const monsters = combatManager.getMonstrosVivos();
    const aliveMonsters = monsters.filter(m => m.pontosDeVida > 0);
    
    if (aliveMonsters.length === 0) {
      handleVictory();
      return true;
    }
    
    if (state.personagem && state.personagem.pontosDeVida <= 0) {
      handlePlayerDefeat();
      return true;
    }
    
    return false;
  };

  const handleVictory = () => {
    setCombatPhase('resolution');
    addToCombatLog('=== VITÓRIA! ===');
    
    const expGained = 100 * turnCounter; // Exemplo
    addExperiencia(expGained);
    addToCombatLog(`Experiência ganha: ${expGained}`);
    
    setTimeout(() => {
      Alert.alert(
        'Vitória!',
        `Você derrotou os inimigos e ganhou ${expGained} pontos de experiência!`,
        [{ text: 'Continuar', onPress: () => returnToAdventure() }]
      );
    }, 2000);
  };

  const handlePlayerDefeat = () => {
    setCombatPhase('resolution');
    addToCombatLog('=== DERROTA ===');
    
    setTimeout(() => {
      Alert.alert(
        'Derrota!',
        'Seu personagem foi derrotado...',
        [{ text: 'Voltar ao Menu', onPress: () => onNavigate(EstadoJogo.MENU_PRINCIPAL) }]
      );
    }, 2000);
  };

  const returnToAdventure = () => {
    setCombate(null);
    onNavigate(EstadoJogo.AVENTURA);
  };

  // Animações
  const triggerHitAnimation = () => {
    Animated.sequence([
      Animated.timing(shakeAnimation, {
        toValue: 10,
        duration: 100,
        useNativeDriver: true,
      }),
      Animated.timing(shakeAnimation, {
        toValue: -10,
        duration: 100,
        useNativeDriver: true,
      }),
      Animated.timing(shakeAnimation, {
        toValue: 0,
        duration: 100,
        useNativeDriver: true,
      }),
    ]).start();
  };

  const triggerCriticalAnimation = () => {
    Animated.sequence([
      Animated.timing(flashAnimation, {
        toValue: 1,
        duration: 200,
        useNativeDriver: true,
      }),
      Animated.timing(flashAnimation, {
        toValue: 0,
        duration: 200,
        useNativeDriver: true,
      }),
    ]).start();
  };

  const triggerFumbleAnimation = () => {
    Animated.sequence([
      Animated.timing(scaleAnimation, {
        toValue: 0.8,
        duration: 300,
        useNativeDriver: true,
      }),
      Animated.timing(scaleAnimation, {
        toValue: 1,
        duration: 300,
        useNativeDriver: true,
      }),
    ]).start();
  };

  const triggerDeathAnimation = () => {
    Animated.timing(scaleAnimation, {
      toValue: 0,
      duration: 500,
      useNativeDriver: true,
    }).start();
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
      default:
        return require('../../assets/sprites/goblin_sprite.png');
    }
  };

  const monstersAlive = combatManager.getMonstrosVivos().filter(m => m.pontosDeVida > 0);

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
      {/* Header com informações do combate */}
      <View style={styles.header}>
        <View style={styles.combatInfo}>
          <Text style={styles.turnText}>Turno {turnCounter}</Text>
          <Text style={styles.phaseText}>
            {isPlayerTurn ? 'Seu Turno' : 'Turno dos Inimigos'}
          </Text>
        </View>
        
        <View style={styles.characterInfo}>
          <Text style={styles.characterName}>{state.personagem.nome}</Text>
          <View style={styles.healthBar}>
            <View style={styles.healthBarBg}>
              <View 
                style={[
                  styles.healthBarFill, 
                  { 
                    width: `${(state.personagem.pontosDeVida / state.personagem.pontosDeVidaMaximos) * 100}%` 
                  }
                ]} 
              />
            </View>
            <Text style={styles.healthText}>
              {state.personagem.pontosDeVida}/{state.personagem.pontosDeVidaMaximos} PV
            </Text>
          </View>
        </View>
      </View>

      {/* Campo de batalha com animações */}
      <View style={styles.battleField}>
        {/* Personagem */}
        <Animated.View 
          style={[
            styles.characterSide,
            {
              transform: [
                { translateX: shakeAnimation },
                { scale: scaleAnimation }
              ]
            }
          ]}
        >
          <Image source={getCharacterSprite()} style={styles.characterSprite} />
          <Text style={styles.spriteLabel}>{state.personagem.nome}</Text>
        </Animated.View>

        {/* VS */}
        <Animated.Text 
          style={[
            styles.vsText,
            {
              opacity: flashAnimation.interpolate({
                inputRange: [0, 1],
                outputRange: [1, 0.3],
              }),
            }
          ]}
        >
          VS
        </Animated.Text>

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
              <Animated.View
                style={{
                  transform: [{ scale: monster.pontosDeVida <= 0 ? 0 : 1 }]
                }}
              >
                <Image 
                  source={getMonsterSprite(monster.nome)} 
                  style={styles.monsterSprite} 
                />
                <Text style={styles.spriteLabel}>{monster.nome}</Text>
                <Text style={styles.monsterHealth}>{monster.pontosDeVida} PV</Text>
              </Animated.View>
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
      {isPlayerTurn && combatPhase === 'combat' && (
        <View style={styles.actionsContainer}>
          <View style={styles.actionRow}>
            <TouchableOpacity
              style={[styles.actionButton, selectedAction === CombatAction.ATTACK_MELEE && styles.selectedAction]}
              onPress={() => handleActionSelect(CombatAction.ATTACK_MELEE)}
            >
              <Text style={styles.actionButtonText}>⚔️ ATACAR</Text>
            </TouchableOpacity>
            
            <TouchableOpacity
              style={[styles.actionButton, selectedAction === CombatAction.ATTACK_RANGED && styles.selectedAction]}
              onPress={() => handleActionSelect(CombatAction.ATTACK_RANGED)}
            >
              <Text style={styles.actionButtonText}>🏹 DISPARAR</Text>
            </TouchableOpacity>
          </View>
          
          <View style={styles.actionRow}>
            <TouchableOpacity
              style={[styles.actionButton, selectedAction === CombatAction.DEFEND && styles.selectedAction]}
              onPress={() => handleActionSelect(CombatAction.DEFEND)}
            >
              <Text style={styles.actionButtonText}>🛡️ DEFENDER</Text>
            </TouchableOpacity>
            
            <TouchableOpacity
              style={[styles.actionButton, selectedAction === CombatAction.USE_ITEM && styles.selectedAction]}
              onPress={() => handleActionSelect(CombatAction.USE_ITEM)}
            >
              <Text style={styles.actionButtonText}>🧪 ITEM</Text>
            </TouchableOpacity>
          </View>
          
          <View style={styles.actionRow}>
            <TouchableOpacity
              style={[styles.actionButton, selectedAction === CombatAction.CAST_SPELL && styles.selectedAction]}
              onPress={() => handleActionSelect(CombatAction.CAST_SPELL)}
            >
              <Text style={styles.actionButtonText}>✨ MAGIA</Text>
            </TouchableOpacity>
            
            <TouchableOpacity
              style={[styles.actionButton, styles.fleeButton, selectedAction === CombatAction.FLEE && styles.selectedAction]}
              onPress={() => handleActionSelect(CombatAction.FLEE)}
            >
              <Text style={styles.actionButtonText}>🏃 FUGIR</Text>
            </TouchableOpacity>
          </View>
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

      {/* Modal de dados */}
      <Modal
        visible={showDiceModal}
        transparent={true}
        animationType="fade"
      >
        <View style={styles.diceModalOverlay}>
          <View style={styles.diceModalContent}>
            {currentDiceRoll && (
              <DiceRoller
                diceNotation="1d20"
                size="large"
                autoRoll={true}
                onRoll={(result) => setCurrentDiceRoll(result)}
              />
            )}
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
  header: {
    backgroundColor: 'rgba(139, 69, 19, 0.9)',
    padding: 15,
    borderBottomWidth: 2,
    borderBottomColor: '#D2691E',
  },
  combatInfo: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 10,
  },
  turnText: {
    color: '#FFD700',
    fontSize: 16,
    fontWeight: 'bold',
  },
  phaseText: {
    color: '#FFF',
    fontSize: 16,
    fontWeight: 'bold',
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
    maxHeight: 120,
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
    padding: 15,
    backgroundColor: 'rgba(139, 69, 19, 0.9)',
    borderTopWidth: 2,
    borderTopColor: '#D2691E',
  },
  actionRow: {
    flexDirection: 'row',
    marginBottom: 8,
    gap: 8,
  },
  actionButton: {
    flex: 1,
    backgroundColor: '#8B4513',
    borderWidth: 2,
    borderColor: '#D2691E',
    borderRadius: 8,
    paddingVertical: 12,
    alignItems: 'center',
  },
  selectedAction: {
    backgroundColor: '#FFD700',
    borderColor: '#FFA500',
  },
  fleeButton: {
    backgroundColor: '#666',
    borderColor: '#888',
  },
  actionButtonText: {
    color: '#FFD700',
    fontSize: 14,
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
  diceModalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.8)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  diceModalContent: {
    backgroundColor: '#2a2a2a',
    borderRadius: 12,
    padding: 30,
    borderWidth: 2,
    borderColor: '#D2691E',
  },
});
