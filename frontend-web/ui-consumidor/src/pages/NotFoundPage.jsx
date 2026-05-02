import { Link } from 'react-router-dom';

export function NotFoundPage() {
  return (
    <div className="page not-found">
      <h1>404</h1>
      <p className="muted">La página que buscas no existe.</p>
      <Link to="/" className="link-more">Ir al inicio →</Link>
    </div>
  );
}
