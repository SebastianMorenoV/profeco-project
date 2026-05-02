import { NavLink, Outlet } from 'react-router-dom';

export function Layout() {
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
