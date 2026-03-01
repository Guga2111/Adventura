import { useState, useEffect } from "react"
import { Plus } from "lucide-react"
import { Button } from "@/components/ui/button"
import { CreateTripDialog } from "@/components/trips/CreateTripDialog"
import { TripCard } from "@/components/trips/TripCard"
import { getTripsByUser } from "@/services/TripService"
import { useAuth } from "@/context/AuthContext"
import type { Trip } from "@/types/trip"

export function HomePageContent() {
  const { user } = useAuth()
  const [createTripOpen, setCreateTripOpen] = useState(false)
  const [trips, setTrips] = useState<Trip[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [fetchError, setFetchError] = useState<string | null>(null)

  const fetchTrips = async () => {
    if (!user) return
    setIsLoading(true)
    setFetchError(null)
    try {
      const data = await getTripsByUser(user.userId)
      setTrips(data)
    } catch (err) {
      console.error("Failed to fetch trips:", err)
      setFetchError("Erro ao carregar viagens. Tenta novamente.")
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    fetchTrips()
  }, [user])

  return (
    <main className="mx-auto max-w-7xl px-6 py-8">
      <div className="flex items-center justify-between">
        <h1 className="text-4xl font-semibold tracking-tight text-stone-800">
          Suas próximas viagens
        </h1>
        <Button
          className="bg-gradient-primary text-white hover:opacity-90 rounded-full cursor-pointer hidden sm:inline-flex"
          onClick={() => setCreateTripOpen(true)}
        >
          <Plus className="h-4 w-4" />
          Planejar nova viagem
        </Button>
        <Button
          size="icon"
          className="bg-gradient-primary text-white hover:opacity-90 rounded-full cursor-pointer sm:hidden"
          onClick={() => setCreateTripOpen(true)}
        >
          <Plus className="h-5 w-5" />
        </Button>
      </div>

      {user && (
        <CreateTripDialog
          open={createTripOpen}
          onOpenChange={setCreateTripOpen}
          userId={user.userId}
          onTripCreated={fetchTrips}
        />
      )}

      <div className="mt-6 grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {isLoading ? (
          Array.from({ length: 3 }).map((_, i) => (
            <div
              key={i}
              className="h-64 rounded-2xl bg-muted animate-pulse"
            />
          ))
        ) : fetchError ? (
          <div className="col-span-full py-16 text-center">
            <p className="text-sm text-destructive">{fetchError}</p>
            <button onClick={fetchTrips} className="mt-2 text-sm text-muted-foreground underline cursor-pointer">
              Tentar novamente
            </button>
          </div>
        ) : trips.length === 0 ? (
          <div className="col-span-full flex flex-col items-center gap-3 py-16 text-center">
            <div className="flex gap-1">
              <p className="text-muted-foreground text-sm">
              Nada criado por aqui.
              </p>
              <p className="text-gradient-primary text-sm cursor-pointer" onClick={() => setCreateTripOpen(true)}>
                Vamos planejar uma viagem?
              </p>
            </div>
          </div>
        ) : (
          trips.map((trip) => (
            <TripCard key={trip.id} trip={trip} />
          ))
        )}
      </div>
    </main>
  );
}
