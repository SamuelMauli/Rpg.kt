import React, { useState, useEffect } from 'react';
import { StyleSheet, View, StatusBar, Alert } from 'react-native';

import { GameProvider } from './src/contexts/GameContext';
import { EstadoJogo } from './src/types/GameTypes';

// Telas originais
import MainMenuScreen from './src/screens/MainMenuScreen';
import CharacterCreationScreen from './src/screens/CharacterCreationScreen';
import AdventureScreen from './src/screens/AdventureScreen';
import CombatScreen from './src/screens/CombatScreen';
import InventoryScreen from './src/screens/InventoryScreen';
import StatusScreen from './src/screens/StatusScreen';

// Telas melhoradas
import EnhancedCharacterCreationScreen from './src/screens/EnhancedCharacterCreationScreen';
import EnhancedCombatScreen from './src/screens/EnhancedCombatScreen';
import EnhancedInventoryScreen from './src/screens/EnhancedInventoryScreen';
import DungeonExplorationScreen from './src/screens/DungeonExplorationScreen';

// Utilitários de teste
import { getTestUtils, TestSuite } from './src/utils/TestUtils';

export default function App() {
  const [currentScreen, setCurrentScreen] = useState<EstadoJogo>(EstadoJogo.MENU_PRINCIPAL);
  const [useEnhancedScreens, setUseEnhancedScreens] = useState(true);
  const [isTestMode, setIsTestMode] = useState(__DEV__); // Ativar testes apenas em desenvolvimento

  useEffect(() => {
    // Executar testes na inicialização (apenas em desenvolvimento)
    if (isTestMode) {
      runInitialTests();
    }
  }, []);

  const runInitialTests = async () => {
    try {
      const testUtils = getTestUtils();
      const testResults: TestSuite = testUtils.runAllTests();
      
      console.log('=== RESULTADOS DOS TESTES RPG.kt ===');
      console.log(`Total: ${testResults.summary.totalPassed}/${testResults.summary.totalTests} (${testResults.summary.successRate.toFixed(1)}%)`);
      
      testResults.results.forEach(result => {
        console.log(`\n${result.category}: ${result.passed}/${result.total}`);
        result.results.forEach(line => console.log(line));
      });

      // Executar testes de performance
      const performanceResults = testUtils.runPerformanceTests();
      console.log('\n=== TESTES DE PERFORMANCE ===');
      performanceResults.tests.forEach(test => {
        console.log(`${test.name}: ${test.duration}ms (${test.opsPerSecond.toFixed(2)} ops/sec)`);
      });

      // Alertar se houver falhas críticas
      if (testResults.summary.successRate < 80) {
        Alert.alert(
          'Testes Falharam',
          `Apenas ${testResults.summary.successRate.toFixed(1)}% dos testes passaram. Verifique o console para detalhes.`,
          [{ text: 'OK' }]
        );
      } else {
        console.log('✅ Todos os sistemas funcionando corretamente!');
      }
    } catch (error) {
      console.error('Erro ao executar testes:', error);
    }
  };

  const handleNavigate = (screen: EstadoJogo) => {
    setCurrentScreen(screen);
  };

  const renderCurrentScreen = () => {
    switch (currentScreen) {
      case EstadoJogo.MENU_PRINCIPAL:
        return (
          <MainMenuScreen 
            onNavigate={handleNavigate}
            // Adicionar props para controlar versões melhoradas
            onToggleEnhanced={() => setUseEnhancedScreens(!useEnhancedScreens)}
            useEnhanced={useEnhancedScreens}
          />
        );

      case EstadoJogo.CRIACAO_PERSONAGEM:
        return useEnhancedScreens ? (
          <EnhancedCharacterCreationScreen onNavigate={handleNavigate} />
        ) : (
          <CharacterCreationScreen onNavigate={handleNavigate} />
        );

      case EstadoJogo.AVENTURA:
        return <AdventureScreen onNavigate={handleNavigate} />;

      case EstadoJogo.COMBATE:
        return useEnhancedScreens ? (
          <EnhancedCombatScreen onNavigate={handleNavigate} />
        ) : (
          <CombatScreen onNavigate={handleNavigate} />
        );

      case EstadoJogo.INVENTARIO:
        return useEnhancedScreens ? (
          <EnhancedInventoryScreen onNavigate={handleNavigate} />
        ) : (
          <InventoryScreen onNavigate={handleNavigate} />
        );

      case EstadoJogo.STATUS:
        return <StatusScreen onNavigate={handleNavigate} />;

      // Nova tela de exploração de dungeon
      case EstadoJogo.DUNGEON:
        return <DungeonExplorationScreen onNavigate={handleNavigate} />;

      default:
        return <MainMenuScreen onNavigate={handleNavigate} />;
    }
  };

  return (
    <GameProvider>
      <View style={styles.container}>
        <StatusBar 
          barStyle="light-content" 
          backgroundColor="#1a1a1a" 
          translucent={false}
        />
        {renderCurrentScreen()}
      </View>
    </GameProvider>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#1a1a1a',
  },
});
