import { 
  Classe, 
  HabilidadeDeClasse, 
  ProgressaoExperiencia, 
  ProgressaoAtributos,
  TipoArma,
  TipoArmadura,
  TipoItem,
  Personagem 
} from '../../types/GameTypes';

// Habilidades de Classe
export class Aparar implements HabilidadeDeClasse {
  nome = 'Aparar';
  descricao = 'Pode aparar ataques com armas corpo a corpo';
  nivel = 1;
  
  aplicar(personagem: Personagem): void {
    // Implementação da habilidade de aparar
  }
}

export class MaestriaEmArma implements HabilidadeDeClasse {
  nome = 'Maestria em Arma';
  descricao = '+1 de bônus em ataques com arma escolhida';
  nivel = 3;
  
  aplicar(personagem: Personagem): void {
    // Implementação da maestria
  }
}

export class AtaqueExtra implements HabilidadeDeClasse {
  nome = 'Ataque Extra';
  descricao = 'Pode fazer um ataque adicional por turno';
  nivel = 5;
  
  aplicar(personagem: Personagem): void {
    // Implementação do ataque extra
  }
}

export class AtaqueFurtivo implements HabilidadeDeClasse {
  nome = 'Ataque Furtivo';
  descricao = 'Causa dano extra quando ataca pelas costas';
  nivel = 1;
  
  aplicar(personagem: Personagem): void {
    // Implementação do ataque furtivo
  }
}

export class EscalarParedes implements HabilidadeDeClasse {
  nome = 'Escalar Paredes';
  descricao = 'Pode escalar superfícies verticais';
  nivel = 1;
  
  aplicar(personagem: Personagem): void {
    // Implementação da escalada
  }
}

export class AbrirFechaduras implements HabilidadeDeClasse {
  nome = 'Abrir Fechaduras';
  descricao = 'Pode abrir fechaduras e desarmar armadilhas';
  nivel = 1;
  
  aplicar(personagem: Personagem): void {
    // Implementação da abertura de fechaduras
  }
}

export class AfastarMortosVivos implements HabilidadeDeClasse {
  nome = 'Afastar Mortos-Vivos';
  descricao = 'Pode afastar ou destruir mortos-vivos';
  nivel = 1;
  
  aplicar(personagem: Personagem): void {
    // Implementação do afastamento
  }
}

export class MagiaDivina implements HabilidadeDeClasse {
  nome = 'Magia Divina';
  descricao = 'Pode conjurar magias divinas';
  nivel = 2;
  
  aplicar(personagem: Personagem): void {
    // Implementação das magias divinas
  }
}

export class MagiaArcana implements HabilidadeDeClasse {
  nome = 'Magia Arcana';
  descricao = 'Pode conjurar magias arcanas';
  nivel = 1;
  
  aplicar(personagem: Personagem): void {
    // Implementação das magias arcanas
  }
}

export class LerMagia implements HabilidadeDeClasse {
  nome = 'Ler Magia';
  descricao = 'Pode ler pergaminhos e textos mágicos';
  nivel = 1;
  
  aplicar(personagem: Personagem): void {
    // Implementação da leitura de magia
  }
}

// Classes
export class Guerreiro extends Classe {
  getNome(): string {
    return 'Guerreiro';
  }

  getDescricao(): string {
    return 'Aventureiros especializados em combate, sempre na linha de frente e mortais quando desembainham suas armas.';
  }

  getDadoDeVida(): number {
    return 10;
  }

  getProgressaoExperiencia(): Map<number, ProgressaoExperiencia> {
    return new Map([
      [1, { nivel: 1, experienciaMinima: 0, experienciaProxima: 2000 }],
      [2, { nivel: 2, experienciaMinima: 2000, experienciaProxima: 4000 }],
      [3, { nivel: 3, experienciaMinima: 4000, experienciaProxima: 7000 }],
      [4, { nivel: 4, experienciaMinima: 7000, experienciaProxima: 10000 }],
      [5, { nivel: 5, experienciaMinima: 10000, experienciaProxima: 20000 }],
      [6, { nivel: 6, experienciaMinima: 20000, experienciaProxima: 30000 }],
      [7, { nivel: 7, experienciaMinima: 30000, experienciaProxima: 40000 }],
      [8, { nivel: 8, experienciaMinima: 40000, experienciaProxima: 50000 }],
      [9, { nivel: 9, experienciaMinima: 50000, experienciaProxima: 100000 }],
      [10, { nivel: 10, experienciaMinima: 100000, experienciaProxima: 200000 }]
    ]);
  }

