import { Share2, MoreHorizontal } from "lucide-react"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import type { Trip } from "src/types/trip"

function formatDateRange(start: string, end: string): string {
  const s = new Date(start);
  const e = new Date(end);
  const opts: Intl.DateTimeFormatOptions = { day: "numeric", month: "short" };
  return `${s.toLocaleDateString("pt-PT", opts)} – ${e.toLocaleDateString("pt-PT", opts)}`;
}

function getInitials(name: string): string {
  return name
    .split(" ")
    .map((n) => n[0])
    .join("")
    .toUpperCase()
    .slice(0, 2);
}

interface TripCardProps {
  trip: Trip;
}

export function TripCard({ trip }: TripCardProps) {
  return (
    <div className="group cursor-pointer">
      <div className="relative aspect-[3/2] overflow-hidden rounded-xl bg-muted">
        <img
          src={trip.coverImageUrl}
          alt={trip.name}
          loading="lazy"
          className="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105"
        />
        <div className="absolute top-3 right-3 flex gap-2 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
          <button
            type="button"
            className="flex h-8 items-center gap-1.5 rounded-full bg-white/80 backdrop-blur-sm px-3 shadow-sm hover:bg-white transition-colors"
          >
            <Share2 className="h-3.5 w-3.5 text-gray-700" />
            <span className="text-xs font-medium text-gray-700">Share</span>
          </button>
          <button
            type="button"
            className="flex h-8 w-8 items-center justify-center rounded-full bg-white/80 backdrop-blur-sm shadow-sm hover:bg-white transition-colors"
          >
            <MoreHorizontal className="h-4 w-4 text-gray-700" />
          </button>
        </div>
      </div>

      <div className="pt-3">
        <h3 className="text-sm font-semibold text-foreground">{trip.name}</h3>
        <div className="mt-1.5 flex items-center gap-2 text-xs text-muted-foreground">
          <Avatar size="sm" className="h-5 w-5">
            {trip.owner.avatarUrl && <AvatarImage src={trip.owner.avatarUrl} alt={trip.owner.name} />}
            <AvatarFallback className="text-[10px]">{getInitials(trip.owner.name)}</AvatarFallback>
          </Avatar>
          <span>{formatDateRange(trip.startDate, trip.endDate)}</span>
          <span className="h-1 w-1 rounded-full bg-muted-foreground/50" />
          <span>{trip.placesCount} lugares</span>
        </div>
      </div>
    </div>
  );
}
