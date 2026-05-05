import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { catalogoApi, usuariosApi } from '../api';
import { useUser } from '../context/UserContext';
import { Loader, ErrorBox, EmptyState } from '../components/Loader';

export function MiWishlistPage() {
  const { usuarioId, wishlist, toggleWishlist, setWishlistRemota } = useUser();
  const [productos, setProductos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let cancelado = false;
    setLoading(true);
    setError(null);

    (async () => {
      try {
        let ids = wishlist;
        try {
          const remotos = await usuariosApi.obtenerWishlist(usuarioId);
          if (Array.isArray(remotos) && remotos.length > 0) {
            ids = Array.from(new Set([...remotos.map(Number), ...wishlist]));
            if (!cancelado) setWishlistRemota(ids);
          }
        } catch {
          // sin backend: usar lista local
        }

        const items = await Promise.all(
          ids.map((id) => catalogoApi.obtenerProducto(id).then((p) => p, () => null))
        );
        if (!cancelado) setProductos(items.filter(Boolean));
      } catch (err) {
        if (!cancelado) setError(err instanceof Error ? err.message : 'No se pudo cargar tu wishlist.');
      } finally {
        if (!cancelado) setLoading(false);
      }
    })();

    return () => { cancelado = true; };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [usuarioId, wishlist.length]);

  return (
    <div className="page">
      <h1>Mi wishlist</h1>
      <p className="muted">
        Guarda productos que te interesan para comparar precios entre comercios cuando los necesites.
      </p>

      {loading && <Loader />}
      {error && <ErrorBox message={error} />}
      {!loading && !error && productos.length === 0 && (
        <EmptyState
          title="Tu wishlist está vacía"
          hint="Agrega productos desde su pantalla de detalle con el botón ‘Guardar en wishlist’."
        />
      )}

      <div className="grid grid-3">
        {productos.map((p) => (
          <article key={p.id} className="card producto">
            <span className="chip">{p.categoria || 'SIN CATEGORÍA'}</span>
            <h3>{p.nombre}</h3>
            <p className="muted">{p.marca} · {p.unidadMedida}</p>
            <p className="line-clamp">{p.descripcion}</p>
            <div className="card-actions">
              <Link to={`/productos/${p.id}`} className="link-more">Ver precios →</Link>
              <button
                type="button"
                className="btn-ghost"
                onClick={() => toggleWishlist(p.id)}
              >
                Quitar
              </button>
            </div>
          </article>
        ))}
      </div>
    </div>
  );
}
