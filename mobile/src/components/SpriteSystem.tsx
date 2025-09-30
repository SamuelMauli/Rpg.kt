import React, { useState, useEffect } from 'react';
import { View, Image, StyleSheet, Animated, Easing } from 'react-native';

export enum SpriteType {
  // Monstros
  GOBLIN = 'goblin',
  ORC = 'orc',
  SKELETON = 'skeleton',
  ZOMBIE = 'zombie',
  NECROMANCER = 'necromancer',
  
  // Personagens
  WARRIOR = 'warrior',
  ROGUE = 'rogue',
  CLERIC = 'cleric',
  MAGE = 'mage',
  
  // Itens
  SWORD = 'sword',
  SHIELD = 'shield',
  ARMOR = 'armor',
  POTION_RED = 'potion_red',
  SCROLL = 'scroll',
  TREASURE_CHEST = 'treasure_chest'
}

export enum AnimationType {
  IDLE = 'idle',
  ATTACK = 'attack',
  HURT = 'hurt',
  DEATH = 'death',
  CAST = 'cast',
  DEFEND = 'defend'
}

interface SpriteProps {
  type: SpriteType;
  animation?: AnimationType;
  size?: number;
  onAnimationComplete?: () => void;
  style?: any;
}

interface SpriteData {
  source: any;
  frames?: number;
  frameWidth?: number;
  frameHeight?: number;
  animationSpeed?: number;
}

const SPRITE_SOURCES: { [key in SpriteType]: SpriteData } = {
  // Monstros
  [SpriteType.GOBLIN]: {
    source: require('../../assets/sprites/goblin_sprite.png'),
    frames: 1,
    frameWidth: 32,
    frameHeight: 32
  },
  [SpriteType.ORC]: {
    source: require('../../assets/sprites/orc_sprite.png'),
    frames: 1,
    frameWidth: 32,
    frameHeight: 32
  },
  [SpriteType.SKELETON]: {
    source: require('../../assets/sprites/skeleton_sprite.png'),
    frames: 1,
    frameWidth: 32,
    frameHeight: 32
  },
  [SpriteType.ZOMBIE]: {
    source: require('../../assets/sprites/zombie_sprite.png'),
    frames: 1,
    frameWidth: 32,
    frameHeight: 32
  },
  [SpriteType.NECROMANCER]: {
    source: require('../../assets/sprites/necromancer_sprite.png'),
    frames: 1,
    frameWidth: 32,
    frameHeight: 32
  },
  
  // Personagens
  [SpriteType.WARRIOR]: {
    source: require('../../assets/sprites/warrior_sprite.png'),
    frames: 1,
    frameWidth: 32,
    frameHeight: 32
  },
  [SpriteType.ROGUE]: {
    source: require('../../assets/sprites/rogue_sprite.png'),
    frames: 1,
    frameWidth: 32,
    frameHeight: 32
  },
  [SpriteType.CLERIC]: {
    source: require('../../assets/sprites/cleric_sprite.png'),
    frames: 1,
    frameWidth: 32,
    frameHeight: 32
  },
  [SpriteType.MAGE]: {
    source: require('../../assets/sprites/mage_sprite.png'),
    frames: 1,
    frameWidth: 32,
    frameHeight: 32
  },
  
  // Itens
  [SpriteType.SWORD]: {
    source: require('../../assets/sprites/sword_icon.png'),
    frames: 1,
    frameWidth: 24,
    frameHeight: 24
  },
  [SpriteType.SHIELD]: {
    source: require('../../assets/sprites/shield_icon.png'),
    frames: 1,
    frameWidth: 24,
    frameHeight: 24
  },
  [SpriteType.ARMOR]: {
    source: require('../../assets/sprites/armor_icon.png'),
    frames: 1,
    frameWidth: 24,
    frameHeight: 24
  },
  [SpriteType.POTION_RED]: {
    source: require('../../assets/sprites/potion_red_icon.png'),
    frames: 1,
    frameWidth: 24,
    frameHeight: 24
  },
  [SpriteType.SCROLL]: {
    source: require('../../assets/sprites/scroll_icon.png'),
    frames: 1,
    frameWidth: 24,
    frameHeight: 24
  },
  [SpriteType.TREASURE_CHEST]: {
    source: require('../../assets/sprites/treasure_chest.png'),
    frames: 1,
    frameWidth: 32,
    frameHeight: 32
  }
};

