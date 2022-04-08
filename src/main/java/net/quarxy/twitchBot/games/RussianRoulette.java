package net.quarxy.twitchBot.games;

import net.quarxy.twitchBot.Bot;
import net.quarxy.twitchBot.utils.BotUser;
import net.quarxy.twitchBot.utils.GameStorage;

import javax.swing.plaf.ButtonUI;
import java.time.Duration;
import java.util.Random;

public class RussianRoulette extends Game {

    private final int bet;

    public RussianRoulette(BotUser initiator, int bet) {
        super("Russisches Roulette",
                "Bei das Game hier du geteimoutet wirst weil halt Glücksspiel!",
                2, 2, initiator);
        this.bet = bet;
    }

    @Override
    public void start() {
        if(getPlayers().size() >= getMinPlayers()) {
            int revolverBullet = new Random().nextInt(3);
            BotUser died = null;
            for(BotUser player : getPlayers()) {
                if(getPlayers().indexOf(player) == revolverBullet) {
                    Bot.getInstance().getTwitchClient().getChat().sendMessage("grifermob",
                            "@" + player.getTwitchUsername() + " hat den Abzug gezogen und - starb");
                    died = player;
                } else {
                    Bot.getInstance().getTwitchClient().getChat().sendMessage("grifermob",
                            player.getTwitchUsername() + " hat den Abzug gezogen und - überlebte");
                }
            }
            if(died != null) {
                Bot.getInstance().getTwitchClient().getChat().timeout("grifermob", died.getTwitchUsername(), Duration.ofSeconds(30), "Russisches Roulette endet leider tödlich.");
                died.removeFromBalance(bet);
                Bot.getInstance().getTwitchClient().getChat().sendMessage("grifermob",
                        "@" + died.getTwitchUsername() + ": -" + bet);
                BotUser finalDied = died;
                getPlayers().forEach(player2 -> {
                    if(player2 != finalDied) {
                        player2.addToBalance((int) Math.floor((double) bet / (getPlayers().size()-1)));
                        Bot.getInstance().getTwitchClient().getChat().sendMessage("grifermob",
                                "@" + player2.getTwitchUsername() + ": +" + ((int) Math.floor((double) bet / (getPlayers().size()-1))));
                    }
                });
            }
            GameStorage.russianRoulette = null;
        }
    }

    public int getBet() {
        return bet;
    }
}
