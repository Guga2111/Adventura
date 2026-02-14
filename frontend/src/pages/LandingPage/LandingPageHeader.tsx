import { Menu, X } from "lucide-react"
import AdventuraIcon from "../../components/common/AdventuraIcon"
import { Button } from "@/components/ui/button"
import { useNavigate } from "react-router-dom"
import { useState } from "react"

export function LandingPageHeader() {

    const navigate = useNavigate();
    const [menuOpen, setMenuOpen] = useState(false);

    return (
        <header className="relative border-b">
            <div className="flex items-center justify-between px-6 py-4">
                <a href="/" className="flex items-center gap-2 text-xl font-bold">
                    <AdventuraIcon className="size-12 text-[#dda600]" />
                    <span className="text-xl text-bold">Adventura</span>
                </a>

                <nav className="hidden md:flex items-center gap-6 text-sm font-medium text-muted-foreground">
                    <a href="#how-it-works" className="hover:text-foreground transition-colors">Como Funciona</a>
                    <a href="#about" className="hover:text-foreground transition-colors">Sobre</a>
                    <a href="#features" className="hover:text-foreground transition-colors">Funcionalidades</a>
                </nav>

                <div className="hidden md:flex items-center gap-3">
                    <Button variant="ghost" size="sm" className="cursor-pointer">Login</Button>
                    <Button size="sm" className="bg-gradient-primary text-white hover:opacity-90 cursor-pointer" onClick={() => navigate('/signup')} >Registrar</Button>
                </div>

                <button
                    type="button"
                    onClick={() => setMenuOpen(!menuOpen)}
                    className="md:hidden flex items-center justify-center rounded-md p-2 text-muted-foreground hover:text-foreground transition-colors"
                >
                    {menuOpen ? <X className="size-6" /> : <Menu className="size-6" />}
                </button>
            </div>

            {menuOpen && (
                <nav className="md:hidden flex flex-col gap-4 border-t px-6 py-4">
                    <a href="#how-it-works" onClick={() => setMenuOpen(false)} className="text-sm font-medium text-muted-foreground hover:text-foreground transition-colors">Como Funciona</a>
                    <a href="#about" onClick={() => setMenuOpen(false)} className="text-sm font-medium text-muted-foreground hover:text-foreground transition-colors">Sobre</a>
                    <a href="#features" onClick={() => setMenuOpen(false)} className="text-sm font-medium text-muted-foreground hover:text-foreground transition-colors">Funcionalidades</a>
                    <div className="flex flex-col gap-2 pt-2">
                        <Button variant="ghost" size="sm" className="w-full cursor-pointer">Login</Button>
                        <Button size="sm" className="w-full bg-gradient-primary text-white hover:opacity-90 cursor-pointer" onClick={() => { setMenuOpen(false); navigate('/signup'); }}>Registrar</Button>
                    </div>
                </nav>
            )}
        </header>
    )
}
