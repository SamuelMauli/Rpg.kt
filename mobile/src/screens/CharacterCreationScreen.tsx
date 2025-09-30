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
import { 
  EstadoJogo, 
  Personagem, 
  Atributos, 
  ModificadoresAtributos,
  MetodoDistribuicao,
  Alinhamento,
  AtributosDistribuicao,
  EstadoCriacaoPersonagem
} from '../types/GameTypes';
import { useGame } from '../contexts/GameContext';
import { RaceFactory } from '../game/races/Races';
import { ClassFactory } from '../game/classes/Classes';
import { DiceUtils } from '../utils/DiceUtils';

interface CharacterCreationScreenProps {
  onNavigate: (screen: EstadoJogo) => void;
}

export default function CharacterCreationScreen({ onNavigate }: CharacterCreationScreenProps) {
  const { setPersonagem } = useGame();
  
  const [estado, setEstado] = useState<EstadoCriacaoPersonagem>({
    etapa: 1,
    nome: '',
    raca: '',
    classe: '',
    metodoDistribuicao: MetodoDistribuicao.HEROICA,
    atributosDistribuicao: {
      valores: [],
      metodo: MetodoDistribuicao.HEROICA,
      atributosAssignados: {}
    },
    atributosFinal: {
      forca: 10,
      destreza: 10,
      constituicao: 10,
      inteligencia: 10,
      sabedoria: 10,
      carisma: 10,
    },
    alinhamento: Alinhamento.NEUTRO
  });

  const races = RaceFactory.getAvailableRaces();
  const classes = ClassFactory.getAvailableClasses();

  const avancarEtapa = () => {
    if (estado.etapa < 6) {
      setEstado(prev => ({ ...prev, etapa: prev.etapa + 1 }));
    }
  };

  const voltarEtapa = () => {
    if (estado.etapa > 1) {
      setEstado(prev => ({ ...prev, etapa: prev.etapa - 1 }));
    }
  };

  const gerarAtributos = () => {
    const valores = DiceUtils.generateAttributeSet(estado.metodoDistribuicao);
    
    let atributosAssignados: Partial<Atributos> = {};
    
    // Para métodos clássico e heróico, atribuir em ordem
    if (estado.metodoDistribuicao !== MetodoDistribuicao.AVENTUREIRO) {
      atributosAssignados = {
        forca: valores[0],
        destreza: valores[1],
        constituicao: valores[2],
        inteligencia: valores[3],
        sabedoria: valores[4],
        carisma: valores[5]
      };
    }
    
    setEstado(prev => ({
      ...prev,
      atributosDistribuicao: {
        valores,
        metodo: estado.metodoDistribuicao,
        atributosAssignados
      }
    }));
  };

  const atribuirValor = (atributo: keyof Atributos, valor: number) => {
    if (estado.metodoDistribuicao !== MetodoDistribuicao.AVENTUREIRO) return;
    
    // Verificar se o valor já foi usado
    const valoresUsados = Object.values(estado.atributosDistribuicao.atributosAssignados);
    if (valoresUsados.includes(valor)) {
      Alert.alert('Erro', 'Este valor já foi atribuído a outro atributo!');
      return;
    }
    
    setEstado(prev => ({
      ...prev,
      atributosDistribuicao: {
        ...prev.atributosDistribuicao,
        atributosAssignados: {
          ...prev.atributosDistribuicao.atributosAssignados,
          [atributo]: valor
        }
      }
    }));
  };

  const calcularModificadores = (attrs: Atributos): ModificadoresAtributos => {
    return {
      forca: DiceUtils.getAttributeModifier(attrs.forca),
      destreza: DiceUtils.getAttributeModifier(attrs.destreza),
      constituicao: DiceUtils.getAttributeModifier(attrs.constituicao),
      inteligencia: DiceUtils.getAttributeModifier(attrs.inteligencia),
      sabedoria: DiceUtils.getAttributeModifier(attrs.sabedoria),
      carisma: DiceUtils.getAttributeModifier(attrs.carisma),
    };
  };

  const finalizarPersonagem = () => {
    if (!estado.nome.trim()) {
      Alert.alert('Erro', 'Digite um nome para o personagem!');
      return;
    }
    if (!estado.raca || !estado.classe) {
      Alert.alert('Erro', 'Selecione uma raça e uma classe!');
      return;
    }

    // Verificar se todos os atributos foram atribuídos no método aventureiro
    if (estado.metodoDistribuicao === MetodoDistribuicao.AVENTUREIRO) {
      const atributosCompletos = Object.keys(estado.atributosDistribuicao.atributosAssignados).length === 6;
      if (!atributosCompletos) {
        Alert.alert('Erro', 'Distribua todos os valores de atributos!');
        return;
      }
    }

    try {
      const race = RaceFactory.createRace(estado.raca);
      const characterClass = ClassFactory.createClass(estado.classe);
      
      // Obter atributos finais
      let atributosFinal: Atributos;
      if (estado.metodoDistribuicao === MetodoDistribuicao.AVENTUREIRO) {
        atributosFinal = estado.atributosDistribuicao.atributosAssignados as Atributos;
      } else {
        atributosFinal = {
          forca: estado.atributosDistribuicao.valores[0],
          destreza: estado.atributosDistribuicao.valores[1],
          constituicao: estado.atributosDistribuicao.valores[2],
          inteligencia: estado.atributosDistribuicao.valores[3],
          sabedoria: estado.atributosDistribuicao.valores[4],
          carisma: estado.atributosDistribuicao.valores[5]
        };
      }
      
      // Aplicar modificadores raciais
      const raceModifiers = race.getModificadoresAtributos();
      const finalAttributes: Atributos = {
        forca: atributosFinal.forca + (raceModifiers.forca || 0),
        destreza: atributosFinal.destreza + (raceModifiers.destreza || 0),
        constituicao: atributosFinal.constituicao + (raceModifiers.constituicao || 0),
        inteligencia: atributosFinal.inteligencia + (raceModifiers.inteligencia || 0),
        sabedoria: atributosFinal.sabedoria + (raceModifiers.sabedoria || 0),
        carisma: atributosFinal.carisma + (raceModifiers.carisma || 0),
      };

      const modifiers = calcularModificadores(finalAttributes);
      
      // Calcular pontos de vida iniciais
      const hitPoints = DiceUtils.rollHitPoints(
        characterClass.getDadoDeVida(),
        finalAttributes.constituicao
      );

      // Calcular classe de armadura base
      const baseAC = 10 + modifiers.destreza;

      const newCharacter: Personagem = {
        nome: estado.nome,
        raca: estado.raca,
        classe: estado.classe,
        nivel: 1,
        experiencia: 0,
        atributos: finalAttributes,
        modificadores: modifiers,
        pontosDeVida: hitPoints,
        pontosVidaMaximos: hitPoints,
        classeDeArmadura: baseAC,
        baseDeAtaque: characterClass.getBaseDeAtaque(1),
        movimento: race.getMovimento(),
        inventario: [],
        habilidades: [...race.getHabilidades(), ...characterClass.getHabilidades()],
        dinheiro: DiceUtils.rollDice(3, 6) * 10, // 3d6 x 10 moedas de ouro iniciais
      };

      setPersonagem(newCharacter);
      onNavigate(EstadoJogo.AVENTURA);
    } catch (error) {
      Alert.alert('Erro', 'Erro ao criar personagem: ' + error);
    }
  };

  const renderEtapa1 = () => (
    <View style={styles.stepContainer}>
      <Text style={styles.stepTitle}>1. Nome do Personagem</Text>
      <TextInput
        style={styles.input}
        placeholder="Digite o nome do seu herói"
        placeholderTextColor="#999"
        value={estado.nome}
        onChangeText={(text) => setEstado(prev => ({ ...prev, nome: text }))}
      />
      <TouchableOpacity 
        style={[styles.button, !estado.nome.trim() && styles.buttonDisabled]}
        onPress={avancarEtapa}
        disabled={!estado.nome.trim()}
      >
        <Text style={styles.buttonText}>Próximo</Text>
      </TouchableOpacity>
    </View>
  );

  const renderEtapa2 = () => (
    <View style={styles.stepContainer}>
      <Text style={styles.stepTitle}>2. Método de Distribuição de Atributos</Text>
      <Text style={styles.description}>
        Escolha como seus atributos serão determinados:
      </Text>
      
      {Object.values(MetodoDistribuicao).map((metodo) => (
        <TouchableOpacity
          key={metodo}
          style={[
            styles.optionButton,
            estado.metodoDistribuicao === metodo && styles.optionButtonSelected
          ]}
          onPress={() => setEstado(prev => ({ ...prev, metodoDistribuicao: metodo }))}
        >
          <Text style={[
            styles.optionText,
            estado.metodoDistribuicao === metodo && styles.optionTextSelected
          ]}>
            {metodo}
          </Text>
          <Text style={styles.optionDescription}>
            {metodo === MetodoDistribuicao.CLASSICA && '3d6 em ordem fixa (Força, Destreza, etc.)'}
            {metodo === MetodoDistribuicao.HEROICA && '4d6, descartar menor, em ordem fixa'}
            {metodo === MetodoDistribuicao.AVENTUREIRO && '4d6, descartar menor, distribuir livremente'}
          </Text>
        </TouchableOpacity>
      ))}
      
      <View style={styles.buttonRow}>
        <TouchableOpacity style={styles.buttonSecondary} onPress={voltarEtapa}>
          <Text style={styles.buttonText}>Voltar</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.button} onPress={avancarEtapa}>
          <Text style={styles.buttonText}>Próximo</Text>
        </TouchableOpacity>
      </View>
    </View>
  );

  const renderEtapa3 = () => (
    <View style={styles.stepContainer}>
      <Text style={styles.stepTitle}>3. Gerar Atributos</Text>
      <Text style={styles.description}>
        Método selecionado: {estado.metodoDistribuicao}
      </Text>
      
      {estado.atributosDistribuicao.valores.length === 0 ? (
        <TouchableOpacity style={styles.button} onPress={gerarAtributos}>
          <Text style={styles.buttonText}>🎲 Rolar Atributos</Text>
        </TouchableOpacity>
      ) : (
        <View>
          <Text style={styles.subtitle}>Valores gerados:</Text>
          <View style={styles.attributeGrid}>
            {estado.atributosDistribuicao.valores.map((valor, index) => (
              <View key={index} style={styles.attributeValue}>
                <Text style={styles.attributeNumber}>{valor}</Text>
              </View>
            ))}
          </View>
          
          {estado.metodoDistribuicao !== MetodoDistribuicao.AVENTUREIRO && (
            <View style={styles.attributeAssignment}>
              <Text style={styles.subtitle}>Atribuição automática:</Text>
              <Text style={styles.attributeText}>Força: {estado.atributosDistribuicao.valores[0]}</Text>
              <Text style={styles.attributeText}>Destreza: {estado.atributosDistribuicao.valores[1]}</Text>
              <Text style={styles.attributeText}>Constituição: {estado.atributosDistribuicao.valores[2]}</Text>
              <Text style={styles.attributeText}>Inteligência: {estado.atributosDistribuicao.valores[3]}</Text>
              <Text style={styles.attributeText}>Sabedoria: {estado.atributosDistribuicao.valores[4]}</Text>
              <Text style={styles.attributeText}>Carisma: {estado.atributosDistribuicao.valores[5]}</Text>
            </View>
          )}
          
          <View style={styles.buttonRow}>
            <TouchableOpacity style={styles.buttonSecondary} onPress={() => 
              setEstado(prev => ({ 
                ...prev, 
                atributosDistribuicao: { ...prev.atributosDistribuicao, valores: [] }
              }))
            }>
              <Text style={styles.buttonText}>Rolar Novamente</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.button} onPress={avancarEtapa}>
              <Text style={styles.buttonText}>Próximo</Text>
            </TouchableOpacity>
          </View>
        </View>
      )}
      
      <TouchableOpacity style={styles.buttonSecondary} onPress={voltarEtapa}>
        <Text style={styles.buttonText}>Voltar</Text>
      </TouchableOpacity>
    </View>
  );

  const renderEtapa4 = () => {
    if (estado.metodoDistribuicao !== MetodoDistribuicao.AVENTUREIRO) {
      // Pular esta etapa para métodos que não permitem distribuição livre
      avancarEtapa();
      return null;
    }

    const atributosDisponiveis = estado.atributosDistribuicao.valores.filter(valor => 
      !Object.values(estado.atributosDistribuicao.atributosAssignados).includes(valor)
    );

    return (
      <View style={styles.stepContainer}>
        <Text style={styles.stepTitle}>4. Distribuir Atributos</Text>
        <Text style={styles.description}>
          Atribua os valores rolados aos atributos de sua escolha:
        </Text>
        
        <View style={styles.attributeDistribution}>
          {(['forca', 'destreza', 'constituicao', 'inteligencia', 'sabedoria', 'carisma'] as (keyof Atributos)[]).map(atributo => (
            <View key={atributo} style={styles.attributeRow}>
              <Text style={styles.attributeLabel}>
                {atributo.charAt(0).toUpperCase() + atributo.slice(1)}:
              </Text>
              <Text style={styles.attributeAssigned}>
                {estado.atributosDistribuicao.atributosAssignados[atributo] || '---'}
              </Text>
              <ScrollView horizontal style={styles.valueSelector}>
                {atributosDisponiveis.map(valor => (
                  <TouchableOpacity
                    key={valor}
                    style={styles.valueButton}
                    onPress={() => atribuirValor(atributo, valor)}
                  >
                    <Text style={styles.valueButtonText}>{valor}</Text>
                  </TouchableOpacity>
                ))}
              </ScrollView>
            </View>
          ))}
        </View>
        
        <View style={styles.buttonRow}>
          <TouchableOpacity style={styles.buttonSecondary} onPress={voltarEtapa}>
            <Text style={styles.buttonText}>Voltar</Text>
          </TouchableOpacity>
          <TouchableOpacity 
            style={[
              styles.button, 
              Object.keys(estado.atributosDistribuicao.atributosAssignados).length < 6 && styles.buttonDisabled
            ]}
            onPress={avancarEtapa}
            disabled={Object.keys(estado.atributosDistribuicao.atributosAssignados).length < 6}
          >
            <Text style={styles.buttonText}>Próximo</Text>
          </TouchableOpacity>
        </View>
      </View>
    );
  };

  const renderEtapa5 = () => (
    <View style={styles.stepContainer}>
      <Text style={styles.stepTitle}>5. Escolher Raça</Text>
      <ScrollView style={styles.raceList}>
        {races.map((raceName) => {
          const race = RaceFactory.createRace(raceName);
          return (
            <TouchableOpacity
              key={raceName}
              style={[
                styles.raceButton,
                estado.raca === raceName && styles.raceButtonSelected
              ]}
              onPress={() => setEstado(prev => ({ ...prev, raca: raceName }))}
            >
              <Text style={[
                styles.raceTitle,
                estado.raca === raceName && styles.raceTextSelected
              ]}>
                {race.getNome()}
              </Text>
              <Text style={styles.raceDescription}>
                {race.getDescricao()}
              </Text>
              <Text style={styles.raceModifiers}>
                Movimento: {race.getMovimento()}m
              </Text>
            </TouchableOpacity>
          );
        })}
      </ScrollView>
      
      <View style={styles.buttonRow}>
        <TouchableOpacity style={styles.buttonSecondary} onPress={voltarEtapa}>
          <Text style={styles.buttonText}>Voltar</Text>
        </TouchableOpacity>
        <TouchableOpacity 
          style={[styles.button, !estado.raca && styles.buttonDisabled]}
          onPress={avancarEtapa}
          disabled={!estado.raca}
        >
          <Text style={styles.buttonText}>Próximo</Text>
        </TouchableOpacity>
      </View>
    </View>
  );

  const renderEtapa6 = () => (
    <View style={styles.stepContainer}>
      <Text style={styles.stepTitle}>6. Escolher Classe</Text>
      <ScrollView style={styles.classList}>
        {classes.map((className) => {
          const characterClass = ClassFactory.createClass(className);
          return (
            <TouchableOpacity
              key={className}
              style={[
                styles.classButton,
                estado.classe === className && styles.classButtonSelected
              ]}
              onPress={() => setEstado(prev => ({ ...prev, classe: className }))}
            >
              <Text style={[
                styles.classTitle,
                estado.classe === className && styles.classTextSelected
              ]}>
                {characterClass.getNome()}
              </Text>
              <Text style={styles.classDescription}>
                {characterClass.getDescricao()}
              </Text>
              <Text style={styles.classInfo}>
                Dado de Vida: d{characterClass.getDadoDeVida()}
              </Text>
            </TouchableOpacity>
          );
        })}
      </ScrollView>
      
      <View style={styles.buttonRow}>
        <TouchableOpacity style={styles.buttonSecondary} onPress={voltarEtapa}>
          <Text style={styles.buttonText}>Voltar</Text>
        </TouchableOpacity>
        <TouchableOpacity 
          style={[styles.button, !estado.classe && styles.buttonDisabled]}
          onPress={finalizarPersonagem}
          disabled={!estado.classe}
        >
          <Text style={styles.buttonText}>Criar Personagem</Text>
        </TouchableOpacity>
      </View>
    </View>
  );

  return (
    <View style={styles.container}>
      <Image 
        source={require('../../assets/sprites/game_logo.png')} 
        style={styles.logo}
        resizeMode="contain"
      />
      
      <Text style={styles.title}>Criação de Personagem</Text>
      
      <View style={styles.progressBar}>
        {[1, 2, 3, 4, 5, 6].map(step => (
          <View 
            key={step}
            style={[
              styles.progressStep,
              step <= estado.etapa && styles.progressStepActive
            ]}
          />
        ))}
      </View>
      
      <ScrollView style={styles.content}>
        {estado.etapa === 1 && renderEtapa1()}
        {estado.etapa === 2 && renderEtapa2()}
        {estado.etapa === 3 && renderEtapa3()}
        {estado.etapa === 4 && renderEtapa4()}
        {estado.etapa === 5 && renderEtapa5()}
        {estado.etapa === 6 && renderEtapa6()}
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#1a1a1a',
    padding: 20,
  },
  logo: {
    width: 120,
    height: 60,
    alignSelf: 'center',
    marginBottom: 10,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#fff',
    textAlign: 'center',
    marginBottom: 20,
  },
  progressBar: {
    flexDirection: 'row',
    justifyContent: 'center',
    marginBottom: 20,
  },
  progressStep: {
    width: 30,
    height: 4,
    backgroundColor: '#333',
    marginHorizontal: 2,
    borderRadius: 2,
  },
  progressStepActive: {
    backgroundColor: '#4ecdc4',
  },
  content: {
    flex: 1,
  },
  stepContainer: {
    flex: 1,
  },
  stepTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#4ecdc4',
    marginBottom: 15,
    textAlign: 'center',
  },
  description: {
    fontSize: 16,
    color: '#ccc',
    textAlign: 'center',
    marginBottom: 20,
  },
  input: {
    backgroundColor: '#333',
    color: '#fff',
    padding: 15,
    borderRadius: 8,
    fontSize: 16,
    marginBottom: 20,
    borderWidth: 1,
    borderColor: '#555',
  },
  button: {
    backgroundColor: '#4ecdc4',
    padding: 15,
    borderRadius: 8,
    alignItems: 'center',
    marginVertical: 5,
  },
  buttonSecondary: {
    backgroundColor: '#666',
    padding: 15,
    borderRadius: 8,
    alignItems: 'center',
    marginVertical: 5,
    flex: 1,
    marginRight: 10,
  },
  buttonDisabled: {
    backgroundColor: '#333',
    opacity: 0.5,
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
  buttonRow: {
    flexDirection: 'row',
    marginTop: 20,
  },
  optionButton: {
    backgroundColor: '#333',
    padding: 15,
    borderRadius: 8,
    marginBottom: 10,
    borderWidth: 2,
    borderColor: '#555',
  },
  optionButtonSelected: {
    borderColor: '#4ecdc4',
    backgroundColor: '#2a4a47',
  },
  optionText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 5,
  },
  optionTextSelected: {
    color: '#4ecdc4',
  },
  optionDescription: {
    color: '#ccc',
    fontSize: 14,
  },
  attributeGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'center',
    marginBottom: 20,
  },
  attributeValue: {
    backgroundColor: '#4ecdc4',
    width: 50,
    height: 50,
    borderRadius: 25,
    justifyContent: 'center',
    alignItems: 'center',
    margin: 5,
  },
  attributeNumber: {
    color: '#fff',
    fontSize: 18,
    fontWeight: 'bold',
  },
  attributeAssignment: {
    backgroundColor: '#333',
    padding: 15,
    borderRadius: 8,
    marginBottom: 20,
  },
  subtitle: {
    color: '#4ecdc4',
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  attributeText: {
    color: '#fff',
    fontSize: 14,
    marginBottom: 5,
  },
  attributeDistribution: {
    marginBottom: 20,
  },
  attributeRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 15,
    backgroundColor: '#333',
    padding: 10,
    borderRadius: 8,
  },
  attributeLabel: {
    color: '#fff',
    fontSize: 14,
    width: 100,
  },
  attributeAssigned: {
    color: '#4ecdc4',
    fontSize: 16,
    fontWeight: 'bold',
    width: 50,
    textAlign: 'center',
  },
  valueSelector: {
    flex: 1,
  },
  valueButton: {
    backgroundColor: '#4ecdc4',
    paddingHorizontal: 15,
    paddingVertical: 8,
    borderRadius: 15,
    marginHorizontal: 5,
  },
  valueButtonText: {
    color: '#fff',
    fontWeight: 'bold',
  },
  raceList: {
    maxHeight: 400,
    marginBottom: 20,
  },
  raceButton: {
    backgroundColor: '#333',
    padding: 15,
    borderRadius: 8,
    marginBottom: 10,
    borderWidth: 2,
    borderColor: '#555',
  },
  raceButtonSelected: {
    borderColor: '#4ecdc4',
    backgroundColor: '#2a4a47',
  },
  raceTitle: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 5,
  },
  raceTextSelected: {
    color: '#4ecdc4',
  },
  raceDescription: {
    color: '#ccc',
    fontSize: 14,
    marginBottom: 5,
  },
  raceModifiers: {
    color: '#999',
    fontSize: 12,
  },
  classList: {
    maxHeight: 400,
    marginBottom: 20,
  },
  classButton: {
    backgroundColor: '#333',
    padding: 15,
    borderRadius: 8,
    marginBottom: 10,
    borderWidth: 2,
    borderColor: '#555',
  },
  classButtonSelected: {
    borderColor: '#4ecdc4',
    backgroundColor: '#2a4a47',
  },
  classTitle: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 5,
  },
  classTextSelected: {
    color: '#4ecdc4',
  },
  classDescription: {
    color: '#ccc',
    fontSize: 14,
    marginBottom: 5,
  },
  classInfo: {
    color: '#999',
    fontSize: 12,
  },
});
