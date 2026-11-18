// Fallback interativo caso o index.html seja aberto diretamente no navegador (duplo-clique ou Live Server) sem o Vite
(function () {
  function mountStandaloneApp() {
    const root = document.getElementById('root');
    if (!root || root.children.length > 0) return;

    const state = {
      activeTab: 'agenda',
      subTabAtendimento: 'Anamnese',
      viewModeAgenda: 'Dia',
      perfil: 'ADMIN',
      searchQuery: '',
      savedTime: '10:42',
      pets: [
        { id: 1, nome: 'Mel', especie: 'Felino', raca: 'SRD', idade: 4, peso: '4,2', sexo: 'Fêmea', porte: 'Pequeno', cor: 'Amarelo', tutorNome: 'Marina Lima', tutorTelefone: '(11) 98765-4321' },
        { id: 2, nome: 'Thor', especie: 'Canino', raca: 'Golden Retriever', idade: 3, peso: '28,5', sexo: 'Macho', porte: 'Grande', cor: 'Dourado', tutorNome: 'Pedro Alves', tutorTelefone: '(11) 97654-3210' },
        { id: 3, nome: 'Luna', especie: 'Felino', raca: 'Siamês', idade: 2, peso: '3,6', sexo: 'Fêmea', porte: 'Pequeno', cor: 'Creme', tutorNome: 'Carla Dias', tutorTelefone: '(11) 96543-2109' },
        { id: 4, nome: 'Simba', especie: 'Canino', raca: 'Bulldog Francês', idade: 5, peso: '12,0', sexo: 'Macho', porte: 'Médio', cor: 'Tigrado', tutorNome: 'Lucas Mendes', tutorTelefone: '(11) 95432-1098' }
      ],
      agendamentos: [
        { id: 1, horario: '08:00', petId: 1, petNome: 'Mel', vetId: 1, vetNome: 'Dra. Camila', tipo: 'Consulta de rotina', cor: 'amber' },
        { id: 2, horario: '09:30', petId: 2, petNome: 'Thor', vetId: 2, vetNome: 'Dr. Rafael', tipo: 'Vacinação', cor: 'blue' },
        { id: 3, horario: '11:00', petId: 3, petNome: 'Luna', vetId: 1, vetNome: 'Dra. Camila', tipo: 'Retorno', cor: 'purple' },
        { id: 4, horario: '14:30', petId: 4, petNome: 'Simba', vetId: 2, vetNome: 'Dr. Rafael', tipo: 'Exame dermatológico', cor: 'rose' }
      ],
      historico: [
        { data: '18 JUN 2025', titulo: 'Consulta atual', vet: 'Dra. Camila · versão 1.0', active: true },
        { data: '02 FEV 2025', titulo: 'Vacina antirrábica', vet: 'Dr. Rafael · versão 1.0', active: false },
        { data: '14 OUT 2024', titulo: 'Consulta de rotina', vet: 'Dra. Camila · versão 1.0', active: false }
      ],
      pagamentos: [
        { id: 1, paciente: 'Mel', tutor: 'Marina Lima', servico: 'Consulta', forma: 'Cartão', status: 'Pago', valor: 'R$ 180,00' },
        { id: 2, paciente: 'Thor', tutor: 'Pedro Alves', servico: 'Vacinação', forma: 'PIX', status: 'Pago', valor: 'R$ 145,00' },
        { id: 3, paciente: 'Luna', tutor: 'Carla Dias', servico: 'Consulta + exame', forma: 'Dinheiro', status: 'Pendente', valor: 'R$ 390,00' }
      ],
      selectedPetId: 1,
      toast: null
    };

    function showToast(msg) {
      state.toast = msg;
      render();
      setTimeout(() => {
        state.toast = null;
        render();
      }, 3200);
    }

    function renderAgenda() {
      const slots = ['08:00', '09:00', '10:00', '11:00', '12:00', '13:00', '14:00'];
      const rowsHtml = slots.map((slot) => {
        const prefix = slot.slice(0, 2);
        const a1 = state.agendamentos.find((a) => a.vetId === 1 && a.horario.startsWith(prefix));
        const a2 = state.agendamentos.find((a) => a.vetId === 2 && a.horario.startsWith(prefix));

        if (slot === '13:00' && !a1 && !a2) {
          return `
            <div class="schedule-row">
              <div class="schedule-time-cell">${slot}</div>
              <div class="schedule-slot-span">
                <button type="button" class="available-pill-btn" data-action="quick-schedule">
                  Horário disponível · clique para agendar
                </button>
              </div>
            </div>
          `;
        }

        return `
          <div class="schedule-row">
            <div class="schedule-time-cell">${slot}</div>
            <div class="schedule-slot-cell">
              ${a1 ? `
                <div class="appt-card ${a1.cor}" data-open-pet="${a1.petId}">
                  <div class="appt-title">${a1.horario} · ${a1.petNome}</div>
                  <div class="appt-sub">${a1.tipo}</div>
                </div>
              ` : ''}
            </div>
            <div class="schedule-slot-cell">
              ${a2 ? `
                <div class="appt-card ${a2.cor}" data-open-pet="${a2.petId}">
                  <div class="appt-title">${a2.horario} · ${a2.petNome}</div>
                  <div class="appt-sub">${a2.tipo}</div>
                </div>
              ` : ''}
            </div>
          </div>
        `;
      }).join('');

      const upcomingHtml = state.agendamentos.slice(0, 3).map((item) => `
        <div class="upcoming-item" data-open-pet="${item.petId}">
          <div class="upcoming-item-left">
            <span class="dot ${item.cor}"></span>
            <div>
              <div style="font-size:13.5px;font-weight:700">${item.horario} · ${item.petNome}</div>
              <div style="font-size:12px;color:#64748b">${item.vetNome}</div>
            </div>
          </div>
          <span style="color:#94a3b8;font-weight:700">›</span>
        </div>
      `).join('');

      return `
        <div class="page-content">
          <div class="toolbar-row">
            <div style="display:flex;align-items:center;gap:20px;flex-wrap:wrap">
              <div class="segmented-control">
                ${['Dia', 'Semana', 'Profissional'].map((m) => `
                  <button type="button" class="segment-btn ${state.viewModeAgenda === m ? 'active' : ''}" data-viewmode="${m}">${m}</button>
                `).join('')}
              </div>
              <div class="date-nav">
                <button type="button" class="date-nav-btn">‹</button>
                <span style="font-weight:600;font-size:13px;padding:0 4px">Hoje</span>
                <button type="button" class="date-nav-btn">›</button>
                <span style="margin-left:6px"><strong>Quarta-feira,</strong> <span style="color:#64748b">18 de junho</span></span>
              </div>
            </div>
            <button type="button" class="btn-primary" data-action="quick-schedule">+ Novo agendamento</button>
          </div>

          <div class="agenda-grid-layout">
            <div class="card schedule-table-card">
              <div class="schedule-header-row">
                <div></div>
                <div class="schedule-col-header">
                  <div class="vet-col-name">Dra. Camila</div>
                  <div class="vet-col-spec">Clínica geral</div>
                </div>
                <div class="schedule-col-header">
                  <div class="vet-col-name">Dr. Rafael</div>
                  <div class="vet-col-spec">Dermatologia</div>
                </div>
              </div>
              ${rowsHtml}
            </div>

            <div class="side-widgets-col">
              <div class="card widget-card">
                <div class="widget-header">
                  <span class="widget-title">Resumo do dia</span>
                  <span class="badge-teal">8 consultas</span>
                </div>
                <div class="summary-stats-grid">
                  <div class="summary-stat-box">
                    <div class="summary-stat-num">06</div>
                    <div class="summary-stat-label">Confirmadas</div>
                  </div>
                  <div class="summary-stat-box">
                    <div class="summary-stat-num">02</div>
                    <div class="summary-stat-label">A confirmar</div>
                  </div>
                </div>
              </div>

              <div class="card widget-card">
                <div class="widget-header">
                  <span class="widget-title">Próximos</span>
                </div>
                <div class="upcoming-list">
                  ${upcomingHtml}
                </div>
              </div>
            </div>
          </div>
        </div>
      `;
    }

    function renderAtendimento() {
      const pet = state.pets.find((p) => p.id === state.selectedPetId) || state.pets[0];
      const subTabs = ['Anamnese', 'Exame físico', 'Diagnóstico', 'Conduta'];

      let formContent = '';
      if (state.subTabAtendimento === 'Anamnese') {
        formContent = `
          <div class="form-section-top">
            <div>
              <h2 style="font-size:18px;font-weight:700">Anamnese</h2>
              <p style="font-size:13px;color:#64748b;margin-top:3px">Registre as informações relatadas pelo tutor</p>
            </div>
            <div class="saved-indicator">✓ Salvo às ${state.savedTime}</div>
          </div>
          <div class="form-group">
            <label class="form-label">Queixa principal</label>
            <input type="text" class="form-input" value="Falta de apetite e comportamento apático há dois dias." />
          </div>
          <div class="form-group">
            <label class="form-label">Histórico e evolução</label>
            <textarea class="form-textarea">Tutora relata que Mel reduziu a ingestão de alimento desde segunda-feira. Consumo de água normal. Sem episódios de vômito ou diarreia.</textarea>
          </div>
          <div class="form-row-2">
            <div class="form-group">
              <label class="form-label">Alimentação</label>
              <select class="form-select"><option>Reduzida</option><option>Normal</option><option>Aumentada</option></select>
            </div>
            <div class="form-group">
              <label class="form-label">Ingestão de água</label>
              <select class="form-select"><option>Normal</option><option>Reduzida</option><option>Aumentada</option></select>
            </div>
          </div>
        `;
      } else if (state.subTabAtendimento === 'Exame físico') {
        formContent = `
          <div class="form-section-top">
            <div>
              <h2 style="font-size:18px;font-weight:700">Exame físico</h2>
              <p style="font-size:13px;color:#64748b;margin-top:3px">Parâmetros vitais e avaliação geral</p>
            </div>
            <div class="saved-indicator">✓ Salvo às ${state.savedTime}</div>
          </div>
          <div class="form-row-3">
            <div class="form-group"><label class="form-label">Temperatura (°C)</label><input class="form-input" value="38,7" /></div>
            <div class="form-group"><label class="form-label">Freq. cardíaca (bpm)</label><input class="form-input" value="160" /></div>
            <div class="form-group"><label class="form-label">Peso (kg)</label><input class="form-input" value="4,2" /></div>
          </div>
          <div class="form-group">
            <label class="form-label">Achados do exame físico</label>
            <textarea class="form-textarea">Mucosas normocoradas, TPC < 2s, hidratação adequada. Leve sensibilidade à palpação abdominal cranial.</textarea>
          </div>
        `;
      } else if (state.subTabAtendimento === 'Diagnóstico') {
        formContent = `
          <div class="form-section-top">
            <div>
              <h2 style="font-size:18px;font-weight:700">Diagnóstico</h2>
              <p style="font-size:13px;color:#64748b;margin-top:3px">Hipóteses diagnósticas e exames complementares</p>
            </div>
            <div class="saved-indicator">✓ Salvo às ${state.savedTime}</div>
          </div>
          <div class="form-group">
            <label class="form-label">Suspeita clínica / Diagnóstico presuntivo</label>
            <input class="form-input" value="Gastrite aguda / corpo estranho linear a descartar" />
          </div>
          <div class="form-group">
            <label class="form-label">Exames solicitados</label>
            <textarea class="form-textarea">Hemograma completo, perfil bioquímico renal e hepático, ultrassonografia abdominal.</textarea>
          </div>
        `;
      } else {
        formContent = `
          <div class="form-section-top">
            <div>
              <h2 style="font-size:18px;font-weight:700">Conduta</h2>
              <p style="font-size:13px;color:#64748b;margin-top:3px">Plano terapêutico, prescrição e orientações</p>
            </div>
            <div class="saved-indicator">✓ Salvo às ${state.savedTime}</div>
          </div>
          <div class="form-group">
            <label class="form-label">Prescrição e tratamento</label>
            <textarea class="form-textarea">Citrato de maropitant 1mg/kg SC em dose única. Dieta pastosa morna fracionada em pequenas porções.</textarea>
          </div>
          <div class="form-group">
            <label class="form-label">Orientações ao tutor e retorno</label>
            <textarea class="form-textarea">Retorno em 48 horas com resultado dos exames ou imediatamente em caso de vômito.</textarea>
          </div>
        `;
      }

      return `
        <div class="page-content">
          <div class="card patient-banner-card">
            <div class="patient-banner-left">
              <div class="patient-avatar-box">🐾</div>
              <div>
                <div class="patient-name-row">
                  <span class="patient-name">${pet.nome}</span>
                  <span class="status-pill-active">EM ATENDIMENTO</span>
                </div>
                <div class="patient-meta-sub">${pet.especie} · ${pet.raca} · ${pet.idade} anos · ${pet.peso} kg</div>
              </div>
            </div>
            <div class="patient-banner-right">
              <div><div class="meta-label">TUTOR</div><div class="meta-value">${pet.tutorNome}</div></div>
              <div><div class="meta-label">CONTATO</div><div class="meta-value">${pet.tutorTelefone}</div></div>
            </div>
          </div>

          <div class="atendimento-grid">
            <div class="card">
              <div class="tabs-header">
                ${subTabs.map((t) => `
                  <button type="button" class="tab-link ${state.subTabAtendimento === t ? 'active' : ''}" data-subtab="${t}">${t}</button>
                `).join('')}
              </div>
              <div class="clinical-form-body">
                ${formContent}
                <div class="form-footer-actions">
                  <button type="button" class="btn-secondary" data-action="save-draft">Salvar rascunho</button>
                  <button type="button" class="btn-primary" data-action="next-step">Concluir etapa ›</button>
                </div>
              </div>
            </div>

            <div class="side-widgets-col">
              <div class="card widget-card">
                <div class="widget-header"><span class="widget-title">Ações rápidas</span></div>
                <div class="quick-actions-list">
                  <button type="button" class="quick-action-btn" data-subtab="Conduta">
                    <div class="quick-action-left">
                      <div class="qa-icon-box teal">💊</div>
                      <div><div style="font-size:13.5px;font-weight:700">Nova prescrição</div><div style="font-size:12px;color:#64748b">Medicamentos e orientações</div></div>
                    </div>
                    <span>›</span>
                  </button>
                  <button type="button" class="quick-action-btn" data-action="add-vaccine">
                    <div class="quick-action-left">
                      <div class="qa-icon-box amber">💉</div>
                      <div><div style="font-size:13.5px;font-weight:700">Registrar vacina</div><div style="font-size:12px;color:#64748b">Atualizar carteira vacinal</div></div>
                    </div>
                    <span>›</span>
                  </button>
                  <button type="button" class="quick-action-btn" data-subtab="Diagnóstico">
                    <div class="quick-action-left">
                      <div class="qa-icon-box purple">📄</div>
                      <div><div style="font-size:13.5px;font-weight:700">Solicitar exame</div><div style="font-size:12px;color:#64748b">Laboratorial ou imagem</div></div>
                    </div>
                    <span>›</span>
                  </button>
                </div>
              </div>

              <div class="card widget-card">
                <div class="widget-header">
                  <span class="widget-title">Histórico clínico</span>
                  <span style="color:#007a66;font-size:12.5px;font-weight:600">Ver tudo</span>
                </div>
                <div class="timeline-list">
                  ${state.historico.map((h) => `
                    <div class="timeline-item">
                      <span class="timeline-dot ${h.active ? 'active' : ''}"></span>
                      <div class="timeline-date">${h.data}</div>
                      <div class="timeline-title">${h.titulo}</div>
                      <div class="timeline-sub">${h.vet}</div>
                    </div>
                  `).join('')}
                </div>
              </div>
            </div>
          </div>
        </div>
      `;
    }

    function renderPacientes() {
      return `
        <div class="page-content">
          <div class="info-banner-teal">
            <strong>Vínculo obrigatório:</strong> todo paciente deve estar associado a um tutor responsável.
          </div>

          <form id="form-novo-paciente">
            <div class="paciente-grid">
              <div class="card" style="padding:24px">
                <div class="step-card-header">
                  <div class="step-badge">1</div>
                  <div>
                    <h2 style="font-size:16px;font-weight:700">Dados do animal</h2>
                    <p style="font-size:12.5px;color:#64748b;margin-top:2px">Informações principais do novo paciente</p>
                  </div>
                </div>

                <div class="form-row-2">
                  <div class="form-group">
                    <label class="form-label">Nome do animal *</label>
                    <input id="p-nome" type="text" class="form-input" placeholder="Ex.: Nina" required />
                  </div>
                  <div class="form-group">
                    <label class="form-label">Espécie *</label>
                    <select id="p-especie" class="form-select" required>
                      <option value="">Selecione</option>
                      <option value="Canino">Canino</option>
                      <option value="Felino">Felino</option>
                      <option value="Ave">Ave</option>
                    </select>
                  </div>
                </div>

                <div class="form-row-2">
                  <div class="form-group">
                    <label class="form-label">Raça</label>
                    <input id="p-raca" type="text" class="form-input" placeholder="Ex.: Golden Retriever" />
                  </div>
                  <div class="form-group">
                    <label class="form-label">Sexo</label>
                    <select id="p-sexo" class="form-select">
                      <option value="">Selecione</option>
                      <option value="Fêmea">Fêmea</option>
                      <option value="Macho">Macho</option>
                    </select>
                  </div>
                </div>

                <div class="form-row-2">
                  <div class="form-group">
                    <label class="form-label">Data de nascimento</label>
                    <input type="text" class="form-input" placeholder="dd/mm/aaaa" />
                  </div>
                  <div class="form-group">
                    <label class="form-label">Peso atual</label>
                    <input id="p-peso" type="text" class="form-input" placeholder="0,0 kg" />
                  </div>
                </div>

                <div class="form-row-2">
                  <div class="form-group">
                    <label class="form-label">Porte</label>
                    <select class="form-select">
                      <option value="">Selecione</option>
                      <option value="Pequeno">Pequeno</option>
                      <option value="Médio">Médio</option>
                      <option value="Grande">Grande</option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label class="form-label">Cor / pelagem</label>
                    <input type="text" class="form-input" placeholder="Ex.: Caramelo" />
                  </div>
                </div>

                <div class="form-group" style="margin-bottom:0">
                  <label class="form-label">Observações</label>
                  <textarea class="form-textarea" placeholder="Alergias, condições preexistentes ou informações relevantes..."></textarea>
                </div>
              </div>

              <div class="card" style="padding:24px">
                <div class="step-card-header">
                  <div class="step-badge">2</div>
                  <div>
                    <h2 style="font-size:16px;font-weight:700">Tutor responsável</h2>
                    <p style="font-size:12.5px;color:#64748b;margin-top:2px">Busque ou cadastre um tutor</p>
                  </div>
                </div>

                <input type="text" class="form-input" placeholder="🔍 CPF, nome ou telefone" />
                <div class="divider-with-text">OU NOVO TUTOR</div>

                <div class="form-group">
                  <label class="form-label">Nome completo *</label>
                  <input id="t-nome" type="text" class="form-input" placeholder="Nome do responsável" required />
                </div>
                <div class="form-group">
                  <label class="form-label">CPF *</label>
                  <input id="t-cpf" type="text" class="form-input" placeholder="000.000.000-00" required />
                </div>
                <div class="form-group">
                  <label class="form-label">Telefone *</label>
                  <input id="t-tel" type="text" class="form-input" placeholder="(00) 00000-0000" required />
                </div>
                <div class="form-group" style="margin-bottom:0">
                  <label class="form-label">E-mail</label>
                  <input type="email" class="form-input" placeholder="email@exemplo.com" />
                </div>
              </div>
            </div>

            <div class="card bottom-bar-card">
              <span style="font-size:13px;color:#64748b">Campos marcados com * são obrigatórios</span>
              <div style="display:flex;gap:12px">
                <button type="reset" class="btn-secondary">Cancelar</button>
                <button type="submit" class="btn-primary">Salvar cadastro</button>
              </div>
            </div>
          </form>
        </div>
      `;
    }

    function renderFinanceiro() {
      const bars = [
        { label: '12 Qui', h: 92 },
        { label: '13 Sex', h: 126 },
        { label: '14 Sáb', h: 100 },
        { label: '15 Dom', h: 158 },
        { label: '16 Seg', h: 136 },
        { label: '17 Ter', h: 186 },
        { label: '18 Qua', h: 166 }
      ];

      return `
        <div class="page-content">
          <div class="toolbar-row">
            <div style="display:flex;align-items:center;gap:14px">
              <select class="form-select" style="width:170px;font-weight:600;padding:8px 14px">
                <option>Últimos 7 dias</option>
                <option>Este mês</option>
              </select>
              <span style="font-size:13.5px;color:#64748b">12–18 de junho de 2025</span>
            </div>
            <button type="button" class="btn-primary" data-action="add-payment">+ Registrar pagamento</button>
          </div>

          <div class="kpi-grid">
            <div class="card kpi-card">
              <div class="kpi-top"><div class="kpi-icon-box">💳</div><span class="trend-badge up">↗ 12,5%</span></div>
              <div class="kpi-value">R$ 18.450</div>
              <div class="kpi-label">Receita no período</div>
            </div>
            <div class="card kpi-card">
              <div class="kpi-top"><div class="kpi-icon-box">📋</div><span class="trend-badge up">↗ 8,2%</span></div>
              <div class="kpi-value">84</div>
              <div class="kpi-label">Atendimentos</div>
            </div>
            <div class="card kpi-card">
              <div class="kpi-top"><div class="kpi-icon-box">📈</div><span class="trend-badge up">↗ 4,1%</span></div>
              <div class="kpi-value">R$ 219,64</div>
              <div class="kpi-label">Ticket médio</div>
            </div>
            <div class="card kpi-card">
              <div class="kpi-top"><div class="kpi-icon-box">🕒</div><span class="trend-badge down">↘ 3,6%</span></div>
              <div class="kpi-value">R$ 2.380</div>
              <div class="kpi-label">Pagamentos pendentes</div>
            </div>
          </div>

          <div class="charts-grid">
            <div class="card" style="padding:22px 24px">
              <div style="display:flex;justify-content:space-between;margin-bottom:12px">
                <div>
                  <h2 style="font-size:16px;font-weight:700">Receita recebida</h2>
                  <p style="font-size:12.5px;color:#64748b;margin-top:2px">Valores confirmados por dia</p>
                </div>
                <span style="color:#94a3b8">•••</span>
              </div>
              <div class="bar-chart-area">
                ${bars.map((b) => `<div class="bar-col"><div class="bar-fill" style="height:${b.h}px"></div></div>`).join('')}
              </div>
              <div style="display:flex;justify-content:space-around;padding:6px 12px 0 12px">
                ${bars.map((b) => `<div class="bar-day-label" style="flex:1;text-align:center">${b.label}</div>`).join('')}
              </div>
            </div>

            <div class="card" style="padding:22px 24px">
              <div style="display:flex;justify-content:space-between;margin-bottom:12px">
                <div>
                  <h2 style="font-size:16px;font-weight:700">Atendimentos</h2>
                  <p style="font-size:12.5px;color:#64748b;margin-top:2px">Por categoria</p>
                </div>
                <span style="color:#007a66;font-size:12.5px;font-weight:600">Detalhes</span>
              </div>
              <div class="donut-container">
                <div style="position:relative;width:148px;height:148px">
                  <svg viewBox="0 0 36 36" style="width:100%;height:100%">
                    <circle cx="18" cy="18" r="14" fill="transparent" stroke="#008f7a" stroke-width="4.8" stroke-dasharray="51 37" stroke-dashoffset="25"></circle>
                    <circle cx="18" cy="18" r="14" fill="transparent" stroke="#00a8e8" stroke-width="4.8" stroke-dasharray="20 68" stroke-dashoffset="-26"></circle>
                    <circle cx="18" cy="18" r="14" fill="transparent" stroke="#f59e0b" stroke-width="4.8" stroke-dasharray="17 71" stroke-dashoffset="-46"></circle>
                  </svg>
                  <div style="position:absolute;inset:0;display:flex;flex-direction:column;align-items:center;justify-content:center">
                    <span style="font-size:20px;font-weight:700">84</span>
                    <span style="font-size:11.5px;color:#64748b">total</span>
                  </div>
                </div>
                <div class="legend-list">
                  <div class="legend-row"><div style="display:flex;align-items:center;gap:8px"><span class="dot teal" style="background:#008f7a"></span><span>Consultas</span></div><strong>49</strong></div>
                  <div class="legend-row"><div style="display:flex;align-items:center;gap:8px"><span class="dot blue" style="background:#00a8e8"></span><span>Vacinas</span></div><strong>19</strong></div>
                  <div class="legend-row"><div style="display:flex;align-items:center;gap:8px"><span class="dot amber" style="background:#f59e0b"></span><span>Procedimentos</span></div><strong>16</strong></div>
                </div>
              </div>
            </div>
          </div>

          <div class="card table-card">
            <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:14px">
              <h2 style="font-size:16px;font-weight:700">Pagamentos recentes</h2>
              <span style="color:#007a66;font-size:12.5px;font-weight:600">Ver todos</span>
            </div>
            <table class="data-table">
              <thead>
                <tr>
                  <th>PACIENTE / TUTOR</th>
                  <th>SERVIÇO</th>
                  <th>FORMA</th>
                  <th>STATUS</th>
                  <th style="text-align:right">VALOR</th>
                </tr>
              </thead>
              <tbody>
                ${state.pagamentos.map((p) => `
                  <tr>
                    <td><strong>${p.paciente}</strong> <span style="color:#64748b">· ${p.tutor}</span></td>
                    <td>${p.servico}</td>
                    <td>${p.forma}</td>
                    <td><span class="${p.status === 'Pago' ? 'status-badge-paid' : 'status-badge-pending'}">${p.status}</span></td>
                    <td style="text-align:right;font-weight:700">${p.valor}</td>
                  </tr>
                `).join('')}
              </tbody>
            </table>
          </div>
        </div>
      `;
    }

    function render() {
      const headers = {
        agenda: { title: 'Agenda', sub: 'Organize os atendimentos da clínica' },
        atendimento: { title: 'Atendimento', sub: 'Prontuário clínico em andamento' },
        pacientes: { title: 'Novo paciente', sub: 'Cadastre o animal e vincule seu tutor' },
        financeiro: { title: 'Financeiro', sub: 'Acompanhe a saúde financeira da clínica' }
      };

      const h = headers[state.activeTab];
      let pageBody = '';
      if (state.activeTab === 'agenda') pageBody = renderAgenda();
      else if (state.activeTab === 'atendimento') pageBody = renderAtendimento();
      else if (state.activeTab === 'pacientes') pageBody = renderPacientes();
      else pageBody = renderFinanceiro();

      root.innerHTML = `
        <div class="app-shell">
          <aside class="sidebar">
            <div>
              <div class="brand-header">
                <div class="brand-logo">🐾</div>
                <div>
                  <div class="brand-title">VetCare</div>
                  <div class="brand-subtitle">CLÍNICA VETERINÁRIA</div>
                </div>
              </div>
              <div class="menu-section-label">MENU PRINCIPAL</div>
              <nav class="nav-list">
                <button type="button" class="nav-btn ${state.activeTab === 'agenda' ? 'active' : ''}" data-tab="agenda">📅 <span>Agenda</span></button>
                <button type="button" class="nav-btn ${state.activeTab === 'atendimento' ? 'active' : ''}" data-tab="atendimento">📋 <span>Atendimento</span></button>
                <button type="button" class="nav-btn ${state.activeTab === 'pacientes' ? 'active' : ''}" data-tab="pacientes">🐾 <span>Pacientes</span></button>
                <button type="button" class="nav-btn ${state.activeTab === 'financeiro' ? 'active' : ''}" data-tab="financeiro">💳 <span>Financeiro</span></button>
              </nav>
            </div>
            <div class="user-card">
              <div class="user-card-top">
                <div class="user-avatar">AS</div>
                <div>
                  <div class="user-name">Ana Souza</div>
                  <div class="user-role">Administradora</div>
                </div>
              </div>
              <button type="button" class="user-settings-btn">Configurações da conta</button>
            </div>
          </aside>

          <div class="main-area">
            <header class="top-header">
              <div>
                <h1 class="header-title">${h.title}</h1>
                <p class="header-subtitle">${h.sub}</p>
              </div>
              <div class="header-actions">
                <div class="search-box">
                  <input type="text" class="search-input" style="padding-left:14px" placeholder="🔍 Buscar paciente ou tutor" value="${state.searchQuery}" />
                </div>
                <button type="button" class="notif-btn">🔔<span class="notif-dot"></span></button>
              </div>
            </header>
            ${pageBody}
          </div>
          ${state.toast ? `<div class="toast-banner">${state.toast}</div>` : ''}
        </div>
      `;

      root.querySelectorAll('[data-tab]').forEach((btn) => {
        btn.addEventListener('click', () => {
          state.activeTab = btn.getAttribute('data-tab');
          render();
        });
      });

      root.querySelectorAll('[data-subtab]').forEach((btn) => {
        btn.addEventListener('click', () => {
          state.subTabAtendimento = btn.getAttribute('data-subtab');
          render();
        });
      });

      root.querySelectorAll('[data-viewmode]').forEach((btn) => {
        btn.addEventListener('click', () => {
          state.viewModeAgenda = btn.getAttribute('data-viewmode');
          render();
        });
      });

      root.querySelectorAll('[data-open-pet]').forEach((el) => {
        el.addEventListener('click', () => {
          state.selectedPetId = Number(el.getAttribute('data-open-pet'));
          state.activeTab = 'atendimento';
          render();
        });
      });

      root.querySelectorAll('[data-action="quick-schedule"]').forEach((btn) => {
        btn.addEventListener('click', () => {
          state.agendamentos.push({
            id: Date.now(),
            horario: '13:00',
            petId: 1,
            petNome: 'Mel',
            vetId: 1,
            vetNome: 'Dra. Camila',
            tipo: 'Retorno clínico',
            cor: 'teal'
          });
          showToast('Agendamento confirmado para 13:00!');
        });
      });

      root.querySelectorAll('[data-action="save-draft"]').forEach((btn) => {
        btn.addEventListener('click', () => {
          showToast('Rascunho do prontuário salvo com sucesso!');
        });
      });

      root.querySelectorAll('[data-action="next-step"]').forEach((btn) => {
        btn.addEventListener('click', () => {
          const order = ['Anamnese', 'Exame físico', 'Diagnóstico', 'Conduta'];
          const idx = order.indexOf(state.subTabAtendimento);
          if (idx < order.length - 1) {
            state.subTabAtendimento = order[idx + 1];
            render();
          } else {
            showToast('Atendimento clínico finalizado (versão 1.1 registrada)!');
          }
        });
      });

      root.querySelectorAll('[data-action="add-vaccine"]').forEach((btn) => {
        btn.addEventListener('click', () => {
          state.historico.unshift({
            data: '18 JUN 2025',
            titulo: 'Vacina Polivalente V4',
            vet: 'Dra. Camila · versão 1.0',
            active: true
          });
          showToast('Vacina registrada na carteira vacinal!');
        });
      });

      root.querySelectorAll('[data-action="add-payment"]').forEach((btn) => {
        btn.addEventListener('click', () => {
          state.pagamentos.unshift({
            id: Date.now(),
            paciente: 'Simba',
            tutor: 'Lucas Mendes',
            servico: 'Exame dermatológico',
            forma: 'PIX',
            status: 'Pago',
            valor: 'R$ 220,00'
          });
          showToast('Pagamento registrado com sucesso!');
        });
      });

      const formPaciente = root.querySelector('#form-novo-paciente');
      if (formPaciente) {
        formPaciente.addEventListener('submit', (e) => {
          e.preventDefault();
          const nome = root.querySelector('#p-nome').value;
          const especie = root.querySelector('#p-especie').value;
          const raca = root.querySelector('#p-raca').value || 'SRD';
          const tutorNome = root.querySelector('#t-nome').value;
          const tutorTel = root.querySelector('#t-tel').value;
          state.pets.push({
            id: state.pets.length + 1,
            nome,
            especie,
            raca,
            idade: 1,
            peso: '5,0',
            tutorNome,
            tutorTelefone: tutorTel
          });
          showToast(`Paciente "${nome}" cadastrado e vinculado a ${tutorNome}!`);
          formPaciente.reset();
        });
      }
    }

    render();
  }

  window.addEventListener('DOMContentLoaded', function () {
    setTimeout(mountStandaloneApp, 250);
  });
})();