export default function Sprite({ 
  type, 
  animation = AnimationType.IDLE, 
  size = 64, 
  onAnimationComplete,
  style 
}: SpriteProps) {
  const [currentFrame, setCurrentFrame] = useState(0);
  const [animationValue] = useState(new Animated.Value(0));
  const [isAnimating, setIsAnimating] = useState(false);

  const spriteData = SPRITE_SOURCES[type];

  useEffect(() => {
    if (animation !== AnimationType.IDLE && !isAnimating) {
      startAnimation();
    }
  }, [animation]);

  const startAnimation = () => {
    setIsAnimating(true);
    
    // Animações específicas baseadas no tipo
    switch (animation) {
      case AnimationType.ATTACK:
        animateAttack();
        break;
      case AnimationType.HURT:
        animateHurt();
        break;
      case AnimationType.DEATH:
        animateDeath();
        break;
      case AnimationType.CAST:
        animateCast();
        break;
      case AnimationType.DEFEND:
        animateDefend();
        break;
      default:
        setIsAnimating(false);
        break;
    }
  };

  const animateAttack = () => {
    Animated.sequence([
      // Movimento para frente
      Animated.timing(animationValue, {
        toValue: 1,
        duration: 150,
        easing: Easing.out(Easing.quad),
        useNativeDriver: true,
      }),
      // Pausa no ataque
      Animated.timing(animationValue, {
        toValue: 1,
        duration: 100,
        useNativeDriver: true,
      }),
      // Volta à posição original
      Animated.timing(animationValue, {
        toValue: 0,
        duration: 200,
        easing: Easing.in(Easing.quad),
        useNativeDriver: true,
      }),
    ]).start(() => {
      setIsAnimating(false);
      onAnimationComplete?.();
    });
  };

  const animateHurt = () => {
    Animated.sequence([
      // Shake effect
      Animated.timing(animationValue, {
        toValue: 1,
        duration: 100,
        useNativeDriver: true,
      }),
      Animated.timing(animationValue, {
        toValue: -1,
        duration: 100,
        useNativeDriver: true,
      }),
      Animated.timing(animationValue, {
        toValue: 1,
        duration: 100,
        useNativeDriver: true,
      }),
      Animated.timing(animationValue, {
        toValue: 0,
        duration: 100,
        useNativeDriver: true,
      }),
    ]).start(() => {
      setIsAnimating(false);
      onAnimationComplete?.();
    });
  };

  const animateDeath = () => {
    Animated.sequence([
      // Fade out
      Animated.timing(animationValue, {
        toValue: -2,
        duration: 500,
        easing: Easing.in(Easing.quad),
        useNativeDriver: true,
      }),
    ]).start(() => {
      setIsAnimating(false);
      onAnimationComplete?.();
    });
  };

  const animateCast = () => {
    Animated.sequence([
      // Glow effect
      Animated.timing(animationValue, {
        toValue: 2,
        duration: 300,
        easing: Easing.inOut(Easing.sin),
        useNativeDriver: true,
      }),
      Animated.timing(animationValue, {
        toValue: 0,
        duration: 300,
        easing: Easing.inOut(Easing.sin),
        useNativeDriver: true,
      }),
    ]).start(() => {
      setIsAnimating(false);
      onAnimationComplete?.();
    });
  };

  const animateDefend = () => {
    Animated.sequence([
      // Movimento defensivo
      Animated.timing(animationValue, {
        toValue: -0.5,
        duration: 200,
        easing: Easing.out(Easing.quad),
        useNativeDriver: true,
      }),
      Animated.timing(animationValue, {
        toValue: 0,
        duration: 300,
        easing: Easing.in(Easing.quad),
        useNativeDriver: true,
      }),
    ]).start(() => {
      setIsAnimating(false);
      onAnimationComplete?.();
    });
  };

  const getAnimatedStyle = () => {
    const baseTransform = [];

    switch (animation) {
      case AnimationType.ATTACK:
        baseTransform.push({
          translateX: animationValue.interpolate({
            inputRange: [0, 1],
            outputRange: [0, 10],
          }),
        });
        break;

      case AnimationType.HURT:
        baseTransform.push({
          translateX: animationValue.interpolate({
            inputRange: [-1, 0, 1],
            outputRange: [-5, 0, 5],
          }),
        });
        break;

      case AnimationType.DEATH:
        baseTransform.push({
          scale: animationValue.interpolate({
            inputRange: [-2, 0],
            outputRange: [0, 1],
          }),
        });
        break;

      case AnimationType.CAST:
        baseTransform.push({
          scale: animationValue.interpolate({
            inputRange: [0, 2],
            outputRange: [1, 1.2],
          }),
        });
        break;

      case AnimationType.DEFEND:
        baseTransform.push({
          translateX: animationValue.interpolate({
            inputRange: [-0.5, 0],
            outputRange: [-8, 0],
          }),
        });
        break;
    }

    return {
      transform: baseTransform,
    };
  };

  const getFilterStyle = () => {
    switch (animation) {
      case AnimationType.HURT:
        return {
          tintColor: isAnimating ? '#FF6B6B' : undefined,
        };
      case AnimationType.CAST:
        return {
          tintColor: isAnimating ? '#4ECDC4' : undefined,
        };
      case AnimationType.DEFEND:
        return {
          tintColor: isAnimating ? '#45B7D1' : undefined,
        };
      default:
        return {};
    }
  };

  return (
    <Animated.View 
      style={[
        styles.container,
        { width: size, height: size },
        getAnimatedStyle(),
        style
      ]}
    >
      <Image
        source={spriteData.source}
        style={[
          styles.sprite,
          {
            width: size,
            height: size,
          },
          getFilterStyle()
        ]}
        resizeMode="contain"
      />
      
      {/* Efeitos visuais adicionais */}
      {animation === AnimationType.CAST && isAnimating && (
        <View style={[styles.magicEffect, { width: size, height: size }]} />
      )}
      
      {animation === AnimationType.DEFEND && isAnimating && (
        <View style={[styles.shieldEffect, { width: size, height: size }]} />
      )}
    </Animated.View>
  );
}

