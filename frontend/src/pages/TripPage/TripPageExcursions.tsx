import { useState, useMemo, useRef, useEffect } from "react";
import {
  DndContext,
  PointerSensor,
  useSensor,
  useSensors,
  type DragEndEvent,
} from "@dnd-kit/core";
import { SortableContext, useSortable, verticalListSortingStrategy } from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";

import { FlightCard } from "./flight/FlightCard";
import { ExcursionCard } from "./ExcursionCard";
import type { Excursion } from "@/types/trip";
import type { Flight } from "@/types/flight";
import { getFlights, updateFlight } from "@/services/FlightService";
import { TripPageExcursionsHeader } from "./TripPageExcursionsHeader";
import { RescheduleDialog } from "./RescheduleDialog";
import type { RescheduleTarget } from "./RescheduleDialog";
import { useExcursionSocket } from "@/hooks/useExcursionSocket";
import type { GroupMember } from "@/types/group";
import { buildFlatTimeline } from "./tripUtils";
import type { TripEvent } from "./tripUtils";

function isoToDateAndTime(iso: string): { date: string; time: string } {
  const [date, time] = iso.split("T");
  return { date, time: time ?? "00:00:00" };
}

function formatDateLabel(isoDate: string) {
  const date = new Date(isoDate + "T00:00:00");
  return date
    .toLocaleDateString("en-US", { month: "short", day: "numeric" })
    .toUpperCase();
}

function buildTimeline(
  flights: Flight[],
  excursions: Excursion[],
): Record<string, TripEvent[]> {
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
    a.date !== b.date ? a.date.localeCompare(b.date) : a.time.localeCompare(b.time),
  );

  return events.reduce<Record<string, TripEvent[]>>((acc, event) => {
    if (!acc[event.date]) acc[event.date] = [];
    acc[event.date].push(event);
    return acc;
  }, {});
}

interface SortableEventCardProps {
  event:       TripEvent;
  step:        number;
  tripId:      number;
  members:     GroupMember[];
  isActive:    boolean;
  onStepClick: () => void;
  cardRef:     React.RefObject<HTMLDivElement | null> | null;
}

function SortableEventCard({ event, step, tripId, members, isActive, onStepClick, cardRef }: SortableEventCardProps) {
  const {
    attributes, listeners, setNodeRef,
    transform, transition, isDragging,
  } = useSortable({ id: event.sortableId });

  const style: React.CSSProperties = {
    transform:  CSS.Transform.toString(transform),
    transition,
    opacity:    isDragging ? 0.5 : 1,
    zIndex:     isDragging ? 50 : undefined,
    position:   isDragging ? "relative" : undefined,
  };

  return (
    <div ref={(node) => { setNodeRef(node); if (cardRef) (cardRef as React.MutableRefObject<HTMLDivElement | null>).current = node; }} style={style}>
      {event.kind === "flight" ? (
        <FlightCard
          flight={event.data}
          tripId={tripId}
          members={members}
          step={step}
          isActive={isActive}
          onStepClick={onStepClick}
          dragHandleListeners={listeners}
          dragHandleAttributes={attributes}
        />
      ) : (
        <ExcursionCard
          excursion={event.data}
          step={step}
          isActive={isActive}
          onStepClick={onStepClick}
          dragHandleListeners={listeners}
          dragHandleAttributes={attributes}
        />
      )}
    </div>
  );
}

interface TripPageExcursionsProps {
  tripId:              number;
  members:             GroupMember[];
  initialFlights:      Flight[];
  initialExcursions:   Excursion[];
  activeStep:          number;
  onStepChange:        (step: number) => void;
  onFlightsChange:     (flights: Flight[]) => void;
  onExcursionsChange:  (excursions: Excursion[]) => void;
}

