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

  if (cargando) return <div style={{ padding: '2rem' }}>Cargando inconsistencias reportadas...</div>;
  if (error) return <div style={{ padding: '2rem', color: 'red' }}>{error}</div>;

  return (
    <div>
      <h1 style={styles.title}>Inconsistencias Reportadas</h1>
      <p style={styles.subtitle}>Reportes realizados por los consumidores hacia tu comercio.</p>
      
      {reportes.length === 0 ? (
        <p style={styles.noData}>No tienes inconsistencias reportadas. ¡Mantén el buen servicio!</p>
      ) : (
        <div style={styles.grid}>
          {reportes.map((r, index) => (
            <div key={r.id || index} style={styles.card}>
              <div style={styles.header}>
                <span style={styles.badge(r.estatus)}>{r.estatus}</span>
                <span style={styles.date}>{r.fechaCreacion ? r.fechaCreacion.split('T')[0] : 'Fecha desconocida'}</span>
              </div>
              <h3 style={styles.motivo}>{r.motivo.replace(/_/g, ' ')}</h3>
              <p style={styles.desc}>{r.descripcion}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

const styles = {
  title: { fontSize: '1.875rem', fontWeight: 'bold', color: '#111827', margin: '0 0 0.5rem 0' },
  subtitle: { color: '#6b7280', marginBottom: '2rem' },
  grid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '1.5rem' },
  card: { backgroundColor: '#fff', padding: '1.5rem', borderRadius: '0.75rem', border: '1px solid #e5e7eb', boxShadow: '0 1px 2px rgba(0,0,0,0.05)' },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' },
  motivo: { fontSize: '1.125rem', fontWeight: 'bold', color: '#111827', margin: '0 0 0.5rem 0' },
  desc: { color: '#4b5563', margin: 0, fontSize: '0.875rem' },
  date: { fontSize: '0.75rem', color: '#9ca3af' },
  noData: { color: '#10b981', fontStyle: 'italic', fontWeight: 'bold' },
  badge: (estatus) => {
    let bg = '#f3f4f6', color = '#374151';
    if (estatus === 'PENDIENTE' || estatus === 'EN_REVISION') { bg = '#fef3c7'; color = '#d97706'; }
    if (estatus === 'RESUELTA_SIN_MULTA' || estatus === 'RECHAZADA') { bg = '#d1fae5'; color = '#059669'; }
    if (estatus === 'RESUELTA_CON_MULTA') { bg = '#fee2e2'; color = '#dc2626'; }
    return {
      padding: '0.25rem 0.5rem', borderRadius: '9999px', fontSize: '0.75rem', fontWeight: 'bold',
      backgroundColor: bg, color: color, display: 'inline-block'
    };
  }
};
