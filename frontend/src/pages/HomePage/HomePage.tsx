import { HomePageHeader } from "./HomePageHeader"
import { HomePageContent } from "./HomePageContent"

export function HomePage() {
  return (
    <div className="min-h-screen bg-background">
      <HomePageHeader />
      <HomePageContent />
    </div>
  )
}
