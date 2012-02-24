package to.joe.j2mc.irc;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.core.command.MasterCommand;

public class SmackIRCCommand extends MasterCommand{
    
    J2MC_IRC plugin;
    
    public SmackIRCCommand(J2MC_IRC IRC){
        super(IRC);
        this.plugin = IRC;
    }
    
    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        if(player.hasPermission("j2mc.senior")){
            plugin.readData();
            plugin.IRCManager.disconnect();
            plugin.IRCManager.connect();
        }
    }

}
