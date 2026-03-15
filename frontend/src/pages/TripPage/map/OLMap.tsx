import { useRef, useEffect } from "react";
import { useOLMap } from "@/hooks/useOLMap";
import { buildMapData } from "./buildMapData";
import { getAirportCoord } from "@/lib/airportUtils";
import type { TripEvent } from "@/pages/TripPage/tripUtils";

interface OLMapProps {
  events:     TripEvent[];
  activeStep: number; // 1-based index into events[]
}

export function OLMap({ events, activeStep }: OLMapProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  const { setMapData, flyTo } = useOLMap(containerRef);

  // Rebuild all pins/paths whenever the event list changes
  useEffect(() => {
    const { points, paths } = buildMapData(events);
    setMapData(points, paths);
  }, [events, setMapData]);

  // Fly to active step's location whenever the step changes
  useEffect(() => {
    if (events.length === 0) return;
    const event = events[activeStep - 1];
    if (!event) return;

    if (event.kind === "flight") {
      const dest = getAirportCoord(event.data.destinationAirport);
      if (dest) flyTo(dest.lon, dest.lat, 9);
    }

    if (event.kind === "excursion") {
      const exc = event.data as typeof event.data & { latitude?: number; longitude?: number };
      if (exc.latitude != null && exc.longitude != null) {
        flyTo(exc.longitude, exc.latitude, 13);
      }
    }
  }, [activeStep, events, flyTo]);

  return <div ref={containerRef} className="w-full h-full" />;
}
