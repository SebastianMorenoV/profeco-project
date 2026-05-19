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
    <div style={styles.container}>
      <div style={styles.card}>
        <div style={styles.header}>
          <h1 style={styles.logo}>🛒 ProFeCo</h1>
          <p style={styles.subtitle}>Acceso a Portal de Comercios</p>
        </div>

        <form onSubmit={handleSubmit}>
          <div style={styles.formGroup}>
            <label style={styles.label}>Correo electrónico</label>
            <input 
              type="email" 
              style={styles.input} 
              placeholder="comercio@ejemplo.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              autoComplete="email"
              required
            />
          </div>

          <div style={styles.formGroup}>
            <label style={styles.label}>Contraseña</label>
            <input 
              type="password" 
              style={styles.input} 
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
              minLength={6}
              required
            />
          </div>

          {error && <p style={styles.error}>{error}</p>}

          <button type="submit" disabled={cargando} style={styles.button}>
            {cargando ? 'Verificando...' : 'Iniciar sesión'}
          </button>
        </form>
      </div>
    </div>
  );
}

const styles = {
  container: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    minHeight: '100vh',
    backgroundColor: '#f9fafb',
    fontFamily: 'system-ui',
  },
  card: {
    backgroundColor: '#ffffff',
    padding: '2.5rem',
    borderRadius: '0.75rem',
    boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
    width: '100%',
    maxWidth: '400px',
    border: '1px solid #e5e7eb',
  },
  header: { textAlign: 'center', marginBottom: '2rem' },
  logo: { fontSize: '1.875rem', fontWeight: 'bold', color: '#111827', margin: '0 0 0.5rem 0' },
  subtitle: { color: '#6b7280', fontSize: '0.875rem', margin: 0 },
  formGroup: { marginBottom: '1.5rem' },
  label: { display: 'block', fontSize: '0.875rem', fontWeight: '500', color: '#374151', marginBottom: '0.5rem' },
  input: { width: '100%', padding: '0.75rem', borderRadius: '0.375rem', border: '1px solid #d1d5db', outline: 'none', boxSizing: 'border-box' },
  error: { color: '#ef4444', fontSize: '0.875rem', marginBottom: '1rem', textAlign: 'center' },
  button: { width: '100%', padding: '0.75rem', backgroundColor: '#111827', color: '#fff', border: 'none', borderRadius: '0.375rem', fontWeight: '600', cursor: 'pointer' }
};