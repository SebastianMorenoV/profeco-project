import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { usuariosApi } from '../api';
import { tokenStore } from '../api/client';

const STORAGE_KEY = 'profeco_consumidor_v1';
const DEFAULT_USUARIO_ID = 1;

const UserContext = createContext(null);

const defaultState = {
  usuarioId: DEFAULT_USUARIO_ID,
  favoritos: [],
  wishlist: [],
  listaCompras: [],
  busquedasRecientes: [],
  sesionActiva: false,
  nombre: '',
  apellido: '',
  email: ''
};

function readState() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return defaultState;
    const parsed = JSON.parse(raw);
    return {
      usuarioId: Number(parsed.usuarioId) > 0 ? Number(parsed.usuarioId) : DEFAULT_USUARIO_ID,
      favoritos: Array.isArray(parsed.favoritos) ? parsed.favoritos.map(Number).filter(Boolean) : [],
      wishlist: Array.isArray(parsed.wishlist) ? parsed.wishlist.map(Number).filter(Boolean) : [],
      listaCompras: Array.isArray(parsed.listaCompras) ? parsed.listaCompras : [],
      busquedasRecientes: Array.isArray(parsed.busquedasRecientes) ? parsed.busquedasRecientes : [],
      sesionActiva: Boolean(parsed.sesionActiva),
      nombre: typeof parsed.nombre === 'string' ? parsed.nombre : '',
      apellido: typeof parsed.apellido === 'string' ? parsed.apellido : '',
      email: typeof parsed.email === 'string' ? parsed.email : ''
    };
  } catch {
    return defaultState;
  }
}

function writeState(state) {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
  } catch {
    // ignore quota errors
  }
}

