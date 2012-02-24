package to.joe.j2mc.irc;

import java.io.IOException;

import org.jibble.pircbot.*;

public class IRCManager {
	J2MC_IRC plugin;
	IRCBot bot;
	public IRCManager(J2MC_IRC IRC){
		this.plugin = IRC;
	}
	
	public void disconnect(){
	    bot.disconnect();
	}
	
	public void connect(){
		bot = new IRCBot(plugin.nick, plugin);
		try {
			plugin.getLogger().info("Attempting connection to " + plugin.ServerHost + ":" + plugin.ServerPort);
			if(plugin.bindToIP){
				bot.connect(plugin.ServerHost, plugin.ServerPort, plugin.BindIP);
			}else{
				bot.connectWithNoB(plugin.ServerHost, plugin.ServerPort, null);
			}
		} catch (NickAlreadyInUseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IrcException e) {
			e.printStackTrace();
		}
		plugin.getLogger().info("Connected! Attempting to join channels.");
		bot.joinChannel(plugin.NormalChannel);
		bot.joinChannel(plugin.AdminChannel);
	}
	
	public void sendMessage(String message, boolean adminChannel){
		if(adminChannel){
			bot.sendMessage(plugin.AdminChannel, message);
		}else{
			bot.sendMessage(plugin.NormalChannel, message);
		}
	}
	
}
