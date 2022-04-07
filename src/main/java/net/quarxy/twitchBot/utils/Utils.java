package net.quarxy.twitchBot.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.twitch4j.tmi.domain.Chatters;
import net.quarxy.twitchBot.Bot;
import net.quarxy.twitchBot.utils.permissions.Permission;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utils {

    /*public static boolean hasModPermission(String userid) {
        return (Bot.getInstance().getTwitchClient().getHelix().getModerators("2hooxe55etc47yjxm1b99ezfkcnmc5",
                        "189073898", List.of(userid), "", 1).execute().getModerators().size() > 0 ||
                        userid.equals("189073898"));
    }*/

    public static Chatters getChatters(String channel) {
        return Bot.getInstance().getTwitchClient().getMessagingInterface().getChatters(channel).execute();
    }

    public static String getNoPermissionMessage(String username, Permission permission) {
        return "@" + username + " Leider bist du nicht qualifiziert, diese Kanalbelohnung zu nutzen. (Missing Permission: " + permission + ")";
    }

    public static String getDescription(String command) {
        return switch (command) {
            case "help" -> "Mit diesem befehl lässt sich diese Art von Hilfe privat an dich senden.\n\nNutze: !help <Befehl>\nBefehlsliste: !commands";
            case "commands" -> "Dieser Befehl sendet eine Liste an allen verfügbaren Befehlen.\n\nNutze: !commands";
            default -> "Dieser Befehl existiert nicht oder es gibt keine Hilfe für diesen.";
        };
    }

    public static String parseName(String argument) {
        return (argument.startsWith("@") ? argument.substring(1) : argument);
    }


}
