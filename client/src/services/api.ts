import { AgendamentoItem, PagamentoItem, Pet, ProntuarioRegistro, Tutor, Veterinario } from '../types/vetcare';

// Em desenvolvimento o Vite faz proxy de /api -> http://localhost:8080 (ver vite.config.ts).
// Em produção, defina VITE_API_URL (ex.: https://api.suaclinica.com/api).
const API_BASE_URL: string = import.meta.env.VITE_API_URL ?? '/api';

// ---------------------------------------------------------------- sessão (JWT do usuário logado)

export type CargoUsuario = 'ADMIN' | 'VETERINARIO' | 'TUTOR' | 'RECEPCIONISTA';

export interface UsuarioLogado {
  id: number;
  nome: string;
  email: string;
  cargo: CargoUsuario;
}

export interface Sessao {
  token: string;
  usuario: UsuarioLogado;
  expiraEm: number; // epoch ms
}

const SESSION_KEY = 'vetcare.sessao';
export const EVENTO_LOGOUT = 'vetcare:logout';

export function getSessao(): Sessao | null {
  try {
    const raw = sessionStorage.getItem(SESSION_KEY);
    if (!raw) return null;
    const sessao = JSON.parse(raw) as Sessao;
    if (!sessao.token || Date.now() >= sessao.expiraEm) {
      sessionStorage.removeItem(SESSION_KEY);
      return null;
    }
    return sessao;
  } catch {
    return null;
  }
}

export function logout(): void {
  sessionStorage.removeItem(SESSION_KEY);
}

