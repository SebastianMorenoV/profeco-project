export function Loader({ label = 'Cargando…' }) {
  return (
    <div className="loader" role="status">
      <div className="spinner" />
      <span>{label}</span>
    </div>
  );
}

export function ErrorBox({ message }) {
  return (
    <div className="error-box" role="alert">
      <strong>Algo salió mal.</strong>
      <p>{message}</p>
    </div>
  );
}

export function EmptyState({ title, hint }) {
  return (
    <div className="empty-state">
      <h3>{title}</h3>
      {hint && <p className="muted">{hint}</p>}
    </div>
  );
}
