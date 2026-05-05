export { catalogoApi } from './catalogo';
export { comerciosApi } from './comercios';
export { reseniasApi } from './resenias';
export { ofertasApi } from './ofertas';
export { usuariosApi } from './usuarios';
export { reportesApi } from './reportes';
export { ApiError } from './client';

export const CATEGORIAS = [
  'ALIMENTOS',
  'BEBIDAS',
  'HIGIENE',
  'LIMPIEZA',
  'ABARROTES',
  'OTROS'
];

export const MOTIVOS_REPORTE = [
  { value: 'PRECIO_EXCESIVO', label: 'Precio excesivo o engañoso' },
  { value: 'PRODUCTO_ADULTERADO', label: 'Producto adulterado o caduco' },
  { value: 'PUBLICIDAD_ENGANOSA', label: 'Publicidad engañosa' },
  { value: 'NEGACION_SERVICIO', label: 'Negación de servicio' },
  { value: 'INCUMPLIMIENTO_OFERTA', label: 'Incumplimiento de oferta' },
  { value: 'OTRO', label: 'Otro' }
];

export const ESTATUS_REPORTE_LABEL = {
  PENDIENTE: 'Pendiente',
  EN_REVISION: 'En revisión',
  RESUELTA_CON_MULTA: 'Resuelta con multa',
  RESUELTA_SIN_MULTA: 'Resuelta sin multa',
  RECHAZADA: 'Rechazada'
};
