package to.joe.j2mc.irc.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.core.command.MasterCommand;
import to.joe.j2mc.irc.J2MC_IRC;

public class SmackIRCCommand extends MasterCommand {

    J2MC_IRC plugin;

    public SmackIRCCommand(J2MC_IRC IRC) {
        super(IRC);
        this.plugin = IRC;
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        if (sender.hasPermission("j2mc.irc.management")) {
            this.plugin.readData();
            this.plugin.IRCManager.disconnect();
            this.plugin.IRCManager.connect();
        }
    }

}
