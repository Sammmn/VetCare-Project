import { AgendamentoItem, PagamentoItem, Pet, ProntuarioRegistro, Tutor, Veterinario } from '../types/vetcare';

const API_BASE_URL = 'http://localhost:8080';

export const INITIAL_TUTORES: Tutor[] = [
  { id: 1, nome: 'Marina Lima', cpf: '123.456.789-00', telefone: '(11) 98765-4321', email: 'marina.lima@email.com' },
  { id: 2, nome: 'Pedro Alves', cpf: '234.567.890-11', telefone: '(11) 97654-3210', email: 'pedro.alves@email.com' },
  { id: 3, nome: 'Carla Dias', cpf: '345.678.901-22', telefone: '(11) 96543-2109', email: 'carla.dias@email.com' },
  { id: 4, nome: 'Lucas Mendes', cpf: '456.789.012-33', telefone: '(11) 95432-1098', email: 'lucas.mendes@email.com' },
];

export const INITIAL_PETS: Pet[] = [
  { id: 1, nome: 'Mel', especie: 'Felino', raca: 'SRD', sexo: 'Fêmea', dataNascimento: '2021-05-10', idade: 4, peso: 4.2, porte: 'Pequeno', cor: 'Amarelo / Branco', observacoes: 'Sem alergias conhecidas.', tutorId: 1, tutorNome: 'Marina Lima', tutorTelefone: '(11) 98765-4321' },
  { id: 2, nome: 'Thor', especie: 'Canino', raca: 'Golden Retriever', sexo: 'Macho', dataNascimento: '2022-02-14', idade: 3, peso: 28.5, porte: 'Grande', cor: 'Dourado', observacoes: 'Carteira vacinal em atualização.', tutorId: 2, tutorNome: 'Pedro Alves', tutorTelefone: '(11) 97654-3210' },
  { id: 3, nome: 'Luna', especie: 'Felino', raca: 'Siamês', sexo: 'Fêmea', dataNascimento: '2023-08-20', idade: 2, peso: 3.6, porte: 'Pequeno', cor: 'Creme com pontas escuras', observacoes: 'Acompanhamento de retorno.', tutorId: 3, tutorNome: 'Carla Dias', tutorTelefone: '(11) 96543-2109' },
  { id: 4, nome: 'Simba', especie: 'Canino', raca: 'Bulldog Francês', sexo: 'Macho', dataNascimento: '2020-11-03', idade: 5, peso: 12.0, porte: 'Médio', cor: 'Tigrado', observacoes: 'Histórico de dermatite alérgica.', tutorId: 4, tutorNome: 'Lucas Mendes', tutorTelefone: '(11) 95432-1098' },
];

export const INITIAL_VETERINARIOS: Veterinario[] = [
  { id: 1, nome: 'Dra. Camila', especialidade: 'Clínica geral', registroProfissional: 'CRMV-SP 41250', valorConsulta: 180 },
  { id: 2, nome: 'Dr. Rafael', especialidade: 'Dermatologia', registroProfissional: 'CRMV-SP 38910', valorConsulta: 220 },
];

export const INITIAL_AGENDAMENTOS: AgendamentoItem[] = [
  { id: 1, horario: '08:00', petId: 1, petNome: 'Mel', tutorId: 1, tutorNome: 'Marina Lima', veterinarioId: 1, veterinarioNome: 'Dra. Camila', tipo: 'Consulta de rotina', status: 'CONFIRMADA', cor: 'amber', data: new Date().toISOString().slice(0, 10) },
  { id: 2, horario: '09:30', petId: 2, petNome: 'Thor', tutorId: 2, tutorNome: 'Pedro Alves', veterinarioId: 2, veterinarioNome: 'Dr. Rafael', tipo: 'Vacinação', status: 'CONFIRMADA', cor: 'blue', data: new Date().toISOString().slice(0, 10) },
  { id: 3, horario: '11:00', petId: 3, petNome: 'Luna', tutorId: 3, tutorNome: 'Carla Dias', veterinarioId: 1, veterinarioNome: 'Dra. Camila', tipo: 'Retorno', status: 'CONFIRMADA', cor: 'purple', data: new Date().toISOString().slice(0, 10) },
  { id: 4, horario: '14:30', petId: 4, petNome: 'Simba', tutorId: 4, tutorNome: 'Lucas Mendes', veterinarioId: 2, veterinarioNome: 'Dr. Rafael', tipo: 'Exame dermatológico', status: 'A_CONFIRMAR', cor: 'rose', data: new Date().toISOString().slice(0, 10) },
];

