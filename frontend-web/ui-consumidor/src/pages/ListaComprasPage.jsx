import { useEffect, useState } from 'react';
import { usuariosApi } from '../api';
import { useUser } from '../context/UserContext';
import { EmptyState } from '../components/Loader';

export function ListaComprasPage() {
  const {
    usuarioId,
    listaCompras,
    agregarItemListaCompras,
    toggleItemListaCompras,
    eliminarItemListaCompras,
    limpiarListaCompras,
    setListaComprasRemota
  } = useUser();

  const [nuevo, setNuevo] = useState('');

  useEffect(() => {
    let cancelado = false;
    (async () => {
      try {
        const remotos = await usuariosApi.obtenerListaCompras(usuarioId);
        if (cancelado) return;
        if (Array.isArray(remotos) && remotos.length > 0) {
          const items = remotos.map((it, idx) => ({
            idLocal: Number(it.idLocal ?? it.id_local ?? idx + 1),
            nombre: String(it.nombre ?? ''),
            marcado: Boolean(it.marcado)
          }));
          setListaComprasRemota(items);
        }
      } catch {
        // backend no disponible: trabajar local
      }
    })();
    return () => { cancelado = true; };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [usuarioId]);

  const submit = (e) => {
    e.preventDefault();
    const txt = nuevo.trim();
    if (!txt) return;
    agregarItemListaCompras(txt);
    setNuevo('');
  };

  const total = listaCompras.length;
  const completados = listaCompras.filter((it) => it.marcado).length;

  return (
    <div className="page">
      <h1>Lista del supermercado</h1>
      <p className="muted">
        Apunta lo que necesitas comprar y márcalo conforme lo vayas tomando del estante.
      </p>

      <form className="filter-bar" onSubmit={submit}>
        <input
          type="text"
          placeholder="Ej. leche, huevos, jabón…"
          maxLength={200}
          value={nuevo}
          onChange={(e) => setNuevo(e.target.value)}
        />
        <button type="submit">Agregar</button>
        {total > 0 && (
          <button
            type="button"
            className="btn-ghost"
            onClick={() => {
              if (window.confirm('¿Vaciar toda la lista?')) limpiarListaCompras();
            }}
          >
            Vaciar lista
          </button>
        )}
      </form>

      {total === 0 ? (
        <EmptyState title="Tu lista está vacía" hint="Empieza agregando productos arriba." />
      ) : (
        <>
          <p className="muted small">{completados} de {total} marcados</p>
          <ul className="lista-compras">
            {listaCompras.map((it) => (
              <li key={it.idLocal} className={`lista-item${it.marcado ? ' marcado' : ''}`}>
                <label>
                  <input
                    type="checkbox"
                    checked={it.marcado}
                    onChange={() => toggleItemListaCompras(it.idLocal)}
                  />
                  <span>{it.nombre}</span>
                </label>
                <button
                  type="button"
                  className="btn-ghost"
                  onClick={() => eliminarItemListaCompras(it.idLocal)}
                  aria-label={`Eliminar ${it.nombre}`}
                >
                  Quitar
                </button>
              </li>
            ))}
          </ul>
        </>
      )}
    </div>
  );
}
