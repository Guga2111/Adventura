import type { Group } from "./group";
import type { GroupMember } from "./group";

export type TripStatus = "PLANNING" | "CONFIRMED" | "ON_GOING" | "FINISHED" | "CANCELLED";

export type ExcursionAction = "ADD" | "UPDATE" | "DELETE";

export interface Excursion {
  id: number;
  name: string;
  price: number;
  presence: GroupMember[];
  startDate: string;
  endDate: string;
  trip?: Trip; // absent when serialized inside Trip.excursions (@JsonBackReference)
  createdAt: string;
  updatedAt: string;
}

export interface Trip {
  id: number;
  group: Group; // group.members omitted by @JsonIgnoreProperties
  destinations: string[];
  countryCodes: string[];
  description: string;
  excursions: Excursion[];
  excursionCount: number;
  startDate: string; 
  endDate: string;
  mainCurrency: string;
  totalBudget: number;
  status: TripStatus;
  coverImageUrl: string;
  coverImageAuthor: string;
  coverImageAuthorUrl: string;
  createdAt: string;
  updatedAt: string;
}

export interface TripDTO {
  destinations: string[];
  description: string;
  startDate: string;
  endDate: string;
  mainCurrency: string;
  totalBudget: number;
  status: TripStatus;
}

export interface ExcursionMessage {
  action: ExcursionAction;
  excursionId: number;
  name: string;
  price: number;
  startDate: string;
  endDate: string;
  presenceIds: number[];
  updatedBy: string;
}
