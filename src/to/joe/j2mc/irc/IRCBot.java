package to.joe.j2mc.irc;

import org.jibble.pircbot.PircBot;

public class IRCBot extends PircBot{

	public IRCBot(String nick){
		this.setName(nick);
		this.setAutoNickChange(true);
		this.setMessageDelay(1100);
	}
}
