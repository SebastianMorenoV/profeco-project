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

  if (cargando) return <div className="loader"><div className="spinner"></div> Cargando comentarios de los consumidores...</div>;

  return (
    <div className="container">
      <div className="section-header">
        <h1 style={{color: 'var(--c-primary-dark)'}}>Comentarios y Calificaciones</h1>
      </div>
      
      {/* Tarjeta de Resumen */}
      <div className="card" style={{maxWidth: '400px', textAlign: 'center', marginBottom: '2rem'}}>
        <div style={{display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '1rem'}}>
          <span style={{fontSize: '3rem', fontWeight: 'bold', color: 'var(--c-primary-dark)'}}>{stats.promedio.toFixed(1)}</span>
          <span style={{fontSize: '2rem', color: '#fbbf24'}}>{renderEstrellas(Math.round(stats.promedio))}</span>
        </div>
        <p className="muted">Basado en {stats.totalResenias} calificaciones</p>
      </div>

      <hr style={{ border: '0', borderTop: '1px solid var(--c-border)', margin: '2rem 0' }} />

      {/* Lista de Reseñas */}
      {resenias.length === 0 ? (
        <p className="empty-state">Aún no hay reseñas registradas para este comercio.</p>
      ) : (
        <div className="grid grid-3">
          {resenias.map((r, index) => (
            <div key={r.id || index} className="card">
              <div style={{display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '1rem'}}>
                <div className="brand-mark" style={{width: '40px', height: '40px', fontSize: '1.2rem'}}>U</div>
                <div>
                  {/* Aquí usamos camelCase: usuarioId, fechaCreacion */}
                  <h3 style={{margin: 0, fontWeight: 600, fontSize: '1rem'}}>Usuario #{r.usuarioId}</h3>
                  <p className="muted small" style={{margin: 0}}>{r.fechaCreacion ? r.fechaCreacion.split('T')[0] : 'Fecha desconocida'}</p>
                </div>
              </div>
              <div style={{color: '#fbbf24', fontSize: '1.25rem', marginBottom: '0.5rem', letterSpacing: '2px'}}>{renderEstrellas(r.calificacion)}</div>
              <p style={{margin: 0, color: 'var(--c-text)', lineHeight: 1.5}}>{r.comentario}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
