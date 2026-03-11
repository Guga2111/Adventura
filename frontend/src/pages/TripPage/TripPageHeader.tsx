import { Calendar, MapPin, Users } from "lucide-react";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import type { Trip, TripStatus } from "@/types/trip";
import { Badge } from "@/components/ui/badge";

const STATUS_LABELS: Record<TripStatus, string> = {
  PLANNING: "Planejando",
  CONFIRMED: "Confirmada",
  ON_GOING: "Acontecendo",
  FINISHED: "Finalizada",
  CANCELLED: "Cancelada",
};

function countryCodeToFlag(code: string): string {
  return code
    .toUpperCase()
    .split("")
    .map((char) => String.fromCodePoint(127397 + char.charCodeAt(0)))
    .join("");
}

function formatDateRange(start: string, end: string): string {
  const s = new Date(start);
  const e = new Date(end);
  const opts: Intl.DateTimeFormatOptions = { day: "numeric", month: "short", year: "numeric" };
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

interface TripPageHeaderProps {
  trip: Trip;
}

export function TripPageHeader({ trip }: TripPageHeaderProps) {
  const members = trip.group.members ?? [];
  const visibleMembers = members.slice(0, 4);
  const extraCount = members.length - visibleMembers.length;

  return (
    <div className="relative h-48 md:h-64 overflow-hidden">
      {/* Background image */}
      <img
        src={trip.coverImageUrl}
        alt={trip.destinations.join(", ")}
        className="absolute inset-0 h-full w-full object-cover"
      />

      {/* Gradient overlay — dark at bottom for text readability */}
      <div className="absolute inset-0 bg-linear-to-t from-black/75 via-black/25 to-transparent" />

      {/* Header content */}
      <div className="relative h-full flex flex-col justify-between p-6 md:p-10">

        {/* Top row: members (right-aligned) */}
        <div className="flex justify-end">
          <div className="flex items-center gap-2 text-white">
            <Users className="h-4 w-4 text-white/80" />
            <div className="flex -space-x-2">
              {visibleMembers.map((m) => (
                <Avatar key={m.id} className="h-7 w-7 border-2 border-white/40">
                  <AvatarFallback className="text-[10px] bg-white/20 text-white">
                    {getInitials(m.user.name)}
                  </AvatarFallback>
                </Avatar>
              ))}
            </div>
            {extraCount > 0 && (
              <span className="text-xs font-semibold text-white/80">+{extraCount}</span>
            )}
          </div>
        </div>

        {/* Bottom: status + period, title, destinations */}
        <div className="space-y-2">
          {/* Status badge + period */}
          <div className="flex items-center gap-3">
            <Badge variant="default" className="bg-gradient-primary">
                {STATUS_LABELS[trip.status]}
            </Badge>
            <span>
                <span className="text-xs font-semibold text-white/80 justify-between flex items-center gap-2">
                    <Calendar size={16}></Calendar>
                    {formatDateRange(trip.startDate, trip.endDate)}
                </span>
            </span>
          </div>

          {/* Title */}
          <h1 className="text-3xl md:text-4xl font-bold text-white leading-tight">
            {trip.destinations.join(", ")}
          </h1>

          {/* Destinations with flags */}
          <div className="flex flex-wrap items-center gap-x-2 gap-y-1">
            <MapPin className="h-4 w-4 text-white/80" />
            {trip.destinations.map((dest, i) => (
              <span key={dest} className="flex items-center text-sm">
                <span>{countryCodeToFlag(trip.countryCodes[i] ?? "")}</span>
              </span>
            ))}
          </div>

          {/* Unsplash attribution */}
          {trip.coverImageAuthor && (
            <a
              href={trip.coverImageAuthorUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="block text-[10px] text-white/35 hover:text-white/60 transition-colors"
            >
              Photo by {trip.coverImageAuthor} on Unsplash
            </a>
          )}
        </div>
      </div>
    </div>
  );
}
