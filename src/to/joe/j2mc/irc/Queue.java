package to.joe.j2mc.irc;

import java.util.TimerTask;

public class Queue extends TimerTask {
    
    J2MC_IRC plugin;
    int OutGoingMessages = 0;
    
    public Queue(J2MC_IRC plugin){
        this.plugin = plugin;
    }
    
    public void run(){
        OutGoingMessages = 0;
    }
    
    public void sendMessage(String channel, String message){
        synchronized(this) {
            OutGoingMessages++;
        }
        if(OutGoingMessages >= 10){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            plugin.IRCManager.bot.sendMessage(channel, message);
        }else{
            plugin.IRCManager.bot.sendMessage(channel, message);
        }
    }
    
}
