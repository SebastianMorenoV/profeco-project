import { useState } from 'react';
import { comerciosApi, usuariosApi } from '../api';
import { useFetch } from '../hooks/useFetch';
import { Loader } from '../components/Loader';
import {
  useCategories,
  DEFAULT_TIPOS_COMERCIO,
  STORAGE_KEY_TIPOS
} from '../hooks/useCategories';

const INITIAL_FORM = {
  nombreComercial: '',
  razonSocial: '',
  rfc: '',
  direccion: '',
  ciudad: '',
  estado: '',
  codigoPostal: '',
  telefono: '',
  email: '',
  tipoComercio: '',
  latitud: '',
  longitud: '',
  idPropietario: ''
};

export function ComerciosPage() {
  /* ── Categorías dinámicas ── */
  const { items: tiposComercio } = useCategories(STORAGE_KEY_TIPOS, DEFAULT_TIPOS_COMERCIO);

  /* ── Propietarios (usuarios tipo COMERCIANTE) ── */
  const { data: propietarios, loading: loadingProp } = useFetch(
    () => usuariosApi.listar('COMERCIANTE'),
    []
  );

  const [form, setForm] = useState(INITIAL_FORM);
  const [guardando, setGuardando] = useState(false);
  const [okMsg, setOkMsg] = useState(null);
  const [errMsg, setErrMsg] = useState(null);

  const onField = (key, value) => setForm((prev) => ({ ...prev, [key]: value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setGuardando(true);
    setOkMsg(null);
    setErrMsg(null);

    try {
      const payload = {
        nombreComercial: form.nombreComercial.trim(),
        razonSocial: form.razonSocial.trim(),
        rfc: form.rfc.trim().toUpperCase(),
        direccion: form.direccion.trim(),
        ciudad: form.ciudad.trim(),
        estado: form.estado.trim(),
        codigoPostal: form.codigoPostal.trim(),
        telefono: form.telefono.trim(),
        email: form.email.trim(),
        tipoComercio: form.tipoComercio.trim(),
        latitud: Number(form.latitud),
        longitud: Number(form.longitud),
        idPropietario: Number(form.idPropietario)
      };

      if (!Number.isFinite(payload.latitud) || !Number.isFinite(payload.longitud)) {
        setErrMsg('Latitud y longitud deben ser números válidos.');
        setGuardando(false);
        return;
      }

      if (!Number.isInteger(payload.idPropietario) || payload.idPropietario < 1) {
        setErrMsg('Debes seleccionar un propietario.');
        setGuardando(false);
        return;
      }

      const comercio = await comerciosApi.registrar(payload);
      setOkMsg(`Comercio registrado correctamente con ID #${comercio?.id ?? 'N/A'}.`);
      setForm(INITIAL_FORM);
    } catch (err) {
      setErrMsg(err.message || 'No se pudo registrar el comercio.');
    } finally {
      setGuardando(false);
    }
  };

  return (
    <div className="page">
      <div className="section-header">
        <h1>Alta de Comercios</h1>
      </div>
      <p className="muted">Registro administrativo de nuevos comercios para supervisión PROFECO.</p>

      <section className="card">
        <h3>Registrar Comercio</h3>
        <form className="resenia-form" onSubmit={handleSubmit}>
          <div className="grid grid-2">
            <label>
              Nombre comercial
              <input
                required
                value={form.nombreComercial}
                onChange={(e) => onField('nombreComercial', e.target.value)}
              />
            </label>

            <label>
              Razón social
              <input
                required
                value={form.razonSocial}
                onChange={(e) => onField('razonSocial', e.target.value)}
              />
            </label>

            <label>
              RFC
              <input
                required
                maxLength={13}
                value={form.rfc}
                onChange={(e) => onField('rfc', e.target.value)}
              />
            </label>

            <label>
              Tipo de comercio
              <select
                required
                value={form.tipoComercio}
                onChange={(e) => onField('tipoComercio', e.target.value)}
              >
                <option value="">Selecciona una opción</option>
                {tiposComercio.map((tipo) => (
                  <option key={tipo.key} value={tipo.key}>
                    {tipo.label}
                  </option>
                ))}
              </select>
            </label>

            <label>
              Dirección
              <input
                required
                value={form.direccion}
                onChange={(e) => onField('direccion', e.target.value)}
              />
            </label>

            <label>
              Ciudad
              <input required value={form.ciudad} onChange={(e) => onField('ciudad', e.target.value)} />
            </label>

            <label>
              Estado
              <input required value={form.estado} onChange={(e) => onField('estado', e.target.value)} />
            </label>

            <label>
              Código postal
              <input
                required
                maxLength={10}
                value={form.codigoPostal}
                onChange={(e) => onField('codigoPostal', e.target.value)}
              />
            </label>

            <label>
              Teléfono
              <input
                required
                value={form.telefono}
                onChange={(e) => onField('telefono', e.target.value)}
              />
            </label>

            <label>
              Correo electrónico
              <input
                required
                type="email"
                value={form.email}
                onChange={(e) => onField('email', e.target.value)}
              />
            </label>

            <label>
              Latitud
              <input
                required
                type="number"
                step="any"
                value={form.latitud}
                onChange={(e) => onField('latitud', e.target.value)}
              />
            </label>

            <label>
              Longitud
              <input
                required
                type="number"
                step="any"
                value={form.longitud}
                onChange={(e) => onField('longitud', e.target.value)}
              />
            </label>

            <label>
              Propietario
              {loadingProp ? (
                <Loader label="Cargando propietarios…" />
              ) : (
                <select
                  required
                  value={form.idPropietario}
                  onChange={(e) => onField('idPropietario', e.target.value)}
                >
                  <option value="">Selecciona un propietario</option>
                  {(propietarios ?? []).map((p) => (
                    <option key={p.id} value={p.id}>
                      {p.nombre} {p.apellido} — {p.email}
                    </option>
                  ))}
                </select>
              )}
            </label>
          </div>

          {okMsg && <div className="success-box">{okMsg}</div>}
          {errMsg && <div className="error-box">{errMsg}</div>}

          <button type="submit" disabled={guardando}>
            {guardando ? 'Guardando...' : 'Registrar comercio'}
          </button>
        </form>
      </section>
    </div>
  );
}
