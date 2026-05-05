import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { reportesApi, usuariosApi, comerciosApi } from '../api';
import { useFetch } from '../hooks/useFetch';
import { Loader, ErrorBox, EmptyState } from '../components/Loader';
import { formatDate } from '../utils/format';

export function ReportesPage() {
  const navigate = useNavigate();
  const [estatusFiltro, setEstatusFiltro] = useState('');
  
  // 1. Cargar la lista principal de reportes
  const { data: reportes, loading, error, reload } = useFetch(
    () => reportesApi.listar(estatusFiltro),
    [estatusFiltro]
  );
  
  const [procesando, setProcesando] = useState(false);
  
  // 2. Estado local para almacenar como caché la info de los usuarios y comercios
  const [usuariosMap, setUsuariosMap] = useState({});
  const [comerciosMap, setComerciosMap] = useState({});

  // 3. Efecto secundario: cuando los reportes cambien, buscar los usuarios faltantes
  useEffect(() => {
    if (!reportes) return;
    const uniqueUserIds = [...new Set(reportes.map(r => r.usuarioId))];
    const missingIds = uniqueUserIds.filter(id => !usuariosMap[id]);
    
    if (missingIds.length > 0) {
      const fetchMissingUsers = async () => {
        try {
          const promises = missingIds.map(id => usuariosApi.obtener(id));
          const usersResult = await Promise.allSettled(promises);
          
          const newUsersData = {};
          usersResult.forEach((res, index) => {
            const requestedId = missingIds[index];
            if (res.status === 'fulfilled' && res.value) {
              newUsersData[requestedId] = res.value;
            } else {
              newUsersData[requestedId] = { nombre: 'Desconocido', apellido: '' };
            }
          });
          setUsuariosMap(prev => ({ ...prev, ...newUsersData }));
        } catch (err) {
          console.error("Error cargando info extra de usuarios", err);
        }
      };
      fetchMissingUsers();
    }
  }, [reportes, usuariosMap]);

  useEffect(() => {
    if (!reportes) return;
    const uniqueComercioIds = [...new Set(reportes.map(r => r.comercioId))];
    const missingIds = uniqueComercioIds.filter(id => !(id in comerciosMap));
    
    if (missingIds.length > 0) {
      const fetchMissingComercios = async () => {
        try {
          const promises = missingIds.map(id => comerciosApi.obtener(id));
          const results = await Promise.allSettled(promises);
          const newData = {};
          results.forEach((res, index) => {
            const requestedId = missingIds[index];
            if (res.status === 'fulfilled' && res.value) {
              newData[requestedId] = res.value;
            } else {
              newData[requestedId] = { nombreComercial: `Comercio #${requestedId}` };
            }
          });
          setComerciosMap(prev => ({ ...prev, ...newData }));
        } catch (err) {
          console.error('Error cargando comercios', err);
        }
      };
      fetchMissingComercios();
    }
  }, [reportes, comerciosMap]);

  const cambiarEstatus = async (id, nuevoEstatus) => {
    if (!confirm(`¿Seguro que deseas cambiar el estatus a ${nuevoEstatus}?`)) return;
    
    setProcesando(true);
    try {
      await reportesApi.actualizarEstatus(id, nuevoEstatus);
      reload();
    } catch (err) {
      alert('Error al actualizar estatus: ' + err.message);
    } finally {
      setProcesando(false);
    }
  };

  return (
    <div className="page">
      <div className="section-header">
        <h1>Panel de Reportes</h1>
      </div>
      <p className="muted">Gestión y revisión de quejas de consumidores.</p>

      <div className="filter-bar">
        <select 
          value={estatusFiltro} 
          onChange={(e) => setEstatusFiltro(e.target.value)}
        >
          <option value="">Todos los estatus</option>
          <option value="PENDIENTE">Pendientes</option>
          <option value="EN_REVISION">En Revisión</option>
          <option value="RESUELTA_CON_MULTA">Resuelta con Multa</option>
          <option value="RESUELTA_SIN_MULTA">Resuelta sin Multa</option>
          <option value="RECHAZADA">Rechazada</option>
        </select>
      </div>

      {loading && <Loader />}
      {error && <ErrorBox message={error} />}
      {!loading && !error && (reportes ?? []).length === 0 && (
        <EmptyState title="No hay reportes para este filtro" />
      )}

      {reportes && reportes.length > 0 && (
        <div className="table-wrap">
        <table className="precios-table">
          <thead>
            <tr>
              <th>ID Reporte</th>
              <th>Fecha</th>
              <th>Consumidor (Afectado)</th>
              <th>Comercio (Infractor)</th>
              <th>Motivo</th>
              <th>Estatus</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {reportes.map((r) => {
              const userInfo = usuariosMap[r.usuarioId];
              const nombreCompleto = userInfo ? `${userInfo.nombre} ${userInfo.apellido}` : 'Cargando...';
              
              const comercioInfo = comerciosMap[r.comercioId];
              const nombreComercio = comercioInfo?.nombreComercial ?? 'Cargando...';

              return (
                <tr key={r.id}>
                  <td><strong>#{r.id}</strong></td>
                  <td className="muted small">{formatDate(r.fechaCreacion)}</td>
                  <td>
                    {nombreCompleto} <br/>
                    <span className="muted small">ID: {r.usuarioId}</span>
                  </td>
                  <td>{nombreComercio}</td>
                  <td>
                    <span className="chip">{r.motivo.replace(/_/g, ' ')}</span>
                  </td>
                  <td>
                    <strong>{r.estatus.replace(/_/g, ' ')}</strong>
                  </td>
                  <td>
                    {r.estatus === 'PENDIENTE' && (
                      <button
                        className="btn-action btn-primary"
                        disabled={procesando}
                        onClick={() => cambiarEstatus(r.id, 'EN_REVISION')}
                      >
                        Poner en Revisión
                      </button>
                    )}
                    
                    {r.estatus === 'EN_REVISION' && (
                      <div className="acciones-container">
                        <button
                          type="button"
                          className="btn-action btn-danger"
                          disabled={procesando}
                          onClick={() => cambiarEstatus(r.id, 'RECHAZADA')}
                        >
                          Rechazar
                        </button>
                        
                        <button
                          type="button"
                          className="btn-action btn-success"
                          disabled={procesando}
                          onClick={() => cambiarEstatus(r.id, 'RESUELTA_SIN_MULTA')}
                        >
                          Resolver sin Multa
                        </button>
                        
                        <button
                          type="button"
                          className="btn-action btn-primary"
                          disabled={procesando}
                          onClick={() => navigate(`/multas?comercioId=${r.comercioId}&reporteId=${r.id}`)}
                        >
                          Resolver con Multa
                        </button>
                      </div>
                    )}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
        </div>
      )}
    </div>
  );
}