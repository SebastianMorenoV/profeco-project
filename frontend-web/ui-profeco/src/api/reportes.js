import { http } from './client';

export const reportesApi = {
  listar: async (estatus = '') => {
    const qs = estatus ? `?estatus=${estatus}` : '';
    const r = await http.get(`/api/reportes${qs}`);
    return r.reportes ?? [];
  },
  obtener: async (id) => {
    const r = await http.get(`/api/reportes/${id}`);
    return r.reporte;
  },
  actualizarEstatus: async (id, estatus) => {
    const r = await http.put(`/api/reportes/${id}/estatus`, { estatus });
    return r.reporte;
  }
};