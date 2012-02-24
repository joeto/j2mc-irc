package to.joe.j2mc.irc;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jibble.pircbot.PircBot;

import to.joe.j2mc.core.J2MC_Core;
import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.exceptions.BadPlayerMatchException;

public class IRCBot extends PircBot {

	J2MC_IRC plugin;
	public IRCBot(String nick, J2MC_IRC j2mc_irc) {
		this.setName(nick);
		this.setAutoNickChange(true);
		this.setMessageDelay(1100);
		this.plugin = j2mc_irc;
	}

	//COmmands begin here.
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		//All channel commands.
		
		//!playerlist command
		if(message.equalsIgnoreCase("!playerlist")){
			int players = 0;
			for(Player plr : plugin.getServer().getOnlinePlayers()){
				if(!J2MC_Manager.getVisibility().isVanished(plr)){
					players++;
				}
			}
			String toSend = "Players (" + players + " of " + plugin.getServer().getMaxPlayers() + "): ";
			if(players != 0){
				for(Player plr : plugin.getServer().getOnlinePlayers()){
					if(!J2MC_Manager.getVisibility().isVanished(plr)){
						toSend = toSend + plr.getName() + ", ";
					}
				}
				toSend = toSend.substring(0, toSend.length() - 2);
			}else{
				toSend = toSend + "No one is online :(";
			}
			this.sendMessage(channel, toSend);
		}
		
		//!players command
		if(message.equalsIgnoreCase("!players")){
			int players = 0;
			for(Player plr : plugin.getServer().getOnlinePlayers()){
				if(!J2MC_Manager.getVisibility().isVanished(plr)){
					players++;
				}
			}
			String toSend = "Currently " + players + " out of " + plugin.getServer().getMaxPlayers() + " on the server";
			this.sendMessage(channel, toSend);
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
			    if(plugin.hosts.containsKey(hostname)){
			        String[] args = message.substring(6).split(" ");
			        String partialplayer = args[0];   
			        Player target = null;
			        try{
			            target = J2MC_Manager.getVisibility().getPlayer(partialplayer, null);
			        }catch(BadPlayerMatchException e){
			            this.sendMessage(sender, e.getMessage());
			            return;
			        }
			        String reason = J2MC_Core.combineSplit(1, args, " ");
			        if(reason == null){
			            reason = "Kicked.";
			        }
			        target.kickPlayer("Kicked: " + reason);
			        J2MC_Manager.getCore().adminAndLog(plugin.hosts.get(hostname) + "-irc kicked " + target.getName() + "(" + reason + ")");
			        J2MC_Manager.getCore().messageNonAdmin(ChatColor.RED + target.getName() + " kicked (" + reason + ")");
			    }
			}
		}
		
		//Admin chanel commands.
		if(channel.equalsIgnoreCase(plugin.AdminChannel)){
			
		}
		
	}
}
