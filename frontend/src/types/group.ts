import type { User } from "./user";

export type GroupMemberRole = "ADMIN" | "MEMBER";

export interface GroupMember {
  id: number;
  group?: Group; // absent when serialized inside Group.members (@JsonBackReference)
  user: User;
  role: GroupMemberRole;
  enteredWhen: string;
}

export interface Group {
  id: number;
  name: string;
  description: string;
  createdBy: User;
  members: GroupMember[];
  createdAt: string;
  updatedAt: string;
}

export interface CreateGroupRequest {
  name: string;
  description: string;
}
