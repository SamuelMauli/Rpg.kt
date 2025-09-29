import React, { useState } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
  TextInput,
  Image,
  Alert,
} from 'react-native';
import { EstadoJogo, Personagem, Atributos, ModificadoresAtributos } from '../types/GameTypes';
import { useGame } from '../contexts/GameContext';
import { RaceFactory } from '../game/races/Races';
import { ClassFactory } from '../game/classes/Classes';
import { DiceUtils } from '../utils/DiceUtils';

interface CharacterCreationScreenProps {
  onNavigate: (screen: EstadoJogo) => void;
}

export default function CharacterCreationScreen({ onNavigate }: CharacterCreationScreenProps) {
  const { setPersonagem } = useGame();
  
  const [step, setStep] = useState(1);
  const [characterName, setCharacterName] = useState('');
  const [selectedRace, setSelectedRace] = useState('');
  const [selectedClass, setSelectedClass] = useState('');
  const [attributes, setAttributes] = useState<Atributos>({
    forca: 10,
    destreza: 10,
    constituicao: 10,
    inteligencia: 10,
    sabedoria: 10,
    carisma: 10,
  });

  const races = RaceFactory.getAvailableRaces();
  const classes = ClassFactory.getAvailableClasses();

  const rollAttributes = () => {
    const newAttributes: Atributos = {
      forca: DiceUtils.rollAttribute(),
      destreza: DiceUtils.rollAttribute(),
      constituicao: DiceUtils.rollAttribute(),
      inteligencia: DiceUtils.rollAttribute(),
      sabedoria: DiceUtils.rollAttribute(),
      carisma: DiceUtils.rollAttribute(),
    };
    setAttributes(newAttributes);
  };

  const calculateModifiers = (attrs: Atributos): ModificadoresAtributos => {
    return {
      forca: DiceUtils.getAttributeModifier(attrs.forca),
      destreza: DiceUtils.getAttributeModifier(attrs.destreza),
      constituicao: DiceUtils.getAttributeModifier(attrs.constituicao),
      inteligencia: DiceUtils.getAttributeModifier(attrs.inteligencia),
      sabedoria: DiceUtils.getAttributeModifier(attrs.sabedoria),
      carisma: DiceUtils.getAttributeModifier(attrs.carisma),
    };
  };

  const createCharacter = () => {
    if (!characterName.trim()) {
      Alert.alert('Erro', 'Digite um nome para o personagem!');
      return;
    }
    if (!selectedRace || !selectedClass) {
      Alert.alert('Erro', 'Selecione uma raça e uma classe!');
      return;
    }

    try {
      const race = RaceFactory.createRace(selectedRace);
      const characterClass = ClassFactory.createClass(selectedClass);
      
      // Aplicar modificadores raciais
      const raceModifiers = race.getModificadoresAtributos();
      const finalAttributes: Atributos = {
        forca: attributes.forca + (raceModifiers.forca || 0),
        destreza: attributes.destreza + (raceModifiers.destreza || 0),
        constituicao: attributes.constituicao + (raceModifiers.constituicao || 0),
        inteligencia: attributes.inteligencia + (raceModifiers.inteligencia || 0),
        sabedoria: attributes.sabedoria + (raceModifiers.sabedoria || 0),
        carisma: attributes.carisma + (raceModifiers.carisma || 0),
      };

      const modifiers = calculateModifiers(finalAttributes);
      
      // Calcular pontos de vida iniciais
      const hitPoints = DiceUtils.rollHitPoints(
        characterClass.getDadoDeVida(),
        finalAttributes.constituicao
      );

      // Calcular classe de armadura base
      const baseAC = 10 + modifiers.destreza;

      const newCharacter: Personagem = {
        nome: characterName.trim(),
        raca: race,
        classe: characterClass,
        nivel: 1,
        experiencia: 0,
        atributos: finalAttributes,
        modificadores: modifiers,
        pontosVida: hitPoints,
        pontosVidaMaximos: hitPoints,
        classeArmadura: baseAC,
        baseAtaque: 1,
        movimento: race.getMovimento(),
        inventario: {
          itens: [],
          capacidade: 20,
          pesoAtual: 0,
          pesoMaximo: 50 + (modifiers.forca * 5),
        },
        habilidades: [...race.getHabilidades(), ...characterClass.getHabilidades()],
        dinheiro: 100,
      };

      setPersonagem(newCharacter);
      onNavigate(EstadoJogo.AVENTURA);
    } catch (error) {
      Alert.alert('Erro', 'Erro ao criar personagem: ' + error);
    }
  };

  const renderStep1 = () => (
    <View style={styles.stepContainer}>
      <Text style={styles.stepTitle}>Nome do Personagem</Text>
      <TextInput
        style={styles.nameInput}
        value={characterName}
        onChangeText={setCharacterName}
        placeholder="Digite o nome do seu herói..."
        placeholderTextColor="#888"
        maxLength={20}
      />
      <TouchableOpacity
        style={[styles.button, !characterName.trim() && styles.buttonDisabled]}
        onPress={() => characterName.trim() && setStep(2)}
        disabled={!characterName.trim()}
      >
        <Text style={styles.buttonText}>PRÓXIMO</Text>
      </TouchableOpacity>
    </View>
  );

  const renderStep2 = () => (
    <View style={styles.stepContainer}>
      <Text style={styles.stepTitle}>Escolha sua Raça</Text>
      <ScrollView style={styles.optionsList}>
        {races.map((race) => (
          <TouchableOpacity
            key={race}
            style={[
              styles.optionButton,
              selectedRace === race && styles.optionButtonSelected
            ]}
            onPress={() => setSelectedRace(race)}
          >
            <Text style={[
              styles.optionText,
              selectedRace === race && styles.optionTextSelected
            ]}>
              {race}
            </Text>
            <Text style={styles.optionDescription}>
              {RaceFactory.getRaceDescription(race)}
            </Text>
          </TouchableOpacity>
        ))}
      </ScrollView>
      <View style={styles.navigationButtons}>
        <TouchableOpacity style={styles.backButton} onPress={() => setStep(1)}>
          <Text style={styles.buttonText}>VOLTAR</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={[styles.button, !selectedRace && styles.buttonDisabled]}
          onPress={() => selectedRace && setStep(3)}
          disabled={!selectedRace}
        >
          <Text style={styles.buttonText}>PRÓXIMO</Text>
        </TouchableOpacity>
      </View>
    </View>
  );

  const renderStep3 = () => (
    <View style={styles.stepContainer}>
      <Text style={styles.stepTitle}>Escolha sua Classe</Text>
      <ScrollView style={styles.optionsList}>
        {classes.map((characterClass) => (
          <TouchableOpacity
            key={characterClass}
            style={[
              styles.optionButton,
              selectedClass === characterClass && styles.optionButtonSelected
            ]}
            onPress={() => setSelectedClass(characterClass)}
          >
            <Text style={[
              styles.optionText,
              selectedClass === characterClass && styles.optionTextSelected
            ]}>
              {characterClass}
            </Text>
            <Text style={styles.optionDescription}>
              {ClassFactory.getClassDescription(characterClass)}
            </Text>
          </TouchableOpacity>
        ))}
      </ScrollView>
      <View style={styles.navigationButtons}>
        <TouchableOpacity style={styles.backButton} onPress={() => setStep(2)}>
          <Text style={styles.buttonText}>VOLTAR</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={[styles.button, !selectedClass && styles.buttonDisabled]}
          onPress={() => selectedClass && setStep(4)}
          disabled={!selectedClass}
        >
          <Text style={styles.buttonText}>PRÓXIMO</Text>
        </TouchableOpacity>
      </View>
    </View>
  );

  const renderStep4 = () => (
    <View style={styles.stepContainer}>
      <Text style={styles.stepTitle}>Atributos</Text>
      <View style={styles.attributesContainer}>
        {Object.entries(attributes).map(([attr, value]) => (
          <View key={attr} style={styles.attributeRow}>
            <Text style={styles.attributeName}>
              {attr.charAt(0).toUpperCase() + attr.slice(1)}:
            </Text>
            <Text style={styles.attributeValue}>{value}</Text>
            <Text style={styles.attributeModifier}>
              ({DiceUtils.getAttributeModifier(value) >= 0 ? '+' : ''}
              {DiceUtils.getAttributeModifier(value)})
            </Text>
          </View>
        ))}
      </View>
      <TouchableOpacity style={styles.rollButton} onPress={rollAttributes}>
        <Text style={styles.buttonText}>ROLAR DADOS</Text>
      </TouchableOpacity>
      <View style={styles.navigationButtons}>
        <TouchableOpacity style={styles.backButton} onPress={() => setStep(3)}>
          <Text style={styles.buttonText}>VOLTAR</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.button} onPress={createCharacter}>
          <Text style={styles.buttonText}>CRIAR PERSONAGEM</Text>
        </TouchableOpacity>
      </View>
    </View>
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Criação de Personagem</Text>
        <Text style={styles.stepIndicator}>Passo {step} de 4</Text>
      </View>
      
      {step === 1 && renderStep1()}
      {step === 2 && renderStep2()}
      {step === 3 && renderStep3()}
      {step === 4 && renderStep4()}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#1a1a1a',
    padding: 20,
  },
  header: {
    alignItems: 'center',
    marginBottom: 30,
    paddingTop: 20,
  },
  title: {
    color: '#FFD700',
    fontSize: 24,
    fontWeight: 'bold',
    textAlign: 'center',
  },
  stepIndicator: {
    color: '#888',
    fontSize: 16,
    marginTop: 5,
  },
  stepContainer: {
    flex: 1,
  },
  stepTitle: {
    color: '#FFD700',
    fontSize: 20,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 20,
  },
  nameInput: {
    backgroundColor: '#333',
    color: '#FFF',
    fontSize: 18,
    padding: 15,
    borderRadius: 8,
    borderWidth: 2,
    borderColor: '#555',
    marginBottom: 20,
    textAlign: 'center',
  },
  optionsList: {
    flex: 1,
    marginBottom: 20,
  },
  optionButton: {
    backgroundColor: '#333',
    padding: 15,
    borderRadius: 8,
    borderWidth: 2,
    borderColor: '#555',
    marginBottom: 10,
  },
  optionButtonSelected: {
    borderColor: '#FFD700',
    backgroundColor: '#444',
  },
  optionText: {
    color: '#FFF',
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 5,
  },
  optionTextSelected: {
    color: '#FFD700',
  },
  optionDescription: {
    color: '#CCC',
    fontSize: 14,
  },
  attributesContainer: {
    backgroundColor: '#333',
    padding: 20,
    borderRadius: 8,
    marginBottom: 20,
  },
  attributeRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 10,
  },
  attributeName: {
    color: '#FFF',
    fontSize: 16,
    flex: 1,
  },
  attributeValue: {
    color: '#FFD700',
    fontSize: 18,
    fontWeight: 'bold',
    width: 40,
    textAlign: 'center',
  },
  attributeModifier: {
    color: '#CCC',
    fontSize: 14,
    width: 40,
    textAlign: 'center',
  },
  rollButton: {
    backgroundColor: '#8B4513',
    borderWidth: 2,
    borderColor: '#D2691E',
    borderRadius: 8,
    paddingVertical: 12,
    paddingHorizontal: 20,
    alignItems: 'center',
    marginBottom: 20,
  },
  navigationButtons: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  button: {
    backgroundColor: '#8B4513',
    borderWidth: 2,
    borderColor: '#D2691E',
    borderRadius: 8,
    paddingVertical: 12,
    paddingHorizontal: 20,
    flex: 1,
    marginLeft: 10,
    alignItems: 'center',
  },
  backButton: {
    backgroundColor: '#666',
    borderWidth: 2,
    borderColor: '#888',
    borderRadius: 8,
    paddingVertical: 12,
    paddingHorizontal: 20,
    flex: 1,
    marginRight: 10,
    alignItems: 'center',
  },
  buttonDisabled: {
    backgroundColor: '#444',
    borderColor: '#666',
  },
  buttonText: {
    color: '#FFD700',
    fontSize: 16,
    fontWeight: 'bold',
  },
});
