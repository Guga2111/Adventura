import { Loader2, Pencil, Trash2 } from "lucide-react";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import type { FlightPassenger, SeatClass, FlightPassengerStatus } from "@/types/flight";

function initials(name: string) {
  return name.split(" ").slice(0, 2).map((n) => n[0]).join("").toUpperCase();
}

const SEAT_CLASS_LABEL: Record<SeatClass, string> = {
  ECONOMY: "Economy",
  PREMIUM_ECONOMY: "Premium Economy",
  BUSINESS: "Business",
  FIRST: "First",
};

const STATUS_STYLES: Record<FlightPassengerStatus, string> = {
  CONFIRMED: "bg-emerald-100 text-emerald-700",
  CANCELLED:  "bg-red-100 text-red-600",
  UNDECIDED:  "bg-amber-100 text-amber-700",
};

interface PassengerTableProps {
  passengers: FlightPassenger[];
  currentUserId: number | null;
  removingId: number | null;
  onEdit: (passenger: FlightPassenger) => void;
  onRemove: (passengerId: number) => void;
}

export function PassengerTable({ passengers, currentUserId, removingId, onEdit, onRemove }: PassengerTableProps) {
  return (
    <div className="overflow-x-auto">
      <table className="w-full text-xs">
        <thead>
          <tr className="text-gray-400 uppercase tracking-wide">
            <th className="pb-1.5 text-left font-medium">Passenger</th>
            <th className="pb-1.5 text-left font-medium">Seat</th>
            <th className="pb-1.5 text-left font-medium">Class</th>
            <th className="pb-1.5 text-right font-medium">Price</th>
            <th className="pb-1.5 text-right font-medium">Status</th>
            <th className="pb-1.5 w-14" />
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-100">
          {passengers.map((p) => {
            const isMe = p.groupMember.user.id === currentUserId;
            const removing = removingId === p.id;
            return (
              <tr key={p.id} className={isMe ? "bg-amber-50/60" : ""}>
                <td className="py-2">
                  <div className="flex items-center gap-2">
                    <Avatar className="h-6 w-6">
                      <AvatarFallback className="text-[9px] bg-gray-200 text-gray-700">
                        {initials(p.groupMember.user.name)}
                      </AvatarFallback>
                    </Avatar>
                    <span className={`font-medium ${isMe ? "text-amber-700" : "text-gray-800"}`}>
                      {p.groupMember.user.name}
                      {isMe && <span className="ml-1 text-[10px] font-normal text-amber-500">(you)</span>}
                    </span>
                  </div>
                </td>
                <td className="py-2 font-mono text-gray-700">{p.seatNumber || "—"}</td>
                <td className="py-2 text-gray-600">{p.seatClass ? SEAT_CLASS_LABEL[p.seatClass] : "—"}</td>
                <td className="py-2 text-right font-medium text-gray-800">
                  {isMe ? `€${p.price}` : <span className="text-gray-300">—</span>}
                </td>
                <td className="py-2 text-right">
                  <span className={`inline-block rounded-full px-2 py-0.5 text-[10px] font-semibold ${STATUS_STYLES[p.status]}`}>
                    {p.status.charAt(0) + p.status.slice(1).toLowerCase()}
                  </span>
                </td>
                <td className="py-2 text-right">
                  {isMe && (
                    <div className="flex items-center justify-end gap-1">
                      <button
                        onClick={() => onEdit(p)}
                        className="rounded p-1 text-gray-400 hover:text-amber-600 hover:bg-amber-50 transition-colors"
                        title="Edit my details"
                      >
                        <Pencil className="h-3.5 w-3.5" />
                      </button>
                      <button
                        onClick={() => onRemove(p.id)}
                        disabled={removing}
                        className="rounded p-1 text-gray-400 hover:text-red-500 hover:bg-red-50 transition-colors disabled:opacity-50"
                        title="Leave flight"
                      >
                        {removing
                          ? <Loader2 className="h-3.5 w-3.5 animate-spin" />
                          : <Trash2 className="h-3.5 w-3.5" />}
                      </button>
                    </div>
                  )}
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
