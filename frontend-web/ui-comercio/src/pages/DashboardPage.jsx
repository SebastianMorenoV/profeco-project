import { useEffect, useState } from 'react';
import { getProductos, getPreciosPorComercio } from '../api/catalogo';
import { getOfertasComercio as getOfertas } from '../api/comercio';
import { useNavigate } from 'react-router-dom';

export default function DashboardPage({ comercioId }) {
  const [productos, setProductos] = useState([]);
  const [stats, setStats] = useState({ totalProd: 0, misPrecios: 0, ofertasActivas: 0, wishlists: 0 });
  const [cargando, setCargando] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const cargarDashboard = async () => {
      try {
        // 1. Cargar catálogo global (solo para stats o cruzar datos)
        const resP = await getProductos();
        const listaP = Array.isArray(resP.data) ? resP.data : (resP.data.productos || []);

        // 2. Cargar mis precios reales
        const resPrecios = await getPreciosPorComercio(comercioId);
        const misPreciosList = resPrecios.data.precios || resPrecios.data || [];
        
        // Mapear los precios para tener el nombre del producto
        const misProductosEnVenta = misPreciosList.map(precio => {
          const prodInfo = listaP.find(p => p.id === precio.productoId);
          return {
            ...precio,
            nombreProducto: prodInfo ? prodInfo.nombre : `Producto #${precio.productoId}`
          };
        });

        setProductos(misProductosEnVenta.slice(0, 5)); // Mostrar los últimos 5 de mis precios

        // 3. Cargar ofertas reales del backend
        const resO = await getOfertas(comercioId);
        const listaO = resO.data.ofertas || [];

        // Simulación: Productos en wishlist
        const wishlistAprox = Math.floor(Math.random() * 50) + 10;

        // Calcular estadísticas
        setStats({
          totalProd: listaP.length,
          misPrecios: misPreciosList.length,
          ofertasActivas: listaO.length,
          wishlists: wishlistAprox
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
        {/* Se quitó la estadística del Catálogo Global porque al comercio solo le interesan sus propios datos */}
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
        <div style={{...styles.statCard, borderLeft: '4px solid #10b981'}}>
          <span style={{...styles.statLabel, color: '#10b981'}}>Favoritos (Wishlists)</span>
          <h2 style={{...styles.statValue, color: '#10b981'}}>{stats.wishlists}</h2>
          <span style={styles.statDesc}>Veces que tus productos fueron guardados</span>
        </div>
      </div>

      {/* LISTA DE MIS PRECIOS RECIENTES */}
      <div style={styles.card}>
        <div style={styles.cardHeader}>
          <h2 style={styles.cardTitle}>Tus Precios Registrados</h2>
          <button onClick={() => navigate('/registrar-precios')} style={styles.viewAll}>Ver y editar todos</button>
        </div>
        
        <div style={styles.list}>
          {productos.length === 0 ? (
             <div style={{padding: '1.5rem', color: '#6b7280'}}>No tienes productos a la venta aún.</div>
          ) : (
            productos.map((prod) => (
              <div key={prod.id} style={styles.listItem}>
                <div>
                  <div style={styles.prodName}>{prod.nombreProducto}</div>
                  <div style={styles.prodId}>ID de Precio: {prod.id}</div>
                </div>
                <span style={{fontWeight: 'bold', color: '#10b981'}}>
                  ${prod.precio?.toFixed(2)}
                </span>
              </div>
            ))
          )}
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