package net.quarxy.twitchBot.utils.permissions;

import net.quarxy.twitchBot.utils.BotUser;
import net.quarxy.twitchBot.utils.Utils;

import java.util.HashMap;

public enum Rank {
    BROADCASTER(new HashMap<>(){{
        put(Permission.FEATURE_AUTO_TIMEOUT, false);
        put(Permission.FEATURE_30DAYS_VIP, false);
        put(Permission.GAME_RUSSIAN_ROULETTE, true);
        put(Permission.GAME_DUEL, true);

        put(Permission.COMMAND_DUELPOINTS, true);
        put(Permission.COMMAND_BALANCE, true);
        put(Permission.COMMAND_ROB, true);
        put(Permission.COMMAND_PAY, true);
        put(Permission.COMMAND_MODIFYECONOMY, true);
        put(Permission.COMMAND_MODIFYDUELPOINTS, true);
        put(Permission.COMMAND_MODIFY_PERMISSIONS, true);
    }}),
    MODERATOR(new HashMap<>(){{
        put(Permission.FEATURE_AUTO_TIMEOUT, false);
        put(Permission.FEATURE_30DAYS_VIP, false);
        put(Permission.GAME_RUSSIAN_ROULETTE, true);
        put(Permission.GAME_DUEL, true);

        put(Permission.COMMAND_DUELPOINTS, true);
        put(Permission.COMMAND_BALANCE, true);
        put(Permission.COMMAND_ROB, true);
        put(Permission.COMMAND_PAY, true);
        put(Permission.COMMAND_MODIFYECONOMY, false);
        put(Permission.COMMAND_MODIFYDUELPOINTS, false);
        put(Permission.COMMAND_MODIFY_PERMISSIONS, false);
    }}),
    VIP(new HashMap<>(){{
        put(Permission.FEATURE_AUTO_TIMEOUT, true);
        put(Permission.FEATURE_30DAYS_VIP, true);
        put(Permission.GAME_RUSSIAN_ROULETTE, true);
        put(Permission.GAME_DUEL, true);

        put(Permission.COMMAND_DUELPOINTS, true);
        put(Permission.COMMAND_BALANCE, true);
        put(Permission.COMMAND_ROB, true);
        put(Permission.COMMAND_PAY, true);
        put(Permission.COMMAND_MODIFYECONOMY, false);
        put(Permission.COMMAND_MODIFYDUELPOINTS, false);
        put(Permission.COMMAND_MODIFY_PERMISSIONS, false);
    }}),
    VIEWER(new HashMap<>(){{
        put(Permission.FEATURE_AUTO_TIMEOUT, true);
        put(Permission.FEATURE_30DAYS_VIP, true);
        put(Permission.GAME_RUSSIAN_ROULETTE, true);
        put(Permission.GAME_DUEL, true);

        put(Permission.COMMAND_DUELPOINTS, true);
        put(Permission.COMMAND_BALANCE, true);
        put(Permission.COMMAND_ROB, false);
        put(Permission.COMMAND_PAY, false);
        put(Permission.COMMAND_MODIFYECONOMY, false);
        put(Permission.COMMAND_MODIFYDUELPOINTS, false);
        put(Permission.COMMAND_MODIFY_PERMISSIONS, false);
    }});

    private final HashMap<Permission, Boolean> permissions;

    Rank(HashMap<Permission, Boolean> permissions) {
        this.permissions = permissions;
    }

    public static Rank get(String username) {
        if(Utils.getChatters("grifermob").getBroadcaster().contains(username)) {
            return BROADCASTER;
        } else if(Utils.getChatters("grifermob").getModerators().contains(username)) {
            return MODERATOR;
        } else if(Utils.getChatters("grifermob").getVips().contains(username)) {
            return VIP;
        } else {
            return VIEWER;
        }
    }

    public HashMap<Permission, Boolean> getPermissions() {
        return permissions;
    }

    public boolean hasPermission(Permission permission) {
        //System.err.println("The Permission " + permission + " is not set for rank " + this + " (Access Denied)");
        return permissions.getOrDefault(permission, false);
    }

    public static Rank get(BotUser user) {
        return get(user.getTwitchUsername());
    }
}
