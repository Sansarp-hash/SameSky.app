export type TitleFormat = "Manga" | "Anime" | "Manhwa" | "Light Novel" | "Film"
export type TitleStatus = "Ongoing" | "Completed" | "Hiatus" | "Upcoming"

export type GLTitle = {
  slug: string
  title: string
  altTitle?: string
  format: TitleFormat
  status: TitleStatus
  year: number
  rating: number
  popularity: number
  creator: string
  cover: string
  tagline: string
  synopsis: string
  tags: string[]
  tropes: string[]
  length: string
  featured?: boolean
}

export const titles: GLTitle[] = [
  {
    slug: "bloom-into-twilight",
    title: "Bloom Into Twilight",
    altTitle: "Tasogare ni Saku",
    format: "Manga",
    status: "Ongoing",
    year: 2021,
    rating: 4.8,
    popularity: 98,
    creator: "Rei Amano",
    cover: "/images/covers/bloom-into-twilight.png",
    tagline: "Two florists, one closing shop, and a summer that refuses to end.",
    synopsis:
      "When quiet horticulture student Yuki takes a part-time job at a fading flower shop, she never expects to fall for its stubborn owner, Sora. As the shop's last season blooms, the two women learn that some feelings, like the rarest flowers, only open at dusk.",
    tags: ["Slice of Life", "Romance", "Adult", "Josei"],
    tropes: ["Slow Burn", "Age Gap", "Found Family"],
    length: "5 volumes",
    featured: true,
  },
  {
    slug: "constellations-between-us",
    title: "Constellations Between Us",
    altTitle: "Bokura no Seiza",
    format: "Anime",
    status: "Completed",
    year: 2022,
    rating: 4.6,
    popularity: 93,
    creator: "Studio Hoshi",
    cover: "/images/covers/constellations-between-us.png",
    tagline: "An astronomy club, a meteor shower, and a wish neither dared to say aloud.",
    synopsis:
      "Reserved transfer student Mika joins her school's dwindling astronomy club, where she meets the radiant club president Nao. Across late-night stargazing sessions and a looming club shutdown, the distance between two hearts begins to close.",
    tags: ["School Life", "Drama", "Romance"],
    tropes: ["Opposites Attract", "Club Activities", "Confession Arc"],
    length: "12 episodes",
    featured: true,
  },
  {
    slug: "the-lily-and-the-sea",
    title: "The Lily and the Sea",
    altTitle: "Yuri to Umi",
    format: "Light Novel",
    status: "Ongoing",
    year: 2020,
    rating: 4.7,
    popularity: 88,
    creator: "Haru Tsukishima",
    cover: "/images/covers/the-lily-and-the-sea.png",
    tagline: "A lighthouse keeper's daughter and the diver who washed ashore.",
    synopsis:
      "On a remote island where the tide keeps its own secrets, lonely lighthouse keeper's daughter Nagisa rescues a mysterious free diver. What begins as an unlikely friendship deepens into something as vast and uncharted as the ocean itself.",
    tags: ["Fantasy", "Romance", "Adventure"],
    tropes: ["Slow Burn", "Coastal Setting", "Mystery"],
    length: "8 volumes",
    featured: true,
  },
  {
    slug: "northern-star-cafe",
    title: "Northern Star Café",
    altTitle: "Hokkyokusei Kissaten",
    format: "Manga",
    status: "Completed",
    year: 2019,
    rating: 4.5,
    popularity: 81,
    creator: "Kanade Ito",
    cover: "/images/covers/northern-star-cafe.png",
    tagline: "Coffee, snowfall, and the barista who always remembers your order.",
    synopsis:
      "Overworked office worker Rin stumbles into a tiny café during a blizzard and is warmed by the gentle barista Yuu. Over countless cups of coffee, the café becomes the one place where both women can finally be themselves.",
    tags: ["Slice of Life", "Romance", "Comedy"],
    tropes: ["Workplace", "Comfort Story", "Winter"],
    length: "3 volumes",
  },
  {
    slug: "petals-of-aoi",
    title: "Petals of Aoi",
    altTitle: "Aoi no Hanabira",
    format: "Manhwa",
    status: "Ongoing",
    year: 2023,
    rating: 4.4,
    popularity: 76,
    creator: "Seo-yeon Park",
    cover: "/images/covers/petals-of-aoi.png",
    tagline: "A rival calligrapher, an inherited garden, and ink that never fades.",
    synopsis:
      "Ambitious art student Aoi inherits her late grandmother's garden studio, only to share it with her infuriatingly talented rival, Hana. Between competitions and quiet afternoons, rivalry blossoms into something neither can put into words.",
    tags: ["Drama", "Romance", "Art"],
    tropes: ["Rivals to Lovers", "Inheritance", "Slow Burn"],
    length: "60+ chapters",
  },
  {
    slug: "moonlit-confession",
    title: "Moonlit Confession",
    altTitle: "Tsukiyo no Kokuhaku",
    format: "Film",
    status: "Completed",
    year: 2024,
    rating: 4.9,
    popularity: 95,
    creator: "Dir. Emi Kurosawa",
    cover: "/images/covers/moonlit-confession.png",
    tagline: "One festival night. One rooftop. One truth left unspoken for years.",
    synopsis:
      "Childhood friends Sumi and Kaori reunite at their hometown's summer festival years after drifting apart. As lanterns rise over the river, a single moonlit night gives them the courage to confront the feelings they buried long ago.",
    tags: ["Drama", "Romance", "Coming of Age"],
    tropes: ["Childhood Friends", "Reunion", "Festival"],
    length: "1h 52m",
    featured: true,
  },
  {
    slug: "sugar-and-static",
    title: "Sugar & Static",
    altTitle: "Ame to Denpa",
    format: "Anime",
    status: "Ongoing",
    year: 2024,
    rating: 4.3,
    popularity: 72,
    creator: "Studio Prism",
    cover: "/images/covers/sugar-and-static.png",
    tagline: "A retiring idol and the sound engineer who hears her real voice.",
    synopsis:
      "Burnt-out idol Miku is ready to quit the industry until she meets Rei, the sound engineer who insists on capturing her true voice. Behind the glare of the stage, two women rediscover why they fell in love with music — and each other.",
    tags: ["Idol", "Drama", "Music", "Romance"],
    tropes: ["Idol Industry", "Slow Burn", "Behind the Scenes"],
    length: "24 episodes",
  },
  {
    slug: "the-astronomers-girlfriend",
    title: "The Astronomer's Girlfriend",
    altTitle: "Tenmonya no Kanojo",
    format: "Light Novel",
    status: "Hiatus",
    year: 2018,
    rating: 4.2,
    popularity: 64,
    creator: "Yuki Nakamura",
    cover: "/images/covers/the-astronomers-girlfriend.png",
    tagline: "She maps distant galaxies; she only wants to be seen.",
    synopsis:
      "Brilliant, distracted astrophysicist Dr. Sae spends her nights charting the cosmos, oblivious to her patient assistant Miyu who has loved her for years. A grant deadline forces them together and the closest star finally comes into focus.",
    tags: ["Science Fiction", "Romance", "Adult"],
    tropes: ["Oblivious Lead", "Workplace", "Pining"],
    length: "6 volumes",
  },
]

