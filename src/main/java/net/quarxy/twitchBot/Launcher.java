package net.quarxy.twitchBot;

public class Launcher {

	private static Bot bot;

	public static void main(String[] args) {
		System.out.println("Starting Bot App ...");
		bot = new Bot();
		bot.registerFeatures();
		bot.start();
		System.out.println("Finished. Bot running:");
	}

	public static Bot getBot() {
		return bot;
	}
}
