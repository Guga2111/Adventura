import { MapPinned } from "lucide-react"
import { Button } from "@/components/ui/button"
import { useNavigate } from "react-router-dom"

export function LandingPageHeader() {

    const navigate = useNavigate();

    return (
        <header className="flex items-center justify-between px-6 py-4 border-b">
            <a href="/" className="flex items-center gap-2 text-xl font-bold">
                <MapPinned className="size-7 text-[#dda600]" />
                <span className="text-xl text-bold">Adventura</span>
            </a>

            <nav className="hidden md:flex items-center gap-6 text-sm font-medium text-muted-foreground">
                <a href="#how-it-works" className="hover:text-foreground transition-colors">Como Funciona</a>
                <a href="#about" className="hover:text-foreground transition-colors">Sobre</a>
                <a href="#features" className="hover:text-foreground transition-colors">Funcionalidades</a>
            </nav>

            <div className="flex items-center gap-3">
                <Button variant="ghost" size="sm" className="cursor-pointer">Login</Button>
                <Button size="sm" className="bg-gradient-primary text-white hover:opacity-90 cursor-pointer" onClick={() => navigate('/signup')} >Registrar</Button>
            </div>
        </header>
    )
}
