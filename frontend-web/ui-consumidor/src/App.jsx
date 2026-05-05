import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { Layout } from './components/Layout';
import { HomePage } from './pages/HomePage';
import { ProductosPage } from './pages/ProductosPage';
import { ProductoDetallePage } from './pages/ProductoDetallePage';
import { ComerciosPage } from './pages/ComerciosPage';
import { ComercioDetallePage } from './pages/ComercioDetallePage';
import { OfertasPage } from './pages/OfertasPage';
import { ReportarPage } from './pages/ReportarPage';
import { MisFavoritosPage } from './pages/MisFavoritosPage';
import { MiWishlistPage } from './pages/MiWishlistPage';
import { ListaComprasPage } from './pages/ListaComprasPage';
import { PerfilPage } from './pages/PerfilPage';
import { LoginPage } from './pages/LoginPage';
import { RegistroPage } from './pages/RegistroPage';
import { NotFoundPage } from './pages/NotFoundPage';
import { UserProvider, useUser } from './context/UserContext';
import { usuariosApi } from './api';

const syncFavoritos = (usuarioId, ids) => usuariosApi.syncComerciosFavoritos(usuarioId, ids);
const syncWishlist = (usuarioId, ids) => usuariosApi.syncWishlist(usuarioId, ids);
const syncListaCompras = (usuarioId, items) => usuariosApi.syncListaCompras(usuarioId, items);

function RequireAuth({ children }) {
  const { sesionActiva } = useUser();
  if (!sesionActiva) return <Navigate to="/login" replace />;
  return children;
}

function RedirectIfLogged({ children }) {
  const { sesionActiva } = useUser();
  if (sesionActiva) return <Navigate to="/" replace />;
  return children;
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="login" element={<RedirectIfLogged><LoginPage /></RedirectIfLogged>} />
      <Route path="registro" element={<RedirectIfLogged><RegistroPage /></RedirectIfLogged>} />

      <Route element={<RequireAuth><Layout /></RequireAuth>}>
        <Route index element={<HomePage />} />
        <Route path="productos" element={<ProductosPage />} />
        <Route path="productos/:id" element={<ProductoDetallePage />} />
        <Route path="comercios" element={<ComerciosPage />} />
        <Route path="comercios/:id" element={<ComercioDetallePage />} />
        <Route path="ofertas" element={<OfertasPage />} />
        <Route path="reportar" element={<ReportarPage />} />
        <Route path="mis-favoritos" element={<MisFavoritosPage />} />
        <Route path="mi-wishlist" element={<MiWishlistPage />} />
        <Route path="lista-compras" element={<ListaComprasPage />} />
        <Route path="perfil" element={<PerfilPage />} />
        <Route path="404" element={<NotFoundPage />} />
        <Route path="*" element={<Navigate to="/404" replace />} />
      </Route>
    </Routes>
  );
}

export function App() {
  return (
    <UserProvider
      syncFavoritos={syncFavoritos}
      syncWishlist={syncWishlist}
      syncListaCompras={syncListaCompras}
    >
      <BrowserRouter>
        <AppRoutes />
      </BrowserRouter>
    </UserProvider>
  );
}
