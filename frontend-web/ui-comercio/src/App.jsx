import { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import './App.css';
import { tokenStore } from './api/client';
import { getComercios } from './api/comercio';
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
  const [comercioIdReal, setComercioIdReal] = useState(null);

  // Al cargar la app, revisamos si ya había un inicio de sesión guardado en el navegador
  useEffect(() => {
    const sesionGuardada = localStorage.getItem('comercio_sesion');
    if (sesionGuardada) {
      const parsed = JSON.parse(sesionGuardada);
      setComercio(parsed);
      cargarComercioReal(parsed.id);
    }
  }, []);

  const cargarComercioReal = async (usuarioId) => {
    try {
      const res = await getComercios();
      const listaComercios = res.data.comercios || (Array.isArray(res.data) ? res.data : []);
      const miComercio = listaComercios.find(c => c.idPropietario === usuarioId || c.id_propietario === usuarioId);
      if (miComercio) {
        setComercioIdReal(miComercio.id);
      } else {
        setComercioIdReal(usuarioId); // Fallback
      }
    } catch (e) {
      console.error("No se pudo obtener el comercio real:", e);
      setComercioIdReal(usuarioId); // Fallback
    }
  };

  // Función que se ejecuta cuando el Login es exitoso
  const manejarLogin = (datosComercio) => {
    setComercio(datosComercio);
    localStorage.setItem('comercio_sesion', JSON.stringify(datosComercio)); // Guardamos en memoria
    cargarComercioReal(datosComercio.id);
  };

  // Función para cerrar sesión (puedes ponérsela a un botón en el Sidebar después)
  const cerrarSesion = () => {
    setComercio(null);
    setComercioIdReal(null);
    localStorage.removeItem('comercio_sesion');
    tokenStore.clear();
    // Opcional: limpiar también los precios/ofertas locales si quieres un reset total
    localStorage.removeItem('precios_simulados');
    localStorage.removeItem('ofertas_simuladas');
  };

  // Si no hay comercio logueado, le mostramos SOLO la pantalla de Login
  if (!comercio) {
    return <LoginPage onLogin={manejarLogin} />;
  }

  // Esperar a resolver el ID real del comercio
  if (!comercioIdReal) {
    return <div style={{padding: '2rem'}}>Cargando portal del comercio...</div>;
  }

  // Si ya se logueó, le mostramos la aplicación completa
  return (
    <Router>
      <div className="app-container">
        <Sidebar onLogout={cerrarSesion} /> 
        
        <main className="main-content">
          <div className="page-wrapper">
            <Routes>
              <Route path="/" element={<DashboardPage comercioId={comercioIdReal} />} />
              <Route path="/registrar-precios" element={<RegistrarPreciosPage comercioId={comercioIdReal} />} />
              <Route path="/ofertas" element={<CrearOfertasPage comercioId={comercioIdReal} />} />
              <Route path="/perfil" element={<PerfilPage comercioId={comercioIdReal} onLogout={cerrarSesion} />} />
              <Route path="/resenias" element={<ReseniasPage comercioId={comercioIdReal} />} />
              <Route path="/multas" element={<MultasPage comercioId={comercioIdReal} />} />
              <Route path="/reportes" element={<ReportesPage comercioId={comercioIdReal} />} />
              {/* Cualquier ruta inventada lo regresa al inicio */}
              <Route path="*" element={<Navigate to="/" />} />
            </Routes>
          </div>
        </main>
      </div>
    </Router>
  );
}

export default App;