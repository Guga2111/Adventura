import type { Excursion } from "@/types/trip";
import type { Flight } from "@/types/flight";

export type TripEvent =
  | { kind: "flight";    sortableId: string; date: string; time: string; data: Flight }
  | { kind: "excursion"; sortableId: string; date: string; time: string; data: Excursion };

function isoToDateAndTime(iso: string): { date: string; time: string } {
  const [date, time] = iso.split("T");
  return { date, time: time ?? "00:00:00" };
}

export function buildFlatTimeline(flights: Flight[], excursions: Excursion[]): TripEvent[] {
  const events: TripEvent[] = [
    ...flights.map((f): TripEvent => {
      const { date, time } = isoToDateAndTime(f.departureLocalTime);
      return { kind: "flight", sortableId: `flight-${f.id}`, date, time, data: f };
    }),
    ...excursions.map((e): TripEvent => {
      const { date, time } = isoToDateAndTime(e.startDate);
      return { kind: "excursion", sortableId: `excursion-${e.id}`, date, time, data: e };
    }),
  ];
  events.sort((a, b) =>
    a.date !== b.date ? a.date.localeCompare(b.date) : a.time.localeCompare(b.time)
  );
  return events;
}
