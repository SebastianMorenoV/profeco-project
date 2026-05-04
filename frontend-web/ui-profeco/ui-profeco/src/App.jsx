import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { Layout } from './pages/Layout';
import { ReportesPage } from './pages/ReportesPage';
import { MultasPage } from './pages/MultasPage';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
          <Route index element={<Navigate to="/reportes" replace />} />
          <Route path="reportes" element={<ReportesPage />} />
          <Route path="multas" element={<MultasPage />} />
          <Route path="*" element={<Navigate to="/reportes" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}