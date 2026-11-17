import React from 'react';
import { Bell, Search } from 'lucide-react';

interface HeaderProps {
  title: string;
  subtitle: string;
  searchQuery: string;
  onSearchChange: (val: string) => void;
}

export const Header: React.FC<HeaderProps> = ({
  title,
  subtitle,
  searchQuery,
  onSearchChange,
}) => {
  return (
    <header className="top-header">
      <div>
        <h1 className="header-title">{title}</h1>
        <p className="header-subtitle">{subtitle}</p>
      </div>

      <div className="header-actions">
        <div className="search-box">
          <Search size={16} className="search-box-icon" />
          <input
            type="text"
            className="search-input"
            placeholder="Buscar paciente ou tutor"
            value={searchQuery}
            onChange={(e) => onSearchChange(e.target.value)}
          />
        </div>
        <button type="button" className="notif-btn" aria-label="Notificações">
          <Bell size={18} />
          <span className="notif-dot" />
        </button>
      </div>
    </header>
  );
};
