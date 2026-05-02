const mxn = new Intl.NumberFormat('es-MX', { style: 'currency', currency: 'MXN' });

export const formatMXN = (n) => mxn.format(Number.isFinite(n) ? n : 0);

export const formatDate = (raw) => {
  if (!raw) return '';
  const d = new Date(raw);
  if (Number.isNaN(d.getTime())) return raw;
  return d.toLocaleDateString('es-MX', { day: '2-digit', month: 'short', year: 'numeric' });
};
