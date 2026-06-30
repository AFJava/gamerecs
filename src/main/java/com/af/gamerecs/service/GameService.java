package com.af.gamerecs.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.af.gamerecs.dto.RawgGameDto;
import com.af.gamerecs.entities.Game;
import com.af.gamerecs.repositories.GameRepository;

@Service
public class GameService {
    public final GameRepository gameRepository;
    
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Game gameFromDto(RawgGameDto game) {
        return new Game(game.id(),
                        game.name(),
                        game.backgroundImage(),
                        game.released(),
                        game.metacritic());
    }

    /*
    public Game gameFromDto(RawgGameDto game) {
        return new Game(game.rawgId(),
                        game.name(),
                        game.backgroundImage(),
                        game.genres(),
                        game.tags(),
                        game.developers(),
                        game.publishers(),
                        game.platforms(),
                        game.released(),
                        game.metacritic());
    }
    */

    public Optional<Game> getGame(Long rawgId) {
        return gameRepository.findByRawgId(rawgId);
    }

    public Game saveGame(Game game) {
        return gameRepository.save(game);
    }
}