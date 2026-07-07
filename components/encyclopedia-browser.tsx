"use client"

import { useMemo, useState } from "react"
import { Search, SlidersHorizontal } from "lucide-react"
import { titles, allFormats, allTags, type TitleFormat } from "@/lib/data"
import { TitleCard } from "@/components/title-card"
import { cn } from "@/lib/utils"

type SortKey = "popularity" | "rating" | "year" | "title"

const sorts: { key: SortKey; label: string }[] = [
  { key: "popularity", label: "Most popular" },
  { key: "rating", label: "Top rated" },
  { key: "year", label: "Newest" },
  { key: "title", label: "A–Z" },
]

export function EncyclopediaBrowser() {
  const [query, setQuery] = useState("")
  const [format, setFormat] = useState<TitleFormat | "All">("All")
  const [tag, setTag] = useState<string | "All">("All")
  const [sort, setSort] = useState<SortKey>("popularity")

  const results = useMemo(() => {
    const q = query.trim().toLowerCase()
    return titles
      .filter((t) => (format === "All" ? true : t.format === format))
      .filter((t) => (tag === "All" ? true : t.tags.includes(tag)))
      .filter((t) =>
        q === ""
          ? true
          : t.title.toLowerCase().includes(q) ||
            t.altTitle?.toLowerCase().includes(q) ||
            t.creator.toLowerCase().includes(q) ||
            t.tags.some((x) => x.toLowerCase().includes(q)),
      )
      .sort((a, b) => {
        switch (sort) {
          case "rating":
            return b.rating - a.rating
          case "year":
            return b.year - a.year
          case "title":
            return a.title.localeCompare(b.title)
          default:
            return b.popularity - a.popularity
        }
      })
  }, [query, format, tag, sort])

  return (
    <div>
      {/* Search + sort */}
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
        <div className="relative flex-1">
          <Search className="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" aria-hidden />
          <input
            type="search"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search titles, creators, or tags…"
            aria-label="Search the encyclopedia"
            className="w-full rounded-full border border-border bg-card py-2.5 pl-10 pr-4 text-sm outline-none transition-colors placeholder:text-muted-foreground focus:border-primary"
          />
        </div>
        <div className="flex items-center gap-2">
          <SlidersHorizontal className="size-4 text-muted-foreground" aria-hidden />
          <label htmlFor="sort" className="sr-only">Sort titles</label>
          <select
            id="sort"
            value={sort}
            onChange={(e) => setSort(e.target.value as SortKey)}
            className="rounded-full border border-border bg-card px-4 py-2.5 text-sm outline-none focus:border-primary"
          >
            {sorts.map((s) => (
              <option key={s.key} value={s.key}>{s.label}</option>
            ))}
          </select>
        </div>
      </div>

      {/* Format filters */}
      <div className="mt-5 flex flex-wrap gap-2">
        <FilterChip active={format === "All"} onClick={() => setFormat("All")}>All formats</FilterChip>
        {allFormats.map((f) => (
          <FilterChip key={f} active={format === f} onClick={() => setFormat(f)}>{f}</FilterChip>
        ))}
      </div>

      {/* Tag filters */}
      <div className="mt-3 flex flex-wrap gap-2">
        <FilterChip active={tag === "All"} onClick={() => setTag("All")} subtle>All tags</FilterChip>
        {allTags.map((t) => (
          <FilterChip key={t} active={tag === t} onClick={() => setTag(t)} subtle>{t}</FilterChip>
        ))}
      </div>

      <p className="mt-6 text-sm text-muted-foreground" aria-live="polite">
        {results.length} {results.length === 1 ? "title" : "titles"} found
      </p>

      {results.length > 0 ? (
        <div className="mt-4 grid grid-cols-2 gap-4 md:grid-cols-4">
          {results.map((t) => (
            <TitleCard key={t.slug} title={t} />
          ))}
        </div>
      ) : (
        <div className="mt-10 rounded-xl border border-dashed border-border p-10 text-center">
          <p className="font-serif text-lg">No titles match your filters.</p>
          <p className="mt-1 text-sm text-muted-foreground">Try clearing the search or picking a different tag.</p>
        </div>
      )}
    </div>
  )
}

function FilterChip({
  active,
  subtle,
  onClick,
  children,
}: {
  active: boolean
  subtle?: boolean
  onClick: () => void
  children: React.ReactNode
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={cn(
        "rounded-full border px-3.5 py-1.5 text-sm transition-colors",
        active
          ? subtle
            ? "border-secondary bg-secondary text-secondary-foreground"
            : "border-primary bg-primary text-primary-foreground"
          : "border-border bg-card text-muted-foreground hover:border-primary/60 hover:text-foreground",
      )}
    >
      {children}
    </button>
  )
}
