export type ScreenTab = 'agenda' | 'atendimento' | 'pacientes' | 'financeiro';

export type PerfilUsuario = 'ADMIN' | 'RECEPCIONISTA' | 'VETERINARIO';

export interface Tutor {
  id: number;
  nome: string;
  cpf: string;
  telefone: string;
  email: string;
}

export interface Pet {
  id: number;
  nome: string;
  especie: string;
  raca: string;
  sexo: string;
  dataNascimento: string;
  idade?: number;
  peso?: number;
  porte: string;
  cor: string;
  observacoes: string;
  tutorId: number;
  tutorNome: string;
  tutorTelefone?: string;
}

export interface Veterinario {
  id: number;
  nome: string;
  especialidade: string;
  registroProfissional: string;
  valorConsulta?: number;
}

export interface AgendamentoItem {
  id: number;
  horario: string;
  fim?: string;
  petId: number;
  petNome: string;
  tutorId: number;
  tutorNome: string;
  veterinarioId: number;
  veterinarioNome: string;
  tipo: string;
  status: 'CONFIRMADA' | 'A_CONFIRMAR' | 'REALIZADA' | 'CANCELADA';
  cor: 'amber' | 'blue' | 'purple' | 'rose' | 'teal';
  data: string;
}

export interface ProntuarioRegistro {
  id: number;
  petId: number;
  data: string;
  titulo: string;
  veterinarioNome: string;
  versao: string;
  queixaPrincipal: string;
  historicoEvolucao: string;
  alimentacao: string;
  ingestaoAgua: string;
  temperatura: string;
  frequenciaCardiaca: string;
  peso: string;
  achadosExameFisico: string;
  diagnosticoPresuntivo: string;
  examesSolicitados: string;
  prescricaoTratamento: string;
  orientacoesRetorno: string;
  atual?: boolean;
}

export interface PagamentoItem {
  id: number;
  pacienteNome: string;
  tutorNome: string;
  servico: string;
  categoria: 'Consultas' | 'Vacinas' | 'Procedimentos';
  forma: 'Cartão' | 'PIX' | 'Dinheiro';
  status: 'Pago' | 'Pendente';
  valor: number;
  data: string;
}
