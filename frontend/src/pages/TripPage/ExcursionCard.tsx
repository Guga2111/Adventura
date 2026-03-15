import { Binoculars, Clock, GripVertical, Users } from "lucide-react";
import type { SyntheticListenerMap } from "@dnd-kit/core/dist/hooks/utilities";
import type { DraggableAttributes } from "@dnd-kit/core";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { cn } from "@/lib/utils";
import type { Excursion } from "@/types/trip";

function isoToTime(iso: string) {
  return (iso.split("T")[1] ?? "00:00:00").slice(0, 5);
}

function initials(name: string) {
  return name.split(" ").slice(0, 2).map((n) => n[0]).join("").toUpperCase();
}

interface ExcursionCardProps {
  excursion: Excursion;
  step: number;
  isActive: boolean;
  onStepClick: () => void;
  dragHandleListeners?: SyntheticListenerMap;
  dragHandleAttributes?: DraggableAttributes;
}

export function ExcursionCard({ excursion, step, isActive, onStepClick, dragHandleListeners, dragHandleAttributes }: ExcursionCardProps) {
  const startTime = isoToTime(excursion.startDate);
  const endTime = excursion.endDate ? isoToTime(excursion.endDate) : null;
  const displayTime = endTime ? `${startTime} - ${endTime}` : startTime;

  return (
    <div className={cn("flex gap-3 rounded-xl border border-gray-200 bg-white px-4 py-3 shadow-sm transition-all", isActive && "ring-2 ring-[#daa060]")}>
      {/* Drag handle + step badge */}
      <div className="flex shrink-0 flex-col items-center gap-1 pt-0.5">
        <button
          onClick={onStepClick}
          aria-label={`Go to step ${step}`}
          className={cn(
            "flex h-7 w-7 items-center justify-center rounded-full text-xs font-bold shadow-sm transition-all",
            isActive
              ? "bg-gradient-primary text-white ring-2 ring-[#daa060] ring-offset-2"
              : "border border-muted-foreground text-muted-foreground hover:opacity-70",
          )}
        >
          {step}
        </button>
        <button
          className="cursor-grab active:cursor-grabbing touch-none text-gray-300 hover:text-gray-500 transition-colors"
          {...dragHandleListeners}
          {...dragHandleAttributes}
        >
          <GripVertical className="h-4 w-4" />
        </button>
      </div>

      <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-gray-900">
        <Binoculars className="h-4 w-4 text-white" />
      </div>

      {/* Content */}
      <div className="flex flex-1 min-w-0 gap-3">
        <div className="flex-1 min-w-0">
          {/* Title row */}
          <div className="flex flex-wrap items-center gap-2">
            <span className="font-semibold text-sm text-gray-900">{excursion.name}</span>
            <Badge variant="outline" className="text-xs bg-gray-50">
              Passeio
            </Badge>
          </div>

          {/* Info row */}
          <div className="mt-1.5 flex flex-wrap items-center gap-x-3 gap-y-1 text-xs text-gray-500">
            <span className="flex items-center gap-1">
              <Clock className="h-3 w-3" />
              {displayTime}
            </span>
            <span className="flex items-center gap-1">
              <Users className="h-3 w-3" />
              {excursion.presence.length} indo
            </span>
            {excursion.price === 0 ? (
              <span className="font-medium text-green-600 underline">Gratuito</span>
            ) : (
              <span className="font-medium text-gray-700">€{excursion.price} /pessoa</span>
            )}
          </div>

          {/* Presence avatars */}
          {excursion.presence.length > 0 && (
            <div className="mt-2 flex -space-x-1.5">
              {excursion.presence.slice(0, 5).map((m) => (
                <Avatar key={m.id} className="h-6 w-6 border-2 border-white">
                  <AvatarFallback className="text-[9px] ">
                    {initials(m.user.name)}
                  </AvatarFallback>
                </Avatar>
              ))}
              {excursion.presence.length > 5 && (
                <div className="flex h-6 w-6 items-center justify-center rounded-full border-2 border-white bg-gray-200 text-[9px] font-medium text-gray-600">
                  +{excursion.presence.length - 5}
                </div>
              )}
            </div>
          )}
        </div>

        {/* Image — will be replaced by real Unsplash URL from backend */}
        <div className="hidden sm:block shrink-0 h-20 w-28 rounded-lg overflow-hidden">
          <img
            src="https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&q=80"
            alt={excursion.name}
            className="h-full w-full object-cover"
          />
        </div>
      </div>
    </div>
  );
}
