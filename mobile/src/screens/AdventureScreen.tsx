import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
  ImageBackground,
  Image,
  Alert,
} from 'react-native';
import { EstadoJogo, EventoAventura, AreaAventura } from '../types/GameTypes';
import { useGame } from '../contexts/GameContext';
import { MonsterFactory } from '../game/monsters/MonsterFactory';
import { CombatManager } from '../game/combat/CombatManager';

interface AdventureScreenProps {
  onNavigate: (screen: EstadoJogo) => void;
}

// Definir as áreas da aventura "O Segredo da Montanha Gelada"
const ADVENTURE_AREAS: AreaAventura[] = [
  {
    nome: 'Entrada da Caverna',
    descricao: 'Você está diante da entrada sombria de uma caverna na montanha gelada. O vento frio sussurra através das pedras, e você pode sentir uma presença maligna emanando das profundezas.',
    nivelDesafio: 1,
    background: 'cave_entrance_bg',
    monstrosDisponiveis: ['rato_gigante', 'kobold'],
    eventos: [
      {
        tipo: 'exploracao',
        descricao: 'Você examina a entrada da caverna. Há pegadas antigas no chão e marcas de garras nas paredes.',
        opcoes: ['Entrar na caverna', 'Examinar as pegadas', 'Procurar por armadilhas'],
        consequencias: ['avançar', 'informacao', 'teste_percepcao']
      },
      {
        tipo: 'combate',
        descricao: 'Ratos gigantes emergem das sombras, com olhos vermelhos brilhando na escuridão!',
        opcoes: ['Lutar', 'Tentar fugir'],
        consequencias: ['combate', 'fuga']
      }
    ]
  },
  {
    nome: 'Túneis Profundos',
    descricao: 'Os túneis se estendem em múltiplas direções. Tochas antigas ainda ardem nas paredes, lançando sombras dançantes. O ar está pesado com o cheiro de decomposição.',
    nivelDesafio: 2,
    background: 'dungeon_corridor_bg',
    monstrosDisponiveis: ['goblin', 'esqueleto'],
    eventos: [
      {
        tipo: 'combate',
        descricao: 'Goblins armados bloqueiam seu caminho, gritando em sua língua gutural!',
        opcoes: ['Atacar', 'Tentar negociar', 'Procurar rota alternativa'],
        consequencias: ['combate', 'dialogo', 'exploracao']
      },
      {
        tipo: 'tesouro',
        descricao: 'Você encontra um baú antigo escondido atrás de algumas pedras.',
        opcoes: ['Abrir o baú', 'Examinar por armadilhas primeiro', 'Ignorar'],
        consequencias: ['tesouro', 'teste_armadilha', 'continuar']
      }
    ]
  },
  {
    nome: 'Câmara dos Ecos',
    descricao: 'Uma grande câmara circular com runas místicas gravadas nas paredes. Seus passos ecoam infinitamente, e você sente uma energia mágica pulsando no ar.',
    nivelDesafio: 3,
    background: 'dungeon_corridor_bg',
    monstrosDisponiveis: ['orc', 'zumbi'],
    eventos: [
      {
        tipo: 'exploracao',
        descricao: 'As runas nas paredes começam a brilhar quando você se aproxima. Elas parecem formar um padrão específico.',
        opcoes: ['Tocar as runas', 'Estudar o padrão', 'Ignorar e continuar'],
        consequencias: ['magia', 'puzzle', 'avançar']
      },
      {
        tipo: 'combate',
        descricao: 'Zumbis emergem do chão, seus gemidos ecoando pela câmara!',
        opcoes: ['Lutar', 'Usar as runas contra eles', 'Recuar'],
        consequencias: ['combate', 'magia_combate', 'recuar']
      }
    ]
  },
  {
    nome: 'Sala dos Rituais',
    descricao: 'Uma sala sinistra com um altar no centro, cercado por círculos mágicos. Velas negras queimam com chamas roxas, e você pode sentir a presença do mal se intensificando.',
    nivelDesafio: 4,
    background: 'ritual_chamber_bg',
    monstrosDisponiveis: ['bugbear'],
    eventos: [
      {
        tipo: 'exploracao',
        descricao: 'O altar está coberto de símbolos necromânticos. Há um livro aberto com páginas que parecem se mover sozinhas.',
        opcoes: ['Ler o livro', 'Destruir o altar', 'Procurar por pistas'],
        consequencias: ['conhecimento', 'combate_altar', 'investigacao']
      },
      {
        tipo: 'armadilha',
        descricao: 'Ao se aproximar do altar, você pisa em uma placa de pressão. Dardos venenosos voam em sua direção!',
        opcoes: ['Esquivar', 'Usar escudo', 'Aceitar o dano'],
        consequencias: ['teste_destreza', 'bloqueio', 'dano']
      }
    ]
  },
  {
    nome: 'Santuário do Necromante',
    descricao: 'O covil final do necromante Malachar. A sala pulsa com energia sombria, e você pode ver o mago encapuzado realizando um ritual diante de um portal dimensional.',
    nivelDesafio: 5,
    background: 'ritual_chamber_bg',
    monstrosDisponiveis: ['necromante'],
    eventos: [
      {
        tipo: 'combate',
        descricao: 'Malachar se vira para você, seus olhos brilhando com poder necromântico. "Você chegou longe demais, mortal!"',
        opcoes: ['Atacar imediatamente', 'Tentar interromper o ritual', 'Desafiá-lo para um duelo'],
        consequencias: ['combate_boss', 'ritual_interrupt', 'duelo']
      }
    ]
  }
];

