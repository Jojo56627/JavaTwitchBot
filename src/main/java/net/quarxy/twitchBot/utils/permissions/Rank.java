package net.quarxy.twitchBot.utils.permissions;

import net.quarxy.twitchBot.utils.Utils;

import java.util.HashMap;

public enum Rank {
    BROADCASTER(new HashMap<>(){{
        put(Permission.FEATURE_AUTO_TIMEOUT, false);
        put(Permission.FEATURE_30DAYS_VIP, false);
        put(Permission.FEATURE_RUSSIAN_ROULETTE, true);
    }}),
    MODERATOR(new HashMap<>(){{
        put(Permission.FEATURE_AUTO_TIMEOUT, false);
        put(Permission.FEATURE_30DAYS_VIP, false);
        put(Permission.FEATURE_RUSSIAN_ROULETTE, true);
    }}),
    VIP(new HashMap<>(){{
        put(Permission.FEATURE_AUTO_TIMEOUT, true);
        put(Permission.FEATURE_30DAYS_VIP, true);
        put(Permission.FEATURE_RUSSIAN_ROULETTE, true);
    }}),
    VIEWER(new HashMap<>(){{
        put(Permission.FEATURE_AUTO_TIMEOUT, true);
        put(Permission.FEATURE_30DAYS_VIP, true);
        put(Permission.FEATURE_RUSSIAN_ROULETTE, true);
    }});

    private final HashMap<Permission, Boolean> permissions;

    Rank(HashMap<Permission, Boolean> permissions) {
        this.permissions = permissions;
    }

    public boolean hasPermission(Permission permission) {
        if(!this.permissions.containsKey(permission)) {
            System.err.println("The Permission " + permission + " is not set for rank " + this + " (Access Denied)");
            return false;
        } else {
            return this.permissions.get(permission);
        }
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
}
