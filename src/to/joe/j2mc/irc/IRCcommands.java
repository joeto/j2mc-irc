package to.joe.j2mc.irc;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.exceptions.BadPlayerMatchException;

public class IRCcommands {
    
    J2MC_IRC plugin;
    IRCBot bot;
    
    public IRCcommands(IRCBot bot, J2MC_IRC IRC){
        this.plugin = IRC;
        this.bot = bot;
    }
    
    public boolean hasAdminPrivelages(String hostname){
        return plugin.hosts.containsKey(hostname);
    }
    
    public void dotKickCommand(String partialname, String reason, String sender, String hostname){
 
        Player target = null;
        try{
            target = J2MC_Manager.getVisibility().getPlayer(partialname, null);
        }catch(BadPlayerMatchException e){
            bot.sendMessage(sender, e.getMessage());
            return;
        }
        if(reason == null){
            reason = "Kicked.";
        }
        target.kickPlayer("Kicked: " + reason);
        bot.sendMessage(sender, target.getName() + " kicked.");
        bot.sendMessage(plugin.NormalChannel, target.getName() + " kicked (" + reason + ")");
        J2MC_Manager.getCore().adminAndLog(plugin.hosts.get(hostname) + "-irc kicked " + target.getName() + "(" + reason + ")");
        J2MC_Manager.getCore().messageNonAdmin(ChatColor.RED + target.getName() + " kicked (" + reason + ")");
    }
    
    public void dotGCommand(String hostname, String message){
        for (final Player plr : J2MC_Manager.getVisibility().getOnlinePlayers(null)) {
            if (plr.hasPermission("j2mc.adminToolKit.admin")) {
                plr.sendMessage("<" + plugin.hosts.get(hostname) + "-irc> " + ChatColor.LIGHT_PURPLE + message);
            } else {
                plr.sendMessage("<ADMIN> " + ChatColor.LIGHT_PURPLE + message);
            }
        }
        bot.sendMessage(plugin.NormalChannel, "<ADMIN> " + message);
    }
    
    public void PlayerListCommand(String channel){
        int players = 0;
        for(Player plr : plugin.getServer().getOnlinePlayers()){
            if(!J2MC_Manager.getVisibility().isVanished(plr)){
                players++;
            }
        }
        String toSend = "Players (" + players + " of " + plugin.getServer().getMaxPlayers() + "): ";
        if(players != 0){
            for(Player plr : plugin.getServer().getOnlinePlayers()){
                if(!J2MC_Manager.getVisibility().isVanished(plr)){
                    toSend = toSend + plr.getName() + ", ";
                }
            }
            toSend = toSend.substring(0, toSend.length() - 2);
        }else{
            toSend = toSend + "No one is online :(";
        }
        bot.sendMessage(channel, toSend);
    }
    
    public void HasCommand(String player, String Channel){
        for(Player plr : J2MC_Manager.getVisibility().getOnlinePlayers(null)){
            if(player.equalsIgnoreCase(plr.getName())){
                bot.sendMessage(Channel, "I has " + player);
            }
        }
    }
    
    public void PlayersCommand(String channel){
        int players = 0;
        for(Player plr : plugin.getServer().getOnlinePlayers()){
            if(!J2MC_Manager.getVisibility().isVanished(plr)){
                players++;
            }
        }
        String toSend = "Currently " + players + " out of " + plugin.getServer().getMaxPlayers() + " on the server";
        bot.sendMessage(channel, toSend);
    }
    
    public void AdminsCommandinPrivate(String channel){
        int admins = 0;
        for(Player plr : plugin.getServer().getOnlinePlayers()){
            if(J2MC_Manager.getPermissions().isAdmin(plr.getName())){
                admins++;
            }
        }
        String toSend = " ";
        if(admins == 0){
            toSend = "No admins online";
        }else{
            toSend = "There are " + admins + " admins online : ";
            for(Player plr : plugin.getServer().getOnlinePlayers()){
                if(J2MC_Manager.getPermissions().isAdmin(plr.getName())){
                    toSend = toSend + plr.getName() + ", ";
                }
            }
            toSend = toSend.substring(0, toSend.length() - 2);
        }
        bot.sendMessage(channel, toSend);
    }
    
    public void AdminsCommandinPublic(String channel){
        boolean haveAdmins = false;
        for(Player plr : plugin.getServer().getOnlinePlayers()){
            if(J2MC_Manager.getPermissions().isAdmin(plr.getName())){
                haveAdmins = true;
            }
        }
        if(haveAdmins){
            bot.sendMessage(channel, "There are admins online");
        }else{
            bot.sendMessage(channel, "No admins online see #joe.to or #minecraft");
        }
    }

}
