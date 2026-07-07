"use client"

import { useState } from "react"
import Link from "next/link"
import type { CommunityPost } from "@/lib/data"
import { getTitle } from "@/lib/data"
import { cn } from "@/lib/utils"
import { ArrowBigUp, MessageCircle } from "lucide-react"

export function PostCard({ post }: { post: CommunityPost }) {
  const [upvoted, setUpvoted] = useState(false)
  const related = post.relatedTitle ? getTitle(post.relatedTitle) : undefined

  return (
    <article className="rounded-2xl border border-border bg-card p-5 transition-colors hover:border-primary/40">
      <div className="flex items-center gap-3">
        <span
          className="flex size-10 items-center justify-center rounded-full font-serif text-lg text-background"
          style={{ backgroundColor: post.avatarColor }}
          aria-hidden
        >
          {post.author.charAt(0)}
        </span>
        <div className="min-w-0">
          <p className="truncate text-sm font-medium text-card-foreground">{post.author}</p>
          <p className="truncate text-xs text-muted-foreground">
            {post.handle} · {post.timeAgo}
          </p>
        </div>
        <span className="ml-auto rounded-full border border-border px-3 py-1 text-xs text-muted-foreground">
          {post.tag}
        </span>
      </div>

      <h3 className="mt-4 font-serif text-xl text-card-foreground text-pretty">{post.title}</h3>
      <p className="mt-2 text-sm leading-relaxed text-muted-foreground">{post.body}</p>

      {related && (
        <Link
          href={`/encyclopedia/${related.slug}`}
          className="mt-3 inline-flex items-center gap-1 rounded-full bg-muted px-3 py-1 text-xs text-foreground transition-colors hover:bg-primary/20"
        >
          on {related.title}
        </Link>
      )}

      <div className="mt-4 flex items-center gap-2 border-t border-border pt-4">
        <button
          type="button"
          onClick={() => setUpvoted((v) => !v)}
          aria-pressed={upvoted}
          aria-label="Upvote post"
          className={cn(
            "flex items-center gap-1.5 rounded-full px-3 py-1.5 text-sm transition-colors",
            upvoted ? "bg-primary/20 text-primary" : "text-muted-foreground hover:bg-muted",
          )}
        >
          <ArrowBigUp className={cn("size-5", upvoted && "fill-primary")} aria-hidden />
          {(post.upvotes + (upvoted ? 1 : 0)).toLocaleString()}
        </button>
        <span className="flex items-center gap-1.5 rounded-full px-3 py-1.5 text-sm text-muted-foreground">
          <MessageCircle className="size-4" aria-hidden />
          {post.comments}
        </span>
      </div>
    </article>
  )
}
