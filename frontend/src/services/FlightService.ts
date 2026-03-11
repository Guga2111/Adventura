import { api } from "./api";
import type { Flight, FlightDTO, FlightPassenger, FlightPassengerDTO } from "@/types/flight";

export async function getFlights(tripId: number): Promise<Flight[]> {
  const { data } = await api.get<Flight[]>(`/trip/${tripId}/flights`);
  return data;
}

export async function createFlight(tripId: number, dto: FlightDTO): Promise<Flight> {
  const { data } = await api.post<Flight>(`/trip/${tripId}/flights`, dto);
  return data;
}

export async function updateFlight(tripId: number, flightId: number, dto: FlightDTO): Promise<Flight> {
  const { data } = await api.put<Flight>(`/trip/${tripId}/flights/${flightId}`, dto);
  return data;
}

export async function deleteFlight(tripId: number, flightId: number): Promise<void> {
  await api.delete(`/trip/${tripId}/flights/${flightId}`);
}

export async function joinFlight(tripId: number, flightId: number, dto: FlightPassengerDTO): Promise<FlightPassenger> {
  const { data } = await api.post<FlightPassenger>(`/trip/${tripId}/flights/${flightId}/passengers`, dto);
  return data;
}

export async function updatePassenger(tripId: number, flightId: number, passengerId: number, dto: FlightPassengerDTO): Promise<FlightPassenger> {
  const { data } = await api.put<FlightPassenger>(`/trip/${tripId}/flights/${flightId}/passengers/${passengerId}`, dto);
  return data;
}

export async function removePassenger(tripId: number, flightId: number, passengerId: number): Promise<void> {
  await api.delete(`/trip/${tripId}/flights/${flightId}/passengers/${passengerId}`);
}
