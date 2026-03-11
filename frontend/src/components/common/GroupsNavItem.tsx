import { useState, useEffect } from "react";
import { Link, useLocation } from "react-router-dom";
import { Users, ChevronRight } from "lucide-react";
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from "@radix-ui/react-collapsible";
import {
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarMenuSub,
  SidebarMenuSubButton,
  SidebarMenuSubItem,
} from "@/components/ui/sidebar";
import { useAuth } from "@/context/AuthContext";
import { getGroupsByUser } from "@/services/GroupService";
import type { Group } from "@/types/group";

export function GroupsNavItem() {
  const { user } = useAuth();
  const location = useLocation();
  const [groups, setGroups] = useState<Group[]>([]);
  const isOnGroups = location.pathname.startsWith("/groups");
  const [open, setOpen] = useState(isOnGroups);

  useEffect(() => {
    if (!user) return;
    getGroupsByUser(user.userId).then(setGroups).catch(() => {});
  }, [user]);

  return (
    <SidebarMenuItem>
      <Collapsible open={open} onOpenChange={setOpen} className="group/collapsible">
        <CollapsibleTrigger asChild>
          <SidebarMenuButton
            tooltip="Grupos"
            isActive={isOnGroups}
            className="data-[active=true]:bg-gradient-primary! data-[active=true]:text-white!"
          >
            <Users />
            <span>Grupos</span>
            <ChevronRight className="ml-auto h-4 w-4 shrink-0 text-muted-foreground transition-transform duration-200 group-data-[state=open]/collapsible:rotate-90 group-data-[collapsible=icon]:hidden" />
          </SidebarMenuButton>
        </CollapsibleTrigger>
        <CollapsibleContent>
          <SidebarMenuSub>
            {groups.map((group) => (
              <SidebarMenuSubItem key={group.id}>
                <SidebarMenuSubButton
                  asChild
                  isActive={location.pathname === `/groups/${group.id}`}
                >
                  <Link to={`/groups/${group.id}`}>{group.name}</Link>
                </SidebarMenuSubButton>
              </SidebarMenuSubItem>
            ))}
          </SidebarMenuSub>
        </CollapsibleContent>
      </Collapsible>
    </SidebarMenuItem>
  );
}
