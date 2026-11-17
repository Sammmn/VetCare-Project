import React from 'react';
import { Calendar, ClipboardList, PawPrint, Wallet } from 'lucide-react';
import { PerfilUsuario, ScreenTab } from '../types/vetcare';

interface SidebarProps {
  activeTab: ScreenTab;
  onSelectTab: (tab: ScreenTab) => void;
  perfil: PerfilUsuario;
  onChangePerfil: (perfil: PerfilUsuario) => void;
}

export const Sidebar: React.FC<SidebarProps> = ({
  activeTab,
  onSelectTab,
  perfil,
  onChangePerfil,
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

  const perfilInfo: Record<PerfilUsuario, { initials: string; nome: string; cargo: string }> = {
    ADMIN: { initials: 'AS', nome: 'Ana Souza', cargo: 'Administradora' },
    RECEPCIONISTA: { initials: 'MC', nome: 'Mariana Costa', cargo: 'Recepcionista' },
    VETERINARIO: { initials: 'CA', nome: 'Dra. Camila', cargo: 'Veterinária' },
  };

  const currentUser = perfilInfo[perfil];

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
          <div className="user-avatar">{currentUser.initials}</div>
          <div>
            <div className="user-name">{currentUser.nome}</div>
            <div className="user-role">{currentUser.cargo}</div>
          </div>
        </div>
        <button
          type="button"
          className="user-settings-btn"
          title="Alternar perfil RBAC (Admin / Recepcionista / Veterinário)"
          onClick={() => {
            const next: Record<PerfilUsuario, PerfilUsuario> = {
              ADMIN: 'VETERINARIO',
              VETERINARIO: 'RECEPCIONISTA',
              RECEPCIONISTA: 'ADMIN',
            };
            const novoPerfil = next[perfil];
            onChangePerfil(novoPerfil);
          }}
        >
          Configurações da conta
        </button>
      </div>
    </aside>
  );
};