export default function AdventureScreen({ onNavigate }: AdventureScreenProps) {
  const { state, setCombate } = useGame();
  const [currentArea, setCurrentArea] = useState(0);
  const [currentEvent, setCurrentEvent] = useState(0);
  const [eventHistory, setEventHistory] = useState<string[]>([]);

  const area = ADVENTURE_AREAS[currentArea];
  const evento = area.eventos[currentEvent];

  const addToHistory = (text: string) => {
    setEventHistory(prev => [...prev, text]);
  };

  const handleChoice = (choiceIndex: number) => {
    const choice = evento.opcoes[choiceIndex];
    const consequence = evento.consequencias[choiceIndex];

    addToHistory(`> ${choice}`);

    switch (consequence) {
      case 'combate':
      case 'combate_boss':
        startCombat();
        break;
      case 'avançar':
        advanceArea();
        break;
      case 'tesouro':
        findTreasure();
        break;
      case 'fuga':
        attemptFlee();
        break;
      case 'teste_percepcao':
        performPerceptionTest();
        break;
      case 'dano':
        takeDamage();
        break;
      default:
        addToHistory('Você continua sua jornada...');
        nextEvent();
    }
  };

  const startCombat = () => {
    if (!state.personagem) return;

    const monsters = MonsterFactory.criarGrupoAleatorio(area.nivelDesafio);
    const combatManager = new CombatManager();
    const combatState = combatManager.iniciarCombate(state.personagem, monsters);
    
    setCombate(combatState);
    onNavigate(EstadoJogo.COMBATE);
  };

  const advanceArea = () => {
    if (currentArea < ADVENTURE_AREAS.length - 1) {
      setCurrentArea(currentArea + 1);
      setCurrentEvent(0);
      addToHistory(`Você avança para: ${ADVENTURE_AREAS[currentArea + 1].nome}`);
    } else {
      addToHistory('Você completou a aventura! Parabéns, herói!');
      Alert.alert(
        'Aventura Completa!',
        'Você derrotou o necromante e salvou a região da Montanha Gelada!',
        [{ text: 'Voltar ao Menu', onPress: () => onNavigate(EstadoJogo.MENU_PRINCIPAL) }]
      );
    }
  };

  const nextEvent = () => {
    if (currentEvent < area.eventos.length - 1) {
      setCurrentEvent(currentEvent + 1);
    } else {
      // Se não há mais eventos, avançar para próxima área
      advanceArea();
    }
  };

  const findTreasure = () => {
    const treasures = ['Poção de Cura', 'Moedas de Ouro', 'Gema Preciosa'];
    const treasure = treasures[Math.floor(Math.random() * treasures.length)];
    addToHistory(`Você encontrou: ${treasure}!`);
    nextEvent();
  };

  const attemptFlee = () => {
    if (Math.random() < 0.7) {
      addToHistory('Você conseguiu fugir com sucesso!');
      nextEvent();
    } else {
      addToHistory('Você não conseguiu escapar!');
      startCombat();
    }
  };

  const performPerceptionTest = () => {
    if (state.personagem) {
      const success = Math.random() < 0.6; // 60% de chance
      if (success) {
        addToHistory('Você nota pegadas de criaturas humanoides e sinais de atividade recente.');
      } else {
        addToHistory('Você não consegue discernir muito das marcas no chão.');
      }
    }
    nextEvent();
  };

  const takeDamage = () => {
    if (state.personagem) {
      const damage = Math.floor(Math.random() * 6) + 1; // 1d6 de dano
      state.personagem.pontosVida = Math.max(0, state.personagem.pontosVida - damage);
      addToHistory(`Você sofreu ${damage} pontos de dano!`);
      
      if (state.personagem.pontosVida <= 0) {
        Alert.alert(
          'Game Over',
          'Seu personagem morreu! A aventura termina aqui.',
          [{ text: 'Voltar ao Menu', onPress: () => onNavigate(EstadoJogo.MENU_PRINCIPAL) }]
        );
        return;
      }
    }
    nextEvent();
  };

  const getBackgroundImage = () => {
    switch (area.background) {
      case 'cave_entrance_bg':
        return require('../../assets/sprites/cave_entrance_bg.png');
      case 'dungeon_corridor_bg':
        return require('../../assets/sprites/dungeon_corridor_bg.png');
      case 'ritual_chamber_bg':
        return require('../../assets/sprites/ritual_chamber_bg.png');
      default:
        return require('../../assets/sprites/dungeon_corridor_bg.png');
    }
  };

  return (
    <ImageBackground
      source={getBackgroundImage()}
      style={styles.background}
      resizeMode="cover"
    >
      <View style={styles.overlay}>
        <View style={styles.container}>
          {/* Header com informações do personagem */}
          <View style={styles.header}>
            <View style={styles.characterInfo}>
              <Text style={styles.characterName}>{state.personagem?.nome}</Text>
              <Text style={styles.characterStats}>
                PV: {state.personagem?.pontosVida}/{state.personagem?.pontosVidaMaximos}
              </Text>
            </View>
            <TouchableOpacity
              style={styles.menuButton}
              onPress={() => onNavigate(EstadoJogo.STATUS)}
            >
              <Text style={styles.menuButtonText}>STATUS</Text>
            </TouchableOpacity>
          </View>

          {/* Área atual */}
          <View style={styles.areaInfo}>
            <Text style={styles.areaName}>{area.nome}</Text>
            <Text style={styles.areaDescription}>{area.descricao}</Text>
          </View>

          {/* Histórico de eventos */}
          <ScrollView style={styles.historyContainer}>
            {eventHistory.map((entry, index) => (
              <Text key={index} style={styles.historyText}>
                {entry}
              </Text>
            ))}
          </ScrollView>

          {/* Evento atual */}
          <View style={styles.eventContainer}>
            <Text style={styles.eventDescription}>{evento.descricao}</Text>
            
            <View style={styles.choicesContainer}>
              {evento.opcoes.map((choice, index) => (
                <TouchableOpacity
                  key={index}
                  style={styles.choiceButton}
                  onPress={() => handleChoice(index)}
                >
                  <Text style={styles.choiceText}>{choice}</Text>
                </TouchableOpacity>
              ))}
            </View>
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
    backgroundColor: 'rgba(0, 0, 0, 0.8)',
  },
  container: {
    flex: 1,
    padding: 15,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 15,
    paddingTop: 20,
  },
  characterInfo: {
    flex: 1,
  },
  characterName: {
    color: '#FFD700',
    fontSize: 18,
    fontWeight: 'bold',
  },
  characterStats: {
    color: '#FFF',
    fontSize: 14,
  },
  menuButton: {
    backgroundColor: '#8B4513',
    borderWidth: 2,
    borderColor: '#D2691E',
    borderRadius: 6,
    paddingVertical: 8,
    paddingHorizontal: 15,
  },
  menuButtonText: {
    color: '#FFD700',
    fontSize: 14,
    fontWeight: 'bold',
  },
  areaInfo: {
    backgroundColor: 'rgba(139, 69, 19, 0.9)',
    borderRadius: 8,
    padding: 15,
    marginBottom: 15,
    borderWidth: 2,
    borderColor: '#D2691E',
  },
  areaName: {
    color: '#FFD700',
    fontSize: 20,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 8,
  },
  areaDescription: {
    color: '#FFF',
    fontSize: 14,
    textAlign: 'center',
    lineHeight: 20,
  },
  historyContainer: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
    borderRadius: 8,
    padding: 10,
    marginBottom: 15,
    maxHeight: 150,
  },
  historyText: {
    color: '#CCC',
    fontSize: 12,
    marginBottom: 5,
    lineHeight: 16,
  },
  eventContainer: {
    backgroundColor: 'rgba(139, 69, 19, 0.9)',
    borderRadius: 8,
    padding: 15,
    borderWidth: 2,
    borderColor: '#D2691E',
  },
  eventDescription: {
    color: '#FFF',
    fontSize: 16,
    marginBottom: 15,
    lineHeight: 22,
    textAlign: 'center',
  },
  choicesContainer: {
    gap: 10,
  },
  choiceButton: {
    backgroundColor: '#654321',
    borderWidth: 2,
    borderColor: '#8B4513',
    borderRadius: 6,
    paddingVertical: 12,
    paddingHorizontal: 15,
    alignItems: 'center',
  },
  choiceText: {
    color: '#FFD700',
    fontSize: 14,
    fontWeight: 'bold',
    textAlign: 'center',
  },
});
