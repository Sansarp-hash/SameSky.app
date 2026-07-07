import type { Metadata } from "next"
import { Geist, Fraunces } from "next/font/google"
import { SiteHeader } from "@/components/site-header"
import { SiteFooter } from "@/components/site-footer"
import "./globals.css"

const geistSans = Geist({
  subsets: ["latin"],
  variable: "--font-geist-sans",
})

const fraunces = Fraunces({
  subsets: ["latin"],
  variable: "--font-fraunces",
})

export const metadata: Metadata = {
  title: "SameSky — The Girls' Love Encyclopedia & Community",
  description:
    "SameSky is the centralized encyclopedia and fan community for Girls' Love (GL) stories — browse titles, join the conversation, and explore the AI Lab.",
  keywords: ["Girls' Love", "GL", "Yuri", "encyclopedia", "community", "SameSky"],
  openGraph: {
    title: "SameSky — The Girls' Love Encyclopedia & Community",
    description:
      "Browse GL titles, join the community, and explore astrology, MBTI, and tarot in the AI Lab.",
    type: "website",
  },
}

export const viewport = {
  themeColor: "#191317",
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en" className={`${geistSans.variable} ${fraunces.variable} bg-background`}>
      <body className="font-sans antialiased">
        <SiteHeader />
        <main className="min-h-screen">{children}</main>
        <SiteFooter />
      </body>
    </html>
  )
}
