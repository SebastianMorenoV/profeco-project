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

  if (cargando) return <div style={{ padding: '2rem' }}>Cargando información de multas...</div>;
  if (error) return <div style={{ padding: '2rem', color: 'red' }}>{error}</div>;

  return (
    <div>
      <h1 style={styles.title}>Multas Registradas</h1>
      <p style={styles.subtitle}>Historial de multas impuestas por PROFECO.</p>
      
      {multas.length === 0 ? (
        <p style={styles.noData}>No tienes multas registradas. ¡Excelente trabajo!</p>
      ) : (
        <div style={styles.tableContainer}>
          <table style={styles.table}>
            <thead>
              <tr>
                <th style={styles.th}>ID</th>
                <th style={styles.th}>Motivo</th>
                <th style={styles.th}>Monto</th>
                <th style={styles.th}>Estatus</th>
                <th style={styles.th}>Fecha Emisión</th>
              </tr>
            </thead>
            <tbody>
              {multas.map(m => (
                <tr key={m.id} style={styles.tr}>
                  <td style={styles.td}>#{m.id}</td>
                  <td style={styles.td}>
                    <strong>{m.motivo.replace(/_/g, ' ')}</strong>
                    <br/><span style={styles.desc}>{m.descripcion}</span>
                  </td>
                  <td style={{...styles.td, color: '#ef4444', fontWeight: 'bold'}}>${m.monto?.toFixed(2)}</td>
                  <td style={styles.td}>
                    <span style={styles.badge(m.estatus)}>{m.estatus}</span>
                  </td>
                  <td style={styles.td}>{m.fechaEmision ? m.fechaEmision.split('T')[0] : 'N/A'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

const styles = {
  title: { fontSize: '1.875rem', fontWeight: 'bold', color: '#111827', margin: '0 0 0.5rem 0' },
  subtitle: { color: '#6b7280', marginBottom: '2rem' },
  tableContainer: { overflowX: 'auto', backgroundColor: '#fff', borderRadius: '0.75rem', border: '1px solid #e5e7eb' },
  table: { width: '100%', borderCollapse: 'collapse', textAlign: 'left' },
  th: { backgroundColor: '#f9fafb', padding: '1rem', fontWeight: '600', color: '#4b5563', borderBottom: '1px solid #e5e7eb' },
  tr: { borderBottom: '1px solid #e5e7eb' },
  td: { padding: '1rem', color: '#111827', verticalAlign: 'top' },
  desc: { fontSize: '0.875rem', color: '#6b7280' },
  noData: { color: '#10b981', fontStyle: 'italic', fontWeight: 'bold' },
  badge: (estatus) => {
    let bg = '#f3f4f6', color = '#374151';
    if (estatus === 'PENDIENTE') { bg = '#fef3c7'; color = '#d97706'; }
    if (estatus === 'PAGADA') { bg = '#d1fae5'; color = '#059669'; }
    if (estatus === 'APELACION') { bg = '#dbeafe'; color = '#2563eb'; }
    if (estatus === 'CANCELADA') { bg = '#f3f4f6'; color = '#6b7280'; }
    return {
      padding: '0.25rem 0.5rem', borderRadius: '9999px', fontSize: '0.75rem', fontWeight: 'bold',
      backgroundColor: bg, color: color, display: 'inline-block'
    };
  }
};
