import React, { useState } from 'react';
import { Header } from './components/Header';
import { Sidebar } from './components/Sidebar';
import { AgendaPage } from './pages/AgendaPage';
import { AtendimentoPage } from './pages/AtendimentoPage';
import { FinanceiroPage } from './pages/FinanceiroPage';
import { PacientesPage } from './pages/PacientesPage';
import {
  INITIAL_AGENDAMENTOS,
  INITIAL_HISTORICO_CLINICO,
  INITIAL_PAGAMENTOS,
  INITIAL_PETS,
  INITIAL_TUTORES,
  INITIAL_VETERINARIOS,
} from './services/api';
import {
  AgendamentoItem,
  PagamentoItem,
  PerfilUsuario,
  Pet,
  ProntuarioRegistro,
  ScreenTab,
  Tutor,
} from './types/vetcare';

export function App() {
  const [activeTab, setActiveTab] = useState<ScreenTab>('agenda');
  const [perfil, setPerfil] = useState<PerfilUsuario>('ADMIN');
  const [searchQuery, setSearchQuery] = useState('');
  const [toastMsg, setToastMsg] = useState<string | null>(null);

  const [tutores, setTutores] = useState<Tutor[]>(INITIAL_TUTORES);
  const [pets, setPets] = useState<Pet[]>(INITIAL_PETS);
  const [agendamentos, setAgendamentos] =
    useState<AgendamentoItem[]>(INITIAL_AGENDAMENTOS);
  const [historico, setHistorico] = useState<ProntuarioRegistro[]>(
    INITIAL_HISTORICO_CLINICO
  );
  const [pagamentos, setPagamentos] = useState<PagamentoItem[]>(INITIAL_PAGAMENTOS);
  const [selectedPetId, setSelectedPetId] = useState<number>(1);

  const showToast = (msg: string) => {
    setToastMsg(msg);
    setTimeout(() => {
      setToastMsg(null);
    }, 3500);
  };

  const screenHeaders: Record<ScreenTab, { title: string; subtitle: string }> = {
    agenda: {
      title: 'Agenda',
      subtitle: 'Organize os atendimentos da clínica',
    },
    atendimento: {
      title: 'Atendimento',
      subtitle: 'Prontuário clínico em andamento',
    },
    pacientes: {
      title: 'Novo paciente',
      subtitle: 'Cadastre o animal e vincule seu tutor',
    },
    financeiro: {
      title: 'Financeiro',
      subtitle: 'Acompanhe a saúde financeira da clínica',
    },
  };

  const selectedPet = pets.find((p) => p.id === selectedPetId) ?? pets[0];

  return (
    <div className="app-shell">
      <Sidebar
        activeTab={activeTab}
        onSelectTab={setActiveTab}
        perfil={perfil}
        onChangePerfil={(novoPerfil) => {
          setPerfil(novoPerfil);
          if (novoPerfil === 'RECEPCIONISTA' && activeTab === 'atendimento') {
            setActiveTab('agenda');
          }
          if (novoPerfil === 'VETERINARIO' && activeTab === 'financeiro') {
            setActiveTab('agenda');
          }
          showToast(`Perfil ativo alterado para: ${novoPerfil}`);
        }}
      />

      <div className="main-area">
        <Header
          title={screenHeaders[activeTab].title}
          subtitle={screenHeaders[activeTab].subtitle}
          searchQuery={searchQuery}
          onSearchChange={setSearchQuery}
        />

        {activeTab === 'agenda' && (
          <AgendaPage
            agendamentos={agendamentos}
            pets={pets}
            veterinarios={INITIAL_VETERINARIOS}
            searchQuery={searchQuery}
            onAddAgendamento={(novo) => {
              setAgendamentos((prev) => [
                ...prev,
                { ...novo, id: prev.length + 10 },
              ]);
              showToast(
                `Agendamento de ${novo.petNome} confirmado para ${novo.horario}!`
              );
            }}
            onCancelAgendamento={(id) => {
              setAgendamentos((prev) =>
                prev.map((a) =>
                  a.id === id ? { ...a, status: 'CANCELADA' } : a
                )
              );
              showToast('Agendamento cancelado com sucesso.');
            }}
            onOpenAtendimento={(petId) => {
              setSelectedPetId(petId);
              setActiveTab('atendimento');
            }}
          />
        )}

        {activeTab === 'atendimento' && selectedPet && (
          <AtendimentoPage
            pet={selectedPet}
            historico={historico.filter((h) => h.petId === selectedPet.id || h.petId === 1)}
            onSaveProntuarioVersion={(novoReg) => {
              setHistorico((prev) => [
                { ...novoReg, id: Date.now() },
                ...prev,
              ]);
            }}
            onNotify={showToast}
          />
        )}

        {activeTab === 'pacientes' && (
          <PacientesPage
            pets={pets}
            tutores={tutores}
            onAddPetAndTutor={(novoPet, novoTutor) => {
              let tutorIdFinal = novoPet.tutorId;
              if (novoTutor) {
                tutorIdFinal = tutores.length + 1;
                setTutores((prev) => [
                  ...prev,
                  { ...novoTutor, id: tutorIdFinal },
                ]);
              }
              setPets((prev) => [
                ...prev,
                { ...novoPet, id: prev.length + 1, tutorId: tutorIdFinal },
              ]);
            }}
            onNotify={showToast}
          />
        )}

        {activeTab === 'financeiro' && (
          <FinanceiroPage
            pagamentos={pagamentos}
            pets={pets}
            onAddPagamento={(novo) => {
              setPagamentos((prev) => [
                { ...novo, id: prev.length + 10 },
                ...prev,
              ]);
              showToast('Pagamento registrado com sucesso!');
            }}
          />
        )}
      </div>

      {toastMsg && <div className="toast-banner">{toastMsg}</div>}
    </div>
  );
}

export default App;