// Componente para sprites animados em loop
interface AnimatedSpriteProps extends SpriteProps {
  frameRate?: number;
  loop?: boolean;
}

export function AnimatedSprite({ 
  frameRate = 8, 
  loop = true, 
  ...props 
}: AnimatedSpriteProps) {
  const [currentFrame, setCurrentFrame] = useState(0);
  const spriteData = SPRITE_SOURCES[props.type];

  useEffect(() => {
    if (!spriteData.frames || spriteData.frames <= 1) return;

    const interval = setInterval(() => {
      setCurrentFrame(prev => {
        const nextFrame = prev + 1;
        if (nextFrame >= spriteData.frames!) {
          if (loop) {
            return 0;
          } else {
            clearInterval(interval);
            return prev;
          }
        }
        return nextFrame;
      });
    }, 1000 / frameRate);

    return () => clearInterval(interval);
  }, [spriteData.frames, frameRate, loop]);

  return <Sprite {...props} />;
}

// Componente para exibir múltiplos sprites em formação
interface SpriteFormationProps {
  sprites: Array<{
    type: SpriteType;
    animation?: AnimationType;
    position: { x: number; y: number };
  }>;
  containerSize: { width: number; height: number };
}

export function SpriteFormation({ sprites, containerSize }: SpriteFormationProps) {
  return (
    <View style={[styles.formation, containerSize]}>
      {sprites.map((sprite, index) => (
        <View
          key={index}
          style={[
            styles.spritePosition,
            {
              left: sprite.position.x,
              top: sprite.position.y,
            }
          ]}
        >
          <Sprite
            type={sprite.type}
            animation={sprite.animation}
            size={48}
          />
        </View>
      ))}
    </View>
  );
}

