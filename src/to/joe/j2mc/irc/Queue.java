package to.joe.j2mc.irc;

import java.util.ArrayList;
import java.util.TimerTask;

public class Queue extends TimerTask {

    J2MC_IRC plugin;
    int OutGoingMessages = 0;
    ArrayList<String> messages = new ArrayList<String>();
    int SimilarMessagesOut = 0;

    public Queue(J2MC_IRC plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.OutGoingMessages = 0;
        this.SimilarMessagesOut = 0;
        this.messages.clear();
    }

    public void sendMessage(String message, String channel) {
        synchronized (this) {
            this.OutGoingMessages++;
        }
        if(this.messages.get(this.messages.size() - 1).equals(message)){
            synchronized (this) {
                this.SimilarMessagesOut++;
            }
        }
        this.messages.add(message);
        if ((this.OutGoingMessages >= 10) && (this.OutGoingMessages < 20) && (this.SimilarMessagesOut <= 5)) {
            try {
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
            }
            this.plugin.IRCManager.bot.sendMessage(channel, message);
        } else if ((this.OutGoingMessages >= 20) && (this.SimilarMessagesOut <= 5)) {
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
