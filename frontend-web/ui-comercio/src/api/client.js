import axios from 'axios';

const client = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
});

// src/api/client.js
client.interceptors.request.use((config) => {
  config.headers['Accept'] = 'application/json';

  // Solo enviamos Content-Type si NO es un GET
  if (config.method.toLowerCase() !== 'get') {
    config.headers['Content-Type'] = 'application/json';
  } else {
    // Limpieza absoluta para peticiones GET
    delete config.headers['Content-Type'];
  }
  
  return config;
});

export default client;