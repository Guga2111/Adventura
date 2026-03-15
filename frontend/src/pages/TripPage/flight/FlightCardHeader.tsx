import { Plane, Clock, Hash, GripVertical, ChevronDown } from "lucide-react";
import type { SyntheticListenerMap } from "@dnd-kit/core/dist/hooks/utilities";
import type { DraggableAttributes } from "@dnd-kit/core";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { cn } from "@/lib/utils";
import type { Flight, FlightPassenger } from "@/types/flight";

function initials(name: string) {
  return name.split(" ").slice(0, 2).map((n) => n[0]).join("").toUpperCase();
}

function isoToTime(iso: string) {
  return (iso.split("T")[1] ?? "00:00").slice(0, 5);
}

interface FlightCardHeaderProps {
  flight: Flight;
  step: number;
  passengers: FlightPassenger[];
  expanded: boolean;
  onToggle: () => void;
  myPassenger: FlightPassenger | null;
  isActive: boolean;
  onStepClick: () => void;
  dragHandleListeners?: SyntheticListenerMap;
  dragHandleAttributes?: DraggableAttributes;
}

export function FlightCardHeader({
  flight, step, passengers, expanded, onToggle, myPassenger,
  isActive, onStepClick,
  dragHandleListeners, dragHandleAttributes,
}: FlightCardHeaderProps) {
  return (
    <div className="w-full text-left cursor-pointer" onClick={onToggle}>
      <div className="flex items-start gap-3 px-4 py-3">
        <div className="flex shrink-0 flex-col items-center gap-1 pt-0.5">
          <button
            onClick={(e) => { e.stopPropagation(); onStepClick(); }}
            aria-label={`Go to step ${step}`}
            className={cn(
              "flex h-7 w-7 items-center justify-center rounded-full text-xs font-bold shadow-sm transition-all",
              isActive
                ? "bg-gradient-primary text-white ring-2 ring-[#daa060] ring-offset-2"
                : "border border-muted-foreground text-muted-foreground hover:opacity-70",
            )}
          >
            {step}
          </button>
          <button
            className="cursor-grab active:cursor-grabbing touch-none text-amber-300 hover:text-amber-500 transition-colors"
            {...dragHandleListeners}
            {...dragHandleAttributes}
          >
            <GripVertical className="h-4 w-4" />
          </button>
        </div>

        <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-gray-900">
          <Plane className="h-4 w-4 text-white" />
        </div>

        <div className="flex-1 min-w-0">
          <div className="flex flex-wrap items-center gap-2">
            <span className="font-semibold text-sm text-gray-900">
              {flight.originAirport} → {flight.destinationAirport}
            </span>
            <Badge variant="outline" className="text-xs bg-gray-50">
              Voo
            </Badge>
            <span className="text-xs text-gray-500 font-medium">
              {flight.airline} · {flight.flightNumber}
            </span>
          </div>

          <div className="mt-1 flex flex-wrap items-center gap-x-3 gap-y-0.5 text-xs text-gray-600">
            <span className="flex items-center gap-1">
              <Clock className="h-3 w-3" />
              {isoToTime(flight.departureLocalTime)} ({flight.departureTimezone}) — {isoToTime(flight.arrivalLocalTime)} ({flight.arrivalTimezone})
            </span>
            {myPassenger && (
              <span className="flex items-center gap-1">
                <span className="text-gray-400">$</span>
                <span className="font-medium text-gray-800">€{myPassenger.price}</span>
              </span>
            )}
          </div>

          <p className="mt-0.5 flex items-center gap-1 text-xs text-gray-500">
            <Hash className="h-3 w-3" />
            Reserva:{" "}
            <span className="font-mono font-medium text-gray-700">{flight.bookingReference}</span>
          </p>
        </div>

        <div className="flex shrink-0 flex-col items-end gap-1.5">
          <div className="flex -space-x-2">
            {passengers.slice(0, 3).map((p) => (
              <Avatar key={p.id} className="h-7 w-7 border-2 border-amber-50">
                <AvatarFallback className="text-[10px] bg-gray-200 text-gray-700">
                  {initials(p.groupMember.user.name)}
                </AvatarFallback>
              </Avatar>
            ))}
            {passengers.length > 3 && (
              <div className="flex h-7 w-7 items-center justify-center rounded-full border-2 border-amber-50 bg-gray-300 text-[10px] font-medium text-gray-700">
                +{passengers.length - 3}
              </div>
            )}
          </div>
          <ChevronDown className={`h-4 w-4 text-amber-400 transition-transform ${expanded ? "rotate-180" : ""}`} />
        </div>
      </div>
    </div>
  );
}
