import { useState, useEffect } from 'react';
import { getProductos, getPreciosPorComercio, registrarPrecio, actualizarPrecio } from '../api/catalogo';

export default function RegistrarPreciosPage({ comercioId }) {
  const [productos, setProductos] = useState([]);
  const [productosFiltrados, setProductosFiltrados] = useState([]);
  const [busqueda, setBusqueda] = useState("");
  const [misPrecios, setMisPrecios] = useState([]);
  const [cargando, setCargando] = useState(true);
  
  // Estado para el formulario de nuevo precio
  const [productoSeleccionado, setProductoSeleccionado] = useState("");
  const [precioInput, setPrecioInput] = useState("");

  // Estado para la edición de precios existentes
  const [editandoId, setEditandoId] = useState(null);
  const [precioEditado, setPrecioEditado] = useState("");

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
      const listProd = resProductos.data.productos || (Array.isArray(resProductos.data) ? resProductos.data : []);
      setProductos(listProd);
      setProductosFiltrados(listProd);
      setMisPrecios(resPrecios.data.precios || (Array.isArray(resPrecios.data) ? resPrecios.data : []));
    } catch (error) {
      console.error("Error al cargar el catálogo:", error);
    } finally {
      setCargando(false);
    }
  };

  // Calculamos los productos disponibles (los que el comercio AÚN NO tiene registrados)
  const productosDisponibles = productos.filter(
    p => !misPrecios.find(mp => mp.productoId === p.id)
  );

  // Aplicamos la búsqueda a los productos disponibles
  const productosParaSelect = productosDisponibles.filter(p => {
    const term = busqueda.toLowerCase().trim();
    if (!term) return true;
    return p.nombre?.toLowerCase().includes(term) || p.marca?.toLowerCase().includes(term);
  });

  const manejarBusqueda = (e) => {
    setBusqueda(e.target.value);
  };

  const manejarGuardarPrecio = async (e) => {
    e.preventDefault();
    if (!productoSeleccionado || !precioInput) return alert("Selecciona un producto y pon un precio.");

    const productoId = parseInt(productoSeleccionado);
    const precio = parseFloat(precioInput);

    try {
      await registrarPrecio({ productoId, comercioId: comercioId, precio });
      alert("¡Nuevo precio registrado en el catálogo!");
      
      // Limpiar formulario y recargar tabla
      setProductoSeleccionado("");
      setPrecioInput("");
      cargarDatos();

    } catch (error) {
      console.error("Error al guardar:", error);
      alert("Hubo un error al guardar el precio.");
    }
  };

  const iniciarEdicion = (precioObj) => {
    setEditandoId(precioObj.id);
    setPrecioEditado(precioObj.precio);
  };

  const guardarEdicion = async (precioId) => {
    if (!precioEditado) return;
    try {
      await actualizarPrecio(precioId, { precio: parseFloat(precioEditado) });
      alert("¡Precio actualizado con éxito!");
      setEditandoId(null);
      cargarDatos();
    } catch (error) {
      console.error("Error al actualizar:", error);
      alert("Hubo un error al actualizar el precio.");
    }
  };

  // Función para encontrar el nombre del producto dado su ID
  const getNombreProducto = (id) => {
    const prod = productos.find(p => p.id === id);
    return prod ? `${prod.nombre} (${prod.marca})` : `Producto #${id}`;
  };

  if (cargando) return <div className="loader"><div className="spinner"></div> Cargando catálogo y precios...</div>;

  return (
    <div className="container">
      <div className="section-header">
        <h1 style={{color: 'var(--c-primary-dark)'}}>Gestión de Precios</h1>
      </div>
      
      {/* FORMULARIO DE REGISTRO/ACTUALIZACIÓN */}
      <div className="card" style={{maxWidth: '600px'}}>
        <h2 style={{fontSize: '1.25rem', marginBottom: '1rem'}}>Subir o Actualizar Precio</h2>
        <form onSubmit={manejarGuardarPrecio} className="resenia-form">
          <label>
            Buscar en el Catálogo Global de Profeco
            <input 
              type="text" 
              placeholder="Buscar por nombre o marca..." 
              value={busqueda}
              onChange={manejarBusqueda}
              style={{marginBottom: '0.5rem'}}
            />
            <select 
              value={productoSeleccionado} 
              onChange={(e) => setProductoSeleccionado(e.target.value)}
              style={{padding: '0.5rem 0.75rem', borderRadius: '8px', border: '1px solid var(--c-border)'}}
            >
              <option value="">-- Selecciona un producto para vender --</option>
              {productosParaSelect.map(p => (
                <option key={p.id} value={p.id}>
                  {p.nombre} - {p.marca} ({p.unidadMedida})
                </option>
              ))}
            </select>
          </label>

          <label>
            Precio de Venta ($)
            <input 
              type="number" 
              step="0.01" 
              value={precioInput} 
              onChange={(e) => setPrecioInput(e.target.value)} 
              placeholder="Ej. 25.50"
            />
          </label>

          <button type="submit" className="nav-cta" style={{marginTop: '0.5rem'}}>Guardar Precio en Catálogo</button>
        </form>
      </div>

      <hr style={{ border: '0', borderTop: '1px solid var(--c-border)', margin: '2rem 0' }} />

      {/* TABLA DE PRECIOS ACTUALES */}
      <h2 style={{fontSize: '1.25rem', marginBottom: '1rem'}}>Mis Precios Publicados</h2>
      {misPrecios.length === 0 ? (
        <p className="empty-state">No has publicado precios aún.</p>
      ) : (
        <div style={{overflowX: 'auto'}}>
          <table className="precios-table">
            <thead>
              <tr>
                <th>ID Precio</th>
                <th>Producto</th>
                <th>Precio Actual</th>
                <th>Última Actualización</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {misPrecios.map(p => (
                <tr key={p.id}>
                  <td className="muted small">#{p.id}</td>
                  <td style={{fontWeight: 500}}>{getNombreProducto(p.productoId)}</td>
                  <td>
                    {editandoId === p.id ? (
                      <input 
                        type="number" 
                        step="0.01" 
                        style={{padding: '0.4rem', width: '100px', borderRadius: '4px', border: '1px solid var(--c-primary)'}}
                        value={precioEditado}
                        onChange={(e) => setPrecioEditado(e.target.value)}
                      />
                    ) : (
                      <span style={{color: 'var(--c-success)', fontWeight: 'bold'}}>${p.precio.toFixed(2)}</span>
                    )}
                  </td>
                  <td>{p.fechaReporte ? p.fechaReporte.split('T')[0] : 'N/A'}</td>
                  <td>
                    {editandoId === p.id ? (
                      <div style={{display: 'flex', gap: '0.5rem'}}>
                        <button className="btn-secondary" style={{backgroundColor: 'var(--c-success)', color: '#fff', borderColor: 'var(--c-success)'}} onClick={() => guardarEdicion(p.id)}>Guardar</button>
                        <button className="btn-ghost" onClick={() => setEditandoId(null)}>Cancelar</button>
                      </div>
                    ) : (
                      <button className="btn-secondary" onClick={() => iniciarEdicion(p)}>Actualizar Precio</button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}