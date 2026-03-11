import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue,
} from "@/components/ui/select";
import type { PassengerForm } from "./useFlightPassengers";
import type { SeatClass, FlightPassengerStatus } from "@/types/flight";

const SEAT_CLASS_LABEL: Record<SeatClass, string> = {
  ECONOMY: "Economy",
  PREMIUM_ECONOMY: "Premium Economy",
  BUSINESS: "Business",
  FIRST: "First",
};

const SEAT_CLASS_OPTIONS: SeatClass[] = ["ECONOMY", "PREMIUM_ECONOMY", "BUSINESS", "FIRST"];
const STATUS_OPTIONS: FlightPassengerStatus[] = ["UNDECIDED", "CONFIRMED", "CANCELLED"];

interface PassengerFormFieldsProps {
  form: PassengerForm;
  setForm: React.Dispatch<React.SetStateAction<PassengerForm>>;
}

export function PassengerFormFields({ form, setForm }: PassengerFormFieldsProps) {
  return (
    <div className="grid grid-cols-2 gap-3">
      <div className="space-y-1.5">
        <Label htmlFor="seat">Seat</Label>
        <Input id="seat" placeholder="14A" maxLength={6} value={form.seatNumber}
          onChange={(e) => setForm((f) => ({ ...f, seatNumber: e.target.value }))} />
      </div>
      <div className="space-y-1.5">
        <Label htmlFor="price">Price (€)</Label>
        <Input id="price" type="number" placeholder="850" value={form.price}
          onChange={(e) => setForm((f) => ({ ...f, price: e.target.value }))} />
      </div>
      <div className="space-y-1.5">
        <Label>Class</Label>
        <Select value={form.seatClass} onValueChange={(v) => setForm((f) => ({ ...f, seatClass: v as SeatClass }))}>
          <SelectTrigger><SelectValue placeholder="Select class" /></SelectTrigger>
          <SelectContent>
            {SEAT_CLASS_OPTIONS.map((c) => (
              <SelectItem key={c} value={c}>{SEAT_CLASS_LABEL[c]}</SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>
      <div className="space-y-1.5">
        <Label>Status</Label>
        <Select value={form.status} onValueChange={(v) => setForm((f) => ({ ...f, status: v as FlightPassengerStatus }))}>
          <SelectTrigger><SelectValue /></SelectTrigger>
          <SelectContent>
            {STATUS_OPTIONS.map((s) => (
              <SelectItem key={s} value={s}>{s.charAt(0) + s.slice(1).toLowerCase()}</SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>
    </div>
  );
}
