package net.quarxy.twitchBot.games;

import net.quarxy.twitchBot.Bot;
import net.quarxy.twitchBot.utils.BotUser;

import java.util.Random;

public class RussianRoulette extends Game {

    public RussianRoulette() {
        super("Russisches Roulette",
                "Bei das Game hier du geteimoutet wirst weil halt Glücksspiel!",
                6, 2);
    }


    @Override
    public void play() {
        if(getPlayers().size() >= getMinPlayers()) {
            int revolverBullet = new Random().nextInt(6);
            for(BotUser player : getPlayers()) {
                if(getPlayers().indexOf(player) == revolverBullet) {
                    Bot.getInstance().getTwitchClient().getChat().sendMessage("grifermob",
                            player.getTwitchUsername() + "hat den Abzug gezogen und - starb");
                    int bet = getPlayerBets().get(player);
                    player.removeFromBalance(bet);
                    removePlayer(player);
                    getPlayers().forEach(player2 -> player2.addToBalance((int) Math.floor((double)bet/getPlayers().size())));
                } else {
                    Bot.getInstance().getTwitchClient().getChat().sendMessage("grifermob",
                            player.getTwitchUsername() + "hat den Abzug gezogen und - überlebte");
                }
            }
        }
    }
}
