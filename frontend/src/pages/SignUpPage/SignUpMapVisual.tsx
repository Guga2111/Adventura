export function SignUpMapVisual() {
  return (
    <div className="hidden lg:flex lg:w-1/2">
      <div className="relative w-full h-full overflow-hidden bg-gray-100">
        <img
          src="/eifel.jpeg"  
          alt="Mapa de fundo"
          className="h-full w-full object-cover" 
        />

        <div className="absolute inset-0 flex items-end bg-gradient-to-t from-black/90 via-black/50 to-transparent">
          <div className="px-12 pb-12">
            <h2 className="text-4xl font-bold text-white shadow-sm">
              Explore o Mundo
            </h2>
            <p className="mt-4 max-w-sm text-lg text-white/90 shadow-sm">
              Planeje viagens com amigos. Descubra novos destinos e crie
              memórias inesquecíveis.
            </p>
          </div>
        </div>
        
      </div>
    </div>
  );
}