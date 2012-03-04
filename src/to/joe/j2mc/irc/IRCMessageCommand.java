package to.joe.j2mc.irc;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.core.J2MC_Core;
import to.joe.j2mc.core.command.MasterCommand;

public class IRCMessageCommand extends MasterCommand{
    
    J2MC_IRC plugin;
    
    public IRCMessageCommand(J2MC_IRC IRC){
        super(IRC);
        this.plugin = IRC;
    }
    
    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        if(sender.hasPermission("j2mc.senior")){
            if(args.length < 2){
                sender.sendMessage(ChatColor.RED + "Usage: /ircmessage target message");
                return;
            }
            String target = args[0];
            String message = J2MC_Core.combineSplit(1, args, " ");
            plugin.IRCManager.bot.sendMessage(target, message);
        }
    }

}
