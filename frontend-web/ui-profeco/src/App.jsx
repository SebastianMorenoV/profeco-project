import { useState, useEffect } from 'react';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { Layout } from './pages/Layout';
import { LoginPage } from './pages/LoginPage';
import { ReportesPage } from './pages/ReportesPage';
import { MultasPage } from './pages/MultasPage';
import { ComerciosPage } from './pages/ComerciosPage';
import { ProductosPage } from './pages/ProductosPage';
import { UsuariosPage } from './pages/UsuariosPage';
import { CategoriasPage } from './pages/CategoriasPage';

const SESSION_KEY = 'profeco_sesion';

export default function App() {
  const [usuario, setUsuario] = useState(null);

  // Al cargar, revisar si ya había sesión guardada
  useEffect(() => {
    const sesionGuardada = localStorage.getItem(SESSION_KEY);
    if (sesionGuardada) {
      try {
        setUsuario(JSON.parse(sesionGuardada));
      } catch { /* ignore */ }
    }
  }, []);

  const manejarLogin = (datosUsuario) => {
    setUsuario(datosUsuario);
    localStorage.setItem(SESSION_KEY, JSON.stringify(datosUsuario));
  };

  const cerrarSesion = () => {
    setUsuario(null);
    localStorage.removeItem(SESSION_KEY);
  };

  // Si no hay usuario logueado, mostrar solo Login
  if (!usuario) {
    return <LoginPage onLogin={manejarLogin} />;
  }

  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout usuario={usuario} onLogout={cerrarSesion} />}>
          <Route index element={<Navigate to="/reportes" replace />} />
          <Route path="reportes" element={<ReportesPage />} />
          <Route path="multas" element={<MultasPage />} />
          <Route path="comercios" element={<ComerciosPage />} />
          <Route path="productos" element={<ProductosPage />} />
          <Route path="usuarios" element={<UsuariosPage />} />
          <Route path="categorias" element={<CategoriasPage />} />
          <Route path="*" element={<Navigate to="/reportes" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}