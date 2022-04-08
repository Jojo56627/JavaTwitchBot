package net.quarxy.twitchBot;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import jdk.jshell.execution.Util;
import net.quarxy.twitchBot.games.Duel;
import net.quarxy.twitchBot.games.RussianRoulette;
import net.quarxy.twitchBot.utils.BotUser;
import net.quarxy.twitchBot.utils.GameStorage;
import net.quarxy.twitchBot.utils.Utils;

public class Launcher {

	public static void main(String[] args) {
		System.out.println("Starting Bot App ...");
		Bot bot = new Bot();
		bot.registerFeatures();
		//bot.createRewards();
		bot.start();
		System.out.println("Finished. Bot running:");
	}

	public static String[] arguments = new String[9];

	public static void launch(ChannelMessageEvent e) {

	}
}
