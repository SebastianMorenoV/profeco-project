export function StarRating({ value, outOf = 5, size = 18, onChange }) {
  const stars = Array.from({ length: outOf }, (_, i) => i + 1);
  const interactive = Boolean(onChange);
  return (
    <span
      className={`stars${interactive ? ' interactive' : ''}`}
      style={{ fontSize: size }}
    >
      {stars.map((n) => {
        const filled = n <= Math.round(value);
        return (
          <button
            key={n}
            type="button"
            className={`star${filled ? ' filled' : ''}`}
            disabled={!interactive}
            onClick={() => onChange?.(n)}
            aria-label={`${n} estrellas`}
          >
            ★
          </button>
        );
      })}
    </span>
  );
}
