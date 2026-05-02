import { http } from './client';

export const catalogoApi = {
  buscarProductos: async (query, categoria) => {
    const params = new URLSearchParams();
    if (query) params.set('query', query);
    if (categoria) params.set('categoria', categoria);
    const qs = params.toString();
    const r = await http.get(`/api/catalogo/productos${qs ? `?${qs}` : ''}`);
    return r.productos ?? [];
  },
  obtenerProducto: async (id) => {
    const r = await http.get(`/api/catalogo/productos/${id}`);
    return r.producto;
  },
  obtenerPreciosProducto: async (id) => {
    const r = await http.get(`/api/catalogo/productos/${id}/precios`);
    return r.precios ?? [];
  }
};
