import { useState, useRef, useEffect } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import {
  House, Users, Compass, Bell,
  CircleUser, CreditCard, LogOut,
  ChevronsUpDown, Wallet, MapPin,
} from "lucide-react";
import AdventuraIcon from "@/components/common/AdventuraIcon";
import { GroupsNavItem } from "@/components/common/GroupsNavItem";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  useSidebar,
} from "@/components/ui/sidebar";
import { useAuth } from "@/context/AuthContext";
import { getTripsByUser } from "@/services/TripService";
import type { Trip } from "@/types/trip";

// ─── helpers ──────────────────────────────────────────────────────────────────

function getInitials(name: string): string {
  return name.split(" ").map((n) => n[0]).join("").toUpperCase().slice(0, 2);
}

function countryCodeToFlag(code: string): string {
  return code
    .toUpperCase()
    .split("")
    .map((c) => String.fromCodePoint(127397 + c.charCodeAt(0)))
    .join("");
}

// ─── nav config ───────────────────────────────────────────────────────────────

interface NavItem {
  icon: React.ElementType;
  label: string;
  href: string;
  badge?: number;
}


const tripNavItems: NavItem[] = [
  { icon: MapPin, label: "Excursões", href: "#excursions" },
  { icon: Wallet, label: "Budget",    href: "#budget" },
  { icon: Users,  label: "Membros",   href: "#members" },
];

// ─── TripSwitcher ─────────────────────────────────────────────────────────────

function TripSwitcher() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [trips, setTrips] = useState<Trip[]>([]);
  const [open, setOpen] = useState(false);
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!user) return;
    getTripsByUser(user.userId).then(setTrips).catch(() => {});
  }, [user]);

  useEffect(() => {
    if (!open) return;
    function handler(e: MouseEvent) {
      if (ref.current && !ref.current.contains(e.target as Node)) setOpen(false);
    }
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, [open]);

  const tripIdFromUrl = location.pathname.match(/^\/trip\/(\d+)/)?.[1];
  const currentTripId = tripIdFromUrl ? Number(tripIdFromUrl) : null;
  const current = trips.find((t) => t.id === currentTripId) ?? trips[0];

  return (
    <div className="relative px-2 py-1" ref={ref}>
      <SidebarMenuButton
        size="lg"
        tooltip="Trocar viagem"
        onClick={() => setOpen((p) => !p)}
        className="cursor-pointer group-data-[collapsible=icon]:justify-center"
      >
        <ChevronsUpDown className="h-4 w-4 shrink-0 text-muted-foreground" />
        <div className="flex flex-col min-w-0 text-left group-data-[collapsible=icon]:hidden">
          <span className="text-[10px] text-muted-foreground uppercase tracking-wide leading-none mb-0.5">
            Viagem atual
          </span>
          <span className="text-sm font-semibold truncate leading-tight">
            {current ? current.destinations.join(", ") : "Nenhuma viagem"}
          </span>
        </div>
      </SidebarMenuButton>

      {open && trips.length > 0 && (
        <div className="absolute top-full left-0 mt-1 min-w-56 w-full rounded-lg border bg-popover shadow-lg text-popover-foreground py-1 z-50">
          <p className="px-3 py-1.5 text-[10px] text-muted-foreground uppercase tracking-wide">
            Todas as viagens
          </p>
          {trips.map((trip) => (
            <button
              key={trip.id}
              onClick={() => { navigate(`/trip/${trip.id}`); setOpen(false); }}
              className={`flex w-full items-center gap-2 px-3 py-2 text-sm hover:bg-accent rounded-sm transition-colors ${trip.id === currentTripId ? "bg-accent" : ""}`}
            >
              <span className="text-base leading-none">
                {countryCodeToFlag(trip.countryCodes[0] ?? "")}
              </span>
              <div className="flex flex-col min-w-0 text-left">
                <span className="font-medium truncate">{trip.destinations.join(", ")}</span>
                <span className="text-xs text-muted-foreground truncate">{trip.group.name}</span>
              </div>
            </button>
          ))}
        </div>
      )}
    </div>
  );
}

// ─── AppSidebar ───────────────────────────────────────────────────────────────

export function AppSidebar() {
  const { setOpen } = useSidebar();
  const { user, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  const [userMenuOpen, setUserMenuOpen] = useState(false);
  const menuRef = useRef<HTMLDivElement>(null);

  const initials = user ? getInitials(user.username) : "";
  const isOnTrip = location.pathname.startsWith("/trip");

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

      <SidebarContent>
        {/* Trip switcher */}
        <SidebarGroup className="p-0">
          <SidebarGroupContent>
            <TripSwitcher />
          </SidebarGroupContent>
        </SidebarGroup>

        {/* Main nav */}
        <SidebarGroup>
          <SidebarGroupLabel>Plataforma</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              <SidebarMenuItem>
                <SidebarMenuButton
                  asChild
                  isActive={location.pathname === "/home"}
                  tooltip="Início"
                  className="data-[active=true]:bg-gradient-primary! data-[active=true]:text-white!"
                >
                  <Link to="/home">
                    <House />
                    <span>Início</span>
                  </Link>
                </SidebarMenuButton>
              </SidebarMenuItem>
              <GroupsNavItem />
              <SidebarMenuItem>
                <SidebarMenuButton
                  asChild
                  isActive={location.pathname === "/explore"}
                  tooltip="Explorar"
                  className="data-[active=true]:bg-gradient-primary! data-[active=true]:text-white!"
                >
                  <Link to="/explore">
                    <Compass />
                    <span>Explorar</span>
                  </Link>
                </SidebarMenuButton>
              </SidebarMenuItem>
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>

        {/* Contextual trip nav — only when on /trip */}
        {isOnTrip && (
          <>
            <SidebarGroup>
              <SidebarGroupLabel>Esta viagem</SidebarGroupLabel>
              <SidebarGroupContent>
                <SidebarMenu>
                  {tripNavItems.map((item) => (
                    <SidebarMenuItem key={item.href}>
                      <SidebarMenuButton
                        asChild
                        isActive={location.hash === item.href}
                        tooltip={item.label}
                        className="data-[active=true]:bg-gradient-primary! data-[active=true]:text-white!"
                      >
                        <a href={item.href}>
                          <item.icon />
                          <span>{item.label}</span>
                        </a>
                      </SidebarMenuButton>
                    </SidebarMenuItem>
                  ))}
                </SidebarMenu>
              </SidebarGroupContent>
            </SidebarGroup>
          </>
        )}
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

              {userMenuOpen && (
                <div className="absolute bottom-full left-0 mb-1 min-w-56 w-full rounded-lg border bg-popover shadow-lg text-popover-foreground py-1 z-50">
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
