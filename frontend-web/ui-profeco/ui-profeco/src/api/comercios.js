import { http } from './client';

export const comerciosApi = {
  obtener: async (id) => {
    const r = await http.get('/api/comercios/' + id);
    return r.comercio;
  },
  listar: async () => {
    const r = await http.get('/api/comercios');
    return r.comercios ?? [];
  },
  registrar: async (data) => {
    const r = await http.post('/api/comercios', data);
    return r.comercio;
  }
};
