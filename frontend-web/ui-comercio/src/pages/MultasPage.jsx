import { useState, useEffect } from 'react';
import { getMultasPorComercio } from '../api/multas';

export default function MultasPage({ comercioId }) {
  const [multas, setMultas] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const cargarMultas = async () => {
      try {
        setCargando(true);
        const res = await getMultasPorComercio(comercioId);
        
        if (res.data && res.data.multas) {
          setMultas(res.data.multas);
        } else if (Array.isArray(res.data)) {
          setMultas(res.data);
        }
      } catch (err) {
        console.error("Error al cargar multas:", err);
        setError("No se pudieron cargar las multas.");
      } finally {
        setCargando(false);
      }
    };
    cargarMultas();
  }, [comercioId]);

  if (cargando) return <div className="loader"><div className="spinner"></div> Cargando información de multas...</div>;
  if (error) return <div className="error-box"><p>{error}</p></div>;

  return (
    <div className="container">
      <div className="section-header" style={{flexDirection: 'column', alignItems: 'flex-start'}}>
        <h1 style={{color: 'var(--c-primary-dark)'}}>Multas Registradas</h1>
        <p className="muted">Historial de multas impuestas por PROFECO.</p>
      </div>
      
      {multas.length === 0 ? (
        <p className="empty-state" style={{color: 'var(--c-success)', fontWeight: 'bold'}}>No tienes multas registradas. ¡Excelente trabajo!</p>
      ) : (
        <div className="card">
          <div style={{overflowX: 'auto'}}>
            <table className="precios-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Motivo</th>
                  <th>Monto</th>
                  <th>Estatus</th>
                  <th>Fecha Emisión</th>
                </tr>
              </thead>
              <tbody>
                {multas.map(m => (
                  <tr key={m.id}>
                    <td className="muted small">#{m.id}</td>
                    <td>
                      <strong style={{color: 'var(--c-text)'}}>{m.motivo.replace(/_/g, ' ')}</strong>
                      <br/><span className="muted small">{m.descripcion}</span>
                    </td>
                    <td style={{color: 'var(--c-danger)', fontWeight: 'bold'}}>${m.monto?.toFixed(2)}</td>
                    <td>
                      <span className={`estatus estatus-${m.estatus ? m.estatus.toLowerCase() : 'pendiente'}`}>{m.estatus}</span>
                    </td>
                    <td>{m.fechaEmision ? m.fechaEmision.split('T')[0] : 'N/A'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}

