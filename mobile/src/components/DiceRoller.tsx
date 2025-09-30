import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Animated,
  Dimensions,
} from 'react-native';
import { DiceSystem, DiceRoll } from '../utils/DiceSystem';

interface DiceRollerProps {
  diceNotation: string;
  modifier?: number;
  onRoll?: (result: DiceRoll) => void;
  disabled?: boolean;
  label?: string;
  size?: 'small' | 'medium' | 'large';
  autoRoll?: boolean;
}

const { width } = Dimensions.get('window');

export default function DiceRoller({
  diceNotation,
  modifier = 0,
  onRoll,
  disabled = false,
  label,
  size = 'medium',
  autoRoll = false
}: DiceRollerProps) {
  const [isRolling, setIsRolling] = useState(false);
  const [lastRoll, setLastRoll] = useState<DiceRoll | null>(null);
  const [animatedValue] = useState(new Animated.Value(0));
  const [currentDisplayValue, setCurrentDisplayValue] = useState(1);

  const diceSystem = DiceSystem.getInstance();

  const sizeStyles = {
    small: { size: 40, fontSize: 16 },
    medium: { size: 60, fontSize: 24 },
    large: { size: 80, fontSize: 32 }
  };

  const currentSize = sizeStyles[size];

  useEffect(() => {
    if (autoRoll) {
      handleRoll();
    }
  }, [autoRoll]);

  const handleRoll = () => {
    if (disabled || isRolling) return;

    setIsRolling(true);
    
    // Animação de rolagem
    const rollDuration = 800;
    const updateInterval = 50;
    const updates = rollDuration / updateInterval;
    
    let currentUpdate = 0;
    
    const rollAnimation = setInterval(() => {
      currentUpdate++;
      
      // Gera valores aleatórios durante a animação
      const maxValue = parseInt(diceNotation.split('d')[1]) || 20;
      setCurrentDisplayValue(Math.floor(Math.random() * maxValue) + 1);
      
      if (currentUpdate >= updates) {
        clearInterval(rollAnimation);
        
        // Rola o dado real
        const result = diceSystem.roll(diceNotation, modifier);
        setLastRoll(result);
        setCurrentDisplayValue(result.total);
        setIsRolling(false);
        
        if (onRoll) {
          onRoll(result);
        }
      }
    }, updateInterval);

    // Animação de escala
    Animated.sequence([
      Animated.timing(animatedValue, {
        toValue: 1.2,
        duration: 200,
        useNativeDriver: true,
      }),
      Animated.timing(animatedValue, {
        toValue: 1,
        duration: 600,
        useNativeDriver: true,
      }),
    ]).start();
  };

  const getDiceColor = () => {
    if (!lastRoll) return '#8B4513';
    
    if (lastRoll.critical) return '#FFD700';
    if (lastRoll.fumble) return '#FF4444';
    return '#8B4513';
  };

  const getDiceTextColor = () => {
    if (!lastRoll) return '#FFF';
    
    if (lastRoll.critical) return '#000';
    if (lastRoll.fumble) return '#FFF';
    return '#FFF';
  };

  const getResultText = () => {
    if (!lastRoll) return '';
    
    let text = '';
    if (lastRoll.critical) text = 'CRÍTICO!';
    else if (lastRoll.fumble) text = 'FALHA!';
    else text = `Total: ${lastRoll.total}`;
    
    return text;
  };

  return (
    <View style={styles.container}>
      {label && (
        <Text style={styles.label}>{label}</Text>
      )}
      
      <TouchableOpacity
        style={[
          styles.dice,
          {
            width: currentSize.size,
            height: currentSize.size,
            backgroundColor: getDiceColor(),
          },
          disabled && styles.disabled,
          isRolling && styles.rolling
        ]}
        onPress={handleRoll}
        disabled={disabled || isRolling}
      >
        <Animated.View
          style={[
            styles.diceInner,
            {
              transform: [
                { scale: animatedValue },
                { 
                  rotateZ: isRolling 
                    ? animatedValue.interpolate({
                        inputRange: [0, 1],
                        outputRange: ['0deg', '360deg'],
                      })
                    : '0deg'
                }
              ]
            }
          ]}
        >
          <Text 
            style={[
              styles.diceText, 
              { 
                fontSize: currentSize.fontSize,
                color: getDiceTextColor()
              }
            ]}
          >
            {isRolling ? currentDisplayValue : (lastRoll?.total || '?')}
          </Text>
        </Animated.View>
      </TouchableOpacity>

      {lastRoll && (
        <View style={styles.resultContainer}>
          <Text style={[
            styles.resultText,
            lastRoll.critical && styles.criticalText,
            lastRoll.fumble && styles.fumbleText
          ]}>
            {getResultText()}
          </Text>
          
          <Text style={styles.detailText}>
            {diceSystem.formatRoll(lastRoll)}
          </Text>
        </View>
      )}

      {isRolling && (
        <Text style={styles.rollingText}>Rolando...</Text>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    margin: 10,
  },
  label: {
    color: '#FFD700',
    fontSize: 14,
    fontWeight: 'bold',
    marginBottom: 8,
    textAlign: 'center',
  },
  dice: {
    borderRadius: 12,
    borderWidth: 3,
    borderColor: '#D2691E',
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 4,
    elevation: 8,
  },
  diceInner: {
    justifyContent: 'center',
    alignItems: 'center',
    width: '100%',
    height: '100%',
  },
  diceText: {
    fontWeight: 'bold',
    textShadowColor: '#000',
    textShadowOffset: { width: 1, height: 1 },
    textShadowRadius: 2,
  },
  disabled: {
    opacity: 0.5,
    backgroundColor: '#666',
  },
  rolling: {
    borderColor: '#FFD700',
  },
  resultContainer: {
    marginTop: 8,
    alignItems: 'center',
  },
  resultText: {
    color: '#FFF',
    fontSize: 16,
    fontWeight: 'bold',
    textAlign: 'center',
  },
  criticalText: {
    color: '#FFD700',
    textShadowColor: '#000',
    textShadowOffset: { width: 1, height: 1 },
    textShadowRadius: 2,
  },
  fumbleText: {
    color: '#FF4444',
    textShadowColor: '#000',
    textShadowOffset: { width: 1, height: 1 },
    textShadowRadius: 2,
  },
  detailText: {
    color: '#CCC',
    fontSize: 12,
    marginTop: 4,
    textAlign: 'center',
  },
  rollingText: {
    color: '#FFD700',
    fontSize: 12,
    fontStyle: 'italic',
    marginTop: 4,
  },
});
