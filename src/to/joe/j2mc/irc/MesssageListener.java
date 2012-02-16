package to.joe.j2mc.irc;

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
       
    }

}
