package net.quarxy.twitchBot.utils;

import net.quarxy.twitchBot.Bot;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static boolean hasModPermission(String userid) {
        return (Bot.getInstance().getTwitchClient().getHelix().getModerators("2hooxe55etc47yjxm1b99ezfkcnmc5",
                        "189073898", List.of(userid), "", 1).execute().getModerators().size() > 0 ||
                        userid.equals("189073898"));
    }

}
