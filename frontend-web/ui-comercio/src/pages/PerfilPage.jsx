import { useState, useEffect } from 'react';
import { getPerfilComercio, updatePerfilComercio } from '../api/comercio';

export default function PerfilPage({ onLogout, comercioId }) {
  const [editando, setEditando] = useState(false);
  const [cargando, setCargando] = useState(true);
  
  // Usamos los nombres exactos del comercio.proto
  const [datos, setDatos] = useState({
  nombreComercial: "",  // Antes: nombre_comercial
  razonSocial: "",      // Antes: razon_social
  rfc: "",
  direccion: "",
  ciudad: "",
  estado: "",
  email: "",
  tipoComercio: "",     // Antes: tipo_comercio
  telefono: "",
  codigoPostal: ""      // Antes: codigo_postal
})

  useEffect(() => {
  const cargarPerfil = async () => {
    try {
      const res = await getPerfilComercio(comercioId);
      console.log("ESTRUCTURA REAL DEL BACKEND:", res.data);
      
      if (res.data.comercio) {
        setDatos(res.data.comercio);
      } else {
        setDatos(res.data);
      }
    } catch (error) {
      console.error("Error de conexión:", error);
    } finally {
      setCargando(false);
    }
  };
  cargarPerfil();
}, [comercioId]);

  const manejarGuardar = async () => {
    try {
      await updatePerfilComercio(comercioId, datos);
      setEditando(false);
      alert("Perfil actualizado en el servidor de ProFeCo ✅");
    } catch (error) {
      alert("Error al guardar. Revisa la conexión con el microservicio.");
    }
  };

  if (cargando) return <div className="loader"><div className="spinner"></div> Accediendo a la base de datos...</div>;

  const etiquetas = {
  nombreComercial: "Nombre del Establecimiento",
  razonSocial: "Razón Social",
  rfc: "RFC",
  direccion: "Dirección",
  ciudad: "Ciudad",
  estado: "Estado",
  email: "Correo Electrónico",
  tipoComercio: "Giro / Tipo",
  telefono: "Teléfono",
  codigoPostal: "Código Postal"
};

  return (
    <div className="container">
      <div className="section-header">
        <h1 style={{color: 'var(--c-primary-dark)'}}>Perfil del Comercio</h1>
      </div>
      
      <div className="card" style={{maxWidth: '800px', padding: '2rem'}}>
        <div style={{display: 'flex', alignItems: 'center', gap: '1.5rem', marginBottom: '2rem'}}>
          <div className="brand-mark" style={{width: '64px', height: '64px', fontSize: '1.5rem'}}>
            {datos.nombreComercial ? datos.nombreComercial.substring(0, 2).toUpperCase() : 'SO'}
          </div>
          <div>
            <h2 style={{fontSize: '1.5rem', fontWeight: 'bold', margin: 0}}>{datos.nombreComercial || "Sin Nombre Registrado"}</h2>
            <p className="muted small" style={{margin: '0.25rem 0 0 0'}}>ID de Comercio: #{comercioId || "1"}</p>
          </div>
        </div>

        <div className="info-grid" style={{gap: '2rem', marginBottom: '2.5rem', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))'}}>
          {Object.keys(etiquetas).map((key) => (
            <div key={key} style={{display: 'flex', flexDirection: 'column', gap: '0.5rem'}}>
              <label className="muted small" style={{fontWeight: 600, textTransform: 'uppercase'}}>{etiquetas[key]}</label>
              {editando ? (
                <input 
                  style={{padding: '0.5rem', borderRadius: '8px', border: '1px solid var(--c-primary)', outline: 'none', fontSize: '1rem'}} 
                  value={datos[key] || ""} 
                  onChange={(e) => setDatos({...datos, [key]: e.target.value})} 
                />
              ) : (
                <p style={{fontSize: '1rem', fontWeight: 500, margin: 0}}>{datos[key] || "No registrado"}</p>
              )}
            </div>
          ))}
        </div>

        <div style={{display: 'flex', gap: '1rem', borderTop: '1px solid var(--c-border)', paddingTop: '2rem'}}>
          {editando ? (
            <button className="nav-cta" style={{padding: '0.6rem 1.2rem', borderRadius: '8px', border: 'none', fontWeight: 600, cursor: 'pointer'}} onClick={manejarGuardar}>Guardar Cambios</button>
          ) : (
            <button className="btn-secondary" onClick={() => setEditando(true)}>Editar Perfil</button>
          )}
          <button className="nav-logout" onClick={onLogout}>Cerrar Sesión</button>
        </div>
      </div>
    </div>
  );
}
