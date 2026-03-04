import { SidebarProvider, SidebarInset, SidebarTrigger } from "@/components/ui/sidebar";
import { AppSidebar } from "@/components/common/AppSidebar";
import { TripPageHeader } from "./TripPageHeader";
import type { Trip } from "@/types/trip";

// TODO: replace with real data from route params + API call
const mockTrip: Trip = {
  id: 1,
  group: {
    id: 1,
    name: "Europe Gang",
    description: "",
    createdBy: { id: 1, name: "Luis Sampaio", email: "luis@example.com", role: "USER", createdAt: "" },
    members: [
      { id: 1, user: { id: 1, name: "Luis Sampaio", email: "luis@example.com", role: "USER", createdAt: "" }, role: "ADMIN", enteredWhen: "" },
      { id: 2, user: { id: 2, name: "Ana Costa", email: "ana@example.com", role: "USER", createdAt: "" }, role: "MEMBER", enteredWhen: "" },
      { id: 3, user: { id: 3, name: "Pedro Silva", email: "pedro@example.com", role: "USER", createdAt: "" }, role: "MEMBER", enteredWhen: "" },
      { id: 4, user: { id: 4, name: "Marta Lopes", email: "marta@example.com", role: "USER", createdAt: "" }, role: "MEMBER", enteredWhen: "" },
      { id: 5, user: { id: 5, name: "Rui Ferreira", email: "rui@example.com", role: "USER", createdAt: "" }, role: "MEMBER", enteredWhen: "" },
    ],
    createdAt: "",
    updatedAt: "",
  },
  destinations: ["France", "Germany", "Austria"],
  countryCodes: ["FR", "DE", "AT"],
  description: "Epic summer adventure through Europe!",
  excursions: [],
  excursionCount: 5,
  startDate: "2026-07-15",
  endDate: "2026-07-29",
  mainCurrency: "EUR",
  totalBudget: 3000,
  status: "PLANNING",
  coverImageUrl: "https://images.unsplash.com/photo-1499856871958-5b9627545d1a?w=1600&q=80",
  coverImageAuthor: "Pedro Lastra",
  coverImageAuthorUrl: "https://unsplash.com/@peterlaster",
  createdAt: "",
  updatedAt: "",
};

export function TripPage() {
  return (
    <SidebarProvider defaultOpen={false}>
      <AppSidebar />
      <SidebarInset>
        {/* Mobile: trigger to open sidebar as sheet */}
        <div className="flex md:hidden items-center gap-2 px-4 py-2 border-b">
          <SidebarTrigger />
          <span className="font-semibold text-sm">Adventura</span>
        </div>

        <TripPageHeader trip={mockTrip} />

        {/* Main content area — tabs and sections go here */}
        <div className="max-w-5xl mx-auto px-4 md:px-8 py-8">
          {/* Excursions | Budget | Members | Settings tabs coming soon */}
        </div>
      </SidebarInset>
    </SidebarProvider>
  );
}
