import client from './client';

// Obtiene todas las reseñas de un comercio específico
export const getReseniasPorComercio = (comercioId) => {
  return client.get(`/api/resenias/comercio/${comercioId}`);
};

// Obtiene el promedio y el total de reseñas
export const getPromedioComercio = (comercioId) => {
  return client.get(`/api/resenias/comercio/${comercioId}/promedio`);
};