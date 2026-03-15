import type { TripEvent } from "@/pages/TripPage/tripUtils";

interface StepLabelProps {
  event: TripEvent | undefined;
}

export function StepLabel({ event }: StepLabelProps) {
  if (!event) return <span className="text-sm text-muted-foreground">No events</span>;

  if (event.kind === "flight") {
    const { originAirport, destinationAirport, airline } = event.data;
    return (
      <p className="text-sm font-medium truncate">
        {airline ? `${airline} · ` : ""}
        {originAirport} → {destinationAirport}
      </p>
    );
  }

  return (
    <p className="text-sm font-medium truncate">
      {(event.data as { locationName?: string } & typeof event.data).locationName ?? event.data.name}
    </p>
  );
}
