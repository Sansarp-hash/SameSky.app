-- Supabase SQL Migration Script
-- Creates the 'series' table with the required columns and configures Row-Level Security (RLS)

CREATE TABLE IF NOT EXISTS series (
    id BIGSERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    image_url TEXT NOT NULL,
    priority TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Enable Row-Level Security (RLS) to protect the table
ALTER TABLE series ENABLE ROW LEVEL SECURITY;

-- Create a policy that allows anyone (anonymous public users) to read/select entries
CREATE POLICY "Allow public read access to series" ON series
    FOR SELECT TO public USING (true);

-- Insert initial Girls' Love (GL) series seed data if the table is empty
INSERT INTO series (title, description, image_url, priority)
SELECT 'The Secret of Us', 'A heart-wrenching second-chance romance between a doctor and an actress.', 'https://images.unsplash.com/photo-1518199266791-5375a83190b7?q=80&w=600', 'High'
WHERE NOT EXISTS (SELECT 1 FROM series WHERE title = 'The Secret of Us');

INSERT INTO series (title, description, image_url, priority)
SELECT 'GAP The Series', 'The pioneering blockbuster Thai GL series showcasing an office romance.', 'https://images.unsplash.com/photo-1516589178581-6cd7833ae3b2?q=80&w=600', 'High'
WHERE NOT EXISTS (SELECT 1 FROM series WHERE title = 'GAP The Series');

INSERT INTO series (title, description, image_url, priority)
SELECT '23.5', 'A charming high-school romance starting with an anonymous online crush under the alias Earth.', 'https://images.unsplash.com/photo-1464802686167-b939a6910659?q=80&w=600', 'Medium'
WHERE NOT EXISTS (SELECT 1 FROM series WHERE title = '23.5');
