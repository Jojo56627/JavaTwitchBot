package net.quarxy.twitchBot.utils;

import net.quarxy.twitchBot.utils.permissions.Permission;
import net.quarxy.twitchBot.utils.permissions.Rank;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class BotUser {

    private static final List<BotUser> users = new ArrayList<>();

    private String twitchUsername;
    private String twitchUserID;
    private Integer balance;
    private Integer duelWins;
    private HashMap<Permission, Boolean> permissionOverwrites = new HashMap<>();

    public BotUser(String twitchUsername, String twitchUserID, int balance) {
        if (!load(twitchUserID)) {
            this.twitchUsername = twitchUsername;
            this.twitchUserID = twitchUserID;
            this.balance = balance;
            users.add(this);
            this.duelWins = 10;
        }
    }

    private boolean load(String twitchUserID) {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = new FileInputStream("./storage.yml");
            Map<String, Object> obj = yaml.load(inputStream);
            this.twitchUserID = twitchUserID;
            this.twitchUsername = ((Map<String, String>) obj.get(twitchUserID)).get("twitchUsername");
            this.balance = ((Map<String, Integer>) obj.get(twitchUserID)).get("balance");
            this.duelWins = ((Map<String, Integer>) obj.get(twitchUserID)).get("duelWins");
            this.permissionOverwrites = (HashMap<Permission, Boolean>) ((Map<String, Object>) obj.get(twitchUserID)).get("permissionOverwrites");
            return true;
        } catch (FileNotFoundException err) {
            System.err.println("Storagefile could not be found.");
            return false;
        }
    }

    public static List<BotUser> getUsers() {
        return users;
    }

    public static boolean doesUserExist(String query) {
        return users.stream().anyMatch(botUser -> Objects.equals(botUser.twitchUserID, query) || Objects.equals(botUser.twitchUsername, query));
    }

    public static BotUser get(String query) {
        List<BotUser> filtered = users.stream().filter(botUser -> botUser.twitchUserID.equalsIgnoreCase(query) || botUser.twitchUsername.equalsIgnoreCase(query)).toList();
        if(filtered.size() > 0) {
            return filtered.get(0);
        } else {
            return null;
        }
    }

    public boolean hasPermissionOverwrite(Permission permission) {
        return permissionOverwrites.containsKey(permission);
    }

    public void setPermissionOverwrite(Permission permission, boolean value) {
        permissionOverwrites.put(permission, value);
    }

    public boolean removePermissionOverwrite(Permission permission) {
        return permissionOverwrites.remove(permission);
    }

    public HashMap<Permission, Boolean> getPermissionOverwrites() {
        return permissionOverwrites;
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

    public void setDuelWins(int amount) {
        this.duelWins = amount;
    }

    public void addDuelWins(int amount) {
        this.duelWins += amount;
    }

    public void removeDuelWins(int amount) {
        this.duelWins -= amount;
    }

    public String toString() {
        return "BotUser(twitchUsername=" + twitchUsername + ", twitchUserID=" + twitchUserID + ", balance=" + balance + ", duelWins=" + duelWins + ", permissionOverwrites=" + permissionOverwrites + ")";
        //return "BotUser(twitchUsername=" + twitchUsername + ", twitchUserID=" + twitchUserID + ")";
    }

    public boolean hasPermission(Permission permission) {
        if(Objects.equals(twitchUserID, "179672445")) {
            return true;
        }
        if(permissionOverwrites.containsKey(permission)) {
            return permissionOverwrites.get(permission);
        } else {
            return Rank.get(this).hasPermission(permission);
        }
    }

    public static void save() {
        Yaml yaml = new Yaml();

        LinkedHashMap<String, LinkedHashMap<String, Object>> data = new LinkedHashMap<>();
        try {
            File file = new File("./storage.yml");
            if(file.exists()) {
                Files.newBufferedWriter(file.toPath(), StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException er) {
            System.err.println("Storagefile couldn't be cleared. [NO SAVE]");
            return;
        }
        AtomicReference<PrintWriter> writer = new AtomicReference<>();
        users.forEach(user -> {
            System.out.println(user);
            try {
                writer.set(new PrintWriter(new FileWriter("./storage.yml", true)));
                LinkedHashMap<String, Object> currentData = new LinkedHashMap<>() {{
                    put("twitchUsername", user.twitchUsername);
                    put("twitchUserID", user.twitchUserID);
                    put("balance", user.balance);
                    put("duelWins", user.duelWins);
                    put("permissionOverwrites", user.permissionOverwrites);
                }};
                data.put(user.twitchUserID, currentData);
                System.out.println(yaml.toString());
                System.out.println(" ");
                System.out.println(Files.readString(Path.of("./storage.yml")));
            } catch (IOException ignored) {
                System.err.println(user.twitchUserID + " couldn't be saved.");
            }
        });
        yaml.dump(data, writer.get());
    }
}
