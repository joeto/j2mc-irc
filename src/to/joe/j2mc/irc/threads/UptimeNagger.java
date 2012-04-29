package to.joe.j2mc.irc.threads;

import java.util.TimerTask;

import to.joe.j2mc.irc.J2MC_IRC;

public class UptimeNagger extends TimerTask{

    J2MC_IRC plugin;
    int nagged = 0;
    
    public UptimeNagger(J2MC_IRC plugin){
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        if ((plugin.mainThread.isAlive() || plugin.mainThread.isInterrupted()) && nagged < 3) {
            plugin.IRCManager.bot.sendMessage(plugin.AdminChannel, "Eviltechie: mbaxter: ammar2: I am down, my main thread isn't alive or is intterupted");
            this.nagged++;
        }
    }

}
