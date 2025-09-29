import React from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  ImageBackground,
  Image,
  Dimensions,
} from 'react-native';
import { EstadoJogo } from '../types/GameTypes';
import { useGame } from '../contexts/GameContext';

interface MainMenuScreenProps {
  onNavigate: (screen: EstadoJogo) => void;
}

const { width, height } = Dimensions.get('window');

export default function MainMenuScreen({ onNavigate }: MainMenuScreenProps) {
  const { state, resetGame } = useGame();

  const handleNewGame = () => {
    resetGame();
    onNavigate(EstadoJogo.CRIACAO_PERSONAGEM);
  };

  const handleContinueGame = () => {
    if (state.personagem) {
      onNavigate(EstadoJogo.AVENTURA);
    } else {
      handleNewGame();
    }
  };

  return (
    <ImageBackground
      source={require('../../assets/sprites/cave_entrance_bg.png')}
      style={styles.background}
      resizeMode="cover"
    >
      <View style={styles.overlay}>
        <View style={styles.container}>
          {/* Logo do jogo */}
          <View style={styles.logoContainer}>
            <Image
              source={require('../../assets/sprites/game_logo.png')}
              style={styles.logo}
              resizeMode="contain"
            />
          </View>

          {/* Menu de opções */}
          <View style={styles.menuContainer}>
            <TouchableOpacity
              style={styles.menuButton}
              onPress={handleNewGame}
            >
              <Text style={styles.menuButtonText}>NOVO JOGO</Text>
            </TouchableOpacity>

            {state.personagem && (
              <TouchableOpacity
                style={styles.menuButton}
                onPress={handleContinueGame}
              >
                <Text style={styles.menuButtonText}>CONTINUAR</Text>
              </TouchableOpacity>
            )}

            <TouchableOpacity
              style={styles.menuButton}
              onPress={() => onNavigate(EstadoJogo.STATUS)}
            >
              <Text style={styles.menuButtonText}>SOBRE</Text>
            </TouchableOpacity>
          </View>

          {/* Informações do personagem atual */}
          {state.personagem && (
            <View style={styles.characterInfo}>
              <Text style={styles.characterInfoText}>
                {state.personagem.nome} - Nível {state.personagem.nivel}
              </Text>
              <Text style={styles.characterInfoText}>
                {state.personagem.raca.getNome()} {state.personagem.classe.getNome()}
              </Text>
            </View>
          )}

          {/* Versão */}
          <View style={styles.versionContainer}>
            <Text style={styles.versionText}>Old Dragon RPG Mobile v1.0</Text>
          </View>
        </View>
      </View>
    </ImageBackground>
  );
}

const styles = StyleSheet.create({
  background: {
    flex: 1,
    width: '100%',
    height: '100%',
  },
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
  },
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  logoContainer: {
    marginBottom: 50,
    alignItems: 'center',
  },
  logo: {
    width: width * 0.8,
    height: width * 0.4,
    maxWidth: 400,
    maxHeight: 200,
  },
  menuContainer: {
    width: '100%',
    maxWidth: 300,
    alignItems: 'center',
  },
  menuButton: {
    backgroundColor: '#8B4513',
    borderWidth: 3,
    borderColor: '#D2691E',
    borderRadius: 8,
    paddingVertical: 15,
    paddingHorizontal: 30,
    marginVertical: 8,
    width: '100%',
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: {
      width: 2,
      height: 2,
    },
    shadowOpacity: 0.8,
    shadowRadius: 3,
    elevation: 5,
  },
  menuButtonText: {
    color: '#FFD700',
    fontSize: 18,
    fontWeight: 'bold',
    textShadowColor: '#000',
    textShadowOffset: { width: 1, height: 1 },
    textShadowRadius: 2,
  },
  characterInfo: {
    marginTop: 30,
    padding: 15,
    backgroundColor: 'rgba(139, 69, 19, 0.8)',
    borderRadius: 8,
    borderWidth: 2,
    borderColor: '#D2691E',
    alignItems: 'center',
  },
  characterInfoText: {
    color: '#FFD700',
    fontSize: 14,
    fontWeight: 'bold',
    textAlign: 'center',
    marginVertical: 2,
  },
  versionContainer: {
    position: 'absolute',
    bottom: 20,
    alignItems: 'center',
  },
  versionText: {
    color: '#888',
    fontSize: 12,
    textAlign: 'center',
  },
});
