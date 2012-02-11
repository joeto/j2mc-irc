package to.joe.j2mc.irc;

import org.jibble.pircbot.*;

public class IRCManager {
	J2MC_IRC plugin;
	IRCBot bot;
	public IRCManager(J2MC_IRC IRC){
		this.plugin = IRC;
	}
	
	public void connect(){
		bot = new IRCBot(plugin.nick);
	}
}
