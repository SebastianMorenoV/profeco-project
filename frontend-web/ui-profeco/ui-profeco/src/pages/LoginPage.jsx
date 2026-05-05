import { useState } from 'react';
import { usuariosApi } from '../api';

export function LoginPage({ onLogin }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [procesando, setProcesando] = useState(false);
  const [errMsg, setErrMsg] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setProcesando(true);
    setErrMsg(null);

    try {
      const res = await usuariosApi.login(email.trim(), password);

      if (!res.exito) {
        setErrMsg(res.mensaje || 'Credenciales inválidas.');
        setProcesando(false);
        return;
      }

      // Solo permitir acceso a usuarios tipo PROFECO
      const tipo = res.usuario?.tipoUsuario ?? res.usuario?.tipo_usuario;
      if (tipo !== 'PROFECO') {
        setErrMsg('Acceso denegado. Solo agentes PROFECO pueden acceder a este panel.');
        setProcesando(false);
        return;
      }

      onLogin(res.usuario);
    } catch (err) {
      setErrMsg(err.message || 'Error al conectar con el servidor.');
    } finally {
      setProcesando(false);
    }
  };

  return (
    <div className="login-wrapper">
      <div className="login-card">
        <div className="login-header">
          <div className="brand-mark" style={{ width: 56, height: 56, fontSize: '1.5rem', margin: '0 auto 1rem' }}>
            P
          </div>
          <h1 style={{ fontSize: '1.5rem', marginBottom: '0.25rem' }}>PROFECO</h1>
          <p className="muted" style={{ margin: 0 }}>Panel Administrativo — Inicio de Sesión</p>
        </div>

        <form className="resenia-form" onSubmit={handleSubmit}>
          <label>
            Correo electrónico:
            <input
              required
              type="email"
              autoComplete="email"
              placeholder="agente@profeco.gob.mx"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </label>

          <label>
            Contraseña:
            <input
              required
              type="password"
              autoComplete="current-password"
              placeholder="••••••••"
              minLength={6}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </label>

          {errMsg && <div className="error-box">{errMsg}</div>}

          <button type="submit" disabled={procesando} style={{ width: '100%' }}>
            {procesando ? 'Verificando...' : 'Iniciar sesión'}
          </button>
        </form>

        <p className="muted" style={{ textAlign: 'center', fontSize: '0.78rem', marginTop: '1rem' }}>
          Plataforma Administrativa — Procuraduría Federal del Consumidor
        </p>
      </div>
    </div>
  );
}