export async function login(email: string, senha: string): Promise<Sessao> {
  const response = await fetch(`${API_BASE_URL}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: email.trim(), senha }),
  });
  if (!response.ok) await parseApiError(response, 'Não foi possível entrar. Verifique se o servidor está ativo.');

  const data = await response.json();
  const cargo = data.usuario?.cargo as CargoUsuario;
  if (cargo === 'TUTOR') {
    throw new Error('Este sistema é de uso exclusivo da equipe da clínica.');
  }

  const sessao: Sessao = {
    token: data.token,
    expiraEm: Date.now() + Number(data.expiraEmSegundos ?? 0) * 1000,
    usuario: { id: data.usuario.id, nome: data.usuario.nome, email: data.usuario.email, cargo },
  };
  sessionStorage.setItem(SESSION_KEY, JSON.stringify(sessao));
  return sessao;
}

async function apiFetch(path: string, options: RequestInit = {}): Promise<Response> {
  const sessao = getSessao();
  if (!sessao) {
    window.dispatchEvent(new Event(EVENTO_LOGOUT));
    throw new Error('Sessão expirada. Faça login novamente.');
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {}),
      Authorization: `Bearer ${sessao.token}`,
    },
  });

  if (response.status === 401) {
    logout();
    window.dispatchEvent(new Event(EVENTO_LOGOUT));
    throw new Error('Sessão expirada. Faça login novamente.');
  }
  return response;
}

/** O backend responde { mensagem, campos: { campo: erro } }. */
async function parseApiError(response: Response, fallback: string): Promise<never> {
  let msg = fallback;
  try {
    const data = await response.json();
    const campos = data?.campos && typeof data.campos === 'object' ? Object.values(data.campos) : [];
    if (campos.length > 0) msg = campos.join(' | ');
    else if (data?.mensagem) msg = data.mensagem;
  } catch {
    // corpo vazio ou não-JSON: mantém a mensagem padrão
  }
  throw new Error(msg);
}

// ---------------------------------------------------------------- utilitários

/** Data de hoje no fuso LOCAL (toISOString() usaria UTC e viraria o dia após as 21h no Brasil). */
export function hojeLocalISO(): string {
  const d = new Date();
  const p = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}`;
}

export function normalizarDataISO(data?: string): string | null {
  if (!data || !data.trim()) return null;
  const limpa = data.trim();
  if (/^\d{4}-\d{2}-\d{2}$/.test(limpa)) return limpa;
  const br = limpa.match(/^(\d{2})\/(\d{2})\/(\d{4})$/);
  return br ? `${br[3]}-${br[2]}-${br[1]}` : null;
}

function senhaAleatoria(): string {
  const bytes = new Uint8Array(18);
  crypto.getRandomValues(bytes);
  return btoa(String.fromCharCode(...bytes)) + 'aA1!';
}

// ---------------------------------------------------------------- dados de exemplo (Financeiro ainda não tem API)

export const INITIAL_PAGAMENTOS: PagamentoItem[] = [
  { id: 1, pacienteNome: 'Mel', tutorNome: 'Marina Lima', servico: 'Consulta', categoria: 'Consultas', forma: 'Cartão', status: 'Pago', valor: 180.0, data: '18/06/2025' },
  { id: 2, pacienteNome: 'Thor', tutorNome: 'Pedro Alves', servico: 'Vacinação', categoria: 'Vacinas', forma: 'PIX', status: 'Pago', valor: 145.0, data: '18/06/2025' },
  { id: 3, pacienteNome: 'Luna', tutorNome: 'Carla Dias', servico: 'Consulta + exame', categoria: 'Procedimentos', forma: 'Dinheiro', status: 'Pendente', valor: 390.0, data: '18/06/2025' },
];

// ---------------------------------------------------------------- tutores

export async function listarTutoresBackend(): Promise<Tutor[]> {
  const response = await apiFetch('/tutores');
  if (!response.ok) await parseApiError(response, 'Erro ao carregar tutores.');
  const lista = await response.json();
  return lista.map((t: any) => ({
    id: t.id, nome: t.nome ?? '', cpf: t.cpf ?? '', telefone: t.telefone ?? '', email: t.email ?? '',
  }));
}

export async function cadastrarTutorBackend(tutor: Omit<Tutor, 'id'>): Promise<Tutor> {
  if (!tutor.email?.trim()) throw new Error('Informe o e-mail do tutor.');

  // A senha é aleatória e não é exibida: o tutor ainda não acessa o sistema.
  // Quando existir um fluxo de "definir/recuperar senha", ele será usado no primeiro acesso.
  const response = await apiFetch('/tutores', {
    method: 'POST',
    body: JSON.stringify({
      nome: tutor.nome,
      cpf: tutor.cpf,
      telefone: tutor.telefone,
      email: tutor.email.trim(),
      senha: senhaAleatoria(),
    }),
  });
  if (!response.ok) await parseApiError(response, 'Erro ao cadastrar tutor.');
  const salvo = await response.json();
  return {
    id: salvo.id, nome: salvo.nome ?? tutor.nome, cpf: salvo.cpf ?? tutor.cpf,
    telefone: salvo.telefone ?? tutor.telefone, email: salvo.email ?? tutor.email,
  };
}

// ---------------------------------------------------------------- pets

export async function listarPetsBackend(): Promise<Pet[]> {
  const response = await apiFetch('/pets');
  if (!response.ok) await parseApiError(response, 'Erro ao carregar pets.');
  const lista = await response.json();
  return lista.map((p: any) => ({
    id: p.id, nome: p.nome, especie: p.especie ?? '', raca: p.raca ?? '', sexo: p.sexo ?? '',
    dataNascimento: p.dataNascimento ?? '', idade: p.idade ?? undefined, peso: p.peso ?? undefined,
    porte: p.porte ?? '', cor: p.cor ?? '', observacoes: p.observacoes ?? '',
    tutorId: p.tutorId, tutorNome: p.tutorNome ?? '', tutorTelefone: p.tutorTelefone ?? '',
  }));
}

export async function cadastrarPetBackend(pet: Omit<Pet, 'id'>): Promise<Pet> {
  const dataISO = normalizarDataISO(pet.dataNascimento);
  if (pet.dataNascimento && !dataISO) throw new Error('Data de nascimento inválida.');
  const response = await apiFetch('/pets', {
    method: 'POST',
    body: JSON.stringify({
      nome: pet.nome,
      especie: pet.especie,
      raca: pet.raca || null,
      idade: pet.idade ?? null,
      peso: pet.peso && pet.peso > 0 ? pet.peso : null,
      sexo: pet.sexo || null,
      porte: pet.porte || null,
      cor: pet.cor || null,
      dataNascimento: dataISO,
      observacoes: pet.observacoes || null,
      tutorId: pet.tutorId,
    }),
  });
  if (!response.ok) await parseApiError(response, 'Erro ao cadastrar paciente.');
  const salvo = await response.json();
  return {
    ...pet, id: salvo.id, dataNascimento: salvo.dataNascimento ?? dataISO ?? '',
    tutorId: salvo.tutorId ?? pet.tutorId, tutorNome: salvo.tutorNome ?? pet.tutorNome,
    tutorTelefone: salvo.tutorTelefone ?? pet.tutorTelefone,
  };
}

// ---------------------------------------------------------------- veterinários

export async function listarVeterinariosBackend(): Promise<Veterinario[]> {
  const response = await apiFetch('/veterinarios');
  if (!response.ok) await parseApiError(response, 'Erro ao carregar veterinários.');
  const lista = await response.json();
  return lista.map((v: any) => ({
    id: v.id, nome: v.nome, especialidade: v.biografia ?? '',
    registroProfissional: v.registroProfissional ?? '', valorConsulta: v.valorConsulta ?? undefined,
  }));
}

// ---------------------------------------------------------------- consultas

const CORES: AgendamentoItem['cor'][] = ['amber', 'blue', 'purple', 'rose', 'teal'];

function mapConsultaToAgendamento(c: any): AgendamentoItem {
  const [data, hora = ''] = String(c.inicio ?? '').split('T');
  const statusMap: Record<string, AgendamentoItem['status']> = {
    AGENDADA: 'CONFIRMADA', REALIZADA: 'REALIZADA', CANCELADA: 'CANCELADA',
  };
  return {
    id: c.id, data, horario: hora.slice(0, 5),
    petId: c.petId, petNome: c.petNome ?? '',
    tutorId: c.tutorId, tutorNome: c.tutorNome ?? '',
    veterinarioId: c.veterinarioId, veterinarioNome: c.veterinarioNome ?? '',
    tipo: c.motivo || 'Consulta',
    status: statusMap[c.status] ?? 'CONFIRMADA',
    cor: CORES[Number(c.veterinarioId) % CORES.length], // cor estável por veterinário
  };
}

const MESES = ['JAN', 'FEV', 'MAR', 'ABR', 'MAI', 'JUN', 'JUL', 'AGO', 'SET', 'OUT', 'NOV', 'DEZ'];

function mapConsultaToProntuario(c: any): ProntuarioRegistro {
  const [ano, mes, dia] = String(c.inicio ?? '').split('T')[0].split('-');
  return {
    id: c.id, petId: c.petId,
    data: `${dia} ${MESES[Number(mes) - 1] ?? ''} ${ano}`,
    titulo: c.motivo || 'Atendimento clínico',
    veterinarioNome: c.veterinarioNome ?? '',
    versao: `consulta #${c.id}`,
    queixaPrincipal: c.motivo ?? '', historicoEvolucao: '', alimentacao: '', ingestaoAgua: '',
    temperatura: '', frequenciaCardiaca: '', peso: '',
    achadosExameFisico: c.observacoes ?? '', diagnosticoPresuntivo: c.diagnostico ?? '',
    examesSolicitados: '', prescricaoTratamento: c.prescricao ?? '', orientacoesRetorno: '',
  };
}

export async function listarConsultasBackend(): Promise<AgendamentoItem[]> {
  const response = await apiFetch('/consultas');
  if (!response.ok) await parseApiError(response, 'Erro ao carregar consultas.');
  const lista = await response.json();
  return lista.map(mapConsultaToAgendamento);
}

/** Histórico clínico = consultas já realizadas (persistido no servidor). */
export async function listarHistoricoBackend(): Promise<ProntuarioRegistro[]> {
  const response = await apiFetch('/consultas?status=REALIZADA');
  if (!response.ok) await parseApiError(response, 'Erro ao carregar histórico clínico.');
  const lista = await response.json();
  return lista.map(mapConsultaToProntuario);
}

export async function agendarConsultaBackend(dados: {
  petId: number; tutorId?: number; veterinarioId: number; data: string; horario: string; observacoes: string;
}): Promise<AgendamentoItem> {
  // 1. Busca ou abre o horário (Agenda) para o veterinário na data/hora
  let agendaId: number | null = null;
  const respBusca = await apiFetch(`/agendas/veterinario/${dados.veterinarioId}?data=${dados.data}`);
  if (respBusca.ok) {
    const agendaDia = await respBusca.json();
    const slot = (agendaDia.horarios || []).find((h: any) => String(h.horario).startsWith(dados.horario));
    if (slot) {
      if (!slot.disponivel) throw new Error('Conflito de horário: este horário já está reservado.');
      agendaId = slot.id;
    }
  }
  if (!agendaId) {
    const respCriaSlot = await apiFetch('/agendas', {
      method: 'POST',
      body: JSON.stringify({ veterinarioId: dados.veterinarioId, data: dados.data, horarios: [dados.horario] }),
    });
    if (!respCriaSlot.ok) await parseApiError(respCriaSlot, 'Erro ao abrir horário na agenda.');
    const criada = await respCriaSlot.json();
    agendaId = criada.horarios?.[0]?.id ?? null;
    if (!agendaId) throw new Error('Não foi possível abrir o horário na agenda.');
  }

  // 2. Reserva a consulta usando agendaId + petId + tutorId
  const response = await apiFetch('/consultas', {
    method: 'POST',
    body: JSON.stringify({ agendaId, petId: dados.petId, tutorId: dados.tutorId, motivo: dados.observacoes }),
  });
  if (!response.ok) await parseApiError(response, 'Erro ao confirmar agendamento.');
  return mapConsultaToAgendamento(await response.json());
}

export async function cancelarConsultaBackend(consultaId: number): Promise<void> {
  const response = await apiFetch(`/consultas/${consultaId}/cancelar`, {
    method: 'PATCH',
    body: JSON.stringify({ motivo: 'Cancelado pela recepção.' }),
  });
  if (!response.ok) await parseApiError(response, 'Erro ao cancelar consulta.');
}

/** Só funciona para o veterinário responsável pela consulta (o backend valida pelo token). */
export async function registrarAtendimentoBackend(
  consultaId: number, dados: { diagnostico: string; prescricao: string; observacoes: string }
): Promise<ProntuarioRegistro> {
  const response = await apiFetch(`/consultas/${consultaId}/realizar`, {
    method: 'PATCH',
    body: JSON.stringify(dados),
  });
  if (!response.ok) await parseApiError(response, 'Erro ao finalizar atendimento no servidor.');
  return mapConsultaToProntuario(await response.json());
}
