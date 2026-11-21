import React from 'react';
import { Calendar, ClipboardList, LogOut, PawPrint, Wallet } from 'lucide-react';
import { PerfilUsuario, ScreenTab } from '../types/vetcare';
import { UsuarioLogado } from '../services/api';

interface SidebarProps {
  activeTab: ScreenTab;
  onSelectTab: (tab: ScreenTab) => void;
  perfil: PerfilUsuario;
  usuario: UsuarioLogado;
  onLogout: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({
  activeTab,
  onSelectTab,
  perfil,
  usuario,
  onLogout,
}) => {
  const navItems: { id: ScreenTab; label: string; icon: React.ReactNode; perfis: PerfilUsuario[] }[] = [
    {
      id: 'agenda',
      label: 'Agenda',
      icon: <Calendar size={18} />,
      perfis: ['ADMIN', 'RECEPCIONISTA', 'VETERINARIO'],
    },
    {
      id: 'atendimento',
      label: 'Atendimento',
      icon: <ClipboardList size={18} />,
      perfis: ['ADMIN', 'VETERINARIO'],
    },
    {
      id: 'pacientes',
      label: 'Pacientes',
      icon: <PawPrint size={18} />,
      perfis: ['ADMIN', 'RECEPCIONISTA', 'VETERINARIO'],
    },
    {
      id: 'financeiro',
      label: 'Financeiro',
      icon: <Wallet size={18} />,
      perfis: ['ADMIN', 'RECEPCIONISTA'],
    },
  ];

  const cargoLabel: Record<string, string> = {
    ADMIN: 'Administração',
    RECEPCIONISTA: 'Recepção',
    VETERINARIO: 'Veterinário(a)',
  };
  const initials = usuario.nome
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((parte) => parte[0].toUpperCase())
    .join('');

  return (
    <aside className="sidebar">
      <div>
        <div className="brand-header">
          <div className="brand-logo">
            <PawPrint size={22} strokeWidth={2.2} />
          </div>
          <div>
            <div className="brand-title">VetCare</div>
            <div className="brand-subtitle">CLÍNICA VETERINÁRIA</div>
          </div>
        </div>

        <div className="menu-section-label">MENU PRINCIPAL</div>
        <nav className="nav-list">
          {navItems
            .filter((item) => item.perfis.includes(perfil))
            .map((item) => (
              <button
                key={item.id}
                type="button"
                className={`nav-btn ${activeTab === item.id ? 'active' : ''}`}
                onClick={() => onSelectTab(item.id)}
              >
                {item.icon}
                <span>{item.label}</span>
              </button>
            ))}
        </nav>
      </div>

      <div className="user-card">
        <div className="user-card-top">
          <div className="user-avatar">{initials}</div>
          <div>
            <div className="user-name">{usuario.nome}</div>
            <div className="user-role">{cargoLabel[usuario.cargo] ?? usuario.cargo}</div>
          </div>
        </div>
        <button type="button" className="user-settings-btn" onClick={onLogout}>
          <LogOut size={14} style={{ marginRight: '6px', verticalAlign: 'middle' }} />
          Sair
        </button>
      </div>
    </aside>
  );
};
