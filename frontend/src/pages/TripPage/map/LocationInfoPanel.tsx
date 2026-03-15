import { useEffect, useRef, useState } from "react";
import { X, Star, Clock, MapPin, Plane } from "lucide-react";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import type { TripEvent } from "@/pages/TripPage/tripUtils";
import { getAirportCoord } from "@/lib/airportUtils";
import { suggestPlace, type PlaceSuggestion } from "@/services/PlaceService";

interface LocationInfoPanelProps {
  event:    TripEvent | undefined;
  onClose?: () => void;
}

type PanelData =
  | { kind: "flight";    origin: string; destination: string; airline?: string; flightNumber?: string }
  | { kind: "excursion"; suggestion: PlaceSuggestion };

export function LocationInfoPanel({ event, onClose }: LocationInfoPanelProps) {
  const [data,    setData]    = useState<PanelData | null>(null);
  const [loading, setLoading] = useState(false);
  const [visible, setVisible] = useState(false);
  const cache = useRef<Map<string, PlaceSuggestion>>(new Map());

  useEffect(() => {
    if (!event) {
      setVisible(false);
      return;
    }

    setVisible(true);

    if (event.kind === "flight") {
      setData({
        kind:         "flight",
        origin:       event.data.originAirport,
        destination:  event.data.destinationAirport,
        airline:      event.data.airline ?? undefined,
        flightNumber: event.data.flightNumber ?? undefined,
      });
      return;
    }

    const exc = event.data;

    if (exc.locationName && exc.latitude != null) {
      const cacheKey = exc.locationName;
      if (cache.current.has(cacheKey)) {
        setData({ kind: "excursion", suggestion: cache.current.get(cacheKey)! });
        return;
      }

      setLoading(true);
      suggestPlace(exc.locationName)
        .then((s) => {
          cache.current.set(cacheKey, s);
          setData({ kind: "excursion", suggestion: s });
        })
        .catch(() =>
          setData({
            kind: "excursion",
            suggestion: {
              name:        exc.locationName ?? event.data.name,
              description: "",
              lat:         exc.latitude ?? 0,
              lon:         exc.longitude ?? 0,
              rating:      null,
              openingTime: null,
              closingTime: null,
            },
          })
        )
        .finally(() => setLoading(false));
    } else {
      setData({
        kind: "excursion",
        suggestion: {
          name:        event.data.name,
          description: "No location set for this excursion.",
          lat:         0,
          lon:         0,
          rating:      null,
          openingTime: null,
          closingTime: null,
        },
      });
    }
  }, [event?.sortableId]);

  if (!event) return null;

  return (
    <div
      className={cn(
        "absolute bottom-4 right-4 z-20 w-72 rounded-xl border bg-background/95 shadow-lg backdrop-blur-sm",
        "transition-all duration-300",
        visible ? "translate-y-0 opacity-100" : "translate-y-4 opacity-0 pointer-events-none"
      )}
    >
      {/* Header */}
      <div className="flex items-start justify-between p-4 pb-2">
        <div className="flex items-center gap-2">
          {data?.kind === "flight" ? (
            <Plane className="h-4 w-4 text-amber-500 shrink-0" />
          ) : (
            <MapPin className="h-4 w-4 text-emerald-500 shrink-0" />
          )}
          <span className="text-sm font-semibold leading-tight line-clamp-2">
            {data?.kind === "flight"
              ? `${data.origin} → ${data.destination}`
              : data?.suggestion.name ?? event.data.name}
          </span>
        </div>
        {onClose && (
          <Button
            size="icon"
            variant="ghost"
            className="h-6 w-6 shrink-0 -mr-1 -mt-1"
            onClick={onClose}
          >
            <X className="h-3.5 w-3.5" />
          </Button>
        )}
      </div>

      {/* Content */}
      <div className="px-4 pb-4 space-y-2 text-xs text-muted-foreground">
        {loading && <p>Loading details…</p>}

        {data?.kind === "flight" && (() => {
          const orig = getAirportCoord(data.origin);
          const dest = getAirportCoord(data.destination);
          return (
            <>
              {data.airline && (
                <p>{data.airline}{data.flightNumber ? ` · ${data.flightNumber}` : ""}</p>
              )}
              {orig && <p>{orig.name} ({data.origin})</p>}
              {dest && <p>{dest.name} ({data.destination})</p>}
            </>
          );
        })()}

        {data?.kind === "excursion" && !loading && (
          <>
            {data.suggestion.rating != null && (
              <div className="flex items-center gap-1">
                <Star className="h-3 w-3 fill-amber-400 text-amber-400" />
                <span>{data.suggestion.rating.toFixed(1)}</span>
              </div>
            )}
            {(data.suggestion.openingTime || data.suggestion.closingTime) && (
              <div className="flex items-center gap-1">
                <Clock className="h-3 w-3" />
                <span>
                  {data.suggestion.openingTime ?? "?"} – {data.suggestion.closingTime ?? "?"}
                </span>
              </div>
            )}
            {data.suggestion.description && (
              <p className="leading-relaxed">{data.suggestion.description}</p>
            )}
          </>
        )}
      </div>
    </div>
  );
}
