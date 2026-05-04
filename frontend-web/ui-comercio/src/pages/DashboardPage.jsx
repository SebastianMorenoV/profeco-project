import { useEffect, useState } from 'react';
import { getProductos, getOfertasComercio } from '../api/comercio';
import { useNavigate } from 'react-router-dom';

export default function DashboardPage() {
  const [productos, setProductos] = useState([]);
  const [stats, setStats] = useState({ totalProd: 0, misPrecios: 0, ofertasActivas: 0 });
  const [cargando, setCargando] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const cargarDashboard = async () => {
      try {
        // 1. Cargar productos del catálogo real
        const resP = await getProductos();
        const listaP = Array.isArray(resP.data) ? resP.data : (resP.data.productos || []);
        setProductos(listaP.slice(0, 5)); // Solo mostramos los últimos 5

        // 2. Cargar precios de la memoria local
        const preciosGuardados = JSON.parse(localStorage.getItem('precios_simulados') || '[]');
        
        // 3. Cargar ofertas reales del backend
        const resO = await getOfertasComercio(1);
        const listaO = resO.data.ofertas || [];

        // Calcular estadísticas
        setStats({
          totalProd: listaP.length,
          misPrecios: preciosGuardados.length,
          ofertasActivas: listaO.length
        });

      } catch (error) {
        console.error("Error al cargar dashboard:", error);
      } finally {
        setCargando(false);
      }
    };
    cargarDashboard();
  }, []);

  if (cargando) return <div style={styles.loading}>Cargando panel de control...</div>;

  return (
    <div>
      <h1 style={styles.title}>Panel de Control</h1>
      <p style={styles.subtitle}>Bienvenido de nuevo al portal de administración de ProFeCo.</p>

      {/* TARJETAS DE ESTADÍSTICAS */}
      <div style={styles.statsGrid}>
        <div style={styles.statCard}>
          <span style={styles.statLabel}>Catálogo Total</span>
          <h2 style={styles.statValue}>{stats.totalProd}</h2>
          <span style={styles.statDesc}>Productos en sistema</span>
        </div>
        <div style={styles.statCard}>
          <span style={styles.statLabel}>Tus Precios</span>
          <h2 style={styles.statValue}>{stats.misPrecios}</h2>
          <span style={styles.statDesc}>Artículos con precio fijo</span>
        </div>
        <div style={styles.statCard}>
          <span style={{...styles.statLabel, color: '#2563eb'}}>Ofertas Activas</span>
          <h2 style={{...styles.statValue, color: '#2563eb'}}>{stats.ofertasActivas}</h2>
          <span style={styles.statDesc}>Promociones publicadas</span>
        </div>
      </div>

      {/* LISTA DE PRODUCTOS RECIENTES */}
      <div style={styles.card}>
        <div style={styles.cardHeader}>
          <h2 style={styles.cardTitle}>Productos Recientes en el Sistema</h2>
          <button onClick={() => navigate('/precios')} style={styles.viewAll}>Ver catálogo completo</button>
        </div>
        
        <div style={styles.list}>
          {productos.map((prod) => (
            <div key={prod.id} style={styles.listItem}>
              <div>
                <div style={styles.prodName}>{prod.nombre}</div>
                <div style={styles.prodId}>ID: {prod.id}</div>
              </div>
              <span 
                style={styles.itemAction} 
                onClick={() => navigate('/precios')}
              >
                Registrar Precio &rarr;
              </span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

const styles = {
  title: { fontSize: '1.875rem', fontWeight: 'bold', color: '#111827', marginBottom: '0.5rem' },
  subtitle: { color: '#6b7280', marginBottom: '2.5rem' },
  loading: { padding: '3rem', textAlign: 'center', color: '#6b7280' },
  
  // Grid de estadísticas
  statsGrid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '1.5rem', marginBottom: '2.5rem' },
  statCard: { backgroundColor: '#fff', padding: '1.5rem', borderRadius: '0.75rem', border: '1px solid #e5e7eb', boxShadow: '0 1px 2px rgba(0,0,0,0.05)' },
  statLabel: { fontSize: '0.875rem', fontWeight: '600', color: '#6b7280', textTransform: 'uppercase', letterSpacing: '0.025em' },
  statValue: { fontSize: '2.25rem', fontWeight: 'bold', color: '#111827', margin: '0.5rem 0' },
  statDesc: { fontSize: '0.875rem', color: '#9ca3af' },

  // Tarjeta de Lista
  card: { backgroundColor: '#fff', borderRadius: '0.75rem', border: '1px solid #e5e7eb', overflow: 'hidden' },
  cardHeader: { padding: '1.5rem', borderBottom: '1px solid #f3f4f6', display: 'flex', justifyContent: 'space-between', alignItems: 'center' },
  cardTitle: { fontSize: '1.125rem', fontWeight: '600', color: '#111827', margin: 0 },
  viewAll: { background: 'none', border: 'none', color: '#2563eb', fontWeight: '500', cursor: 'pointer', fontSize: '0.875rem' },
  
  list: { display: 'flex', flexDirection: 'column' },
  listItem: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '1.25rem 1.5rem', borderBottom: '1px solid #f3f4f6' },
  prodName: { fontWeight: '500', color: '#111827' },
  prodId: { fontSize: '0.75rem', color: '#9ca3af' },
  itemAction: { color: '#2563eb', fontSize: '0.875rem', fontWeight: '600', cursor: 'pointer' }
};