import { api } from "./api";
import type { User } from "@/types/user";

export async function getUserById(id: number): Promise<User> {
  const { data } = await api.get<User>(`/user/${id}`);
  return data;
}

export async function getUserByEmail(email: string): Promise<User> {
  const { data } = await api.get<User>("/user", { params: { email } });
  return data;
}
