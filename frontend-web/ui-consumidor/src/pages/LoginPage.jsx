import { useState } from 'react';
import { Link } from 'react-router-dom';
import { authRepository } from '../auth/authRepository';
import { useUser } from '../context/UserContext';
import { ErrorBox } from '../components/Loader';

export function LoginPage() {
  const { iniciarSesion } = useUser();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [enviando, setEnviando] = useState(false);

  const submit = async (e) => {
    e.preventDefault();
    setError('');
    if (!email.trim() || !password) {
      setError('Captura correo y contraseña.');
      return;
    }
    setEnviando(true);
    const r = await authRepository.login(email, password);
    setEnviando(false);
    if (r.ok) {
      iniciarSesion(r.sesion);
    } else {
      setError(r.mensaje);
    }
  };

  return (
    <div className="auth-shell">
      <div className="auth-card">
        <div className="auth-header">
          <span className="brand-mark">P</span>
          <div>
            <h1>PROFECO Consumidor</h1>
            <p className="muted">Entra con tu correo y contraseña.</p>
          </div>
        </div>

        <form onSubmit={submit} className="resenia-form">
          <label>
            Correo electrónico
            <input
              type="email"
              autoComplete="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="ej. juan.perez@mail.com"
              required
            />
          </label>
          <label>
            Contraseña
            <input
              type="password"
              autoComplete="current-password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </label>

          {error && <ErrorBox message={error} />}

          <button type="submit" disabled={enviando}>
            {enviando ? 'Validando…' : 'Iniciar sesión'}
          </button>
        </form>

        <div className="auth-footer">
          <Link to="/registro" className="link-more">¿No tienes cuenta? Regístrate aquí</Link>
          <p className="muted small">
            Cuenta demo: <strong>juan.perez@mail.com</strong> / <strong>12345678</strong>
          </p>
        </div>
      </div>
    </div>
  );
}
