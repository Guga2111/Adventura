import { useState } from "react";
import { joinFlight, updatePassenger, removePassenger } from "@/services/FlightService";
import type { FlightPassenger, SeatClass, FlightPassengerStatus } from "@/types/flight";
import type { GroupMember } from "@/types/group";

export interface PassengerForm {
  seatNumber: string;
  seatClass: SeatClass | "";
  price: string;
  status: FlightPassengerStatus;
}

export const EMPTY_FORM: PassengerForm = { seatNumber: "", seatClass: "", price: "", status: "UNDECIDED" };

export function formFromPassenger(p: FlightPassenger): PassengerForm {
  return {
    seatNumber: p.seatNumber ?? "",
    seatClass: p.seatClass ?? "",
    price: String(p.price ?? ""),
    status: p.status,
  };
}

interface UseFlightPassengersParams {
  initialPassengers: FlightPassenger[];
  tripId: number;
  flightId: number;
  myGroupMember: GroupMember | null;
}

export function useFlightPassengers({ initialPassengers, tripId, flightId, myGroupMember }: UseFlightPassengersParams) {
  const [passengers, setPassengers] = useState<FlightPassenger[]>(initialPassengers);
  const [dialog, setDialog] = useState<"join" | "edit" | null>(null);
  const [form, setForm] = useState<PassengerForm>(EMPTY_FORM);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [removingId, setRemovingId] = useState<number | null>(null);

  function openJoin() {
    setForm(EMPTY_FORM);
    setError(null);
    setDialog("join");
  }

  function openEdit(passenger: FlightPassenger) {
    setForm(formFromPassenger(passenger));
    setError(null);
    setDialog("edit");
  }

  function closeDialog() {
    setDialog(null);
  }

  async function handleJoin(e: React.FormEvent) {
    e.preventDefault();
    if (!myGroupMember) return;
    setLoading(true);
    setError(null);
    try {
      const created = await joinFlight(tripId, flightId, {
        groupMemberId: myGroupMember.id,
        seatNumber:    form.seatNumber || undefined,
        seatClass:     form.seatClass  || undefined,
        price:         form.price      ? Number(form.price) : undefined,
        status:        form.status,
      });
      setPassengers((prev) => [...prev, created]);
      setDialog(null);
    } catch {
      setError("Failed to join flight. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  async function handleEdit(e: React.FormEvent) {
    e.preventDefault();
    const myPassenger = passengers.find((p) => p.groupMember.user.id === myGroupMember?.user.id) ?? null;
    if (!myPassenger || !myGroupMember) return;
    setLoading(true);
    setError(null);
    try {
      const updated = await updatePassenger(tripId, flightId, myPassenger.id, {
        groupMemberId: myGroupMember.id,
        seatNumber:    form.seatNumber || undefined,
        seatClass:     form.seatClass  || undefined,
        price:         form.price      ? Number(form.price) : undefined,
        status:        form.status,
      });
      setPassengers((prev) => prev.map((p) => (p.id === myPassenger.id ? updated : p)));
      setDialog(null);
    } catch {
      setError("Failed to update passenger. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  async function handleRemove(passengerId: number) {
    setRemovingId(passengerId);
    try {
      await removePassenger(tripId, flightId, passengerId);
      setPassengers((prev) => prev.filter((p) => p.id !== passengerId));
    } catch {
      // silent — could show a toast in the future
    } finally {
      setRemovingId(null);
    }
  }

  return {
    passengers,
    dialog,
    form,
    setForm,
    loading,
    error,
    removingId,
    openJoin,
    openEdit,
    closeDialog,
    handleJoin,
    handleEdit,
    handleRemove,
  };
}
