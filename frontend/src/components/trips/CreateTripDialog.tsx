import { useState, useEffect } from "react";
import { Users, MapPin, X, Plus } from "lucide-react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/components/ui/dialog";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { CreateGroupDialog } from "@/components/groups/CreateGroupDialog";
import { getGroupsByUser } from "@/services/GroupService";
import { createTrip } from "@/services/TripService";
import type { Group } from "@/types/group";
import type { TripDTO } from "@/types/trip";

interface CreateTripDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  userId: number;
  onTripCreated?: () => void;
}

type DialogStep = "loading" | "no-groups" | "form";

const CURRENCIES = ["EUR", "USD", "GBP", "BRL", "CHF", "JPY", "CAD", "AUD"]; // change to api future

export function CreateTripDialog({
  open,
  onOpenChange,
  userId,
  onTripCreated,
}: CreateTripDialogProps) {
  const [step, setStep] = useState<DialogStep>("loading");
  const [groups, setGroups] = useState<Group[]>([]);
  const [createGroupOpen, setCreateGroupOpen] = useState(false);

  // Form state
  const [selectedGroupId, setSelectedGroupId] = useState<string>("");
  const [destinations, setDestinations] = useState<string[]>([]);
  const [destinationInput, setDestinationInput] = useState("");
  const [description, setDescription] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [currency, setCurrency] = useState("EUR");
  const [budget, setBudget] = useState<string>("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!open) return;
    fetchGroups();
  }, [open]);

  const fetchGroups = async () => {
    setStep("loading");
    try {
      const data = await getGroupsByUser(userId);
      setGroups(data);
      setStep(data.length === 0 ? "no-groups" : "form");
    } catch {
      setStep("no-groups");
    }
  };

  const handleGroupCreated = (group: Group) => {
    setCreateGroupOpen(false);
    setGroups((prev) => [...prev, group]);
    setSelectedGroupId(String(group.id));
    setStep("form");
  };

  const addDestination = () => {
    const trimmed = destinationInput.trim();
    if (trimmed && !destinations.includes(trimmed)) {
      setDestinations((prev) => [...prev, trimmed]);
    }
    setDestinationInput("");
  };

  const removeDestination = (dest: string) => {
    setDestinations((prev) => prev.filter((d) => d !== dest));
  };

  const handleDestinationKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      e.preventDefault();
      addDestination();
    }
  };

  const resetForm = () => {
    setSelectedGroupId("");
    setDestinations([]);
    setDestinationInput("");
    setDescription("");
    setStartDate("");
    setEndDate("");
    setCurrency("EUR");
    setBudget("");
    setError(null);
  };

  const handleClose = (isOpen: boolean) => {
    if (!isOpen) resetForm();
    onOpenChange(isOpen);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedGroupId || destinations.length === 0) return;

    setIsSubmitting(true);
    setError(null);

    const tripData: TripDTO = {
      destinations,
      description: description.trim(),
      startDate,
      endDate,
      mainCurrency: currency,
      totalBudget: parseFloat(budget) || 0,
      status: "PLANNING",
    };

    try {
      await createTrip(Number(selectedGroupId), userId, tripData);
      resetForm();
      onOpenChange(false);
      onTripCreated?.();
    } catch {
      setError("Erro ao criar viagem. Tenta novamente.");
    } finally {
      setIsSubmitting(false);
    }
  };

  const isFormValid =
    selectedGroupId !== "" &&
    destinations.length > 0 &&
    startDate !== "" &&
    endDate !== "" &&
    endDate >= startDate;

  return (
    <>
      <Dialog open={open} onOpenChange={handleClose}>
        <DialogContent className="sm:max-w-lg max-h-[90vh] overflow-y-auto">
          {step === "loading" && (
            <div className="flex items-center justify-center py-12 text-muted-foreground text-sm">
              A carregar grupos...
            </div>
          )}

          {step === "no-groups" && (
            <>
              <DialogHeader>
                <DialogTitle>Planejar nova viagem</DialogTitle>
                <DialogDescription>
                  Precisas de estar num grupo para poderes criar uma viagem.
                </DialogDescription>
              </DialogHeader>
              <div className="flex flex-col items-center gap-4 py-8 text-center">
                <div className="flex h-16 w-16 items-center justify-center rounded-full bg-muted">
                  <Users className="h-8 w-8 text-muted-foreground" />
                </div>
                <div className="space-y-1">
                  <p className="font-medium">Nenhum grupo encontrado</p>
                  <p className="text-sm text-muted-foreground">
                    Cria um grupo primeiro e depois poderá planejar nossa viagem juntos.
                  </p>
                </div>
                <Button
                  className="bg-gradient-primary text-white hover:opacity-90 cursor-pointer"
                  onClick={() => setCreateGroupOpen(true)}
                >
                  <Plus className="h-4 w-4" />
                  Criar grupo
                </Button>
              </div>
            </>
          )}

          {step === "form" && (
            <>
              <DialogHeader>
                <DialogTitle>Planejar nova viagem</DialogTitle>
                <DialogDescription>
                  Preenche os detalhes da viagem. Pode editar tudo mais tarde.
                </DialogDescription>
              </DialogHeader>

              <form onSubmit={handleSubmit} className="space-y-5 py-2">
                {/* Group selector */}
                <div className="space-y-2">
                  <Label htmlFor="group-select">Grupo</Label>
                  <Select value={selectedGroupId} onValueChange={setSelectedGroupId}>
                    <SelectTrigger id="group-select" className="w-full">
                      <SelectValue placeholder="Selecione um grupo..." />
                    </SelectTrigger>
                    <SelectContent>
                      {groups.map((g) => (
                        <SelectItem key={g.id} value={String(g.id)}>
                          {g.name}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                {/* Destinations */}
                <div className="space-y-2">
                  <Label>Destinos</Label>
                  <div className="flex gap-2">
                    <div className="relative flex-1">
                      <MapPin className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                      <Input
                        placeholder="ex: Lisboa, Portugal"
                        value={destinationInput}
                        onChange={(e) => setDestinationInput(e.target.value)}
                        onKeyDown={handleDestinationKeyDown}
                        className="pl-9"
                      />
                    </div>
                    <Button
                      type="button"
                      variant="outline"
                      onClick={addDestination}
                      disabled={!destinationInput.trim()}
                      className="cursor-pointer"
                    >
                      Adicionar
                    </Button>
                  </div>
                  {destinations.length > 0 && (
                    <div className="flex flex-wrap gap-2 pt-1">
                      {destinations.map((dest) => (
                        <span
                          key={dest}
                          className="flex items-center gap-1 rounded-full bg-muted px-3 py-1 text-sm font-medium"
                        >
                          {dest}
                          <button
                            type="button"
                            onClick={() => removeDestination(dest)}
                            className="ml-1 rounded-full p-0.5 hover:bg-muted-foreground/20 transition-colors cursor-pointer"
                          >
                            <X className="h-3 w-3" />
                          </button>
                        </span>
                      ))}
                    </div>
                  )}
                </div>

                {/* Description */}
                <div className="space-y-2">
                  <Label htmlFor="trip-description">
                    Descrição{" "}
                    <span className="text-muted-foreground text-xs">(opcional)</span>
                  </Label>
                  <Textarea
                    id="trip-description"
                    placeholder="O que está planejado para esta viagem?"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    rows={3}
                  />
                </div>

                {/* Dates */}
                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="start-date">Data de início</Label>
                    <Input
                      id="start-date"
                      type="date"
                      value={startDate}
                      onChange={(e) => setStartDate(e.target.value)}
                      required
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="end-date">Data de fim</Label>
                    <Input
                      id="end-date"
                      type="date"
                      value={endDate}
                      min={startDate}
                      onChange={(e) => setEndDate(e.target.value)}
                      required
                    />
                  </div>
                </div>

                {/* Currency & Budget */}
                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="currency">Moeda principal</Label>
                    <Select value={currency} onValueChange={setCurrency}>
                      <SelectTrigger id="currency" className="w-full">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        {CURRENCIES.map((c) => (
                          <SelectItem key={c} value={c}>
                            {c}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="budget">
                      Orçamento{" "}
                      <span className="text-muted-foreground text-xs">(opcional)</span>
                    </Label>
                    <Input
                      id="budget"
                      type="number"
                      min="0"
                      step="0.01"
                      placeholder="0.00"
                      value={budget}
                      onChange={(e) => setBudget(e.target.value)}
                    />
                  </div>
                </div>

                {error && <p className="text-sm text-destructive">{error}</p>}

                <DialogFooter>
                  <Button
                    type="button"
                    variant="outline"
                    onClick={() => handleClose(false)}
                    disabled={isSubmitting}
                    className="cursor-pointer"
                  >
                    Cancelar
                  </Button>
                  <Button
                    type="submit"
                    className="bg-gradient-primary text-white hover:opacity-90 cursor-pointer"
                    disabled={isSubmitting || !isFormValid}
                  >
                    {isSubmitting ? "Criando..." : "Criar viagem"}
                  </Button>
                </DialogFooter>
              </form>
            </>
          )}
        </DialogContent>
      </Dialog>

      <CreateGroupDialog
        open={createGroupOpen}
        onOpenChange={setCreateGroupOpen}
        userId={userId}
        onGroupCreated={handleGroupCreated}
      />
    </>
  );
}
