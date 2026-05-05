import { useState } from 'react';
import { Link } from 'react-router-dom';
import { authRepository } from '../auth/authRepository';
import { useUser } from '../context/UserContext';
import { ErrorBox } from '../components/Loader';

export function RegistroPage() {
  const { iniciarSesion } = useUser();
  const [datos, setDatos] = useState({
    nombre: '',
    apellido: '',
    email: '',
    telefono: '',
    password: '',
    confirmar: ''
  });
  const [error, setError] = useState('');
  const [enviando, setEnviando] = useState(false);

  const cambiar = (campo) => (e) => {
    setError('');
    let valor = e.target.value;
    if (campo === 'telefono') valor = valor.replace(/\D/g, '');
    setDatos({ ...datos, [campo]: valor });
  };

  const submit = async (e) => {
    e.preventDefault();
    setError('');
    setEnviando(true);
    const r = await authRepository.registrar(datos);
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
            <h1>Crear cuenta</h1>
            <p className="muted">Regístrate para guardar favoritos, wishlist, lista del super y enviar reportes.</p>
          </div>
        </div>

        <form onSubmit={submit} className="resenia-form">
          <label>
            Nombre
            <input
              type="text"
              autoComplete="given-name"
              value={datos.nombre}
              onChange={cambiar('nombre')}
              required
            />
          </label>
          <label>
            Apellido
            <input
              type="text"
              autoComplete="family-name"
              value={datos.apellido}
              onChange={cambiar('apellido')}
            />
          </label>
          <label>
            Correo electrónico
            <input
              type="email"
              autoComplete="email"
              value={datos.email}
              onChange={cambiar('email')}
              placeholder="ej. juan.perez@mail.com"
              required
            />
          </label>
          <label>
            Teléfono (opcional)
            <input
              type="tel"
              autoComplete="tel"
              value={datos.telefono}
              onChange={cambiar('telefono')}
              maxLength={20}
            />
          </label>
          <label>
            Contraseña
            <input
              type="password"
              autoComplete="new-password"
              value={datos.password}
              onChange={cambiar('password')}
              placeholder="mínimo 6 caracteres"
              required
            />
          </label>
          <label>
            Confirmar contraseña
            <input
              type="password"
              autoComplete="new-password"
              value={datos.confirmar}
              onChange={cambiar('confirmar')}
              required
            />
          </label>

          {error && <ErrorBox message={error} />}

          <button type="submit" disabled={enviando}>
            {enviando ? 'Creando cuenta…' : 'Crear cuenta'}
          </button>
        </form>

        <div className="auth-footer">
          <Link to="/login" className="link-more">Ya tengo cuenta, iniciar sesión</Link>
        </div>
      </div>
    </div>
  );
}
