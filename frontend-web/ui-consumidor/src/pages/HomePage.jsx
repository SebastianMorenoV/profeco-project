import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { ofertasApi } from '../api';
import { useFetch } from '../hooks/useFetch';
import { Loader, ErrorBox, EmptyState } from '../components/Loader';
import { formatMXN } from '../utils/format';
import { useUser } from '../context/UserContext';

export function HomePage() {
  const nav = useNavigate();
  const [q, setQ] = useState('');
  const ofertas = useFetch(() => ofertasApi.listar(true), []);
  const { busquedasRecientes, registrarBusqueda, favoritos, wishlist, listaCompras } = useUser();

  const submit = (e) => {
    e.preventDefault();
    const limpio = q.trim();
    const params = new URLSearchParams();
    if (limpio) {
      params.set('q', limpio);
      registrarBusqueda(limpio);
    }
    nav(`/productos${params.toString() ? `?${params}` : ''}`);
  };

  const usarBusqueda = (texto) => {
    registrarBusqueda(texto);
    nav(`/productos?q=${encodeURIComponent(texto)}`);
  };

  const destacadas = (ofertas.data ?? []).slice(0, 3);

  return (
    <div className="home">
      <section className="hero">
        <h1>Compara precios y reseñas antes de comprar</h1>
        <p>
          Encuentra el mejor precio entre comercios de tu ciudad, evalúa la calidad
          del servicio y consulta historial público de multas.
        </p>
        <form className="search" onSubmit={submit}>
          <input
            type="search"
            placeholder="Busca un producto: leche, huevos, refresco…"
            value={q}
            onChange={(e) => setQ(e.target.value)}
          />
          <button type="submit">Buscar</button>
        </form>
        {busquedasRecientes.length > 0 && (
          <div className="hero-recientes">
            <span className="muted small">Búsquedas recientes:</span>
            <div className="chips-row">
              {busquedasRecientes.map((b) => (
                <button
                  key={b}
                  type="button"
                  className="chip-action"
                  onClick={() => usarBusqueda(b)}
                >
                  {b}
                </button>
              ))}
            </div>
          </div>
        )}
      </section>

      <section className="mis-listas">
        <div className="section-header">
          <h2>Mis listas</h2>
          <Link to="/perfil" className="link-more">Mi cuenta →</Link>
        </div>
        <div className="grid grid-3">
          <Link to="/mis-favoritos" className="card mini-stat">
            <span className="chip">Favoritos</span>
            <strong>{favoritos.length}</strong>
            <p className="muted">Comercios que sigues</p>
          </Link>
          <Link to="/mi-wishlist" className="card mini-stat">
            <span className="chip">Wishlist</span>
            <strong>{wishlist.length}</strong>
            <p className="muted">Productos guardados</p>
          </Link>
          <Link to="/lista-compras" className="card mini-stat">
            <span className="chip">Lista</span>
            <strong>{listaCompras.length}</strong>
            <p className="muted">Items por comprar</p>
          </Link>
        </div>
      </section>

      <section>
        <div className="section-header">
          <h2>Ofertas activas</h2>
          <Link to="/ofertas" className="link-more">Ver todas →</Link>
        </div>

        {ofertas.loading && <Loader />}
        {ofertas.error && <ErrorBox message={ofertas.error} />}
        {!ofertas.loading && !ofertas.error && destacadas.length === 0 && (
          <EmptyState title="Aún no hay ofertas publicadas" />
        )}

        <div className="grid grid-3">
          {destacadas.map((o) => (
            <article key={o.id} className="card oferta">
              <div className="badge">-{Math.round(o.porcentajeDescuento)}%</div>
              <h3>{o.titulo}</h3>
              <p className="muted">{o.descripcion}</p>
              <div className="precios">
                <span className="precio-tachado">{formatMXN(o.precioOriginal)}</span>
                <span className="precio-oferta">{formatMXN(o.precioOferta)}</span>
              </div>
            </article>
          ))}
        </div>
      </section>

      <section className="info-grid">
        <Link to="/productos" className="info-card">
          <h3>Catálogo de productos</h3>
          <p>Explora precios reportados por comercio.</p>
        </Link>
        <Link to="/comercios" className="info-card">
          <h3>Comercios registrados</h3>
          <p>Califica y consulta reseñas de otros consumidores.</p>
        </Link>
        <Link to="/reportar" className="info-card">
          <h3>Reportar inconsistencia</h3>
          <p>Denuncia precios excesivos o publicidad engañosa.</p>
        </Link>
      </section>
    </div>
  );
}
