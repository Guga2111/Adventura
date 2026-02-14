const AdventuraIcon = ({ size = 64, color = "currentColor", className = "" }) => {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 100 100"
      fill="none"
      className={className}
    >
      <defs>
        <clipPath id="globeClip">
          <circle cx="50" cy="50" r="28" />
        </clipPath>
        <clipPath id="outsideGlobe">
          <path
            d="M0,0 H100 V100 H0 Z M50,18 A32,32 0 1,0 50,82 A32,32 0 1,0 50,18 Z"
            clipRule="evenodd"
          />
        </clipPath>
        <clipPath id="insideGlobe">
          <circle cx="50" cy="50" r="30" />
        </clipPath>
        <clipPath id="frontHalf">
          <polygon points="0,79 100,21 100,100 0,100" />
        </clipPath>
      </defs>

      {/* Globe outline */}
      <circle cx="50" cy="50" r="30" stroke={color} strokeWidth="4" />

      {/* Continents (simplified shapes, clipped to globe) */}
      <g clipPath="url(#globeClip)" fill={color} opacity={0.45}>
        {/* North America */}
        <path d="M28,28 C30,25 34,24 37,25 C39,26 40,28 42,28 C44,28 44,30 43,32 C42,34 40,36 38,38 C36,39 34,40 32,42 C30,43 28,42 27,40 C26,38 26,35 27,32 Z" />
        {/* South America */}
        <path d="M35,52 C37,50 39,50 40,52 C41,54 42,57 42,60 C42,63 41,66 39,68 C37,70 35,70 34,68 C33,65 32,62 32,59 C32,56 33,54 35,52 Z" />
        {/* Europe */}
        <path d="M48,28 C50,27 52,27 54,28 C55,29 56,31 55,33 C54,34 52,35 50,35 C48,35 47,33 47,31 C47,30 47,29 48,28 Z" />
        {/* Africa */}
        <path d="M50,40 C52,38 55,38 57,40 C59,42 60,45 60,48 C60,52 59,56 57,59 C55,62 53,63 51,62 C49,61 48,58 47,55 C46,52 47,48 48,45 C48,43 49,41 50,40 Z" />
        {/* Asia / Middle East */}
        <path d="M58,27 C61,25 65,25 68,27 C70,29 72,32 72,35 C72,38 70,40 68,41 C65,42 62,42 60,40 C58,38 56,35 56,32 C56,30 57,28 58,27 Z" />
        {/* Australia */}
        <path d="M64,55 C66,54 69,54 70,56 C71,58 71,60 69,61 C67,62 65,62 64,60 C63,58 63,56 64,55 Z" />
      </g>

      {/* Orbit — parts outside the globe (always visible) */}
      <g clipPath="url(#outsideGlobe)">
        <ellipse
          cx="50"
          cy="50"
          rx="42"
          ry="18"
          transform="rotate(-30 50 50)"
          stroke={color}
          strokeWidth="2.5"
          strokeDasharray="5 4"
          opacity={0.5}
        />
      </g>

      {/* Orbit — front arc crossing over the globe (visible) */}
      <g clipPath="url(#insideGlobe)">
        <g clipPath="url(#frontHalf)">
          <ellipse
            cx="50"
            cy="50"
            rx="42"
            ry="18"
            transform="rotate(-30 50 50)"
            stroke={color}
            strokeWidth="2.5"
            strokeDasharray="5 4"
            opacity={0.5}
          />
        </g>
      </g>

      {/* Airplane flying along the orbit path */}
      <g transform="translate(84, 23) rotate(148)">
        {/* Fuselage */}
        <ellipse cx="-7" cy="0" rx="7" ry="1.3" fill={color} />
        {/* Left wing — swept back triangle */}
        <path d="M-5,-1 L-9,-6 L-10,-1.2 Z" fill={color} />
        {/* Right wing — swept back triangle */}
        <path d="M-5,1 L-9,6 L-10,1.2 Z" fill={color} />
        {/* Left tail wing — trapezoid */}
        <path d="M-11.5,-0.8 L-12.5,-3.5 L-14,-2.5 L-14,-0.5 Z" fill={color} />
        {/* Right tail wing — trapezoid */}
        <path d="M-11.5,0.8 L-12.5,3.5 L-14,2.5 L-14,0.5 Z" fill={color} />
      </g>
    </svg>
  );
};

export default AdventuraIcon;
