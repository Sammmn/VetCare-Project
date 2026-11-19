import React, { useState } from 'react';
import { Check, ChevronRight, FileText, PawPrint, Pill, Syringe, X } from 'lucide-react';
import { AgendamentoItem, Pet, ProntuarioRegistro } from '../types/vetcare';
import { registrarAtendimentoBackend } from '../services/api';

interface AtendimentoPageProps {
  pet: Pet;
  agendamentoAtivo?: AgendamentoItem;
  historico: ProntuarioRegistro[];
  onSaveProntuarioVersion: (registro: Omit<ProntuarioRegistro, 'id'>) => void;
  onNotify: (msg: string) => void;
}

type ClinicalTab = 'Anamnese' | 'Exame físico' | 'Diagnóstico' | 'Conduta';

export const AtendimentoPage: React.FC<AtendimentoPageProps> = ({ pet, agendamentoAtivo, historico, onSaveProntuarioVersion, onNotify }) => {
  const [activeSubTab, setActiveSubTab] = useState<ClinicalTab>('Anamnese');
  const [savedTime, setSavedTime] = useState('10:42');
  const [queixaPrincipal, setQueixaPrincipal] = useState('Falta de apetite e comportamento apático há dois dias.');
  const [historicoEvolucao, setHistoricoEvolucao] = useState('Tutora relata redução na ingestão de alimento desde segunda-feira.');
  const [alimentacao, setAlimentacao] = useState('Reduzida');
  const [ingestaoAgua, setIngestaoAgua] = useState('Normal');
  const [temperatura, setTemperatura] = useState('38,7');
  const [frequenciaCardiaca, setFrequenciaCardiaca] = useState('160');
  const [peso, setPeso] = useState(String(pet.peso || '4,2'));
  const [achadosExameFisico, setAchadosExameFisico] = useState('Mucosas normocoradas, TPC < 2s, hidratação adequada.');
  const [diagnosticoPresuntivo, setDiagnosticoPresuntivo] = useState('Gastrite aguda / corpo estranho linear a descartar');
  const [examesSolicitados, setExamesSolicitados] = useState('Hemograma completo e ultrassonografia abdominal.');
  const [prescricaoTratamento, setPrescricaoTratamento] = useState('Citrato de maropitant 1mg/kg SC em dose única.');
  const [orientacoesRetorno, setOrientacoesRetorno] = useState('Retorno em 48 horas com resultado dos exames.');
  const [vacinaModalOpen, setVacinaModalOpen] = useState(false);
  const [nomeVacina, setNomeVacina] = useState('Vacina Polivalente V4');
  const [proximaDose, setProximaDose] = useState('2026-06-18');

  const tabs: ClinicalTab[] = ['Anamnese', 'Exame físico', 'Diagnóstico', 'Conduta'];

  const handleSalvarRascunho = () => {
    const now = new Date();
    const hh = String(now.getHours()).padStart(2, '0');
    const mm = String(now.getMinutes()).padStart(2, '0');
    setSavedTime(`${hh}:${mm}`);
    onNotify(`Rascunho salvo às ${hh}:${mm}`);
  };

  const handleConcluirEtapa = async () => {
    const idx = tabs.indexOf(activeSubTab);
    if (idx < tabs.length - 1) {
      setActiveSubTab(tabs[idx + 1]);
      handleSalvarRascunho();
      return;
    }

    const consultaId = agendamentoAtivo?.id ?? 1;
    const profissionalId = agendamentoAtivo?.veterinarioId ?? 1;
    try {
      await registrarAtendimentoBackend(consultaId, profissionalId, {
        diagnostico: diagnosticoPresuntivo,
        prescricao: prescricaoTratamento,
        observacoes: `${queixaPrincipal} | ${achadosExameFisico}`,
      });
      onSaveProntuarioVersion({
        petId: pet.id, data: new Date().toLocaleDateString('pt-BR', { day: '2-digit', month: 'short', year: 'numeric' }).toUpperCase(),
        titulo: 'Atendimento clínico finalizado', veterinarioNome: agendamentoAtivo?.veterinarioNome ?? 'Dra. Camila',
        versao: `versão 1.${historico.length}`, atual: true, queixaPrincipal, historicoEvolucao, alimentacao, ingestaoAgua,
        temperatura, frequenciaCardiaca, peso, achadosExameFisico, diagnosticoPresuntivo, examesSolicitados, prescricaoTratamento, orientacoesRetorno,
      });
      onNotify(`Prontuário salvo e consulta #${consultaId} finalizada no servidor!`);
    } catch (err: any) {
      onNotify(err?.message || 'Erro ao registrar atendimento no servidor.');
    }
  };

  const handleRegistrarVacina = (e: React.FormEvent) => {
    e.preventDefault();
    onSaveProntuarioVersion({
      petId: pet.id, data: new Date().toLocaleDateString('pt-BR', { day: '2-digit', month: 'short', year: 'numeric' }).toUpperCase(),
      titulo: `${nomeVacina} (Próx. dose: ${proximaDose})`, veterinarioNome: agendamentoAtivo?.veterinarioNome ?? 'Dra. Camila',
      versao: 'versão 1.0', queixaPrincipal: `Aplicação de ${nomeVacina}`, historicoEvolucao: `Próxima dose agendada para ${proximaDose}.`,
      alimentacao, ingestaoAgua, temperatura, frequenciaCardiaca, peso, achadosExameFisico,
      diagnosticoPresuntivo: 'Imunização preventiva', examesSolicitados: '', prescricaoTratamento: nomeVacina, orientacoesRetorno: `Retorno em ${proximaDose}`,
    });
    setVacinaModalOpen(false);
    onNotify(`Vacina "${nomeVacina}" registrada na carteira vacinal!`);
  };
  return (
    <div className="page-content">
      <div className="card patient-banner-card">
        <div className="patient-banner-left">
          <div className="patient-avatar-box"><PawPrint size={24} /></div>
          <div>
            <div className="patient-name-row">
              <span className="patient-name">{pet.nome}</span>
              <span className="status-pill-active">EM ATENDIMENTO</span>
            </div>
            <div className="patient-meta-sub">{pet.especie} · {pet.raca} · {pet.idade ?? 1} anos · {String(pet.peso).replace('.', ',')} kg</div>
          </div>
        </div>
        <div className="patient-banner-right">
          <div><div className="meta-label">TUTOR</div><div className="meta-value">{pet.tutorNome}</div></div>
          <div><div className="meta-label">CONTATO</div><div className="meta-value">{pet.tutorTelefone || '(11) 98765-4321'}</div></div>
        </div>
      </div>

      <div className="atendimento-grid">
        <div className="card">
          <div className="tabs-header">
            {tabs.map((tab) => (
              <button key={tab} type="button" className={`tab-link ${activeSubTab === tab ? 'active' : ''}`} onClick={() => setActiveSubTab(tab)}>{tab}</button>
            ))}
          </div>

          <div className="clinical-form-body">
            {activeSubTab === 'Anamnese' && (
              <>
                <div className="form-section-top">
                  <div><h2 style={{ fontSize: '18px', fontWeight: 700 }}>Anamnese</h2><p style={{ fontSize: '13px', color: '#64748b' }}>Informações relatadas pelo tutor</p></div>
                  <div className="saved-indicator"><Check size={14} /><span>Salvo às {savedTime}</span></div>
                </div>
                <div className="form-group"><label className="form-label">Queixa principal</label><input type="text" className="form-input" value={queixaPrincipal} onChange={(e) => setQueixaPrincipal(e.target.value)} /></div>
                <div className="form-group"><label className="form-label">Histórico e evolução</label><textarea className="form-textarea" value={historicoEvolucao} onChange={(e) => setHistoricoEvolucao(e.target.value)} /></div>
                <div className="form-row-2">
                  <div className="form-group"><label className="form-label">Alimentação</label><select className="form-select" value={alimentacao} onChange={(e) => setAlimentacao(e.target.value)}><option value="Reduzida">Reduzida</option><option value="Normal">Normal</option><option value="Aumentada">Aumentada</option><option value="Ausente">Ausente</option></select></div>
                  <div className="form-group"><label className="form-label">Ingestão de água</label><select className="form-select" value={ingestaoAgua} onChange={(e) => setIngestaoAgua(e.target.value)}><option value="Normal">Normal</option><option value="Reduzida">Reduzida</option><option value="Aumentada">Aumentada</option></select></div>
                </div>
              </>
            )}

            {activeSubTab === 'Exame físico' && (
              <>
                <div className="form-section-top">
                  <div><h2 style={{ fontSize: '18px', fontWeight: 700 }}>Exame físico</h2><p style={{ fontSize: '13px', color: '#64748b' }}>Parâmetros vitais e avaliação geral</p></div>
                  <div className="saved-indicator"><Check size={14} /><span>Salvo às {savedTime}</span></div>
                </div>
                <div className="form-row-3">
                  <div className="form-group"><label className="form-label">Temperatura (°C)</label><input type="text" className="form-input" value={temperatura} onChange={(e) => setTemperatura(e.target.value)} /></div>
                  <div className="form-group"><label className="form-label">Freq. cardíaca (bpm)</label><input type="text" className="form-input" value={frequenciaCardiaca} onChange={(e) => setFrequenciaCardiaca(e.target.value)} /></div>
                  <div className="form-group"><label className="form-label">Peso (kg)</label><input type="text" className="form-input" value={peso} onChange={(e) => setPeso(e.target.value)} /></div>
                </div>
                <div className="form-group"><label className="form-label">Achados do exame físico</label><textarea className="form-textarea" value={achadosExameFisico} onChange={(e) => setAchadosExameFisico(e.target.value)} /></div>
              </>
            )}

            {activeSubTab === 'Diagnóstico' && (
              <>
                <div className="form-section-top">
                  <div><h2 style={{ fontSize: '18px', fontWeight: 700 }}>Diagnóstico</h2><p style={{ fontSize: '13px', color: '#64748b' }}>Hipóteses diagnósticas e exames</p></div>
                  <div className="saved-indicator"><Check size={14} /><span>Salvo às {savedTime}</span></div>
                </div>
                <div className="form-group"><label className="form-label">Diagnóstico presuntivo</label><input type="text" className="form-input" value={diagnosticoPresuntivo} onChange={(e) => setDiagnosticoPresuntivo(e.target.value)} /></div>
                <div className="form-group"><label className="form-label">Exames solicitados</label><textarea className="form-textarea" value={examesSolicitados} onChange={(e) => setExamesSolicitados(e.target.value)} /></div>
              </>
            )}

            {activeSubTab === 'Conduta' && (
              <>
                <div className="form-section-top">
                  <div><h2 style={{ fontSize: '18px', fontWeight: 700 }}>Conduta</h2><p style={{ fontSize: '13px', color: '#64748b' }}>Plano terapêutico e prescrição</p></div>
                  <div className="saved-indicator"><Check size={14} /><span>Salvo às {savedTime}</span></div>
                </div>
                <div className="form-group"><label className="form-label">Prescrição e tratamento</label><textarea className="form-textarea" value={prescricaoTratamento} onChange={(e) => setPrescricaoTratamento(e.target.value)} /></div>
                <div className="form-group"><label className="form-label">Orientações ao tutor e retorno</label><textarea className="form-textarea" value={orientacoesRetorno} onChange={(e) => setOrientacoesRetorno(e.target.value)} /></div>
              </>
            )}

            <div className="form-footer-actions">
              <button type="button" className="btn-secondary" onClick={handleSalvarRascunho}>Salvar rascunho</button>
              <button type="button" className="btn-primary" onClick={handleConcluirEtapa}><span>Concluir etapa</span><ChevronRight size={16} /></button>
            </div>
          </div>
        </div>

        <div className="side-widgets-col">
          <div className="card widget-card">
            <div className="widget-header"><span className="widget-title">Ações rápidas</span></div>
            <div className="quick-actions-list">
              <button type="button" className="quick-action-btn" onClick={() => setActiveSubTab('Conduta')}><div className="quick-action-left"><div className="qa-icon-box teal"><Pill size={17} /></div><div><div style={{ fontSize: '13.5px', fontWeight: 700 }}>Nova prescrição</div><div style={{ fontSize: '12px', color: '#64748b' }}>Medicamentos e orientações</div></div></div><ChevronRight size={15} color="#94a3b8" /></button>
              <button type="button" className="quick-action-btn" onClick={() => setVacinaModalOpen(true)}><div className="quick-action-left"><div className="qa-icon-box amber"><Syringe size={17} /></div><div><div style={{ fontSize: '13.5px', fontWeight: 700 }}>Registrar vacina</div><div style={{ fontSize: '12px', color: '#64748b' }}>Atualizar carteira vacinal</div></div></div><ChevronRight size={15} color="#94a3b8" /></button>
              <button type="button" className="quick-action-btn" onClick={() => setActiveSubTab('Diagnóstico')}><div className="quick-action-left"><div className="qa-icon-box purple"><FileText size={17} /></div><div><div style={{ fontSize: '13.5px', fontWeight: 700 }}>Solicitar exame</div><div style={{ fontSize: '12px', color: '#64748b' }}>Laboratorial ou imagem</div></div></div><ChevronRight size={15} color="#94a3b8" /></button>
            </div>
          </div>
          <div className="card widget-card">
            <div className="widget-header"><span className="widget-title">Histórico clínico</span></div>
            <div className="timeline-list">
              {historico.map((item, idx) => (
                <div key={item.id} className="timeline-item"><span className={`timeline-dot ${idx === 0 ? 'active' : ''}`} /><div className="timeline-date">{item.data}</div><div className="timeline-title">{item.titulo}</div><div className="timeline-sub">{item.veterinarioNome} · {item.versao}</div></div>
              ))}
            </div>
          </div>
        </div>
      </div>

      {vacinaModalOpen && (
        <div className="modal-backdrop" onClick={() => setVacinaModalOpen(false)}>
          <div className="modal-card" onClick={(e) => e.stopPropagation()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '18px' }}>
              <h3 style={{ fontSize: '18px', fontWeight: 700 }}>Registrar vacina — {pet.nome}</h3>
              <button type="button" onClick={() => setVacinaModalOpen(false)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}><X size={18} /></button>
            </div>
            <form onSubmit={handleRegistrarVacina}>
              <div className="form-group"><label className="form-label">Vacina aplicada *</label><input type="text" className="form-input" value={nomeVacina} onChange={(e) => setNomeVacina(e.target.value)} required /></div>
              <div className="form-group"><label className="form-label">Previsão da próxima dose *</label><input type="date" className="form-input" value={proximaDose} onChange={(e) => setProximaDose(e.target.value)} required /></div>
              <div className="form-footer-actions"><button type="button" className="btn-secondary" onClick={() => setVacinaModalOpen(false)}>Cancelar</button><button type="submit" className="btn-primary">Salvar vacina</button></div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};