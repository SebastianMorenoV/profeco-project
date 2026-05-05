import { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { reportesApi, MOTIVOS_REPORTE, ESTATUS_REPORTE_LABEL } from '../api';
import { useUser } from '../context/UserContext';
import { Loader, ErrorBox, EmptyState } from '../components/Loader';
import { formatDate } from '../utils/format';

export function ReportarPage() {
  const { usuarioId } = useUser();
  const [searchParams] = useSearchParams();

  const comercioInicial = searchParams.get('comercioId') ?? '';

  const [comercioId, setComercioId] = useState(comercioInicial);
  const [motivo, setMotivo] = useState(MOTIVOS_REPORTE[0].value);
  const [descripcion, setDescripcion] = useState('');
  const [enviando, setEnviando] = useState(false);
  const [okMsg, setOkMsg] = useState(null);
  const [errMsg, setErrMsg] = useState(null);

  const [reportes, setReportes] = useState([]);
  const [reportesLoading, setReportesLoading] = useState(true);
  const [reportesError, setReportesError] = useState(null);

  const cargarReportes = async () => {
    setReportesLoading(true);
    setReportesError(null);
    try {
      const list = await reportesApi.listarPorUsuario(usuarioId);
      setReportes(list);
    } catch (err) {
      setReportesError(err instanceof Error ? err.message : 'No se pudieron cargar tus reportes.');
    } finally {
      setReportesLoading(false);
    }
  };

  useEffect(() => {
    cargarReportes();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [usuarioId]);

  const enviar = async (e) => {
    e.preventDefault();
    setOkMsg(null);
    setErrMsg(null);
    const cid = Number(comercioId);
    if (!Number.isFinite(cid) || cid <= 0) {
      setErrMsg('Captura un ID de comercio válido.');
      return;
    }
    if (!descripcion.trim()) {
      setErrMsg('La descripción es obligatoria.');
      return;
    }
    setEnviando(true);
    try {
      await reportesApi.crear({
        usuarioId,
        comercioId: cid,
        motivo,
        descripcion: descripcion.trim()
      });
      setOkMsg('Reporte enviado. PROFECO lo revisará.');
      setDescripcion('');
      cargarReportes();
    } catch (err) {
      setErrMsg(err instanceof Error ? err.message : 'No se pudo enviar el reporte.');
    } finally {
      setEnviando(false);
    }
  };

  return (
    <div className="page">
      <h1>Reportar inconsistencia</h1>
      <p className="muted">
        Denuncia precios excesivos, publicidad engañosa o cualquier práctica abusiva. Los reportes se envían a PROFECO.
      </p>

      <form className="card resenia-form" onSubmit={enviar}>
        <h3>Nuevo reporte</h3>

        <label>
          ID del comercio
          <input
            type="number"
            min={1}
            placeholder="Ej. 1"
            value={comercioId}
            onChange={(e) => setComercioId(e.target.value)}
            required
          />
          <span className="muted small">
            Puedes consultar el ID en la pantalla del comercio (URL <code>/comercios/&#123;id&#125;</code>).
          </span>
        </label>

        <label>
          Motivo
          <select value={motivo} onChange={(e) => setMotivo(e.target.value)}>
            {MOTIVOS_REPORTE.map((m) => (
              <option key={m.value} value={m.value}>{m.label}</option>
            ))}
          </select>
        </label>

        <label>
          Descripción de los hechos
          <textarea
            required
            rows={4}
            maxLength={500}
            value={descripcion}
            onChange={(e) => setDescripcion(e.target.value)}
            placeholder="Describe lo ocurrido con el mayor detalle posible (fecha, producto, precio publicado vs. cobrado, etc.)"
          />
          <span className="muted small">{descripcion.length}/500</span>
        </label>

        {okMsg && <div className="success-box">{okMsg}</div>}
        {errMsg && <ErrorBox message={errMsg} />}

        <button type="submit" disabled={enviando}>
          {enviando ? 'Enviando…' : 'Enviar reporte'}
        </button>
      </form>

      <section>
        <div className="section-header">
          <h2>Mis reportes</h2>
          <span className="muted small">Usuario #{usuarioId}</span>
        </div>

        {reportesLoading && <Loader />}
        {reportesError && <ErrorBox message={reportesError} />}
        {!reportesLoading && !reportesError && reportes.length === 0 && (
          <EmptyState title="Aún no has enviado reportes" hint="Cuando reportes una inconsistencia aparecerá aquí." />
        )}

        {reportes.length > 0 && (
          <ul className="resenias-list">
            {reportes.map((r) => (
              <li key={r.id} className="card resenia">
                <div className="resenia-head">
                  <span className={`estatus estatus-${r.estatus?.toLowerCase()}`}>
                    {ESTATUS_REPORTE_LABEL[r.estatus] ?? r.estatus}
                  </span>
                  <span className="muted small">
                    {formatDate(r.fechaCreacion)} · <Link to={`/comercios/${r.comercioId}`}>Comercio #{r.comercioId}</Link>
                  </span>
                </div>
                <p className="muted small">
                  Motivo: <strong>{(MOTIVOS_REPORTE.find((m) => m.value === r.motivo)?.label) ?? r.motivo}</strong>
                </p>
                <p>{r.descripcion}</p>
                {r.multaId ? (
                  <p className="muted small">PROFECO emitió la multa #{r.multaId} relacionada con este reporte.</p>
                ) : null}
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  );
}
