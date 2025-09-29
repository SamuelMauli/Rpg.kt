import React, { useState, useEffect } from 'react';
import { StyleSheet, View, StatusBar } from 'react-native';

import { GameProvider } from './src/contexts/GameContext';
import { EstadoJogo } from './src/types/GameTypes';
import MainMenuScreen from './src/screens/MainMenuScreen';
import CharacterCreationScreen from './src/screens/CharacterCreationScreen';
import AdventureScreen from './src/screens/AdventureScreen';
import CombatScreen from './src/screens/CombatScreen';
import InventoryScreen from './src/screens/InventoryScreen';
import StatusScreen from './src/screens/StatusScreen';

export default function App() {
  const [currentScreen, setCurrentScreen] = useState<EstadoJogo>(EstadoJogo.MENU_PRINCIPAL);

  const renderCurrentScreen = () => {
    switch (currentScreen) {
      case EstadoJogo.MENU_PRINCIPAL:
        return <MainMenuScreen onNavigate={setCurrentScreen} />;
      case EstadoJogo.CRIACAO_PERSONAGEM:
        return <CharacterCreationScreen onNavigate={setCurrentScreen} />;
      case EstadoJogo.AVENTURA:
        return <AdventureScreen onNavigate={setCurrentScreen} />;
      case EstadoJogo.COMBATE:
        return <CombatScreen onNavigate={setCurrentScreen} />;
      case EstadoJogo.INVENTARIO:
        return <InventoryScreen onNavigate={setCurrentScreen} />;
      case EstadoJogo.STATUS:
        return <StatusScreen onNavigate={setCurrentScreen} />;
      default:
        return <MainMenuScreen onNavigate={setCurrentScreen} />;
    }
  };

  return (
    <GameProvider>
      <View style={styles.container}>
        <StatusBar barStyle="light-content" backgroundColor="#000" />
        {renderCurrentScreen()}
      </View>
    </GameProvider>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#000',
  },
});
