import type { Metadata } from "next"
import { AiLab } from "@/components/ai-lab"

export const metadata: Metadata = {
  title: "AI Lab",
  description:
    "Explore SameSky's AI Lab — discover your Girls' Love romance archetype through star signs, personality types, and daily tarot readings.",
}

export default function AiLabPage() {
  return (
    <div className="mx-auto max-w-4xl px-4 py-10 sm:px-6 lg:px-8">
      <header className="mb-10 text-center">
        <p className="text-sm font-medium uppercase tracking-widest text-primary">AI Lab</p>
        <h1 className="mt-2 font-serif text-4xl text-foreground text-balance sm:text-5xl">
          Written in the stars
        </h1>
        <p className="mx-auto mt-3 max-w-xl text-muted-foreground leading-relaxed">
          A playful corner where astrology, personality, and tarot meet Girls&apos; Love. Discover your romance
          archetype and the tropes you were destined for.
        </p>
      </header>

      <AiLab />
    </div>
  )
}
