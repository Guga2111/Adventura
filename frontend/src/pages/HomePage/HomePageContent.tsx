import { Plus, ChevronDown } from "lucide-react"
import { Button } from "@/components/ui/button"
import { TripCard } from "./TripCard"

export function HomePageContent() {
  return (
    <main className="mx-auto max-w-7xl px-6 py-8">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold tracking-tight">
          Visto recentemente e proximas
        </h1>
        <Button className="bg-gradient-primary text-white hover:opacity-90 rounded-full cursor-pointer hidden sm:inline-flex">
          <Plus className="h-4 w-4" />
          Planejar nova viagem
        </Button>
        <Button
          size="icon"
          className="bg-gradient-primary text-white hover:opacity-90 rounded-full cursor-pointer sm:hidden"
        >
          <Plus className="h-5 w-5" />
        </Button>
      </div>

      <div className="mt-6">
        <button
          type="button"
          className="flex items-center gap-1.5 rounded-full border px-3 py-1.5 text-sm font-medium hover:bg-accent transition-colors cursor-pointer"
        >
          Visto recentemente
          <ChevronDown className="h-4 w-4" />
        </button>
      </div>

      <div className="mt-6 grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {/*{mockTrips.map((trip) => (
          <TripCard key={trip.id} trip={trip} />
        ))} */}
      </div>
    </main>
  );
}
