import client from './client';

export const getMultasPorComercio = (idComercio) => {
  return client.get(`/api/multas/comercio/${idComercio}`);
};
