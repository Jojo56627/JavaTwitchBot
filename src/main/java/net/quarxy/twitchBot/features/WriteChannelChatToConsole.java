package net.quarxy.twitchBot.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import net.quarxy.twitchBot.Bot;
import net.quarxy.twitchBot.utils.BotUser;
import net.quarxy.twitchBot.utils.Utils;

import java.util.Arrays;

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
    public void onChannelMessage(ChannelMessageEvent e) {
        if(!BotUser.doesUserExist(e.getUser().getId())) {
            new BotUser(e.getUser().getName(), e.getUser().getId());
        }
        if(e.getMessage().startsWith("!")) {
            String command = e.getMessage().split(" ")[0].substring(1);
            System.out.println(command);
            String[] arguments = Arrays.copyOfRange(e.getMessage().split(" "), 1, e.getMessage().split(" ").length);
            switch (command) {
                case "help" -> {
                    /*if (arguments.length == 0) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Bitte gib einen Befehl an. (!help <Befehl>)" +
                                " oder nutze !commands, um alle Befehle einzublenden");
                    } else {
                        String description;
                        if (arguments[0].startsWith("!")) {
                            description = Utils.getDescription(arguments[0].substring(1));
                        } else {
                            description = Utils.getDescription(arguments[0]);
                        }
                        for (String line : description.split("\n")) {
                            System.out.println(line);
                            Bot.getInstance().getTwitchClient().getChat().sendPrivateMessage(e.getUser().getId(), line);
                        }
                    }*/
                    Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Hilfe: ");
                    Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "!russianroulette - Glückspiel um 30 Sekunden Timeout");
                }

                case "russianroulette", "rr" -> {
                    if (arguments.length == 0) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Bitte gib einen Einsatz an (z. B. !russionroulette 500)");
                    } else {
                        BotUser user = BotUser.get(e.getUser().getId());
                        try {
                            int einsatz = Integer.parseInt(arguments[0]);
                            if(user.getBalance() >= einsatz) {

                            } else {
                                Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Du hast leider nicht genügend Geld. " +
                                        "(Kontostand: " + user.getBalance() + ")");
                            }
                        } catch (NumberFormatException err) {
                            Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Bitte gib einen Einsatz an (z. B. !russionroulette 500)");
                        }
                    }
                }
            }
        }

        System.out.printf(
                "Channel [%s] - User[%s] - Message ['%s']%n",
                e.getChannel().getName() + " - " + e.getChannel().getId(),
                e.getUser().getName(),
                e.getMessage()
        );
    }

}
