package to.joe.j2mc.irc;

import org.bukkit.ChatColor;
import org.jibble.pircbot.PircBot;

import to.joe.j2mc.core.J2MC_Core;

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
	    String[] MessageArray = message.split(" ");
		
		//!playerlist command
		if(message.toLowerCase().equalsIgnoreCase("!playerlist")){
		    commands.PlayerListCommand(channel);
		}
		
		//!players command
		if(message.toLowerCase().equalsIgnoreCase("!players")){
		    commands.PlayersCommand(channel);
		}
		
		//Public channel commands.
		if(channel.equalsIgnoreCase(plugin.NormalChannel)){
			//!msg command
			if(MessageArray[0].equalsIgnoreCase("!msg")) {
				String toSend = message.substring(5);
				String toSendIRC = "[IRC] <" + sender + "> " + toSend;
				toSend = "(IRC) <" + ChatColor.AQUA + sender + ChatColor.WHITE + "> " + toSend;
				plugin.getServer().broadcastMessage(toSend);
				this.sendMessage(channel, toSendIRC);
			}
			
			//.kick command
			if(MessageArray[0].equalsIgnoreCase(".kick")){
			    if(commands.hasAdminPrivelages(hostname)){
			        String[] args = message.substring(6).split(" ");
			        String partialplayer = args[0];  
			        String reason = J2MC_Core.combineSplit(1, args, " ");
			        commands.dotKickCommand(partialplayer, reason, sender, hostname);
			    }
			}
			
			//.g command
			if(MessageArray[0].equalsIgnoreCase(".g")){
			    if(commands.hasAdminPrivelages(hostname)){
			        String gmessage = message.substring(3);
			        commands.dotGCommand(hostname, gmessage);
			    }
			}
			
			//!admins command
			if(MessageArray[0].equalsIgnoreCase("!admins")){
			    commands.AdminsCommandinPublic(channel);
			}
		}
		
		//Admin chanel commands.
		if(channel.equalsIgnoreCase(plugin.AdminChannel)){
		    //!has command
		    if(MessageArray[0].equalsIgnoreCase("!has")){
		        String player = MessageArray[1];
		        commands.HasCommand(player, channel);
		    }
		    
		    //!admins command
		    if(MessageArray[0].equalsIgnoreCase("!admins")){
		        commands.AdminsCommandinPrivate(channel);
		    }
		}
		
	}
}
