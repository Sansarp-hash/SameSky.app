import type { Metadata } from "next"
import Link from "next/link"
import Image from "next/image"
import { notFound } from "next/navigation"
import { ArrowLeft, Calendar, Clock, Star, TrendingUp } from "lucide-react"
import { titles, getTitle, relatedTitles } from "@/lib/data"
import { TitleActions } from "@/components/title-actions"
import { TitleCard } from "@/components/title-card"

export function generateStaticParams() {
  return titles.map((t) => ({ slug: t.slug }))
}

export async function generateMetadata({
  params,
}: {
  params: Promise<{ slug: string }>
}): Promise<Metadata> {
  const { slug } = await params
  const title = getTitle(slug)
  if (!title) return { title: "Not found — SameSky" }
  return {
    title: `${title.title} — SameSky`,
    description: title.tagline,
  }
}

export default async function TitleDetailPage({
  params,
}: {
  params: Promise<{ slug: string }>
}) {
  const { slug } = await params
  const title = getTitle(slug)
  if (!title) notFound()

  const related = relatedTitles(slug)

  return (
    <article>
      {/* Ambient banner */}
      <div className="relative border-b border-border/70">
        <div
          className="pointer-events-none absolute inset-0 opacity-30"
          style={{
            background:
              "radial-gradient(70% 90% at 20% 0%, color-mix(in oklab, var(--primary) 45%, transparent), transparent 65%)",
          }}
          aria-hidden
        />
        <div className="star-field pointer-events-none absolute inset-0 opacity-50" aria-hidden />
        <div className="relative mx-auto max-w-6xl px-4 py-8 sm:px-6">
          <Link
            href="/encyclopedia"
            className="inline-flex items-center gap-2 text-sm text-muted-foreground transition-colors hover:text-foreground"
          >
            <ArrowLeft className="size-4" aria-hidden />
            Back to encyclopedia
          </Link>

          <div className="mt-6 grid gap-8 md:grid-cols-[280px_1fr]">
            <div className="mx-auto w-full max-w-[280px]">
              <div className="relative aspect-[3/4] overflow-hidden rounded-xl border border-border shadow-lg">
                <Image
                  src={title.cover || "/placeholder.svg"}
                  alt={`Cover art for ${title.title}`}
                  fill
                  sizes="280px"
                  className="object-cover"
                  priority
                />
              </div>
            </div>

            <div>
              <div className="flex flex-wrap items-center gap-2">
                <span className="rounded-full bg-primary px-3 py-1 text-xs font-medium text-primary-foreground">
                  {title.format}
                </span>
                <span className="rounded-full border border-border bg-card px-3 py-1 text-xs text-muted-foreground">
                  {title.status}
                </span>
              </div>
              <h1 className="mt-4 font-serif text-3xl font-semibold text-balance sm:text-4xl">{title.title}</h1>
              {title.altTitle && (
                <p className="mt-1 text-sm text-muted-foreground">{title.altTitle}</p>
              )}
              <p className="mt-4 max-w-2xl text-pretty text-lg italic leading-relaxed text-secondary">
                {title.tagline}
              </p>

              <div className="mt-6 flex flex-wrap gap-x-6 gap-y-3 text-sm">
                <Meta icon={<Star className="size-4 fill-accent text-accent" />} label={`${title.rating.toFixed(1)} rating`} />
                <Meta icon={<TrendingUp className="size-4 text-primary" />} label={`${title.popularity}% popularity`} />
                <Meta icon={<Calendar className="size-4 text-muted-foreground" />} label={`${title.year}`} />
                <Meta icon={<Clock className="size-4 text-muted-foreground" />} label={title.length} />
              </div>

              <div className="mt-8">
                <TitleActions />
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Body */}
      <div className="mx-auto grid max-w-6xl gap-10 px-4 py-12 sm:px-6 md:grid-cols-[1fr_260px]">
        <div>
          <h2 className="font-serif text-xl font-semibold">Synopsis</h2>
          <p className="mt-3 max-w-2xl leading-relaxed text-muted-foreground">{title.synopsis}</p>

          <h2 className="mt-10 font-serif text-xl font-semibold">Tropes &amp; themes</h2>
          <div className="mt-3 flex flex-wrap gap-2">
            {title.tropes.map((t) => (
              <span key={t} className="rounded-full border border-secondary/60 bg-secondary/10 px-3 py-1 text-sm text-secondary">
                {t}
              </span>
            ))}
          </div>
        </div>

        <aside className="space-y-4">
          <div className="rounded-xl border border-border bg-card p-5">
            <h3 className="text-sm font-medium text-muted-foreground">Details</h3>
            <dl className="mt-3 space-y-2 text-sm">
              <Detail label="Creator" value={title.creator} />
              <Detail label="Format" value={title.format} />
              <Detail label="Status" value={title.status} />
              <Detail label="Released" value={String(title.year)} />
              <Detail label="Length" value={title.length} />
            </dl>
          </div>
          <div className="rounded-xl border border-border bg-card p-5">
            <h3 className="text-sm font-medium text-muted-foreground">Tags</h3>
            <div className="mt-3 flex flex-wrap gap-2">
              {title.tags.map((tag) => (
                <Link
                  key={tag}
                  href="/encyclopedia"
                  className="rounded-full bg-muted px-2.5 py-1 text-xs text-muted-foreground transition-colors hover:text-foreground"
                >
                  {tag}
                </Link>
              ))}
            </div>
          </div>
        </aside>
      </div>

      {/* Related */}
      {related.length > 0 && (
        <section className="border-t border-border/70">
          <div className="mx-auto max-w-6xl px-4 py-12 sm:px-6">
            <h2 className="font-serif text-2xl font-semibold">You might also love</h2>
            <div className="mt-6 grid grid-cols-2 gap-4 md:grid-cols-4">
              {related.map((t) => (
                <TitleCard key={t.slug} title={t} />
              ))}
            </div>
          </div>
        </section>
      )}
    </article>
  )
}

function Meta({ icon, label }: { icon: React.ReactNode; label: string }) {
  return (
    <span className="inline-flex items-center gap-1.5 text-muted-foreground">
      {icon}
      <span className="text-foreground">{label}</span>
    </span>
  )
}

function Detail({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex items-center justify-between gap-4">
      <dt className="text-muted-foreground">{label}</dt>
      <dd className="text-right font-medium">{value}</dd>
    </div>
  )
}
