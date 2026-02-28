import { api } from "./api";
import type { Trip, TripDTO } from "@/types/trip";

export async function getAllTrips(): Promise<Trip[]> {
  const { data } = await api.get<Trip[]>("/trip");
  return data;
}

export async function getTripById(id: number): Promise<Trip> {
  const { data } = await api.get<Trip>(`/trip/${id}`);
  return data;
}

export async function getTripsByGroup(groupId: number): Promise<Trip[]> {
  const { data } = await api.get<Trip[]>(`/trip/group/${groupId}`);
  return data;
}

export async function getTripsByUser(userId: number): Promise<Trip[]> {
  const { data } = await api.get<Trip[]>(`/trip/user/${userId}`);
  return data;
}

export async function createTrip(
  groupId: number,
  userId: number,
  tripData: TripDTO
): Promise<Trip> {
  const { data } = await api.post<Trip>(`/trip/group/${groupId}/user/${userId}`, tripData);
  return data;
}

export async function updateTrip(
  tripId: number,
  userId: number,
  tripData: TripDTO
): Promise<Trip> {
  const { data } = await api.put<Trip>(`/trip/${tripId}/user/${userId}`, tripData);
  return data;
}

export async function deleteTrip(tripId: number, userId: number): Promise<void> {
  await api.delete(`/trip/${tripId}/user/${userId}`);
}
