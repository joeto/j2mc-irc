package to.joe.j2mc.irc;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import to.joe.j2mc.core.event.MessageEvent;;

public class MesssageListener implements Listener{
	
	J2MC_IRC plugin;
	public MesssageListener(J2MC_IRC IRC){
		this.plugin = IRC;
	}
	
    @EventHandler
    public void onIRCMessageEvent(MessageEvent event) {
    	HashSet<String> targets = event.alltargets();
    	for(String target : targets){
    		if(target.equals("ADMININFO")){
    			plugin.IRCManager.sendMessage(ChatColor.stripColor(event.getMessage()), true);
    		}else if(target.equals("GAMEMSG")){
    			plugin.queue.sendMessage(ChatColor.stripColor(event.getMessage()), plugin.NormalChannel);
    		}
    	}
    }

}
