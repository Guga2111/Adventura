import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { SidebarProvider, SidebarInset, SidebarTrigger } from "@/components/ui/sidebar";
import { AppSidebar } from "@/components/common/AppSidebar";
import { TripPageHeader } from "./TripPageHeader";
import { TripPageExcursions } from "./TripPageExcursions";
import { TripPageMap } from "./TripPageMap";
import { getTripById } from "@/services/TripService";
import type { Trip } from "@/types/trip";

export function TripPage() {
  const { id } = useParams<{ id: string }>();
  const [trip, setTrip] = useState<Trip | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    if (!id) return;
    setIsLoading(true);
    setError(false);
    getTripById(Number(id))
      .then(setTrip)
      .catch(() => setError(true))
      .finally(() => setIsLoading(false));
  }, [id]);

  if (isLoading) {
    return (
      <SidebarProvider defaultOpen={false}>
        <AppSidebar />
        <SidebarInset>
          <div className="h-64 md:h-96 bg-muted animate-pulse" />
          <div className="max-w-5xl mx-auto px-4 md:px-8 py-8 space-y-4">
            <div className="h-6 w-32 bg-muted animate-pulse rounded" />
            <div className="h-10 w-96 bg-muted animate-pulse rounded" />
            <div className="h-4 w-64 bg-muted animate-pulse rounded" />
          </div>
        </SidebarInset>
      </SidebarProvider>
    );
  }

  if (error || !trip) {
    return (
      <SidebarProvider defaultOpen={false}>
        <AppSidebar />
        <SidebarInset>
          <div className="flex flex-col items-center justify-center h-64 gap-2 text-muted-foreground">
            <p className="text-sm">Não foi possível carregar a viagem.</p>
          </div>
        </SidebarInset>
      </SidebarProvider>
    );
  }

  return (
    <SidebarProvider defaultOpen={false}>
      <AppSidebar />
      <SidebarInset>
        {/* Mobile: trigger to open sidebar as sheet */}
        <div className="flex md:hidden items-center gap-2 px-4 py-2 border-b">
          <SidebarTrigger />
          <span className="font-semibold text-sm">Adventura</span>
        </div>

        <TripPageHeader trip={trip} />

        {/* Main content area */}
        <div className="flex flex-col lg:flex-row lg:h-[calc(100vh-(--spacing(16)))]">
          {/* Timeline — scrollable */}
          <div className="flex-1 min-w-0 overflow-y-auto px-4 md:px-8 py-8">
            <TripPageExcursions
              tripId={trip.id}
              members={trip.group.members}
              initialFlights={trip.flights ?? []}
              initialExcursions={trip.excursions ?? []}
            />
          </div>
          {/* Map — fills remaining height, hidden on mobile */}
          <div className="hidden lg:block lg:w-42/100 shrink-0">
            <TripPageMap />
          </div>
        </div>
      </SidebarInset>
    </SidebarProvider>
  );
}
