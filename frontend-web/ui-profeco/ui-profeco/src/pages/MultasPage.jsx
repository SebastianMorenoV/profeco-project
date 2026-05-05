import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { multasApi, usuariosApi, comerciosApi } from '../api';
import { useFetch } from '../hooks/useFetch';
import { Loader, ErrorBox, EmptyState } from '../components/Loader';
import { formatDate, formatMXN } from '../utils/format';
import {
  useCategories,
  DEFAULT_MOTIVOS_MULTA,
  STORAGE_KEY_MOTIVOS
} from '../hooks/useCategories';

export function MultasPage() {
  const [searchParams] = useSearchParams();
  const [estatusFiltro, setEstatusFiltro] = useState('');

  /* ── Categorías dinámicas de motivos ── */
  const { items: motivosMulta } = useCategories(STORAGE_KEY_MOTIVOS, DEFAULT_MOTIVOS_MULTA);

  /* ── Datos auxiliares para los comboboxes ── */
  const { data: agentes, loading: loadingAgentes } = useFetch(
    () => usuariosApi.listar('PROFECO'),
    []
  );

  const { data: comercios, loading: loadingComercios } = useFetch(
    () => comerciosApi.listar(),
    []
  );

  /* ── Lista principal de multas ── */
  const { data: multas, loading, error, reload } = useFetch(
    () => multasApi.listar(estatusFiltro),
    [estatusFiltro]
  );

  const [procesando, setProcesando] = useState(false);

  const [form, setForm] = useState(() => ({
    comercioId: searchParams.get('comercioId') ?? '',
    motivo: motivosMulta.length > 0 ? motivosMulta[0].key : '',
    descripcion: '',
    monto: '',
    reporteId: searchParams.get('reporteId') ?? '',
    emitidoPorUsuarioId: ''
  }));

  const [okMsg, setOkMsg] = useState(null);
  const [errMsg, setErrMsg] = useState(null);

  /* ── Caché de nombres de comercios para la tabla ── */
  const [comerciosMap, setComerciosMap] = useState({});

  useEffect(() => {
    if (!multas) return;
    const uniqueIds = [...new Set(multas.map((m) => m.comercioId))];
    const missingIds = uniqueIds.filter((id) => !(id in comerciosMap));

    if (missingIds.length > 0) {
      const fetchMissing = async () => {
        try {
          const promises = missingIds.map((id) => comerciosApi.obtener(id));
          const results = await Promise.allSettled(promises);
          const newData = {};
          results.forEach((res, idx) => {
            const reqId = missingIds[idx];
            if (res.status === 'fulfilled' && res.value) {
              newData[reqId] = res.value;
            } else {
              newData[reqId] = { nombreComercial: `Comercio #${reqId}` };
            }
          });
          setComerciosMap((prev) => ({ ...prev, ...newData }));
        } catch (err) {
          console.error('Error cargando comercios', err);
        }
      };
      fetchMissing();
    }
  }, [multas, comerciosMap]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setProcesando(true);
    setOkMsg(null);
    setErrMsg(null);

    try {
      const comercioId = Number(form.comercioId);
      const emitidoPorUsuarioId = Number(form.emitidoPorUsuarioId);

      if (!Number.isInteger(comercioId) || comercioId < 1) {
        setErrMsg('Debes seleccionar un comercio.');
        setProcesando(false);
        return;
      }

      if (!Number.isInteger(emitidoPorUsuarioId) || emitidoPorUsuarioId < 1) {
        setErrMsg('Debes seleccionar un agente PROFECO.');
        setProcesando(false);
        return;
      }

      await multasApi.emitir({
        comercioId,
        motivo: form.motivo,
        descripcion: form.descripcion,
        monto: Number(form.monto),
        reporteId: form.reporteId ? Number(form.reporteId) : undefined,
        emitidoPorUsuarioId
      });

      setOkMsg('¡Multa emitida con éxito!');
      setForm({
        comercioId: '',
        motivo: motivosMulta.length > 0 ? motivosMulta[0].key : '',
        descripcion: '',
        monto: '',
        reporteId: '',
        emitidoPorUsuarioId: ''
      });
      reload();
    } catch (err) {
      setErrMsg(err.message || 'No se pudo emitir la multa.');
    } finally {
      setProcesando(false);
    }
  };

  const cambiarEstatus = async (id, nuevoEstatus) => {
    if (!confirm(`¿Cambiar estatus de la multa #${id} a ${nuevoEstatus}?`)) return;
    
    setProcesando(true);
    try {
      await multasApi.actualizarEstatus(id, nuevoEstatus);
      reload();
    } catch (err) {
      alert('Error al actualizar: ' + err.message);
    } finally {
      setProcesando(false);
    }
  };

  return (
    <div className="page">
      <div className="section-header">
        <h1>Control de Multas</h1>
      </div>
      <p className="muted">Emisión y seguimiento de sanciones a comercios.</p>

      <section className="card" style={{ marginBottom: '2rem' }}>
        <h3>Emitir Nueva Multa</h3>
        <form className="resenia-form" onSubmit={handleSubmit}>
          <div className="grid grid-2">
            {/* ── Comercio (combobox) ── */}
            <label>
              Comercio:
              {loadingComercios ? (
                <Loader label="Cargando comercios…" />
              ) : (
                <select
                  required
                  value={form.comercioId}
                  onChange={(e) => setForm({ ...form, comercioId: e.target.value })}
                >
                  <option value="">Selecciona un comercio</option>
                  {(comercios ?? []).map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.nombreComercial} — {c.ciudad ?? ''}
                    </option>
                  ))}
                </select>
              )}
            </label>

            {/* ── Motivo (dinámico) ── */}
            <label>
              Motivo:
              <select
                value={form.motivo}
                onChange={(e) => setForm({ ...form, motivo: e.target.value })}
              >
                {motivosMulta.map((m) => (
                  <option key={m.key} value={m.key}>
                    {m.label}
                  </option>
                ))}
              </select>
            </label>

            <label>
              Monto (MXN):
              <input
                type="number" required min="0" step="0.01"
                value={form.monto}
                onChange={(e) => setForm({ ...form, monto: e.target.value })}
              />
            </label>

            <label>
              ID del Reporte (Opcional):
              <input
                type="number" min="1"
                placeholder="Si deriva de un reporte"
                value={form.reporteId}
                onChange={(e) => setForm({ ...form, reporteId: e.target.value })}
              />
            </label>

            {/* ── Agente PROFECO (combobox) ── */}
            <label>
              Agente que emite:
              {loadingAgentes ? (
                <Loader label="Cargando agentes…" />
              ) : (
                <select
                  required
                  value={form.emitidoPorUsuarioId}
                  onChange={(e) => setForm({ ...form, emitidoPorUsuarioId: e.target.value })}
                >
                  <option value="">Selecciona un agente</option>
                  {(agentes ?? []).map((a) => (
                    <option key={a.id} value={a.id}>
                      {a.nombre} {a.apellido} — {a.email}
                    </option>
                  ))}
                </select>
              )}
            </label>
          </div>

          <label>
            Descripción detallada:
            <textarea
              required rows={3}
              value={form.descripcion}
              onChange={(e) => setForm({ ...form, descripcion: e.target.value })}
            />
          </label>
          
          {okMsg && <div className="success-box">{okMsg}</div>}
          {errMsg && <div className="error-box">{errMsg}</div>}
          
          <button type="submit" disabled={procesando}>
            {procesando ? 'Procesando...' : 'Emitir Sanción'}
          </button>
        </form>
      </section>

      <section>
        <div className="filter-bar">
          <select
            value={estatusFiltro}
            onChange={(e) => setEstatusFiltro(e.target.value)}
          >
            <option value="">Todas las multas</option>
            <option value="PENDIENTE">Pendientes</option>
            <option value="PAGADA">Pagadas</option>
            <option value="APELACION">En Apelación</option>
            <option value="CANCELADA">Canceladas</option>
          </select>
        </div>

        {loading && <Loader/>}
        {error && <ErrorBox message={error}/>}
        
        {!loading && !error && (multas ?? []).length === 0 && (
          <EmptyState title="No se encontraron multas"/>
        )}

        {multas && multas.length > 0 && (
          <div className="table-wrap">
          <table className="precios-table">
            <thead>
              <tr>
                <th>Folio</th>
                <th>Emisión</th>
                <th>Comercio</th>
                <th>Motivo</th>
                <th>Monto</th>
                <th>Estatus</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {multas.map((m) => {
                const comercioInfo = comerciosMap[m.comercioId];
                const nombreComercio = comercioInfo?.nombreComercial ?? 'Cargando...';

                return (
                  <tr key={m.id}>
                    <td><strong>#{m.id}</strong></td>
                    <td className="muted small">{formatDate(m.fechaEmision)}</td>
                    <td>
                      {nombreComercio}<br/>
                      <span className="muted small">ID: {m.comercioId}</span>
                    </td>
                    <td><span className="chip">{m.motivo.replace(/_/g, ' ')}</span></td>
                    <td><strong>{formatMXN(m.monto)}</strong></td>
                    <td>
                      <span style={{ 
                        color: m.estatus === 'PAGADA' ? 'var(--c-success)' : 
                               m.estatus === 'PENDIENTE' ? 'var(--c-accent)' : 'var(--c-muted)'
                      }}>
                        {m.estatus}
                      </span>
                    </td>
                    <td>
                      {m.estatus === 'PENDIENTE' && (
                        <div className="acciones-container">
                          <button
                            type="button"
                            className="btn-action btn-success"
                            disabled={procesando}
                            onClick={() => cambiarEstatus(m.id, 'PAGADA')}
                          >
                            Marcar Pagada
                          </button>
                          
                          <button
                            type="button"
                            className="btn-action btn-primary"
                            disabled={procesando}
                            onClick={() => cambiarEstatus(m.id, 'APELACION')}
                          >
                            Apelación
                          </button>
                          
                          <button
                            type="button"
                            className="btn-action btn-danger"
                            disabled={procesando}
                            onClick={() => cambiarEstatus(m.id, 'CANCELADA')}
                          >
                            Cancelar multa
                          </button>
                        </div>
                      )}
                      
                      {m.estatus === 'APELACION' && (
                        <button
                          type="button"
                          className="btn-action btn-danger"
                          disabled={procesando}
                          onClick={() => cambiarEstatus(m.id, 'CANCELADA')}
                        >
                          Cancelar multa
                        </button>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
          </div>
        )}
      </section>
    </div>
  );
}