import { useState, useEffect } from "react";
import { Plane, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { createFlight } from "@/services/FlightService";
import type { FlightDTO, FlightPassengerDTO, SeatClass, FlightPassengerStatus } from "@/types/flight";
import type { GroupMember } from "@/types/group";

interface PassengerRow {
  groupMemberId: number;
  name: string;
  included: boolean;
  seatNumber: string;
  seatClass: SeatClass | "";
  price: string;
  status: FlightPassengerStatus;
}

export interface AddFlightDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  tripId: number;
  members: GroupMember[];
  onFlightCreated: () => void;
}

const SEAT_CLASS_OPTIONS: { value: SeatClass; label: string }[] = [
  { value: "ECONOMY", label: "Economy" },
  { value: "PREMIUM_ECONOMY", label: "Premium Economy" },
  { value: "BUSINESS", label: "Business" },
  { value: "FIRST", label: "First" },
];

const STATUS_OPTIONS: { value: FlightPassengerStatus; label: string }[] = [
  { value: "UNDECIDED", label: "Undecided" },
  { value: "CONFIRMED", label: "Confirmed" },
  { value: "CANCELLED", label: "Cancelled" },
];

export function AddFlightDialog({ open, onOpenChange, tripId, members, onFlightCreated }: AddFlightDialogProps) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [flightNumber, setFlightNumber] = useState("");
  const [airline, setAirline] = useState("");
  const [originAirport, setOriginAirport] = useState("");
  const [destinationAirport, setDestinationAirport] = useState("");
  const [departureLocalTime, setDepartureLocalTime] = useState("");
  const [departureTimezone, setDepartureTimezone] = useState("");
  const [arrivalLocalTime, setArrivalLocalTime] = useState("");
  const [arrivalTimezone, setArrivalTimezone] = useState("");
  const [bookingReference, setBookingReference] = useState("");
  const [notes, setNotes] = useState("");
  const [passengerRows, setPassengerRows] = useState<PassengerRow[]>(() =>
    members.map((m) => ({
      groupMemberId: m.id,
      name: m.user.name,
      included: false,
      seatNumber: "",
      seatClass: "",
      price: "",
      status: "UNDECIDED",
    }))
  );

  function updatePassenger(index: number, patch: Partial<PassengerRow>) {
    setPassengerRows((rows) =>
      rows.map((row, i) => (i === index ? { ...row, ...patch } : row))
    );
  }

  function resetForm() {
    setFlightNumber("");
    setAirline("");
    setOriginAirport("");
    setDestinationAirport("");
    setDepartureLocalTime("");
    setDepartureTimezone("");
    setArrivalLocalTime("");
    setArrivalTimezone("");
    setBookingReference("");
    setNotes("");
    setPassengerRows(
      members.map((m) => ({
        groupMemberId: m.id,
        name: m.user.name,
        included: false,
        seatNumber: "",
        seatClass: "",
        price: "",
        status: "UNDECIDED",
      }))
    );
    setError(null);
  }

  useEffect(() => {
    if (!open) resetForm();
  }, [open]);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true);
    setError(null);

    const passengers: FlightPassengerDTO[] = passengerRows
      .filter((r) => r.included)
      .map((r) => ({
        groupMemberId: r.groupMemberId,
        seatNumber: r.seatNumber || undefined,
        seatClass: r.seatClass || undefined,
        price: r.price ? Number(r.price) : undefined,
        status: r.status,
      }));

    const dto: FlightDTO = {
      flightNumber: flightNumber || undefined,
      airline: airline || undefined,
      originAirport: originAirport.toUpperCase(),
      destinationAirport: destinationAirport.toUpperCase(),
      departureLocalTime,
      departureTimezone,
      arrivalLocalTime,
      arrivalTimezone,
      bookingReference: bookingReference || undefined,
      notes: notes || undefined,
      passengers,
    };

    try {
      await createFlight(tripId, dto);
      onOpenChange(false);
      onFlightCreated();
    } catch {
      setError("Failed to create flight. Please check the fields and try again.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-lg max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-gray-900">
              <Plane className="h-4 w-4 text-white" />
            </div>
            Add Flight
          </DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-5">
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label htmlFor="airline">Airline</Label>
              <Input
                id="airline"
                placeholder="Turkish Airlines"
                value={airline}
                onChange={(e) => setAirline(e.target.value)}
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="flightNumber">Flight number</Label>
              <Input
                id="flightNumber"
                placeholder="TK 12"
                value={flightNumber}
                onChange={(e) => setFlightNumber(e.target.value)}
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label htmlFor="origin">
                Origin <span className="text-muted-foreground text-xs">(IATA)</span>
              </Label>
              <Input
                id="origin"
                placeholder="GRU"
                maxLength={3}
                value={originAirport}
                onChange={(e) => setOriginAirport(e.target.value.toUpperCase())}
                required
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="destination">
                Destination <span className="text-muted-foreground text-xs">(IATA)</span>
              </Label>
              <Input
                id="destination"
                placeholder="IST"
                maxLength={3}
                value={destinationAirport}
                onChange={(e) => setDestinationAirport(e.target.value.toUpperCase())}
                required
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label htmlFor="departureTime">Departure</Label>
              <Input
                id="departureTime"
                type="datetime-local"
                value={departureLocalTime}
                onChange={(e) => setDepartureLocalTime(e.target.value)}
                required
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="departureTz">Departure timezone</Label>
              <Input
                id="departureTz"
                placeholder="America/Sao_Paulo"
                value={departureTimezone}
                onChange={(e) => setDepartureTimezone(e.target.value)}
                required
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label htmlFor="arrivalTime">Arrival</Label>
              <Input
                id="arrivalTime"
                type="datetime-local"
                value={arrivalLocalTime}
                onChange={(e) => setArrivalLocalTime(e.target.value)}
                required
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="arrivalTz">Arrival timezone</Label>
              <Input
                id="arrivalTz"
                placeholder="Europe/Istanbul"
                value={arrivalTimezone}
                onChange={(e) => setArrivalTimezone(e.target.value)}
                required
              />
            </div>
          </div>

          <div className="space-y-1.5">
            <Label htmlFor="bookingRef">Booking reference</Label>
            <Input
              id="bookingRef"
              placeholder="ABC123"
              value={bookingReference}
              onChange={(e) => setBookingReference(e.target.value)}
            />
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="notes">Notes</Label>
            <Textarea
              id="notes"
              placeholder="Window seats requested..."
              rows={2}
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
            />
          </div>

          {members.length > 0 && (
            <div className="space-y-2">
              <Label>Passengers</Label>
              <div className="rounded-lg border divide-y text-sm">
                {passengerRows.map((row, i) => (
                  <div key={row.groupMemberId} className="p-3 space-y-2">
                    <label className="flex items-center gap-2 cursor-pointer">
                      <input
                        type="checkbox"
                        checked={row.included}
                        onChange={(e) => updatePassenger(i, { included: e.target.checked })}
                        className="rounded"
                      />
                      <span className="font-medium text-gray-800">{row.name}</span>
                    </label>

                    {row.included && (
                      <div className="grid grid-cols-2 gap-2 pl-6">
                        <div className="space-y-1">
                          <Label className="text-xs">Seat</Label>
                          <Input
                            placeholder="14A"
                            value={row.seatNumber}
                            onChange={(e) => updatePassenger(i, { seatNumber: e.target.value })}
                            className="h-8 text-xs"
                          />
                        </div>
                        <div className="space-y-1">
                          <Label className="text-xs">Price (€)</Label>
                          <Input
                            type="number"
                            placeholder="850"
                            value={row.price}
                            onChange={(e) => updatePassenger(i, { price: e.target.value })}
                            className="h-8 text-xs"
                          />
                        </div>
                        <div className="space-y-1">
                          <Label className="text-xs">Class</Label>
                          <Select
                            value={row.seatClass}
                            onValueChange={(v) => updatePassenger(i, { seatClass: v as SeatClass })}
                          >
                            <SelectTrigger className="h-8 text-xs">
                              <SelectValue placeholder="Select" />
                            </SelectTrigger>
                            <SelectContent>
                              {SEAT_CLASS_OPTIONS.map((opt) => (
                                <SelectItem key={opt.value} value={opt.value}>
                                  {opt.label}
                                </SelectItem>
                              ))}
                            </SelectContent>
                          </Select>
                        </div>
                        <div className="space-y-1">
                          <Label className="text-xs">Status</Label>
                          <Select
                            value={row.status}
                            onValueChange={(v) => updatePassenger(i, { status: v as FlightPassengerStatus })}
                          >
                            <SelectTrigger className="h-8 text-xs">
                              <SelectValue />
                            </SelectTrigger>
                            <SelectContent>
                              {STATUS_OPTIONS.map((opt) => (
                                <SelectItem key={opt.value} value={opt.value}>
                                  {opt.label}
                                </SelectItem>
                              ))}
                            </SelectContent>
                          </Select>
                        </div>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>
          )}

          {error && <p className="text-sm text-red-500">{error}</p>}

          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
              disabled={loading}
            >
              Cancel
            </Button>
            <Button type="submit" disabled={loading}>
              {loading && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
              Add Flight
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
