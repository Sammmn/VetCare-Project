import React, { useEffect, useState } from 'react';
import { ChevronLeft, ChevronRight, Plus, X } from 'lucide-react';
import { AgendamentoItem, Pet, Veterinario } from '../types/vetcare';
import { agendarConsultaBackend, cancelarConsultaBackend, hojeLocalISO } from '../services/api';

interface AgendaPageProps {
  agendamentos: AgendamentoItem[];
  pets: Pet[];
  veterinarios: Veterinario[];
  searchQuery: string;
  onAddAgendamento: (novo: AgendamentoItem) => void;
  onCancelAgendamento: (id: number) => void;
  onOpenAtendimento: (petId: number) => void;
  onNotify: (msg: string) => void;
}

const TIME_SLOTS = ['08:00', '09:00', '10:00', '11:00', '12:00', '13:00', '14:00'];

export const AgendaPage: React.FC<AgendaPageProps> = ({
  agendamentos, pets, veterinarios, searchQuery, onAddAgendamento, onCancelAgendamento, onOpenAtendimento, onNotify,
}) => {
  const [viewMode, setViewMode] = useState<'Dia' | 'Semana' | 'Profissional'>('Dia');
  const [selectedVetFilter, setSelectedVetFilter] = useState<number | 'ALL'>('ALL');
  const [modalOpen, setModalOpen] = useState(false);
  const [novoHorario, setNovoHorario] = useState('13:00');
  const [novoPetId, setNovoPetId] = useState<number>(pets[0]?.id ?? 1);
  const [novoVetId, setNovoVetId] = useState<number>(veterinarios[0]?.id ?? 1);
  const [novoTipo, setNovoTipo] = useState('Consulta de rotina');
  const [erroConflito, setErroConflito] = useState('');

  useEffect(() => {
    if (pets.length > 0 && !pets.some((p) => p.id === novoPetId)) setNovoPetId(pets[0].id);
  }, [pets, novoPetId]);

  useEffect(() => {
    if (veterinarios.length > 0 && !veterinarios.some((v) => v.id === novoVetId)) setNovoVetId(veterinarios[0].id);
  }, [veterinarios, novoVetId]);

  const hoje = hojeLocalISO();
  const filteredAgendamentos = agendamentos.filter((item) => {
    if (item.status === 'CANCELADA') return false;
    if (item.data !== hoje) return false; // esta grade mostra apenas o dia de hoje
    if (selectedVetFilter !== 'ALL' && item.veterinarioId !== selectedVetFilter) return false;
    if (!searchQuery.trim()) return true;
    const q = searchQuery.toLowerCase();
    return item.petNome.toLowerCase().includes(q) || item.tutorNome.toLowerCase().includes(q) || item.tipo.toLowerCase().includes(q);
  });

  const findSlotAppointment = (slotHour: string, vetId: number) =>
    filteredAgendamentos.find((a) => a.veterinarioId === vetId && a.horario.startsWith(slotHour));

  const handleOpenNewModal = (defaultTime = '13:00') => {
    setNovoHorario(defaultTime);
    setErroConflito('');
    setModalOpen(true);
  };

  const handleSalvarAgendamento = async (e: React.FormEvent) => {
    e.preventDefault();
    setErroConflito('');
    const petObj = pets.find((p) => p.id === Number(novoPetId)) ?? pets[0];
    const vetObj = veterinarios.find((v) => v.id === Number(novoVetId)) ?? veterinarios[0];
    if (!petObj || !petObj.tutorId) {
      setErroConflito('Vínculo obrigatório: o animal precisa ter um tutor vinculado.');
      return;
    }
    try {
      const salvo = await agendarConsultaBackend({ petId: petObj.id, tutorId: petObj.tutorId, veterinarioId: vetObj.id, data: hojeLocalISO(), horario: novoHorario, observacoes: novoTipo });
      onAddAgendamento(salvo);
      setModalOpen(false);
    } catch (err: any) {
      setErroConflito(err?.message || 'Erro ao agendar consulta.');
    }
  };

  const handleCancelar = async (item: AgendamentoItem) => {
    if (!window.confirm(`Deseja cancelar o agendamento de ${item.petNome} às ${item.horario}?`)) return;
    try {
      await cancelarConsultaBackend(item.id);
      onCancelAgendamento(item.id);
    } catch (err: any) {
      onNotify(err?.message || 'Erro ao cancelar consulta.');
    }
  };
  return (
    <div className="page-content">
      <div className="toolbar-row">
        <div style={{ display: 'flex', alignItems: 'center', gap: '20px', flexWrap: 'wrap' }}>
          <div className="segmented-control">
            {(['Dia', 'Semana', 'Profissional'] as const).map((mode) => (
              <button key={mode} type="button" className={`segment-btn ${viewMode === mode ? 'active' : ''}`} onClick={() => { setViewMode(mode); if (mode !== 'Profissional') setSelectedVetFilter('ALL'); }}>
                {mode}
              </button>
            ))}
          </div>

          {viewMode === 'Profissional' && (
            <select className="form-select" style={{ width: '200px', padding: '7px 12px' }} value={selectedVetFilter} onChange={(e) => setSelectedVetFilter(e.target.value === 'ALL' ? 'ALL' : Number(e.target.value))}>
              <option value="ALL">Todos os profissionais</option>
              {veterinarios.map((v) => (<option key={v.id} value={v.id}>{v.nome} ({v.especialidade})</option>))}
            </select>
          )}

          <div className="date-nav">
            <button type="button" className="date-nav-btn" aria-label="Dia anterior"><ChevronLeft size={16} /></button>
            <span style={{ fontWeight: 600, fontSize: '13px', padding: '0 4px' }}>Hoje</span>
            <button type="button" className="date-nav-btn" aria-label="Próximo dia"><ChevronRight size={16} /></button>
            <span style={{ marginLeft: '6px' }}><strong>Agenda de hoje</strong></span>
          </div>
        </div>

        <button type="button" className="btn-primary" onClick={() => handleOpenNewModal('13:00')}><Plus size={16} /><span>Novo agendamento</span></button>
      </div>

      <div className="agenda-grid-layout">
        <div className="card schedule-table-card" style={{ overflowX: 'auto' }}>
          <div style={{ minWidth: `${76 + Math.max(veterinarios.length, 1) * 240}px` }}>
            <div
              className="schedule-header-row"
              style={{ gridTemplateColumns: `76px repeat(${Math.max(veterinarios.length, 1)}, minmax(240px, 1fr))` }}
            >
              <div />
              {veterinarios.map((vet) => (<div key={vet.id} className="schedule-col-header"><div className="vet-col-name">{vet.nome}</div><div className="vet-col-spec">{vet.especialidade}</div></div>))}
            </div>

            {TIME_SLOTS.map((slot) => {
              const slotAppts = veterinarios.map((vet) => findSlotAppointment(slot, vet.id));
              const hasAnyAppt = slotAppts.some(Boolean);
              const gridStyle = { gridTemplateColumns: `76px repeat(${Math.max(veterinarios.length, 1)}, minmax(240px, 1fr))` };

              if (slot === '13:00' && !hasAnyAppt) {
                return (
                  <div key={slot} className="schedule-row" style={gridStyle}>
                    <div className="schedule-time-cell">{slot}</div>
                    <div className="schedule-slot-span" style={{ gridColumn: '2 / -1' }}><button type="button" className="available-pill-btn" onClick={() => handleOpenNewModal('13:00')}>Horário disponível · clique para agendar</button></div>
                  </div>
                );
              }

              return (
                <div key={slot} className="schedule-row" style={gridStyle}>
                  <div className="schedule-time-cell">{slot}</div>
                  {slotAppts.map((appt, i) => (
                    <div key={veterinarios[i]?.id ?? i} className="schedule-slot-cell">
                      {appt && (
                        <div className={`appt-card ${appt.cor}`} onClick={() => onOpenAtendimento(appt.petId)} title="Clique para abrir o prontuário">
                          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                            <div className="appt-title">{appt.horario} · {appt.petNome}</div>
                            <button type="button" onClick={(e) => { e.stopPropagation(); handleCancelar(appt); }} style={{ border: 'none', background: 'transparent', cursor: 'pointer', color: '#64748b', padding: '2px' }} title="Cancelar consulta"><X size={13} /></button>
                          </div>
                          <div className="appt-sub">{appt.tipo}</div>
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              );
            })}
          </div>
        </div>

        <div className="side-widgets-col">
          <div className="card widget-card">
            <div className="widget-header"><span className="widget-title">Resumo do dia</span><span className="badge-teal">{filteredAgendamentos.length} consultas</span></div>
            <div className="summary-stats-grid">
              <div className="summary-stat-box"><div className="summary-stat-num">{String(filteredAgendamentos.filter((a) => a.status === 'CONFIRMADA').length).padStart(2, '0')}</div><div className="summary-stat-label">Confirmadas</div></div>
              <div className="summary-stat-box"><div className="summary-stat-num">{String(filteredAgendamentos.filter((a) => a.status === 'REALIZADA').length).padStart(2, '0')}</div><div className="summary-stat-label">Realizadas</div></div>
            </div>
          </div>

          <div className="card widget-card">
            <div className="widget-header"><span className="widget-title">Próximos</span></div>
            <div className="upcoming-list">
              {filteredAgendamentos.slice(0, 4).map((item) => (
                <div key={item.id} className="upcoming-item" onClick={() => onOpenAtendimento(item.petId)}>
                  <div className="upcoming-item-left"><span className={`dot ${item.cor}`} /><div><div style={{ fontSize: '13.5px', fontWeight: 700 }}>{item.horario} · {item.petNome}</div><div style={{ fontSize: '12px', color: '#64748b' }}>{item.veterinarioNome}</div></div></div>
                  <ChevronRight size={15} color="#94a3b8" />
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      {modalOpen && (
        <div className="modal-backdrop" onClick={() => setModalOpen(false)}>
          <div className="modal-card" onClick={(e) => e.stopPropagation()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '18px' }}>
              <h3 style={{ fontSize: '18px', fontWeight: 700 }}>Novo agendamento</h3>
              <button type="button" onClick={() => setModalOpen(false)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}><X size={18} /></button>
            </div>

            {erroConflito && <div style={{ backgroundColor: '#fff1f2', border: '1px solid #fecdd3', color: '#be123c', padding: '10px 14px', borderRadius: '10px', fontSize: '13px', marginBottom: '14px' }}>{erroConflito}</div>}

            <form onSubmit={handleSalvarAgendamento}>
              <div className="form-group">
                <label className="form-label">Paciente (Animal e Tutor) *</label>
                <select className="form-select" value={novoPetId} onChange={(e) => setNovoPetId(Number(e.target.value))}>
                  {pets.map((p) => (<option key={p.id} value={p.id}>{p.nome} ({p.especie}) — Tutor: {p.tutorNome}</option>))}
                </select>
              </div>
              <div className="form-row-2">
                <div className="form-group">
                  <label className="form-label">Profissional *</label>
                  <select className="form-select" value={novoVetId} onChange={(e) => setNovoVetId(Number(e.target.value))}>
                    {veterinarios.map((v) => (<option key={v.id} value={v.id}>{v.nome} — {v.especialidade}</option>))}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Horário *</label>
                  <select className="form-select" value={novoHorario} onChange={(e) => setNovoHorario(e.target.value)}>
                    {TIME_SLOTS.map((t) => (<option key={t} value={t}>{t}</option>))}
                  </select>
                </div>
              </div>
              <div className="form-group"><label className="form-label">Tipo de atendimento *</label><input type="text" className="form-input" value={novoTipo} onChange={(e) => setNovoTipo(e.target.value)} required /></div>
              <div className="form-footer-actions"><button type="button" className="btn-secondary" onClick={() => setModalOpen(false)}>Cancelar</button><button type="submit" className="btn-primary">Confirmar agendamento</button></div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};