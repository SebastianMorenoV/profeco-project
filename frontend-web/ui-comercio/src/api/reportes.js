import client from './client';

export const getReportesPorComercio = (idComercio) => {
  return client.get(`/api/reportes/comercio/${idComercio}`);
};
