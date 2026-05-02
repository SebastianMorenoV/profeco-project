import { http } from './client';

export const comerciosApi = {
  listar: async (ciudad, tipoComercio) => {
    const params = new URLSearchParams();
    if (ciudad) params.set('ciudad', ciudad);
    if (tipoComercio) params.set('tipo_comercio', tipoComercio);
    const qs = params.toString();
    const r = await http.get(`/api/comercios${qs ? `?${qs}` : ''}`);
    return r.comercios ?? [];
  },
  obtener: async (id) => {
    const r = await http.get(`/api/comercios/${id}`);
    return r.comercio;
  },
  buscar: async (query) => {
    const r = await http.get(`/api/comercios/buscar?query=${encodeURIComponent(query)}`);
    return r.comercios ?? [];
  }
};
