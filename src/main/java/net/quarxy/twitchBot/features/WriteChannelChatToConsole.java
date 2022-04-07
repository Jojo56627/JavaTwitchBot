package net.quarxy.twitchBot.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import net.quarxy.twitchBot.Bot;
import net.quarxy.twitchBot.games.RussianRoulette;
import net.quarxy.twitchBot.utils.BotUser;
import net.quarxy.twitchBot.utils.GameStorage;
import net.quarxy.twitchBot.utils.Utils;

import java.util.Arrays;
import java.util.Random;

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
                            int bet = Integer.parseInt(arguments[0]);
                            if(user.getBalance() >= bet) {
                                if(GameStorage.russianRoulette == null) {
                                    GameStorage.russianRoulette = new RussianRoulette(bet);
                                }
                                if(GameStorage.russianRoulette.getBet() == bet) {
                                    if (GameStorage.russianRoulette.addPlayer(user, bet)) {
                                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Du hast " + bet +
                                                "$ auf dein Überleben gesetzt.");
                                        GameStorage.russianRoulette.checkStart();
                                    } else {
                                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(),
                                                "Entweder ist das Spiel bereits voll oder du bist bereits gejoint.");
                                    }
                                } else {
                                    Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(),
                                            "Dieses Spiel benötigt einen Einsatz von " + GameStorage.russianRoulette.getBet() + "$.");
                                }
                            } else {
                                Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Du hast leider nicht genügend Geld. " +
                                        "(Kontostand: " + user.getBalance() + ")");
                            }
                        } catch (NumberFormatException err) {
                            Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Bitte gib einen Einsatz an (z. B. !russionroulette 500)");
                        }
                    }
                }
                case "bal", "balance" -> {
                    Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(),
                            "Du besitzt " + BotUser.get(e.getUser().getId()).getBalance() + "$.");
                }
                case "rob" -> {
                    if (arguments.length == 0) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Bitte gib einen Nutzer ein, den du ausrauben möchtest.");
                    } else {
                        BotUser userToRob = BotUser.get(Utils.parseName(arguments[0]));
                        BotUser self = BotUser.get(e.getUser().getId());
                        if(userToRob == self) {
                            Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Du kannst nicht dich selbst berauben.");
                        } else {
                            int amountToRob = new Random().nextInt(500)+1;
                            if (Math.random() > 0.5) {
                                userToRob.removeFromBalance(amountToRob);
                                BotUser.get(e.getUser().getId()).addToBalance(amountToRob);
                                Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Du hast " + amountToRob + "$ von " +
                                        (arguments[0].startsWith("@") ? arguments[0] : "@" + arguments[0]) + " geraubt.");
                            } else {
                                Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Leider ging der Raub schief.");
                            }
                        }
                    }
                }
                case "pay" -> {
                    if (arguments.length == 0) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Bitte gib einen Nutzer ein, den du bezahlen möchtest.");
                    } else if (arguments.length == 1) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Bitte gib einen Betrag ein, den du bezahlen möchtest.");
                    } else {
                        try {
                            BotUser sender = BotUser.get(e.getUser().getId());
                            BotUser receiver = BotUser.get(Utils.parseName(arguments[0]));
                            sender.removeFromBalance(Integer.parseInt(arguments[1]));
                            receiver.addToBalance(Integer.parseInt(arguments[1]));
                            Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Du hast " + Utils.parseName(arguments[0]) +
                                    " erfolreich " + Integer.parseInt(arguments[1]) + "$ überwiesen.");
                        } catch (NumberFormatException err) {
                            Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Bitte gib einen Betrag an (z. B. !pay <User> 500)");
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
