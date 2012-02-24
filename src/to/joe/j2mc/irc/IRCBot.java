package to.joe.j2mc.irc;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jibble.pircbot.PircBot;

import to.joe.j2mc.core.J2MC_Core;
import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.exceptions.BadPlayerMatchException;

public class IRCBot extends PircBot {

	J2MC_IRC plugin;
	IRCcommands commands;
	
	public IRCBot(String nick, J2MC_IRC j2mc_irc) {
		this.setName(nick);
		this.setAutoNickChange(true);
		this.setMessageDelay(1100);
		this.plugin = j2mc_irc;
		this.commands = new IRCcommands(this, plugin);
	}

	//COmmands begin here.
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		//All channel commands.
		
		//!playerlist command
		if(message.equalsIgnoreCase("!playerlist")){
		    commands.PlayerListCommand(channel);
		}
		
		//!players command
		if(message.equalsIgnoreCase("!players")){
		    commands.PlayersCommand(channel);
		}
		
		//Public channel commands.
		if(channel.equalsIgnoreCase(plugin.NormalChannel)){
			//!msg command
			if(message.startsWith("!msg")) {
				String toSend = message.substring(5);
				String toSendIRC = "[IRC] <" + sender + "> " + toSend;
				toSend = "(IRC) <" + ChatColor.AQUA + sender + ChatColor.WHITE + "> " + toSend;
				plugin.getServer().broadcastMessage(toSend);
				this.sendMessage(channel, toSendIRC);
			}
			
			//.kick command
			if(message.startsWith(".kick")){
			    if(commands.hasAdminPrivelages(hostname)){
			        String[] args = message.substring(6).split(" ");
			        String partialplayer = args[0];  
			        String reason = J2MC_Core.combineSplit(1, args, " ");
			        commands.dotKickCommand(partialplayer, reason, sender, hostname);
			    }
			}
			
			//.g command
			if(message.startsWith(".g")){
			    if(commands.hasAdminPrivelages(hostname)){
			        String gmessage = message.substring(3);
			        commands.dotGCommand(hostname, gmessage);
			    }
			}
			
		}
		
		//Admin chanel commands.
		if(channel.equalsIgnoreCase(plugin.AdminChannel)){
		    if(message.startsWith("!has")){
		        String player = message.substring(4);
		        commands.HasCommand(player, channel);
		    }
		}
		
	}
}
