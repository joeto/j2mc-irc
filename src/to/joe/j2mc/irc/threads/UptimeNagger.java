package to.joe.j2mc.irc.threads;

import java.util.TimerTask;

import to.joe.j2mc.irc.J2MC_IRC;

public class UptimeNagger extends TimerTask {

    private J2MC_IRC plugin;
    private int nagged = 0;

    public UptimeNagger(J2MC_IRC plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (((System.currentTimeMillis() - this.plugin.lastUp) > 10000) && (this.nagged < 3)) {
            this.plugin.ircManager.bot.sendMessage(this.plugin.adminChannel, "Eviltechie: mbaxter: ammar2: Please confirm that I am not down (and do something about it :D)");
        }
        this.nagged++;
        if (!this.plugin.ircManager.bot.isConnected()) {
            try {
                this.plugin.ircManager.connect();
            } catch (final Exception e) {
                
            }
        }
    }

}