export const INITIAL_HISTORICO_CLINICO: ProntuarioRegistro[] = [
  {
    id: 101, petId: 1, data: '18 JUN 2025', titulo: 'Consulta atual', veterinarioNome: 'Dra. Camila', versao: 'versão 1.0', atual: true,
    queixaPrincipal: 'Falta de apetite e comportamento apático há dois dias.',
    historicoEvolucao: 'Tutora relata que Mel reduziu a ingestão de alimento desde segunda-feira.',
    alimentacao: 'Reduzida', ingestaoAgua: 'Normal', temperatura: '38,7', frequenciaCardiaca: '160', peso: '4,2',
    achadosExameFisico: 'Mucosas normocoradas, TPC < 2s, hidratação adequada.',
    diagnosticoPresuntivo: 'Gastrite aguda / corpo estranho linear a descartar',
    examesSolicitados: 'Hemograma completo e ultrassonografia abdominal.',
    prescricaoTratamento: 'Citrato de maropitant 1mg/kg SC em dose única.',
    orientacoesRetorno: 'Retorno em 48 horas.',
  },
];

export const INITIAL_PAGAMENTOS: PagamentoItem[] = [
  { id: 1, pacienteNome: 'Mel', tutorNome: 'Marina Lima', servico: 'Consulta', categoria: 'Consultas', forma: 'Cartão', status: 'Pago', valor: 180.0, data: '18/06/2025' },
  { id: 2, pacienteNome: 'Thor', tutorNome: 'Pedro Alves', servico: 'Vacinação', categoria: 'Vacinas', forma: 'PIX', status: 'Pago', valor: 145.0, data: '18/06/2025' },
  { id: 3, pacienteNome: 'Luna', tutorNome: 'Carla Dias', servico: 'Consulta + exame', categoria: 'Procedimentos', forma: 'Dinheiro', status: 'Pendente', valor: 390.0, data: '18/06/2025' },
];

async function parseApiError(response: Response, fallback: string): Promise<never> {
  let msg = fallback;
  try { const data = await response.json(); if (data?.message) msg = data.message; } catch {}
  throw new Error(msg);
}

export function normalizarDataISO(data?: string): string | null {
  if (!data || !data.trim()) return null;
  const limpa = data.trim();
  if (/^\d{4}-\d{2}-\d{2}$/.test(limpa)) return limpa;
  const br = limpa.match(/^(\d{2})\/(\d{2})\/(\d{4})$/);
  return br ? `${br[3]}-${br[2]}-${br[1]}` : null;
}
export async function listarTutoresBackend(): Promise<Tutor[]> {
  const response = await fetch(`${API_BASE_URL}/tutores/lista/todos`);
  if (!response.ok) await parseApiError(response, 'Erro ao carregar tutores.');
  const lista = await response.json();
  return lista.map((t: any) => ({ id: t.id, nome: t.nome ?? '', cpf: t.cpf ?? '', telefone: t.telefone ?? '', email: t.email ?? '' }));
}

export async function cadastrarTutorBackend(tutor: Omit<Tutor, 'id'>): Promise<Tutor> {
  const response = await fetch(`${API_BASE_URL}/tutores`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ nome: tutor.nome, cpf: tutor.cpf, telefone: tutor.telefone, email: tutor.email?.trim() || null }),
  });
  if (!response.ok) await parseApiError(response, 'Erro ao cadastrar tutor.');
  const salvo = await response.json();
  return { id: salvo.id, nome: salvo.nome ?? tutor.nome, cpf: salvo.cpf ?? tutor.cpf, telefone: salvo.telefone ?? tutor.telefone, email: salvo.email ?? tutor.email };
}

export async function listarPetsBackend(): Promise<Pet[]> {
  const response = await fetch(`${API_BASE_URL}/pets/lista/todos`);
  if (!response.ok) await parseApiError(response, 'Erro ao carregar pets.');
  const lista = await response.json();
  return lista.map((p: any) => ({
    id: p.id, nome: p.nome, especie: p.especie ?? '', raca: p.raca ?? 'SRD', sexo: p.sexo ?? 'Fêmea',
    dataNascimento: p.dataNascimento ?? '', idade: p.idade ?? 1, peso: p.peso ?? 0, porte: p.porte ?? 'Médio',
    cor: p.cor ?? '', observacoes: p.observacoes ?? '', tutorId: p.tutorId, tutorNome: p.tutorNome ?? 'Tutor', tutorTelefone: p.tutorTelefone ?? '',
  }));
}

