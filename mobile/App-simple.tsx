import React, { useState } from 'react';
import {
  StyleSheet,
  Text,
  View,
  TouchableOpacity,
  TextInput,
  ScrollView,
  Alert,
  ActivityIndicator,
} from 'react-native';
import { StatusBar } from 'expo-status-bar';

// Configurar URL da API - ALTERE PARA SEU IP LOCAL
const API_URL = 'http://localhost:8080/api';

interface Personagem {
  id?: number;
  nome: string;
  raca: string;
  classe: string;
  nivel: number;
  pontosVida: number;
  pontosVidaMaximos: number;
  experiencia: number;
  dinheiro: number;
  forca?: number;
  destreza?: number;
  constituicao?: number;
  inteligencia?: number;
  sabedoria?: number;
  carisma?: number;
}

type Tela = 'menu' | 'criar' | 'listar' | 'personagem';

export default function App() {
  const [tela, setTela] = useState<Tela>('menu');
  const [loading, setLoading] = useState(false);
  const [personagens, setPersonagens] = useState<Personagem[]>([]);
  const [personagemAtual, setPersonagemAtual] = useState<Personagem | null>(null);
  
  // Formulário de criação
  const [nome, setNome] = useState('');
  const [raca, setRaca] = useState('humano');
  const [classe, setClasse] = useState('guerreiro');
  
  // Carregar personagens
  const carregarPersonagens = async () => {
    try {
      setLoading(true);
      const response = await fetch(`${API_URL}/personagens`);
      const data = await response.json();
      if (data.success) {
        setPersonagens(data.data || []);
      }
    } catch (error) {
      Alert.alert('Erro', 'Não foi possível carregar personagens. Verifique se o backend está rodando.');
    } finally {
      setLoading(false);
    }
  };
  
  // Criar personagem
  const criarPersonagem = async () => {
    if (!nome.trim()) {
      Alert.alert('Erro', 'Digite um nome para o personagem');
      return;
    }
    
    try {
      setLoading(true);
      const response = await fetch(`${API_URL}/personagens`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          nome,
          raca,
          classe,
          alinhamento: 'neutro'
        })
      });
      
      const data = await response.json();
      
      if (data.success) {
        Alert.alert('Sucesso', 'Personagem criado!');
        setNome('');
        setTela('menu');
      } else {
        Alert.alert('Erro', data.message || 'Não foi possível criar personagem');
      }
    } catch (error) {
      Alert.alert('Erro', 'Não foi possível conectar ao servidor. Verifique se o backend está rodando.');
    } finally {
      setLoading(false);
    }
  };
  
  // Iniciar combate
  const iniciarCombate = async () => {
    if (!personagemAtual) return;
    
    try {
      setLoading(true);
      const response = await fetch(`${API_URL}/combate/iniciar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          personagemNome: personagemAtual.nome
        })
      });
      
      const data = await response.json();
      
      if (data.success) {
        const resultado = data.data;
        
        if (resultado.resultado === 'vitoria') {
          Alert.alert(
            'Vitória!',
            `XP ganho: ${resultado.xpGanho}\nOuro ganho: ${resultado.ouroGanho}`,
            [{ text: 'OK', onPress: () => carregarPersonagemAtual() }]
          );
        } else {
          Alert.alert(
            'Derrota',
            'Você foi derrotado...',
            [{ text: 'OK', onPress: () => carregarPersonagemAtual() }]
          );
        }
      }
    } catch (error) {
      Alert.alert('Erro', 'Não foi possível iniciar combate');
    } finally {
      setLoading(false);
    }
  };
  
  // Carregar personagem atual
  const carregarPersonagemAtual = async () => {
    if (!personagemAtual) return;
    
    try {
      const response = await fetch(`${API_URL}/personagens/${personagemAtual.nome}`);
      const data = await response.json();
      if (data.success) {
        setPersonagemAtual(data.data);
      }
    } catch (error) {
      console.error('Erro ao carregar personagem');
    }
  };
  
  // Renderizar tela do menu
  const renderMenu = () => (
    <View style={styles.container}>
      <Text style={styles.title}>OLD DRAGON 2 RPG</Text>
      <Text style={styles.subtitle}>Mobile Edition</Text>
      
      <TouchableOpacity
        style={styles.button}
        onPress={() => setTela('criar')}
      >
        <Text style={styles.buttonText}>🆕 Criar Personagem</Text>
      </TouchableOpacity>
      
      <TouchableOpacity
        style={styles.button}
        onPress={() => {
          setTela('listar');
          carregarPersonagens();
        }}
      >
        <Text style={styles.buttonText}>📂 Carregar Personagem</Text>
      </TouchableOpacity>
      
      <View style={styles.footer}>
        <Text style={styles.footerText}>Versão 1.0.0</Text>
        <Text style={styles.footerText}>API: {API_URL}</Text>
      </View>
    </View>
  );
  
  // Renderizar tela de criação
  const renderCriar = () => (
    <ScrollView style={styles.scrollContainer}>
      <View style={styles.container}>
        <Text style={styles.title}>Criar Personagem</Text>
        
        <Text style={styles.label}>Nome:</Text>
        <TextInput
          style={styles.input}
          value={nome}
          onChangeText={setNome}
          placeholder="Digite o nome"
          placeholderTextColor="#999"
        />
        
        <Text style={styles.label}>Raça:</Text>
        <View style={styles.optionsContainer}>
          {['humano', 'elfo', 'anao', 'halfling'].map((r) => (
            <TouchableOpacity
              key={r}
              style={[styles.optionButton, raca === r && styles.optionButtonSelected]}
              onPress={() => setRaca(r)}
            >
              <Text style={[styles.optionText, raca === r && styles.optionTextSelected]}>
                {r.charAt(0).toUpperCase() + r.slice(1)}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
        
        <Text style={styles.label}>Classe:</Text>
        <View style={styles.optionsContainer}>
          {['guerreiro', 'mago', 'clerigo', 'ladrao'].map((c) => (
            <TouchableOpacity
              key={c}
              style={[styles.optionButton, classe === c && styles.optionButtonSelected]}
              onPress={() => setClasse(c)}
            >
              <Text style={[styles.optionText, classe === c && styles.optionTextSelected]}>
                {c.charAt(0).toUpperCase() + c.slice(1)}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
        
        {loading ? (
          <ActivityIndicator size="large" color="#FFD700" />
        ) : (
          <>
            <TouchableOpacity style={styles.button} onPress={criarPersonagem}>
              <Text style={styles.buttonText}>✨ Criar</Text>
            </TouchableOpacity>
            
            <TouchableOpacity
              style={[styles.button, styles.buttonSecondary]}
              onPress={() => setTela('menu')}
            >
              <Text style={styles.buttonText}>↩️ Voltar</Text>
            </TouchableOpacity>
          </>
        )}
      </View>
    </ScrollView>
  );
  
  // Renderizar lista de personagens
  const renderListar = () => (
    <ScrollView style={styles.scrollContainer}>
      <View style={styles.container}>
        <Text style={styles.title}>Personagens</Text>
        
        {loading ? (
          <ActivityIndicator size="large" color="#FFD700" />
        ) : personagens.length === 0 ? (
          <Text style={styles.emptyText}>Nenhum personagem encontrado</Text>
        ) : (
          personagens.map((p, index) => (
            <TouchableOpacity
              key={index}
              style={styles.personagemCard}
              onPress={() => {
                setPersonagemAtual(p);
                setTela('personagem');
              }}
            >
              <Text style={styles.personagemNome}>{p.nome}</Text>
              <Text style={styles.personagemInfo}>
                {p.raca} {p.classe} - Nível {p.nivel}
              </Text>
              <Text style={styles.personagemInfo}>
                PV: {p.pontosVida}/{p.pontosVidaMaximos} | XP: {p.experiencia}
              </Text>
            </TouchableOpacity>
          ))
        )}
        
        <TouchableOpacity
          style={[styles.button, styles.buttonSecondary]}
          onPress={() => setTela('menu')}
        >
          <Text style={styles.buttonText}>↩️ Voltar</Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
  );
  
  // Renderizar ficha do personagem
  const renderPersonagem = () => {
    if (!personagemAtual) return null;
    
    return (
      <ScrollView style={styles.scrollContainer}>
        <View style={styles.container}>
          <Text style={styles.title}>{personagemAtual.nome}</Text>
          <Text style={styles.subtitle}>
            {personagemAtual.raca} {personagemAtual.classe} - Nível {personagemAtual.nivel}
          </Text>
          
          <View style={styles.statsContainer}>
            <View style={styles.statBox}>
              <Text style={styles.statLabel}>PV</Text>
              <Text style={styles.statValue}>
                {personagemAtual.pontosVida}/{personagemAtual.pontosVidaMaximos}
              </Text>
            </View>
            
            <View style={styles.statBox}>
              <Text style={styles.statLabel}>XP</Text>
              <Text style={styles.statValue}>{personagemAtual.experiencia}</Text>
            </View>
            
            <View style={styles.statBox}>
              <Text style={styles.statLabel}>Ouro</Text>
              <Text style={styles.statValue}>{personagemAtual.dinheiro}</Text>
            </View>
          </View>
          
          {loading ? (
            <ActivityIndicator size="large" color="#FFD700" />
          ) : (
            <>
              <TouchableOpacity style={styles.button} onPress={iniciarCombate}>
                <Text style={styles.buttonText}>⚔️ Iniciar Combate</Text>
              </TouchableOpacity>
              
              <TouchableOpacity
                style={[styles.button, styles.buttonSecondary]}
                onPress={() => setTela('listar')}
              >
                <Text style={styles.buttonText}>↩️ Voltar</Text>
              </TouchableOpacity>
            </>
          )}
        </View>
      </ScrollView>
    );
  };
  
  return (
    <View style={styles.app}>
      <StatusBar style="light" />
      {tela === 'menu' && renderMenu()}
      {tela === 'criar' && renderCriar()}
      {tela === 'listar' && renderListar()}
      {tela === 'personagem' && renderPersonagem()}
    </View>
  );
}

const styles = StyleSheet.create({
  app: {
    flex: 1,
    backgroundColor: '#1a1a2e',
  },
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 20,
    backgroundColor: '#1a1a2e',
  },
  scrollContainer: {
    flex: 1,
    backgroundColor: '#1a1a2e',
  },
  title: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#FFD700',
    marginBottom: 10,
    textAlign: 'center',
  },
  subtitle: {
    fontSize: 18,
    color: '#fff',
    marginBottom: 30,
    textAlign: 'center',
  },
  button: {
    backgroundColor: '#FFD700',
    paddingVertical: 15,
    paddingHorizontal: 40,
    borderRadius: 10,
    marginVertical: 10,
    width: '100%',
    maxWidth: 300,
  },
  buttonSecondary: {
    backgroundColor: '#555',
  },
  buttonText: {
    color: '#1a1a2e',
    fontSize: 18,
    fontWeight: 'bold',
    textAlign: 'center',
  },
  label: {
    color: '#FFD700',
    fontSize: 16,
    fontWeight: 'bold',
    marginTop: 20,
    marginBottom: 10,
    alignSelf: 'flex-start',
  },
  input: {
    backgroundColor: '#2a2a3e',
    color: '#fff',
    padding: 15,
    borderRadius: 10,
    width: '100%',
    fontSize: 16,
    borderWidth: 2,
    borderColor: '#FFD700',
  },
  optionsContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'center',
    width: '100%',
    marginBottom: 10,
  },
  optionButton: {
    backgroundColor: '#2a2a3e',
    padding: 10,
    borderRadius: 8,
    margin: 5,
    borderWidth: 2,
    borderColor: '#555',
  },
  optionButtonSelected: {
    borderColor: '#FFD700',
    backgroundColor: '#3a3a4e',
  },
  optionText: {
    color: '#fff',
    fontSize: 14,
  },
  optionTextSelected: {
    color: '#FFD700',
    fontWeight: 'bold',
  },
  personagemCard: {
    backgroundColor: '#2a2a3e',
    padding: 20,
    borderRadius: 10,
    marginVertical: 10,
    width: '100%',
    borderWidth: 2,
    borderColor: '#FFD700',
  },
  personagemNome: {
    color: '#FFD700',
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 5,
  },
  personagemInfo: {
    color: '#fff',
    fontSize: 14,
    marginTop: 2,
  },
  emptyText: {
    color: '#999',
    fontSize: 16,
    textAlign: 'center',
    marginVertical: 30,
  },
  statsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    width: '100%',
    marginVertical: 20,
  },
  statBox: {
    backgroundColor: '#2a2a3e',
    padding: 15,
    borderRadius: 10,
    alignItems: 'center',
    borderWidth: 2,
    borderColor: '#FFD700',
    flex: 1,
    margin: 5,
  },
  statLabel: {
    color: '#FFD700',
    fontSize: 12,
    fontWeight: 'bold',
    marginBottom: 5,
  },
  statValue: {
    color: '#fff',
    fontSize: 18,
    fontWeight: 'bold',
  },
  footer: {
    position: 'absolute',
    bottom: 20,
    alignItems: 'center',
  },
  footerText: {
    color: '#666',
    fontSize: 12,
  },
});

