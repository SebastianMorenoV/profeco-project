import axios from 'axios';

const JWT_KEY = 'profeco_jwt';

const client = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
});

// Interceptor de request: inyecta JWT y Content-Type
client.interceptors.request.use((config) => {
  config.headers['Accept'] = 'application/json';

  // Inyectar JWT si existe
  const token = localStorage.getItem(JWT_KEY);
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }

  // Solo enviamos Content-Type si NO es un GET
  if (config.method.toLowerCase() !== 'get') {
    config.headers['Content-Type'] = 'application/json';
  } else {
    // Limpieza absoluta para peticiones GET
    delete config.headers['Content-Type'];
  }
  
  return config;
});

// Interceptor de response: auto-logout en 401
client.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem(JWT_KEY);
    }
    return Promise.reject(error);
  }
);

// Helpers para manejar JWT
export const tokenStore = {
  save: (token) => localStorage.setItem(JWT_KEY, token),
  get: () => localStorage.getItem(JWT_KEY),
  clear: () => localStorage.removeItem(JWT_KEY)
};

export default client;