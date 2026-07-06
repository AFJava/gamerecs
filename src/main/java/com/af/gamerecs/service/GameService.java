package com.af.gamerecs.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.af.gamerecs.dto.IgdbGameDto;
import com.af.gamerecs.dto.NameDto;
import com.af.gamerecs.entities.Game;
import com.af.gamerecs.repositories.GameRepository;

@Service
public class GameService {
    public final GameRepository gameRepository;
    
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }
    
    /*
    public Game gameFromDto(RawgGameDto game) {
        return new Game(game.id(),
                        game.name(),
                        game.backgroundImage(),
                        game.released(),
                        game.metacritic());
    }
    */

    public Game gameFromDto(IgdbGameDto game) {
        LocalDate releaseDate = Instant.ofEpochSecond(game.first_release_date())
            .atZone(ZoneOffset.UTC)
            .toLocalDate();

        return new Game(game.id(),
                        game.name(),
                        game.cover().image_id(),
                        releaseDate,
                        new HashSet<>(franchiseNames(game)),
                        new HashSet<>(names(game.involved_companies())),
                        new HashSet<>(names(game.platforms())),
                        new HashSet<>(names(game.genres())),
                        new HashSet<>(names(game.themes())),
                        new HashSet<>(names(game.game_modes())),
                        new HashSet<>(names(game.player_perspectives())),
                        new HashSet<>(names(game.keywords())),
                        game.rating(),
                        game.rating_count());
    }

    public Optional<Game> getGame(Long igdbId) {
        return gameRepository.findByIgdbId(igdbId);
    }

    public Game saveGame(Game game) {
        return gameRepository.save(game);
    }

    public List<Game> getGamesFromIgdbIds(List<Long> igdbIds) {
        return gameRepository.findAllByIgdbIdIn(igdbIds);
    }

    private Set<String> names(List<NameDto> values) {
        if (values == null) {
            return new HashSet<>();
        }

        return values.stream()
                .filter(Objects::nonNull)
                .map(NameDto::name)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Set<String> franchiseNames(IgdbGameDto game) {
        Set<String> result = new HashSet<>();

        if (game.franchise() != null && game.franchise().name() != null) {
            result.add(game.franchise().name());
        }

        if (game.franchises() != null) {
            result.addAll(names(game.franchises()));
        }

        return result;
    }
}