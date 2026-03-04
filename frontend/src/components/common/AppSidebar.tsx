import { useState, useRef, useEffect } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { House, Users, Compass, CircleUser, CreditCard, Bell, LogOut } from "lucide-react";
import AdventuraIcon from "@/components/common/AdventuraIcon";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  useSidebar,
} from "@/components/ui/sidebar";
import { useAuth } from "@/context/AuthContext";

const navItems = [
  { icon: House,   label: "Início",   href: "/home" },
  { icon: Users,   label: "Grupos",   href: "/groups" },
  { icon: Compass, label: "Explorar", href: "/explore" },
];

function getInitials(name: string): string {
  return name.split(" ").map((n) => n[0]).join("").toUpperCase().slice(0, 2);
}

export function AppSidebar() {
  const { setOpen } = useSidebar();
  const { user, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  const [userMenuOpen, setUserMenuOpen] = useState(false);
  const menuRef = useRef<HTMLDivElement>(null);

  const initials = user ? getInitials(user.username) : "";

  useEffect(() => {
    if (!userMenuOpen) return;
    function handler(e: MouseEvent) {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
        setUserMenuOpen(false);
      }
    }
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, [userMenuOpen]);

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  return (
    <Sidebar
      collapsible="icon"
      onMouseEnter={() => setOpen(true)}
      onMouseLeave={() => { if (!userMenuOpen) setOpen(false); }}
    >
      {/* Top: Adventura logo */}
      <SidebarHeader className="group-data-[collapsible=icon]:p-0">
        <Link to="/home" className="flex h-12 items-center gap-3 px-2 overflow-hidden group-data-[collapsible=icon]:justify-center group-data-[collapsible=icon]:px-0">
          <AdventuraIcon size={30} className="text-[#dda600] shrink-0" />
          <span className="font-bold text-gradient-primary text-base whitespace-nowrap group-data-[collapsible=icon]:opacity-0 group-data-[collapsible=icon]:w-0 transition-all duration-200 overflow-hidden">
            Adventura
          </span>
        </Link>
      </SidebarHeader>

      {/* Middle: Nav items */}
      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupContent>
            <SidebarMenu>
              {navItems.map(({ icon: Icon, label, href }) => (
                <SidebarMenuItem key={href}>
                  <SidebarMenuButton
                    asChild
                    isActive={location.pathname === href}
                    tooltip={label}
                  >
                    <Link to={href}>
                      <Icon />
                      <span>{label}</span>
                    </Link>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>

      {/* Bottom: User */}
      <SidebarFooter>
        <SidebarMenu>
          <SidebarMenuItem>
            <div className="relative" ref={menuRef}>
              <SidebarMenuButton
                size="lg"
                tooltip={user?.username ?? ""}
                onClick={() => setUserMenuOpen((prev) => !prev)}
                className="cursor-pointer group-data-[collapsible=icon]:justify-center"
              >
                <Avatar className="h-6 w-6 shrink-0">
                  <AvatarFallback className="text-[10px]">{initials}</AvatarFallback>
                </Avatar>
                <div className="flex flex-col min-w-0 text-left">
                  <span className="text-sm font-medium truncate leading-tight">{user?.username}</span>
                  <span className="text-xs text-muted-foreground truncate leading-tight">{user?.email}</span>
                </div>
              </SidebarMenuButton>

              {/* User popup — opens above the footer button */}
              {userMenuOpen && (
                <div className="absolute bottom-full left-0 mb-1 min-w-56 w-full rounded-lg border bg-popover shadow-lg text-popover-foreground py-1 z-50">
                  {/* User info header */}
                  <div className="flex items-center gap-2 px-3 py-2">
                    <Avatar className="h-8 w-8 shrink-0">
                      <AvatarFallback className="text-xs">{initials}</AvatarFallback>
                    </Avatar>
                    <div className="min-w-0">
                      <p className="text-sm font-medium truncate">{user?.username}</p>
                      <p className="text-xs text-muted-foreground truncate">{user?.email}</p>
                    </div>
                  </div>

                  <div className="h-px bg-border mx-1 my-1" />

                  <button className="flex w-full items-center gap-2 px-3 py-1.5 text-sm hover:bg-accent rounded-sm transition-colors">
                    <CircleUser className="h-4 w-4 shrink-0" />
                    <span>Account</span>
                  </button>
                  <button className="flex w-full items-center gap-2 px-3 py-1.5 text-sm hover:bg-accent rounded-sm transition-colors">
                    <CreditCard className="h-4 w-4 shrink-0" />
                    <span>Billing</span>
                  </button>
                  <button className="flex w-full items-center gap-2 px-3 py-1.5 text-sm hover:bg-accent rounded-sm transition-colors">
                    <Bell className="h-4 w-4 shrink-0" />
                    <span>Notifications</span>
                  </button>

                  <div className="h-px bg-border mx-1 my-1" />

                  <button
                    onClick={handleLogout}
                    className="flex w-full items-center gap-2 px-3 py-1.5 text-sm hover:bg-accent rounded-sm transition-colors text-destructive"
                  >
                    <LogOut className="h-4 w-4 shrink-0" />
                    <span>Log out</span>
                  </button>
                </div>
              )}
            </div>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarFooter>
    </Sidebar>
  );
}
