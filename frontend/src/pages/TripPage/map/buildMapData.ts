import type { TripEvent } from "@/pages/TripPage/tripUtils";
import { getAirportCoord } from "@/lib/airportUtils";
import type { MapPoint, MapPath } from "@/hooks/useOLMap";

export function buildMapData(events: TripEvent[]): { points: MapPoint[]; paths: MapPath[] } {
  const points: MapPoint[] = [];
  const paths:  MapPath[]  = [];
  const seen = new Set<string>();

  function addPoint(lon: number, lat: number, label: string, kind: MapPoint["kind"]) {
    const key = `${lon},${lat}`;
    if (!seen.has(key)) {
      seen.add(key);
      points.push({ lon, lat, label, kind });
    }
  }

  for (const event of events) {
    if (event.kind === "flight") {
      const { originAirport, destinationAirport } = event.data;
      const origin = getAirportCoord(originAirport);
      const dest   = getAirportCoord(destinationAirport);

      if (origin) addPoint(origin.lon, origin.lat, origin.name, "airport");
      if (dest)   addPoint(dest.lon,   dest.lat,   dest.name,   "airport");
      if (origin && dest) {
        paths.push({ from: [origin.lon, origin.lat], to: [dest.lon, dest.lat] });
      }
    }

    if (event.kind === "excursion") {
      const exc = event.data as typeof event.data & {
        locationName?: string;
        latitude?:     number;
        longitude?:    number;
      };
      if (exc.latitude != null && exc.longitude != null) {
        addPoint(exc.longitude, exc.latitude, exc.locationName ?? event.data.name, "excursion");
      }
    }
  }

  return { points, paths };
}
