import axios from 'axios';
import client from './client';

// Catálogo
export const getProductos = () => client.get('/api/catalogo/productos');
export const registrarPrecio = (datosPrecio) => client.post('/api/catalogo/precios', datosPrecio);

// Ofertas
export const crearOferta = (oferta) => client.post('/api/ofertas', oferta);
export const getOfertasComercio = (idComercio) => client.get(`/api/ofertas/comercio/${idComercio}`); // <-- NUEVA LÍNEA 100% REAL


// Comercio
export const getPerfilComercio = (id) => {
  const url = `${import.meta.env.VITE_API_BASE_URL}/api/comercios/${id}`;
  return axios.get(url, {
    headers: {
      'Accept': 'application/json'
      // AQUÍ NO DEBE HABER NADA MÁS
    }
  });
};;
export const updatePerfilComercio = (id, datos) => client.put(`/api/comercios/${id}`, datos);
export const getPreciosComercio = (idComercio) => client.get(`/api/catalogo/comercio/${idComercio}/precios`);