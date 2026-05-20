import { useState } from 'react';
import client, { tokenStore } from '../api/client';

export default function LoginPage({ onLogin }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [cargando, setCargando] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setCargando(true);

    try {
      if (!email.trim() || !password) {
        setError('Captura email y contraseña.');
        setCargando(false);
        return;
      }

      // Login via ms-auth (JWT)
      const res = await client.post('/api/auth/login', { email: email.trim(), password });

      if (res.data && res.data.exito) {
        // Guardar JWT
        if (res.data.token) {
          tokenStore.save(res.data.token);
        }

        // Verificar que sea tipo COMERCIANTE
        const tipo = res.data.usuario?.tipoUsuario ?? res.data.usuario?.tipo_usuario;
        if (tipo !== 'COMERCIANTE') {
          setError('Acceso denegado. Solo comerciantes pueden acceder a este panel.');
          tokenStore.clear();
          setCargando(false);
          return;
        }

        onLogin(res.data.usuario);
      } else {
        setError(res.data?.mensaje || 'Credenciales inválidas.');
      }
    } catch (err) {
      const msg = err.response?.data?.mensaje || err.response?.data?.message || 'Error al conectar con el servidor.';
      setError(msg);
    } finally {
      setCargando(false);
    }
  };

  return (
    <div className="auth-shell">
      <div className="auth-card">
        <div className="auth-header" style={{flexDirection: 'column', textAlign: 'center'}}>
          <h1 style={{fontSize: '1.875rem', color: 'var(--c-primary-dark)'}}>🛒 ProFeCo</h1>
          <p className="muted">Acceso a Portal de Comercios</p>
        </div>

        <form className="resenia-form" onSubmit={handleSubmit}>
          <label>
            Correo electrónico
            <input 
              type="email" 
              placeholder="comercio@ejemplo.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              autoComplete="email"
              required
            />
          </label>

          <label>
            Contraseña
            <input 
              type="password" 
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
              minLength={6}
              required
            />
          </label>

          {error && <div className="error-box"><p>{error}</p></div>}

          <button type="submit" disabled={cargando}>
            {cargando ? 'Verificando...' : 'Iniciar sesión'}
          </button>
        </form>
      </div>
    </div>
  );
}
