import React, { useCallback, useEffect, useState } from 'react';
import { Header } from './components/Header';
import { Login } from './components/Login';
import { Sidebar } from './components/Sidebar';
import { AgendaPage } from './pages/AgendaPage';
import { AtendimentoPage } from './pages/AtendimentoPage';
import { FinanceiroPage } from './pages/FinanceiroPage';
import { PacientesPage } from './pages/PacientesPage';
import {
  EVENTO_LOGOUT,
  INITIAL_PAGAMENTOS,
  Sessao,
  getSessao,
  hojeLocalISO,
  listarConsultasBackend,
  listarHistoricoBackend,
  listarPetsBackend,
  listarTutoresBackend,
  listarVeterinariosBackend,
  logout,
} from './services/api';
import {
  AgendamentoItem,
  PagamentoItem,
  PerfilUsuario,
  Pet,
  ProntuarioRegistro,
  ScreenTab,
  Tutor,
  Veterinario,
} from './types/vetcare';

/** Porta de entrada: sem sessão válida, só a tela de login é exibida. */
export function App() {
  const [sessao, setSessao] = useState<Sessao | null>(() => getSessao());

  useEffect(() => {
    const aoExpirar = () => setSessao(null);
    window.addEventListener(EVENTO_LOGOUT, aoExpirar);
    return () => window.removeEventListener(EVENTO_LOGOUT, aoExpirar);
  }, []);

  if (!sessao) {
    return <Login onLogin={setSessao} />;
  }

  return (
    <Painel
      sessao={sessao}
      onLogout={() => {
        logout();
        setSessao(null);
      }}
    />
  );
}

interface PainelProps {
  sessao: Sessao;
  onLogout: () => void;
}

