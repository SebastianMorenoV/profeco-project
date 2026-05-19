const BASE = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8083';

const JWT_KEY = 'profeco_jwt';

export class ApiError extends Error {
  constructor(status, body, message) {
    super(message);
    this.status = status;
    this.body = body;
  }
}

function parseBody(text, contentType) {
  if (!text) return null;
  if (contentType && contentType.includes('application/json')) {
    try { return JSON.parse(text); } catch { return text; }
  }
  try { return JSON.parse(text); } catch { return text; }
}

async function request(path, init) {
  const headers = { ...(init?.headers || {}) };
  const method = (init?.method || 'GET').toUpperCase();

  // Inyectar JWT si existe
  const token = localStorage.getItem(JWT_KEY);
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  // Solo enviar Content-Type en métodos con body
  if (['POST', 'PUT', 'PATCH'].includes(method)) {
    if (!headers['Content-Type']) headers['Content-Type'] = 'application/json';
  }

  const res = await fetch(`${BASE}${path}`, { ...init, headers });
  const text = await res.text();
  const contentType = res.headers.get('content-type') ?? '';
  const body = parseBody(text, contentType);

  if (!res.ok) {
    // Si Envoy rechaza por JWT inválido, limpiar sesión
    if (res.status === 401) {
      localStorage.removeItem(JWT_KEY);
    }
    let msg;
    if (typeof body === 'string') {
      msg = body.length > 160 ? `${body.slice(0, 160)}…` : body;
    } else if (body && typeof body === 'object') {
      msg = body.message || body.error || `HTTP ${res.status}`;
    } else {
      msg = `HTTP ${res.status}`;
    }
    throw new ApiError(res.status, body, msg);
  }

  if (typeof body === 'string') {
    throw new ApiError(
      res.status,
      body,
      'El servidor respondió un mensaje no esperado. Es posible que el microservicio esté iniciando o no responda. Intenta de nuevo en unos segundos.'
    );
  }

  return body;
}

export const http = {
  get: (path) => request(path),
  post: (path, data) => request(path, { method: 'POST', body: JSON.stringify(data) }),
  put: (path, data) => request(path, { method: 'PUT', body: JSON.stringify(data) }),
  del: (path) => request(path, { method: 'DELETE' })
};

// Helpers para manejar JWT
export const tokenStore = {
  save: (token) => localStorage.setItem(JWT_KEY, token),
  get: () => localStorage.getItem(JWT_KEY),
  clear: () => localStorage.removeItem(JWT_KEY)
};
