import { useState } from 'react';
import {
  useCategories,
  DEFAULT_TIPOS_COMERCIO,
  DEFAULT_MOTIVOS_MULTA,
  DEFAULT_CATEGORIAS_PRODUCTO,
  DEFAULT_UNIDADES_MEDIDA,
  STORAGE_KEY_TIPOS,
  STORAGE_KEY_MOTIVOS,
  STORAGE_KEY_CATEGORIAS_PRODUCTO,
  STORAGE_KEY_UNIDADES_MEDIDA
} from '../hooks/useCategories';

/**
 * Componente genérico para gestionar una lista de categorías (CRUD).
 * Se reutiliza para "Tipos de Comercio" y "Motivos de Multa".
 */
function CategoryManager({ title, subtitle, items, onAdd, onUpdate, onRemove, onReset }) {
  const [editKey, setEditKey] = useState(null);
  const [editLabel, setEditLabel] = useState('');
  const [editKeyValue, setEditKeyValue] = useState('');

  const [newKey, setNewKey] = useState('');
  const [newLabel, setNewLabel] = useState('');
  const [showAdd, setShowAdd] = useState(false);
  const [errMsg, setErrMsg] = useState(null);

  const startEdit = (item) => {
    setEditKey(item.key);
    setEditKeyValue(item.key);
    setEditLabel(item.label);
    setErrMsg(null);
  };

  const cancelEdit = () => {
    setEditKey(null);
    setEditKeyValue('');
    setEditLabel('');
  };

  const saveEdit = () => {
    const k = editKeyValue.trim().toUpperCase().replace(/\s+/g, '_');
    const l = editLabel.trim();
    if (!k || !l) { setErrMsg('Clave y nombre son obligatorios.'); return; }
    if (k !== editKey && items.some((it) => it.key === k)) {
      setErrMsg('Ya existe una categoría con esa clave.');
      return;
    }
    onUpdate(editKey, { key: k, label: l });
    cancelEdit();
    setErrMsg(null);
  };

  const handleAdd = () => {
    const k = newKey.trim().toUpperCase().replace(/\s+/g, '_');
    const l = newLabel.trim();
    if (!k || !l) { setErrMsg('Clave y nombre son obligatorios.'); return; }
    if (items.some((it) => it.key === k)) {
      setErrMsg('Ya existe una categoría con esa clave.');
      return;
    }
    onAdd({ key: k, label: l });
    setNewKey('');
    setNewLabel('');
    setShowAdd(false);
    setErrMsg(null);
  };

  const handleRemove = (key, label) => {
    if (!confirm(`¿Eliminar "${label}"? Esta acción no se puede deshacer.`)) return;
    onRemove(key);
  };

  return (
    <section className="card" style={{ marginBottom: '2rem' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '0.5rem' }}>
        <div>
          <h3 style={{ margin: 0 }}>{title}</h3>
          {subtitle && <p className="muted small" style={{ margin: '0.25rem 0 0' }}>{subtitle}</p>}
        </div>
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <button
            type="button"
            className="btn-action btn-primary"
            style={{ width: 'auto' }}
            onClick={() => { setShowAdd(true); setErrMsg(null); }}
          >
            + Agregar
          </button>
          <button
            type="button"
            className="btn-action btn-danger"
            style={{ width: 'auto' }}
            onClick={() => {
              if (confirm('¿Restaurar valores por defecto? Se perderán los cambios.')) onReset();
            }}
          >
            Restaurar
          </button>
        </div>
      </div>

      {errMsg && <div className="error-box" style={{ marginTop: '0.75rem' }}>{errMsg}</div>}

      {/* Fila de agregar */}
      {showAdd && (
        <div className="grid grid-2" style={{ marginTop: '1rem', padding: '1rem', background: 'var(--c-primary-soft)', borderRadius: 'var(--radius-sm)' }}>
          <label style={{ display: 'flex', flexDirection: 'column', gap: '0.3rem', fontSize: '0.875rem', fontWeight: 600 }}>
            Clave (ej: NUEVO_TIPO):
            <input
              value={newKey}
              onChange={(e) => setNewKey(e.target.value)}
              placeholder="CLAVE_UNICA"
              style={{ fontFamily: 'inherit', fontSize: '0.9rem', padding: '0.45rem 0.65rem', border: '1px solid var(--c-border)', borderRadius: 'var(--radius-sm)' }}
            />
          </label>
          <label style={{ display: 'flex', flexDirection: 'column', gap: '0.3rem', fontSize: '0.875rem', fontWeight: 600 }}>
            Nombre visible:
            <input
              value={newLabel}
              onChange={(e) => setNewLabel(e.target.value)}
              placeholder="Nombre para mostrar"
              style={{ fontFamily: 'inherit', fontSize: '0.9rem', padding: '0.45rem 0.65rem', border: '1px solid var(--c-border)', borderRadius: 'var(--radius-sm)' }}
            />
          </label>
          <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'flex-end' }}>
            <button type="button" className="btn-action btn-success" style={{ width: 'auto' }} onClick={handleAdd}>
              Guardar
            </button>
            <button type="button" className="btn-action" style={{ width: 'auto', background: 'var(--c-bg)', border: '1px solid var(--c-border)' }} onClick={() => { setShowAdd(false); setErrMsg(null); }}>
              Cancelar
            </button>
          </div>
        </div>
      )}

      {/* Tabla de categorías */}
      {items.length > 0 && (
        <div className="table-wrap" style={{ marginTop: '1rem' }}>
          <table className="precios-table">
            <thead>
              <tr>
                <th>Clave</th>
                <th>Nombre</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {items.map((item) => (
                <tr key={item.key}>
                  {editKey === item.key ? (
                    <>
                      <td>
                        <input
                          value={editKeyValue}
                          onChange={(e) => setEditKeyValue(e.target.value)}
                          style={{ fontFamily: 'inherit', fontSize: '0.85rem', padding: '0.35rem 0.5rem', border: '1px solid var(--c-border)', borderRadius: 'var(--radius-sm)', width: '100%' }}
                        />
                      </td>
                      <td>
                        <input
                          value={editLabel}
                          onChange={(e) => setEditLabel(e.target.value)}
                          style={{ fontFamily: 'inherit', fontSize: '0.85rem', padding: '0.35rem 0.5rem', border: '1px solid var(--c-border)', borderRadius: 'var(--radius-sm)', width: '100%' }}
                        />
                      </td>
                      <td>
                        <div className="acciones-container" style={{ flexDirection: 'row' }}>
                          <button type="button" className="btn-action btn-success" onClick={saveEdit}>Guardar</button>
                          <button type="button" className="btn-action" style={{ background: 'var(--c-bg)', border: '1px solid var(--c-border)' }} onClick={cancelEdit}>Cancelar</button>
                        </div>
                      </td>
                    </>
                  ) : (
                    <>
                      <td><code style={{ fontSize: '0.8rem', background: 'var(--c-bg)', padding: '0.15rem 0.4rem', borderRadius: '4px' }}>{item.key}</code></td>
                      <td>{item.label}</td>
                      <td>
                        <div className="acciones-container" style={{ flexDirection: 'row' }}>
                          <button type="button" className="btn-action btn-primary" onClick={() => startEdit(item)}>Editar</button>
                          <button type="button" className="btn-action btn-danger" onClick={() => handleRemove(item.key, item.label)}>Eliminar</button>
                        </div>
                      </td>
                    </>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}

export function CategoriasPage() {
  const tipos = useCategories(STORAGE_KEY_TIPOS, DEFAULT_TIPOS_COMERCIO);
  const motivos = useCategories(STORAGE_KEY_MOTIVOS, DEFAULT_MOTIVOS_MULTA);
  const catProducto = useCategories(STORAGE_KEY_CATEGORIAS_PRODUCTO, DEFAULT_CATEGORIAS_PRODUCTO);
  const unidades = useCategories(STORAGE_KEY_UNIDADES_MEDIDA, DEFAULT_UNIDADES_MEDIDA);

  return (
    <div className="page">
      <div className="section-header">
        <h1>Gestión de Categorías</h1>
      </div>
      <p className="muted">
        Administra las categorías que se usan en los formularios del sistema.
        Los cambios se guardan automáticamente y se reflejan en todas las páginas.
      </p>

      <CategoryManager
        title="Tipos de Comercio"
        subtitle="Se usan al dar de alta un comercio en la sección Comercios."
        items={tipos.items}
        onAdd={tipos.add}
        onUpdate={tipos.update}
        onRemove={tipos.remove}
        onReset={tipos.reset}
      />

      <CategoryManager
        title="Motivos de Multa"
        subtitle="Se usan al emitir una sanción en la sección Multas."
        items={motivos.items}
        onAdd={motivos.add}
        onUpdate={motivos.update}
        onRemove={motivos.remove}
        onReset={motivos.reset}
      />

      <CategoryManager
        title="Categorías de Producto"
        subtitle="Se usan al dar de alta un producto en el catálogo."
        items={catProducto.items}
        onAdd={catProducto.add}
        onUpdate={catProducto.update}
        onRemove={catProducto.remove}
        onReset={catProducto.reset}
      />

      <CategoryManager
        title="Unidades de Medida"
        subtitle="Se usan al registrar un producto en el catálogo."
        items={unidades.items}
        onAdd={unidades.add}
        onUpdate={unidades.update}
        onRemove={unidades.remove}
        onReset={unidades.reset}
      />
    </div>
  );
}
