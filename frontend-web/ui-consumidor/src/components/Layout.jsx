import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useUser } from '../context/UserContext';

export function Layout() {
  const { nombre, apellido, email, cerrarSesion } = useUser();
  const navigate = useNavigate();

  const salir = () => {
    cerrarSesion();
    navigate('/login', { replace: true });
  };

  const nombreCompleto = [nombre, apellido].filter(Boolean).join(' ').trim();
  const etiquetaUsuario = nombreCompleto || email || 'Mi cuenta';

  return (
    <div className="app">
      <header className="app-header">
        <div className="container header-inner">
          <NavLink to="/" className="brand">
            <span className="brand-mark">P</span>
            <span className="brand-text">
              <strong>PROFECO</strong>
              <span>Compara, evalúa, denuncia</span>
            </span>
          </NavLink>
          <nav className="main-nav">
            <NavLink to="/productos">Productos</NavLink>
            <NavLink to="/comercios">Comercios</NavLink>
            <NavLink to="/ofertas">Ofertas</NavLink>
            <NavLink to="/reportar">Reportar</NavLink>
            <NavLink to="/lista-compras">Lista</NavLink>
            <NavLink to="/perfil" className="nav-cta">{etiquetaUsuario}</NavLink>
            <button type="button" className="nav-logout" onClick={salir}>
              Salir
            </button>
          </nav>
        </div>
      </header>

      <main className="container app-main">
        <Outlet />
      </main>

      <footer className="app-footer">
        <div className="container">
          <p>Plataforma del Consumidor · Procuraduría Federal del Consumidor</p>
          <p className="muted">Datos públicos, transparencia para todos.</p>
        </div>
      </footer>
    </div>
  );
}
