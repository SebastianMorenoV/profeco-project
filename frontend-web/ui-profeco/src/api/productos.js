import { http } from './client';

export const productosApi = {
  listar: async (query = '', categoria = '') => {
    const params = new URLSearchParams();
    if (query) params.set('query', query);
    if (categoria) params.set('categoria', categoria);
    const qs = params.toString();
    const r = await http.get('/api/catalogo/productos' + (qs ? '?' + qs : ''));
    return r.productos ?? [];
  },

  obtener: async (id) => {
    const r = await http.get('/api/catalogo/productos/' + id);
    return r.producto;
  },

  crear: async (data) => {
    const r = await http.post('/api/catalogo/productos', data);
    return r.producto;
  },

  actualizar: async (id, data) => {
    const r = await http.put('/api/catalogo/productos/' + id, data);
    return r.producto;
  },

  eliminar: async (id) => {
    return await http.del('/api/catalogo/productos/' + id);
  }
};
