package net.quarxy.twitchBot.features;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.TwitchChatBuilder;
import com.github.twitch4j.eventsub.domain.RedemptionStatus;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import net.quarxy.twitchBot.Bot;
import net.quarxy.twitchBot.Launcher;
import net.quarxy.twitchBot.utils.Utils;
import net.quarxy.twitchBot.utils.permissions.Permission;
import net.quarxy.twitchBot.utils.permissions.Rank;
import okio.Utf8;

import java.time.Duration;
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
    public void onRewardRedeem(RewardRedeemedEvent e) {
        String channel = Bot.getInstance().getTwitchClient().getChat().getChannelIdToChannelName().get(e.getRedemption().getChannelId());
        System.out.println("Channel-Point-Reward redeemed: " + e.getRedemption().getReward().getTitle() + " (" + e.getRedemption().getReward().getId() + ")");
        String rewardID = e.getRedemption().getReward().getId();
        switch (rewardID) {
            case "fc148b09-30bb-4af6-a4d1-07e6d36cbdbc" -> {
                if(Rank.get(e.getRedemption().getUser().getLogin()).hasPermission(Permission.FEATURE_AUTO_TIMEOUT)) {
                    Bot.getInstance().getTwitchClient().getChat().timeout(channel, e.getRedemption().getUser().getLogin(), Duration.ofSeconds(45), "Einlösung der Kanalbelohnung");
                } else {
                    Bot.getInstance().getTwitchClient().getHelix().updateRedemptionStatus("0ruv4lzljoqs2n0bhbs3qslvbkfj9m", "189073898",
                            rewardID, List.of(e.getRedemption().getId()), RedemptionStatus.CANCELED).execute();
                    Bot.getInstance().getTwitchClient().getChat().sendMessage(
                            channel, Utils.getNoPermissionMessage(e.getRedemption().getUser().getDisplayName(), Permission.FEATURE_AUTO_TIMEOUT));
                }
            }
            case "f954709b-667b-467a-80d8-6049f1e7fa47" -> {
                if(Rank.get(e.getRedemption().getUser().getLogin()).hasPermission(Permission.FEATURE_30DAYS_VIP)) {
                    Bot.getInstance().getBroadcasterChat().sendMessage(channel, "Herzlichen Glückwunsch @" + e.getRedemption().getUser().getDisplayName()
                            + "! Du bist nun für 30 Tage VIP blobDance");
                    Bot.getInstance().getBroadcasterChat().sendMessage(channel, "/vip @" + e.getRedemption().getUser().getDisplayName());
                } else {
                    Bot.getInstance().getTwitchClient().getHelix().updateRedemptionStatus("0ruv4lzljoqs2n0bhbs3qslvbkfj9m", "189073898",
                            rewardID, List.of(e.getRedemption().getId()), RedemptionStatus.CANCELED).execute();
                    Bot.getInstance().getTwitchClient().getChat().sendMessage(
                            channel, Utils.getNoPermissionMessage(e.getRedemption().getUser().getDisplayName(), Permission.FEATURE_AUTO_TIMEOUT));
                }
            }
        }
    }
}
