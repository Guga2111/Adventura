import { MapPinned } from "lucide-react"
import { Button } from "@/components/ui/button"

export function LandingPageHeader() {
    return (
        <header className="flex items-center justify-between px-6 py-4 border-b">
            <a href="/" className="flex items-center gap-2 text-xl font-bold">
                <MapPinned className="size-6" />
                Adventura
            </a>

            <nav className="hidden md:flex items-center gap-6 text-sm font-medium text-muted-foreground">
                <a href="#features" className="hover:text-foreground transition-colors">Features</a>
                <a href="#how-it-works" className="hover:text-foreground transition-colors">How It Works</a>
                <a href="#about" className="hover:text-foreground transition-colors">About</a>
            </nav>

            <div className="flex items-center gap-3">
                <Button variant="ghost" size="sm" >Sign In</Button>
                <Button size="sm">Sign Up</Button>
            </div>
        </header>
    )
}