  getProgressaoAtributos(): Map<number, ProgressaoAtributos> {
    return new Map([
      [1, { nivel: 1, pontosVida: '10', baseAtaque: 1, classeArmadura: 5 }],
      [2, { nivel: 2, pontosVida: '+1d10', baseAtaque: 2, classeArmadura: 5 }],
      [3, { nivel: 3, pontosVida: '+1d10', baseAtaque: 3, classeArmadura: 6 }],
      [4, { nivel: 4, pontosVida: '+1d10', baseAtaque: 4, classeArmadura: 6 }],
      [5, { nivel: 5, pontosVida: '+1d10', baseAtaque: 5, classeArmadura: 8 }],
      [6, { nivel: 6, pontosVida: '+1d10', baseAtaque: 6, classeArmadura: 8 }],
      [7, { nivel: 7, pontosVida: '+1d10', baseAtaque: 7, classeArmadura: 10 }],
      [8, { nivel: 8, pontosVida: '+1d10', baseAtaque: 8, classeArmadura: 10 }],
      [9, { nivel: 9, pontosVida: '+1d10', baseAtaque: 9, classeArmadura: 11 }],
      [10, { nivel: 10, pontosVida: '+1d10', baseAtaque: 10, classeArmadura: 11 }]
    ]);
  }

  getHabilidades(): HabilidadeDeClasse[] {
    return [
      new Aparar(),
      new MaestriaEmArma(),
      new AtaqueExtra()
    ];
  }

  getArmasPermitidas(): TipoArma[] {
    return [TipoArma.PEQUENA, TipoArma.MEDIA, TipoArma.GRANDE];
  }

  getArmadurasPermitidas(): TipoArmadura[] {
    return [TipoArmadura.LEVE, TipoArmadura.MEDIA, TipoArmadura.PESADA];
  }

  getItensPermitidos(): TipoItem[] {
    return [TipoItem.ARMA, TipoItem.ARMADURA, TipoItem.ESCUDO, TipoItem.ITEM_MAGICO];
  }

  getRestricoes(): string[] {
    return [
      'Não pode usar cajados, varinhas e pergaminhos mágicos',
      'Pode usar pergaminhos de proteção'
    ];
  }
}

export class Ladrao extends Classe {
  getNome(): string {
    return 'Ladrão';
  }

  getDescricao(): string {
    return 'Especialistas em furtividade, desarmar armadilhas e ataques surpresa.';
  }

  getDadoDeVida(): number {
    return 6;
  }

  getProgressaoExperiencia(): Map<number, ProgressaoExperiencia> {
    return new Map([
      [1, { nivel: 1, experienciaMinima: 0, experienciaProxima: 1500 }],
      [2, { nivel: 2, experienciaMinima: 1500, experienciaProxima: 3000 }],
      [3, { nivel: 3, experienciaMinima: 3000, experienciaProxima: 6000 }],
      [4, { nivel: 4, experienciaMinima: 6000, experienciaProxima: 12000 }],
      [5, { nivel: 5, experienciaMinima: 12000, experienciaProxima: 25000 }],
      [6, { nivel: 6, experienciaMinima: 25000, experienciaProxima: 50000 }],
      [7, { nivel: 7, experienciaMinima: 50000, experienciaProxima: 100000 }],
      [8, { nivel: 8, experienciaMinima: 100000, experienciaProxima: 200000 }],
      [9, { nivel: 9, experienciaMinima: 200000, experienciaProxima: 400000 }],
      [10, { nivel: 10, experienciaMinima: 400000, experienciaProxima: 800000 }]
    ]);
  }

  getProgressaoAtributos(): Map<number, ProgressaoAtributos> {
    return new Map([
      [1, { nivel: 1, pontosVida: '6', baseAtaque: 1, classeArmadura: 2 }],
      [2, { nivel: 2, pontosVida: '+1d6', baseAtaque: 1, classeArmadura: 2 }],
      [3, { nivel: 3, pontosVida: '+1d6', baseAtaque: 2, classeArmadura: 3 }],
      [4, { nivel: 4, pontosVida: '+1d6', baseAtaque: 2, classeArmadura: 3 }],
      [5, { nivel: 5, pontosVida: '+1d6', baseAtaque: 3, classeArmadura: 4 }],
      [6, { nivel: 6, pontosVida: '+1d6', baseAtaque: 3, classeArmadura: 4 }],
      [7, { nivel: 7, pontosVida: '+1d6', baseAtaque: 4, classeArmadura: 5 }],
      [8, { nivel: 8, pontosVida: '+1d6', baseAtaque: 4, classeArmadura: 5 }],
      [9, { nivel: 9, pontosVida: '+1d6', baseAtaque: 5, classeArmadura: 6 }],
      [10, { nivel: 10, pontosVida: '+1d6', baseAtaque: 5, classeArmadura: 6 }]
    ]);
  }

