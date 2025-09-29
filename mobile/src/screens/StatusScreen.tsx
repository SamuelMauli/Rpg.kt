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

interface StatusScreenProps {
  onNavigate: (screen: EstadoJogo) => void;
}

export default function StatusScreen({ onNavigate }: StatusScreenProps) {
  const { state } = useGame();

  const renderAboutScreen = () => (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Sobre o Jogo</Text>
        <TouchableOpacity
          style={styles.backButton}
          onPress={() => onNavigate(EstadoJogo.MENU_PRINCIPAL)}
        >
          <Text style={styles.buttonText}>Voltar</Text>
        </TouchableOpacity>
      </View>

      <ScrollView style={styles.content}>
        <View style={styles.aboutSection}>
          <Image
            source={require('../../assets/sprites/game_logo.png')}
            style={styles.aboutLogo}
            resizeMode="contain"
          />
          
          <Text style={styles.aboutTitle}>Old Dragon RPG Mobile</Text>
          <Text style={styles.aboutVersion}>Versão 1.0</Text>
          
          <Text style={styles.aboutDescription}>
            Um RPG completo baseado nas regras do Old Dragon 2, adaptado para dispositivos móveis.
            Embarque na aventura "O Segredo da Montanha Gelada" e enfrente o necromante Malachar!
          </Text>

          <View style={styles.featuresSection}>
            <Text style={styles.sectionTitle}>Características:</Text>
            <Text style={styles.featureText}>• Sistema de regras completo do Old Dragon 2</Text>
            <Text style={styles.featureText}>• 4 raças jogáveis (Humano, Elfo, Anão, Halfling)</Text>
            <Text style={styles.featureText}>• 4 classes (Guerreiro, Ladrão, Clérigo, Mago)</Text>
            <Text style={styles.featureText}>• Sistema de combate por turnos</Text>
            <Text style={styles.featureText}>• Aventura narrativa completa</Text>
            <Text style={styles.featureText}>• Arte pixel 8-bit autêntica</Text>
          </View>

          <View style={styles.creditsSection}>
            <Text style={styles.sectionTitle}>Créditos:</Text>
            <Text style={styles.creditText}>Sistema de Regras: Old Dragon 2</Text>
            <Text style={styles.creditText}>Desenvolvimento: Manus AI</Text>
            <Text style={styles.creditText}>Arte: Geração de sprites 8-bit</Text>
          </View>
        </View>
      </ScrollView>
    </View>
  );

  if (!state.personagem) {
    return renderAboutScreen();
  }

  const { personagem } = state;

  const getCharacterSprite = () => {
    switch (personagem.classe.getNome().toLowerCase()) {
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

  const getExperienceToNext = () => {
    const progressao = personagem.classe.getProgressaoExperiencia();
    const proximoNivel = progressao.get(personagem.nivel + 1);
    return proximoNivel ? proximoNivel.experienciaMinima - personagem.experiencia : 0;
  };

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Status do Personagem</Text>
        <TouchableOpacity
          style={styles.backButton}
          onPress={() => onNavigate(EstadoJogo.AVENTURA)}
        >
          <Text style={styles.buttonText}>Voltar</Text>
        </TouchableOpacity>
      </View>

      <ScrollView style={styles.content}>
        {/* Informações básicas */}
        <View style={styles.characterHeader}>
          <Image source={getCharacterSprite()} style={styles.characterSprite} />
          <View style={styles.basicInfo}>
            <Text style={styles.characterName}>{personagem.nome}</Text>
            <Text style={styles.characterClass}>
              {personagem.raca.getNome()} {personagem.classe.getNome()}
            </Text>
            <Text style={styles.characterLevel}>Nível {personagem.nivel}</Text>
          </View>
        </View>

        {/* Pontos de vida e experiência */}
        <View style={styles.vitalStats}>
          <View style={styles.statBar}>
            <Text style={styles.statLabel}>Pontos de Vida</Text>
            <View style={styles.healthBarBg}>
              <View 
                style={[
                  styles.healthBarFill, 
                  { width: `${(personagem.pontosVida / personagem.pontosVidaMaximos) * 100}%` }
                ]} 
              />
            </View>
            <Text style={styles.statValue}>
              {personagem.pontosVida}/{personagem.pontosVidaMaximos}
            </Text>
          </View>

          <View style={styles.experienceInfo}>
            <Text style={styles.statLabel}>Experiência</Text>
            <Text style={styles.statValue}>{personagem.experiencia} XP</Text>
            <Text style={styles.nextLevelText}>
              Próximo nível: {getExperienceToNext()} XP
            </Text>
          </View>
        </View>

        {/* Atributos */}
        <View style={styles.attributesSection}>
          <Text style={styles.sectionTitle}>Atributos</Text>
          <View style={styles.attributesGrid}>
            {Object.entries(personagem.atributos).map(([attr, value]) => (
              <View key={attr} style={styles.attributeItem}>
                <Text style={styles.attributeName}>
                  {attr.charAt(0).toUpperCase() + attr.slice(1)}
                </Text>
                <Text style={styles.attributeValue}>{value}</Text>
                <Text style={styles.attributeModifier}>
                  ({personagem.modificadores[attr as keyof typeof personagem.modificadores] >= 0 ? '+' : ''}
                  {personagem.modificadores[attr as keyof typeof personagem.modificadores]})
                </Text>
              </View>
            ))}
          </View>
        </View>

        {/* Estatísticas de combate */}
        <View style={styles.combatStats}>
          <Text style={styles.sectionTitle}>Combate</Text>
          <View style={styles.combatGrid}>
            <View style={styles.combatStat}>
              <Text style={styles.combatLabel}>Classe de Armadura</Text>
              <Text style={styles.combatValue}>{personagem.classeArmadura}</Text>
            </View>
            <View style={styles.combatStat}>
              <Text style={styles.combatLabel}>Base de Ataque</Text>
              <Text style={styles.combatValue}>{personagem.baseAtaque}</Text>
            </View>
            <View style={styles.combatStat}>
              <Text style={styles.combatLabel}>Movimento</Text>
              <Text style={styles.combatValue}>{personagem.movimento}m</Text>
            </View>
          </View>
        </View>

        {/* Habilidades */}
        <View style={styles.abilitiesSection}>
          <Text style={styles.sectionTitle}>Habilidades</Text>
          {personagem.habilidades.map((habilidade, index) => (
            <View key={index} style={styles.abilityItem}>
              <Text style={styles.abilityName}>{habilidade.nome}</Text>
              <Text style={styles.abilityDescription}>{habilidade.descricao}</Text>
            </View>
          ))}
        </View>
      </ScrollView>
    </View>
  );
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
  buttonText: {
    color: '#FFD700',
    fontSize: 14,
    fontWeight: 'bold',
  },
  content: {
    flex: 1,
    padding: 20,
  },
  characterHeader: {
    flexDirection: 'row',
    backgroundColor: '#333',
    borderRadius: 8,
    padding: 15,
    marginBottom: 20,
    borderWidth: 2,
    borderColor: '#555',
    alignItems: 'center',
  },
  characterSprite: {
    width: 80,
    height: 80,
    marginRight: 15,
    resizeMode: 'contain',
  },
  basicInfo: {
    flex: 1,
  },
  characterName: {
    color: '#FFD700',
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 4,
  },
  characterClass: {
    color: '#FFF',
    fontSize: 16,
    marginBottom: 4,
  },
  characterLevel: {
    color: '#CCC',
    fontSize: 14,
  },
  vitalStats: {
    backgroundColor: '#333',
    borderRadius: 8,
    padding: 15,
    marginBottom: 20,
    borderWidth: 2,
    borderColor: '#555',
  },
  statBar: {
    marginBottom: 15,
  },
  statLabel: {
    color: '#FFD700',
    fontSize: 14,
    fontWeight: 'bold',
    marginBottom: 5,
  },
  healthBarBg: {
    width: '100%',
    height: 20,
    backgroundColor: '#222',
    borderRadius: 10,
    borderWidth: 1,
    borderColor: '#666',
    overflow: 'hidden',
    marginBottom: 5,
  },
  healthBarFill: {
    height: '100%',
    backgroundColor: '#FF4444',
    borderRadius: 9,
  },
  statValue: {
    color: '#FFF',
    fontSize: 14,
    textAlign: 'center',
  },
  experienceInfo: {
    alignItems: 'center',
  },
  nextLevelText: {
    color: '#CCC',
    fontSize: 12,
    marginTop: 4,
  },
  attributesSection: {
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
    marginBottom: 15,
    textAlign: 'center',
  },
  attributesGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  },
  attributeItem: {
    width: '48%',
    alignItems: 'center',
    marginBottom: 15,
    padding: 10,
    backgroundColor: '#222',
    borderRadius: 6,
  },
  attributeName: {
    color: '#CCC',
    fontSize: 12,
    marginBottom: 4,
  },
  attributeValue: {
    color: '#FFD700',
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 2,
  },
  attributeModifier: {
    color: '#FFF',
    fontSize: 12,
  },
  combatStats: {
    backgroundColor: '#333',
    borderRadius: 8,
    padding: 15,
    marginBottom: 20,
    borderWidth: 2,
    borderColor: '#555',
  },
  combatGrid: {
    flexDirection: 'row',
    justifyContent: 'space-around',
  },
  combatStat: {
    alignItems: 'center',
  },
  combatLabel: {
    color: '#CCC',
    fontSize: 12,
    textAlign: 'center',
    marginBottom: 4,
  },
  combatValue: {
    color: '#FFD700',
    fontSize: 16,
    fontWeight: 'bold',
  },
  abilitiesSection: {
    backgroundColor: '#333',
    borderRadius: 8,
    padding: 15,
    marginBottom: 20,
    borderWidth: 2,
    borderColor: '#555',
  },
  abilityItem: {
    marginBottom: 12,
    padding: 10,
    backgroundColor: '#222',
    borderRadius: 6,
  },
  abilityName: {
    color: '#FFD700',
    fontSize: 14,
    fontWeight: 'bold',
    marginBottom: 4,
  },
  abilityDescription: {
    color: '#CCC',
    fontSize: 12,
    lineHeight: 16,
  },
  // Estilos para tela "Sobre"
  aboutSection: {
    alignItems: 'center',
    padding: 20,
  },
  aboutLogo: {
    width: 200,
    height: 100,
    marginBottom: 20,
  },
  aboutTitle: {
    color: '#FFD700',
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 8,
    textAlign: 'center',
  },
  aboutVersion: {
    color: '#CCC',
    fontSize: 16,
    marginBottom: 20,
    textAlign: 'center',
  },
  aboutDescription: {
    color: '#FFF',
    fontSize: 14,
    lineHeight: 20,
    textAlign: 'center',
    marginBottom: 30,
  },
  featuresSection: {
    width: '100%',
    marginBottom: 30,
  },
  featureText: {
    color: '#CCC',
    fontSize: 14,
    marginBottom: 8,
    lineHeight: 18,
  },
  creditsSection: {
    width: '100%',
  },
  creditText: {
    color: '#CCC',
    fontSize: 14,
    marginBottom: 6,
    lineHeight: 18,
  },
});
