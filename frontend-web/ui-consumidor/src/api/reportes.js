import { http } from './client';

export const reportesApi = {
  crear: async ({ usuarioId, comercioId, motivo, descripcion }) => {
    const r = await http.post('/api/reportes', {
      usuario_id: usuarioId,
      comercio_id: comercioId,
      motivo,
      descripcion
    });
    return r.reporte;
  },
  listarPorUsuario: async (usuarioId) => {
    const r = await http.get(`/api/reportes/usuario/${usuarioId}`);
    return r.reportes ?? [];
  }
};
