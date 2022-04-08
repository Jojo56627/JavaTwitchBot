package net.quarxy.twitchBot.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import net.quarxy.twitchBot.Bot;
import net.quarxy.twitchBot.games.RussianRoulette;
import net.quarxy.twitchBot.utils.BotUser;
import net.quarxy.twitchBot.utils.GameStorage;
import net.quarxy.twitchBot.utils.Utils;
import net.quarxy.twitchBot.utils.permissions.Permission;

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
        BotUser messageSender;
        //System.out.println("Before: " + BotUser.getUsers());
        if(!BotUser.doesUserExist(e.getUser().getId())) {
            messageSender = new BotUser(e.getUser().getName(), e.getUser().getId());
        } else {
            messageSender = BotUser.get(e.getUser().getId());
        }
        BotUser.save();
        //System.out.println("After: " + BotUser.getUsers());
        if(e.getMessage().startsWith("!")) {
            String command = e.getMessage().split(" ")[0].substring(1);
            String[] arguments = Arrays.copyOfRange(e.getMessage().split(" "), 1, e.getMessage().split(" ").length);

            switch (command) {
                case "russianroulette", "rr" -> {
                    if(!messageSender.hasPermission(Permission.GAME_RUSSIAN_ROULETTE)) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), Utils.getNoPermissionMessage(messageSender.getTwitchUsername(), Permission.GAME_RUSSIAN_ROULETTE, true));
                        return;
                    }
                    if (arguments.length == 0) {
                        if(GameStorage.russianRoulette != null && messageSender == GameStorage.russianRoulette.getInitiator()) {
                            Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Nutze !russianroulette abort um das laufende Spiel abzubrechen.");
                        } else {
                            Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Bitte gib einen Einsatz an (z. B. !russionroulette 500)");
                        }
                    } else {
                        if(GameStorage.russianRoulette != null && messageSender == GameStorage.russianRoulette.getInitiator()) {
                            if(arguments[0].equalsIgnoreCase("abort")) {
                                GameStorage.russianRoulette = null;
                                Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Das Spiel 'Russisch Roulett' wurde abgebrochen.");
                            } else {
                                Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Nutze !russianroulette abort um das laufende Spiel abzubrechen.");
                            }
                        } else {
                            try {
                                int bet = Integer.parseInt(arguments[0]);
                                if (messageSender.getBalance() >= bet) {
                                    if (GameStorage.russianRoulette == null) {
                                        GameStorage.russianRoulette = new RussianRoulette(messageSender, bet);
                                    }
                                    if (GameStorage.russianRoulette.getBet() == bet) {
                                        if (GameStorage.russianRoulette.addPlayer(messageSender, bet)) {
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
                                            "(Kontostand: " + messageSender.getBalance() + ")");
                                }
                            } catch (NumberFormatException err) {
                                Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Bitte gib einen Einsatz an (z. B. !russionroulette 500)");
                            }
                        }
                    }
                }
                case "duel" -> {
                    if(!messageSender.hasPermission(Permission.GAME_DUEL)) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), Utils.getNoPermissionMessage(messageSender.getTwitchUsername(), Permission.GAME_DUEL, true));
                        return;
                    }
                    if (arguments.length == 0) {
                        if(GameStorage.doesDuelWithInitiatorExist(messageSender)) {
                            Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Nutze !duel abort um die laufende Anfrage zurückzuziehen.");
                        } else {
                            Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Bitte gib einen Spieler an.");
                        }
                    } else {
                        BotUser defender = BotUser.get(Utils.parseName(arguments[0]));
                        if(messageSender == defender) {
                            Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Du kannst nicht dich selbst herausfordern.");
                        } else {
                            if (GameStorage.doesDuelWithInitiatorExist(messageSender)) {
                                if (arguments[0].equalsIgnoreCase("abort")) {
                                    GameStorage.terminateDuel(messageSender);
                                    Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Deine laufende Anfrage für 'Duell' wurde zurückgezogen.");
                                } else {
                                    Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Nutze !duel abort um die laufende Anfrage zurückzuziehen.");
                                }
                            } else {
                                if (GameStorage.doesDuelWithInitiatorAndDefenderExist(defender, messageSender)) {
                                    Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Du hast die Herausforderung von " + defender.getTwitchUsername() +
                                            " angenommen.");
                                    GameStorage.getDuel(defender).start();
                                } else {
                                    GameStorage.createDuel(messageSender, defender);
                                    Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Du hast " + defender.getTwitchUsername() +
                                            " zu einem Duel herausgefordert.");
                                }
                            }
                        }
                    }
                }
                case "duelpoints", "dp" -> {
                    if(!messageSender.hasPermission(Permission.COMMAND_DUELPOINTS)) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), Utils.getNoPermissionMessage(messageSender.getTwitchUsername(), Permission.COMMAND_DUELPOINTS, true));
                        return;
                    }
                    if (arguments.length == 0) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Du besitzt " + messageSender.getDuelWins() + " Duell Punkte.");
                    } else {
                        BotUser another = BotUser.get(Utils.parseName(arguments[0]));
                        if (another == messageSender) {
                            Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Du besitzt " + messageSender.getDuelWins() + " Duell Punkte.");
                        } else {
                            Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), another.getTwitchUsername() + " besitzt " + another.getDuelWins() + " Duell Punkte.");
                        }
                    }
                }
                case "bal", "balance" -> {
                    if(!messageSender.hasPermission(Permission.COMMAND_BALANCE)) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), Utils.getNoPermissionMessage(messageSender.getTwitchUsername(), Permission.COMMAND_BALANCE, true));
                        return;
                    }
                    Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(),
                            "Du besitzt " + messageSender.getBalance() + "$.");
                }
                case "rob" -> {
                    if(!messageSender.hasPermission(Permission.COMMAND_ROB)) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), Utils.getNoPermissionMessage(messageSender.getTwitchUsername(), Permission.COMMAND_ROB, true));
                        return;
                    }
                    if (arguments.length == 0) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Bitte gib einen Nutzer ein, den du ausrauben möchtest.");
                    } else {
                        BotUser selfToRob = BotUser.get(Utils.parseName(arguments[0]));
                        if(selfToRob == messageSender) {
                            Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Du kannst nicht dich selbst berauben.");
                        } else {
                            int amountToRob = new Random().nextInt(500)+1;
                            if (Math.random() > 0.5) {
                                selfToRob.removeFromBalance(amountToRob);
                                messageSender.addToBalance(amountToRob);
                                Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Du hast " + amountToRob + "$ von " +
                                        (arguments[0].startsWith("@") ? arguments[0] : "@" + arguments[0]) + " geraubt.");
                            } else {
                                Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Leider ging der Raub schief.");
                            }
                        }
                    }
                }
                case "pay" -> {
                    if(!messageSender.hasPermission(Permission.COMMAND_PAY)) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), Utils.getNoPermissionMessage(messageSender.getTwitchUsername(), Permission.COMMAND_PAY, true));
                        return;
                    }
                    if (arguments.length == 0) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Bitte gib einen Nutzer ein, den du bezahlen möchtest.");
                    } else if (arguments.length == 1) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Bitte gib einen Betrag ein, den du bezahlen möchtest.");
                    } else {
                        if (messageSender.getBalance() >= Integer.parseInt(arguments[1])) {
                            try {
                                BotUser receiver = BotUser.get(Utils.parseName(arguments[0]));
                                messageSender.removeFromBalance(Integer.parseInt(arguments[1]));
                                receiver.addToBalance(Integer.parseInt(arguments[1]));
                                Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Du hast " + Utils.parseName(arguments[0]) +
                                        " erfolreich " + Integer.parseInt(arguments[1]) + "$ überwiesen.");
                            } catch (NumberFormatException err) {
                                Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Bitte gib einen Betrag an (z. B. !pay <User> 500)");
                            }
                        } else {
                            Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Du hast leider nicht genügend Geld. " +
                                    "(Kontostand: " + messageSender.getBalance() + ")");
                        }
                    }
                }
                case "modifyduelpoints", "mdp" -> {
                    if(!messageSender.hasPermission(Permission.COMMAND_MODIFYDUELPOINTS)) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), Utils.getNoPermissionMessage(messageSender.getTwitchUsername(), Permission.COMMAND_MODIFYDUELPOINTS, true));
                        return;
                    }
                    if (arguments.length == 0) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Bitte gib einen Nutzer ein, den du verändern möchtest.");
                    } else if (arguments.length == 1) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Bitte gib einen Betrag ein, den du setzen möchtest.");
                    } else {
                        try {
                            BotUser receiver = BotUser.get(Utils.parseName(arguments[0]));
                            int old = receiver.getDuelWins();
                            if(arguments[1].startsWith("-")) {
                                receiver.removeDuelWins(Integer.parseInt(arguments[1]));
                            } else if(arguments[1].startsWith("+")){
                                receiver.addDuelWins(Integer.parseInt(arguments[1]));
                            } else {
                                receiver.setDuelWins(Integer.parseInt(arguments[1]));
                            }
                            Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Du hast " + Utils.parseName(arguments[0]) +
                                    "'s Duelpunkte erfolgreich verändert. (" + old + " -> " + receiver.getDuelWins() + ")");
                        } catch (NumberFormatException err) {
                            Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Bitte gib einen Betrag an (z. B. !modifyduelpoints <User> 10)");
                        }
                    }
                }
                case "modifyeconomy", "meco" -> {
                    if(!messageSender.hasPermission(Permission.COMMAND_MODIFYECONOMY)) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), Utils.getNoPermissionMessage(messageSender.getTwitchUsername(), Permission.COMMAND_MODIFYECONOMY, true));
                        return;
                    }
                    if (arguments.length == 0) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Bitte gib einen Nutzer ein, den du verändern möchtest.");
                    } else if (arguments.length == 1) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Bitte gib einen Betrag ein, den du setzen möchtest.");
                    } else {
                        try {
                            BotUser receiver = BotUser.get(Utils.parseName(arguments[0]));
                            int old = receiver.getBalance();
                            if(arguments[1].startsWith("-")) {
                                receiver.removeFromBalance(Integer.parseInt(arguments[1]));
                            } else if(arguments[1].startsWith("+")){
                                receiver.addToBalance(Integer.parseInt(arguments[1]));
                            } else {
                                receiver.setBalance(Integer.parseInt(arguments[1]));
                            }
                            Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Du hast " + Utils.parseName(arguments[0]) +
                                    "'s Geldlevel erfolgreich verändert. (" + old + " -> " + receiver.getBalance() + ")");
                        } catch (NumberFormatException err) {
                            Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Bitte gib einen Betrag an (z. B. !modifyduelpoints <User> 10)");
                        }
                    }
                }
                case "modifypermissions", "mperm" -> {
                    if(!messageSender.hasPermission(Permission.COMMAND_MODIFY_PERMISSIONS)) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), Utils.getNoPermissionMessage(messageSender.getTwitchUsername(), Permission.COMMAND_MODIFY_PERMISSIONS, true));
                        return;
                    }
                    if (arguments.length == 0) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Bitte gib einen Nutzer ein, den du verändern möchtest.");
                    } else if (arguments.length == 1) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Bitte gib eine Permission ein, die du setzen möchtest.");
                    } else if (arguments.length == 2) {
                        Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Bitte gib einen Wert der Permission an.");
                    } else {
                        try {
                            BotUser receiver = BotUser.get(Utils.parseName(arguments[0]));
                            Permission permission = Permission.valueOf(arguments[1]);
                            boolean value = Boolean.parseBoolean(arguments[2]);
                            boolean b;
                            if(!receiver.hasPermissionOverwrite(permission)) {
                                receiver.setPermissionOverwrite(permission, value);
                                b = true;
                            } else {
                                receiver.removePermissionOverwrite(permission);
                                b = false;
                            }
                            Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Du hast " + Utils.parseName(arguments[0]) +
                                    "'s Rechte erfolgreich verändert. (" + (b ? "Gesetzt" : "Entfernt") + ")");
                        } catch (Exception err) {
                            Bot.getInstance().getTwitchClient().getChat().sendMessage(e.getChannel().getName(), "Es ist ein Fehler aufgetreten (Vermutlich nicht korrekte Argumente) " +
                                    "(z. B. !modifypermissions <User> <Permission> <Wert>");
                        }
                    }
                }
                case "save" -> {
                    BotUser.save();
                }
            }
        }

        /*System.out.printf(
                "Channel [%s] - User[%s] - Message ['%s']%n",
                e.getChannel().getName() + " - " + e.getChannel().getId(),
                e.getUser().getName(),
                e.getMessage()
        );*/
    }

}
