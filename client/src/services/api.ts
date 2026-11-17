import {
  AgendamentoItem,
  PagamentoItem,
  Pet,
  ProntuarioRegistro,
  Tutor,
  Veterinario,
} from '../types/vetcare';

const API_BASE_URL = 'http://localhost:8080';

export const INITIAL_TUTORES: Tutor[] = [
  {
    id: 1,
    nome: 'Marina Lima',
    cpf: '123.456.789-00',
    telefone: '(11) 98765-4321',
    email: 'marina.lima@email.com',
  },
  {
    id: 2,
    nome: 'Pedro Alves',
    cpf: '234.567.890-11',
    telefone: '(11) 97654-3210',
    email: 'pedro.alves@email.com',
  },
  {
    id: 3,
    nome: 'Carla Dias',
    cpf: '345.678.901-22',
    telefone: '(11) 96543-2109',
    email: 'carla.dias@email.com',
  },
  {
    id: 4,
    nome: 'Lucas Mendes',
    cpf: '456.789.012-33',
    telefone: '(11) 95432-1098',
    email: 'lucas.mendes@email.com',
  },
];

export const INITIAL_PETS: Pet[] = [
  {
    id: 1,
    nome: 'Mel',
    especie: 'Felino',
    raca: 'SRD',
    sexo: 'Fêmea',
    dataNascimento: '2021-05-10',
    idade: 4,
    peso: 4.2,
    porte: 'Pequeno',
    cor: 'Amarelo / Branco',
    observacoes: 'Sem alergias conhecidas.',
    tutorId: 1,
    tutorNome: 'Marina Lima',
    tutorTelefone: '(11) 98765-4321',
  },
  {
    id: 2,
    nome: 'Thor',
    especie: 'Canino',
    raca: 'Golden Retriever',
    sexo: 'Macho',
    dataNascimento: '2022-02-14',
    idade: 3,
    peso: 28.5,
    porte: 'Grande',
    cor: 'Dourado',
    observacoes: 'Carteira vacinal em atualização.',
    tutorId: 2,
    tutorNome: 'Pedro Alves',
    tutorTelefone: '(11) 97654-3210',
  },
  {
    id: 3,
    nome: 'Luna',
    especie: 'Felino',
    raca: 'Siamês',
    sexo: 'Fêmea',
    dataNascimento: '2023-08-20',
    idade: 2,
    peso: 3.6,
    porte: 'Pequeno',
    cor: 'Creme com pontas escuras',
    observacoes: 'Acompanhamento de retorno.',
    tutorId: 3,
    tutorNome: 'Carla Dias',
    tutorTelefone: '(11) 96543-2109',
  },
  {
    id: 4,
    nome: 'Simba',
    especie: 'Canino',
    raca: 'Bulldog Francês',
    sexo: 'Macho',
    dataNascimento: '2020-11-03',
    idade: 5,
    peso: 12.0,
    porte: 'Médio',
    cor: 'Tigrado',
    observacoes: 'Histórico de dermatite alérgica.',
    tutorId: 4,
    tutorNome: 'Lucas Mendes',
    tutorTelefone: '(11) 95432-1098',
  },
];

export const INITIAL_VETERINARIOS: Veterinario[] = [
  {
    id: 1,
    nome: 'Dra. Camila',
    especialidade: 'Clínica geral',
    registroProfissional: 'CRMV-SP 41250',
    valorConsulta: 180,
  },
  {
    id: 2,
    nome: 'Dr. Rafael',
    especialidade: 'Dermatologia',
    registroProfissional: 'CRMV-SP 38910',
    valorConsulta: 220,
  },
];

export const INITIAL_AGENDAMENTOS: AgendamentoItem[] = [
  {
    id: 1,
    horario: '08:00',
    petId: 1,
    petNome: 'Mel',
    tutorId: 1,
    tutorNome: 'Marina Lima',
    veterinarioId: 1,
    veterinarioNome: 'Dra. Camila',
    tipo: 'Consulta de rotina',
    status: 'CONFIRMADA',
    cor: 'amber',
    data: '2025-06-18',
  },
  {
    id: 2,
    horario: '09:30',
    petId: 2,
    petNome: 'Thor',
    tutorId: 2,
    tutorNome: 'Pedro Alves',
    veterinarioId: 2,
    veterinarioNome: 'Dr. Rafael',
    tipo: 'Vacinação',
    status: 'CONFIRMADA',
    cor: 'blue',
    data: '2025-06-18',
  },
  {
    id: 3,
    horario: '11:00',
    petId: 3,
    petNome: 'Luna',
    tutorId: 3,
    tutorNome: 'Carla Dias',
    veterinarioId: 1,
    veterinarioNome: 'Dra. Camila',
    tipo: 'Retorno',
    status: 'CONFIRMADA',
    cor: 'purple',
    data: '2025-06-18',
  },
  {
    id: 4,
    horario: '14:30',
    petId: 4,
    petNome: 'Simba',
    tutorId: 4,
    tutorNome: 'Lucas Mendes',
    veterinarioId: 2,
    veterinarioNome: 'Dr. Rafael',
    tipo: 'Exame dermatológico',
    status: 'A_CONFIRMAR',
    cor: 'rose',
    data: '2025-06-18',
  },
];

