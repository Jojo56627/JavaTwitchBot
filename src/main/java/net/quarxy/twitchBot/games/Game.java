package net.quarxy.twitchBot.games;

import net.quarxy.twitchBot.utils.BotUser;

import java.util.ArrayList;
import java.util.List;

public abstract class Game {

    private final String title;
    private final String description;
    private final int maxPlayers;
    private final List<BotUser> players = new ArrayList<>();

    public Game(String title, String description, int maxPlayers) {
        this.title = title;
        this.description = description;
        this.maxPlayers = maxPlayers;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public List<BotUser> getPlayers() {
        return this.players;
    }

    public boolean addPlayer(BotUser player) {
        if(this.players.size() + 1 > this.maxPlayers) {
            return false;
        }
        return this.players.add(player);
    }

    public abstract void player();
}
