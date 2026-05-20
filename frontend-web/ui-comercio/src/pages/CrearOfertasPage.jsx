import { useState, useEffect } from 'react';
import { getProductos, crearOferta, getOfertasComercio } from '../api/comercio';
import { getPreciosPorComercio } from '../api/catalogo';

const TIPOS_PROMOCION = [
  { value: '2x1', label: '2x1 — Lleva 2, paga 1' },
  { value: '3x2', label: '3x2 — Lleva 3, paga 2' },
  { value: 'COMBO', label: 'Combo — Paquete especial' },
  { value: 'PORCENTAJE', label: 'Descuento por porcentaje' },
  { value: 'PRECIO_ESPECIAL', label: 'Precio especial directo' },
  { value: 'OTRO', label: 'Otro tipo de promoción' },
];

export default function CrearOfertasPage({ comercioId }) {
  const [productos, setProductos] = useState([]);
  const [misOfertas, setMisOfertas] = useState([]);
  const [esFlexible, setEsFlexible] = useState(false);

  const [form, setForm] = useState({
    idProducto: '',
    titulo: '',
    descripcion: '',
    precioOferta: '',
    precioOriginal: '',
    fechaFin: '',
    tipoPromocion: '',
    productoIdFlexible: '',
  });

  const [mensaje, setMensaje] = useState({ texto: '', tipo: '' });
  const [enviando, setEnviando] = useState(false);

  const cargarDatos = async () => {
    try {
      const resP = await getProductos();
      const catalogoGlobal = resP.data.productos || (Array.isArray(resP.data) ? resP.data : []);

      const resPrecios = await getPreciosPorComercio(comercioId);
      const misPreciosList = resPrecios.data.precios || (Array.isArray(resPrecios.data) ? resPrecios.data : []);

      const misProductosParaOfertas = misPreciosList.map(precio => {
        const prodInfo = catalogoGlobal.find(p => p.id === precio.productoId);
        return {
          id: precio.productoId,
          nombre: prodInfo ? prodInfo.nombre : `Producto #${precio.productoId}`,
          precio: precio.precio || 0
        };
      });

      setProductos(misProductosParaOfertas);

      const resO = await getOfertasComercio(comercioId);
      const listaO = resO.data.ofertas || (Array.isArray(resO.data) ? resO.data : []);
      setMisOfertas(listaO.map(of => ({
        id: of.id,
        producto: of.titulo,
        precioOferta: of.precio_oferta || of.precioOferta || 0,
        tipoPromocion: of.tipo_promocion || of.tipoPromocion || '',
        fechaFin: of.fecha_fin || of.fechaFin || '---'
      })));
    } catch (error) { console.error("Error al cargar:", error); }
  };

  useEffect(() => { cargarDatos(); }, []);

  const handleProductoChange = (idProducto) => {
    const prod = productos.find(p => p.id.toString() === idProducto.toString());
    setForm({
      ...form,
      idProducto,
      precioOriginal: prod ? prod.precio.toString() : ''
    });
  };

  const handleProductoFlexibleChange = (productoIdFlexible) => {
    const prod = productos.find(p => p.id.toString() === productoIdFlexible.toString());
    setForm({
      ...form,
      productoIdFlexible,
      precioOriginal: prod ? prod.precio.toString() : form.precioOriginal
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setEnviando(true);
    try {
      const precioOriginal = parseFloat(form.precioOriginal) || 0;
      const precioOferta = parseFloat(form.precioOferta) || 0;
      const porcentajeDescuento = precioOriginal > 0
        ? ((precioOriginal - precioOferta) / precioOriginal) * 100
        : 0;

      let payload = {
        comercio_id: parseInt(comercioId, 10),
        precio_oferta: precioOferta,
        fecha_inicio: new Date().toISOString().split('T')[0],
        fecha_fin: form.fechaFin,
        precio_original: precioOriginal,
        porcentaje_descuento: Math.max(0, Math.round(porcentajeDescuento * 100) / 100)
      };

      if (esFlexible) {
        const tipoLabel = TIPOS_PROMOCION.find(t => t.value === form.tipoPromocion)?.label || form.tipoPromocion;
        const prod = productos.find(p => p.id.toString() === form.productoIdFlexible?.toString());
        const productoNombre = prod?.nombre || '';

        payload.titulo = form.titulo || `${tipoLabel}${productoNombre ? ': ' + productoNombre : ''}`;
        payload.descripcion = form.descripcion || `Promoción ${tipoLabel} en sucursal.`;
        payload.tipo_promocion = form.tipoPromocion;
        payload.producto_id = form.productoIdFlexible ? parseInt(form.productoIdFlexible) : 0;
      } else {
        const prod = productos.find(p => p.id.toString() === form.idProducto.toString());
        payload.titulo = `Oferta: ${prod?.nombre || 'Producto'}`;
        payload.descripcion = `Precio especial directo en sucursal.`;
        payload.tipo_promocion = 'DESCUENTO';
        payload.producto_id = parseInt(form.idProducto) || 0;
      }

      await crearOferta(payload);
      setMensaje({ texto: 'Oferta publicada con éxito 🎉. Notificación push enviada a consumidores.', tipo: 'exito' });
      setForm({ idProducto: '', titulo: '', descripcion: '', precioOferta: '', precioOriginal: '', fechaFin: '', tipoPromocion: '', productoIdFlexible: '' });
      cargarDatos();
    } catch (error) {
      setMensaje({ texto: 'Error al conectar con el servicio.', tipo: 'error' });
    } finally { setEnviando(false); }
  };

  const precioOrigNum = parseFloat(form.precioOriginal) || 0;
  const precioOfertaNum = parseFloat(form.precioOferta) || 0;
  const descuentoPreview = precioOrigNum > 0 && precioOfertaNum > 0
    ? Math.round(((precioOrigNum - precioOfertaNum) / precioOrigNum) * 10000) / 100
    : 0;

  const tipoSeleccionado = TIPOS_PROMOCION.find(t => t.value === form.tipoPromocion);

  return (
    <div className="container">
      <div className="section-header">
        <h1 style={{color: 'var(--c-primary-dark)'}}>Lanzar Ofertas</h1>
      </div>

      {/* Selector de Modo */}
      <div style={{display: 'flex', gap: '1rem', marginBottom: '2rem', borderBottom: '1px solid var(--c-border)', paddingBottom: '1rem'}}>
        <button
          onClick={() => setEsFlexible(false)}
          className={!esFlexible ? "nav-cta" : "btn-ghost"}
          style={{padding: '0.5rem 1rem', borderRadius: '0.5rem', border: 'none', cursor: 'pointer', fontWeight: 600}}
        >
          Por Producto
        </button>
        <button
          onClick={() => setEsFlexible(true)}
          className={esFlexible ? "nav-cta" : "btn-ghost"}
          style={{padding: '0.5rem 1rem', borderRadius: '0.5rem', border: 'none', cursor: 'pointer', fontWeight: 600}}
        >
          Promoción Flexible (2x1, Combos)
        </button>
      </div>

      <div className="grid grid-2" style={{gap: '2rem'}}>
        <div className="card">
          <h2 style={{fontSize: '1.25rem', marginBottom: '1.5rem'}}>{esFlexible ? 'Configurar Promo Flexible' : 'Bajar Precio a Producto'}</h2>

          <form onSubmit={handleSubmit} className="resenia-form">
            {!esFlexible ? (
              <>
                <label>
                  Selecciona el Producto
                  <select value={form.idProducto} onChange={e => handleProductoChange(e.target.value)} required>
                    <option value="">-- Elige un producto --</option>
                    {productos.map(p => <option key={p.id} value={p.id}>{p.nombre} (${p.precio.toFixed(2)})</option>)}
                  </select>
                </label>
                {form.precioOriginal && (
                  <div className="info-card" style={{padding: '0.75rem', background: '#eff6ff', borderColor: '#bfdbfe', color: '#1e40af'}}>
                    Precio actual en catálogo: <strong>${parseFloat(form.precioOriginal).toFixed(2)}</strong>
                  </div>
                )}
              </>
            ) : (
              <>
                {/* Tipo de Promoción */}
                <label>
                  Tipo de Promoción
                  <select value={form.tipoPromocion} onChange={e => setForm({ ...form, tipoPromocion: e.target.value })} required>
                    <option value="">-- Selecciona el tipo --</option>
                    {TIPOS_PROMOCION.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
                  </select>
                </label>

                {tipoSeleccionado && (
                  <div className="info-card" style={{padding: '0.75rem', background: '#fef3c7', borderColor: '#fcd34d', color: '#92400e'}}>
                    Tipo seleccionado: <strong>{tipoSeleccionado.label}</strong>
                  </div>
                )}

                {/* Producto asociado (opcional) */}
                <label>
                  Producto Asociado
                  <select value={form.productoIdFlexible} onChange={e => handleProductoFlexibleChange(e.target.value)}>
                    <option value="">-- Sin producto asociado (opcional) --</option>
                    {productos.map(p => <option key={p.id} value={p.id}>{p.nombre} (${p.precio.toFixed(2)})</option>)}
                  </select>
                </label>

                {form.productoIdFlexible && (
                  <div className="info-card" style={{padding: '0.75rem', background: '#eff6ff', borderColor: '#bfdbfe', color: '#1e40af'}}>
                    Precio del producto: <strong>${parseFloat(form.precioOriginal).toFixed(2)}</strong>
                  </div>
                )}

                <label>
                  Título de la Promoción (ej. 3x2 en Leche Alpura)
                  <input type="text" value={form.titulo} onChange={e => setForm({ ...form, titulo: e.target.value })} required />
                </label>
                <label>
                  Descripción / Reglas de la promoción
                  <textarea value={form.descripcion} onChange={e => setForm({ ...form, descripcion: e.target.value })} required />
                </label>
                <label>
                  Precio Original / Regular ($)
                  <input type="number" step="0.01" value={form.precioOriginal} onChange={e => setForm({ ...form, precioOriginal: e.target.value })} required />
                </label>
              </>
            )}

            <label>
              Precio de Oferta / Combo ($)
              <input type="number" step="0.01" value={form.precioOferta} onChange={e => setForm({ ...form, precioOferta: e.target.value })} required />
            </label>

            {descuentoPreview > 0 && (
              <div className="success-box" style={{padding: '0.75rem', fontSize: '0.9rem'}}>
                Descuento: <strong>{descuentoPreview.toFixed(1)}%</strong> (${precioOrigNum.toFixed(2)} → ${precioOfertaNum.toFixed(2)})
              </div>
            )}

            <label>
              Fecha de Vencimiento
              <input type="date" value={form.fechaFin} onChange={e => setForm({ ...form, fechaFin: e.target.value })} required />
            </label>

            {mensaje.texto && <div className={mensaje.tipo === 'error' ? 'error-box' : 'success-box'}><p>{mensaje.texto}</p></div>}
            
            <button type="submit" disabled={enviando} className="nav-cta" style={{marginTop: '0.5rem'}}>
              {enviando ? 'Publicando...' : 'Publicar y Notificar a Clientes 🔔'}
            </button>
          </form>
        </div>

        <div className="card">
          <h2 style={{fontSize: '1.25rem', marginBottom: '1.5rem'}}>Ofertas en el Sistema</h2>
          <div style={{overflowX: 'auto'}}>
            <table className="precios-table">
              <thead>
                <tr>
                  <th>Descripción</th>
                  <th>Tipo</th>
                  <th>Precio</th>
                  <th>Acción</th>
                </tr>
              </thead>
              <tbody>
                {misOfertas.map(of => (
                  <tr key={of.id}>
                    <td>{of.producto}</td>
                    <td>
                      {of.tipoPromocion ? (
                        <span className="estatus estatus-en_revision">{of.tipoPromocion}</span>
                      ) : (
                        <span className="muted small">—</span>
                      )}
                    </td>
                    <td style={{ color: 'var(--c-success)', fontWeight: 'bold' }}>${of.precioOferta.toFixed(2)}</td>
                    <td><span style={{ color: 'var(--c-danger)', cursor: 'pointer', fontWeight: 600, fontSize: '0.85rem' }}>Cancelar</span></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
