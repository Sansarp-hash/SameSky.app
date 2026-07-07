"use client"

import { useState } from "react"
import { zodiacProfiles, mbtiProfiles, tarotDeck } from "@/lib/data"
import { cn } from "@/lib/utils"
import { Sparkles, Star, Moon } from "lucide-react"

type Tab = "astrology" | "mbti" | "tarot"

const tabs: { id: Tab; label: string; icon: typeof Star }[] = [
  { id: "astrology", label: "Star Sign", icon: Star },
  { id: "mbti", label: "Personality", icon: Sparkles },
  { id: "tarot", label: "Daily Pull", icon: Moon },
]

export function AiLab() {
  const [tab, setTab] = useState<Tab>("astrology")

  return (
    <div>
      <div
        className="mb-8 flex flex-wrap gap-2 rounded-full border border-border bg-card p-1.5"
        role="tablist"
        aria-label="AI Lab features"
      >
        {tabs.map(({ id, label, icon: Icon }) => (
          <button
            key={id}
            role="tab"
            aria-selected={tab === id}
            onClick={() => setTab(id)}
            className={cn(
              "flex flex-1 items-center justify-center gap-2 rounded-full px-4 py-2.5 text-sm font-medium transition-colors",
              tab === id
                ? "bg-primary text-primary-foreground"
                : "text-muted-foreground hover:text-foreground",
            )}
          >
            <Icon className="size-4" aria-hidden />
            {label}
          </button>
        ))}
      </div>

      {tab === "astrology" && <AstrologyPanel />}
      {tab === "mbti" && <MbtiPanel />}
      {tab === "tarot" && <TarotPanel />}
    </div>
  )
}

function AstrologyPanel() {
  const [selected, setSelected] = useState<string | null>(null)
  const profile = zodiacProfiles.find((z) => z.sign === selected)

  return (
    <div>
      <p className="mb-4 text-sm text-muted-foreground">Pick your sign to reveal your GL romance archetype.</p>
      <div className="grid grid-cols-3 gap-2 sm:grid-cols-4 md:grid-cols-6">
        {zodiacProfiles.map((z) => (
          <button
            key={z.sign}
            onClick={() => setSelected(z.sign)}
            aria-pressed={selected === z.sign}
            className={cn(
              "flex flex-col items-center gap-1 rounded-xl border p-3 transition-colors",
              selected === z.sign ? "border-primary bg-primary/10" : "border-border hover:border-primary/50",
            )}
          >
            <span className="text-2xl" aria-hidden>
              {z.symbol}
            </span>
            <span className="text-xs text-card-foreground">{z.sign}</span>
          </button>
        ))}
      </div>

      {profile && (
        <div className="mt-6 rounded-2xl border border-primary/30 bg-card p-6">
          <div className="flex items-center gap-3">
            <span className="text-4xl" aria-hidden>
              {profile.symbol}
            </span>
            <div>
              <h3 className="font-serif text-2xl text-card-foreground">{profile.sign}</h3>
              <p className="text-xs text-muted-foreground">{profile.dates}</p>
            </div>
          </div>
          <p className="mt-4 leading-relaxed text-card-foreground">{profile.vibe}</p>
          <p className="mt-3 text-sm text-muted-foreground">
            Best paired with: <span className="font-medium text-primary">{profile.match}</span>
          </p>
        </div>
      )}
    </div>
  )
}

function MbtiPanel() {
  const [selected, setSelected] = useState<string | null>(null)
  const profile = mbtiProfiles.find((m) => m.type === selected)

  return (
    <div>
      <p className="mb-4 text-sm text-muted-foreground">Choose a personality type to find its GL character energy.</p>
      <div className="grid grid-cols-2 gap-3 sm:grid-cols-3">
        {mbtiProfiles.map((m) => (
          <button
            key={m.type}
            onClick={() => setSelected(m.type)}
            aria-pressed={selected === m.type}
            className={cn(
              "rounded-xl border p-4 text-left transition-colors",
              selected === m.type ? "border-primary bg-primary/10" : "border-border hover:border-primary/50",
            )}
          >
            <p className="font-serif text-lg text-card-foreground">{m.type}</p>
            <p className="text-xs text-muted-foreground">{m.nickname}</p>
          </button>
        ))}
      </div>

      {profile && (
        <div className="mt-6 rounded-2xl border border-primary/30 bg-card p-6">
          <h3 className="font-serif text-2xl text-card-foreground">
            {profile.type} · {profile.nickname}
          </h3>
          <p className="mt-3 leading-relaxed text-card-foreground">{profile.archetype}</p>
          <p className="mt-3 text-sm text-muted-foreground">
            Pairs beautifully with: <span className="font-medium text-primary">{profile.pairsWith}</span>
          </p>
        </div>
      )}
    </div>
  )
}

function TarotPanel() {
  const [card, setCard] = useState<(typeof tarotDeck)[number] | null>(null)
  const [flipping, setFlipping] = useState(false)

  function pull() {
    setFlipping(true)
    setTimeout(() => {
      const next = tarotDeck[Math.floor(Math.random() * tarotDeck.length)]
      setCard(next)
      setFlipping(false)
    }, 450)
  }

  return (
    <div className="flex flex-col items-center text-center">
      <p className="mb-6 max-w-md text-sm text-muted-foreground">
        Draw a card for your daily GL love reading. The cosmos knows your ship.
      </p>

      <div
        className={cn(
          "flex aspect-[3/4] w-52 flex-col items-center justify-center rounded-2xl border-2 border-primary/40 bg-card p-6 transition-transform duration-500",
          flipping && "[transform:rotateY(90deg)]",
        )}
      >
        {card ? (
          <>
            <Moon className="mb-3 size-8 text-accent" aria-hidden />
            <h3 className="font-serif text-2xl text-card-foreground">{card.name}</h3>
            <p className="mt-1 text-xs uppercase tracking-widest text-muted-foreground">{card.meaning}</p>
            <p className="mt-4 text-sm leading-relaxed text-card-foreground">{card.glReading}</p>
          </>
        ) : (
          <>
            <Sparkles className="mb-3 size-8 text-primary" aria-hidden />
            <p className="font-serif text-lg text-muted-foreground">Your card awaits</p>
          </>
        )}
      </div>

      <button
        onClick={pull}
        className="mt-6 rounded-full bg-primary px-6 py-3 text-sm font-medium text-primary-foreground transition-opacity hover:opacity-90"
      >
        {card ? "Draw again" : "Draw a card"}
      </button>
    </div>
  )
}
