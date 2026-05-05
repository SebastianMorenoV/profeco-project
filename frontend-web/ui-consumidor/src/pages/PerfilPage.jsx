import { Link, useNavigate } from 'react-router-dom';
import { useUser } from '../context/UserContext';

export function PerfilPage() {
  const {
    usuarioId,
    nombre,
    apellido,
    email,
    favoritos,
    wishlist,
    listaCompras,
    busquedasRecientes,
    limpiarBusquedas,
    cerrarSesion
  } = useUser();

  const nombreCompleto = [nombre, apellido].filter(Boolean).join(' ').trim();

  const navigate = useNavigate();

  const salir = () => {
    cerrarSesion();
    navigate('/login', { replace: true });
  };

  return (
    <div className="page">
      <h1>Mi cuenta</h1>
      <p className="muted">
        Tu sesión local guarda tus listas, búsquedas y la identidad con la que envías reseñas y reportes.
      </p>

      <div className="grid grid-2">
        <article className="card">
          <h3>Sesión activa</h3>
          {nombreCompleto && <p><strong>{nombreCompleto}</strong></p>}
          {email && <p className="muted small">{email}</p>}
          <p className="muted small">ID #{usuarioId}</p>
          <button type="button" className="btn-ghost" onClick={salir}>
            Cerrar sesión
          </button>
        </article>

        <article className="card">
          <h3>Mis listas</h3>
          <ul className="resumen-listas">
            <li>
              <Link to="/mis-favoritos">Comercios favoritos</Link>
              <span className="chip-count">{favoritos.length}</span>
            </li>
            <li>
              <Link to="/mi-wishlist">Productos en wishlist</Link>
              <span className="chip-count">{wishlist.length}</span>
            </li>
            <li>
              <Link to="/lista-compras">Lista del supermercado</Link>
              <span className="chip-count">{listaCompras.length}</span>
            </li>
            <li>
              <Link to="/reportar">Mis reportes a PROFECO</Link>
              <span className="muted small">Ver historial</span>
            </li>
          </ul>
        </article>
      </div>

      <article className="card">
        <h3>Búsquedas recientes</h3>
        {busquedasRecientes.length === 0 ? (
          <p className="muted">Aún no tienes búsquedas guardadas.</p>
        ) : (
          <>
            <ul className="busquedas-recientes">
              {busquedasRecientes.map((q) => (
                <li key={q}>
                  <Link to={`/productos?q=${encodeURIComponent(q)}`}>{q}</Link>
                </li>
              ))}
            </ul>
            <button type="button" className="btn-ghost" onClick={limpiarBusquedas}>
              Borrar historial
            </button>
          </>
        )}
      </article>
    </div>
  );
}
