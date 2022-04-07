package net.quarxy.twitchBot.games;

import net.quarxy.twitchBot.utils.BotUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Game {

    private final String title;
    private final String description;
    private final int minPlayers;
    private final int maxPlayers;
    private final HashMap<BotUser, Integer> players = new HashMap<>();

    public Game(String title, String description, int minPlayers, int maxPlayers) {
        this.title = title;
        this.description = description;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public int getMinPlayers() {
        return this.minPlayers;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public List<BotUser> getPlayers() {
        return this.players.keySet().stream().toList();
    }

    public HashMap<BotUser, Integer> getPlayerBets() {
        return this.players;
    }

    public boolean addPlayer(BotUser player, int bet) {
        if(this.players.size() + 1 > this.maxPlayers || this.players.containsKey(player)) {
            return false;
        }
        this.players.put(player, bet);
        return true;
    }
    public boolean removePlayer(BotUser player) {
        if(!this.players.containsKey(player)) {
            return false;
        } else {
            this.players.remove(player);
            return true;
        }
    }

    public void checkStart() {
        if(this.players.size() == this.maxPlayers) {
            start();
        } else {
            start();
        }
    }

    public abstract void start();
}
