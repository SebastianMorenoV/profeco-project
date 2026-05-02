import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { catalogoApi, comerciosApi } from '../api';
import { useFetch } from '../hooks/useFetch';
import { Loader, ErrorBox, EmptyState } from '../components/Loader';
import { formatMXN, formatDate } from '../utils/format';

export function ProductoDetallePage() {
  const { id } = useParams();
  const productoId = Number(id);

  const producto = useFetch(() => catalogoApi.obtenerProducto(productoId), [productoId]);
  const precios = useFetch(() => catalogoApi.obtenerPreciosProducto(productoId), [productoId]);

  const [comercios, setComercios] = useState({});

  useEffect(() => {
    if (!precios.data) return;
    const ids = Array.from(new Set(precios.data.map((p) => p.comercioId)));
    Promise.all(
      ids.map((cid) =>
        comerciosApi.obtener(cid).then(
          (c) => [cid, c],
          () => [cid, null]
        )
      )
    ).then((entries) => {
      const map = {};
      for (const [cid, c] of entries) if (c) map[cid] = c;
      setComercios(map);
    });
  }, [precios.data]);

  if (producto.loading) return <Loader />;
  if (producto.error) return <ErrorBox message={producto.error} />;
  if (!producto.data) return <EmptyState title="Producto no encontrado" />;

  const ordenados = [...(precios.data ?? [])].sort((a, b) => a.precio - b.precio);
  const mejor = ordenados[0];

  return (
    <div className="page">
      <Link to="/productos" className="link-back">← Volver al catálogo</Link>

      <div className="producto-header">
        <span className="chip">{producto.data.categoria}</span>
        <h1>{producto.data.nombre}</h1>
        <p className="muted">{producto.data.marca} · {producto.data.unidadMedida}</p>
        <p>{producto.data.descripcion}</p>
        {producto.data.codigoBarras && (
          <p className="muted small">Código de barras: {producto.data.codigoBarras}</p>
        )}
      </div>

      <section>
        <div className="section-header">
          <h2>Comparación de precios</h2>
          {mejor && (
            <span className="muted">
              Mejor precio: <strong>{formatMXN(mejor.precio)}</strong>
            </span>
          )}
        </div>

        {precios.loading && <Loader />}
        {precios.error && <ErrorBox message={precios.error} />}
        {!precios.loading && !precios.error && ordenados.length === 0 && (
          <EmptyState title="Aún no hay precios reportados para este producto" />
        )}

        {ordenados.length > 0 && (
          <table className="precios-table">
            <thead>
              <tr>
                <th>Comercio</th>
                <th>Ciudad</th>
                <th>Precio</th>
                <th>Reportado</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {ordenados.map((p, idx) => {
                const c = comercios[p.comercioId];
                return (
                  <tr key={p.id} className={idx === 0 ? 'best' : ''}>
                    <td>
                      {c ? (
                        <Link to={`/comercios/${c.id}`}>{c.nombreComercial}</Link>
                      ) : (
                        <span className="muted">Comercio #{p.comercioId}</span>
                      )}
                    </td>
                    <td>{c?.ciudad ?? '—'}</td>
                    <td>
                      <strong>{formatMXN(p.precio)}</strong>
                      {idx === 0 && <span className="badge-mini">Mejor</span>}
                    </td>
                    <td className="muted small">{formatDate(p.fechaReporte)}</td>
                    <td>
                      {c && (
                        <Link to={`/comercios/${c.id}`} className="link-more">
                          Ver tienda →
                        </Link>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
      </section>
    </div>
  );
}