export const allTags = Array.from(new Set(titles.flatMap((t) => t.tags))).sort()
export const allFormats: TitleFormat[] = ["Manga", "Anime", "Manhwa", "Light Novel", "Film"]

export function getTitle(slug: string) {
  return titles.find((t) => t.slug === slug)
}

export function relatedTitles(slug: string) {
  const current = getTitle(slug)
  if (!current) return []
  return titles
    .filter((t) => t.slug !== slug)
    .map((t) => ({
      title: t,
      score: t.tags.filter((tag) => current.tags.includes(tag)).length,
    }))
    .sort((a, b) => b.score - a.score || b.title.popularity - a.title.popularity)
    .slice(0, 3)
    .map((r) => r.title)
}

/* ---------------- Community ---------------- */

export type CommunityPost = {
  id: string
  author: string
  handle: string
  avatarColor: string
  timeAgo: string
  title: string
  body: string
  tag: string
  upvotes: number
  comments: number
  relatedTitle?: string
}

export const communityPosts: CommunityPost[] = [
  {
    id: "p1",
    author: "Yuzu",
    handle: "@yuzu_reads",
    avatarColor: "var(--primary)",
    timeAgo: "2h",
    title: "Bloom Into Twilight vol. 5 wrecked me (spoiler-free)",
    body: "The greenhouse chapter is everything I wanted from this series. Rei Amano really understands that quiet longing hits harder than any grand confession. Who else is emotionally recovering?",
    tag: "Discussion",
    upvotes: 342,
    comments: 87,
    relatedTitle: "bloom-into-twilight",
  },
  {
    id: "p2",
    author: "Nova",
    handle: "@nova_gazes",
    avatarColor: "var(--secondary)",
    timeAgo: "5h",
    title: "Underrated GL light novels that deserve anime adaptations",
    body: "Making a list for newcomers. The Lily and the Sea is my #1 pick — the prose is gorgeous and the pacing would translate so well to screen. Drop your suggestions below and I'll compile them.",
    tag: "Recommendations",
    upvotes: 218,
    comments: 143,
    relatedTitle: "the-lily-and-the-sea",
  },
  {
    id: "p3",
    author: "Kae",
    handle: "@kae_draws",
    avatarColor: "var(--accent)",
    timeAgo: "8h",
    title: "Finished my Moonlit Confession fan illustration!",
    body: "Spent two weeks on this rooftop scene. The lighting in that film lives rent-free in my head. Posting the full piece in the Fan Art gallery — feedback welcome, I'm still learning color!",
    tag: "Fan Art",
    upvotes: 511,
    comments: 62,
    relatedTitle: "moonlit-confession",
  },
  {
    id: "p4",
    author: "Mira",
    handle: "@mira_watches",
    avatarColor: "var(--primary)",
    timeAgo: "12h",
    title: "Sugar & Static episode 8 — best voice acting of the season?",
    body: "The recording booth scene had zero dialogue for almost a full minute and it was still the most romantic thing I've watched all year. This studio gets it.",
    tag: "Discussion",
    upvotes: 176,
    comments: 41,
    relatedTitle: "sugar-and-static",
  },
]

