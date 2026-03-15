import { api } from "./api";

export interface PlaceSuggestion {
  name: string;
  description: string;
  lat: number;
  lon: number;
  rating: number | null;
  openingTime: string | null;
  closingTime: string | null;
}

export async function suggestPlace(query: string): Promise<PlaceSuggestion> {
  const res = await api.post("/api/places/suggest", { query });
  return res.data;
}
