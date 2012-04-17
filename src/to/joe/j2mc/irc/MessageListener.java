package to.joe.j2mc.irc;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import to.joe.j2mc.core.event.MessageEvent;

;

public class MessageListener implements Listener {

    J2MC_IRC plugin;

    public MessageListener(J2MC_IRC IRC) {
        this.plugin = IRC;
    }

    @EventHandler
    public void onIRCMessageEvent(MessageEvent event) {
        final HashSet<String> targets = event.alltargets();
        for (final String target : targets) {
            if (target.equals("ADMININFO")) {
                this.plugin.IRCManager.sendMessage(ChatColor.stripColor(event.getMessage()), true);
            } else if (target.equals("GAMEMSG")) {
                this.plugin.queue.sendMessage(ChatColor.stripColor(event.getMessage()), this.plugin.NormalChannel);
            } else if (target.startsWith("SendNotice")) {
                final String WhoToSend = target.split(" ")[1];
                this.plugin.IRCManager.bot.sendNotice(WhoToSend, event.getMessage());
            }
        }
    }

}