export type Poll = {
  id: string
  question: string
  totalVotes: number
  options: { label: string; votes: number }[]
}

export const polls: Poll[] = [
  {
    id: "poll1",
    question: "What's your favorite GL trope?",
    totalVotes: 4820,
    options: [
      { label: "Slow Burn", votes: 2114 },
      { label: "Rivals to Lovers", votes: 1188 },
      { label: "Childhood Friends", votes: 902 },
      { label: "Found Family", votes: 616 },
    ],
  },
  {
    id: "poll2",
    question: "Best format for discovering new GL?",
    totalVotes: 3140,
    options: [
      { label: "Manga", votes: 1290 },
      { label: "Anime", votes: 1005 },
      { label: "Light Novel", votes: 512 },
      { label: "Manhwa", votes: 333 },
    ],
  },
]

/* ---------------- AI Lab ---------------- */

export type SignProfile = {
  sign: string
  symbol: string
  dates: string
  vibe: string
  match: string
}

export const zodiacProfiles: SignProfile[] = [
  { sign: "Aries", symbol: "\u2648", dates: "Mar 21 – Apr 19", vibe: "The bold confessor who never waits for the right moment.", match: "Leo" },
  { sign: "Taurus", symbol: "\u2649", dates: "Apr 20 – May 20", vibe: "The steady comfort read; slow burn incarnate.", match: "Cancer" },
  { sign: "Gemini", symbol: "\u264A", dates: "May 21 – Jun 20", vibe: "Witty banter energy and enemies-to-lovers chaos.", match: "Aquarius" },
  { sign: "Cancer", symbol: "\u264B", dates: "Jun 21 – Jul 22", vibe: "The devoted childhood-friend who remembers everything.", match: "Pisces" },
  { sign: "Leo", symbol: "\u264C", dates: "Jul 23 – Aug 22", vibe: "The radiant lead everyone quietly pines for.", match: "Sagittarius" },
  { sign: "Virgo", symbol: "\u264D", dates: "Aug 23 – Sep 22", vibe: "The oblivious genius too busy to notice the pining.", match: "Capricorn" },
  { sign: "Libra", symbol: "\u264E", dates: "Sep 23 – Oct 22", vibe: "The hopeless romantic who orchestrates the meet-cute.", match: "Gemini" },
  { sign: "Scorpio", symbol: "\u264F", dates: "Oct 23 – Nov 21", vibe: "Intense mutual pining with a tragic backstory.", match: "Cancer" },
  { sign: "Sagittarius", symbol: "\u2650", dates: "Nov 22 – Dec 21", vibe: "The free spirit who sweeps someone off their feet.", match: "Aries" },
  { sign: "Capricorn", symbol: "\u2651", dates: "Dec 22 – Jan 19", vibe: "The reserved senpai with a hidden soft heart.", match: "Virgo" },
  { sign: "Aquarius", symbol: "\u2652", dates: "Jan 20 – Feb 18", vibe: "The stargazer who loves in cosmic metaphors.", match: "Libra" },
  { sign: "Pisces", symbol: "\u2653", dates: "Feb 19 – Mar 20", vibe: "The dreamy artist confessing through fan art.", match: "Scorpio" },
]

