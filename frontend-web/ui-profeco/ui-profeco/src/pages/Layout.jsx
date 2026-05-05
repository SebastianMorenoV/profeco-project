import { NavLink, Outlet } from 'react-router-dom';

export function Layout({ usuario, onLogout }) {
  return (
    <div className="app">
      <header className="app-header">
        <div className="container header-inner">
          <NavLink to="/" className="brand">
            <span className="brand-mark">P</span>
            <span className="brand-text">
              <strong>PROFECO</strong>
              <span>Panel Administrativo</span>
            </span>
          </NavLink>
          <nav className="main-nav">
            <NavLink to="/reportes">Reportes</NavLink>
            <NavLink to="/multas">Multas</NavLink>
            <NavLink to="/comercios">Comercios</NavLink>
            <NavLink to="/usuarios">Usuarios</NavLink>
            <NavLink to="/categorias">Categorías</NavLink>
          </nav>
          {usuario && (
            <div className="user-info">
              <span className="user-name">{usuario.nombre} {usuario.apellido}</span>
              <button type="button" className="btn-logout" onClick={onLogout}>
                Cerrar sesión
              </button>
            </div>
          )}
        </div>
      </header>
      <main className="container app-main">
        <Outlet/>
      </main>
      <footer className="app-footer">
        <div className="container">
          <p>Plataforma Administrativa - Procuraduría Federal del Consumidor</p>
        </div>
      </footer>
    </div>
  );
}