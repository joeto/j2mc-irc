package to.joe.j2mc.irc;

import java.io.IOException;

import org.jibble.pircbot.*;

public class IRCManager {
	J2MC_IRC plugin;
	IRCBot bot;
	public IRCManager(J2MC_IRC IRC){
		this.plugin = IRC;
	}
	
	public void connect(){
		bot = new IRCBot(plugin.nick);
		try {
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
