import { http } from './client';

export const usuariosApi = {
  obtener: async (id) => {
    const r = await http.get(`/api/usuarios/${id}`);
    return r.usuario;
  },
  listar: async (tipo_usuario = '') => {
    const qs = tipo_usuario ? `?tipo_usuario=${tipo_usuario}` : '';
    const r = await http.get(`/api/usuarios${qs}`);
    return r.usuarios ?? [];
  },
  registrar: async (data) => {
    const r = await http.post('/api/usuarios', data);
    return r.usuario;
  },
  login: async (email, password) => {
    const r = await http.post('/api/usuarios/login', { email, password });
    return r; // { exito, mensaje, usuario }
  },
  buscarPorEmail: async (email) => {
    const r = await http.get(`/api/usuarios/email/${email}`);
    return r.usuario;
  }
};