export const INITIAL_HISTORICO_CLINICO: ProntuarioRegistro[] = [
  {
    id: 101,
    petId: 1,
    data: '18 JUN 2025',
    titulo: 'Consulta atual',
    veterinarioNome: 'Dra. Camila',
    versao: 'versão 1.0',
    atual: true,
    queixaPrincipal: 'Falta de apetite e comportamento apático há dois dias.',
    historicoEvolucao:
      'Tutora relata que Mel reduziu a ingestão de alimento desde segunda-feira. Consumo de água normal. Sem episódios de vômito ou diarreia.',
    alimentacao: 'Reduzida',
    ingestaoAgua: 'Normal',
    temperatura: '38,7',
    frequenciaCardiaca: '160',
    peso: '4,2',
    achadosExameFisico:
      'Mucosas normocoradas, TPC < 2s, hidratação adequada. Leve sensibilidade à palpação abdominal cranial.',
    diagnosticoPresuntivo: 'Gastrite aguda / corpo estranho linear a descartar',
    examesSolicitados:
      'Hemograma completo, perfil bioquímico renal e hepático, ultrassonografia abdominal.',
    prescricaoTratamento:
      'Citrato de maropitant 1mg/kg SC em dose única. Dieta pastosa morna fracionada em pequenas porções.',
    orientacoesRetorno:
      'Retorno em 48 horas com resultado dos exames ou imediatamente em caso de vômito.',
  },
  {
    id: 100,
    petId: 1,
    data: '02 FEV 2025',
    titulo: 'Vacina antirrábica',
    veterinarioNome: 'Dr. Rafael',
    versao: 'versão 1.0',
    queixaPrincipal: 'Imunização anual antirrábica.',
    historicoEvolucao: 'Animal hígido, sem alterações clínicas.',
    alimentacao: 'Normal',
    ingestaoAgua: 'Normal',
    temperatura: '38,3',
    frequenciaCardiaca: '150',
    peso: '4,1',
    achadosExameFisico: 'Parâmetros vitais dentro da normalidade.',
    diagnosticoPresuntivo: 'Animal saudável apto para vacinação',
    examesSolicitados: 'Nenhum exame solicitado.',
    prescricaoTratamento: 'Aplicação de 1 dose de vacina antirrábica inativada SC.',
    orientacoesRetorno: 'Próxima dose prevista para 02 FEV 2026.',
  },
  {
    id: 99,
    petId: 1,
    data: '14 OUT 2024',
    titulo: 'Consulta de rotina',
    veterinarioNome: 'Dra. Camila',
    versao: 'versão 1.0',
    queixaPrincipal: 'Check-up anual preventivo.',
    historicoEvolucao: 'Desenvolvimento adequado e pelagem saudável.',
    alimentacao: 'Normal',
    ingestaoAgua: 'Normal',
    temperatura: '38,4',
    frequenciaCardiaca: '148',
    peso: '4,0',
    achadosExameFisico: 'Ausculta cardiopulmonar limpa, escore corporal ideal.',
    diagnosticoPresuntivo: 'Check-up preventivo sem alterações',
    examesSolicitados: 'Hemograma preventivo normal.',
    prescricaoTratamento: 'Vermifugação oral conforme peso.',
    orientacoesRetorno: 'Retorno preventivo em 6 meses.',
  },
];

export const INITIAL_PAGAMENTOS: PagamentoItem[] = [
  {
    id: 1,
    pacienteNome: 'Mel',
    tutorNome: 'Marina Lima',
    servico: 'Consulta',
    categoria: 'Consultas',
    forma: 'Cartão',
    status: 'Pago',
    valor: 180.0,
    data: '18/06/2025',
  },
  {
    id: 2,
    pacienteNome: 'Thor',
    tutorNome: 'Pedro Alves',
    servico: 'Vacinação',
    categoria: 'Vacinas',
    forma: 'PIX',
    status: 'Pago',
    valor: 145.0,
    data: '18/06/2025',
  },
  {
    id: 3,
    pacienteNome: 'Luna',
    tutorNome: 'Carla Dias',
    servico: 'Consulta + exame',
    categoria: 'Procedimentos',
    forma: 'Dinheiro',
    status: 'Pendente',
    valor: 390.0,
    data: '18/06/2025',
  },
];

export async function cadastrarPetBackend(pet: Omit<Pet, 'id'>): Promise<Pet | null> {
  try {
    const response = await fetch(`${API_BASE_URL}/pets`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        nome: pet.nome,
        especie: pet.especie,
        raca: pet.raca,
        idade: pet.idade ?? 1,
        peso: pet.peso,
        sexo: pet.sexo,
        porte: pet.porte,
        cor: pet.cor,
        dataNascimento: pet.dataNascimento || null,
        observacoes: pet.observacoes,
        tutorId: pet.tutorId,
      }),
    });
    if (!response.ok) return null;
    return await response.json();
  } catch {
    return null;
  }
}

export async function registrarAtendimentoBackend(
  consultaId: number,
  profissionalId: number,
  dados: { diagnostico: string; prescricao: string; observacoes: string }
): Promise<boolean> {
  try {
    const response = await fetch(
      `${API_BASE_URL}/consultas/${consultaId}/realizar?profissionalId=${profissionalId}`,
      {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(dados),
      }
    );
    return response.ok;
  } catch {
    return false;
  }
}