export async function cadastrarPetBackend(pet: Omit<Pet, 'id'>): Promise<Pet> {
  const dataISO = normalizarDataISO(pet.dataNascimento);
  if (pet.dataNascimento && !dataISO) throw new Error('Data de nascimento inválida.');
  const response = await fetch(`${API_BASE_URL}/pets`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      nome: pet.nome, especie: pet.especie, raca: pet.raca, idade: pet.idade ?? 1, peso: pet.peso,
      sexo: pet.sexo, porte: pet.porte, cor: pet.cor, dataNascimento: dataISO, observacoes: pet.observacoes, tutorId: pet.tutorId,
    }),
  });
  if (!response.ok) await parseApiError(response, 'Erro ao cadastrar paciente.');
  const salvo = await response.json();
  return { ...pet, id: salvo.id, dataNascimento: salvo.dataNascimento ?? dataISO ?? '', tutorId: salvo.tutorId ?? pet.tutorId, tutorNome: salvo.tutorNome ?? pet.tutorNome, tutorTelefone: salvo.tutorTelefone ?? pet.tutorTelefone };
}

export async function listarVeterinariosBackend(): Promise<Veterinario[]> {
  const response = await fetch(`${API_BASE_URL}/medicos/lista/todos`);
  if (!response.ok) await parseApiError(response, 'Erro ao carregar veterinários.');
  const lista = await response.json();
  return lista.map((v: any) => ({
    id: v.id, nome: v.nome, especialidade: v.especialidade ?? v.especialidades?.[0] ?? 'Clínica geral',
    registroProfissional: v.registroProfissional ?? '', valorConsulta: v.valorConsulta ?? 180,
  }));
}

function mapConsultaToAgendamento(c: any, idx: number): AgendamentoItem {
  const cores: AgendamentoItem['cor'][] = ['amber', 'blue', 'purple', 'rose', 'teal'];
  const inicioStr: string = c.inicio ?? '';
  const data = inicioStr.includes('T') ? inicioStr.split('T')[0] : new Date().toISOString().slice(0, 10);
  const horario = inicioStr.includes('T') ? inicioStr.split('T')[1].slice(0, 5) : '08:00';
  const statusMap: Record<string, AgendamentoItem['status']> = { AGENDADA: 'CONFIRMADA', REALIZADA: 'REALIZADA', CANCELADA: 'CANCELADA' };
  return {
    id: c.id, horario, petId: c.petId ?? 1, petNome: c.petNome ?? 'Paciente', tutorId: c.pacienteId ?? 1, tutorNome: c.pacienteNome ?? 'Tutor',
    veterinarioId: c.profissionalId ?? 1, veterinarioNome: c.profissionalNome ?? 'Veterinário', tipo: c.observacoes || 'Consulta de rotina',
    status: statusMap[c.status] ?? 'CONFIRMADA', cor: cores[idx % cores.length], data,
  };
}

export async function listarConsultasBackend(): Promise<AgendamentoItem[]> {
  const response = await fetch(`${API_BASE_URL}/consultas`);
  if (!response.ok) await parseApiError(response, 'Erro ao carregar consultas.');
  const lista = await response.json();
  return lista.map((c: any, i: number) => mapConsultaToAgendamento(c, i));
}

export async function agendarConsultaBackend(dados: { petId: number; veterinarioId: number; data: string; horario: string; observacoes: string }): Promise<AgendamentoItem> {
  const response = await fetch(`${API_BASE_URL}/consultas`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ petId: dados.petId, veterinarioId: dados.veterinarioId, inicio: `${dados.data}T${dados.horario}:00`, observacoes: dados.observacoes }),
  });
  if (!response.ok) await parseApiError(response, 'Erro ao confirmar agendamento.');
  return mapConsultaToAgendamento(await response.json(), 4);
}

export async function cancelarConsultaBackend(consultaId: number): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/consultas/${consultaId}/cancelar`, { method: 'PATCH' });
  if (!response.ok) await parseApiError(response, 'Erro ao cancelar consulta.');
}

export async function registrarAtendimentoBackend(consultaId: number, profissionalId: number, dados: { diagnostico: string; prescricao: string; observacoes: string }): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/consultas/${consultaId}/realizar?profissionalId=${profissionalId}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(dados),
  });
  if (!response.ok) await parseApiError(response, 'Erro ao finalizar atendimento no servidor.');
}