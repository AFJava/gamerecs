CREATE INDEX IF NOT EXISTS idx_game_developers_game_id
ON game_developers(game_id);

CREATE INDEX IF NOT EXISTS idx_game_franchises_game_id
ON game_franchises(game_id);

CREATE INDEX IF NOT EXISTS idx_game_game_modes_game_id
ON game_game_modes(game_id);

CREATE INDEX IF NOT EXISTS idx_game_keywords_game_id
ON game_keywords(game_id);

CREATE INDEX IF NOT EXISTS idx_game_platforms_game_id
ON game_platforms(game_id);

CREATE INDEX IF NOT EXISTS idx_game_player_perspectives_game_id
ON game_player_perspectives(game_id);

CREATE INDEX IF NOT EXISTS idx_game_publishers_game_id
ON game_publishers(game_id);

CREATE INDEX IF NOT EXISTS idx_game_tags_game_id
ON game_tags(game_id);