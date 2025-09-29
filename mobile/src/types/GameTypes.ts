// Tipos principais do jogo RPG
export interface Atributos {
  forca: number;
  destreza: number;
  constituicao: number;
  inteligencia: number;
  sabedoria: number;
  carisma: number;
}

export interface ModificadoresAtributos {
  forca: number;
  destreza: number;
  constituicao: number;
  inteligencia: number;
  sabedoria: number;
  carisma: number;
}

export interface ProgressaoExperiencia {
  nivel: number;
  experienciaMinima: number;
  experienciaProxima: number;
}

export interface ProgressaoAtributos {
  nivel: number;
  pontosVida: string;
  baseAtaque: number;
  classeArmadura: number;
}

export enum TipoArma {
  PEQUENA = 'pequena',
  MEDIA = 'media',
  GRANDE = 'grande'
}

export enum TipoArmadura {
  LEVE = 'leve',
  MEDIA = 'media',
  PESADA = 'pesada'
}

export enum TipoItem {
  ARMA = 'arma',
  ARMADURA = 'armadura',
  ESCUDO = 'escudo',
  ITEM_MAGICO = 'item_magico',
  POCAO = 'pocao'
}

export interface HabilidadeRacial {
  nome: string;
  descricao: string;
  aplicar(personagem: Personagem): void;
}

export interface HabilidadeDeClasse {
  nome: string;
  descricao: string;
  nivel: number;
  aplicar(personagem: Personagem): void;
}

export abstract class Raca {
  abstract getNome(): string;
  abstract getDescricao(): string;
  abstract getModificadoresAtributos(): Partial<Atributos>;
  abstract getHabilidades(): HabilidadeRacial[];
  abstract getMovimento(): number;
  abstract getIdiomas(): string[];
}

export abstract class Classe {
  abstract getNome(): string;
  abstract getDescricao(): string;
  abstract getDadoDeVida(): number;
  abstract getProgressaoExperiencia(): Map<number, ProgressaoExperiencia>;
  abstract getProgressaoAtributos(): Map<number, ProgressaoAtributos>;
  abstract getHabilidades(): HabilidadeDeClasse[];
  abstract getArmasPermitidas(): TipoArma[];
  abstract getArmadurasPermitidas(): TipoArmadura[];
  abstract getItensPermitidos(): TipoItem[];
  abstract getRestricoes(): string[];
}

export interface Personagem {
  nome: string;
  raca: Raca;
  classe: Classe;
  nivel: number;
  experiencia: number;
  atributos: Atributos;
  modificadores: ModificadoresAtributos;
  pontosVida: number;
  pontosVidaMaximos: number;
  classeArmadura: number;
  baseAtaque: number;
  movimento: number;
  inventario: Inventario;
  habilidades: (HabilidadeRacial | HabilidadeDeClasse)[];
  dinheiro: number;
}

export interface Item {
  nome: string;
  tipo: TipoItem;
  descricao: string;
  peso: number;
  valor: number;
}

export interface Arma extends Item {
  dano: string;
  tipoArma: TipoArma;
  alcance: number;
}

export interface Armadura extends Item {
  classeArmadura: number;
  tipoArmadura: TipoArmadura;
}

export interface Pocao extends Item {
  efeito: string;
  quantidade: number;
}

export interface Inventario {
  itens: Item[];
  capacidade: number;
  pesoAtual: number;
  pesoMaximo: number;
}

export interface Monstro {
  nome: string;
  tipo: string;
  dadosDeVida: number;
  pontosDeVida: number;
  classeArmadura: number;
  baseAtaque: number;
  dano: string;
  movimento: number;
  moral: number;
  tesouro: string;
  habilidadesEspeciais: string[];
  experiencia: number;
  tamanho: string;
}

export interface EstatisticasMonstro {
  nome: string;
  tipo: string;
  dadosVida: number;
  ca: number;
  baseAtaque: number;
  dano: string;
  movimento: number;
  moral: number;
  tesouro: string;
  xp: number;
  habilidades: string[];
  tamanho: string;
}

export enum EstadoJogo {
  MENU_PRINCIPAL = 'menu_principal',
  CRIACAO_PERSONAGEM = 'criacao_personagem',
  AVENTURA = 'aventura',
  COMBATE = 'combate',
  INVENTARIO = 'inventario',
  STATUS = 'status'
}

export interface EventoAventura {
  tipo: 'combate' | 'exploracao' | 'tesouro' | 'armadilha' | 'descanso';
  descricao: string;
  opcoes: string[];
  consequencias: any[];
}

export interface AreaAventura {
  nome: string;
  descricao: string;
  eventos: EventoAventura[];
  monstrosDisponiveis: string[];
  nivelDesafio: number;
  background: string;
}

export interface Aventura {
  nome: string;
  descricao: string;
  areas: AreaAventura[];
  areaAtual: number;
  eventoAtual: number;
}

export interface EstadoCombate {
  personagem: Personagem;
  monstros: Monstro[];
  turno: number;
  iniciativaPersonagem: number;
  iniciativaMonstros: number[];
  combateAtivo: boolean;
}

export interface ResultadoCombate {
  vitoria: boolean;
  experienciaGanha: number;
  tesouroEncontrado: Item[];
  danoRecebido: number;
}
