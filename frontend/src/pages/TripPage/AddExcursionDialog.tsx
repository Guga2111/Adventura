import { useState, useEffect } from "react";
import { Binoculars, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import type { GroupMember } from "@/types/group";

export interface AddExcursionDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  members: GroupMember[];
  sendUpdate: (msg: object) => void;
}

export function AddExcursionDialog({ open, onOpenChange, members, sendUpdate }: AddExcursionDialogProps) {
  const [excLoading, setExcLoading] = useState(false);
  const [excError, setExcError] = useState<string | null>(null);
  const [excName, setExcName] = useState("");
  const [excPrice, setExcPrice] = useState("");
  const [excStartDate, setExcStartDate] = useState("");
  const [excEndDate, setExcEndDate] = useState("");
  const [presenceRows, setPresenceRows] = useState(() =>
    members.map((m) => ({ groupMemberId: m.id, name: m.user.name, included: false }))
  );

  function resetExcForm() {
    setExcName("");
    setExcPrice("");
    setExcStartDate("");
    setExcEndDate("");
    setPresenceRows(members.map((m) => ({ groupMemberId: m.id, name: m.user.name, included: false })));
    setExcError(null);
  }

  useEffect(() => {
    if (!open) resetExcForm();
  }, [open]);

  function handleExcSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!excName.trim() || !excStartDate) {
      setExcError("Name and start date are required.");
      return;
    }
    setExcLoading(true);
    setExcError(null);
    const presenceIds = presenceRows.filter((r) => r.included).map((r) => r.groupMemberId);
    sendUpdate({
      action: "ADD",
      name: excName.trim(),
      price: excPrice ? Number(excPrice) : undefined,
      startDate: excStartDate,
      endDate: excEndDate || undefined,
      presenceIds,
    });
    setExcLoading(false);
    onOpenChange(false);
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-md max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-gray-900">
              <Binoculars className="h-4 w-4 text-white" />
            </div>
            Add Excursion
          </DialogTitle>
        </DialogHeader>

        <form onSubmit={handleExcSubmit} className="space-y-5">
          <div className="space-y-1.5">
            <Label htmlFor="excName">Name</Label>
            <Input
              id="excName"
              placeholder="Bosphorus Cruise"
              value={excName}
              onChange={(e) => setExcName(e.target.value)}
              required
            />
          </div>

          <div className="space-y-1.5">
            <Label htmlFor="excPrice">
              Price per person <span className="text-muted-foreground text-xs">(optional, €)</span>
            </Label>
            <Input
              id="excPrice"
              type="number"
              min="0"
              placeholder="45"
              value={excPrice}
              onChange={(e) => setExcPrice(e.target.value)}
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label htmlFor="excStartDate">Start date</Label>
              <Input
                id="excStartDate"
                type="datetime-local"
                value={excStartDate}
                onChange={(e) => setExcStartDate(e.target.value)}
                required
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="excEndDate">
                End date <span className="text-muted-foreground text-xs">(optional)</span>
              </Label>
              <Input
                id="excEndDate"
                type="datetime-local"
                value={excEndDate}
                onChange={(e) => setExcEndDate(e.target.value)}
              />
            </div>
          </div>

          {members.length > 0 && (
            <div className="space-y-2">
              <Label>Participants</Label>
              <div className="rounded-lg border divide-y text-sm">
                {presenceRows.map((row, i) => (
                  <label key={row.groupMemberId} className="flex items-center gap-2 cursor-pointer p-3">
                    <input
                      type="checkbox"
                      checked={row.included}
                      onChange={(e) =>
                        setPresenceRows((rows) =>
                          rows.map((r, idx) => idx === i ? { ...r, included: e.target.checked } : r)
                        )
                      }
                      className="rounded"
                    />
                    <span className="font-medium text-gray-800">{row.name}</span>
                  </label>
                ))}
              </div>
            </div>
          )}

          {excError && <p className="text-sm text-red-500">{excError}</p>}

          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
              disabled={excLoading}
            >
              Cancel
            </Button>
            <Button type="submit" disabled={excLoading}>
              {excLoading && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
              Add Excursion
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
