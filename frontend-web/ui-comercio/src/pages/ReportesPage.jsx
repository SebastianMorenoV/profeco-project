import { useState, useEffect } from 'react';
import { getReportesPorComercio } from '../api/reportes';

export default function ReportesPage({ comercioId }) {
  const [reportes, setReportes] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const cargarReportes = async () => {
      try {
        setCargando(true);
        const res = await getReportesPorComercio(comercioId);
        
        if (res.data && res.data.reportes) {
          setReportes(res.data.reportes);
        } else if (Array.isArray(res.data)) {
          setReportes(res.data);
        }
      } catch (err) {
        console.error("Error al cargar reportes:", err);
        setError("No se pudieron cargar los reportes de inconsistencias.");
      } finally {
        setCargando(false);
      }
    };
    cargarReportes();
  }, [comercioId]);

  if (cargando) return <div className="loader"><div className="spinner"></div> Cargando inconsistencias reportadas...</div>;
  if (error) return <div className="error-box"><p>{error}</p></div>;

  return (
    <div className="container">
      <div className="section-header" style={{flexDirection: 'column', alignItems: 'flex-start'}}>
        <h1 style={{color: 'var(--c-primary-dark)'}}>Inconsistencias Reportadas</h1>
        <p className="muted">Reportes realizados por los consumidores hacia tu comercio.</p>
      </div>
      
      {reportes.length === 0 ? (
        <p className="empty-state" style={{color: 'var(--c-success)', fontWeight: 'bold'}}>No tienes inconsistencias reportadas. ¡Mantén el buen servicio!</p>
      ) : (
        <div className="grid grid-3">
          {reportes.map((r, index) => (
            <div key={r.id || index} className="card">
              <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem'}}>
                <span className={`estatus estatus-${r.estatus ? r.estatus.toLowerCase() : 'pendiente'}`}>{r.estatus}</span>
                <span className="muted small">{r.fechaCreacion ? r.fechaCreacion.split('T')[0] : 'Fecha desconocida'}</span>
              </div>
              <h3 style={{fontSize: '1.125rem', fontWeight: 'bold', color: 'var(--c-primary-dark)', margin: '0 0 0.5rem 0'}}>{r.motivo.replace(/_/g, ' ')}</h3>
              <p style={{color: 'var(--c-text)', margin: 0, fontSize: '0.875rem'}}>{r.descripcion}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

