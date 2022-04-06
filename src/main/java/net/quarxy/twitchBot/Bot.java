package net.quarxy.twitchBot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactoryBuilder;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.TwitchChatBuilder;
import com.github.twitch4j.eventsub.domain.Reward;
import com.github.twitch4j.helix.domain.CustomReward;
import com.github.twitch4j.pubsub.domain.ChannelPointsReward;
import com.github.twitch4j.tmi.domain.Chatters;
import net.quarxy.twitchBot.features.CustomChannelPointRewards;
import net.quarxy.twitchBot.features.WriteChannelChatToConsole;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Bot {

    private static Bot instance;

    /**
     * Holds the Bot Configuration
     */
    private Configuration configuration;

    /**
     * Twitch4J API
     */
    private final TwitchClient twitchClient;

    /**
     * Holds the Credentials of the Bot's twitch-account and the Broadcaster's Account
     */
    private final OAuth2Credential credential;
    private final OAuth2Credential broadcaster_credential;

    /**
     * Constructor
     */
    public Bot() {

        // Load Configuration
        loadConfiguration();

        TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();

        //region Auth
        credential = new OAuth2Credential(
                "twitch",
                configuration.getCredentials().get("irc")
        );

        //ChatAuth of Broadcaster
        broadcaster_credential = new OAuth2Credential(
                "twitch",
                configuration.getCredentials().get("irc_broadcaster")
        );

        //endregion

        //region TwitchClient
        twitchClient = clientBuilder
                /*
                 * Connection to the Twitch-API Application
                 */
                .withClientId(configuration.getApi().get("twitch_client_id"))
                .withClientSecret(configuration.getApi().get("twitch_client_secret"))
                .withEnableHelix(true)

                /*
                 * TMI Module to get Viewers
                 */
                .withEnableTMI(true)
                /*
                 * Chat Module
                 * Joins irc and triggers all chat based events (viewer join/leave/sub/bits/gifted subs/...)
                 */
                .withChatAccount(credential)
                .withDefaultAuthToken(credential)
                .withEnableChat(true)

                /*
                 * GraphQL has a limited support
                 * Don't expect a bunch of features enabling it
                 */
                .withEnableGraphQL(true)
                /*
                 * Kraken is going to be deprecated
                 * see : https://dev.twitch.tv/docs/v5/#which-api-version-can-you-use
                 * It is only here so you can call methods that are not (yet)
                 * implemented in Helix
                 */
                .withEnableKraken(true)
                /*
                * PubSub is used to subscribe to not-irc events
                */
                .withEnablePubSub(true)
                /*
                 * Build the TwitchClient Instance
                 */
                .build();

        //create instance
        instance = this;

        //endregion
    }

    /**
     * Method to register all features
     */
    public void registerFeatures() {
		SimpleEventHandler eventHandler = twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class);

        // Register Event-based features
        CustomChannelPointRewards customChannelPointRewards = new CustomChannelPointRewards(eventHandler);
        WriteChannelChatToConsole writeChannelChatToConsole = new WriteChannelChatToConsole(eventHandler);
    }

    /**
     * Load the Configuration
     */
    private void loadConfiguration() {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("config.yml");

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            configuration = mapper.readValue(is, Configuration.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Unable to load Configuration ... Exiting.");
            System.exit(1);
        }
    }

    public void start() {
        // Connect to all channels
        for (String channel : configuration.getChannels()) {
            twitchClient.getChat().joinChannel(channel);
        }
        twitchClient.getPubSub().listenForChannelPointsRedemptionEvents(credential, "189073898");

/*
        Chatters chatters = twitchClient.getMessagingInterface().getChatters("grifermob").execute();

        System.out.println("Broadcaster: " + chatters.getBroadcaster());
        System.out.println("VIPs: " + chatters.getVips());
        System.out.println("Mods: " + chatters.getModerators());
        System.out.println("Admins: " + chatters.getAdmins());
        System.out.println("Staff: " + chatters.getStaff());
        System.out.println("Viewers: " + chatters.getViewers());
        System.out.println("All Viewers (sum of the above): " + chatters.getAllViewers());*/
    }

    public TwitchClient getTwitchClient() {
        return twitchClient;
    }

    public static Bot getInstance() {
        return instance;
    }

    public void createRewards() {

/*
*         CustomReward customReward = CustomReward.builder()
                .broadcasterId("189073898")
                .broadcasterLogin("grifermob")
                .broadcasterName("Grifermob")
                .title("Time Out")
                .prompt("Du hast die Ehre von mir oder einem meiner Mods für 45 Sekunden ins Wunderland geschickt zu werden.")
                .cost(500)
                .isEnabled(true)
                .backgroundColor("#BEFF00")
                .isUserInputRequired(false)
                .maxPerStreamSetting(new CustomReward.MaxPerStreamSetting().toBuilder().isEnabled(false).build())
                .globalCooldownSetting(new CustomReward.GlobalCooldownSetting().toBuilder().isEnabled(false).build())
                .maxPerUserPerStreamSetting(new CustomReward.MaxPerUserPerStreamSetting().toBuilder().isEnabled(false).build())
                .isPaused(false)
                .isInStock(true)
                .shouldRedemptionsSkipRequestQueue(false)
                .redemptionsRedeemedCurrentStream(null)
                .cooldownExpiresAt(null)
                .build();
        twitchClient.getHelix().createCustomReward("0ruv4lzljoqs2n0bhbs3qslvbkfj9m", "189073898", customReward).execute();*/
        CustomReward customReward = CustomReward.builder()
                .broadcasterId("189073898")
                .broadcasterLogin("grifermob")
                .broadcasterName("Grifermob")
                .title("30 Tage VIP")
                .prompt("Solltest du als erstes 100k erreichen bekommst du VIP (Es sind 3 plätze frei). Der Status VIP ist für 30 Tage verfügbar.")
                .cost(10)
                .isEnabled(true)
                .backgroundColor("#FF05D2")
                .isUserInputRequired(false)
                .maxPerStreamSetting(new CustomReward.MaxPerStreamSetting().toBuilder().isEnabled(false).build())
                .globalCooldownSetting(new CustomReward.GlobalCooldownSetting().toBuilder().isEnabled(false).build())
                .maxPerUserPerStreamSetting(new CustomReward.MaxPerUserPerStreamSetting().toBuilder().isEnabled(false).build())
                .isPaused(false)
                .isInStock(true)
                .shouldRedemptionsSkipRequestQueue(false)
                .redemptionsRedeemedCurrentStream(null)
                .cooldownExpiresAt(null)
                .build();
        twitchClient.getHelix().createCustomReward("0ruv4lzljoqs2n0bhbs3qslvbkfj9m", "189073898", customReward).execute();
    }

    public TwitchChat getBroadcasterChat() {
        return TwitchChatBuilder.builder().withChatAccount(broadcaster_credential).withAutoJoinOwnChannel(true).build();
    }
}
