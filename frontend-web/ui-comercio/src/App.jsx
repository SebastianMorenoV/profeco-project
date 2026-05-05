import { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Sidebar from './components/layout/Sidebar';
import DashboardPage from './pages/DashboardPage';
import CrearOfertasPage from './pages/CrearOfertasPage';
import LoginPage from './pages/LoginPage';
import PerfilPage from './pages/PerfilPage';
import ReseniasPage from './pages/ReseniasPage';
import RegistrarPreciosPage from './pages/RegistrarPreciosPage';
import MultasPage from './pages/MultasPage';
import ReportesPage from './pages/ReportesPage';

function App() {
  // Estado para saber si el usuario está logueado
  const [comercio, setComercio] = useState(null);

  // Al cargar la app, revisamos si ya había un inicio de sesión guardado en el navegador
  useEffect(() => {
    const sesionGuardada = localStorage.getItem('comercio_sesion');
    if (sesionGuardada) {
      setComercio(JSON.parse(sesionGuardada));
    }
  }, []);

  // Función que se ejecuta cuando el Login es exitoso
  const manejarLogin = (datosComercio) => {
    setComercio(datosComercio);
    localStorage.setItem('comercio_sesion', JSON.stringify(datosComercio)); // Guardamos en memoria
  };

  // Función para cerrar sesión (puedes ponérsela a un botón en el Sidebar después)
  const cerrarSesion = () => {
  setComercio(null);
  localStorage.removeItem('comercio_sesion');
  // Opcional: limpiar también los precios/ofertas locales si quieres un reset total
  localStorage.removeItem('precios_simulados');
  localStorage.removeItem('ofertas_simuladas');
};

  // Si no hay comercio logueado, le mostramos SOLO la pantalla de Login
  if (!comercio) {
    return <LoginPage onLogin={manejarLogin} />;
  }

  // Si ya se logueó, le mostramos la aplicación completa
  return (
    <Router>
      <div style={styles.appContainer}>
        <Sidebar onLogout={cerrarSesion} /> 
        
        <main style={styles.mainContent}>
          <div style={styles.pageWrapper}>
            <Routes>
              <Route path="/" element={<DashboardPage comercioId={comercio.id} />} />
              <Route path="/registrar-precios" element={<RegistrarPreciosPage comercioId={comercio.id} />} />
              <Route path="/ofertas" element={<CrearOfertasPage comercioId={comercio.id} />} />
              <Route path="/perfil" element={<PerfilPage comercioId={comercio.id} onLogout={cerrarSesion} />} />
              <Route path="/resenias" element={<ReseniasPage comercioId={comercio.id} />} />
              <Route path="/multas" element={<MultasPage comercioId={comercio.id} />} />
              <Route path="/reportes" element={<ReportesPage comercioId={comercio.id} />} />
              {/* Cualquier ruta inventada lo regresa al inicio */}
              <Route path="*" element={<Navigate to="/" />} />
            </Routes>
          </div>
        </main>
      </div>
    </Router>
  );
}

const styles = {
  appContainer: {
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif',
    backgroundColor: '#f9fafb',
    minHeight: '100vh',
    display: 'flex', 
  },
  mainContent: {
    flex: 1,
    marginLeft: '260px', 
    padding: '2rem',
  },
  pageWrapper: {
    maxWidth: '1000px', 
    margin: '0 auto',
  }
};

export default App;