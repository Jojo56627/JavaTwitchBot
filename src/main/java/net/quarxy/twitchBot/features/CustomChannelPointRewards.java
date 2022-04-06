package net.quarxy.twitchBot.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import net.quarxy.twitchBot.Launcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomChannelPointRewards {

    /**
     * Register events of this class with the EventManager/EventHandler
     *
     * @param eventHandler SimpleEventHandler
     */
    public CustomChannelPointRewards(SimpleEventHandler eventHandler) {
        eventHandler.onEvent(RewardRedeemedEvent.class, this::onRewardRedeem);
    }

    /**
     * Subscribe to the RewardRedeemed Event
     */
    public void onRewardRedeem(RewardRedeemedEvent event) {
        switch (event.getRedemption().getReward().getId()) {
            case "1cfff4e1-f11f-4b35-8b8c-8b1d9c0d734e" -> {
                System.out.println("Mods: " + Launcher.getBot().getTwitchClient().getHelix().getModerators("2hooxe55etc47yjxm1b99ezfkcnmc5",
                        "189073898", new ArrayList<>(List.of(event.getRedemption().getUser().getId())), "", 1).execute().getModerators());
            }
        }
    }

}
