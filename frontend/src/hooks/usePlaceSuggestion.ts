import { useState, useEffect, useRef } from "react";
import { suggestPlace, type PlaceSuggestion } from "@/services/PlaceService";

interface UsePlaceSuggestionResult {
  suggestion: PlaceSuggestion | null;
  loading:    boolean;
  error:      string | null;
}

export function usePlaceSuggestion(query: string, debounceMs = 600): UsePlaceSuggestionResult {
  const [suggestion, setSuggestion] = useState<PlaceSuggestion | null>(null);
  const [loading,    setLoading]    = useState(false);
  const [error,      setError]      = useState<string | null>(null);
  const abortRef = useRef<AbortController | null>(null);

  useEffect(() => {
    const trimmed = query.trim();

    if (trimmed.length < 3) {
      setSuggestion(null);
      setError(null);
      return;
    }

    const timer = setTimeout(async () => {
      abortRef.current?.abort();
      abortRef.current = new AbortController();

      setLoading(true);
      setError(null);

      try {
        const result = await suggestPlace(trimmed);
        setSuggestion(result);
      } catch (e: unknown) {
        const status = (e as { response?: { status?: number } })?.response?.status;
        setError(status === 429 ? "AI quota exceeded, try again later" : "Could not resolve location");
        setSuggestion(null);
      } finally {
        setLoading(false);
      }
    }, debounceMs);

    return () => clearTimeout(timer);
  }, [query, debounceMs]);

  return { suggestion, loading, error };
}
