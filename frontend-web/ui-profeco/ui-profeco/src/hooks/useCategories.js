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

  const persist = useCallback((next) => {
    setItems(next);
    localStorage.setItem(storageKey, JSON.stringify(next));
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
