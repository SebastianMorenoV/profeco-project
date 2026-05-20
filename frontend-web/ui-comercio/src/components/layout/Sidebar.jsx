import { Link, useLocation } from 'react-router-dom';

export default function Sidebar({ onLogout }) {
  const location = useLocation();

  const isActive = (path) => location.pathname === path;

  return (
    <aside className="sidebar">
      <div className="sidebar-logo-container">
        <h2 className="sidebar-logo"> ProFeCo</h2>
        <span className="sidebar-badge">Portal Comercio</span>
      </div>
      
      <nav className="sidebar-nav">
        <Link to="/" className={`sidebar-link ${isActive('/') ? 'active' : ''}`}>
           Dashboard
        </Link>
        <Link to="/registrar-precios" className={`sidebar-link ${isActive('/registrar-precios') ? 'active' : ''}`}>
           Mis Precios
        </Link>
        <Link to="/ofertas" className={`sidebar-link ${isActive('/ofertas') ? 'active' : ''}`}>
         Ofertas
        </Link>
        <Link to="/resenias" className={`sidebar-link ${isActive('/resenias') ? 'active' : ''}`}>
         Comentarios y Calificaciones
        </Link>
        <Link to="/reportes" className={`sidebar-link ${isActive('/reportes') ? 'active' : ''}`}>
         Inconsistencias Reportadas
        </Link>
        <Link to="/multas" className={`sidebar-link ${isActive('/multas') ? 'active' : ''}`}>
         Multas de PROFECO
        </Link>
        <Link to="/perfil" className={`sidebar-link ${isActive('/perfil') ? 'active' : ''}`}>
         Mi Perfil
        </Link>
      </nav>

      <div style={{ marginTop: 'auto', padding: '1rem', borderTop: '1px solid var(--c-border)' }}>
        <button onClick={onLogout} className="sidebar-logout">
          Cerrar Sesión
        </button>
      </div>
    </aside>
  );
}
