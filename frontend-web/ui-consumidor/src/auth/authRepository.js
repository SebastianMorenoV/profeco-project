import { usuariosApi } from '../api';
import { ApiError } from '../api/client';

const armaSesion = (u) => ({
  usuarioId: Number(u.id),
  nombre: String(u.nombre ?? ''),
  apellido: String(u.apellido ?? ''),
  email: String(u.email ?? '')
});

const mapearError = (err, fallback) => {
  if (err instanceof ApiError) {
    if (err.status === 409) return 'Ya existe una cuenta con ese correo.';
    if (err.status === 400) return err.message || 'Datos inválidos. Revisa el correo y la contraseña.';
    if (err.status === 404) return 'Endpoint no disponible. Verifica que el gateway esté actualizado.';
    if (err.message) return err.message;
  }
  return fallback;
};

export const authRepository = {
  async login(email, password) {
    if (!email || !password) {
      return { ok: false, mensaje: 'Captura correo y contraseña.' };
    }
    try {
      const r = await usuariosApi.login(email.trim(), password);
      if (r?.exito && r?.usuario) {
        return { ok: true, sesion: armaSesion(r.usuario) };
      }
      return { ok: false, mensaje: r?.mensaje || 'Credenciales inválidas.' };
    } catch (err) {
      return { ok: false, mensaje: mapearError(err, 'No se pudo conectar con el servidor.') };
    }
  },

  async registrar({ nombre, apellido, email, telefono, password, confirmar }) {
    if (!nombre || !nombre.trim()) return { ok: false, mensaje: 'Captura tu nombre.' };
    if (!email || !email.includes('@')) return { ok: false, mensaje: 'Captura un correo válido.' };
    if (!password || password.length < 6) return { ok: false, mensaje: 'La contraseña debe tener al menos 6 caracteres.' };
    if (password !== confirmar) return { ok: false, mensaje: 'Las contraseñas no coinciden.' };

    try {
      const usuario = await usuariosApi.registrar({
        nombre: nombre.trim(),
        apellido: (apellido ?? '').trim(),
        email: email.trim(),
        telefono: (telefono ?? '').trim(),
        password
      });
      if (!usuario) {
        return { ok: false, mensaje: 'El servidor no devolvió la cuenta creada.' };
      }
      return { ok: true, sesion: armaSesion(usuario) };
    } catch (err) {
      return { ok: false, mensaje: mapearError(err, 'No se pudo crear la cuenta.') };
    }
  }
};
