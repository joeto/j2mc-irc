package to.joe.j2mc.irc.threads;

import java.util.TimerTask;

import to.joe.j2mc.irc.J2MC_IRC;

public class UptimeNagger extends TimerTask {

    J2MC_IRC plugin;
    int nagged = 0;

    public UptimeNagger(J2MC_IRC plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (((System.currentTimeMillis() - this.plugin.lastUp) > 10000) && (this.nagged < 3)) {
            this.plugin.IRCManager.bot.sendMessage(this.plugin.AdminChannel, "Eviltechie: mbaxter: ammar2: Please confirm that I am not down (and do something about it :D)");
        }
        this.nagged++;
        if (!this.plugin.IRCManager.bot.isConnected()) {
            try {
                this.plugin.IRCManager.connect();
            } catch (Exception e) {
                
            }
        }
    }

}