function Painel({ sessao, onLogout }: PainelProps) {
  // O perfil vem do token (cargo), nunca de um seletor na tela.
  const perfil: PerfilUsuario =
    sessao.usuario.cargo === 'ADMIN' ? 'ADMIN' : sessao.usuario.cargo === 'RECEPCIONISTA' ? 'RECEPCIONISTA' : 'VETERINARIO';

  const [activeTab, setActiveTab] = useState<ScreenTab>('agenda');
  const [searchQuery, setSearchQuery] = useState('');
  const [toastMsg, setToastMsg] = useState<string | null>(null);
  const [erroCarga, setErroCarga] = useState<string | null>(null);

  const [tutores, setTutores] = useState<Tutor[]>([]);
  const [pets, setPets] = useState<Pet[]>([]);
  const [veterinarios, setVeterinarios] = useState<Veterinario[]>([]);
  const [agendamentos, setAgendamentos] = useState<AgendamentoItem[]>([]);
  const [historico, setHistorico] = useState<ProntuarioRegistro[]>([]);
  const [pagamentos, setPagamentos] = useState<PagamentoItem[]>(INITIAL_PAGAMENTOS); // ainda sem API
  const [selectedPetId, setSelectedPetId] = useState<number | null>(null);

  const showToast = (msg: string) => {
    setToastMsg(msg);
    setTimeout(() => {
      setToastMsg(null);
    }, 4000);
  };

  const sincronizarBackend = useCallback(async () => {
    try {
      const [tutoresApi, petsApi, vetsApi, consultasApi, historicoApi] = await Promise.all([
        listarTutoresBackend(),
        listarPetsBackend(),
        listarVeterinariosBackend(),
        listarConsultasBackend(),
        // a recepção não acessa dados clínicos
        perfil === 'RECEPCIONISTA' ? Promise.resolve<ProntuarioRegistro[]>([]) : listarHistoricoBackend(),
      ]);
      setTutores(tutoresApi);
      setPets(petsApi);
      setVeterinarios(vetsApi);
      setAgendamentos(consultasApi);
      setHistorico(historicoApi);
      setSelectedPetId((atual) => (atual !== null && petsApi.some((p) => p.id === atual) ? atual : petsApi[0]?.id ?? null));
      setErroCarga(null);
    } catch (err: any) {
      // Sem fallback para dados fictícios: o usuário precisa saber que a carga falhou.
      setErroCarga(err?.message || 'Não foi possível carregar os dados do servidor.');
    }
  }, [perfil]);

  useEffect(() => {
    sincronizarBackend();
  }, [sincronizarBackend]);

  const screenHeaders: Record<ScreenTab, { title: string; subtitle: string }> = {
    agenda: { title: 'Agenda', subtitle: 'Organize os atendimentos da clínica' },
    atendimento: { title: 'Atendimento', subtitle: 'Prontuário clínico em andamento' },
    pacientes: { title: 'Novo paciente', subtitle: 'Cadastre o animal e vincule seu tutor' },
    financeiro: { title: 'Financeiro', subtitle: 'Acompanhe a saúde financeira da clínica' },
  };

  const selectedPet = pets.find((p) => p.id === selectedPetId) ?? pets[0];

  // Consulta de hoje ainda pendente para o pet selecionado (a mais cedo primeiro)
  const hoje = hojeLocalISO();
  const agendamentoDoPet = agendamentos
    .filter((a) => a.petId === selectedPet?.id && a.status === 'CONFIRMADA' && a.data === hoje)
    .sort((a, b) => a.horario.localeCompare(b.horario))[0];

  return (
    <div className="app-shell">
      <Sidebar
        activeTab={activeTab}
        onSelectTab={setActiveTab}
        perfil={perfil}
        usuario={sessao.usuario}
        onLogout={onLogout}
      />

      <div className="main-area">
        <Header
          title={screenHeaders[activeTab].title}
          subtitle={screenHeaders[activeTab].subtitle}
          searchQuery={searchQuery}
          onSearchChange={setSearchQuery}
        />

        {erroCarga && (
          <div
            role="alert"
            className="info-banner-teal"
            style={{ background: '#fef2f2', color: '#b91c1c', borderColor: '#fecaca', display: 'flex', justifyContent: 'space-between', gap: '12px' }}
          >
            <span>{erroCarga}</span>
            <button type="button" className="btn-secondary" onClick={sincronizarBackend}>Tentar novamente</button>
          </div>
        )}

        {activeTab === 'agenda' && (
          <AgendaPage
            agendamentos={agendamentos}
            pets={pets}
            veterinarios={veterinarios}
            searchQuery={searchQuery}
            onAddAgendamento={(novo) => {
              setAgendamentos((prev) => [...prev, novo]);
              showToast(`Agendamento de ${novo.petNome} confirmado para ${novo.horario}!`);
            }}
            onCancelAgendamento={(id) => {
              setAgendamentos((prev) =>
                prev.map((a) => (a.id === id ? { ...a, status: 'CANCELADA' } : a))
              );
              showToast('Agendamento cancelado com sucesso.');
            }}
            onOpenAtendimento={(petId) => {
              setSelectedPetId(petId);
              setActiveTab('atendimento');
            }}
            onNotify={showToast}
          />
        )}

        {activeTab === 'atendimento' && selectedPet && (
          <AtendimentoPage
            key={selectedPet.id}
            pet={selectedPet}
            agendamentoAtivo={agendamentoDoPet}
            historico={historico.filter((h) => h.petId === selectedPet.id)}
            onSaveProntuarioVersion={(novoReg) => {
              // anotações locais (ex.: vacina) — ainda sem endpoint no servidor
              setHistorico((prev) => [{ ...novoReg, id: Date.now() }, ...prev]);
            }}
            onConsultaFinalizada={(consultaId, registro) => {
              setAgendamentos((prev) =>
                prev.map((a) => (a.id === consultaId ? { ...a, status: 'REALIZADA' } : a))
              );
              setHistorico((prev) => [registro, ...prev.filter((h) => h.id !== registro.id)]);
            }}
            onNotify={showToast}
          />
        )}

        {activeTab === 'pacientes' && (
          <PacientesPage
            pets={pets}
            tutores={tutores}
            onAddPetAndTutor={(novoPet, novoTutor) => {
              if (novoTutor) {
                setTutores((prev) => [...prev, novoTutor]);
              }
              setPets((prev) => [...prev, novoPet]);
            }}
            onNotify={showToast}
          />
        )}

        {activeTab === 'financeiro' && (
          <>
            <div className="info-banner-teal">
              Dados de exemplo: o módulo financeiro ainda não está integrado ao servidor.
            </div>
            <FinanceiroPage
              pagamentos={pagamentos}
              pets={pets}
              onAddPagamento={(novo) => {
                setPagamentos((prev) => [{ ...novo, id: prev.length + 10 }, ...prev]);
                showToast('Pagamento registrado com sucesso!');
              }}
            />
          </>
        )}
      </div>

      {toastMsg && <div className="toast-banner">{toastMsg}</div>}
    </div>
  );
}

export default App;
