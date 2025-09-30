import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
  TextInput,
  Alert,
  Modal,
  Dimensions,
} from 'react-native';
import { EstadoJogo } from '../types/GameTypes';
import { useGame } from '../contexts/GameContext';
import { DiceRoller } from '../components/DiceRoller';
import { AttributeDistribution, GenerationMethod, AttributeSet, AttributeRoll } from '../game/character/AttributeDistribution';
import Sprite, { SpriteType, SpriteUtils } from '../components/SpriteSystem';

interface EnhancedCharacterCreationScreenProps {
  onNavigate: (screen: EstadoJogo) => void;
}

interface Race {
  id: string;
  name: string;
  description: string;
  attributeModifiers: Partial<AttributeSet>;
  traits: string[];
  sprite: SpriteType;
}

interface CharacterClass {
  id: string;
  name: string;
  description: string;
  hitDie: number;
  primaryAttributes: string[];
  skills: string[];
  sprite: SpriteType;
}

const { width } = Dimensions.get('window');

const RACES: Race[] = [
  {
    id: 'human',
    name: 'Humano',
    description: 'Versáteis e adaptáveis, os humanos são a raça mais comum.',
    attributeModifiers: { strength: 1, dexterity: 1 },
    traits: ['Versatilidade', 'Adaptabilidade', 'Ambição'],
    sprite: SpriteType.WARRIOR
  },
  {
    id: 'elf',
    name: 'Elfo',
    description: 'Graciosos e mágicos, os elfos vivem em harmonia com a natureza.',
    attributeModifiers: { dexterity: 2, intelligence: 1, constitution: -1 },
    traits: ['Visão Noturna', 'Resistência a Encantamentos', 'Afinidade Mágica'],
    sprite: SpriteType.MAGE
  },
  {
    id: 'dwarf',
    name: 'Anão',
    description: 'Resistentes e determinados, os anões são mestres artesãos.',
    attributeModifiers: { constitution: 2, strength: 1, charisma: -1 },
    traits: ['Resistência a Venenos', 'Visão no Escuro', 'Conhecimento de Pedras'],
    sprite: SpriteType.WARRIOR
  },
  {
    id: 'halfling',
    name: 'Halfling',
    description: 'Pequenos e ágeis, os halflings são conhecidos por sua sorte.',
    attributeModifiers: { dexterity: 2, wisdom: 1, strength: -1 },
    traits: ['Sorte', 'Agilidade', 'Coragem'],
    sprite: SpriteType.ROGUE
  }
];

const CLASSES: CharacterClass[] = [
  {
    id: 'warrior',
    name: 'Guerreiro',
    description: 'Mestres do combate corpo a corpo e especialistas em armas.',
    hitDie: 10,
    primaryAttributes: ['Força', 'Constituição'],
    skills: ['Combate', 'Intimidação', 'Atletismo'],
    sprite: SpriteType.WARRIOR
  },
  {
    id: 'rogue',
    name: 'Ladrão',
    description: 'Especialistas em furtividade, agilidade e ataques precisos.',
    hitDie: 6,
    primaryAttributes: ['Destreza', 'Inteligência'],
    skills: ['Furtividade', 'Desarmar Armadilhas', 'Acrobacia'],
    sprite: SpriteType.ROGUE
  },
  {
    id: 'cleric',
    name: 'Clérigo',
    description: 'Servos divinos com poderes de cura e proteção.',
    hitDie: 8,
    primaryAttributes: ['Sabedoria', 'Constituição'],
    skills: ['Cura', 'Religião', 'Diplomacia'],
    sprite: SpriteType.CLERIC
  },
  {
    id: 'mage',
    name: 'Mago',
    description: 'Estudiosos da magia arcana com poderosos feitiços.',
    hitDie: 4,
    primaryAttributes: ['Inteligência', 'Sabedoria'],
    skills: ['Magia Arcana', 'Conhecimento', 'Investigação'],
    sprite: SpriteType.MAGE
  }
];

enum CreationStep {
  NAME = 'name',
  RACE = 'race',
  CLASS = 'class',
  ATTRIBUTES = 'attributes',
  FINAL = 'final'
}

