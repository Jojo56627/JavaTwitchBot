package net.quarxy.twitchBot;

public class Launcher {

	public static void main(String[] args) {
		System.out.println("Starting Bot App ...");
		Bot bot = new Bot();
		bot.registerFeatures();
		bot.start();
		System.out.println("Finished. Bot running:");
	}
}
