import { useState } from 'react';
import { Link } from 'react-router-dom';
import { ofertasApi } from '../api';
import { useFetch } from '../hooks/useFetch';
import { Loader, ErrorBox, EmptyState } from '../components/Loader';
import { formatDate, formatMXN } from '../utils/format';

export function OfertasPage() {
  const [soloActivas, setSoloActivas] = useState(true);
  const { data, loading, error } = useFetch(
    () => ofertasApi.listar(soloActivas),
    [soloActivas]
  );

  return (
    <div className="page">
      <h1>Ofertas</h1>
      <p className="muted">Promociones reportadas por los comercios.</p>

      <div className="filter-bar">
        <label className="checkbox">
          <input
            type="checkbox"
            checked={soloActivas}
            onChange={(e) => setSoloActivas(e.target.checked)}
          />
          Mostrar solo ofertas activas
        </label>
      </div>

      {loading && <Loader />}
      {error && <ErrorBox message={error} />}
      {!loading && !error && (data ?? []).length === 0 && (
        <EmptyState title="No hay ofertas para mostrar" />
      )}

      <div className="grid grid-3">
        {(data ?? []).map((o) => (
          <article key={o.id} className="card oferta">
            <div className="badge">-{Math.round(o.porcentajeDescuento)}%</div>
            <h3>{o.titulo}</h3>
            <p className="muted">{o.descripcion}</p>
            <div className="precios">
              <span className="precio-tachado">{formatMXN(o.precioOriginal)}</span>
              <span className="precio-oferta">{formatMXN(o.precioOferta)}</span>
            </div>
            <p className="muted small">
              Vigencia: {formatDate(o.fechaInicio)} – {formatDate(o.fechaFin)}
            </p>
            <Link to={`/comercios/${o.comercioId}`} className="link-more">
              Ver comercio →
            </Link>
          </article>
        ))}
      </div>
    </div>
  );
}
