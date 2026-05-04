import { Link, useLocation } from 'react-router-dom';

export default function Sidebar() {
  const location = useLocation();

  const isActive = (path) => location.pathname === path;

  return (
    <aside style={styles.sidebar}>
      <div style={styles.logoContainer}>
        <h2 style={styles.logo}> ProFeCo</h2>
        <span style={styles.badge}>Portal Comercio</span>
      </div>
      
      <nav style={styles.nav}>
        <Link to="/" style={{...styles.link, ...(isActive('/') ? styles.activeLink : {})}}>
           Dashboard
        </Link>
        <Link to="/precios" style={{...styles.link, ...(isActive('/precios') ? styles.activeLink : {})}}>
           Mis Precios
        </Link>
        <Link to="/ofertas" style={{...styles.link, ...(isActive('/ofertas') ? styles.activeLink : {})}}>
         Ofertas
        </Link>
        <Link to="/perfil" style={{...styles.link, ...(isActive('/perfil') ? styles.activeLink : {})}}>
         Mi Perfil
        </Link>
      </nav>
    </aside>
  );
}

const styles = {
  sidebar: {
    width: '260px',
    backgroundColor: '#ffffff',
    borderRight: '1px solid #e5e7eb',
    height: '100vh',
    display: 'flex',
    flexDirection: 'column',
    position: 'fixed', 
    top: 0,
    left: 0,
  },
  logoContainer: {
    padding: '2rem 1.5rem',
    borderBottom: '1px solid #f3f4f6',
    marginBottom: '1rem',
  },
  logo: {
    margin: 0,
    fontSize: '1.5rem',
    fontWeight: 'bold',
    color: '#111827',
  },
  badge: {
    fontSize: '0.75rem',
    color: '#6b7280',
    textTransform: 'uppercase',
    letterSpacing: '0.05em',
    fontWeight: '600',
  },
  nav: {
    display: 'flex',
    flexDirection: 'column',
    padding: '0 1rem',
    gap: '0.5rem',
  },
  link: {
    textDecoration: 'none',
    color: '#4b5563',
    fontWeight: '500',
    padding: '0.75rem 1rem',
    borderRadius: '0.5rem',
    transition: 'background-color 0.2s',
  },
  activeLink: {
    backgroundColor: '#f3f4f6',
    color: '#111827',
    fontWeight: '600',
  }
};