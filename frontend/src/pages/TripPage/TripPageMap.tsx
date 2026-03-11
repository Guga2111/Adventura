// Placeholder — real map functionality will be added later
export function TripPageMap() {
  return (
    <div className="relative w-full h-full overflow-hidden bg-gray-100">
      <img
        src="/eifel.jpeg"
        alt="Map placeholder"
        className="h-full w-full object-cover"
      />
      <div className="absolute inset-0 flex items-end bg-gradient-to-t from-black/90 via-black/50 to-transparent">
        <div className="px-8 pb-8">
          <h2 className="text-3xl font-bold text-white">Explore the World</h2>
          <p className="mt-3 max-w-xs text-sm text-white/80">
            Plan trips with friends. Discover new destinations and create
            unforgettable memories.
          </p>
        </div>
      </div>
    </div>
  );
}
