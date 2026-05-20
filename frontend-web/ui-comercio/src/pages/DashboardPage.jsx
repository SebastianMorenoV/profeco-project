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
        const listaP = resP.data.productos || (Array.isArray(resP.data) ? resP.data : []);

        // 2. Cargar mis precios reales
        const resPrecios = await getPreciosPorComercio(comercioId);
        const misPreciosList = resPrecios.data.precios || (Array.isArray(resPrecios.data) ? resPrecios.data : []);
        
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
        const listaO = resO.data.ofertas || (Array.isArray(resO.data) ? resO.data : []);

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

  if (cargando) return <div className="loader"><div className="spinner"></div> Cargando panel de control...</div>;

  return (
    <div className="container">
      <div className="section-header" style={{flexDirection: 'column', alignItems: 'flex-start'}}>
        <h1 style={{color: 'var(--c-primary-dark)'}}>Panel de Control</h1>
        <p className="muted">Bienvenido de nuevo al portal de administración de ProFeCo.</p>
      </div>

      {/* TARJETAS DE ESTADÍSTICAS */}
      <div className="info-grid" style={{marginBottom: '2rem'}}>
        <div className="info-card">
          <span className="muted small">TUS PRECIOS</span>
          <h2 style={{fontSize: '2.25rem', color: 'var(--c-primary-dark)', margin: '0.5rem 0'}}>{stats.misPrecios}</h2>
          <span className="muted small">Artículos con precio fijo</span>
        </div>
        <div className="info-card">
          <span className="small" style={{color: '#2563eb', fontWeight: 600}}>OFERTAS ACTIVAS</span>
          <h2 style={{fontSize: '2.25rem', color: '#2563eb', margin: '0.5rem 0'}}>{stats.ofertasActivas}</h2>
          <span className="muted small">Promociones publicadas</span>
        </div>
        <div className="info-card" style={{borderLeft: '4px solid var(--c-success)'}}>
          <span className="small" style={{color: 'var(--c-success)', fontWeight: 600}}>FAVORITOS (WISHLISTS)</span>
          <h2 style={{fontSize: '2.25rem', color: 'var(--c-success)', margin: '0.5rem 0'}}>{stats.wishlists}</h2>
          <span className="muted small">Veces que tus productos fueron guardados</span>
        </div>
      </div>

      {/* LISTA DE MIS PRECIOS RECIENTES */}
      <div className="card">
        <div className="section-header" style={{marginTop: 0}}>
          <h2 style={{fontSize: '1.25rem'}}>Tus Precios Registrados</h2>
          <button onClick={() => navigate('/registrar-precios')} className="btn-ghost">Ver y editar todos</button>
        </div>
        
        <div style={{display: 'flex', flexDirection: 'column'}}>
          {productos.length === 0 ? (
             <div className="empty-state">No tienes productos a la venta aún.</div>
          ) : (
            productos.map((prod) => (
              <div key={prod.id} style={{display: 'flex', justifyContent: 'space-between', padding: '1rem 0', borderBottom: '1px solid var(--c-border)'}}>
                <div>
                  <div style={{fontWeight: '600'}}>{prod.nombreProducto}</div>
                  <div className="muted small">ID de Precio: {prod.id}</div>
                </div>
                <span style={{fontWeight: 'bold', color: 'var(--c-success)', fontSize: '1.1rem'}}>
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
