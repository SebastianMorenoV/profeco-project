import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { comerciosApi, usuariosApi } from '../api';
import { useUser } from '../context/UserContext';
import { Loader, ErrorBox, EmptyState } from '../components/Loader';

export function MisFavoritosPage() {
  const { usuarioId, favoritos, toggleFavorito, setFavoritosRemotos } = useUser();
  const [comercios, setComercios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let cancelado = false;
    setLoading(true);
    setError(null);

    (async () => {
      try {
        let ids = favoritos;
        try {
          const remotos = await usuariosApi.obtenerComerciosFavoritos(usuarioId);
          if (Array.isArray(remotos) && remotos.length > 0) {
            ids = Array.from(new Set([...remotos.map(Number), ...favoritos]));
            if (!cancelado) setFavoritosRemotos(ids);
          }
        } catch {
          // backend no disponible: trabajar con lista local
        }

        const items = await Promise.all(
          ids.map((id) => comerciosApi.obtener(id).then((c) => c, () => null))
        );
        if (!cancelado) setComercios(items.filter(Boolean));
      } catch (err) {
        if (!cancelado) setError(err instanceof Error ? err.message : 'No se pudieron cargar tus favoritos.');
      } finally {
        if (!cancelado) setLoading(false);
      }
    })();

    return () => { cancelado = true; };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [usuarioId, favoritos.length]);

  return (
    <div className="page">
      <h1>Mis comercios favoritos</h1>
      <p className="muted">
        Guarda los comercios que más visitas para encontrarlos rápido y recibir avisos de sus ofertas.
      </p>

      {loading && <Loader />}
      {error && <ErrorBox message={error} />}
      {!loading && !error && comercios.length === 0 && (
        <EmptyState
          title="Aún no tienes favoritos"
          hint="Marca un comercio como favorito desde su pantalla de detalle."
        />
      )}

      <div className="grid grid-2">
        {comercios.map((c) => (
          <article key={c.id} className="card comercio">
            <span className="chip">{c.tipoComercio?.replace('_', ' ')}</span>
            <h3>{c.nombreComercial}</h3>
            <p className="muted">{c.direccion}</p>
            <p className="muted small">{c.ciudad}, {c.estado}</p>
            <div className="card-actions">
              <Link to={`/comercios/${c.id}`} className="link-more">Ver detalle →</Link>
              <button
                type="button"
                className="btn-ghost"
                onClick={() => toggleFavorito(c.id)}
              >
                Quitar favorito
              </button>
            </div>
          </article>
        ))}
      </div>
    </div>
  );
}
