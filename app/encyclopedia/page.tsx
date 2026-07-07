import type { Metadata } from "next"
import { EncyclopediaBrowser } from "@/components/encyclopedia-browser"

export const metadata: Metadata = {
  title: "Encyclopedia — SameSky",
  description: "Browse and filter the SameSky encyclopedia of Girls' Love manga, anime, novels, manhwa, and films.",
}

export default function EncyclopediaPage() {
  return (
    <div className="mx-auto max-w-6xl px-4 py-12 sm:px-6">
      <header className="max-w-2xl">
        <h1 className="font-serif text-3xl font-semibold sm:text-4xl">The Encyclopedia</h1>
        <p className="mt-3 text-pretty leading-relaxed text-muted-foreground">
          A living catalog of Girls&apos; Love stories across every format. Search by title,
          filter by trope, and find your next favorite read or watch.
        </p>
      </header>
      <div className="mt-8">
        <EncyclopediaBrowser />
      </div>
    </div>
  )
}
