import { useState } from "react";
import { Plus, Binoculars } from "lucide-react";
import { Button } from "@/components/ui/button";
import type { GroupMember } from "@/types/group";
import { AddFlightDialog } from "./AddFlightDialog";
import { AddExcursionDialog } from "./AddExcursionDialog";

interface TripPageExcursionsHeaderProps {
  tripId: number;
  members: GroupMember[];
  onFlightCreated: () => void;
  sendUpdate: (msg: object) => void;
}

export function TripPageExcursionsHeader({ tripId, members, onFlightCreated, sendUpdate }: TripPageExcursionsHeaderProps) {
  const [flightOpen, setFlightOpen] = useState(false);
  const [excursionOpen, setExcursionOpen] = useState(false);

  return (
    <>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-lg font-semibold text-foreground">Jornal da Viagem</h1>
        <div className="flex items-center justify-between gap-2">
          <Button size="sm" className="cursor-pointer rounded-full bg-gradient-primary" onClick={() => setFlightOpen(true)}>
            <Plus className="h-4 w-4 mr-1" />
            Adicione Voo
          </Button>
          <Button size="sm" className="cursor-pointer rounded-full bg-gradient-primary" onClick={() => setExcursionOpen(true)}>
            <Binoculars className="h-4 w-4 mr-1" />
            Adicione Passeio
          </Button>
        </div>
      </div>

      <AddFlightDialog
        open={flightOpen}
        onOpenChange={setFlightOpen}
        tripId={tripId}
        members={members}
        onFlightCreated={onFlightCreated}
      />

      <AddExcursionDialog
        open={excursionOpen}
        onOpenChange={setExcursionOpen}
        members={members}
        sendUpdate={sendUpdate}
      />
    </>
  );
}
