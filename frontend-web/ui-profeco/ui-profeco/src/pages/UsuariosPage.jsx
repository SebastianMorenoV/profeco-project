import { useState } from 'react';
import { usuariosApi } from '../api';
import { useFetch } from '../hooks/useFetch';
import { Loader, ErrorBox, EmptyState } from '../components/Loader';

const TIPOS_USUARIO = ['PROFECO', 'COMERCIANTE'];

const INITIAL_FORM = {
  nombre: '',
  apellido: '',
  email: '',
  telefono: '',
  password: '',
  tipo_usuario: 'PROFECO'
};

export function UsuariosPage() {
  const [filtroTipo, setFiltroTipo] = useState('');

  const { data: usuarios, loading, error, reload } = useFetch(
    () => usuariosApi.listar(filtroTipo),
    [filtroTipo]
  );

  const [form, setForm] = useState(INITIAL_FORM);
  const [guardando, setGuardando] = useState(false);
  const [okMsg, setOkMsg] = useState(null);
  const [errMsg, setErrMsg] = useState(null);

  const onField = (key, value) => setForm((prev) => ({ ...prev, [key]: value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setGuardando(true);
    setOkMsg(null);
    setErrMsg(null);

    try {
      const payload = {
        nombre: form.nombre.trim(),
        apellido: form.apellido.trim(),
        email: form.email.trim(),
        telefono: form.telefono.trim(),
        password: form.password,
        tipo_usuario: form.tipo_usuario
      };

      const usuario = await usuariosApi.registrar(payload);
      setOkMsg(`Usuario registrado con éxito — ID #${usuario?.id ?? 'N/A'}`);
      setForm(INITIAL_FORM);
      reload();
    } catch (err) {
      setErrMsg(err.message || 'No se pudo registrar el usuario.');
    } finally {
      setGuardando(false);
    }
  };

  return (
    <div className="page">
      <div className="section-header">
        <h1>Gestión de Usuarios</h1>
      </div>
      <p className="muted">Registro de agentes PROFECO y propietarios de comercios.</p>

      {/* ── Formulario de registro ── */}
      <section className="card" style={{ marginBottom: '2rem' }}>
        <h3>Registrar Nuevo Usuario</h3>
        <form className="resenia-form" onSubmit={handleSubmit}>
          <div className="grid grid-2">
            <label>
              Nombre:
              <input
                required
                value={form.nombre}
                onChange={(e) => onField('nombre', e.target.value)}
              />
            </label>

            <label>
              Apellido:
              <input
                required
                value={form.apellido}
                onChange={(e) => onField('apellido', e.target.value)}
              />
            </label>

            <label>
              Correo electrónico:
              <input
                required
                type="email"
                value={form.email}
                onChange={(e) => onField('email', e.target.value)}
              />
            </label>

            <label>
              Teléfono:
              <input
                required
                value={form.telefono}
                onChange={(e) => onField('telefono', e.target.value)}
              />
            </label>

            <label>
              Contraseña:
              <input
                required
                type="password"
                minLength={6}
                placeholder="Mínimo 6 caracteres"
                value={form.password}
                onChange={(e) => onField('password', e.target.value)}
              />
            </label>

            <label>
              Tipo de usuario:
              <select
                required
                value={form.tipo_usuario}
                onChange={(e) => onField('tipo_usuario', e.target.value)}
              >
                {TIPOS_USUARIO.map((t) => (
                  <option key={t} value={t}>{t}</option>
                ))}
              </select>
            </label>
          </div>

          {okMsg && <div className="success-box">{okMsg}</div>}
          {errMsg && <div className="error-box">{errMsg}</div>}

          <button type="submit" disabled={guardando}>
            {guardando ? 'Registrando...' : 'Registrar usuario'}
          </button>
        </form>
      </section>

      {/* ── Tabla de usuarios ── */}
      <section>
        <div className="filter-bar">
          <select
            value={filtroTipo}
            onChange={(e) => setFiltroTipo(e.target.value)}
          >
            <option value="">Todos los tipos</option>
            {TIPOS_USUARIO.map((t) => (
              <option key={t} value={t}>{t}</option>
            ))}
          </select>
        </div>

        {loading && <Loader />}
        {error && <ErrorBox message={error} />}

        {!loading && !error && (usuarios ?? []).length === 0 && (
          <EmptyState title="No se encontraron usuarios" />
        )}

        {usuarios && usuarios.length > 0 && (
          <div className="table-wrap">
            <table className="precios-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nombre</th>
                  <th>Email</th>
                  <th>Teléfono</th>
                  <th>Tipo</th>
                </tr>
              </thead>
              <tbody>
                {usuarios.map((u) => (
                  <tr key={u.id}>
                    <td><strong>#{u.id}</strong></td>
                    <td>{u.nombre} {u.apellido}</td>
                    <td className="muted small">{u.email}</td>
                    <td className="muted small">{u.telefono}</td>
                    <td>
                      <span className="chip">{u.tipo_usuario ?? u.tipoUsuario}</span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </div>
  );
}
