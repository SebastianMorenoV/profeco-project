import { useState, useEffect } from 'react';
import { getReseniasPorComercio, getPromedioComercio } from '../api/resenias';

export default function ReseniasPage({ comercioId }) {
  const [resenias, setResenias] = useState([]);
  const [stats, setStats] = useState({ promedio: 0, totalResenias: 0 });
  const [cargando, setCargando] = useState(true);

  useEffect(() => {
    const cargarDatos = async () => {
      try {
        // Hacemos las dos peticiones al mismo tiempo para que cargue más rápido
        const [resResenias, resPromedio] = await Promise.all([
          getReseniasPorComercio(comercioId),
          getPromedioComercio(comercioId)
        ]);

        // Verificamos cómo llega la envoltura de los datos
        if (resResenias.data.resenias) {
          setResenias(resResenias.data.resenias);
        } else if (Array.isArray(resResenias.data)) {
          setResenias(resResenias.data);
        }

        if (resPromedio.data) {
          setStats({
            promedio: resPromedio.data.promedio || 0,
            totalResenias: resPromedio.data.totalResenias || 0
          });
        }
      } catch (error) {
        console.error("Error al cargar reseñas:", error);
      } finally {
        setCargando(false);
      }
    };

    cargarDatos();
  }, [comercioId]);

  // Función auxiliar para dibujar las estrellas
  const renderEstrellas = (calificacion) => {
    const maxEstrellas = 5;
    return "★".repeat(calificacion) + "☆".repeat(maxEstrellas - calificacion);
  };

  if (cargando) return <div style={{ padding: '2rem' }}>Cargando comentarios de los consumidores...</div>;

  return (
    <div>
      <h1 style={styles.title}>Comentarios y Calificaciones</h1>
      
      {/* Tarjeta de Resumen */}
      <div style={styles.summaryCard}>
        <div style={styles.promedioWrap}>
          <span style={styles.promedioNumero}>{stats.promedio.toFixed(1)}</span>
          <span style={styles.estrellasGrandes}>{renderEstrellas(Math.round(stats.promedio))}</span>
        </div>
        <p style={styles.totalText}>Basado en {stats.totalResenias} calificaciones</p>
      </div>

      <div style={styles.divider}></div>

      {/* Lista de Reseñas */}
      {resenias.length === 0 ? (
        <p style={styles.noData}>Aún no hay reseñas registradas para este comercio.</p>
      ) : (
        <div style={styles.grid}>
          {resenias.map((r, index) => (
            <div key={r.id || index} style={styles.reviewCard}>
              <div style={styles.reviewHeader}>
                <div style={styles.avatar}>U</div>
                <div>
                  {/* Aquí usamos camelCase: usuarioId, fechaCreacion */}
                  <h3 style={styles.userName}>Usuario #{r.usuarioId}</h3>
                  <p style={styles.date}>{r.fechaCreacion ? r.fechaCreacion.split('T')[0] : 'Fecha desconocida'}</p>
                </div>
              </div>
              <div style={styles.stars}>{renderEstrellas(r.calificacion)}</div>
              <p style={styles.comment}>{r.comentario}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

const styles = {
  title: { fontSize: '1.875rem', fontWeight: 'bold', color: '#111827', margin: '0 0 1.5rem 0' },
  summaryCard: { backgroundColor: '#fff', padding: '1.5rem', borderRadius: '0.75rem', border: '1px solid #e5e7eb', maxWidth: '400px', textAlign: 'center', marginBottom: '2rem' },
  promedioWrap: { display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '1rem' },
  promedioNumero: { fontSize: '3rem', fontWeight: 'bold', color: '#111827' },
  estrellasGrandes: { fontSize: '2rem', color: '#fbbf24' },
  totalText: { color: '#6b7280', marginTop: '0.5rem' },
  divider: { border: '0', borderTop: '1px solid #e5e7eb', margin: '2rem 0' },
  grid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '1.5rem' },
  reviewCard: { backgroundColor: '#fff', padding: '1.5rem', borderRadius: '0.75rem', border: '1px solid #e5e7eb' },
  reviewHeader: { display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '1rem' },
  avatar: { width: '40px', height: '40px', backgroundColor: '#e5e7eb', color: '#4b5563', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 'bold' },
  userName: { margin: 0, fontWeight: '600', fontSize: '1rem', color: '#111827' },
  date: { margin: 0, fontSize: '0.75rem', color: '#9ca3af' },
  stars: { color: '#fbbf24', fontSize: '1.25rem', marginBottom: '0.5rem', letterSpacing: '2px' },
  comment: { margin: 0, color: '#4b5563', lineHeight: '1.5' },
  noData: { color: '#6b7280', fontStyle: 'italic' }
};