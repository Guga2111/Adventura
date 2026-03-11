import { Loader2, Pencil, PlaneTakeoff } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter,
} from "@/components/ui/dialog";
import { PassengerFormFields } from "./PassengerFormFields";
import type { PassengerForm } from "./useFlightPassengers";

interface PassengerDialogProps {
  mode: "join" | "edit" | null;
  form: PassengerForm;
  setForm: React.Dispatch<React.SetStateAction<PassengerForm>>;
  onSubmit: (e: React.FormEvent) => void;
  onClose: () => void;
  loading: boolean;
  error: string | null;
}

export function PassengerDialog({ mode, form, setForm, onSubmit, onClose, loading, error }: PassengerDialogProps) {
  const isJoin = mode === "join";
  return (
    <Dialog open={mode !== null} onOpenChange={(v) => !v && onClose()}>
      <DialogContent className="max-w-sm">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            {isJoin
              ? <><PlaneTakeoff className="h-4 w-4" /> Entre no voo</>
              : <><Pencil className="h-4 w-4" /> Edite meus detalhes</>}
          </DialogTitle>
        </DialogHeader>
        <form onSubmit={onSubmit} className="space-y-4">
          <PassengerFormFields form={form} setForm={setForm} />
          {error && <p className="text-xs text-red-500">{error}</p>}
          <DialogFooter>
            <Button type="button" variant="outline" onClick={onClose} disabled={loading}>Cancelar</Button>
            <Button type="submit" disabled={loading}>
              {loading && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
              {isJoin ? "Join" : "Save"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
