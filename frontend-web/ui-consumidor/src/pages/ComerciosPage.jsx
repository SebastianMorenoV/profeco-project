import { useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { comerciosApi } from '../api';
import { useFetch } from '../hooks/useFetch';
import { Loader, ErrorBox, EmptyState } from '../components/Loader';

const TIPOS = [
  'SUPERMERCADO',
  'TIENDA_CONVENIENCIA',
  'FARMACIA',
  'MERCADO',
  'DEPARTAMENTAL'
];

export function ComerciosPage() {
  const [params, setParams] = useSearchParams();
  const initialCiudad = params.get('ciudad') ?? '';
  const initialTipo = params.get('tipo') ?? '';

  const [ciudad, setCiudad] = useState(initialCiudad);
  const [tipo, setTipo] = useState(initialTipo);

  const { data, loading, error } = useFetch(
    () => comerciosApi.listar(initialCiudad || undefined, initialTipo || undefined),
    [initialCiudad, initialTipo]
  );

  const submit = (e) => {
    e.preventDefault();
    const next = new URLSearchParams();
    if (ciudad.trim()) next.set('ciudad', ciudad.trim());
    if (tipo) next.set('tipo', tipo);
    setParams(next);
  };

  return (
    <div className="page">
      <h1>Comercios registrados</h1>
      <p className="muted">Filtra por ciudad o tipo de comercio.</p>

      <form className="filter-bar" onSubmit={submit}>
        <input
          type="text"
          placeholder="Ciudad"
          value={ciudad}
          onChange={(e) => setCiudad(e.target.value)}
        />
        <select value={tipo} onChange={(e) => setTipo(e.target.value)}>
          <option value="">Todos los tipos</option>
          {TIPOS.map((t) => (
            <option key={t} value={t}>{t.replace('_', ' ')}</option>
          ))}
        </select>
        <button type="submit">Filtrar</button>
      </form>

      {loading && <Loader />}
      {error && <ErrorBox message={error} />}
      {!loading && !error && data && data.length === 0 && (
        <EmptyState title="No hay comercios para ese filtro" />
      )}

      <div className="grid grid-2">
        {(data ?? []).map((c) => (
          <Link key={c.id} to={`/comercios/${c.id}`} className="card comercio">
            <span className="chip">{c.tipoComercio.replace('_', ' ')}</span>
            <h3>{c.nombreComercial}</h3>
            <p className="muted">{c.direccion}</p>
            <p className="muted small">{c.ciudad}, {c.estado} · CP {c.codigoPostal}</p>
            <span className="link-more">Ver detalle y reseñas →</span>
          </Link>
        ))}
      </div>
    </div>
  );
}
