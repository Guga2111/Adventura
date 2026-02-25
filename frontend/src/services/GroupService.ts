import { api } from "./api";
import type { Group, GroupMember, GroupMemberRole, CreateGroupRequest } from "@/types/group";

export async function getAllGroups(): Promise<Group[]> {
  const { data } = await api.get<Group[]>("/group");
  return data;
}

export async function getGroupById(id: number): Promise<Group> {
  const { data } = await api.get<Group>(`/group/${id}`);
  return data;
}

export async function getGroupsByUser(userId: number): Promise<Group[]> {
  const { data } = await api.get<Group[]>(`/group/user/${userId}`);
  return data;
}

export async function createGroup(userId: number, groupData: CreateGroupRequest): Promise<Group> {
  const { data } = await api.post<Group>(`/group/user/${userId}`, groupData);
  return data;
}

export async function updateGroup(
  groupId: number,
  userId: number,
  groupData: CreateGroupRequest
): Promise<Group> {
  const { data } = await api.put<Group>(`/group/${groupId}/user/${userId}`, groupData);
  return data;
}

export async function deleteGroup(groupId: number, userId: number): Promise<void> {
  await api.delete(`/group/${groupId}/user/${userId}`);
}

export async function getGroupMembers(groupId: number): Promise<GroupMember[]> {
  const { data } = await api.get<GroupMember[]>(`/group/${groupId}/members`);
  return data;
}

export async function addUserToGroup(
  groupId: number,
  userId: number,
  requestedByUserId: number,
  groupRole: GroupMemberRole
): Promise<GroupMember> {
  const { data } = await api.post<GroupMember>(
    `/group/${groupId}/user/${userId}/requested-by/${requestedByUserId}`,
    null,
    { params: { groupRole } }
  );
  return data;
}

export async function removeUserFromGroup(
  groupId: number,
  targetUserId: number,
  requestedByUserId: number
): Promise<void> {
  await api.delete(
    `/group/${groupId}/member/${targetUserId}/requested-by/${requestedByUserId}`
  );
}
