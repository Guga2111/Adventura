import { useState } from "react";
import { BookOpen, PlaneTakeoff } from "lucide-react";
import type { SyntheticListenerMap } from "@dnd-kit/core/dist/hooks/utilities";
import type { DraggableAttributes } from "@dnd-kit/core";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
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
  isActive: boolean;
  onStepClick: () => void;
  dragHandleListeners?: SyntheticListenerMap;
  dragHandleAttributes?: DraggableAttributes;
}

export function FlightCard({ flight, tripId, members, step, isActive, onStepClick, dragHandleListeners, dragHandleAttributes }: FlightCardProps) {
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
      <div className={cn("rounded-xl border overflow-hidden shadow-sm transition-all", isActive && "ring-2 ring-[#daa060]")}>
        <FlightCardHeader
          flight={flight}
          step={step}
          passengers={passengers}
          expanded={expanded}
          onToggle={() => setExpanded((v) => !v)}
          myPassenger={myPassenger}
          isActive={isActive}
          onStepClick={onStepClick}
          dragHandleListeners={dragHandleListeners}
          dragHandleAttributes={dragHandleAttributes}
        />

        {expanded && (
          <div className="border-t border-gray-200 bg-white px-4 py-3 space-y-3">
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
                Entre no voo
              </Button>
            )}

            {flight.notes && (
              <div className="flex gap-2 rounded-lg bg-gray-50 border border-gray-200 px-3 py-2 text-xs text-gray-600">
                <BookOpen className="mt-0.5 h-3.5 w-3.5 shrink-0 text-gray-400" />
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
