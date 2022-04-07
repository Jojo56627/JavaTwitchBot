package net.quarxy.twitchBot.games;

import net.quarxy.twitchBot.Bot;
import net.quarxy.twitchBot.utils.BotUser;

import java.util.Random;

public class Duel extends Game {

    public Duel(String title, String description, int minPlayers, int maxPlayers) {
        super("Duel", "Bei diesem Spiel kannst du um Geld wetten", 2, 2);
    }

    public static double calculateWinProbabilityOfAttacker(BotUser attacker, BotUser defender) {
        return ((double) attacker.getDuelWins()/(attacker.getDuelWins()+defender.getDuelWins()));
    }

    @Override
    public void play() {
        double winProbability = calculateWinProbabilityOfAttacker(getPlayers().get(0), getPlayers().get(1));
        if(Math.random() <= winProbability) {
            getPlayers().get(0).addToBalance((int) Math.floor(100-winProbability));
            getPlayers().get(1).removeFromBalance((int) Math.floor(100-winProbability));
            Bot.getInstance().getTwitchClient().getChat().sendMessage("grifermob", "Der Angriff " +
                    "war erfolgreich");
        } else {
            getPlayers().get(0).addToBalance((int) Math.floor(100-winProbability));
            getPlayers().get(1).removeFromBalance((int) Math.floor(100-winProbability));
            Bot.getInstance().getTwitchClient().getChat().sendMessage("grifermob", "Der Angriff " +
                    "wurde vereitelt");
        }
    }
}
