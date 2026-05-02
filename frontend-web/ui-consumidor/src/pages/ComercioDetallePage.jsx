import { useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { comerciosApi, ofertasApi, reseniasApi } from '../api';
import { useFetch } from '../hooks/useFetch';
import { Loader, ErrorBox, EmptyState } from '../components/Loader';
import { StarRating } from '../components/StarRating';
import { formatDate, formatMXN } from '../utils/format';

export function ComercioDetallePage() {
  const { id } = useParams();
  const comercioId = Number(id);

  const comercio = useFetch(() => comerciosApi.obtener(comercioId), [comercioId]);
  const promedio = useFetch(() => reseniasApi.obtenerPromedio(comercioId), [comercioId]);
  const resenias = useFetch(() => reseniasApi.listarPorComercio(comercioId), [comercioId]);
  const ofertas = useFetch(() => ofertasApi.listarPorComercio(comercioId), [comercioId]);

  const [calificacion, setCalificacion] = useState(5);
  const [comentario, setComentario] = useState('');
  const [usuarioId, setUsuarioId] = useState(1);
  const [enviando, setEnviando] = useState(false);
  const [okMsg, setOkMsg] = useState(null);
  const [errMsg, setErrMsg] = useState(null);

  const enviarResenia = async (e) => {
    e.preventDefault();
    setEnviando(true);
    setOkMsg(null);
    setErrMsg(null);
    try {
      await reseniasApi.crear({ usuarioId, comercioId, calificacion, comentario });
      setOkMsg('Reseña publicada. Gracias por tu aporte.');
      setComentario('');
      resenias.reload();
      promedio.reload();
    } catch (err) {
      setErrMsg(err instanceof Error ? err.message : 'No se pudo publicar.');
    } finally {
      setEnviando(false);
    }
  };

  if (comercio.loading) return <Loader />;
  if (comercio.error) return <ErrorBox message={comercio.error} />;
  if (!comercio.data) return <EmptyState title="Comercio no encontrado" />;

  const c = comercio.data;
  const prom = promedio.data;

  return (
    <div className="page">
      <Link to="/comercios" className="link-back">← Volver a comercios</Link>

      <div className="comercio-header">
        <span className="chip">{c.tipoComercio.replace('_', ' ')}</span>
        <h1>{c.nombreComercial}</h1>
        <p className="muted">{c.razonSocial} · RFC {c.rfc}</p>
        <p>{c.direccion}</p>
        <p className="muted small">
          {c.ciudad}, {c.estado} · CP {c.codigoPostal}
        </p>
        <p className="muted small">
          Tel: {c.telefono || 's/d'} · {c.email || 's/d'}
        </p>

        {prom && prom.totalResenias > 0 ? (
          <div className="rating-summary">
            <StarRating value={prom.promedio} size={22} />
            <span>
              <strong>{prom.promedio.toFixed(1)}</strong> de 5
              <span className="muted"> · {prom.totalResenias} reseñas</span>
            </span>
          </div>
        ) : (
          <p className="muted">Aún no hay reseñas para este comercio.</p>
        )}
      </div>

      <section>
        <h2>Ofertas vigentes</h2>
        {ofertas.loading && <Loader />}
        {ofertas.error && <ErrorBox message={ofertas.error} />}
        {!ofertas.loading && !ofertas.error && (ofertas.data ?? []).length === 0 && (
          <EmptyState title="Sin ofertas activas" />
        )}
        <div className="grid grid-3">
          {(ofertas.data ?? []).map((o) => (
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
            </article>
          ))}
        </div>
      </section>

      <section>
        <h2>Reseñas</h2>
        {resenias.loading && <Loader />}
        {resenias.error && <ErrorBox message={resenias.error} />}
        {!resenias.loading && !resenias.error && (resenias.data ?? []).length === 0 && (
          <EmptyState title="Sé el primero en opinar" />
        )}

        <ul className="resenias-list">
          {(resenias.data ?? []).map((r) => (
            <li key={r.id} className="card resenia">
              <div className="resenia-head">
                <StarRating value={r.calificacion} />
                <span className="muted small">
                  Usuario #{r.usuarioId} · {formatDate(r.fechaCreacion)}
                </span>
              </div>
              <p>{r.comentario}</p>
            </li>
          ))}
        </ul>

        <form className="card resenia-form" onSubmit={enviarResenia}>
          <h3>Deja tu reseña</h3>
          <label>
            Tu calificación
            <StarRating value={calificacion} size={26} onChange={setCalificacion} />
          </label>
          <label>
            ID de usuario
            <input
              type="number"
              min={1}
              value={usuarioId}
              onChange={(e) => setUsuarioId(Number(e.target.value))}
            />
          </label>
          <label>
            Comentario
            <textarea
              required
              rows={3}
              maxLength={500}
              value={comentario}
              onChange={(e) => setComentario(e.target.value)}
              placeholder="¿Cómo fue tu experiencia?"
            />
          </label>
          {okMsg && <div className="success-box">{okMsg}</div>}
          {errMsg && <ErrorBox message={errMsg} />}
          <button type="submit" disabled={enviando}>
            {enviando ? 'Enviando…' : 'Publicar reseña'}
          </button>
        </form>
      </section>
    </div>
  );
}