// Hook para gerenciar animações de combate
export function useCombatAnimations() {
  const [currentAnimation, setCurrentAnimation] = useState<AnimationType>(AnimationType.IDLE);
  const [isAnimating, setIsAnimating] = useState(false);

  const playAnimation = (animation: AnimationType) => {
    if (isAnimating) return;
    
    setCurrentAnimation(animation);
    setIsAnimating(true);
  };

  const onAnimationComplete = () => {
    setCurrentAnimation(AnimationType.IDLE);
    setIsAnimating(false);
  };

  return {
    currentAnimation,
    isAnimating,
    playAnimation,
    onAnimationComplete
  };
}

const styles = StyleSheet.create({
  container: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  sprite: {
    // Aplicar filtro pixelado para manter o estilo 8-bit
  },
  magicEffect: {
    position: 'absolute',
    backgroundColor: 'rgba(78, 205, 196, 0.3)',
    borderRadius: 50,
    borderWidth: 2,
    borderColor: '#4ECDC4',
  },
  shieldEffect: {
    position: 'absolute',
    backgroundColor: 'rgba(69, 183, 209, 0.2)',
    borderRadius: 50,
    borderWidth: 2,
    borderColor: '#45B7D1',
  },
  formation: {
    position: 'relative',
  },
  spritePosition: {
    position: 'absolute',
  },
});

// Utilitários para sprites
export const SpriteUtils = {
  /**
   * Retorna o sprite apropriado para uma classe de personagem
   */
  getSpriteForClass: (className: string): SpriteType => {
    switch (className.toLowerCase()) {
      case 'guerreiro':
        return SpriteType.WARRIOR;
      case 'ladrao':
        return SpriteType.ROGUE;
      case 'clerigo':
        return SpriteType.CLERIC;
      case 'mago':
        return SpriteType.MAGE;
      default:
        return SpriteType.WARRIOR;
    }
  },

  /**
   * Retorna o sprite apropriado para um tipo de monstro
   */
  getSpriteForMonster: (monsterName: string): SpriteType => {
    const name = monsterName.toLowerCase();
    
    if (name.includes('goblin')) return SpriteType.GOBLIN;
    if (name.includes('orc')) return SpriteType.ORC;
    if (name.includes('skeleton') || name.includes('esqueleto')) return SpriteType.SKELETON;
    if (name.includes('zombie') || name.includes('zumbi')) return SpriteType.ZOMBIE;
    if (name.includes('necromancer') || name.includes('necromante')) return SpriteType.NECROMANCER;
    
    return SpriteType.GOBLIN; // Default
  },

  /**
   * Retorna o sprite apropriado para um item
   */
  getSpriteForItem: (itemType: string): SpriteType => {
    const type = itemType.toLowerCase();
    
    if (type.includes('sword') || type.includes('espada')) return SpriteType.SWORD;
    if (type.includes('shield') || type.includes('escudo')) return SpriteType.SHIELD;
    if (type.includes('armor') || type.includes('armadura')) return SpriteType.ARMOR;
    if (type.includes('potion') || type.includes('pocao')) return SpriteType.POTION_RED;
    if (type.includes('scroll') || type.includes('pergaminho')) return SpriteType.SCROLL;
    if (type.includes('chest') || type.includes('bau')) return SpriteType.TREASURE_CHEST;
    
    return SpriteType.SWORD; // Default
  },

  /**
   * Cria uma formação de combate
   */
  createCombatFormation: (
    playerSprites: SpriteType[], 
    enemySprites: SpriteType[]
  ): Array<{
    type: SpriteType;
    animation: AnimationType;
    position: { x: number; y: number };
  }> => {
    const formation = [];
    
    // Posicionar jogadores à esquerda
    playerSprites.forEach((sprite, index) => {
      formation.push({
        type: sprite,
        animation: AnimationType.IDLE,
        position: {
          x: 50,
          y: 100 + (index * 80)
        }
      });
    });
    
    // Posicionar inimigos à direita
    enemySprites.forEach((sprite, index) => {
      formation.push({
        type: sprite,
        animation: AnimationType.IDLE,
        position: {
          x: 250,
          y: 100 + (index * 80)
        }
      });
    });
    
    return formation;
  }
};
