-- Supabase SQL Migration Script for Watchlists
-- Creates the 'watchlists' table, sets up constraints, and creates optimized indexes for ultra-fast querying.

CREATE TABLE IF NOT EXISTS watchlists (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    tmdb_id INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT unique_user_movie UNIQUE (user_id, tmdb_id)
);

-- Optimize queries searching for a specific user's watchlist items (Crucial for My Watchlist fetching)
CREATE INDEX IF NOT EXISTS idx_watchlists_user_id ON watchlists(user_id);

-- Optimize checks to see if a specific movie exists in any watchlist
CREATE INDEX IF NOT EXISTS idx_watchlists_tmdb_id ON watchlists(tmdb_id);

-- Enable Row-Level Security (RLS)
ALTER TABLE watchlists ENABLE ROW LEVEL SECURITY;

-- Create Policies for authenticated users to manage their own watchlists
CREATE POLICY "Allow authenticated read of own watchlist" ON watchlists
    FOR SELECT TO authenticated USING (auth.uid() = user_id);

CREATE POLICY "Allow authenticated insert of own watchlist" ON watchlists
    FOR INSERT TO authenticated WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Allow authenticated delete of own watchlist" ON watchlists
    FOR DELETE TO authenticated USING (auth.uid() = user_id);
