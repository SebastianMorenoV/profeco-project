import { http } from './client';

export const usuariosApi = {
  obtenerComerciosFavoritos: async (usuarioId) => {
    const r = await http.get(`/api/usuarios/${usuarioId}/comercios-favoritos`);
    return r.ids ?? [];
  },
  syncComerciosFavoritos: async (usuarioId, ids) => {
    const r = await http.put(`/api/usuarios/${usuarioId}/comercios-favoritos`, {
      usuario_id: usuarioId,
      ids: ids.map(Number)
    });
    return r.ids ?? [];
  },
  obtenerWishlist: async (usuarioId) => {
    const r = await http.get(`/api/usuarios/${usuarioId}/wishlist`);
    return r.ids ?? [];
  },
  syncWishlist: async (usuarioId, ids) => {
    const r = await http.put(`/api/usuarios/${usuarioId}/wishlist`, {
      usuario_id: usuarioId,
      ids: ids.map(Number)
    });
    return r.ids ?? [];
  },
  obtenerListaCompras: async (usuarioId) => {
    const r = await http.get(`/api/usuarios/${usuarioId}/lista-compras`);
    return r.items ?? [];
  },
  syncListaCompras: async (usuarioId, items) => {
    const payload = items.map((it) => ({
      id_local: Number(it.idLocal),
      nombre: String(it.nombre ?? ''),
      marcado: Boolean(it.marcado)
    }));
    const r = await http.put(`/api/usuarios/${usuarioId}/lista-compras`, {
      usuario_id: usuarioId,
      items: payload
    });
    return r.items ?? [];
  }
};
