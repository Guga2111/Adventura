import { useState } from "react";
import { CalendarClock } from "lucide-react";
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

export interface RescheduleTarget {
  kind: "flight" | "excursion";
  id: number;
  name: string;
  currentStartDate: string; 
  currentEndDate?: string;
}

interface RescheduleDialogProps {
  target: RescheduleTarget | null;
  onConfirm: (startDate: string, endDate: string | undefined) => void;
  onCancel: () => void;
}

function RescheduleDialogInner({
  target,
  onConfirm,
  onCancel,
}: { target: RescheduleTarget } & Pick<RescheduleDialogProps, "onConfirm" | "onCancel">) {
  const [date,      setDate]      = useState(target.currentStartDate.split("T")[0]);
  const [startTime, setStartTime] = useState((target.currentStartDate.split("T")[1] ?? "").slice(0, 5));
  const [endTime,   setEndTime]   = useState(
    target.currentEndDate ? (target.currentEndDate.split("T")[1] ?? "").slice(0, 5) : "",
  );

  function handleConfirm() {
    onConfirm(`${date}T${startTime}:00`, endTime ? `${date}T${endTime}:00` : undefined);
  }

  return (
    <Dialog open onOpenChange={(open) => !open && onCancel()}>
      <DialogContent className="max-w-sm">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <CalendarClock className="h-4 w-4" />
            Reagendar
          </DialogTitle>
          <p className="text-sm text-muted-foreground truncate">{target.name}</p>
        </DialogHeader>

        <div className="space-y-3">
          <div className="space-y-1.5">
            <Label>Data</Label>
            <Input type="date" value={date} onChange={(e) => setDate(e.target.value)} />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label>Tempo de início</Label>
              <Input type="time" value={startTime} onChange={(e) => setStartTime(e.target.value)} />
            </div>
            <div className="space-y-1.5">
              <Label>Tempo final <span className="text-muted-foreground">(optional)</span></Label>
              <Input type="time" value={endTime} onChange={(e) => setEndTime(e.target.value)} />
            </div>
          </div>
          {target.kind === "flight" && (
            <p className="text-xs text-amber-600 bg-amber-50 border border-amber-200 rounded-lg px-3 py-2">
              Reagendamento do voo é apenas informacional — se a passagem mudar comunicar diretamente com a companhia de sua escolha.
            </p>
          )}
        </div>

        <DialogFooter>
          <Button variant="outline" onClick={onCancel}>Cancelar</Button>
          <Button onClick={handleConfirm} disabled={!date || !startTime}>
            Confirmar
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

export function RescheduleDialog({ target, onConfirm, onCancel }: RescheduleDialogProps) {
  if (!target) return null;
  return (
    <RescheduleDialogInner
      key={`${target.kind}-${target.id}`}
      target={target}
      onConfirm={onConfirm}
      onCancel={onCancel}
    />
  );
}
