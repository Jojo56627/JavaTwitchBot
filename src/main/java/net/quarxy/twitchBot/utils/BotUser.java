package net.quarxy.twitchBot.utils;

import net.quarxy.twitchBot.utils.permissions.Rank;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BotUser {

    private static final List<BotUser> users = new ArrayList<>();

    private final String twitchUsername;
    private final String twitchUserID;
    private int balance;
    private final int duelWins;

    public BotUser(String twitchUsername, String twitchUserID, int balance) {
        this.twitchUsername = twitchUsername;
        this.twitchUserID = twitchUserID;
        this.balance = balance;
        users.add(this);
        this.duelWins = 10;
    }

    public static List<BotUser> getUsers() {
        return users;
    }

    public static boolean doesUserExist(String query) {
        return users.stream().anyMatch(botUser -> Objects.equals(botUser.twitchUserID, query) || Objects.equals(botUser.twitchUsername, query));
    }

    public static BotUser get(String query) {
        return users.stream().filter(botUser -> Objects.equals(botUser.twitchUserID, query) || Objects.equals(botUser.twitchUsername, query)).toList().get(0);
    }

    public int getBalance() {
        return balance;
    }

    public int getDuelWins() {
        return duelWins;
    }

    public String getTwitchUsername() {
        return twitchUsername;
    }

    public String getTwitchUserID() {
        return twitchUserID;
    }

    public BotUser(String twitchUsername, String twitchUserID) {
        this(twitchUsername, twitchUserID, 99999);
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void addToBalance(int amount) {
        this.balance += amount;
    }

    public void removeFromBalance(int amount) {
        this.balance -= amount;
    }
}
