import { useState, useCallback } from 'react';

/**
 * Hook reutilizable para gestionar categorías dinámicas en localStorage.
 *
 * @param {string}   storageKey  — clave de localStorage
 * @param {Array}    defaults    — valores iniciales si no hay nada guardado
 * @returns {{ items, add, update, remove, reset }}
 */
export function useCategories(storageKey, defaults) {
  const read = () => {
    try {
      const raw = localStorage.getItem(storageKey);
      if (raw) return JSON.parse(raw);
    } catch { /* ignore */ }
    return defaults;
  };

  const [items, setItems] = useState(read);

  const persist = useCallback((nextOrUpdater) => {
    if (typeof nextOrUpdater === 'function') {
      setItems((prev) => {
        const next = nextOrUpdater(prev);
        localStorage.setItem(storageKey, JSON.stringify(next));
        return next;
      });
    } else {
      setItems(nextOrUpdater);
      localStorage.setItem(storageKey, JSON.stringify(nextOrUpdater));
    }
  }, [storageKey]);

  /** Agregar una categoría nueva. */
  const add = useCallback((item) => {
    persist((prev) => [...prev, item]);
  }, [persist]);

  /** Actualizar un elemento por su clave. */
  const update = useCallback((oldKey, newItem) => {
    persist((prev) => prev.map((it) => (it.key === oldKey ? newItem : it)));
  }, [persist]);

  /** Eliminar un elemento por su clave. */
  const remove = useCallback((key) => {
    persist((prev) => prev.filter((it) => it.key !== key));
  }, [persist]);

  /** Restaurar valores por defecto. */
  const reset = useCallback(() => {
    persist(defaults);
  }, [persist, defaults]);

  return { items, add, update, remove, reset };
}

/* ─── Valores por defecto ─── */

export const DEFAULT_TIPOS_COMERCIO = [
  { key: 'SUPERMERCADO',         label: 'Supermercado' },
  { key: 'TIENDA_CONVENIENCIA',  label: 'Tienda de Conveniencia' },
  { key: 'FARMACIA',             label: 'Farmacia' },
  { key: 'MERCADO',              label: 'Mercado' },
  { key: 'DEPARTAMENTAL',        label: 'Departamental' },
  { key: 'OTRO',                 label: 'Otro' },
];

export const DEFAULT_MOTIVOS_MULTA = [
  { key: 'PRECIO_EXCESIVO',       label: 'Precio Excesivo' },
  { key: 'PRODUCTO_ADULTERADO',   label: 'Producto Adulterado' },
  { key: 'PUBLICIDAD_ENGANOSA',   label: 'Publicidad Engañosa' },
  { key: 'NEGACION_SERVICIO',     label: 'Negación de Servicio' },
  { key: 'INCUMPLIMIENTO_OFERTA', label: 'Incumplimiento de Oferta' },
];

export const STORAGE_KEY_TIPOS  = 'profeco_tipos_comercio';
export const STORAGE_KEY_MOTIVOS = 'profeco_motivos_multa';
export const STORAGE_KEY_CATEGORIAS_PRODUCTO = 'profeco_categorias_producto';
export const STORAGE_KEY_UNIDADES_MEDIDA = 'profeco_unidades_medida';

export const DEFAULT_CATEGORIAS_PRODUCTO = [
  { key: 'ALIMENTOS',   label: 'Alimentos' },
  { key: 'BEBIDAS',     label: 'Bebidas' },
  { key: 'HIGIENE',     label: 'Higiene' },
  { key: 'LIMPIEZA',    label: 'Limpieza' },
  { key: 'FARMACIA',    label: 'Farmacia' },
  { key: 'ELECTRONICA', label: 'Electrónica' },
  { key: 'ROPA',        label: 'Ropa' },
  { key: 'HOGAR',       label: 'Hogar' },
  { key: 'OTRO',        label: 'Otro' },
];

export const DEFAULT_UNIDADES_MEDIDA = [
  { key: 'PIEZA',     label: 'Pieza' },
  { key: 'KILOGRAMO', label: 'Kilogramo' },
  { key: 'LITRO',     label: 'Litro' },
  { key: 'METRO',     label: 'Metro' },
  { key: 'PAQUETE',   label: 'Paquete' },
  { key: 'CAJA',      label: 'Caja' },
];
