import { useState, useEffect } from 'react';
import { getPerfilComercio, updatePerfilComercio } from '../api/comercio';

export default function PerfilPage({ onLogout }) {
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
      const res = await getPerfilComercio(1);
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
}, []);

  const manejarGuardar = async () => {
    try {
      await updatePerfilComercio(1, datos);
      setEditando(false);
      alert("Perfil actualizado en el servidor de ProFeCo ✅");
    } catch (error) {
      alert("Error al guardar. Revisa la conexión con el microservicio.");
    }
  };

  if (cargando) return <div style={{padding: '2rem'}}>Accediendo a la base de datos...</div>;

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
    <div>
      <h1 style={styles.title}>Perfil del Comercio</h1>
      
      <div style={styles.card}>
        <div style={styles.profileHeader}>
          <div style={styles.avatar}>
            {datos.nombreComercial ? datos.nombreComercial.substring(0, 2).toUpperCase() : 'SO'}
          </div>
          <div>
            <h2 style={styles.comercioName}>{datos.nombreComercial || "Sin Nombre Registrado"}</h2>
            <p style={styles.comercioId}>ID de Comercio: #1</p>
          </div>
        </div>

        <div style={styles.infoGrid}>
          {Object.keys(etiquetas).map((key) => (
            <div key={key} style={styles.infoGroup}>
              <label style={styles.label}>{etiquetas[key]}</label>
              {editando ? (
                <input 
                  style={styles.inputEdit} 
                  value={datos[key] || ""} 
                  onChange={(e) => setDatos({...datos, [key]: e.target.value})} 
                />
              ) : (
                <p style={styles.value}>{datos[key] || "No registrado"}</p>
              )}
            </div>
          ))}
        </div>

        <div style={styles.actions}>
          {editando ? (
            <button style={styles.saveButton} onClick={manejarGuardar}>Guardar Cambios</button>
          ) : (
            <button style={styles.editButton} onClick={() => setEditando(true)}>Editar Perfil</button>
          )}
          <button style={styles.logoutButton} onClick={onLogout}>Cerrar Sesión</button>
        </div>
      </div>
    </div>
  );
}

const styles = {
  title: { fontSize: '1.875rem', fontWeight: 'bold', color: '#111827', margin: '0 0 0.5rem 0' },
  subtitle: { color: '#6b7280', margin: '0 0 2rem 0' },
  card: { backgroundColor: '#fff', padding: '2rem', borderRadius: '0.75rem', border: '1px solid #e5e7eb', maxWidth: '800px' },
  profileHeader: { display: 'flex', alignItems: 'center', gap: '1.5rem', marginBottom: '2rem' },
  avatar: { 
    width: '64px', height: '64px', backgroundColor: '#111827', color: '#fff', 
    borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', 
    fontSize: '1.5rem', fontWeight: 'bold' 
  },
  comercioName: { fontSize: '1.5rem', fontWeight: 'bold', margin: 0, color: '#111827' },
  comercioId: { fontSize: '0.875rem', color: '#6b7280', margin: '0.25rem 0 0 0' },
  divider: { border: '0', borderTop: '1px solid #f3f4f6', margin: '0 0 2rem 0' },
  infoGrid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '2rem', marginBottom: '2.5rem' },
  label: { display: 'block', fontSize: '0.75rem', fontWeight: '600', color: '#9ca3af', textTransform: 'uppercase', marginBottom: '0.5rem' },
  value: { fontSize: '1rem', fontWeight: '500', color: '#111827', margin: 0 },
  actions: { display: 'flex', gap: '1rem', borderTop: '1px solid #f3f4f6', paddingTop: '2rem' },
  editButton: { padding: '0.6rem 1.2rem', backgroundColor: '#fff', border: '1px solid #d1d5db', borderRadius: '0.375rem', fontWeight: '600', cursor: 'pointer' },
  logoutButton: { padding: '0.6rem 1.2rem', backgroundColor: '#fee2e2', color: '#dc2626', border: 'none', borderRadius: '0.375rem', fontWeight: '600', cursor: 'pointer' },

  inputEdit: {
    width: '100%',
    padding: '0.5rem',
    borderRadius: '0.375rem',
    border: '1px solid #2563eb',
    outline: 'none',
    fontSize: '1rem'
  },
  saveButton: {
    padding: '0.6rem 1.2rem',
    backgroundColor: '#111827',
    color: '#fff',
    border: 'none',
    borderRadius: '0.375rem',
    fontWeight: '600',
    cursor: 'pointer'
  },
  title: { fontSize: '1.875rem', fontWeight: 'bold', color: '#111827', margin: '0 0 2rem 0' },
  card: { backgroundColor: '#fff', padding: '2rem', borderRadius: '0.75rem', border: '1px solid #e5e7eb' },
  profileHeader: { display: 'flex', alignItems: 'center', gap: '1.5rem', marginBottom: '2rem' },
  avatar: { width: '64px', height: '64px', backgroundColor: '#111827', color: '#fff', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.5rem', fontWeight: 'bold' },
  comercioName: { fontSize: '1.5rem', fontWeight: 'bold', margin: 0 },
  comercioId: { fontSize: '0.875rem', color: '#6b7280' },
  infoGrid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '2rem', marginBottom: '2.5rem' },
  label: { display: 'block', fontSize: '0.75rem', fontWeight: '600', color: '#9ca3af', textTransform: 'uppercase', marginBottom: '0.5rem' },
  value: { fontSize: '1rem', fontWeight: '500', color: '#111827' },
  actions: { display: 'flex', gap: '1rem', borderTop: '1px solid #f3f4f6', paddingTop: '2rem' },
  editButton: { padding: '0.6rem 1.2rem', backgroundColor: '#fff', border: '1px solid #d1d5db', borderRadius: '0.375rem', cursor: 'pointer' },
  logoutButton: { padding: '0.6rem 1.2rem', backgroundColor: '#fee2e2', color: '#dc2626', border: 'none', borderRadius: '0.375rem', fontWeight: '600', cursor: 'pointer' }
};