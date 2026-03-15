import airports from "airports";

export interface AirportCoord {
  iata: string;
  name: string;
  lon:  number;
  lat:  number;
}

const byIata = new Map<string, AirportCoord>(
  airports
    .filter((a) => a.iata && a.lat && a.lon)
    .map((a) => [
      a.iata.toUpperCase(),
      {
        iata: a.iata,
        name: a.name,
        lon:  parseFloat(a.lon!),
        lat:  parseFloat(a.lat!),
      },
    ])
);

export function getAirportCoord(iata: string): AirportCoord | null {
  return byIata.get(iata.toUpperCase()) ?? null;
}
