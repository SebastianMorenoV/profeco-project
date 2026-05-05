import { useState, useEffect } from 'react';
import { getProductos, getPreciosPorComercio, registrarPrecio, actualizarPrecio } from '../api/catalogo';

export default function RegistrarPreciosPage({ comercioId }) {
  const [productos, setProductos] = useState([]);
  const [productosFiltrados, setProductosFiltrados] = useState([]);
  const [busqueda, setBusqueda] = useState("");
  const [misPrecios, setMisPrecios] = useState([]);
  const [cargando, setCargando] = useState(true);
  
  // Estado para el formulario
  const [productoSeleccionado, setProductoSeleccionado] = useState("");
  const [precioInput, setPrecioInput] = useState("");

  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    try {
      setCargando(true);
      const [resProductos, resPrecios] = await Promise.all([
        getProductos(),
        getPreciosPorComercio(comercioId)
      ]);

      // Extraer arreglos (considerando la envoltura de gRPC)
      const listProd = resProductos.data.productos || resProductos.data || [];
      setProductos(listProd);
      setProductosFiltrados(listProd);
      setMisPrecios(resPrecios.data.precios || resPrecios.data || []);
    } catch (error) {
      console.error("Error al cargar el catálogo:", error);
    } finally {
      setCargando(false);
    }
  };

  const manejarBusqueda = (e) => {
    const term = e.target.value.toLowerCase();
    setBusqueda(term);
    if(term.trim() === '') {
      setProductosFiltrados(productos);
    } else {
      setProductosFiltrados(productos.filter(p => 
        p.nombre?.toLowerCase().includes(term) || 
        p.marca?.toLowerCase().includes(term)
      ));
    }
  };

  const manejarGuardarPrecio = async (e) => {
    e.preventDefault();
    if (!productoSeleccionado || !precioInput) return alert("Selecciona un producto y pon un precio.");

    const productoId = parseInt(productoSeleccionado);
    const precio = parseFloat(precioInput);

    // Buscamos si este comercio ya le había puesto precio a este producto
    const precioExistente = misPrecios.find(p => p.productoId === productoId);

    try {
      if (precioExistente) {
        // ACTUALIZAR (PUT)
        await actualizarPrecio(precioExistente.id, { precio });
        alert("¡Precio actualizado con éxito!");
      } else {
        // CREAR (POST)
        await registrarPrecio({ productoId, comercioId: comercioId, precio });
        alert("¡Nuevo precio registrado en el catálogo!");
      }
      
      // Limpiar formulario y recargar tabla
      setProductoSeleccionado("");
      setPrecioInput("");
      cargarDatos();

    } catch (error) {
      console.error("Error al guardar:", error);
      alert("Hubo un error al guardar el precio.");
    }
  };

  // Función para encontrar el nombre del producto dado su ID
  const getNombreProducto = (id) => {
    const prod = productos.find(p => p.id === id);
    return prod ? `${prod.nombre} (${prod.marca})` : `Producto #${id}`;
  };

  if (cargando) return <div style={{ padding: '2rem' }}>Cargando catálogo y precios...</div>;

  return (
    <div>
      <h1 style={styles.title}>Gestión de Precios</h1>
      
      {/* FORMULARIO DE REGISTRO/ACTUALIZACIÓN */}
      <div style={styles.card}>
        <h2 style={styles.subtitle}>Subir o Actualizar Precio</h2>
        <form onSubmit={manejarGuardarPrecio} style={styles.form}>
          <div style={styles.formGroup}>
            <label style={styles.label}>Buscar en el Catálogo Global de Profeco</label>
            <input 
              type="text" 
              placeholder="Buscar por nombre o marca..." 
              value={busqueda}
              onChange={manejarBusqueda}
              style={{...styles.input, marginBottom: '0.5rem'}}
            />
            <select 
              style={styles.input} 
              value={productoSeleccionado} 
              onChange={(e) => setProductoSeleccionado(e.target.value)}
            >
              <option value="">-- Selecciona un producto para vender --</option>
              {productosFiltrados.map(p => (
                <option key={p.id} value={p.id}>
                  {p.nombre} - {p.marca} ({p.unidadMedida})
                </option>
              ))}
            </select>
          </div>

          <div style={styles.formGroup}>
            <label style={styles.label}>Precio de Venta ($)</label>
            <input 
              type="number" 
              step="0.01" 
              style={styles.input} 
              value={precioInput} 
              onChange={(e) => setPrecioInput(e.target.value)} 
              placeholder="Ej. 25.50"
            />
          </div>

          <button type="submit" style={styles.button}>Guardar Precio en Catálogo</button>
        </form>
      </div>

      <div style={styles.divider}></div>

      {/* TABLA DE PRECIOS ACTUALES */}
      <h2 style={styles.subtitle}>Mis Precios Publicados</h2>
      {misPrecios.length === 0 ? (
        <p style={styles.noData}>No has publicado precios aún.</p>
      ) : (
        <div style={styles.tableContainer}>
          <table style={styles.table}>
            <thead>
              <tr>
                <th style={styles.th}>ID Precio</th>
                <th style={styles.th}>Producto</th>
                <th style={styles.th}>Precio Actual</th>
                <th style={styles.th}>Última Actualización</th>
              </tr>
            </thead>
            <tbody>
              {misPrecios.map(p => (
                <tr key={p.id} style={styles.tr}>
                  <td style={styles.td}>#{p.id}</td>
                  <td style={styles.td}>{getNombreProducto(p.productoId)}</td>
                  <td style={styles.td}>${p.precio.toFixed(2)}</td>
                  <td style={styles.td}>{p.fechaReporte ? p.fechaReporte.split('T')[0] : 'N/A'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

const styles = {
  title: { fontSize: '1.875rem', fontWeight: 'bold', color: '#111827', margin: '0 0 1.5rem 0' },
  subtitle: { fontSize: '1.25rem', fontWeight: 'bold', color: '#374151', marginBottom: '1rem' },
  card: { backgroundColor: '#fff', padding: '1.5rem', borderRadius: '0.75rem', border: '1px solid #e5e7eb', maxWidth: '600px' },
  form: { display: 'flex', flexDirection: 'column', gap: '1rem' },
  formGroup: { display: 'flex', flexDirection: 'column', gap: '0.5rem' },
  label: { fontSize: '0.875rem', fontWeight: '600', color: '#4b5563' },
  input: { padding: '0.75rem', borderRadius: '0.375rem', border: '1px solid #d1d5db', fontSize: '1rem', outline: 'none' },
  button: { padding: '0.75rem', backgroundColor: '#2563eb', color: '#fff', border: 'none', borderRadius: '0.375rem', fontWeight: 'bold', cursor: 'pointer', marginTop: '0.5rem' },
  divider: { border: '0', borderTop: '1px solid #e5e7eb', margin: '2rem 0' },
  tableContainer: { overflowX: 'auto', backgroundColor: '#fff', borderRadius: '0.75rem', border: '1px solid #e5e7eb' },
  table: { width: '100%', borderCollapse: 'collapse', textAlign: 'left' },
  th: { backgroundColor: '#f9fafb', padding: '1rem', fontWeight: '600', color: '#4b5563', borderBottom: '1px solid #e5e7eb' },
  tr: { borderBottom: '1px solid #e5e7eb' },
  td: { padding: '1rem', color: '#111827' },
  noData: { color: '#6b7280', fontStyle: 'italic' }
};