export function UserProvider({ children, syncFavoritos, syncWishlist, syncListaCompras }) {
  const [state, setState] = useState(readState);

  useEffect(() => {
    writeState(state);
  }, [state]);

  useEffect(() => {
    let cancelado = false;
    (async () => {
      try {
        const [favs, wish, compras] = await Promise.all([
          usuariosApi.obtenerComerciosFavoritos(state.usuarioId).catch(() => null),
          usuariosApi.obtenerWishlist(state.usuarioId).catch(() => null),
          usuariosApi.obtenerListaCompras(state.usuarioId).catch(() => null)
        ]);
        if (cancelado) return;

        setState((s) => {
          if (s.usuarioId !== state.usuarioId) return s;

          let nextFavs = s.favoritos;
          if (Array.isArray(favs)) {
            nextFavs = Array.from(new Set(favs.map(Number).filter(Boolean)));
          }

          let nextWish = s.wishlist;
          if (Array.isArray(wish)) {
            nextWish = Array.from(new Set(wish.map(Number).filter(Boolean)));
          }

          let nextCompras = s.listaCompras;
          if (Array.isArray(compras)) {
            nextCompras = compras.map((it, idx) => ({
              idLocal: Number(it.idLocal ?? it.id_local ?? idx + 1),
              nombre: String(it.nombre ?? ''),
              marcado: Boolean(it.marcado)
            }));
          }

          return {
            ...s,
            favoritos: nextFavs,
            wishlist: nextWish,
            listaCompras: nextCompras
          };
        });
      } catch {
        // ignore
      }
    })();
    return () => { cancelado = true; };
  }, [state.usuarioId]);

  const setUsuarioId = useCallback((id) => {
    const num = Number(id);
    if (!Number.isFinite(num) || num <= 0) return;
    setState((s) => {
      if (s.usuarioId === num) return s;
      return {
        ...s,
        usuarioId: num,
        favoritos: [],
        wishlist: [],
        listaCompras: []
      };
    });
  }, []);

  const toggleFavorito = useCallback((comercioId) => {
    const id = Number(comercioId);
    if (!Number.isFinite(id) || id <= 0) return;
    setState((s) => {
      const existe = s.favoritos.includes(id);
      const next = existe ? s.favoritos.filter((x) => x !== id) : [...s.favoritos, id];
      if (syncFavoritos) syncFavoritos(s.usuarioId, next).catch(() => {});
      return { ...s, favoritos: next };
    });
  }, [syncFavoritos]);

  const toggleWishlist = useCallback((productoId) => {
    const id = Number(productoId);
    if (!Number.isFinite(id) || id <= 0) return;
    setState((s) => {
      const existe = s.wishlist.includes(id);
      const next = existe ? s.wishlist.filter((x) => x !== id) : [...s.wishlist, id];
      if (syncWishlist) syncWishlist(s.usuarioId, next).catch(() => {});
      return { ...s, wishlist: next };
    });
  }, [syncWishlist]);

  const setFavoritosRemotos = useCallback((ids) => {
    setState((s) => ({ ...s, favoritos: Array.from(new Set(ids.map(Number).filter(Boolean))) }));
  }, []);

  const setWishlistRemota = useCallback((ids) => {
    setState((s) => ({ ...s, wishlist: Array.from(new Set(ids.map(Number).filter(Boolean))) }));
  }, []);

  const setListaComprasRemota = useCallback((items) => {
    setState((s) => ({ ...s, listaCompras: items }));
  }, []);

  const agregarItemListaCompras = useCallback((nombre) => {
    const limpio = String(nombre ?? '').trim();
    if (!limpio) return;
    setState((s) => {
      const idLocal = (s.listaCompras.reduce((m, it) => Math.max(m, it.idLocal ?? 0), 0) + 1);
      const item = { idLocal, nombre: limpio.slice(0, 200), marcado: false };
      const next = [...s.listaCompras, item];
      if (syncListaCompras) syncListaCompras(s.usuarioId, next).catch(() => {});
      return { ...s, listaCompras: next };
    });
  }, [syncListaCompras]);

  const toggleItemListaCompras = useCallback((idLocal) => {
    setState((s) => {
      const next = s.listaCompras.map((it) => it.idLocal === idLocal ? { ...it, marcado: !it.marcado } : it);
      if (syncListaCompras) syncListaCompras(s.usuarioId, next).catch(() => {});
      return { ...s, listaCompras: next };
    });
  }, [syncListaCompras]);

  const eliminarItemListaCompras = useCallback((idLocal) => {
    setState((s) => {
      const next = s.listaCompras.filter((it) => it.idLocal !== idLocal);
      if (syncListaCompras) syncListaCompras(s.usuarioId, next).catch(() => {});
      return { ...s, listaCompras: next };
    });
  }, [syncListaCompras]);

  const limpiarListaCompras = useCallback(() => {
    setState((s) => {
      if (syncListaCompras) syncListaCompras(s.usuarioId, []).catch(() => {});
      return { ...s, listaCompras: [] };
    });
  }, [syncListaCompras]);

  const registrarBusqueda = useCallback((query) => {
    const q = String(query ?? '').trim();
    if (!q) return;
    setState((s) => {
      const dedupe = [q, ...s.busquedasRecientes.filter((x) => x.toLowerCase() !== q.toLowerCase())];
      return { ...s, busquedasRecientes: dedupe.slice(0, 8) };
    });
  }, []);

  const limpiarBusquedas = useCallback(() => {
    setState((s) => ({ ...s, busquedasRecientes: [] }));
  }, []);

  const iniciarSesion = useCallback((sesion) => {
    const id = Number(sesion?.usuarioId);
    if (!Number.isFinite(id) || id <= 0) return;
    setState((s) => {
      const cambiaUsuario = s.usuarioId !== id;
      return {
        ...s,
        usuarioId: id,
        nombre: String(sesion.nombre ?? ''),
        apellido: String(sesion.apellido ?? ''),
        email: String(sesion.email ?? ''),
        sesionActiva: true,
        favoritos: cambiaUsuario ? [] : s.favoritos,
        wishlist: cambiaUsuario ? [] : s.wishlist,
        listaCompras: cambiaUsuario ? [] : s.listaCompras,
        busquedasRecientes: cambiaUsuario ? [] : s.busquedasRecientes
      };
    });
  }, []);

  const cerrarSesion = useCallback(() => {
    tokenStore.clear();
    setState(() => ({
      ...defaultState
    }));
  }, []);

  const value = useMemo(() => ({
    ...state,
    setUsuarioId,
    toggleFavorito,
    toggleWishlist,
    setFavoritosRemotos,
    setWishlistRemota,
    setListaComprasRemota,
    agregarItemListaCompras,
    toggleItemListaCompras,
    eliminarItemListaCompras,
    limpiarListaCompras,
    registrarBusqueda,
    limpiarBusquedas,
    iniciarSesion,
    cerrarSesion
  }), [
    state,
    setUsuarioId,
    toggleFavorito,
    toggleWishlist,
    setFavoritosRemotos,
    setWishlistRemota,
    setListaComprasRemota,
    agregarItemListaCompras,
    toggleItemListaCompras,
    eliminarItemListaCompras,
    limpiarListaCompras,
    registrarBusqueda,
    limpiarBusquedas,
    iniciarSesion,
    cerrarSesion
  ]);

  return <UserContext.Provider value={value}>{children}</UserContext.Provider>;
}

export function useUser() {
  const ctx = useContext(UserContext);
  if (!ctx) throw new Error('useUser debe usarse dentro de UserProvider');
  return ctx;
}
