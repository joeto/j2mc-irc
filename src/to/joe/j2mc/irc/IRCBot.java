package to.joe.j2mc.irc;

import org.bukkit.ChatColor;
import org.jibble.pircbot.PircBot;

public class IRCBot extends PircBot {

	J2MC_IRC plugin;
	public IRCBot(String nick, J2MC_IRC j2mc_irc) {
		this.setName(nick);
		this.setAutoNickChange(true);
		this.setMessageDelay(1100);
		this.plugin = j2mc_irc;
	}

	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		if(channel.equalsIgnoreCase(plugin.NormalChannel)){
			if (message.startsWith("!msg")) {
				String toSend = message.substring(5);
				String toSendIRC = "[IRC] <" + sender + "> " + toSend;
				toSend = "(IRC) <" + ChatColor.AQUA + sender + ChatColor.WHITE + "> " + toSend;
				plugin.getServer().broadcastMessage(toSend);
				this.sendMessage(channel, toSendIRC);
			}
		}
	}
}
