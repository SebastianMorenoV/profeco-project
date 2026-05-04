import { http } from './client';

export const multasApi = {
  listar: async (estatus = '') => {
    const qs = estatus ? `?estatus=${estatus}` : '';
    const r = await http.get(`/api/multas${qs}`);
    return r.multas ?? [];
  },
  emitir: async (data) => {
    const r = await http.post('/api/multas', data);
    return r.multa;
  },
  actualizarEstatus: async (id, estatus) => {
    const r = await http.put(`/api/multas/${id}/estatus`, { estatus });
    return r.multa;
  }
};