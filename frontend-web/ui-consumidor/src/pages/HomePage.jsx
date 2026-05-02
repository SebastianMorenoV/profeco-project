import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { ofertasApi } from '../api';
import { useFetch } from '../hooks/useFetch';
import { Loader, ErrorBox, EmptyState } from '../components/Loader';
import { formatMXN } from '../utils/format';

export function HomePage() {
  const nav = useNavigate();
  const [q, setQ] = useState('');
  const ofertas = useFetch(() => ofertasApi.listar(true), []);

  const submit = (e) => {
    e.preventDefault();
    const params = new URLSearchParams();
    if (q.trim()) params.set('q', q.trim());
    nav(`/productos${params.toString() ? `?${params}` : ''}`);
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
        <Link to="/ofertas" className="info-card">
          <h3>Promociones vigentes</h3>
          <p>Las mejores ofertas publicadas en tiempo real.</p>
        </Link>
      </section>
    </div>
  );
}
