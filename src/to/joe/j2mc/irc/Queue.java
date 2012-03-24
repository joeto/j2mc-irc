package to.joe.j2mc.irc;

import java.util.TimerTask;

public class Queue extends TimerTask {

    J2MC_IRC plugin;
    int OutGoingMessages = 0;

    public Queue(J2MC_IRC plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.OutGoingMessages = 0;
    }

    public void sendMessage(String message, String channel) {
        synchronized (this) {
            this.OutGoingMessages++;
        }
        if ((this.OutGoingMessages >= 10) && (this.OutGoingMessages < 20)) {
            try {
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
            }
            this.plugin.IRCManager.bot.sendMessage(channel, message);
        } else if (this.OutGoingMessages >= 20) {
            try {
                Thread.sleep(2000);
            } catch (final InterruptedException e) {
            }
            this.plugin.IRCManager.bot.sendMessage(channel, message);

        } else {
            this.plugin.IRCManager.bot.sendMessage(channel, message);
        }
    }

}
