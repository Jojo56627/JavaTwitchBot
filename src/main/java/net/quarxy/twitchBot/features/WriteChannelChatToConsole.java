package net.quarxy.twitchBot.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class WriteChannelChatToConsole {

    /**
     * Register events of this class with the EventManager/EventHandler
     *
     * @param eventHandler SimpleEventHandler
     */
    public WriteChannelChatToConsole(SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    /**
     * Subscribe to the ChannelMessage Event and write the output to the console
     */
    public void onChannelMessage(ChannelMessageEvent event) {
        System.out.printf(
                "Channel [%s] - User[%s] - Message ['%s']%n",
                event.getChannel().getName() + " - " + event.getChannel().getId(),
                event.getUser().getName(),
                event.getMessage()
        );
        event.getTwitchChat().sendMessage(event.getChannel().getName(),
                "Channel ["+event.getChannel().getName()+
                        "] - User["+event.getUser().getName()+
                        "] - Message ['"+event.getMessage()+"']");
    }

}
