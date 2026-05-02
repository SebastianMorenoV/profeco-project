import { http } from './client';

export const reseniasApi = {
  listarPorComercio: async (comercioId) => {
    const r = await http.get(`/api/resenias/comercio/${comercioId}`);
    return r.resenias ?? [];
  },
  obtenerPromedio: async (comercioId) => {
    return await http.get(`/api/resenias/comercio/${comercioId}/promedio`);
  },
  crear: async ({ usuarioId, comercioId, calificacion, comentario }) => {
    const r = await http.post('/api/resenias', {
      usuario_id: usuarioId,
      comercio_id: comercioId,
      calificacion,
      comentario
    });
    return r.resenia;
  }
};
