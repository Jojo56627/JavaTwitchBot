package net.quarxy.twitchBot.utils;

import net.quarxy.twitchBot.games.Duel;
import net.quarxy.twitchBot.games.RussianRoulette;

import java.util.ArrayList;
import java.util.List;

public class GameStorage {

    public static RussianRoulette russianRoulette = null;
    private static final List<Duel> duels = new ArrayList<>();

    public static Duel getDuel(BotUser initiator) {
        return duels.stream().filter(duel -> duel.getInitiator() == initiator).toList().get(0);
    }

    public static void createDuel(BotUser initiator, BotUser defender) {
        duels.add(new Duel(initiator, defender));
    }

    public static void terminateDuel(BotUser initiator) {
        duels.removeIf(duel -> duel.getInitiator() == initiator);
    }

    public static boolean doesDuelWithInitiatorExist(BotUser initiator) {
        return duels.stream().anyMatch(duel -> duel.getInitiator() == initiator);
    }

    public static boolean doesDuelWithInitiatorAndDefenderExist(BotUser initiator, BotUser defender) {
        return duels.stream().anyMatch(duel -> duel.getInitiator() == initiator && duel.getDefender() == defender);
    }

}
