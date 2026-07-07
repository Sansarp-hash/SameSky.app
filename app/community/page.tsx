import type { Metadata } from "next"
import { communityPosts, polls, stats } from "@/lib/data"
import { PostCard } from "@/components/post-card"
import { PollCard } from "@/components/poll-card"

export const metadata: Metadata = {
  title: "Community",
  description:
    "Join the SameSky community — discuss your favorite Girls' Love titles, vote in polls, share recommendations, and celebrate fan art together.",
}

export default function CommunityPage() {
  return (
    <div className="mx-auto max-w-6xl px-4 py-10 sm:px-6 lg:px-8">
      <header className="mb-10">
        <p className="text-sm font-medium uppercase tracking-widest text-primary">Community</p>
        <h1 className="mt-2 font-serif text-4xl text-foreground text-balance sm:text-5xl">Under the same sky</h1>
        <p className="mt-3 max-w-2xl text-muted-foreground leading-relaxed">
          A warm, inclusive space to talk about the GL stories you love. Share thoughts, drop recommendations, vote in
          weekly polls, and lift up fellow fans.
        </p>
      </header>

      <div className="grid gap-8 lg:grid-cols-[1fr_320px]">
        <section className="flex flex-col gap-5" aria-label="Community posts">
          {communityPosts.map((post) => (
            <PostCard key={post.id} post={post} />
          ))}
        </section>

        <aside className="flex flex-col gap-6">
          <div>
            <h2 className="mb-3 font-serif text-xl text-foreground">Weekly polls</h2>
            <div className="flex flex-col gap-4">
              {polls.map((poll) => (
                <PollCard key={poll.id} poll={poll} />
              ))}
            </div>
          </div>

          <div className="rounded-2xl border border-border bg-card p-5">
            <h2 className="mb-4 font-serif text-xl text-card-foreground">By the numbers</h2>
            <dl className="grid grid-cols-2 gap-4">
              {stats.map((s) => (
                <div key={s.label}>
                  <dt className="text-xs text-muted-foreground">{s.label}</dt>
                  <dd className="font-serif text-2xl text-primary">{s.value}</dd>
                </div>
              ))}
            </dl>
          </div>
        </aside>
      </div>
    </div>
  )
}
