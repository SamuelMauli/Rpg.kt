import React, { createContext, useContext, useReducer, ReactNode } from 'react';
import { Personagem, Monstro, EstadoCombate, Aventura } from '../types/GameTypes';

interface GameState {
  personagem: Personagem | null;
  aventuraAtual: Aventura | null;
  estadoCombate: EstadoCombate | null;
  experienciaTotal: number;
  dinheiro: number;
}

type GameAction = 
  | { type: 'SET_PERSONAGEM'; payload: Personagem }
  | { type: 'UPDATE_PERSONAGEM'; payload: Partial<Personagem> }
  | { type: 'SET_AVENTURA'; payload: Aventura }
  | { type: 'SET_COMBATE'; payload: EstadoCombate | null }
  | { type: 'ADD_EXPERIENCIA'; payload: number }
  | { type: 'ADD_DINHEIRO'; payload: number }
  | { type: 'RESET_GAME' };

const initialState: GameState = {
  personagem: null,
  aventuraAtual: null,
  estadoCombate: null,
  experienciaTotal: 0,
  dinheiro: 100, // Dinheiro inicial
};

function gameReducer(state: GameState, action: GameAction): GameState {
  switch (action.type) {
    case 'SET_PERSONAGEM':
      return {
        ...state,
        personagem: action.payload,
      };
    
    case 'UPDATE_PERSONAGEM':
      if (!state.personagem) return state;
      return {
        ...state,
        personagem: {
          ...state.personagem,
          ...action.payload,
        },
      };
    
    case 'SET_AVENTURA':
      return {
        ...state,
        aventuraAtual: action.payload,
      };
    
    case 'SET_COMBATE':
      return {
        ...state,
        estadoCombate: action.payload,
      };
    
    case 'ADD_EXPERIENCIA':
      const novaExperiencia = state.experienciaTotal + action.payload;
      let personagemAtualizado = state.personagem;
      
      if (personagemAtualizado) {
        personagemAtualizado = {
          ...personagemAtualizado,
          experiencia: novaExperiencia,
        };
        
        // Verificar se subiu de nível
        const progressao = personagemAtualizado.classe.getProgressaoExperiencia();
        const nivelAtual = personagemAtualizado.nivel;
        const proximoNivel = progressao.get(nivelAtual + 1);
        
        if (proximoNivel && novaExperiencia >= proximoNivel.experienciaMinima) {
          personagemAtualizado.nivel += 1;
          // Aqui poderia adicionar lógica para ganhar PV, habilidades, etc.
        }
      }
      
      return {
        ...state,
        experienciaTotal: novaExperiencia,
        personagem: personagemAtualizado,
      };
    
    case 'ADD_DINHEIRO':
      return {
        ...state,
        dinheiro: state.dinheiro + action.payload,
      };
    
    case 'RESET_GAME':
      return initialState;
    
    default:
      return state;
  }
}

interface GameContextType {
  state: GameState;
  dispatch: React.Dispatch<GameAction>;
  // Funções auxiliares
  setPersonagem: (personagem: Personagem) => void;
  updatePersonagem: (updates: Partial<Personagem>) => void;
  setAventura: (aventura: Aventura) => void;
  setCombate: (combate: EstadoCombate | null) => void;
  addExperiencia: (xp: number) => void;
  addDinheiro: (valor: number) => void;
  resetGame: () => void;
}

const GameContext = createContext<GameContextType | undefined>(undefined);

interface GameProviderProps {
  children: ReactNode;
}

export function GameProvider({ children }: GameProviderProps) {
  const [state, dispatch] = useReducer(gameReducer, initialState);

  const contextValue: GameContextType = {
    state,
    dispatch,
    setPersonagem: (personagem: Personagem) => 
      dispatch({ type: 'SET_PERSONAGEM', payload: personagem }),
    updatePersonagem: (updates: Partial<Personagem>) => 
      dispatch({ type: 'UPDATE_PERSONAGEM', payload: updates }),
    setAventura: (aventura: Aventura) => 
      dispatch({ type: 'SET_AVENTURA', payload: aventura }),
    setCombate: (combate: EstadoCombate | null) => 
      dispatch({ type: 'SET_COMBATE', payload: combate }),
    addExperiencia: (xp: number) => 
      dispatch({ type: 'ADD_EXPERIENCIA', payload: xp }),
    addDinheiro: (valor: number) => 
      dispatch({ type: 'ADD_DINHEIRO', payload: valor }),
    resetGame: () => 
      dispatch({ type: 'RESET_GAME' }),
  };

  return (
    <GameContext.Provider value={contextValue}>
      {children}
    </GameContext.Provider>
  );
}

export function useGame() {
  const context = useContext(GameContext);
  if (context === undefined) {
    throw new Error('useGame must be used within a GameProvider');
  }
  return context;
}
