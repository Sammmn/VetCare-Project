import React, { useState } from 'react';
import { PawPrint } from 'lucide-react';
import { login, Sessao } from '../services/api';

interface LoginProps {
  onLogin: (sessao: Sessao) => void;
}

export const Login: React.FC<LoginProps> = ({ onLogin }) => {
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [erro, setErro] = useState('');
  const [carregando, setCarregando] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErro('');
    setCarregando(true);
    try {
      onLogin(await login(email, senha));
    } catch (err: any) {
      setErro(err?.message || 'Não foi possível entrar.');
    } finally {
      setCarregando(false);
    }
  };

  return (
    <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: '#f1f5f9', padding: '24px' }}>
      <form className="card" onSubmit={handleSubmit} style={{ width: '100%', maxWidth: '380px', padding: '32px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '24px' }}>
          <div className="brand-logo"><PawPrint size={22} strokeWidth={2.2} /></div>
          <div>
            <div className="brand-title" style={{ color: '#0f172a' }}>VetCare</div>
            <div className="brand-subtitle" style={{ color: '#64748b' }}>ACESSO DA EQUIPE</div>
          </div>
        </div>

        <div className="form-group">
          <label className="form-label">E-mail</label>
          <input type="email" className="form-input" value={email} onChange={(e) => setEmail(e.target.value)} autoComplete="username" required autoFocus />
        </div>
        <div className="form-group">
          <label className="form-label">Senha</label>
          <input type="password" className="form-input" value={senha} onChange={(e) => setSenha(e.target.value)} autoComplete="current-password" required />
        </div>

        {erro && <p role="alert" style={{ color: '#dc2626', fontSize: '13px', marginBottom: '12px' }}>{erro}</p>}

        <button type="submit" className="btn-primary" style={{ width: '100%' }} disabled={carregando}>
          {carregando ? 'Entrando...' : 'Entrar'}
        </button>
      </form>
    </div>
  );
};
