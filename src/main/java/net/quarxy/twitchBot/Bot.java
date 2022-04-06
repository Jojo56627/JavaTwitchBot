package net.quarxy.twitchBot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import net.quarxy.twitchBot.features.CustomChannelPointRewards;
import net.quarxy.twitchBot.features.WriteChannelChatToConsole;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Bot {

    /**
     * Holds the Bot Configuration
     */
    private Configuration configuration;

    /**
     * Twitch4J API
     */
    private final TwitchClient twitchClient;

    /**
     * Holds the Credentials of the bot's twitch-account
     */
    private final OAuth2Credential credential;

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
        //endregion

        //region TwitchClient
        twitchClient = clientBuilder
                .withClientId(configuration.getApi().get("twitch_client_id"))
                .withClientSecret(configuration.getApi().get("twitch_client_secret"))
                .withEnableHelix(true)
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
    }

    public TwitchClient getTwitchClient() {
        return twitchClient;
    }
}
