import { useState, useEffect } from 'react';
import { getProductos, crearOferta, getOfertasComercio } from '../api/comercio';
import { getPreciosPorComercio } from '../api/catalogo';

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
    fechaFin: ''
  });

  const [mensaje, setMensaje] = useState({ texto: '', tipo: '' });
  const [enviando, setEnviando] = useState(false);

  const cargarDatos = async () => {
    try {
      const resP = await getProductos();
      const catalogoGlobal = Array.isArray(resP.data) ? resP.data : (resP.data.productos || []);

      const resPrecios = await getPreciosPorComercio(comercioId);
      const misPreciosList = resPrecios.data.precios || resPrecios.data || [];

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
      const listaO = resO.data.ofertas || [];
      setMisOfertas(listaO.map(of => ({
        id: of.id,
        producto: of.titulo,
        precioOferta: of.precio_oferta || of.precioOferta || 0,
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

  const handleSubmit = async (e) => {
    e.preventDefault();
    setEnviando(true);
    try {
      const precioOriginal = parseFloat(form.precioOriginal) || 0;
      const precioOferta = parseFloat(form.precioOferta);
      const porcentajeDescuento = precioOriginal > 0
        ? ((precioOriginal - precioOferta) / precioOriginal) * 100
        : 0;

      let payload = {
        comercio_id: comercioId,
        precio_oferta: precioOferta,
        fecha_inicio: new Date().toISOString().split('T')[0],
        fecha_fin: form.fechaFin,
        precio_original: precioOriginal,
        porcentaje_descuento: Math.max(0, Math.round(porcentajeDescuento * 100) / 100)
      };

      if (esFlexible) {
        payload.titulo = form.titulo;
        payload.descripcion = form.descripcion;
      } else {
        const prod = productos.find(p => p.id.toString() === form.idProducto.toString());
        payload.titulo = `Oferta: ${prod?.nombre || 'Producto'}`;
        payload.descripcion = `Precio especial directo en sucursal.`;
      }

      await crearOferta(payload);
      setMensaje({ texto: 'Oferta publicada con éxito 🎉. Notificación push enviada a consumidores.', tipo: 'exito' });
      setForm({ idProducto: '', titulo: '', descripcion: '', precioOferta: '', precioOriginal: '', fechaFin: '' });
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

  return (
    <div>
      <h1 style={styles.title}>Lanzar Ofertas</h1>

      {/* Selector de Modo */}
      <div style={styles.switchContainer}>
        <button
          onClick={() => setEsFlexible(false)}
          style={!esFlexible ? styles.activeTab : styles.tab}
        >
          Por Producto
        </button>
        <button
          onClick={() => setEsFlexible(true)}
          style={esFlexible ? styles.activeTab : styles.tab}
        >
          Promoción Flexible (2x1, Combos)
        </button>
      </div>

      <div style={styles.gridContainer}>
        <div style={styles.card}>
          <h2 style={styles.cardTitle}>{esFlexible ? 'Configurar Promo Flexible' : 'Bajar Precio a Producto'}</h2>

          <form onSubmit={handleSubmit}>
            {!esFlexible ? (
              <>
                <div style={styles.formGroup}>
                  <label style={styles.label}>Selecciona el Producto</label>
                  <select style={styles.input} value={form.idProducto} onChange={e => handleProductoChange(e.target.value)} required>
                    <option value="">-- Elige un producto --</option>
                    {productos.map(p => <option key={p.id} value={p.id}>{p.nombre} (${p.precio.toFixed(2)})</option>)}
                  </select>
                </div>
                {form.precioOriginal && (
                  <div style={styles.infoBanner}>
                    Precio actual en catálogo: <strong>${parseFloat(form.precioOriginal).toFixed(2)}</strong>
                  </div>
                )}
              </>
            ) : (
              <>
                <div style={styles.formGroup}>
                  <label style={styles.label}>Título (ej. 3x2 en Leche)</label>
                  <input type="text" style={styles.input} value={form.titulo} onChange={e => setForm({ ...form, titulo: e.target.value })} required />
                </div>
                <div style={styles.formGroup}>
                  <label style={styles.label}>Descripción de la regla</label>
                  <textarea style={styles.textArea} value={form.descripcion} onChange={e => setForm({ ...form, descripcion: e.target.value })} required />
                </div>
                <div style={styles.formGroup}>
                  <label style={styles.label}>Precio Original / Regular ($)</label>
                  <input type="number" step="0.01" style={styles.input} value={form.precioOriginal} onChange={e => setForm({ ...form, precioOriginal: e.target.value })} required />
                </div>
              </>
            )}

            <div style={styles.formGroup}>
              <label style={styles.label}>Precio de Oferta / Combo ($)</label>
              <input type="number" step="0.01" style={styles.input} value={form.precioOferta} onChange={e => setForm({ ...form, precioOferta: e.target.value })} required />
            </div>

            {descuentoPreview > 0 && (
              <div style={styles.discountBanner}>
                Descuento: <strong>{descuentoPreview.toFixed(1)}%</strong> (${precioOrigNum.toFixed(2)} → ${precioOfertaNum.toFixed(2)})
              </div>
            )}

            <div style={styles.formGroup}>
              <label style={styles.label}>Fecha de Vencimiento</label>
              <input type="date" style={styles.input} value={form.fechaFin} onChange={e => setForm({ ...form, fechaFin: e.target.value })} required />
            </div>

            {mensaje.texto && <p style={{ color: mensaje.tipo === 'error' ? '#ef4444' : '#10b981', marginBottom: '1rem' }}>{mensaje.texto}</p>}
            <button type="submit" disabled={enviando} style={styles.button}>
              {enviando ? 'Publicando...' : 'Publicar y Notificar a Clientes 🔔'}
            </button>
          </form>
        </div>

        <div style={styles.card}>
          <h2 style={styles.cardTitle}>Ofertas en el Sistema</h2>
          <table style={styles.table}>
            <thead>
              <tr>
                <th style={styles.th}>Descripción</th>
                <th style={styles.th}>Precio</th>
                <th style={styles.th}>Acción</th>
              </tr>
            </thead>
            <tbody>
              {misOfertas.map(of => (
                <tr key={of.id} style={styles.tr}>
                  <td style={styles.td}>{of.producto}</td>
                  <td style={{ ...styles.td, color: '#10b981', fontWeight: 'bold' }}>${of.precioOferta.toFixed(2)}</td>
                  <td style={styles.td}><span style={{ color: '#ef4444', cursor: 'pointer' }}>Cancelar</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

const styles = {
  title: { fontSize: '1.875rem', fontWeight: 'bold', color: '#111827', marginBottom: '1.5rem' },
  switchContainer: { display: 'flex', gap: '1rem', marginBottom: '2rem', borderBottom: '1px solid #e5e7eb', paddingBottom: '1rem' },
  tab: { padding: '0.5rem 1rem', border: 'none', background: 'none', cursor: 'pointer', color: '#6b7280', fontWeight: '500' },
  activeTab: { padding: '0.5rem 1rem', border: 'none', background: '#f3f4f6', borderRadius: '0.5rem', cursor: 'pointer', color: '#111827', fontWeight: '600' },
  gridContainer: { display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(350px, 1fr))', gap: '2rem' },
  card: { background: '#fff', padding: '1.5rem', borderRadius: '0.5rem', border: '1px solid #e5e7eb' },
  cardTitle: { fontSize: '1.1rem', marginBottom: '1.5rem', fontWeight: '600' },
  formGroup: { marginBottom: '1rem' },
  label: { display: 'block', fontSize: '0.875rem', marginBottom: '0.5rem', fontWeight: '500' },
  input: { width: '100%', padding: '0.6rem', borderRadius: '0.3rem', border: '1px solid #d1d5db', boxSizing: 'border-box' },
  textArea: { width: '100%', padding: '0.6rem', borderRadius: '0.3rem', border: '1px solid #d1d5db', boxSizing: 'border-box', minHeight: '60px' },
  button: { width: '100%', padding: '0.75rem', background: '#2563eb', color: '#fff', border: 'none', borderRadius: '0.3rem', fontWeight: '600', cursor: 'pointer' },
  infoBanner: { background: '#eff6ff', border: '1px solid #bfdbfe', borderRadius: '0.3rem', padding: '0.5rem 0.75rem', marginBottom: '1rem', fontSize: '0.85rem', color: '#1e40af' },
  discountBanner: { background: '#f0fdf4', border: '1px solid #bbf7d0', borderRadius: '0.3rem', padding: '0.5rem 0.75rem', marginBottom: '1rem', fontSize: '0.85rem', color: '#166534' },
  table: { width: '100%', borderCollapse: 'collapse' },
  th: { textAlign: 'left', padding: '0.5rem', borderBottom: '1px solid #e5e7eb', fontSize: '0.8rem' },
  tr: { borderBottom: '1px solid #f3f4f6' },
  td: { padding: '0.8rem 0.5rem', fontSize: '0.85rem' }
};