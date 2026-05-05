import { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { catalogoApi, CATEGORIAS } from '../api';
import { useFetch } from '../hooks/useFetch';
import { Loader, ErrorBox, EmptyState } from '../components/Loader';
import { useUser } from '../context/UserContext';

export function ProductosPage() {
  const [params, setParams] = useSearchParams();
  const initialQ = params.get('q') ?? '';
  const initialCat = params.get('categoria') ?? '';

  const [q, setQ] = useState(initialQ);
  const [categoria, setCategoria] = useState(initialCat);

  const { wishlist, toggleWishlist, registrarBusqueda } = useUser();

  const { data, loading, error } = useFetch(
    () => catalogoApi.buscarProductos(initialQ || undefined, initialCat || undefined),
    [initialQ, initialCat]
  );

  useEffect(() => {
    if (initialQ && Array.isArray(data) && data.length > 0) {
      registrarBusqueda(initialQ);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [initialQ, data]);

  const submit = (e) => {
    e.preventDefault();
    const next = new URLSearchParams();
    if (q.trim()) next.set('q', q.trim());
    if (categoria) next.set('categoria', categoria);
    setParams(next);
  };

  const enWishlist = (id) => wishlist.includes(Number(id));

  return (
    <div className="page">
      <h1>Productos</h1>
      <p className="muted">
        Busca por nombre o filtra por categoría para comparar precios entre comercios.
      </p>

      <form className="filter-bar" onSubmit={submit}>
        <input
          type="search"
          placeholder="Nombre del producto"
          value={q}
          onChange={(e) => setQ(e.target.value)}
        />
        <select value={categoria} onChange={(e) => setCategoria(e.target.value)}>
          <option value="">Todas las categorías</option>
          {CATEGORIAS.map((c) => (
            <option key={c} value={c}>{c}</option>
          ))}
        </select>
        <button type="submit">Filtrar</button>
      </form>

      {loading && <Loader />}
      {error && <ErrorBox message={error} />}
      {!loading && !error && data && data.length === 0 && (
        <EmptyState
          title="No encontramos productos"
          hint="Prueba con otra palabra clave o quita los filtros."
        />
      )}

      <div className="grid grid-3">
        {(data ?? []).map((p) => (
          <article key={p.id} className="card producto">
            <span className="chip">{p.categoria || 'SIN CATEGORÍA'}</span>
            <h3>
              <Link to={`/productos/${p.id}`}>{p.nombre}</Link>
            </h3>
            <p className="muted">{p.marca}</p>
            <p className="line-clamp">{p.descripcion}</p>
            <div className="card-actions">
              <Link to={`/productos/${p.id}`} className="link-more">Ver precios →</Link>
              <button
                type="button"
                className={`btn-fav${enWishlist(p.id) ? ' on' : ''}`}
                onClick={() => toggleWishlist(p.id)}
                aria-label={enWishlist(p.id) ? 'Quitar de wishlist' : 'Agregar a wishlist'}
              >
                {enWishlist(p.id) ? '♥ En wishlist' : '♡ Wishlist'}
              </button>
            </div>
          </article>
        ))}
      </div>
    </div>
  );
}
