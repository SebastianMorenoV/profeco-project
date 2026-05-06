import { useState } from 'react';
import { productosApi } from '../api/productos';
import { useFetch } from '../hooks/useFetch';
import { Loader } from '../components/Loader';
import {
  useCategories,
  DEFAULT_CATEGORIAS_PRODUCTO,
  DEFAULT_UNIDADES_MEDIDA,
  STORAGE_KEY_CATEGORIAS_PRODUCTO,
  STORAGE_KEY_UNIDADES_MEDIDA
} from '../hooks/useCategories';

const INITIAL_FORM = {
  nombre: '',
  descripcion: '',
  marca: '',
  categoria: '',
  codigoBarras: '',
  unidadMedida: '',
};

export function ProductosPage() {
  /* ── Categorías dinámicas ── */
  const { items: categoriasProducto } = useCategories(STORAGE_KEY_CATEGORIAS_PRODUCTO, DEFAULT_CATEGORIAS_PRODUCTO);
  const { items: unidadesMedida } = useCategories(STORAGE_KEY_UNIDADES_MEDIDA, DEFAULT_UNIDADES_MEDIDA);

  /* ── Estado del buscador ── */
  const [busqueda, setBusqueda] = useState('');
  const [filtroCat, setFiltroCat] = useState('');

  /* ── Listar productos ── */
  const { data: productos, loading, error, reload } = useFetch(
    () => productosApi.listar(busqueda, filtroCat),
    [busqueda, filtroCat]
  );

  /* ── Formulario de alta ── */
  const [form, setForm] = useState(INITIAL_FORM);
  const [guardando, setGuardando] = useState(false);
  const [okMsg, setOkMsg] = useState(null);
  const [errMsg, setErrMsg] = useState(null);
  const [showForm, setShowForm] = useState(false);

  /* ── Edición inline ── */
  const [editId, setEditId] = useState(null);
  const [editForm, setEditForm] = useState(INITIAL_FORM);
  const [editGuardando, setEditGuardando] = useState(false);

  const onField = (key, value) => setForm((prev) => ({ ...prev, [key]: value }));
  const onEditField = (key, value) => setEditForm((prev) => ({ ...prev, [key]: value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setGuardando(true);
    setOkMsg(null);
    setErrMsg(null);

    try {
      const payload = {
        nombre: form.nombre.trim(),
        descripcion: form.descripcion.trim(),
        marca: form.marca.trim(),
        categoria: form.categoria,
        codigoBarras: form.codigoBarras.trim(),
        unidadMedida: form.unidadMedida,
      };

      if (!payload.nombre) {
        setErrMsg('El nombre del producto es obligatorio.');
        setGuardando(false);
        return;
      }
      if (!payload.categoria) {
        setErrMsg('Debes seleccionar una categoría.');
        setGuardando(false);
        return;
      }

      const producto = await productosApi.crear(payload);
      setOkMsg(`Producto "${producto?.nombre ?? ''}" registrado correctamente con ID #${producto?.id ?? 'N/A'}.`);
      setForm(INITIAL_FORM);
      reload();
    } catch (err) {
      setErrMsg(err.message || 'No se pudo registrar el producto.');
    } finally {
      setGuardando(false);
    }
  };

  const startEdit = (p) => {
    setEditId(p.id);
    setEditForm({
      nombre: p.nombre || '',
      descripcion: p.descripcion || '',
      marca: p.marca || '',
      categoria: p.categoria || '',
      codigoBarras: p.codigoBarras || '',
      unidadMedida: p.unidadMedida || '',
    });
  };

  const cancelEdit = () => {
    setEditId(null);
    setEditForm(INITIAL_FORM);
  };

  const saveEdit = async () => {
    setEditGuardando(true);
    try {
      await productosApi.actualizar(editId, {
        nombre: editForm.nombre.trim(),
        descripcion: editForm.descripcion.trim(),
        marca: editForm.marca.trim(),
        categoria: editForm.categoria,
        codigoBarras: editForm.codigoBarras.trim(),
        unidadMedida: editForm.unidadMedida,
      });
      cancelEdit();
      reload();
    } catch (err) {
      alert('Error al actualizar: ' + (err.message || 'desconocido'));
    } finally {
      setEditGuardando(false);
    }
  };

  const handleToggle = async (id, esActivo, nombre) => {
    const accion = esActivo ? 'desactivar' : 'activar';
    if (!confirm(`¿Deseas ${accion} "${nombre}"?`)) return;
    try {
      if (esActivo) {
        // Desactivar: usa DELETE (soft-delete, pone activo=false en BD)
        await productosApi.eliminar(id);
      } else {
        // Activar: usa PUT con setActivo=true, activo=true
        await productosApi.actualizar(id, { setActivo: true, activo: true });
      }
      reload();
    } catch (err) {
      alert('Error al cambiar estado: ' + (err.message || 'desconocido'));
    }
  };

  return (
    <div className="page">
      <div className="section-header">
        <h1>Catálogo de Productos</h1>
        <button
          type="button"
          className="btn-action btn-primary"
          style={{ width: 'auto', padding: '0.55rem 1.3rem', fontSize: '0.9rem' }}
          onClick={() => { setShowForm(!showForm); setOkMsg(null); setErrMsg(null); }}
        >
          {showForm ? '✕ Cerrar formulario' : '+ Agregar Producto'}
        </button>
      </div>
      <p className="muted">
        Administra el catálogo oficial de productos. Los precios son registrados por los comercios, no por PROFECO.
      </p>

      {/* ── Formulario de alta ── */}
      {showForm && (
        <section className="card" style={{ marginBottom: '1.75rem' }}>
          <h3>Registrar Producto</h3>
          <form className="resenia-form" onSubmit={handleSubmit}>
            <div className="grid grid-2">
              <label>
                Nombre del producto *
                <input
                  required
                  value={form.nombre}
                  onChange={(e) => onField('nombre', e.target.value)}
                  placeholder="Ej: Leche Lala Entera 1L"
                />
              </label>

              <label>
                Marca
                <input
                  value={form.marca}
                  onChange={(e) => onField('marca', e.target.value)}
                  placeholder="Ej: Lala, Bimbo, Coca-Cola"
                />
              </label>

              <label>
                Categoría *
                <select
                  required
                  value={form.categoria}
                  onChange={(e) => onField('categoria', e.target.value)}
                >
                  <option value="">Selecciona una categoría</option>
                  {categoriasProducto.map((c) => (
                    <option key={c.key} value={c.key}>{c.label}</option>
                  ))}
                </select>
              </label>

              <label>
                Unidad de medida
                <select
                  value={form.unidadMedida}
                  onChange={(e) => onField('unidadMedida', e.target.value)}
                >
                  <option value="">Selecciona una unidad</option>
                  {unidadesMedida.map((u) => (
                    <option key={u.key} value={u.key}>{u.label}</option>
                  ))}
                </select>
              </label>

              <label>
                Código de barras
                <input
                  value={form.codigoBarras}
                  onChange={(e) => onField('codigoBarras', e.target.value)}
                  placeholder="Ej: 7501020512342"
                />
              </label>

              <label>
                Descripción
                <input
                  value={form.descripcion}
                  onChange={(e) => onField('descripcion', e.target.value)}
                  placeholder="Breve descripción del producto"
                />
              </label>
            </div>

            {okMsg && <div className="success-box">{okMsg}</div>}
            {errMsg && <div className="error-box">{errMsg}</div>}

            <button type="submit" disabled={guardando}>
              {guardando ? 'Guardando...' : 'Registrar producto'}
            </button>
          </form>
        </section>
      )}

      {/* ── Filtros de búsqueda ── */}
      <div className="filter-bar">
        <input
          type="text"
          placeholder="Buscar por nombre…"
          value={busqueda}
          onChange={(e) => setBusqueda(e.target.value)}
        />
        <select value={filtroCat} onChange={(e) => setFiltroCat(e.target.value)}>
          <option value="">Todas las categorías</option>
          {categoriasProducto.map((c) => (
            <option key={c.key} value={c.key}>{c.label}</option>
          ))}
        </select>
      </div>

      {/* ── Lista de productos ── */}
      {loading && <Loader label="Cargando productos…" />}
      {error && <div className="error-box"><strong>Error</strong><p>{error}</p></div>}

      {!loading && !error && productos && productos.length === 0 && (
        <div className="empty-state">
          <h3>Sin productos</h3>
          <p>No se encontraron productos con los filtros actuales.</p>
        </div>
      )}

      {!loading && !error && productos && productos.length > 0 && (
        <div className="table-wrap">
          <table className="precios-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Marca</th>
                <th>Categoría</th>
                <th>Código barras</th>
                <th>Unidad</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {productos.map((p) => (
                <tr key={p.id}>
                  {editId === p.id ? (
                    <>
                      <td style={{ fontVariantNumeric: 'tabular-nums' }}>#{p.id}</td>
                      <td>
                        <input
                          value={editForm.nombre}
                          onChange={(e) => onEditField('nombre', e.target.value)}
                          style={inputInlineStyle}
                        />
                      </td>
                      <td>
                        <input
                          value={editForm.marca}
                          onChange={(e) => onEditField('marca', e.target.value)}
                          style={inputInlineStyle}
                        />
                      </td>
                      <td>
                        <select
                          value={editForm.categoria}
                          onChange={(e) => onEditField('categoria', e.target.value)}
                          style={inputInlineStyle}
                        >
                          <option value="">—</option>
                          {categoriasProducto.map((c) => (
                            <option key={c.key} value={c.key}>{c.label}</option>
                          ))}
                        </select>
                      </td>
                      <td>
                        <input
                          value={editForm.codigoBarras}
                          onChange={(e) => onEditField('codigoBarras', e.target.value)}
                          style={inputInlineStyle}
                        />
                      </td>
                      <td>
                        <select
                          value={editForm.unidadMedida}
                          onChange={(e) => onEditField('unidadMedida', e.target.value)}
                          style={inputInlineStyle}
                        >
                          <option value="">—</option>
                          {unidadesMedida.map((u) => (
                            <option key={u.key} value={u.key}>{u.label}</option>
                          ))}
                        </select>
                      </td>

                      <td>
                        <div className="acciones-container" style={{ flexDirection: 'row' }}>
                          <button
                            type="button"
                            className="btn-action btn-success"
                            disabled={editGuardando}
                            onClick={saveEdit}
                          >
                            {editGuardando ? '…' : 'Guardar'}
                          </button>
                          <button
                            type="button"
                            className="btn-action"
                            style={{ background: 'var(--c-bg)', border: '1px solid var(--c-border)' }}
                            onClick={cancelEdit}
                          >
                            Cancelar
                          </button>
                        </div>
                      </td>
                    </>
                  ) : (
                    <>
                      <td style={{ fontVariantNumeric: 'tabular-nums', fontWeight: 600, color: 'var(--c-muted)' }}>
                        #{p.id}
                      </td>
                      <td style={{ fontWeight: 600 }}>{p.nombre}</td>
                      <td>{p.marca || <span className="muted">—</span>}</td>
                      <td>
                        <span className="chip">{formatCategoria(p.categoria, categoriasProducto)}</span>
                      </td>
                      <td>
                        {p.codigoBarras ? (
                          <code style={{ fontSize: '0.8rem', background: 'var(--c-bg)', padding: '0.15rem 0.4rem', borderRadius: '4px' }}>
                            {p.codigoBarras}
                          </code>
                        ) : (
                          <span className="muted">—</span>
                        )}
                      </td>
                      <td>{p.unidadMedida || <span className="muted">—</span>}</td>
                      <td>
                        {!!p.activo ? (
                          <span style={{ color: 'var(--c-success)', fontWeight: 600, fontSize: '0.85rem' }}>● Activo</span>
                        ) : (
                          <span style={{ color: 'var(--c-danger)', fontWeight: 600, fontSize: '0.85rem' }}>● Inactivo</span>
                        )}
                      </td>
                      <td>
                        <div className="acciones-container" style={{ flexDirection: 'row' }}>
                          <button
                            type="button"
                            className="btn-action btn-primary"
                            onClick={() => startEdit(p)}
                          >
                            Editar
                          </button>
                          <button
                            type="button"
                            className={!!p.activo ? 'btn-action btn-danger' : 'btn-action btn-success'}
                            onClick={() => handleToggle(p.id, !!p.activo, p.nombre)}
                          >
                            {!!p.activo ? 'Desactivar' : 'Activar'}
                          </button>
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
    </div>
  );
}

/* ── Helpers ── */

const inputInlineStyle = {
  fontFamily: 'inherit',
  fontSize: '0.85rem',
  padding: '0.35rem 0.5rem',
  border: '1px solid var(--c-border)',
  borderRadius: 'var(--radius-sm)',
  width: '100%',
};

function formatCategoria(cat, categoriasProducto) {
  if (!cat) return '—';
  const found = categoriasProducto.find((c) => c.key === cat);
  return found ? found.label : cat;
}
