import { useState } from "react"
import { Link, useLocation } from "react-router-dom"
import { Menu, X, Search, Bell } from "lucide-react"
import AdventuraIcon from "@/components/common/AdventuraIcon"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Avatar, AvatarFallback } from "@/components/ui/avatar"
import { cn } from "@/lib/utils"

const navLinks = [
  { label: "Inicio", path: "/home" },
  { label: "Grupos", path: "/grupos"},
  { label: "Explorar", path: "/explore" },
];

export function HomePageHeader() {
  const [menuOpen, setMenuOpen] = useState(false);
  const location = useLocation();

  return (
    <header className="relative border-b bg-background">
      <div className="flex items-center justify-between px-6 py-3">
        {/* Left: Logo + Nav */}
        <div className="flex items-center gap-6">
          <Link to="/home" className="flex items-center gap-2 text-lg font-bold">
            <AdventuraIcon size={36} className="text-[#dda600]" />
            <span>Adventura</span>
          </Link>

          <nav className="hidden md:flex items-center gap-5">
            {navLinks.map((link) => (
              <Link
                key={link.path}
                to={link.path}
                className={cn(
                  "text-sm font-medium transition-colors",
                  location.pathname === link.path
                    ? "text-foreground font-semibold"
                    : "text-muted-foreground hover:text-foreground"
                )}
              >
                {link.label}
              </Link>
            ))}
          </nav>
        </div>

        {/* Right: Search + Notifications + Avatar */}
        <div className="hidden md:flex items-center gap-3">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <Input
              type="text"
              placeholder="Pesquisar lugar ou utilizador"
              className="h-8 w-48 lg:w-64 rounded-full pl-9 pr-3 text-sm"
            />
          </div>

          <Button variant="ghost" size="icon-sm" className="relative cursor-pointer">
            <Bell className="h-4 w-4" />
            <span className="absolute -top-0.5 -right-0.5 h-2 w-2 rounded-full bg-gradient-primary" />
          </Button>

          <Avatar size="sm">
            <AvatarFallback className="text-xs">LS</AvatarFallback>
          </Avatar>
        </div>

        {/* Mobile: Search + Hamburger */}
        <div className="flex md:hidden items-center gap-2">
          <Button variant="ghost" size="icon-sm" className="cursor-pointer">
            <Search className="h-4 w-4" />
          </Button>
          <button
            type="button"
            onClick={() => setMenuOpen(!menuOpen)}
            className="flex items-center justify-center rounded-md p-2 text-muted-foreground hover:text-foreground transition-colors"
          >
            {menuOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
          </button>
        </div>
      </div>

      {/* Mobile menu */}
      {menuOpen && (
        <nav className="md:hidden flex flex-col gap-4 border-t px-6 py-4">
          {navLinks.map((link) => (
            <Link
              key={link.path}
              to={link.path}
              onClick={() => setMenuOpen(false)}
              className={cn(
                "text-sm font-medium transition-colors",
                location.pathname === link.path
                  ? "text-foreground font-semibold"
                  : "text-muted-foreground hover:text-foreground"
              )}
            >
              {link.label}
            </Link>
          ))}
          <div className="flex items-center gap-3 pt-2 border-t">
            <Avatar size="sm">
              <AvatarFallback className="text-xs">LS</AvatarFallback>
            </Avatar>
            <span className="text-sm font-medium">Luis Sampaio</span>
          </div>
        </nav>
      )}
    </header>
  );
}
