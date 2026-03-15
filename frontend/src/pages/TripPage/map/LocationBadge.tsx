import { Loader2, MapPin, Star, Clock } from "lucide-react";
import type { PlaceSuggestion } from "@/services/PlaceService";

interface LocationBadgeProps {
  loading:    boolean;
  error:      string | null;
  suggestion: PlaceSuggestion | null;
}

export function LocationBadge({ loading, error, suggestion }: LocationBadgeProps) {
  if (loading) {
    return (
      <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
        <Loader2 className="h-3 w-3 animate-spin" />
        Finding location…
      </div>
    );
  }
  if (error) {
    return <p className="text-xs text-destructive">{error}</p>;
  }
  if (!suggestion) return null;

  return (
    <div className="rounded-lg border bg-muted/50 p-3 space-y-1.5 text-xs">
      <div className="flex items-start gap-1.5">
        <MapPin className="h-3.5 w-3.5 mt-0.5 text-emerald-500 shrink-0" />
        <span className="font-medium">{suggestion.name}</span>
      </div>
      {suggestion.rating && (
        <div className="flex items-center gap-1 text-muted-foreground">
          <Star className="h-3 w-3 fill-amber-400 text-amber-400" />
          {suggestion.rating.toFixed(1)}
        </div>
      )}
      {(suggestion.openingTime || suggestion.closingTime) && (
        <div className="flex items-center gap-1 text-muted-foreground">
          <Clock className="h-3 w-3" />
          {suggestion.openingTime ?? "?"} – {suggestion.closingTime ?? "?"}
        </div>
      )}
      <p className="text-muted-foreground leading-relaxed">{suggestion.description}</p>
    </div>
  );
}
