package net.quarxy.twitchBot.games;

import net.quarxy.twitchBot.Bot;
import net.quarxy.twitchBot.utils.BotUser;

import java.util.Random;

public class Duel extends Game {

    private final BotUser defender;

    public Duel(BotUser initiator, BotUser defender) {
        super("Duel", "Bei diesem Spiel kannst du um Geld wetten", 2, 2, initiator);
        this.defender = defender;
    }

    public BotUser getDefender() {
        return defender;
    }

    public static double calculateWinProbabilityOfAttacker(BotUser attacker, BotUser defender) {
        return ((double) attacker.getDuelWins()/(attacker.getDuelWins()+defender.getDuelWins()));
    }

    @Override
    public void start() {
        double winProbability = calculateWinProbabilityOfAttacker(getInitiator(), getDefender());
        System.out.println(winProbability);
        if(Math.random() <= winProbability) {
            getInitiator().addToBalance((int) Math.floor(100-(winProbability*100)));
            getDefender().removeFromBalance((int) Math.floor(100-(winProbability*100)));
            Bot.getInstance().getTwitchClient().getChat().sendMessage("grifermob", "Der Angriff " +
                    "war erfolgreich");
            getInitiator().addDuelWins(1);
            getDefender().removeDuelWins(1);
        } else {
            getInitiator().removeFromBalance((int) Math.floor(winProbability*100));
            getDefender().removeFromBalance((int) Math.floor(winProbability*100));
            Bot.getInstance().getTwitchClient().getChat().sendMessage("grifermob", "Der Angriff " +
                    "wurde vereitelt");
            getDefender().addDuelWins(1);
            getInitiator().removeDuelWins(1);
        }
    }
}
