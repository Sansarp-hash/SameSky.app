import Link from "next/link"
import { Sparkles } from "lucide-react"

export function SiteFooter() {
  return (
    <footer className="border-t border-border/70 bg-card">
      <div className="mx-auto grid max-w-6xl gap-8 px-4 py-12 sm:px-6 md:grid-cols-4">
        <div className="md:col-span-2">
          <div className="flex items-center gap-2">
            <span className="grid size-8 place-items-center rounded-full bg-primary text-primary-foreground">
              <Sparkles className="size-4" aria-hidden />
            </span>
            <span className="font-serif text-xl font-semibold">SameSky</span>
          </div>
          <p className="mt-3 max-w-sm text-sm leading-relaxed text-muted-foreground">
            The centralized encyclopedia and fan community for Girls&apos; Love stories.
            Under the same sky, every story has a home.
          </p>
        </div>

        <div>
          <h3 className="font-medium">Explore</h3>
          <ul className="mt-3 space-y-2 text-sm text-muted-foreground">
            <li><Link href="/encyclopedia" className="hover:text-foreground">Encyclopedia</Link></li>
            <li><Link href="/community" className="hover:text-foreground">Community</Link></li>
            <li><Link href="/ai-lab" className="hover:text-foreground">AI Lab</Link></li>
          </ul>
        </div>

        <div>
          <h3 className="font-medium">Community</h3>
          <ul className="mt-3 space-y-2 text-sm text-muted-foreground">
            <li><Link href="/community" className="hover:text-foreground">Discussions</Link></li>
            <li><Link href="/community" className="hover:text-foreground">Fan Art</Link></li>
            <li><Link href="/community" className="hover:text-foreground">Polls</Link></li>
          </ul>
        </div>
      </div>
      <div className="border-t border-border/70">
        <p className="mx-auto max-w-6xl px-4 py-6 text-xs text-muted-foreground sm:px-6">
          &copy; {new Date().getFullYear()} SameSky. A fan-built encyclopedia. All featured works are fictional samples.
        </p>
      </div>
    </footer>
  )
}
