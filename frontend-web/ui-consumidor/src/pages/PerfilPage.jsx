import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useUser } from '../context/UserContext';

export function PerfilPage() {
  const {
    usuarioId,
    favoritos,
    wishlist,
    listaCompras,
    busquedasRecientes,
    setUsuarioId,
    limpiarBusquedas
  } = useUser();

  const [draftId, setDraftId] = useState(String(usuarioId));
  const [okMsg, setOkMsg] = useState(null);

  const guardar = (e) => {
    e.preventDefault();
    const num = Number(draftId);
    if (!Number.isFinite(num) || num <= 0) {
      setOkMsg(null);
      return;
    }
    setUsuarioId(num);
    setOkMsg('Identidad actualizada.');
    setTimeout(() => setOkMsg(null), 2500);
  };

  return (
    <div className="page">
      <h1>Mi cuenta</h1>
      <p className="muted">
        Mientras la plataforma habilita el inicio de sesión, identifícate manualmente con tu ID de usuario para que tus reseñas, reportes y listas se asocien correctamente.
      </p>

      <div className="grid grid-2">
        <form className="card resenia-form" onSubmit={guardar}>
          <h3>Identidad</h3>
          <label>
            ID de usuario
            <input
              type="number"
              min={1}
              value={draftId}
              onChange={(e) => setDraftId(e.target.value)}
              required
            />
            <span className="muted small">
              Se persiste en este navegador y se usa al publicar reseñas, reportes y al sincronizar listas.
            </span>
          </label>
          {okMsg && <div className="success-box">{okMsg}</div>}
          <button type="submit">Guardar</button>
        </form>

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
