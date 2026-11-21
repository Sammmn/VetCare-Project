import React, { useState } from 'react';
import { Search } from 'lucide-react';
import { Pet, Tutor } from '../types/vetcare';
import { cadastrarPetBackend, cadastrarTutorBackend } from '../services/api';

interface PacientesPageProps {
  pets: Pet[];
  tutores: Tutor[];
  onAddPetAndTutor: (novoPet: Pet, novoTutor?: Tutor) => void;
  onNotify: (msg: string) => void;
}

export const PacientesPage: React.FC<PacientesPageProps> = ({ tutores, onAddPetAndTutor, onNotify }) => {
  const [nomeAnimal, setNomeAnimal] = useState('');
  const [especie, setEspecie] = useState('');
  const [raca, setRaca] = useState('');
  const [sexo, setSexo] = useState('');
  const [dataNascimento, setDataNascimento] = useState('');
  const [pesoAtual, setPesoAtual] = useState('');
  const [porte, setPorte] = useState('');
  const [corPelagem, setCorPelagem] = useState('');
  const [observacoes, setObservacoes] = useState('');

  const [buscaTutor, setBuscaTutor] = useState('');
  const [tutorSelecionadoId, setTutorSelecionadoId] = useState<number | null>(null);
  const [nomeTutor, setNomeTutor] = useState('');
  const [cpfTutor, setCpfTutor] = useState('');
  const [telefoneTutor, setTelefoneTutor] = useState('');
  const [emailTutor, setEmailTutor] = useState('');
  const [salvando, setSalvando] = useState(false);

  const tutoresFiltrados = buscaTutor.trim()
    ? tutores.filter((t) => {
        const q = buscaTutor.toLowerCase();
        return t.nome.toLowerCase().includes(q) || t.cpf.toLowerCase().includes(q) || t.telefone.toLowerCase().includes(q);
      })
    : [];

  const handleSelecionarTutorExistente = (tutor: Tutor) => {
    setTutorSelecionadoId(tutor.id);
    setNomeTutor(tutor.nome);
    setCpfTutor(tutor.cpf);
    setTelefoneTutor(tutor.telefone);
    setEmailTutor(tutor.email);
    setBuscaTutor('');
  };

  const handleLimparFormulario = () => {
    setNomeAnimal(''); setEspecie(''); setRaca(''); setSexo(''); setDataNascimento('');
    setPesoAtual(''); setPorte(''); setCorPelagem(''); setObservacoes('');
    setBuscaTutor(''); setTutorSelecionadoId(null); setNomeTutor(''); setCpfTutor(''); setTelefoneTutor(''); setEmailTutor('');
  };

  const handleSalvarCadastro = async (e: React.FormEvent) => {
    e.preventDefault();
    if (salvando) return;
    if (!nomeAnimal.trim() || !especie || !sexo) { onNotify('Preencha Nome, Espécie e Sexo do animal.'); return; }
    if (!tutorSelecionadoId && (!nomeTutor.trim() || !cpfTutor.trim() || !telefoneTutor.trim() || !emailTutor.trim())) {
      onNotify('Vínculo obrigatório: selecione um tutor ou preencha Nome, CPF, Telefone e E-mail.');
      return;
    }

    setSalvando(true);
    try {
      let tutorIdFinal = tutorSelecionadoId;
      let tutorCriado: Tutor | undefined;
      if (!tutorIdFinal) {
        tutorCriado = await cadastrarTutorBackend({ nome: nomeTutor.trim(), cpf: cpfTutor.trim(), telefone: telefoneTutor.trim(), email: emailTutor.trim() });
        tutorIdFinal = tutorCriado.id;
      }
      const pesoNum = parseFloat(pesoAtual.replace(',', '.')) || 0;
      const petSalvo = await cadastrarPetBackend({
        nome: nomeAnimal.trim(), especie, raca: raca.trim(), sexo, dataNascimento,
        peso: pesoNum, porte, cor: corPelagem.trim(), observacoes: observacoes.trim(),
        tutorId: tutorIdFinal, tutorNome: tutorCriado?.nome ?? nomeTutor.trim(), tutorTelefone: tutorCriado?.telefone ?? telefoneTutor.trim(),
      });
      onAddPetAndTutor(petSalvo, tutorCriado);
      onNotify(`Paciente "${petSalvo.nome}" cadastrado e vinculado a ${petSalvo.tutorNome}!`);
      handleLimparFormulario();
    } catch (err: any) {
      onNotify(err?.message || 'Erro ao salvar cadastro no servidor.');
    } finally {
      setSalvando(false);
    }
  };
  return (
    <div className="page-content">
      <div className="info-banner-teal">
        <strong>Vínculo obrigatório:</strong> todo paciente deve estar associado a um tutor responsável.
      </div>

      <form onSubmit={handleSalvarCadastro}>
        <div className="paciente-grid">
          <div className="card" style={{ padding: '24px' }}>
            <div className="step-card-header">
              <div className="step-badge">1</div>
              <div>
                <h2 style={{ fontSize: '16px', fontWeight: 700 }}>Dados do animal</h2>
                <p style={{ fontSize: '12.5px', color: '#64748b', marginTop: '2px' }}>Informações principais do novo paciente</p>
              </div>
            </div>

            <div className="form-row-2">
              <div className="form-group">
                <label className="form-label">Nome do animal *</label>
                <input type="text" className="form-input" placeholder="Ex.: Nina" value={nomeAnimal} onChange={(e) => setNomeAnimal(e.target.value)} required />
              </div>
              <div className="form-group">
                <label className="form-label">Espécie *</label>
                <select className="form-select" value={especie} onChange={(e) => setEspecie(e.target.value)} required>
                  <option value="">Selecione</option>
                  <option value="Canino">Canino</option>
                  <option value="Felino">Felino</option>
                  <option value="Ave">Ave</option>
                  <option value="Silvestre / Exótico">Silvestre / Exótico</option>
                </select>
              </div>
            </div>

            <div className="form-row-2">
              <div className="form-group">
                <label className="form-label">Raça</label>
                <input type="text" className="form-input" placeholder="Ex.: Golden Retriever" value={raca} onChange={(e) => setRaca(e.target.value)} />
              </div>
              <div className="form-group">
                <label className="form-label">Sexo *</label>
                <select className="form-select" value={sexo} onChange={(e) => setSexo(e.target.value)}>
                  <option value="">Selecione</option>
                  <option value="Fêmea">Fêmea</option>
                  <option value="Macho">Macho</option>
                </select>
              </div>
            </div>

            <div className="form-row-2">
              <div className="form-group">
                <label className="form-label">Data de nascimento</label>
                <input type="date" className="form-input" value={dataNascimento} onChange={(e) => setDataNascimento(e.target.value)} />
              </div>
              <div className="form-group">
                <label className="form-label">Peso atual (kg)</label>
                <input type="text" className="form-input" placeholder="0,0" value={pesoAtual} onChange={(e) => setPesoAtual(e.target.value)} />
              </div>
            </div>

            <div className="form-row-2">
              <div className="form-group">
                <label className="form-label">Porte</label>
                <select className="form-select" value={porte} onChange={(e) => setPorte(e.target.value)}>
                  <option value="">Selecione</option>
                  <option value="Pequeno">Pequeno</option>
                  <option value="Médio">Médio</option>
                  <option value="Grande">Grande</option>
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Cor / pelagem</label>
                <input type="text" className="form-input" placeholder="Ex.: Caramelo" value={corPelagem} onChange={(e) => setCorPelagem(e.target.value)} />
              </div>
            </div>

            <div className="form-group" style={{ marginBottom: 0 }}>
              <label className="form-label">Observações</label>
              <textarea className="form-textarea" placeholder="Alergias, condições preexistentes..." value={observacoes} onChange={(e) => setObservacoes(e.target.value)} />
            </div>
          </div>

          <div className="card" style={{ padding: '24px' }}>
            <div className="step-card-header">
              <div className="step-badge">2</div>
              <div>
                <h2 style={{ fontSize: '16px', fontWeight: 700 }}>Tutor responsável</h2>
                <p style={{ fontSize: '12.5px', color: '#64748b', marginTop: '2px' }}>
                  {tutorSelecionadoId ? `Tutor existente vinculado (ID #${tutorSelecionadoId})` : 'Busque um tutor cadastrado ou preencha um novo'}
                </p>
              </div>
            </div>

            <div style={{ position: 'relative' }}>
              <Search size={16} color="#94a3b8" style={{ position: 'absolute', left: '13px', top: '50%', transform: 'translateY(-50%)' }} />
              <input type="text" className="form-input" style={{ paddingLeft: '38px' }} placeholder="Buscar tutor por CPF, nome ou telefone" value={buscaTutor} onChange={(e) => setBuscaTutor(e.target.value)} />
            </div>

            {tutoresFiltrados.length > 0 && (
              <div style={{ marginTop: '8px', border: '1px solid #e2e8f0', borderRadius: '10px', overflow: 'hidden' }}>
                {tutoresFiltrados.map((t) => (
                  <button key={t.id} type="button" onClick={() => handleSelecionarTutorExistente(t)} style={{ display: 'block', width: '100%', padding: '10px 14px', textAlign: 'left', background: '#f8fafc', border: 'none', borderBottom: '1px solid #e2e8f0', cursor: 'pointer', fontSize: '13px' }}>
                    <strong>{t.nome}</strong> · {t.cpf} · {t.telefone}
                  </button>
                ))}
              </div>
            )}

            <div className="divider-with-text">OU NOVO TUTOR</div>

            <div className="form-group">
              <label className="form-label">Nome completo *</label>
              <input type="text" className="form-input" placeholder="Nome do responsável" value={nomeTutor} onChange={(e) => { setTutorSelecionadoId(null); setNomeTutor(e.target.value); }} required />
            </div>
            <div className="form-group">
              <label className="form-label">CPF *</label>
              <input type="text" className="form-input" placeholder="000.000.000-00" value={cpfTutor} onChange={(e) => { setTutorSelecionadoId(null); setCpfTutor(e.target.value); }} required />
            </div>
            <div className="form-group">
              <label className="form-label">Telefone *</label>
              <input type="text" className="form-input" placeholder="(00) 00000-0000" value={telefoneTutor} onChange={(e) => { setTutorSelecionadoId(null); setTelefoneTutor(e.target.value); }} required />
            </div>
            <div className="form-group" style={{ marginBottom: 0 }}>
              <label className="form-label">E-mail *</label>
              <input type="email" className="form-input" placeholder="email@exemplo.com" value={emailTutor} onChange={(e) => { setTutorSelecionadoId(null); setEmailTutor(e.target.value); }} required />
            </div>
          </div>
        </div>

        <div className="card bottom-bar-card">
          <span style={{ fontSize: '13px', color: '#64748b' }}>Campos marcados com * são obrigatórios</span>
          <div style={{ display: 'flex', gap: '12px' }}>
            <button type="button" className="btn-secondary" onClick={handleLimparFormulario} disabled={salvando}>Cancelar</button>
            <button type="submit" className="btn-primary" disabled={salvando}>{salvando ? 'Salvando...' : 'Salvar cadastro'}</button>
          </div>
        </div>
      </form>
    </div>
  );
};