export function TripPageExcursions({ tripId, members, initialFlights, initialExcursions, activeStep, onStepChange, onFlightsChange, onExcursionsChange }: TripPageExcursionsProps) {
  const [rescheduleTarget, setRescheduleTarget] = useState<RescheduleTarget | null>(null);
  const [flights, setFlights] = useState<Flight[]>(initialFlights);
  const [excursions, setExcursions] = useState<Excursion[]>(initialExcursions);

  const activeCardRef = useRef<HTMLDivElement | null>(null);

  // Sync live data up to TripPage so TripPageMap always has fresh state
  useEffect(() => { onFlightsChange(flights); }, [flights]);
  useEffect(() => { onExcursionsChange(excursions); }, [excursions]);

  useEffect(() => {
    activeCardRef.current?.scrollIntoView({ behavior: "smooth", block: "nearest" });
  }, [activeStep]);

  const { sendUpdate } = useExcursionSocket(tripId, (raw) => {
    const data = raw as Record<string, unknown>;

    if (data.action === "DELETE") {
      setExcursions((prev) => prev.filter((e) => e.id !== (data.excursionId as number)));
    } else {
      const excursion = raw as Excursion;
      setExcursions((prev) => {
        const exists = prev.some((e) => e.id === excursion.id);
        if (exists) {
          return prev.map((e) =>
            e.id === excursion.id ? { ...e, startDate: excursion.startDate, endDate: excursion.endDate } : e,
          );
        }
        return [...prev, excursion];
      });
    }
  });

  const sensors = useSensors(
    useSensor(PointerSensor, { activationConstraint: { distance: 8 } }),
  );

  const flatEvents  = useMemo(() => buildFlatTimeline(flights, excursions), [flights, excursions]);
  const grouped     = buildTimeline(flights, excursions);
  const allEvents   = Object.values(grouped).flat();
  const sortableIds = allEvents.map((e) => e.sortableId);

  function stepForEvent(sortableId: string): number {
    return flatEvents.findIndex((e) => e.sortableId === sortableId) + 1;
  }

  function handleDragEnd({ active, over }: DragEndEvent) {
    if (!over || active.id === over.id) return;

    const dragged = allEvents.find((e) => e.sortableId === active.id);
    if (!dragged) return;

    const target: RescheduleTarget =
      dragged.kind === "flight"
        ? {
            kind:             "flight",
            id:               dragged.data.id,
            name:             `${dragged.data.originAirport} → ${dragged.data.destinationAirport}`,
            currentStartDate: dragged.data.departureLocalTime,
          }
        : {
            kind:             "excursion",
            id:               dragged.data.id,
            name:             dragged.data.name,
            currentStartDate: dragged.data.startDate,
            currentEndDate:   dragged.data.endDate,
          };

    setRescheduleTarget(target);
  }

  function handleRescheduleConfirm(startDate: string, endDate: string | undefined) {
    if (!rescheduleTarget) return;

    if (rescheduleTarget.kind === "excursion") {
      sendUpdate({
        action:      "UPDATE",
        excursionId: rescheduleTarget.id,
        startDate,
        endDate:     endDate ?? null,
      });

      setExcursions((prev) =>
        prev.map((e) =>
          e.id === rescheduleTarget.id ? { ...e, startDate, endDate: endDate ?? e.endDate } : e,
        ),
      );
    }

    if (rescheduleTarget.kind === "flight") {
      const flight = flights.find((f) => f.id === rescheduleTarget.id);
      if (flight) {
        const dto = {
          flightNumber:       flight.flightNumber,
          airline:            flight.airline,
          originAirport:      flight.originAirport,
          destinationAirport: flight.destinationAirport,
          departureLocalTime: startDate,
          departureTimezone:  flight.departureTimezone,
          arrivalLocalTime:   flight.arrivalLocalTime,
          arrivalTimezone:    flight.arrivalTimezone,
          bookingReference:   flight.bookingReference,
          notes:              flight.notes,
          passengers:         flight.passengers.map((p) => ({
            groupMemberId: p.groupMember.id,
            seatNumber:    p.seatNumber,
            seatClass:     p.seatClass,
            price:         p.price,
            status:        p.status,
          })),
        };
        updateFlight(tripId, flight.id, dto)
          .then((updated) =>
            setFlights((prev) => prev.map((f) => (f.id === updated.id ? updated : f)))
          )
          .catch(console.error);
      }
    }

    setRescheduleTarget(null);
    onStepChange(1);
  }

  return (
    <div>
      <TripPageExcursionsHeader
        tripId={tripId}
        members={members}
        onFlightCreated={() => getFlights(tripId).then(setFlights).catch(console.error)}
        sendUpdate={sendUpdate}
      />

      <DndContext sensors={sensors} onDragEnd={handleDragEnd}>
        <SortableContext items={sortableIds} strategy={verticalListSortingStrategy}>
          <div className="space-y-6">
            {(() => {
              let globalStep = 0;
              return Object.entries(grouped).map(([date, events]) => (
                <section key={date}>
                  <h2 className="mb-3 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                    {formatDateLabel(date)}
                  </h2>
                  <div className="space-y-3">
                    {events.map((event) => {
                      globalStep++;
                      const step = globalStep;
                      const isActive = stepForEvent(event.sortableId) === activeStep;
                      return (
                        <SortableEventCard
                          key={event.sortableId}
                          event={event}
                          step={step}
                          tripId={tripId}
                          members={members}
                          isActive={isActive}
                          onStepClick={() => onStepChange(stepForEvent(event.sortableId))}
                          cardRef={isActive ? activeCardRef : null}
                        />
                      );
                    })}
                  </div>
                </section>
              ));
            })()}
          </div>
        </SortableContext>
      </DndContext>

      <RescheduleDialog
        target={rescheduleTarget}
        onConfirm={handleRescheduleConfirm}
        onCancel={() => setRescheduleTarget(null)}
      />
    </div>
  );
}
