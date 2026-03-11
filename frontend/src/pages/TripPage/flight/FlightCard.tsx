import { useState } from "react";
import { BookOpen, PlaneTakeoff } from "lucide-react";
import type { SyntheticListenerMap } from "@dnd-kit/core/dist/hooks/utilities";
import type { DraggableAttributes } from "@dnd-kit/core";
import { Button } from "@/components/ui/button";
import type { Flight } from "@/types/flight";
import type { GroupMember } from "@/types/group";
import { useAuth } from "@/context/AuthContext";
import { useFlightPassengers } from "./useFlightPassengers";
import { FlightCardHeader } from "./FlightCardHeader";
import { PassengerTable } from "./PassengerTable";
import { PassengerDialog } from "./PassengerDialog";

interface FlightCardProps {
  flight: Flight;
  tripId: number;
  members: GroupMember[];
  step: number;
  dragHandleListeners?: SyntheticListenerMap;
  dragHandleAttributes?: DraggableAttributes;
}

export function FlightCard({ flight, tripId, members, step, dragHandleListeners, dragHandleAttributes }: FlightCardProps) {
  const [expanded, setExpanded] = useState(false);

  const { user } = useAuth();
  const currentUserId = user?.userId ?? null;
  const myGroupMember = members.find((m) => m.user.id === currentUserId) ?? null;

  const {
    passengers, dialog, form, setForm, loading, error, removingId,
    openJoin, openEdit, closeDialog, handleJoin, handleEdit, handleRemove,
  } = useFlightPassengers({
    initialPassengers: flight.passengers,
    tripId,
    flightId: flight.id,
    myGroupMember,
  });

  const myPassenger = passengers.find((p) => p.groupMember.user.id === currentUserId) ?? null;

  return (
    <>
      <div className="rounded-xl border border-amber-200 bg-amber-50 overflow-hidden shadow-sm">
        <FlightCardHeader
          flight={flight}
          step={step}
          passengers={passengers}
          expanded={expanded}
          onToggle={() => setExpanded((v) => !v)}
          myPassenger={myPassenger}
          dragHandleListeners={dragHandleListeners}
          dragHandleAttributes={dragHandleAttributes}
        />

        {expanded && (
          <div className="border-t border-amber-200 bg-white px-4 py-3 space-y-3">
            <PassengerTable
              passengers={passengers}
              currentUserId={currentUserId}
              removingId={removingId}
              onEdit={openEdit}
              onRemove={handleRemove}
            />

            {!myPassenger && myGroupMember && (
              <Button size="sm" variant="outline" className="w-full border-dashed text-gray-500" onClick={openJoin}>
                <PlaneTakeoff className="h-3.5 w-3.5 mr-1.5" />
                Join this flight
              </Button>
            )}

            {flight.notes && (
              <div className="flex gap-2 rounded-lg bg-amber-50 border border-amber-100 px-3 py-2 text-xs text-gray-600">
                <BookOpen className="mt-0.5 h-3.5 w-3.5 shrink-0 text-amber-500" />
                <span>{flight.notes}</span>
              </div>
            )}
          </div>
        )}
      </div>

      <PassengerDialog
        mode={dialog}
        form={form}
        setForm={setForm}
        onSubmit={dialog === "join" ? handleJoin : handleEdit}
        onClose={closeDialog}
        loading={loading}
        error={error}
      />
    </>
  );
}
