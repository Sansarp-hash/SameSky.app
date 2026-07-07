import Link from "next/link"
import Image from "next/image"
import { Star } from "lucide-react"
import type { GLTitle } from "@/lib/data"

export function TitleCard({ title }: { title: GLTitle }) {
  return (
    <Link
      href={`/encyclopedia/${title.slug}`}
      className="group flex flex-col overflow-hidden rounded-xl border border-border bg-card transition-colors hover:border-primary/60"
    >
      <div className="relative aspect-[3/4] overflow-hidden">
        <Image
          src={title.cover || "/placeholder.svg"}
          alt={`Cover art for ${title.title}`}
          fill
          sizes="(max-width: 768px) 50vw, 25vw"
          className="object-cover transition-transform duration-500 group-hover:scale-105"
        />
        <span className="absolute left-2 top-2 rounded-full bg-background/85 px-2.5 py-1 text-xs font-medium backdrop-blur">
          {title.format}
        </span>
        <span className="absolute right-2 top-2 flex items-center gap-1 rounded-full bg-background/85 px-2 py-1 text-xs font-medium backdrop-blur">
          <Star className="size-3 fill-accent text-accent" aria-hidden />
          {title.rating.toFixed(1)}
        </span>
      </div>
      <div className="flex flex-1 flex-col gap-1 p-3">
        <h3 className="font-serif text-base font-semibold leading-tight text-balance transition-colors group-hover:text-primary">
          {title.title}
        </h3>
        <p className="text-xs text-muted-foreground">
          {title.creator} · {title.year}
        </p>
        <div className="mt-2 flex flex-wrap gap-1">
          {title.tags.slice(0, 2).map((tag) => (
            <span
              key={tag}
              className="rounded-full bg-muted px-2 py-0.5 text-[11px] text-muted-foreground"
            >
              {tag}
            </span>
          ))}
        </div>
      </div>
    </Link>
  )
}