  getHabilidades(): HabilidadeDeClasse[] {
    return [
      new AtaqueFurtivo(),
      new EscalarParedes(),
      new AbrirFechaduras()
    ];
  }

  getArmasPermitidas(): TipoArma[] {
    return [TipoArma.PEQUENA, TipoArma.MEDIA];
  }

  getArmadurasPermitidas(): TipoArmadura[] {
    return [TipoArmadura.LEVE];
  }

  getItensPermitidos(): TipoItem[] {
    return [TipoItem.ARMA, TipoItem.ARMADURA, TipoItem.POCAO];
  }

  getRestricoes(): string[] {
    return [
      'Não pode usar armaduras pesadas',
      'Limitado a armas pequenas e médias'
    ];
  }
}

export class Clerigo extends Classe {
  getNome(): string {
    return 'Clérigo';
  }

  getDescricao(): string {
    return 'Servos dos deuses, capazes de curar ferimentos e afastar mortos-vivos.';
  }

  getDadoDeVida(): number {
    return 8;
  }

  getProgressaoExperiencia(): Map<number, ProgressaoExperiencia> {
    return new Map([
      [1, { nivel: 1, experienciaMinima: 0, experienciaProxima: 2000 }],
      [2, { nivel: 2, experienciaMinima: 2000, experienciaProxima: 4000 }],
      [3, { nivel: 3, experienciaMinima: 4000, experienciaProxima: 8000 }],
      [4, { nivel: 4, experienciaMinima: 8000, experienciaProxima: 16000 }],
      [5, { nivel: 5, experienciaMinima: 16000, experienciaProxima: 32000 }],
      [6, { nivel: 6, experienciaMinima: 32000, experienciaProxima: 64000 }],
      [7, { nivel: 7, experienciaMinima: 64000, experienciaProxima: 128000 }],
      [8, { nivel: 8, experienciaMinima: 128000, experienciaProxima: 256000 }],
      [9, { nivel: 9, experienciaMinima: 256000, experienciaProxima: 512000 }],
      [10, { nivel: 10, experienciaMinima: 512000, experienciaProxima: 1024000 }]
    ]);
  }

  getProgressaoAtributos(): Map<number, ProgressaoAtributos> {
    return new Map([
      [1, { nivel: 1, pontosVida: '8', baseAtaque: 1, classeArmadura: 3 }],
      [2, { nivel: 2, pontosVida: '+1d8', baseAtaque: 1, classeArmadura: 3 }],
      [3, { nivel: 3, pontosVida: '+1d8', baseAtaque: 2, classeArmadura: 4 }],
      [4, { nivel: 4, pontosVida: '+1d8', baseAtaque: 2, classeArmadura: 4 }],
      [5, { nivel: 5, pontosVida: '+1d8', baseAtaque: 3, classeArmadura: 5 }],
      [6, { nivel: 6, pontosVida: '+1d8', baseAtaque: 3, classeArmadura: 5 }],
      [7, { nivel: 7, pontosVida: '+1d8', baseAtaque: 4, classeArmadura: 6 }],
      [8, { nivel: 8, pontosVida: '+1d8', baseAtaque: 4, classeArmadura: 6 }],
      [9, { nivel: 9, pontosVida: '+1d8', baseAtaque: 5, classeArmadura: 7 }],
      [10, { nivel: 10, pontosVida: '+1d8', baseAtaque: 5, classeArmadura: 7 }]
    ]);
  }

  getHabilidades(): HabilidadeDeClasse[] {
    return [
      new AfastarMortosVivos(),
      new MagiaDivina()
    ];
  }

  getArmasPermitidas(): TipoArma[] {
    return [TipoArma.PEQUENA, TipoArma.MEDIA];
  }

  getArmadurasPermitidas(): TipoArmadura[] {
    return [TipoArmadura.LEVE, TipoArmadura.MEDIA, TipoArmadura.PESADA];
  }

