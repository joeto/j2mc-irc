package to.joe.j2mc.irc.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.core.command.MasterCommand;
import to.joe.j2mc.irc.J2MC_IRC;

public class SmackIRCCommand extends MasterCommand<J2MC_IRC> {

    public SmackIRCCommand(J2MC_IRC IRC) {
        super(IRC);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        this.plugin.readData();
        if ((args.length > 0) && args[0].equalsIgnoreCase("settingsonly")) {
            return;
        }
        this.plugin.ircManager.disconnect();
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                SmackIRCCommand.this.plugin.ircManager.connect();
            }
        }, 100L);
    }

}
