import Link from "next/link"
import { ArrowRight, BookOpen, MessageCircle, Sparkles, Star, Users } from "lucide-react"
import { titles, stats } from "@/lib/data"
import { TitleCard } from "@/components/title-card"

export default function HomePage() {
  const featured = titles.filter((t) => t.featured)
  const trending = [...titles].sort((a, b) => b.popularity - a.popularity).slice(0, 4)

  return (
    <div>
      {/* Hero */}
      <section className="relative overflow-hidden border-b border-border/70">
        <div className="star-field pointer-events-none absolute inset-0 opacity-70" aria-hidden />
        <div
          className="pointer-events-none absolute inset-x-0 top-0 h-[420px] opacity-40"
          style={{
            background:
              "radial-gradient(60% 80% at 50% 0%, color-mix(in oklab, var(--primary) 40%, transparent), transparent 70%)",
          }}
          aria-hidden
        />
        <div className="relative mx-auto max-w-6xl px-4 py-20 text-center sm:px-6 sm:py-28">
          <span className="inline-flex items-center gap-2 rounded-full border border-border bg-card/60 px-4 py-1.5 text-xs text-muted-foreground backdrop-blur">
            <Sparkles className="size-3.5 text-accent" aria-hidden />
            The Girls&apos; Love encyclopedia &amp; community
          </span>
          <h1 className="mx-auto mt-6 max-w-3xl font-serif text-4xl font-semibold leading-tight text-balance sm:text-6xl">
            Every GL story, under the{" "}
            <span className="text-primary">same sky</span>
          </h1>
          <p className="mx-auto mt-5 max-w-xl text-pretty text-base leading-relaxed text-muted-foreground sm:text-lg">
            Discover and catalog Girls&apos; Love manga, anime, novels, and films.
            Join a community of fans, share art, and explore the stars in our AI Lab.
          </p>
          <div className="mt-8 flex flex-wrap items-center justify-center gap-3">
            <Link
              href="/encyclopedia"
              className="inline-flex items-center gap-2 rounded-full bg-primary px-6 py-3 text-sm font-medium text-primary-foreground transition-opacity hover:opacity-90"
            >
              Browse the encyclopedia
              <ArrowRight className="size-4" aria-hidden />
            </Link>
            <Link
              href="/community"
              className="inline-flex items-center gap-2 rounded-full border border-border bg-card px-6 py-3 text-sm font-medium transition-colors hover:border-primary/60"
            >
              Explore the community
            </Link>
          </div>
        </div>

        {/* Stats */}
        <div className="relative mx-auto grid max-w-6xl grid-cols-2 gap-px overflow-hidden border-t border-border/70 md:grid-cols-4">
          {stats.map((s) => (
            <div key={s.label} className="bg-card px-4 py-6 text-center">
              <div className="font-serif text-2xl font-semibold text-primary sm:text-3xl">{s.value}</div>
              <div className="mt-1 text-xs text-muted-foreground sm:text-sm">{s.label}</div>
            </div>
          ))}
        </div>
      </section>

      {/* Featured */}
      <section className="mx-auto max-w-6xl px-4 py-16 sm:px-6">
        <div className="flex items-end justify-between gap-4">
          <div>
            <h2 className="font-serif text-2xl font-semibold sm:text-3xl">Featured this season</h2>
            <p className="mt-1 text-sm text-muted-foreground">Hand-picked stories the community can&apos;t stop talking about.</p>
          </div>
          <Link href="/encyclopedia" className="hidden shrink-0 items-center gap-1 text-sm text-primary hover:underline sm:inline-flex">
            View all <ArrowRight className="size-4" aria-hidden />
          </Link>
        </div>
        <div className="mt-8 grid grid-cols-2 gap-4 md:grid-cols-4">
          {featured.map((t) => (
            <TitleCard key={t.slug} title={t} />
          ))}
        </div>
      </section>

      {/* Feature trio */}
      <section className="border-y border-border/70 bg-card/40">
        <div className="mx-auto grid max-w-6xl gap-6 px-4 py-16 sm:px-6 md:grid-cols-3">
          <FeatureCard
            icon={<BookOpen className="size-5" aria-hidden />}
            title="A living encyclopedia"
            body="Detailed entries for thousands of GL titles across manga, anime, novels, manhwa, and film — complete with tropes, ratings, and recommendations."
            href="/encyclopedia"
            cta="Start browsing"
          />
          <FeatureCard
            icon={<Users className="size-5" aria-hidden />}
            title="A community that gets it"
            body="Discuss the latest chapters, vote in polls, and share your fan art with readers who love the same stories you do."
            href="/community"
            cta="Join the conversation"
          />
          <FeatureCard
            icon={<Star className="size-5" aria-hidden />}
            title="The AI Lab"
            body="For fun: match your zodiac sign, MBTI type, and daily tarot pull to GL tropes and characters. Pure celestial self-indulgence."
            href="/ai-lab"
            cta="Read the stars"
          />
        </div>
      </section>

      {/* Trending */}
      <section className="mx-auto max-w-6xl px-4 py-16 sm:px-6">
        <div className="flex items-end justify-between gap-4">
          <h2 className="font-serif text-2xl font-semibold sm:text-3xl">Trending now</h2>
          <Link href="/encyclopedia" className="inline-flex shrink-0 items-center gap-1 text-sm text-primary hover:underline">
            View all <ArrowRight className="size-4" aria-hidden />
          </Link>
        </div>
        <div className="mt-8 grid grid-cols-2 gap-4 md:grid-cols-4">
          {trending.map((t) => (
            <TitleCard key={t.slug} title={t} />
          ))}
        </div>
      </section>

      {/* CTA */}
      <section className="mx-auto max-w-6xl px-4 pb-20 sm:px-6">
        <div className="relative overflow-hidden rounded-2xl border border-border bg-card p-8 text-center sm:p-14">
          <div className="star-field pointer-events-none absolute inset-0 opacity-60" aria-hidden />
          <div className="relative">
            <MessageCircle className="mx-auto size-8 text-primary" aria-hidden />
            <h2 className="mx-auto mt-4 max-w-xl font-serif text-2xl font-semibold text-balance sm:text-3xl">
              Found your next favorite? Someone&apos;s already talking about it.
            </h2>
            <p className="mx-auto mt-3 max-w-md text-sm text-muted-foreground">
              Sign up to track what you&apos;re reading, join discussions, and share your art with the SameSky community.
            </p>
            <Link
              href="/community"
              className="mt-6 inline-flex items-center gap-2 rounded-full bg-primary px-6 py-3 text-sm font-medium text-primary-foreground transition-opacity hover:opacity-90"
            >
              Join SameSky
              <ArrowRight className="size-4" aria-hidden />
            </Link>
          </div>
        </div>
      </section>
    </div>
  )
}

function FeatureCard({
  icon,
  title,
  body,
  href,
  cta,
}: {
  icon: React.ReactNode
  title: string
  body: string
  href: string
  cta: string
}) {
  return (
    <div className="flex flex-col rounded-xl border border-border bg-card p-6">
      <span className="grid size-11 place-items-center rounded-full bg-muted text-primary">{icon}</span>
      <h3 className="mt-4 font-serif text-lg font-semibold">{title}</h3>
      <p className="mt-2 flex-1 text-sm leading-relaxed text-muted-foreground">{body}</p>
      <Link href={href} className="mt-4 inline-flex items-center gap-1 text-sm text-primary hover:underline">
        {cta} <ArrowRight className="size-4" aria-hidden />
      </Link>
    </div>
  )
}
