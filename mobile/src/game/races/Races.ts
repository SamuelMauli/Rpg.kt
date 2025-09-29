import { Raca, HabilidadeRacial, Atributos, Personagem } from '../../types/GameTypes';

// Habilidades Raciais
export class ResistenciaVeneno implements HabilidadeRacial {
  nome = 'Resistência a Veneno';
  descricao = '+2 em testes de resistência contra venenos e magias';
  
  aplicar(personagem: Personagem): void {
    // Implementação da resistência será aplicada durante os testes
  }
}

export class VisaoNoturna implements HabilidadeRacial {
  nome = 'Visão Noturna';
  descricao = 'Pode ver no escuro até 18 metros';
  
  aplicar(personagem: Personagem): void {
    // Implementação da visão noturna
  }
}

export class ResistenciaEncantamento implements HabilidadeRacial {
  nome = 'Resistência a Encantamentos';
  descricao = 'Resistência a sono e encantamentos';
  
  aplicar(personagem: Personagem): void {
    // Implementação da resistência
  }
}

export class Furtividade implements HabilidadeRacial {
  nome = 'Furtividade';
  descricao = '+2 em testes de furtividade e esconder-se';
  
  aplicar(personagem: Personagem): void {
    // Implementação do bônus de furtividade
  }
}

export class Sortudo implements HabilidadeRacial {
  nome = 'Sortudo';
  descricao = '+1 em todos os testes de resistência';
  
  aplicar(personagem: Personagem): void {
    // Implementação da sorte
  }
}

export class Versatilidade implements HabilidadeRacial {
  nome = 'Versatilidade';
  descricao = '+1 em todos os atributos';
  
  aplicar(personagem: Personagem): void {
    personagem.atributos.forca += 1;
    personagem.atributos.destreza += 1;
    personagem.atributos.constituicao += 1;
    personagem.atributos.inteligencia += 1;
    personagem.atributos.sabedoria += 1;
    personagem.atributos.carisma += 1;
  }
}

// Raças
export class Humano extends Raca {
  getNome(): string {
    return 'Humano';
  }

  getDescricao(): string {
    return 'Versáteis e adaptáveis, os humanos são a raça mais comum e diversificada.';
  }

  getModificadoresAtributos(): Partial<Atributos> {
    return {
      forca: 1,
      destreza: 1,
      constituicao: 1,
      inteligencia: 1,
      sabedoria: 1,
      carisma: 1
    };
  }

  getHabilidades(): HabilidadeRacial[] {
    return [new Versatilidade()];
  }

  getMovimento(): number {
    return 9;
  }

  getIdiomas(): string[] {
    return ['Comum'];
  }
}

export class Elfo extends Raca {
  getNome(): string {
    return 'Elfo';
  }

  getDescricao(): string {
    return 'Seres mágicos e graciosos, com afinidade natural com a magia e arco e flecha.';
  }

  getModificadoresAtributos(): Partial<Atributos> {
    return {
      destreza: 1,
      inteligencia: 1,
      constituicao: -1
    };
  }

  getHabilidades(): HabilidadeRacial[] {
    return [new VisaoNoturna(), new ResistenciaEncantamento()];
  }

  getMovimento(): number {
    return 9;
  }

  getIdiomas(): string[] {
    return ['Comum', 'Élfico'];
  }
}

export class Anao extends Raca {
  getNome(): string {
    return 'Anão';
  }

  getDescricao(): string {
    return 'Resistentes e determinados, os anões são conhecidos por sua força e resistência.';
  }

  getModificadoresAtributos(): Partial<Atributos> {
    return {
      forca: 1,
      constituicao: 1,
      carisma: -1
    };
  }

  getHabilidades(): HabilidadeRacial[] {
    return [new ResistenciaVeneno(), new VisaoNoturna()];
  }

  getMovimento(): number {
    return 6;
  }

  getIdiomas(): string[] {
    return ['Comum', 'Anão'];
  }
}

export class Halfling extends Raca {
  getNome(): string {
    return 'Halfling';
  }

  getDescricao(): string {
    return 'Pequenos e ágeis, os halflings são conhecidos por sua sorte e furtividade.';
  }

  getModificadoresAtributos(): Partial<Atributos> {
    return {
      destreza: 1,
      constituicao: 1,
      forca: -1
    };
  }

  getHabilidades(): HabilidadeRacial[] {
    return [new Furtividade(), new Sortudo()];
  }

  getMovimento(): number {
    return 6;
  }

  getIdiomas(): string[] {
    return ['Comum', 'Halfling'];
  }
}

// Factory para criar raças
export class RaceFactory {
  static createRace(raceName: string): Raca {
    switch (raceName.toLowerCase()) {
      case 'humano':
        return new Humano();
      case 'elfo':
        return new Elfo();
      case 'anao':
      case 'anão':
        return new Anao();
      case 'halfling':
        return new Halfling();
      default:
        throw new Error(`Raça desconhecida: ${raceName}`);
    }
  }

  static getAvailableRaces(): string[] {
    return ['Humano', 'Elfo', 'Anão', 'Halfling'];
  }

  static getRaceDescription(raceName: string): string {
    const race = this.createRace(raceName);
    return race.getDescricao();
  }
}
