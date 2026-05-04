import { Link } from 'react-router-dom';

export default function Navbar() {
  return (
    <nav style={styles.navbar}>
      <div style={styles.logo}>🛒 Comercio ProFeCo</div>
      <div style={styles.navLinks}>
        <Link to="/" style={styles.link}>Inicio</Link>
        <Link to="/precios" style={styles.link}>Mis Precios</Link>
        <Link to="/ofertas" style={styles.link}>Ofertas</Link>
      </div>
    </nav>
  );
}

const styles = {
  navbar: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '1rem 2rem',
    backgroundColor: '#ffffff',
    borderBottom: '1px solid #e5e7eb',
    boxShadow: '0 1px 2px 0 rgba(0, 0, 0, 0.05)',
  },
  logo: {
    fontSize: '1.25rem',
    fontWeight: 'bold',
    color: '#111827',
  },
  navLinks: {
    display: 'flex',
    gap: '1.5rem',
  },
  link: {
    textDecoration: 'none',
    color: '#4b5563',
    fontWeight: '500',
  }
};