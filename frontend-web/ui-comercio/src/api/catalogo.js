import client from './client';

// 1. Obtener el catálogo general de productos (para saber a qué le ponemos precio)
export const getProductos = (query = '') => {
  return client.get(`/api/catalogo/productos?query=${query}`);
};

// 2. Obtener los precios actuales de un comercio específico
export const getPreciosPorComercio = (comercioId) => {
  return client.get(`/api/catalogo/comercio/${comercioId}/precios`);
};

// 3. Registrar un PRECIO NUEVO para un producto
export const registrarPrecio = (datos) => {
  return client.post('/api/catalogo/precios', datos);
};

// 4. Actualizar un PRECIO EXISTENTE
export const actualizarPrecio = (precioId, datos) => {
  return client.put(`/api/catalogo/precios/${precioId}`, datos);
};