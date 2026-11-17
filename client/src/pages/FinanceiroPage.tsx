import React, { useState } from 'react';
import {
  Clock,
  ClipboardList,
  MoreHorizontal,
  Plus,
  TrendingDown,
  TrendingUp,
  Wallet,
  X,
} from 'lucide-react';
import { PagamentoItem, Pet } from '../types/vetcare';

interface FinanceiroPageProps {
  pagamentos: PagamentoItem[];
  pets: Pet[];
  onAddPagamento: (novo: Omit<PagamentoItem, 'id'>) => void;
}

const DAILY_BARS = [
  { label: '12 Qui', heightPx: 92 },
  { label: '13 Sex', heightPx: 126 },
  { label: '14 Sáb', heightPx: 100 },
  { label: '15 Dom', heightPx: 158 },
  { label: '16 Seg', heightPx: 136 },
  { label: '17 Ter', heightPx: 186 },
  { label: '18 Qua', heightPx: 166 },
];

export const FinanceiroPage: React.FC<FinanceiroPageProps> = ({
  pagamentos,
  pets,
  onAddPagamento,
}) => {
  const [periodo, setPeriodo] = useState('Últimos 7 dias');
  const [modalOpen, setModalOpen] = useState(false);
  const [petSelecionadoId, setPetSelecionadoId] = useState<number>(pets[0]?.id ?? 1);
  const [servico, setServico] = useState('Consulta');
  const [categoria, setCategoria] = useState<'Consultas' | 'Vacinas' | 'Procedimentos'>(
    'Consultas'
  );
  const [forma, setForma] = useState<'Cartão' | 'PIX' | 'Dinheiro'>('PIX');
  const [status, setStatus] = useState<'Pago' | 'Pendente'>('Pago');
  const [valor, setValor] = useState('180,00');

  const handleSalvarPagamento = (e: React.FormEvent) => {
    e.preventDefault();
    const petObj = pets.find((p) => p.id === petSelecionadoId) ?? pets[0];
    const valorNum = parseFloat(valor.replace('.', '').replace(',', '.')) || 180;
    onAddPagamento({
      pacienteNome: petObj?.nome ?? 'Mel',
      tutorNome: petObj?.tutorNome ?? 'Marina Lima',
      servico,
      categoria,
      forma,
      status,
      valor: valorNum,
      data: '18/06/2025',
    });
    setModalOpen(false);
  };

  return (
    <div className="page-content">
      {/* Top Toolbar */}
      <div className="toolbar-row">
        <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
          <select
            className="form-select"
            style={{ width: '170px', fontWeight: 600, padding: '8px 14px' }}
            value={periodo}
            onChange={(e) => setPeriodo(e.target.value)}
          >
            <option value="Últimos 7 dias">Últimos 7 dias</option>
            <option value="Este mês">Este mês</option>
            <option value="Últimos 30 dias">Últimos 30 dias</option>
          </select>
          <span style={{ fontSize: '13.5px', color: '#64748b' }}>
            12–18 de junho de 2025
          </span>
        </div>

        <button
          type="button"
          className="btn-primary"
          onClick={() => setModalOpen(true)}
        >
          <Plus size={16} />
          <span>Registrar pagamento</span>
        </button>
      </div>

      {/* 4 KPI Summary Cards */}
      <div className="kpi-grid">
        <div className="card kpi-card">
          <div className="kpi-top">
            <div className="kpi-icon-box">
              <Wallet size={17} />
            </div>
            <span className="trend-badge up">
              <TrendingUp size={12} /> 12,5%
            </span>
          </div>
          <div className="kpi-value">R$ 18.450</div>
          <div className="kpi-label">Receita no período</div>
        </div>

        <div className="card kpi-card">
          <div className="kpi-top">
            <div className="kpi-icon-box">
              <ClipboardList size={17} />
            </div>
            <span className="trend-badge up">
              <TrendingUp size={12} /> 8,2%
            </span>
          </div>
          <div className="kpi-value">84</div>
          <div className="kpi-label">Atendimentos</div>
        </div>

        <div className="card kpi-card">
          <div className="kpi-top">
            <div className="kpi-icon-box">
              <TrendingUp size={17} />
            </div>
            <span className="trend-badge up">
              <TrendingUp size={12} /> 4,1%
            </span>
          </div>
          <div className="kpi-value">R$ 219,64</div>
          <div className="kpi-label">Ticket médio</div>
        </div>

        <div className="card kpi-card">
          <div className="kpi-top">
            <div className="kpi-icon-box">
              <Clock size={17} />
            </div>
            <span className="trend-badge down">
              <TrendingDown size={12} /> 3,6%
            </span>
          </div>
          <div className="kpi-value">R$ 2.380</div>
          <div className="kpi-label">Pagamentos pendentes</div>
        </div>
      </div>

      {/* Charts Row */}
      <div className="charts-grid">
        <div className="card" style={{ padding: '22px 24px' }}>
          <div
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'flex-start',
              marginBottom: '12px',
            }}
          >
            <div>
              <h2 style={{ fontSize: '16px', fontWeight: 700 }}>Receita recebida</h2>
              <p style={{ fontSize: '12.5px', color: '#64748b', marginTop: '2px' }}>
                Valores confirmados por dia
              </p>
            </div>
            <MoreHorizontal size={18} color="#94a3b8" />
          </div>

          <div className="bar-chart-area">
            {DAILY_BARS.map((bar) => (
              <div key={bar.label} className="bar-col">
                <div
                  className="bar-fill"
                  style={{ height: `${bar.heightPx}px` }}
                />
              </div>
            ))}
          </div>
          <div
            style={{
              display: 'flex',
              justifyContent: 'space-around',
              padding: '6px 12px 0 12px',
            }}
          >
            {DAILY_BARS.map((bar) => (
              <div
                key={bar.label}
                className="bar-day-label"
                style={{ flex: 1, textAlign: 'center' }}
              >
                {bar.label}
              </div>
            ))}
          </div>
        </div>

        <div className="card" style={{ padding: '22px 24px' }}>
          <div
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'flex-start',
              marginBottom: '12px',
            }}
          >
            <div>
              <h2 style={{ fontSize: '16px', fontWeight: 700 }}>Atendimentos</h2>
              <p style={{ fontSize: '12.5px', color: '#64748b', marginTop: '2px' }}>
                Por categoria
              </p>
            </div>
            <button
              type="button"
              style={{
                background: 'none',
                border: 'none',
                color: '#007a66',
                fontSize: '12.5px',
                fontWeight: 600,
                cursor: 'pointer',
              }}
            >
              Detalhes
            </button>
          </div>

          <div className="donut-container">
            <div style={{ position: 'relative', width: '148px', height: '148px' }}>
              <svg viewBox="0 0 36 36" style={{ width: '100%', height: '100%' }}>
                {/* Consultas: 58% */}
                <circle
                  cx="18"
                  cy="18"
                  r="14"
                  fill="transparent"
                  stroke="#008f7a"
                  strokeWidth="4.8"
                  strokeDasharray="51 37"
                  strokeDashoffset="25"
                />
                {/* Vacinas: 23% */}
                <circle
                  cx="18"
                  cy="18"
                  r="14"
                  fill="transparent"
                  stroke="#00a8e8"
                  strokeWidth="4.8"
                  strokeDasharray="20 68"
                  strokeDashoffset="-26"
                />
                {/* Procedimentos: 19% */}
                <circle
                  cx="18"
                  cy="18"
                  r="14"
                  fill="transparent"
                  stroke="#f59e0b"
                  strokeWidth="4.8"
                  strokeDasharray="17 71"
                  strokeDashoffset="-46"
                />
              </svg>
              <div
                style={{
                  position: 'absolute',
                  inset: 0,
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  justifyContent: 'center',
                }}
              >
                <span style={{ fontSize: '20px', fontWeight: 700 }}>84</span>
                <span style={{ fontSize: '11.5px', color: '#64748b' }}>total</span>
              </div>
            </div>

            <div className="legend-list">
              <div className="legend-row">
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <span className="dot teal" style={{ backgroundColor: '#008f7a' }} />
                  <span style={{ color: '#475569' }}>Consultas</span>
                </div>
                <strong>49</strong>
              </div>
              <div className="legend-row">
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <span className="dot blue" style={{ backgroundColor: '#00a8e8' }} />
                  <span style={{ color: '#475569' }}>Vacinas</span>
                </div>
                <strong>19</strong>
              </div>
              <div className="legend-row">
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <span className="dot amber" style={{ backgroundColor: '#f59e0b' }} />
                  <span style={{ color: '#475569' }}>Procedimentos</span>
                </div>
                <strong>16</strong>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Pagamentos Recentes Table */}
      <div className="card table-card">
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            marginBottom: '14px',
          }}
        >
          <h2 style={{ fontSize: '16px', fontWeight: 700 }}>Pagamentos recentes</h2>
          <button
            type="button"
            style={{
              background: 'none',
              border: 'none',
              color: '#007a66',
              fontSize: '12.5px',
              fontWeight: 600,
              cursor: 'pointer',
            }}
          >
            Ver todos
          </button>
        </div>

        <table className="data-table">
          <thead>
            <tr>
              <th>PACIENTE / TUTOR</th>
              <th>SERVIÇO</th>
              <th>FORMA</th>
              <th>STATUS</th>
              <th style={{ textAlign: 'right' }}>VALOR</th>
            </tr>
          </thead>
          <tbody>
            {pagamentos.map((item) => (
              <tr key={item.id}>
                <td>
                  <strong>{item.pacienteNome}</strong>{' '}
                  <span style={{ color: '#64748b' }}>· {item.tutorNome}</span>
                </td>
                <td style={{ color: '#475569' }}>{item.servico}</td>
                <td style={{ color: '#475569' }}>{item.forma}</td>
                <td>
                  <span
                    className={
                      item.status === 'Pago'
                        ? 'status-badge-paid'
                        : 'status-badge-pending'
                    }
                  >
                    {item.status}
                  </span>
                </td>
                <td style={{ textAlign: 'right', fontWeight: 700 }}>
                  R${' '}
                  {item.valor.toLocaleString('pt-BR', {
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2,
                  })}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {modalOpen && (
        <div className="modal-backdrop" onClick={() => setModalOpen(false)}>
          <div className="modal-card" onClick={(e) => e.stopPropagation()}>
            <div
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                marginBottom: '18px',
              }}
            >
              <h3 style={{ fontSize: '18px', fontWeight: 700 }}>Registrar pagamento</h3>
              <button
                type="button"
                onClick={() => setModalOpen(false)}
                style={{ background: 'none', border: 'none', cursor: 'pointer' }}
              >
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleSalvarPagamento}>
              <div className="form-group">
                <label className="form-label">Paciente / Tutor *</label>
                <select
                  className="form-select"
                  value={petSelecionadoId}
                  onChange={(e) => setPetSelecionadoId(Number(e.target.value))}
                >
                  {pets.map((p) => (
                    <option key={p.id} value={p.id}>
                      {p.nome} · {p.tutorNome}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-row-2">
                <div className="form-group">
                  <label className="form-label">Serviço *</label>
                  <input
                    type="text"
                    className="form-input"
                    value={servico}
                    onChange={(e) => setServico(e.target.value)}
                    required
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Categoria *</label>
                  <select
                    className="form-select"
                    value={categoria}
                    onChange={(e) =>
                      setCategoria(
                        e.target.value as 'Consultas' | 'Vacinas' | 'Procedimentos'
                      )
                    }
                  >
                    <option value="Consultas">Consultas</option>
                    <option value="Vacinas">Vacinas</option>
                    <option value="Procedimentos">Procedimentos</option>
                  </select>
                </div>
              </div>

              <div className="form-row-3">
                <div className="form-group">
                  <label className="form-label">Forma *</label>
                  <select
                    className="form-select"
                    value={forma}
                    onChange={(e) =>
                      setForma(e.target.value as 'Cartão' | 'PIX' | 'Dinheiro')
                    }
                  >
                    <option value="Cartão">Cartão</option>
                    <option value="PIX">PIX</option>
                    <option value="Dinheiro">Dinheiro</option>
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Status *</label>
                  <select
                    className="form-select"
                    value={status}
                    onChange={(e) => setStatus(e.target.value as 'Pago' | 'Pendente')}
                  >
                    <option value="Pago">Pago</option>
                    <option value="Pendente">Pendente</option>
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Valor (R$) *</label>
                  <input
                    type="text"
                    className="form-input"
                    value={valor}
                    onChange={(e) => setValor(e.target.value)}
                    required
                  />
                </div>
              </div>

              <div className="form-footer-actions">
                <button
                  type="button"
                  className="btn-secondary"
                  onClick={() => setModalOpen(false)}
                >
                  Cancelar
                </button>
                <button type="submit" className="btn-primary">
                  Confirmar pagamento
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
