import { http } from './client';

export const ofertasApi = {
  listar: async (soloActivas = true) => {
    const r = await http.get(`/api/ofertas?solo_activas=${soloActivas}`);
    return r.ofertas ?? [];
  },
  obtener: async (id) => {
    const r = await http.get(`/api/ofertas/${id}`);
    return r.oferta;
  },
  listarPorComercio: async (comercioId) => {
    const r = await http.get(`/api/ofertas/comercio/${comercioId}`);
    return r.ofertas ?? [];
  }
};
