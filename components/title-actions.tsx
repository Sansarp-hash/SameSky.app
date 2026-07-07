"use client"

import { useState } from "react"
import { Bookmark, BookmarkCheck, Heart } from "lucide-react"
import { cn } from "@/lib/utils"

const reactions = [
  { key: "love", label: "Loved it", emoji: "\u{1F495}" },
  { key: "cry", label: "Made me cry", emoji: "\u{1F62D}" },
  { key: "cozy", label: "So cozy", emoji: "\u2728" },
]

export function TitleActions() {
  const [saved, setSaved] = useState(false)
  const [active, setActive] = useState<string | null>(null)
  const [counts, setCounts] = useState<Record<string, number>>({ love: 214, cry: 132, cozy: 98 })

  function react(key: string) {
    setCounts((prev) => {
      const next = { ...prev }
      if (active === key) {
        next[key] -= 1
        return next
      }
      if (active) next[active] -= 1
      next[key] += 1
      return next
    })
    setActive((cur) => (cur === key ? null : key))
  }

  return (
    <div className="space-y-4">
      <button
        type="button"
        onClick={() => setSaved((v) => !v)}
        aria-pressed={saved}
        className={cn(
          "inline-flex w-full items-center justify-center gap-2 rounded-full px-5 py-3 text-sm font-medium transition-colors sm:w-auto",
          saved
            ? "border border-primary bg-primary/15 text-primary"
            : "bg-primary text-primary-foreground hover:opacity-90",
        )}
      >
        {saved ? <BookmarkCheck className="size-4" aria-hidden /> : <Bookmark className="size-4" aria-hidden />}
        {saved ? "On your watchlist" : "Add to watchlist"}
      </button>

      <div>
        <div className="mb-2 flex items-center gap-1.5 text-xs text-muted-foreground">
          <Heart className="size-3.5" aria-hidden />
          How did it make you feel?
        </div>
        <div className="flex flex-wrap gap-2">
          {reactions.map((r) => (
            <button
              key={r.key}
              type="button"
              onClick={() => react(r.key)}
              aria-pressed={active === r.key}
              className={cn(
                "inline-flex items-center gap-2 rounded-full border px-3 py-1.5 text-sm transition-colors",
                active === r.key
                  ? "border-primary bg-primary/15 text-foreground"
                  : "border-border bg-card text-muted-foreground hover:border-primary/60 hover:text-foreground",
              )}
            >
              <span aria-hidden>{r.emoji}</span>
              {r.label}
              <span className="tabular-nums text-xs text-muted-foreground">{counts[r.key]}</span>
            </button>
          ))}
        </div>
      </div>
    </div>
  )
}
