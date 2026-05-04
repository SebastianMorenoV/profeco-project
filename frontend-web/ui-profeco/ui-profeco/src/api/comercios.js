import { http } from './client';

export const comerciosApi = {
  obtener: async (id) => {
    const r = await http.get('/api/comercios/' + id);
    return r.comercio;
  }
};