  getItensPermitidos(): TipoItem[] {
    return [TipoItem.ARMA, TipoItem.ARMADURA, TipoItem.ESCUDO, TipoItem.ITEM_MAGICO, TipoItem.POCAO];
  }

  getRestricoes(): string[] {
    return [
      'Não pode usar armas cortantes',
      'Deve seguir preceitos de sua divindade'
    ];
  }
}

export class Mago extends Classe {
  getNome(): string {
    return 'Mago';
  }

  getDescricao(): string {
    return 'Estudiosos da magia arcana, capazes de conjurar poderosas magias.';
  }

  getDadoDeVida(): number {
    return 4;
  }

  getProgressaoExperiencia(): Map<number, ProgressaoExperiencia> {
    return new Map([
      [1, { nivel: 1, experienciaMinima: 0, experienciaProxima: 3000 }],
      [2, { nivel: 2, experienciaMinima: 3000, experienciaProxima: 6000 }],
      [3, { nivel: 3, experienciaMinima: 6000, experienciaProxima: 12000 }],
      [4, { nivel: 4, experienciaMinima: 12000, experienciaProxima: 24000 }],
      [5, { nivel: 5, experienciaMinima: 24000, experienciaProxima: 48000 }],
      [6, { nivel: 6, experienciaMinima: 48000, experienciaProxima: 96000 }],
      [7, { nivel: 7, experienciaMinima: 96000, experienciaProxima: 192000 }],
      [8, { nivel: 8, experienciaMinima: 192000, experienciaProxima: 384000 }],
      [9, { nivel: 9, experienciaMinima: 384000, experienciaProxima: 768000 }],
      [10, { nivel: 10, experienciaMinima: 768000, experienciaProxima: 1536000 }]
    ]);
  }

  getProgressaoAtributos(): Map<number, ProgressaoAtributos> {
    return new Map([
      [1, { nivel: 1, pontosVida: '4', baseAtaque: 1, classeArmadura: 0 }],
      [2, { nivel: 2, pontosVida: '+1d4', baseAtaque: 1, classeArmadura: 0 }],
      [3, { nivel: 3, pontosVida: '+1d4', baseAtaque: 1, classeArmadura: 1 }],
      [4, { nivel: 4, pontosVida: '+1d4', baseAtaque: 1, classeArmadura: 1 }],
      [5, { nivel: 5, pontosVida: '+1d4', baseAtaque: 2, classeArmadura: 2 }],
      [6, { nivel: 6, pontosVida: '+1d4', baseAtaque: 2, classeArmadura: 2 }],
      [7, { nivel: 7, pontosVida: '+1d4', baseAtaque: 2, classeArmadura: 3 }],
      [8, { nivel: 8, pontosVida: '+1d4', baseAtaque: 2, classeArmadura: 3 }],
      [9, { nivel: 9, pontosVida: '+1d4', baseAtaque: 3, classeArmadura: 4 }],
      [10, { nivel: 10, pontosVida: '+1d4', baseAtaque: 3, classeArmadura: 4 }]
    ]);
  }

  getHabilidades(): HabilidadeDeClasse[] {
    return [
      new MagiaArcana(),
      new LerMagia()
    ];
  }

  getArmasPermitidas(): TipoArma[] {
    return [TipoArma.PEQUENA];
  }

  getArmadurasPermitidas(): TipoArmadura[] {
    return [];
  }

  getItensPermitidos(): TipoItem[] {
    return [TipoItem.ARMA, TipoItem.ITEM_MAGICO, TipoItem.POCAO];
  }

  getRestricoes(): string[] {
    return [
      'Não pode usar armaduras',
      'Limitado a armas pequenas',
      'Não pode usar escudos'
    ];
  }
}

// Factory para criar classes
export class ClassFactory {
  static createClass(className: string): Classe {
    switch (className.toLowerCase()) {
      case 'guerreiro':
        return new Guerreiro();
      case 'ladrao':
      case 'ladrão':
        return new Ladrao();
      case 'clerigo':
      case 'clérigo':
        return new Clerigo();
      case 'mago':
        return new Mago();
      default:
        throw new Error(`Classe desconhecida: ${className}`);
    }
  }

  static getAvailableClasses(): string[] {
    return ['Guerreiro', 'Ladrão', 'Clérigo', 'Mago'];
  }

  static getClassDescription(className: string): string {
    const characterClass = this.createClass(className);
    return characterClass.getDescricao();
  }
}