export default function EnhancedCharacterCreationScreen({ onNavigate }: EnhancedCharacterCreationScreenProps) {
  const { updatePersonagem } = useGame();
  
  // Estados do personagem
  const [characterName, setCharacterName] = useState('');
  const [selectedRace, setSelectedRace] = useState<Race | null>(null);
  const [selectedClass, setSelectedClass] = useState<CharacterClass | null>(null);
  const [attributes, setAttributes] = useState<AttributeSet>({
    strength: 10,
    dexterity: 10,
    constitution: 10,
    intelligence: 10,
    wisdom: 10,
    charisma: 10
  });
  const [finalAttributes, setFinalAttributes] = useState<AttributeSet>(attributes);
  
  // Estados da interface
  const [currentStep, setCurrentStep] = useState<CreationStep>(CreationStep.NAME);
  const [showDiceModal, setShowDiceModal] = useState(false);
  const [generationMethod, setGenerationMethod] = useState<GenerationMethod>(GenerationMethod.ROLL_4D6_DROP_LOWEST);
  const [attributeRolls, setAttributeRolls] = useState<AttributeRoll[]>([]);
  const [availablePoints, setAvailablePoints] = useState(27);
  
  // Sistema de dados e distribuição
  const [attributeDistribution] = useState(new AttributeDistribution());

  useEffect(() => {
    if (selectedRace) {
      const modifiedAttributes = attributeDistribution.applyRacialModifiers(attributes, selectedRace.attributeModifiers);
      setFinalAttributes(modifiedAttributes);
    } else {
      setFinalAttributes(attributes);
    }
  }, [attributes, selectedRace]);

  const handleGenerateAttributes = () => {
    setShowDiceModal(true);
  };

  const handleAttributeGeneration = (method: GenerationMethod) => {
    const result = attributeDistribution.generateAttributesWithRolls(method);
    setAttributes(result.attributes);
    setAttributeRolls(result.rolls);
    setGenerationMethod(method);
    setShowDiceModal(false);
  };

  const handleManualAttributeChange = (attribute: keyof AttributeSet, value: number) => {
    if (generationMethod === GenerationMethod.POINT_BUY) {
      const newAttributes = { ...attributes, [attribute]: value };
      const cost = attributeDistribution.calculatePointBuyCost(newAttributes);
      
      if (cost <= 27 && value >= 8 && value <= 15) {
        setAttributes(newAttributes);
        setAvailablePoints(27 - cost);
      }
    } else {
      // Para outros métodos, permitir ajustes limitados
      if (value >= 3 && value <= 18) {
        setAttributes({ ...attributes, [attribute]: value });
      }
    }
  };

  const handleNextStep = () => {
    switch (currentStep) {
      case CreationStep.NAME:
        if (!characterName.trim()) {
          Alert.alert('Erro', 'Por favor, insira um nome para seu personagem.');
          return;
        }
        setCurrentStep(CreationStep.RACE);
        break;
        
      case CreationStep.RACE:
        if (!selectedRace) {
          Alert.alert('Erro', 'Por favor, selecione uma raça.');
          return;
        }
        setCurrentStep(CreationStep.CLASS);
        break;
        
      case CreationStep.CLASS:
        if (!selectedClass) {
          Alert.alert('Erro', 'Por favor, selecione uma classe.');
          return;
        }
        setCurrentStep(CreationStep.ATTRIBUTES);
        break;
        
      case CreationStep.ATTRIBUTES:
        const validation = attributeDistribution.validateAttributes(finalAttributes);
        if (!validation.valid) {
          Alert.alert('Atributos Inválidos', validation.errors.join('\n'));
          return;
        }
        setCurrentStep(CreationStep.FINAL);
        break;
        
      case CreationStep.FINAL:
        createCharacter();
        break;
    }
  };

  const handlePreviousStep = () => {
    switch (currentStep) {
      case CreationStep.RACE:
        setCurrentStep(CreationStep.NAME);
        break;
      case CreationStep.CLASS:
        setCurrentStep(CreationStep.RACE);
        break;
      case CreationStep.ATTRIBUTES:
        setCurrentStep(CreationStep.CLASS);
        break;
      case CreationStep.FINAL:
        setCurrentStep(CreationStep.ATTRIBUTES);
        break;
    }
  };

  const createCharacter = () => {
    if (!selectedRace || !selectedClass) return;

    const hitPoints = attributeDistribution.calculateHitPoints(
      finalAttributes.constitution,
      selectedClass.hitDie,
      1
    );

    const newCharacter = {
      nome: characterName,
      raca: selectedRace.name,
      classe: selectedClass.name,
      nivel: 1,
      experiencia: 0,
      pontosDeVida: hitPoints,
      pontosDeVidaMaximos: hitPoints,
      atributos: {
        forca: finalAttributes.strength,
        destreza: finalAttributes.dexterity,
        constituicao: finalAttributes.constitution,
        inteligencia: finalAttributes.intelligence,
        sabedoria: finalAttributes.wisdom,
        carisma: finalAttributes.charisma
      },
      dinheiro: 100, // Dinheiro inicial
      equipamentos: [],
      habilidades: selectedClass.skills
    };

    updatePersonagem(newCharacter);
    Alert.alert(
      'Personagem Criado!',
      `${characterName} foi criado com sucesso!`,
      [{ text: 'OK', onPress: () => onNavigate(EstadoJogo.AVENTURA) }]
    );
  };

  const renderNameStep = () => (
    <View style={styles.stepContainer}>
      <Text style={styles.stepTitle}>Nome do Personagem</Text>
      <Text style={styles.stepDescription}>
        Escolha um nome épico para seu herói!
      </Text>
      
      <TextInput
        style={styles.nameInput}
        placeholder="Digite o nome do personagem"
        placeholderTextColor="#888"
        value={characterName}
        onChangeText={setCharacterName}
        maxLength={20}
      />
      
      <View style={styles.namePreview}>
        <Sprite type={SpriteType.WARRIOR} size={80} />
        <Text style={styles.previewName}>
          {characterName || 'Seu Herói'}
        </Text>
      </View>
    </View>
  );

  const renderRaceStep = () => (
    <View style={styles.stepContainer}>
      <Text style={styles.stepTitle}>Escolha sua Raça</Text>
      <Text style={styles.stepDescription}>
        Cada raça possui características únicas que afetam seus atributos.
      </Text>
      
      <ScrollView style={styles.optionsList}>
        {RACES.map(race => (
          <TouchableOpacity
            key={race.id}
            style={[
              styles.optionCard,
              selectedRace?.id === race.id && styles.selectedOption
            ]}
            onPress={() => setSelectedRace(race)}
          >
            <View style={styles.optionHeader}>
              <Sprite type={race.sprite} size={48} />
              <View style={styles.optionInfo}>
                <Text style={styles.optionName}>{race.name}</Text>
                <Text style={styles.optionDescription}>{race.description}</Text>
              </View>
            </View>
            
            <View style={styles.modifiersContainer}>
              <Text style={styles.modifiersTitle}>Modificadores:</Text>
              {Object.entries(race.attributeModifiers).map(([attr, mod]) => (
                <Text key={attr} style={styles.modifierText}>
                  {attr}: {mod > 0 ? '+' : ''}{mod}
                </Text>
              ))}
            </View>
            
            <View style={styles.traitsContainer}>
              <Text style={styles.traitsTitle}>Características:</Text>
              {race.traits.map((trait, index) => (
                <Text key={index} style={styles.traitText}>• {trait}</Text>
              ))}
            </View>
          </TouchableOpacity>
        ))}
      </ScrollView>
    </View>
  );

  const renderClassStep = () => (
    <View style={styles.stepContainer}>
      <Text style={styles.stepTitle}>Escolha sua Classe</Text>
      <Text style={styles.stepDescription}>
        Sua classe determina suas habilidades e estilo de jogo.
      </Text>
      
      <ScrollView style={styles.optionsList}>
        {CLASSES.map(characterClass => (
          <TouchableOpacity
            key={characterClass.id}
            style={[
              styles.optionCard,
              selectedClass?.id === characterClass.id && styles.selectedOption
            ]}
            onPress={() => setSelectedClass(characterClass)}
          >
            <View style={styles.optionHeader}>
              <Sprite type={characterClass.sprite} size={48} />
              <View style={styles.optionInfo}>
                <Text style={styles.optionName}>{characterClass.name}</Text>
                <Text style={styles.optionDescription}>{characterClass.description}</Text>
              </View>
            </View>
            
            <View style={styles.classStatsContainer}>
              <Text style={styles.classStatText}>Dado de Vida: d{characterClass.hitDie}</Text>
              <Text style={styles.classStatText}>
                Atributos Primários: {characterClass.primaryAttributes.join(', ')}
              </Text>
            </View>
            
            <View style={styles.skillsContainer}>
              <Text style={styles.skillsTitle}>Habilidades:</Text>
              {characterClass.skills.map((skill, index) => (
                <Text key={index} style={styles.skillText}>• {skill}</Text>
              ))}
            </View>
          </TouchableOpacity>
        ))}
      </ScrollView>
    </View>
  );

  const renderAttributesStep = () => (
    <View style={styles.stepContainer}>
      <Text style={styles.stepTitle}>Atributos</Text>
      <Text style={styles.stepDescription}>
        Determine os atributos do seu personagem.
      </Text>
      
      <View style={styles.attributeControls}>
        <TouchableOpacity
          style={styles.generateButton}
          onPress={handleGenerateAttributes}
        >
          <Text style={styles.generateButtonText}>🎲 Rolar Dados</Text>
        </TouchableOpacity>
        
        {generationMethod === GenerationMethod.POINT_BUY && (
          <Text style={styles.pointsText}>
            Pontos Disponíveis: {availablePoints}
          </Text>
        )}
      </View>

      <View style={styles.attributesContainer}>
        {Object.entries(finalAttributes).map(([key, value]) => {
          const modifier = attributeDistribution.getModifier(value);
          const baseValue = attributes[key as keyof AttributeSet];
          const racialBonus = value - baseValue;
          
          return (
            <View key={key} style={styles.attributeRow}>
              <Text style={styles.attributeName}>
                {key.charAt(0).toUpperCase() + key.slice(1)}
              </Text>
              
              <View style={styles.attributeControls}>
                <TouchableOpacity
                  style={styles.attributeButton}
                  onPress={() => handleManualAttributeChange(key as keyof AttributeSet, baseValue - 1)}
                >
                  <Text style={styles.attributeButtonText}>-</Text>
                </TouchableOpacity>
                
                <View style={styles.attributeValueContainer}>
                  <Text style={styles.attributeValue}>{baseValue}</Text>
                  {racialBonus !== 0 && (
                    <Text style={styles.racialBonus}>
                      {racialBonus > 0 ? '+' : ''}{racialBonus}
                    </Text>
                  )}
                  <Text style={styles.finalValue}>= {value}</Text>
                </View>
                
                <TouchableOpacity
                  style={styles.attributeButton}
                  onPress={() => handleManualAttributeChange(key as keyof AttributeSet, baseValue + 1)}
                >
                  <Text style={styles.attributeButtonText}>+</Text>
                </TouchableOpacity>
              </View>
              
              <Text style={styles.modifierText}>
                ({modifier >= 0 ? '+' : ''}{modifier})
              </Text>
            </View>
          );
        })}
      </View>

      {attributeRolls.length > 0 && (
        <View style={styles.rollsContainer}>
          <Text style={styles.rollsTitle}>Últimas Rolagens:</Text>
          {attributeRolls.map((roll, index) => (
            <Text key={index} style={styles.rollText}>
              {roll.attribute}: {roll.rolls.join(', ')} = {roll.total}
            </Text>
          ))}
        </View>
      )}
    </View>
  );

  const renderFinalStep = () => {
    if (!selectedRace || !selectedClass) return null;

    const hitPoints = attributeDistribution.calculateHitPoints(
      finalAttributes.constitution,
      selectedClass.hitDie,
      1
    );

    return (
      <View style={styles.stepContainer}>
        <Text style={styles.stepTitle}>Resumo do Personagem</Text>
        
        <View style={styles.characterSummary}>
          <View style={styles.summaryHeader}>
            <Sprite type={selectedClass.sprite} size={80} />
            <View style={styles.summaryInfo}>
              <Text style={styles.summaryName}>{characterName}</Text>
              <Text style={styles.summaryRaceClass}>
                {selectedRace.name} {selectedClass.name}
              </Text>
              <Text style={styles.summaryLevel}>Nível 1</Text>
            </View>
          </View>

          <View style={styles.summaryStats}>
            <Text style={styles.summaryStatsTitle}>Estatísticas:</Text>
            <Text style={styles.summaryStatText}>Pontos de Vida: {hitPoints}</Text>
            
            <View style={styles.summaryAttributes}>
              {Object.entries(finalAttributes).map(([key, value]) => {
                const modifier = attributeDistribution.getModifier(value);
                return (
                  <Text key={key} style={styles.summaryAttributeText}>
                    {key.charAt(0).toUpperCase() + key.slice(1)}: {value} ({modifier >= 0 ? '+' : ''}{modifier})
                  </Text>
                );
              })}
            </View>
          </View>
        </View>
      </View>
    );
  };

  const renderDiceModal = () => (
    <Modal
      visible={showDiceModal}
      transparent={true}
      animationType="fade"
      onRequestClose={() => setShowDiceModal(false)}
    >
      <View style={styles.modalOverlay}>
        <View style={styles.diceModalContent}>
          <Text style={styles.diceModalTitle}>Método de Geração</Text>
          
          <TouchableOpacity
            style={styles.methodButton}
            onPress={() => handleAttributeGeneration(GenerationMethod.ROLL_4D6_DROP_LOWEST)}
          >
            <Text style={styles.methodButtonText}>4d6 (descartar menor)</Text>
            <Text style={styles.methodDescription}>Método padrão - equilibrado</Text>
          </TouchableOpacity>
          
          <TouchableOpacity
            style={styles.methodButton}
            onPress={() => handleAttributeGeneration(GenerationMethod.ROLL_3D6_STRAIGHT)}
          >
            <Text style={styles.methodButtonText}>3d6 direto</Text>
            <Text style={styles.methodDescription}>Clássico - mais desafiador</Text>
          </TouchableOpacity>
          
          <TouchableOpacity
            style={styles.methodButton}
            onPress={() => handleAttributeGeneration(GenerationMethod.ROLL_4D6_REROLL_ONES)}
          >
            <Text style={styles.methodButtonText}>4d6 (reroll 1s)</Text>
            <Text style={styles.methodDescription}>Heróico - mais poderoso</Text>
          </TouchableOpacity>
          
          <TouchableOpacity
            style={styles.methodButton}
            onPress={() => handleAttributeGeneration(GenerationMethod.STANDARD_ARRAY)}
          >
            <Text style={styles.methodButtonText}>Array Padrão</Text>
            <Text style={styles.methodDescription}>15, 14, 13, 12, 10, 8</Text>
          </TouchableOpacity>
          
          <TouchableOpacity
            style={styles.methodButton}
            onPress={() => handleAttributeGeneration(GenerationMethod.POINT_BUY)}
          >
            <Text style={styles.methodButtonText}>Point Buy</Text>
            <Text style={styles.methodDescription}>27 pontos para distribuir</Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={styles.closeButton}
            onPress={() => setShowDiceModal(false)}
          >
            <Text style={styles.closeButtonText}>Cancelar</Text>
          </TouchableOpacity>
        </View>
      </View>
    </Modal>
  );

  const getStepContent = () => {
    switch (currentStep) {
      case CreationStep.NAME:
        return renderNameStep();
      case CreationStep.RACE:
        return renderRaceStep();
      case CreationStep.CLASS:
        return renderClassStep();
      case CreationStep.ATTRIBUTES:
        return renderAttributesStep();
      case CreationStep.FINAL:
        return renderFinalStep();
      default:
        return renderNameStep();
    }
  };

  const getStepTitle = () => {
    switch (currentStep) {
      case CreationStep.NAME:
        return 'Nome';
      case CreationStep.RACE:
        return 'Raça';
      case CreationStep.CLASS:
        return 'Classe';
      case CreationStep.ATTRIBUTES:
        return 'Atributos';
      case CreationStep.FINAL:
        return 'Finalizar';
      default:
        return 'Criação';
    }
  };

  return (
    <View style={styles.container}>
      {/* Header com progresso */}
      <View style={styles.header}>
        <Text style={styles.title}>Criação de Personagem</Text>
        <Text style={styles.stepIndicator}>
          Passo {Object.values(CreationStep).indexOf(currentStep) + 1} de {Object.values(CreationStep).length}: {getStepTitle()}
        </Text>
      </View>

      {/* Conteúdo do passo atual */}
      <ScrollView style={styles.content}>
        {getStepContent()}
      </ScrollView>

      {/* Botões de navegação */}
      <View style={styles.navigationButtons}>
        {currentStep !== CreationStep.NAME && (
          <TouchableOpacity
            style={styles.navButton}
            onPress={handlePreviousStep}
          >
            <Text style={styles.navButtonText}>Anterior</Text>
          </TouchableOpacity>
        )}
        
        <TouchableOpacity
          style={[styles.navButton, styles.nextButton]}
          onPress={handleNextStep}
        >
          <Text style={styles.navButtonText}>
            {currentStep === CreationStep.FINAL ? 'Criar Personagem' : 'Próximo'}
          </Text>
        </TouchableOpacity>
      </View>

      {renderDiceModal()}
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
  },
  stepIndicator: {
    color: '#FFF',
    fontSize: 14,
    textAlign: 'center',
    marginTop: 5,
  },
  content: {
    flex: 1,
    padding: 15,
  },
  stepContainer: {
    flex: 1,
  },
  stepTitle: {
    color: '#FFD700',
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 10,
    textAlign: 'center',
  },
  stepDescription: {
    color: '#CCC',
    fontSize: 14,
    textAlign: 'center',
    marginBottom: 20,
    lineHeight: 20,
  },
  nameInput: {
    backgroundColor: 'rgba(255, 255, 255, 0.1)',
    borderRadius: 8,
    paddingHorizontal: 15,
    paddingVertical: 12,
    color: '#FFF',
    fontSize: 16,
    marginBottom: 20,
    borderWidth: 2,
    borderColor: '#8B4513',
  },
  namePreview: {
    alignItems: 'center',
    marginTop: 20,
  },
  previewName: {
    color: '#FFD700',
    fontSize: 18,
    fontWeight: 'bold',
    marginTop: 10,
  },
  optionsList: {
    flex: 1,
  },
  optionCard: {
    backgroundColor: 'rgba(255, 255, 255, 0.1)',
    borderRadius: 12,
    padding: 15,
    marginBottom: 15,
    borderWidth: 2,
    borderColor: '#666',
  },
  selectedOption: {
    borderColor: '#FFD700',
    backgroundColor: 'rgba(255, 215, 0, 0.1)',
  },
  optionHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 10,
  },
  optionInfo: {
    flex: 1,
    marginLeft: 15,
  },
  optionName: {
    color: '#FFD700',
    fontSize: 18,
    fontWeight: 'bold',
  },
  optionDescription: {
    color: '#CCC',
    fontSize: 14,
    marginTop: 5,
  },
  modifiersContainer: {
    marginBottom: 10,
  },
  modifiersTitle: {
    color: '#FFF',
    fontSize: 14,
    fontWeight: 'bold',
    marginBottom: 5,
  },
  modifierText: {
    color: '#CCC',
    fontSize: 12,
  },
  traitsContainer: {
    marginBottom: 5,
  },
  traitsTitle: {
    color: '#FFF',
    fontSize: 14,
    fontWeight: 'bold',
    marginBottom: 5,
  },
  traitText: {
    color: '#CCC',
    fontSize: 12,
    marginBottom: 2,
  },
  classStatsContainer: {
    marginBottom: 10,
  },
  classStatText: {
    color: '#CCC',
    fontSize: 12,
    marginBottom: 2,
  },
  skillsContainer: {
    marginBottom: 5,
  },
  skillsTitle: {
    color: '#FFF',
    fontSize: 14,
    fontWeight: 'bold',
    marginBottom: 5,
  },
  skillText: {
    color: '#CCC',
    fontSize: 12,
    marginBottom: 2,
  },
  attributeControls: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 20,
  },
  generateButton: {
    backgroundColor: '#8B4513',
    borderRadius: 8,
    paddingHorizontal: 20,
    paddingVertical: 10,
    borderWidth: 2,
    borderColor: '#D2691E',
  },
  generateButtonText: {
    color: '#FFD700',
    fontSize: 16,
    fontWeight: 'bold',
  },
  pointsText: {
    color: '#FFD700',
    fontSize: 16,
    fontWeight: 'bold',
  },
  attributesContainer: {
    marginBottom: 20,
  },
  attributeRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingVertical: 10,
    borderBottomWidth: 1,
    borderBottomColor: '#333',
  },
  attributeName: {
    color: '#FFF',
    fontSize: 16,
    fontWeight: 'bold',
    flex: 1,
  },
  attributeButton: {
    backgroundColor: '#8B4513',
    borderRadius: 6,
    width: 30,
    height: 30,
    justifyContent: 'center',
    alignItems: 'center',
  },
  attributeButtonText: {
    color: '#FFD700',
    fontSize: 18,
    fontWeight: 'bold',
  },
  attributeValueContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginHorizontal: 15,
  },
  attributeValue: {
    color: '#FFF',
    fontSize: 16,
    fontWeight: 'bold',
    minWidth: 25,
    textAlign: 'center',
  },
  racialBonus: {
    color: '#4CAF50',
    fontSize: 14,
    marginHorizontal: 5,
  },
  finalValue: {
    color: '#FFD700',
    fontSize: 16,
    fontWeight: 'bold',
    marginLeft: 5,
  },
  modifierText: {
    color: '#CCC',
    fontSize: 14,
    minWidth: 40,
    textAlign: 'center',
  },
  rollsContainer: {
    backgroundColor: 'rgba(255, 255, 255, 0.05)',
    borderRadius: 8,
    padding: 15,
  },
  rollsTitle: {
    color: '#FFD700',
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  rollText: {
    color: '#CCC',
    fontSize: 12,
    marginBottom: 5,
  },
  characterSummary: {
    backgroundColor: 'rgba(255, 255, 255, 0.1)',
    borderRadius: 12,
    padding: 20,
  },
  summaryHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 20,
  },
  summaryInfo: {
    flex: 1,
    marginLeft: 20,
  },
  summaryName: {
    color: '#FFD700',
    fontSize: 24,
    fontWeight: 'bold',
  },
  summaryRaceClass: {
    color: '#FFF',
    fontSize: 16,
    marginTop: 5,
  },
  summaryLevel: {
    color: '#CCC',
    fontSize: 14,
    marginTop: 5,
  },
  summaryStats: {
    borderTopWidth: 1,
    borderTopColor: '#333',
    paddingTop: 15,
  },
  summaryStatsTitle: {
    color: '#FFD700',
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  summaryStatText: {
    color: '#FFF',
    fontSize: 16,
    marginBottom: 10,
  },
  summaryAttributes: {
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  summaryAttributeText: {
    color: '#CCC',
    fontSize: 12,
    width: '50%',
    marginBottom: 5,
  },
  navigationButtons: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    padding: 15,
    backgroundColor: 'rgba(139, 69, 19, 0.5)',
  },
  navButton: {
    backgroundColor: '#8B4513',
    borderWidth: 2,
    borderColor: '#D2691E',
    borderRadius: 8,
    paddingVertical: 12,
    paddingHorizontal: 20,
    flex: 1,
    marginHorizontal: 5,
    alignItems: 'center',
  },
  nextButton: {
    backgroundColor: '#228B22',
    borderColor: '#32CD32',
  },
  navButtonText: {
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
  diceModalContent: {
    backgroundColor: '#2a2a2a',
    borderRadius: 12,
    padding: 20,
    margin: 20,
    maxWidth: width * 0.9,
    borderWidth: 2,
    borderColor: '#D2691E',
  },
  diceModalTitle: {
    color: '#FFD700',
    fontSize: 20,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 20,
  },
  methodButton: {
    backgroundColor: '#8B4513',
    borderRadius: 8,
    padding: 15,
    marginBottom: 10,
    alignItems: 'center',
  },
  methodButtonText: {
    color: '#FFD700',
    fontSize: 16,
    fontWeight: 'bold',
  },
  methodDescription: {
    color: '#CCC',
    fontSize: 12,
    marginTop: 5,
  },
  closeButton: {
    backgroundColor: '#666',
    borderRadius: 8,
    paddingVertical: 12,
    alignItems: 'center',
    marginTop: 10,
  },
  closeButtonText: {
    color: '#FFF',
    fontSize: 16,
    fontWeight: 'bold',
  },
});
