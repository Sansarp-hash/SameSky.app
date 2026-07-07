"use client"

import { useState } from "react"
import type { Poll } from "@/lib/data"
import { cn } from "@/lib/utils"
import { Check } from "lucide-react"

export function PollCard({ poll }: { poll: Poll }) {
  const [voted, setVoted] = useState<number | null>(null)
  const hasVoted = voted !== null
  const totalVotes = poll.totalVotes + (hasVoted ? 1 : 0)

  return (
    <div className="rounded-2xl border border-border bg-card p-5">
      <p className="font-serif text-lg text-card-foreground text-pretty">{poll.question}</p>
      <div className="mt-4 flex flex-col gap-2">
        {poll.options.map((opt, i) => {
          const votes = opt.votes + (voted === i ? 1 : 0)
          const pct = Math.round((votes / totalVotes) * 100)
          const selected = voted === i
          return (
            <button
              key={opt.label}
              type="button"
              onClick={() => !hasVoted && setVoted(i)}
              disabled={hasVoted}
              aria-label={`Vote for ${opt.label}`}
              className={cn(
                "relative overflow-hidden rounded-xl border px-4 py-3 text-left transition-colors",
                hasVoted ? "cursor-default border-border" : "cursor-pointer border-border hover:border-primary",
                selected && "border-primary",
              )}
            >
              {hasVoted && (
                <span
                  className={cn(
                    "absolute inset-y-0 left-0 rounded-xl transition-all duration-700",
                    selected ? "bg-primary/25" : "bg-muted",
                  )}
                  style={{ width: `${pct}%` }}
                  aria-hidden
                />
              )}
              <span className="relative flex items-center justify-between gap-3">
                <span className="flex items-center gap-2 text-sm text-card-foreground">
                  {selected && <Check className="size-4 text-primary" aria-hidden />}
                  {opt.label}
                </span>
                {hasVoted && <span className="text-sm font-medium text-muted-foreground">{pct}%</span>}
              </span>
            </button>
          )
        })}
      </div>
      <p className="mt-3 text-xs text-muted-foreground">
        {totalVotes.toLocaleString()} votes{hasVoted ? " · thanks for voting!" : " · tap an option to vote"}
      </p>
    </div>
  )
}
