export type SeatClass = "ECONOMY" | "PREMIUM_ECONOMY" | "BUSINESS" | "FIRST";
export type FlightPassengerStatus = "UNDECIDED" | "CONFIRMED" | "CANCELLED";

export interface FlightPassengerDTO {
  groupMemberId: number;
  seatNumber?: string;
  seatClass?: SeatClass;
  price?: number;
  status: FlightPassengerStatus;
}

export interface FlightDTO {
  flightNumber?: string;
  airline?: string;
  originAirport: string;       // IATA 3-letter code
  destinationAirport: string;
  departureLocalTime: string;  // ISO datetime: "2026-04-15T08:00:00"
  departureTimezone: string;
  arrivalLocalTime: string;
  arrivalTimezone: string;
  bookingReference?: string;
  notes?: string;
  passengers: FlightPassengerDTO[];
}

export interface FlightPassenger {
  id: number;
  groupMember: {
    id: number;
    user: { id: number; name: string; email: string };
  };
  seatNumber?: string;
  seatClass?: SeatClass;
  price?: number;
  status: FlightPassengerStatus;
}

export interface Flight {
  id: number;
  flightNumber?: string;
  airline?: string;
  originAirport: string;
  destinationAirport: string;
  departureLocalTime: string;
  departureTimezone: string;
  arrivalLocalTime: string;
  arrivalTimezone: string;
  bookingReference?: string;
  notes?: string;
  passengers: FlightPassenger[];
  createdAt: string;
  updatedAt?: string;
}