export type MbtiMatch = {
  type: string
  nickname: string
  archetype: string
  pairsWith: string
}

export const mbtiProfiles: MbtiMatch[] = [
  { type: "INFP", nickname: "The Dreamer", archetype: "Writes the love confession letter but never sends it.", pairsWith: "ENFJ" },
  { type: "ENFJ", nickname: "The Protagonist", archetype: "Organizes the whole club just to spend time with her.", pairsWith: "INFP" },
  { type: "INTJ", nickname: "The Strategist", archetype: "Plans the perfect confession three chapters in advance.", pairsWith: "ENFP" },
  { type: "ENFP", nickname: "The Spark", archetype: "Chaotic bisexual disaster energy, beloved by all.", pairsWith: "INTJ" },
  { type: "ISFJ", nickname: "The Caretaker", archetype: "Remembers her coffee order and her birthday.", pairsWith: "ESTP" },
  { type: "ISTP", nickname: "The Cool One", archetype: "Says three words a chapter, all of them devastating.", pairsWith: "ESFJ" },
]

export type TarotCard = {
  name: string
  meaning: string
  glReading: string
}

export const tarotDeck: TarotCard[] = [
  { name: "The Star", meaning: "Hope, renewal, quiet faith", glReading: "A gentle new beginning is blooming. Reach for the person under the same sky." },
  { name: "The Lovers", meaning: "Union, choice, alignment", glReading: "A meaningful bond deepens. The choice you've been avoiding brings you closer." },
  { name: "The Moon", meaning: "Intuition, dreams, the unsaid", glReading: "Unspoken feelings surface tonight. Trust what your heart already knows." },
  { name: "The Sun", meaning: "Joy, clarity, warmth", glReading: "A radiant, honest connection lights the way forward. Let yourself be seen." },
  { name: "The Empress", meaning: "Nurture, growth, abundance", glReading: "Tend to the bond gently and it will flourish like a garden in spring." },
  { name: "Strength", meaning: "Courage, tenderness, patience", glReading: "Softness is your power. The courage to be vulnerable will be rewarded." },
  { name: "The Wheel", meaning: "Fate, cycles, serendipity", glReading: "A chance reunion is written in the stars. Old feelings return renewed." },
  { name: "The World", meaning: "Completion, belonging, home", glReading: "A journey comes full circle. You've found where — and with whom — you belong." },
]

export const stats = [
  { label: "Titles catalogued", value: "12,400+" },
  { label: "Community members", value: "86K" },
  { label: "Fan artworks", value: "31,200" },
  { label: "Daily discussions", value: "1,900" },
]
