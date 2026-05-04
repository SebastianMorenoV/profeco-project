import { useEffect, useState } from 'react';
import { getProductos, registrarPrecio, getPreciosComercio } from '../api/comercio';

export default function RegistrarPreciosPage() {
  const [productos, setProductos] = useState([]);
  const [misPrecios, setMisPrecios] = useState([]);
  const [form, setForm] = useState({ idProducto: '', precio: '' });
  const [mensaje, setMensaje] = useState({ texto: '', tipo: '' });
  const [enviando, setEnviando] = useState(false);

  useEffect(() => {
    const cargarTodo = async () => {
      try {
        const resP = await getProductos();
        setProductos(resP.data.productos || resP.data.items || []);

        try {
          const resPrecios = await getPreciosComercio(1);
          if (resPrecios.data && resPrecios.data.precios) {
            setMisPrecios(resPrecios.data.precios);
            localStorage.setItem('precios_simulados', JSON.stringify(resPrecios.data.precios));
            return; 
          }
        } catch (e) {
          console.warn("Backend de precios no listo, usando respaldo local.");
        }

        const local = localStorage.getItem('precios_simulados');
        if (local) setMisPrecios(JSON.parse(local));

      } catch (error) {
        console.error("Error crítico al cargar:", error);
      }
    };
    cargarTodo();
  }, []);

  const manejarEdicion = (item) => {
    setForm({
      idProducto: item.idProducto.toString(), 
      precio: item.precio                     
    });
    setMensaje({ texto: 'Editando producto. Modifica el precio y guarda.', tipo: 'exito' });
    
    window.scrollTo({ top: 0, behavior: 'smooth' }); 
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setEnviando(true);
    try {
      await registrarPrecio({
        producto_id: form.idProducto, 
        precio: parseFloat(form.precio),
        comercio_id: 1 
      });

      const prod = productos.find(p => p.id.toString() === form.idProducto.toString());
      const nuevoItem = {
        idProducto: form.idProducto,
        producto: prod ? prod.nombre : 'Producto',
        precio: parseFloat(form.precio),
        fecha: new Date().toISOString().split('T')[0]
      };

      const nuevaLista = [...misPrecios.filter(p => p.idProducto !== form.idProducto), nuevoItem];
      setMisPrecios(nuevaLista);

      localStorage.setItem('precios_simulados', JSON.stringify(nuevaLista));

      setMensaje({ texto: '¡Guardado correctamente!', tipo: 'exito' });
      setForm({ idProducto: '', precio: '' });
    } catch (error) {
      setMensaje({ texto: 'Error al guardar en el servidor.', tipo: 'error' });
    } finally {
      setEnviando(false);
    }
  };

  return (
    <div>
      <h1 style={styles.title}>Gestión de Precios</h1>
      <p style={styles.subtitle}>Actualiza el costo de los artículos en tu sucursal.</p>

      <div style={styles.gridContainer}>
        {/* Formulario */}
        <div style={styles.card}>
          <h2 style={styles.cardTitle}>Registrar / Editar Precio</h2>
          <form onSubmit={handleSubmit}>
            <div style={styles.formGroup}>
              <label style={styles.label}>Selecciona el Producto</label>
              <select 
                style={styles.input} 
                value={form.idProducto}
                onChange={(e) => setForm({ ...form, idProducto: e.target.value })}
                required
              >
                <option value="">-- Elige un producto --</option>
                {productos.map((p, i) => (
                  <option key={i} value={p.id}>{p.nombre}</option>
                ))}
              </select>
            </div>

            <div style={styles.formGroup}>
              <label style={styles.label}>Precio de Venta ($)</label>
              <input 
                type="number" 
                step="0.01"
                style={styles.input} 
                placeholder="Ej. 25.50"
                value={form.precio}
                onChange={(e) => setForm({ ...form, precio: e.target.value })}
                required
              />
            </div>

            {mensaje.texto && (
              <p style={{ color: mensaje.tipo === 'error' ? '#ef4444' : '#10b981', fontSize: '0.875rem', marginBottom: '1rem' }}>
                {mensaje.texto}
              </p>
            )}

            <button type="submit" disabled={enviando} style={styles.button}>
              {enviando ? 'Guardando...' : 'Guardar Precio'}
            </button>
          </form>
        </div>

        {/* Tabla */}
        <div style={styles.card}>
          <h2 style={styles.cardTitle}>Tus Precios Actuales</h2>
          
          <div style={styles.tableContainer}>
            <table style={styles.table}>
              <thead>
                <tr>
                  <th style={styles.th}>Producto</th>
                  <th style={styles.th}>Precio</th>
                  <th style={styles.th}>Acción</th>
                </tr>
              </thead>
              <tbody>
                {misPrecios.length === 0 ? (
                  <tr>
                    <td colSpan="3" style={styles.emptyState}>No tienes precios registrados.</td>
                  </tr>
                ) : (
                  misPrecios.map((item, index) => (
                    <tr key={index} style={styles.tr}>
                      <td style={styles.td}>{item.producto}</td>
                      <td style={styles.td}>${item.precio.toFixed(2)}</td>
                      <td style={styles.td}>
                        {/* BOTÓN EDITAR CON FUNCIONALIDAD */}
                        <button 
                          type="button" 
                          onClick={() => manejarEdicion(item)} 
                          style={styles.editButton}
                        >
                          Editar
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>

      </div>
    </div>
  );
}

const styles = {
  title: { fontSize: '1.875rem', fontWeight: 'bold', color: '#111827', margin: '0 0 0.5rem 0' },
  subtitle: { color: '#6b7280', margin: '0 0 2rem 0' },
  gridContainer: { display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '2rem', alignItems: 'start' },
  card: { backgroundColor: '#fff', padding: '1.5rem', borderRadius: '0.5rem', border: '1px solid #e5e7eb', boxShadow: '0 1px 3px 0 rgba(0,0,0,0.1)' },
  cardTitle: { fontSize: '1.25rem', fontWeight: '600', color: '#111827', margin: '0 0 1.5rem 0' },
  formGroup: { marginBottom: '1.5rem' },
  label: { display: 'block', fontSize: '0.875rem', fontWeight: '500', color: '#374151', marginBottom: '0.5rem' },
  input: { width: '100%', padding: '0.75rem', borderRadius: '0.375rem', border: '1px solid #d1d5db', outline: 'none', boxSizing: 'border-box' },
  button: { width: '100%', padding: '0.75rem', backgroundColor: '#111827', color: '#fff', border: 'none', borderRadius: '0.375rem', fontWeight: '600', cursor: 'pointer', marginTop: '1rem' },
  tableContainer: { overflowX: 'auto' },
  table: { width: '100%', borderCollapse: 'collapse', textAlign: 'left' },
  th: { padding: '0.75rem', borderBottom: '2px solid #e5e7eb', color: '#4b5563', fontSize: '0.875rem', fontWeight: '600' },
  tr: { borderBottom: '1px solid #e5e7eb' },
  td: { padding: '1rem 0.75rem', color: '#111827', fontSize: '0.875rem' },
  emptyState: { padding: '2rem', textAlign: 'center', color: '#6b7280' },
  editButton: { backgroundColor: 'transparent', border: 'none', color: '#3b82f6', fontWeight: '500', cursor: 'pointer', padding: '0', textDecoration: 'underline' }
};